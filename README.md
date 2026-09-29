# Quản lý sự cố CNTT và kho kinh nghiệm xử lý - Trường ĐH Giao thông Vận tải

Bài tập lớn Oracle + Java: CSDL Oracle (thư mục `sql/`) và web Spring Boot MVC đơn giản (thư mục `web/`).
Sơ đồ bảng và quan hệ: xem [SO_DO_QUAN_HE.md](SO_DO_QUAN_HE.md) (ảnh ER `docs/so_do_ER_SUCO_APP.png`, vẽ tự động từ CSDL bằng `tools/VeSoDoER.java`).

## Môi trường

| Thành phần | Phiên bản |
|---|---|
| Oracle AI Database 26ai Free | 23.26.3.0.0, PDB `FREEPDB1`, cổng 1521 |
| JDK | 26.0.2.1 (biên dịch `release = 21`) |
| Spring Boot | 4.1.1 (Spring MVC, Thymeleaf, JdbcTemplate) |
| JDBC driver | `com.oracle.database.jdbc:ojdbc17:23.26.3.0.0` |
| Maven | 3.9.x |

## 1. Chạy script SQL

Mở terminal trong thư mục `sql/`. File có tiếng Việt có dấu (UTF-8): nên dùng **SQLcl** hoặc SQL Developer. Nếu dùng SQL*Plus trên Windows, chạy `chcp 65001` và `set NLS_LANG=.AL32UTF8` trước.

