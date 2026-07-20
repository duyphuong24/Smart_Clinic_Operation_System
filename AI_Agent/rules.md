# AI Agent Rules - Smart Clinic Operations System

Bạn là senior Java fullstack agent hỗ trợ dự án Smart Clinic Operations System.

## Repo & Branch Information

- **Repo local:**
  `D:\download chrome\Semester_5\HSF302\Assignments\CourseProject\course-project-hsf302_se20a11_pbt`
- **Git remote:**
  `https://github.com/fudn-traltb-su26/course-project-hsf302_se20a11_pbt.git`
- **Branch chính để tích hợp team hiện tại là `dev`, không phải `main`.**

---

## Quy trình Git/GitHub của team

1. **Luôn kiểm tra trạng thái trước khi làm:**
   ```bash
   git status --short --branch
   git fetch origin --prune
   ```
2. **Mọi task mới phải bắt đầu từ latest `dev`:**
   ```bash
   git switch dev
   git pull origin dev
   ```
3. **Nếu có bug/blocker/missing requirement mới, trước khi fix phải thêm issue mới vào `docs/issues.md` với số tiếp theo trong danh sách issue.**
4. **Tạo branch theo team rule:**
   `<type>/<issue-id>-<short-description>`

   *Ví dụ:*
   - `feature/27-refresh-token-security`
   - `bugfix/28-fix-api-role-authorization`
   - `docs/29-update-demo-script`

5. **Commit theo Conventional Commits:**
   `<type>(<scope>): <short summary>`

   *Ví dụ:*
   - `feat(security): add refresh token rotation`
   - `fix(auth): reject revoked refresh token`
   - `docs(tasks): update leader task status`
   - `test(workflow): cover payment business rules`

6. **Một branch chỉ nên tập trung một feature/fix/docs task.**
7. **Một commit chỉ nên mô tả một thay đổi rõ ràng.**
8. **Trước khi push branch phải chạy test phù hợp:**
   - **Backend:**
     ```bash
     cd clinic-backend
     .\mvnw.cmd test
     ```
   - **JavaFX:**
     ```bash
     cd clinic-desktop
     .\mvnw.cmd test
     ```
9. **Push branch:**
   ```bash
   git push -u origin <branch-name>
   ```
10. **Tạo Pull Request vào `dev`, không tạo vào `main` trừ khi leader yêu cầu.**
11. **Nếu không tạo PR tự động được, hãy cung cấp PR title/body theo template:**
    ```markdown
    ## Summary
    ## Changes
    ## Linked Issues
    ## Test
    ## Review Notes
    ## Author Checklist
    ```
12. **Sau khi PR merge, quay lại:**
    ```bash
    git switch dev
    git pull origin dev
    ```
    chạy lại test baseline nếu cần.

---

## Lịch sử & Bối cảnh dự án

- Team từng merge nhầm một số PR vào `main`, sau đó đã xử lý bằng revert trên `main`.
- Từ đó thống nhất source of truth cho tích hợp là `dev`.
- Các PR backend core đã lần lượt được merge vào `dev`:
  - #15 Encounter workflow
  - #17 Service Catalog
  - #16 Encounter Service Orders
  - #18 Invoice Generation
  - #19 Payment Recording
  - #23-24 Reporting/Audit foundation
- Khi PR bị conflict do code member khác merge vào, hướng xử lý đã chọn là:
  - Không force merge branch cũ nếu conflict dây chuyền.
  - Tạo branch sạch từ latest `dev`.
  - Cherry-pick đúng commit riêng của feature.
  - Test lại.
  - Push branch mới và tạo PR mới vào `dev`.

---

## Quy tắc kiến trúc & xử lý conflict

- **Không overwrite code Web UI/member khác nếu phần đó đúng scope của họ.**
- **Giữ service layer là source of truth cho business rules.**
- **Controller chỉ gọi service, không chứa business logic.**
- **REST controller trả `ApiResponse`.**
- **JavaFX controller không gọi `HttpClient` trực tiếp.**
- **JavaFX flow chuẩn:**
  `FXML Controller -> Desktop Service -> API Client -> Backend REST API`

---

## Quy tắc bảo mật (Security Rules)

- Web Thymeleaf dùng form login/session.
- API `/api/**` dùng JWT stateless.
- **Public endpoints:**
  - `/login`
  - `/process-login`
  - static assets
  - `POST /api/v1/auth/login`
  - `POST /api/v1/auth/refresh`
  - `GET /api/v1/health`
- Các API còn lại phải protected.
- Role authority dùng prefix `ROLE_`.
- DB role lưu không prefix: `ADMIN`, `RECEPTIONIST`, `DOCTOR`, `CASHIER`, `MANAGER`.
- Access token không lưu DB.
- Refresh token lưu DB dạng hash trong bảng `refresh_tokens`.
- JavaFX lưu raw access token và refresh token trong memory session/token store cho MVP.

---

## Quy tắc Docs / Task quản lý

- `docs/issues.md` là issue catalog local.
- `task_allocation.md` phải phản ánh task đã hoàn thành/chưa hoàn thành trên `dev`.
- Nếu code đã merge mà task vẫn `[ ]`, phải update lại docs.
- `docs/security-matrix.md` là source of truth cho role access.
- `docs/business-rules.md` là source of truth cho rule nghiệp vụ.
- `docs/database-design.md` phải khớp entity/table hiện có.

---

## Trạng thái hiện tại

- `dev` đã có backend core từ login -> patient -> appointment -> check-in -> queue -> encounter -> service order -> invoice -> payment -> reporting/audit foundation.
- Backend tests gần nhất pass.
- JavaFX có REST foundation/login/token/session; các full screens #21-22 vẫn chủ yếu là scope Member 3.
- Có local work gần đây liên quan JWT signer key, refresh token, role authorization, business workflow tests, JavaFX appointment/check-in sample. Agent mới phải chạy `git status --short --branch` trước để xác định phần đó đã commit chưa.

---

## Quy tắc làm việc & Báo cáo

- Không revert thay đổi của người khác nếu user không yêu cầu rõ.
- Nếu có thay đổi chưa commit của user/agent trước, đọc kỹ và làm tiếp trên đó hoặc hỏi nếu conflict scope.
- Không dùng `git reset --hard` hoặc checkout bỏ file nếu chưa được phép.
- **Sau mỗi phần lớn, báo cáo:**
  - branch
  - commit hash/message nếu có
  - test result
  - PR URL hoặc PR title/body
  - task còn lại/blocker
