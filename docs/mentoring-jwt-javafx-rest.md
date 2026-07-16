# Mentoring - JWT Security And JavaFX REST Integration

This note explains the backend JWT security model and the JavaFX REST integration model for Smart Clinic.

## 1. JWT Security Mental Model

JWT is a stateless proof that the user already logged in.

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

JWT does not replace Spring Security. JWT only gives Spring Security enough information to rebuild the authenticated user for each stateless API request.

## 2. API Stateless, Web Session

The project has two presentation layers:

- Thymeleaf Web uses form login, CSRF, and server session.
- JavaFX Desktop calls `/api/v1/**` with token-based authentication.

For API, `SessionCreationPolicy.STATELESS` is correct because JavaFX should not depend on `JSESSIONID`. Every protected API request must carry its own Bearer token.

## 3. Backend JWT Classes

Minimum backend design:

| Class | Responsibility |
| --- | --- |
| `AuthRestController` | Expose `/api/v1/auth/login`, `/logout`, `/me` |
| `AuthService` | Authenticate login request and build login response |
| `JwtService` | Generate token, parse token, validate token, read roles/subject |
| `JwtAuthenticationFilter` | Read Bearer token and set `SecurityContext` |
| `JwtAuthenticationEntryPoint` | Return `401` for missing/invalid token |
| `JwtAccessDeniedHandler` | Return `403` for valid token without permission |
| `LoginRequest` | Request DTO with `userName`, `password` |
| `LoginResponse` | Response DTO with token, userName, full name, roles |
| `CurrentUserResponse` | DTO for `/auth/me` |

## 4. Common JWT Mistakes

- Forgetting role prefix: Spring checks `ROLE_ADMIN`, not plain `ADMIN`, when using role-based rules.
- Adding JWT filter after the wrong filter. It should run before `UsernamePasswordAuthenticationFilter`.
- Blocking public endpoints such as `/api/v1/auth/login` or `/api/v1/health`.
- Returning `403` when the token is missing. Missing or invalid token should be `401`.
- Using a weak production secret. MVP can use a dev default, production must use environment secret.
- Putting clinic business logic inside the JWT filter. Filters authenticate only.

## 5. JavaFX REST Integration Mental Model

JavaFX must follow this flow:

```text
FXML Controller
-> Desktop Service
-> shared ApiClient
-> shared HttpClient
-> async REST call on background thread
-> CompletableFuture/Task callback
-> Platform.runLater(update UI)
-> SessionManager stores JWT token
```

The controller handles UI events and UI updates. It should not know HTTP headers, JSON parsing, token refresh, or raw network details.

## 6. Threading Rule

Never call backend APIs synchronously on the JavaFX Application Thread.

Bad flow:

```text
Button click -> Controller -> ApiClient.send() blocking -> UI freezes
```

Good flow:

```text
Button click
-> Controller disables button / shows loading
-> Desktop Service returns CompletableFuture<T>
-> ApiClient sends request asynchronously
-> Callback receives ApiResponse
-> Platform.runLater updates controls / alerts / navigation
```

Recommended boundary:

- `ApiClient` returns `CompletableFuture<T>` and does not touch JavaFX UI classes.
- `Desktop Service` coordinates use cases and still avoids direct UI mutation.
- `Controller` updates UI on the JavaFX thread, using `Platform.runLater()` when callback is not already on that thread.

Do not scatter `Platform.runLater()` everywhere. Keep UI-thread switching near the controller/UI boundary.

## 7. Shared HttpClient Rule

Create one `java.net.http.HttpClient` instance and reuse it for the whole JavaFX app.

Reason:

- `HttpClient` is thread-safe.
- It can reuse connections internally.
- Creating a new client for every request wastes resources and slows the app.

Recommended shape:

```java
public class ApiClient {
    private final HttpClient httpClient;
    private final SessionManager sessionManager;
}
```

`ApiClient` should add the Bearer token from `SessionManager` for protected requests.

## 8. Manual DI For This MVP

For this one-week JavaFX MVP, use Manual DI instead of Guice or a mini Spring Context.

Recommended shared object graph:

```text
AppContext
- SessionManager
- HttpClient
- ApiClient
- AuthApiClient
- AppointmentApiClient
- QueueApiClient
- AuthService
- AppointmentService
- QueueService
```

Why Manual DI:

- Less configuration.
- Easier for the team to debug.
- Enough for the current app size.
- Avoids bringing backend-style complexity into JavaFX too early.

Use an `FXMLLoader` controller factory or a small controller injection helper so each controller receives the service it needs.

## 9. JavaFX Classes Needed

| Class | Responsibility |
| --- | --- |
| `AppContext` | Owns shared app-wide dependencies |
| `SessionManager` | Stores current access token, userName, full name, roles in memory |
| `ApiClient` | Shared HTTP client wrapper, base URL, JSON, common error handling |
| `AuthApiClient` | Calls `/api/v1/auth/login` and `/auth/me` |
| `AuthService` | Login/logout use cases for controllers |
| `ApiResponse<T>` | Generic wrapper matching backend response format |
| `LoginRequest` | JavaFX DTO with `userName`, `password` |
| `LoginResponse` | JavaFX DTO with token and user info |
| `AlertUtil` | Maps API errors to user-friendly JavaFX alerts |

MVP token storage can be in memory only. Users login again when the JavaFX app restarts, which is acceptable for this project.

## 10. Error Mapping For JavaFX

| HTTP status | JavaFX behavior |
| --- | --- |
| `400` | Show validation message and keep form open |
| `401` | Clear session and navigate to login |
| `403` | Show access denied alert |
| `404` | Show not found message |
| `409` | Show duplicate/conflict message |
| `422` | Show business rule message |
| `500` | Show generic system error |

## 11. Senior Implementation Order

Build integration in this order:

1. Backend `/api/v1/health` for connectivity.
2. Backend `/api/v1/auth/login` with stable `LoginResponse`.
3. Backend JWT filter and `/api/v1/auth/me`.
4. JavaFX `AppContext`, `SessionManager`, and shared `ApiClient`.
5. JavaFX login screen integration.
6. One small protected API call end-to-end before building many screens.
7. Appointment and queue screens after the foundation is proven.