**macOS/Linux:** trước khi mở SQL*Plus chạy `export NLS_LANG=.AL32UTF8` (Terminal phải để UTF-8). Nếu không, tiếng Việt trong `04_data.sql` bị lưu thành ký tự lỗi `?`. Cách chắc nhất là dùng SQLcl (Java, mặc định UTF-8). Đường dẫn `C:\ORADATA\` trong `07_admin_demo.sql` chỉ là ví dụ Windows, phải đổi `DEFINE dir` thành thư mục của máy bạn (Oracle chạy trong Docker thì dùng `/opt/oracle/oradata/`).

**Cách nhanh:** `@00_run_all.sql` (hỏi mật khẩu SYSTEM một lần, chạy 01 -> 06 đúng user). Xóa sạch làm lại: `@99_drop_all.sql`.

**Cách chạy từng file:**

| Thứ tự | File | Chạy bằng | Kết nối vào |
|---|---|---|---|
| 1 | `01_tablespace_user.sql` | SYSTEM | `localhost:1521/FREEPDB1` |
| 2 | `02_tables.sql` | SUCO_APP | `localhost:1521/FREEPDB1` |
| 3 | `03_triggers.sql` | SUCO_APP | `localhost:1521/FREEPDB1` |
| 4 | `04_data.sql` | SUCO_APP | `localhost:1521/FREEPDB1` |
| 5 | `05_queries.sql` (tạo view `V_SU_CO_CHI_TIET` + truy vấn mẫu) | SUCO_APP | `localhost:1521/FREEPDB1` |
| 6 | `06_users_roles.sql` (phân quyền, user demo) | SYSTEM | `localhost:1521/FREEPDB1` |
| 7 | `08_quan_tri_user.sql` (PROFILE, tạo/khóa/mở khóa/đổi mật khẩu/quota/xóa user, role nhiều cấp) | SYSTEM | `localhost:1521/FREEPDB1` (hỏi mật khẩu SYSTEM) |
| - | `07_admin_demo.sql` (kịch bản quản trị: instance, tablespace, RMAN, expdp/impdp) | SYSDBA | **chạy thủ công từng khối**, có ghi rõ CDB hay PDB |

Báo cáo Word (43 trang, có mục lục và số trang): `docs/BaoCao_Oracle_QuanLySuCo.docx`.

Kết quả các lần chạy thật của 07 và 08 (log SQL*Plus, expdp, impdp) nằm trong thư mục `sql/ketqua/`, dùng làm bằng chứng cho báo cáo.

Ví dụ: `sql system@localhost:1521/FREEPDB1` rồi `@01_tablespace_user.sql`.

> Web bắt buộc phải có view `V_SU_CO_CHI_TIET` (tạo ở file 05). `00_run_all.sql` đã gồm cả 05.
> `07_admin_demo.sql` có lệnh SHUTDOWN, DROP TABLESPACE, RMAN: đọc comment và sửa biến `dir`, `dir2` (đường dẫn datafile) trước khi chạy.

## 2. Build và chạy web

```
cd web
mvn spring-boot:run
```

Mở http://localhost:8080. Cấu hình kết nối trong `web/src/main/resources/application.properties` (user `SUCO_APP`, mật khẩu `SuCo_App_123`). Đóng gói: `mvn package` rồi `java -jar target/suco-web-1.0.0.jar`.

## 3. Tài khoản demo

Mật khẩu được băm PBKDF2-HMAC-SHA256 + salt ngẫu nhiên (`web/…/util/MatKhau.java`) trước khi lưu vào `NGUOI_DUNG.MAT_KHAU`. CSDL dựng từ bản cũ: chạy thêm `sql/09_bam_mat_khau.sql`.

| Vai trò | Email | Mật khẩu |
|---|---|---|
| ADMIN | `hung.nv@utc.edu.vn` | `admin123` |
| ADMIN | `ha.ttt@utc.edu.vn` | `admin123` |
| USER | `anh.pd@st.utc.edu.vn` (Phạm Đức Anh) | `123456` |
| USER | `lan.lt@utc.edu.vn` (Lê Thị Lan) | `123456` |
| USER | các user còn lại trong `04_data.sql` | `123456` |

Tài khoản Oracle demo (file 06): `U_NHANVIEN` / `Nhanvien_123` (role `ROLE_APP_USER`), `U_QUANTRI` / `Quantri_123` (role `ROLE_APP_ADMIN`). Web luôn kết nối bằng `SUCO_APP`; hai user này dùng để demo phân quyền trong SQL.

## 4. Chức năng

1. **Đăng nhập** (`/login`): kiểm tra email + mật khẩu trong `NGUOI_DUNG`, lưu vào `HttpSession`. USER -> `/user`, ADMIN -> `/admin`, `/logout` để thoát. Chưa đăng nhập thì bị chuyển về `/login`.
2. **/user**: form báo sự cố (thiết bị, danh mục lấy từ CSDL) và bảng sự cố của chính mình.
3. **/admin**: (chỉ ADMIN) bảng mọi sự cố, lọc theo trạng thái; trang chi tiết đổi trạng thái, ghi nguyên nhân, cách khắc phục, chọn bài kinh nghiệm, **xóa sự cố** (kèm lịch sử); hiển thị lịch sử xử lý.
   - **/admin/thiet-bi**: danh sách, **thêm, sửa, xóa** thiết bị. Thiết bị đã có sự cố thì Oracle chặn xóa (khóa ngoại), web báo lỗi dễ hiểu.
   - **/admin/nguoi-dung**: danh sách, **thêm, sửa, xóa** người dùng, đổi vai trò, đặt lại mật khẩu (để trống = giữ nguyên). Email không được trùng; admin không tự xóa hoặc tự hạ quyền chính mình; người dùng đã có sự cố/bài viết thì không xóa được.
4. **/kinh-nghiem**: danh sách bài kinh nghiệm, lọc theo danh mục, xem chi tiết (USER và ADMIN đều xem được).

## 5. Luồng MVC

```
Trình duyệt --HTTP--> AuthInterceptor (kiểm tra session/vai trò)
                          |
                          v
                      Controller  --gọi--> Repository (SQL viết tay, JdbcTemplate) --JDBC--> Oracle FREEPDB1
                          |                                                                 (bảng, view, trigger)
                          v  Model (record) đưa vào Model của Spring
                    View Thymeleaf (templates/*.html, layout Bootstrap 5)
                          |
                          v
                       HTML trả về trình duyệt
```

Ví dụ luồng "admin đổi trạng thái": `POST /admin/{id}` -> `AdminController.capNhat` -> `SuCoRepo.capNhat` (UPDATE `SU_CO`, gán `MA_ADMIN_XU_LY` = admin đăng nhập) -> trigger `TRG_SU_CO_HOAN_THANH` gán ngày hoàn thành và `TRG_SU_CO_LICH_SU` ghi `LICH_SU_XU_LY` -> redirect về `GET /admin/{id}` hiển thị lịch sử mới.

## 6. Cấu trúc thư mục

```
sql/   00_run_all.sql, 01..07, 99_drop_all.sql
web/   pom.xml, src/main/java/vn/utc/suco/{model,repository,controller,config}, src/main/resources/{application.properties,templates}
```
