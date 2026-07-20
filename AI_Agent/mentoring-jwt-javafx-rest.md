# Mentoring - JWT Security And JavaFX REST Integration

This note explains the next architecture step for Smart Clinic after the current security skeleton.

## 1. Current State

The backend already has:

- `SecurityConfig` with a separated API filter chain for `/api/**`.
- `CustomUserDetailsService` loading users by `userName`.
- BCrypt password encoding.
- Standard `ApiResponse` and global REST exception handling.

The backend does not yet have real JWT login or token verification. JavaFX is still a skeleton and does not yet call REST APIs.

## 2. JWT Security Mental Model

JWT is a stateless proof that the user already logged in.

Flow:

```text
JavaFX login screen
-> POST /api/v1/auth/login with userName/password
-> Backend authenticates with AuthenticationManager
-> Backend creates JWT containing subject + roles + expiration
-> JavaFX stores accessToken in SessionManager
-> Later JavaFX API calls send Authorization: Bearer <token>
-> JwtAuthenticationFilter validates token
-> Filter creates Authentication and stores it in SecurityContext
-> Spring Security checks roles before controller runs
```

Key point: JWT does not replace Spring Security. JWT only gives Spring Security enough information to rebuild the authenticated user for each stateless API request.

## 3. Why API Is Stateless But Web Uses Session

The project has two presentation layers:

- Thymeleaf Web runs in browser and can use form login, CSRF, and server session.
- JavaFX Desktop is a separate client and should call `/api/v1/**` with token-based authentication.

For API, `SessionCreationPolicy.STATELESS` is correct because the backend should not rely on `JSESSIONID` for JavaFX. Every protected API request must carry its own Bearer token.

## 4. Backend Classes Needed For JWT

Minimum backend design:

| Class | Responsibility |
| --- | --- |
| `AuthRestController` | Expose `/api/v1/auth/login`, `/logout`, `/me` |
| `AuthService` | Authenticate login request and build login response |
| `JwtService` | Generate token, parse token, validate token, read roles/subject |
| `JwtAuthenticationFilter` | Read Bearer token and set `SecurityContext` |
| `LoginRequest` | Request DTO with `userName`, `password` |
| `LoginResponse` | Response DTO with token, token type, userName, full name, roles |
| `CurrentUserResponse` | DTO for `/auth/me` |

Suggested login response shape:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "userName": "admin",
  "fullName": "System Admin",
  "roles": ["ROLE_ADMIN"]
}
```

## 5. Common JWT Mistakes

- Forgetting role prefix: Spring checks `ROLE_ADMIN`, not `ADMIN`, when using `hasRole("ADMIN")`.
- Adding JWT filter after the wrong filter or not adding it before `UsernamePasswordAuthenticationFilter`.
- Permitting `/api/v1/auth/login` but accidentally blocking `/api/v1/health`.
- Returning `403` when token is missing; missing/invalid token should be `401`.
- Using a weak or hard-coded production secret. MVP can use config property, production must use environment secret.
- Putting business logic inside the JWT filter. Filters should authenticate, not decide clinic workflows.

## 6. JavaFX REST Integration Mental Model

JavaFX must follow this flow:

```text
FXML Controller -> Desktop Service -> API Client -> Backend REST API
```

The controller should only handle UI events and update controls. It should not know how HTTP headers, JSON, or tokens work.

## 7. JavaFX Classes Needed

Minimum desktop design:

| Class | Responsibility |
| --- | --- |
| `ApiClient` | Shared Java `HttpClient`, base URL, JSON serialization, common error handling |
| `AuthApiClient` | Calls `/api/v1/auth/login` and `/auth/me` |
| `SessionManager` | Stores current access token, userName, full name, roles in memory |
| `AuthService` | Login/logout use cases for controllers |
| `ApiResponse<T>` | Generic wrapper matching backend response format |
| `LoginRequest` | JavaFX DTO with `userName`, `password` |
| `LoginResponse` | JavaFX DTO with token and user info |
| `AlertUtil` | Maps API errors to user-friendly JavaFX alerts |

MVP token storage can be in memory only. That means users login again when the JavaFX app restarts, which is acceptable for the course project.

## 8. JavaFX Request Flow After Login

```text
LoginController receives button click
-> AuthService.login(userName, password)
-> AuthApiClient sends POST /api/v1/auth/login
-> ApiClient parses ApiResponse<LoginResponse>
-> SessionManager stores accessToken and roles
-> Navigation moves to main screen
```

For later requests:

```text
AppointmentController asks AppointmentService for today's appointments
-> AppointmentService calls AppointmentApiClient
-> ApiClient adds Authorization: Bearer <token>
-> Backend validates JWT
-> Controller receives DTO list and updates TableView
```

## 9. Error Mapping For JavaFX

| HTTP status | JavaFX behavior |
| --- | --- |
| `400` | Show validation message and keep form open |
| `401` | Clear session and navigate to login |
| `403` | Show access denied alert |
| `404` | Show not found message |
| `409` | Show duplicate/conflict message |
| `422` | Show business rule message |
| `500` | Show generic system error |

## 10. Senior Mentor Advice

Build the integration in this order:

1. Implement backend `/api/v1/health` first so JavaFX can test connectivity.
2. Implement `/api/v1/auth/login` and return a stable `LoginResponse`.
3. Add JWT generation and validation filter.
4. Build JavaFX `ApiClient` and `SessionManager` before building screens.
5. Connect one tiny screen end-to-end before creating many screens.
6. Keep business rules in backend services; JavaFX only displays results and errors.
