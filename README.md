# Quản lý sự cố CNTT và kho kinh nghiệm xử lý - Trường ĐH Giao thông Vận tải

Đồ án Oracle: CSDL Oracle (thư mục `sql/`) và web Spring Boot MVC (`src/`) quản lý sự cố CNTT và kho kinh
nghiệm xử lý. Sơ đồ bảng và quan hệ: xem [SO_DO_QUAN_HE.md](SO_DO_QUAN_HE.md).

## Môi trường

| Thành phần | Phiên bản |
|---|---|
| Oracle AI Database 26ai Free | 23.26.3.0.0, PDB `FREEPDB1`, cổng 1521 |
| JDK | 21+ (biên dịch `release = 21`) |
| Spring Boot | 4.1.1 (Spring MVC, Thymeleaf, JdbcTemplate) |
| JDBC driver | `com.oracle.database.jdbc:ojdbc17:23.26.3.0.0` |
| Maven | 3.9.x |

## 1. Chạy script SQL

Mở terminal trong thư mục `sql/`. File có tiếng Việt có dấu (UTF-8): nên dùng **SQLcl** hoặc SQL Developer.
Nếu dùng SQL*Plus trên Windows, chạy `chcp 65001` và `set NLS_LANG=.AL32UTF8` trước; trên macOS/Linux
`export NLS_LANG=.AL32UTF8` (terminal phải để UTF-8), nếu không tiếng Việt trong `04_data.sql` bị lưu sai.

Kết nối bằng SQLcl/SQL*Plus vào `localhost:1521/FREEPDB1`, đứng trong `sql/`, rồi chạy **một lệnh**:

```
@00_run_all.sql
```

Lệnh hỏi mật khẩu SYSTEM một lần rồi tự chạy lần lượt 01 -> 07 (tạo tablespace, user, bảng, trigger, dữ
liệu mẫu, view + truy vấn, phân quyền, quản trị user/profile). Xong là chạy được web (mục 2), không cần
bước nào khác.

Làm lại từ đầu: `@99_drop_all.sql` (xóa sạch, tắt web trước) rồi `@00_run_all.sql`.

| File | Nội dung | Chạy bằng |
|---|---|---|
| `00_run_all.sql` | gọi 01 -> 07 | SYSTEM (tự đổi user) |
| `01_tablespace_user.sql` | tablespace `TS_SUCO`, user `SUCO_APP`, 2 role ứng dụng | SYSTEM |
| `02_tables.sql` | 9 bảng, index | SUCO_APP |
| `03_triggers.sql` | 2 trigger trên SU_CO (ghi lịch sử, tự gán ngày hoàn thành) | SUCO_APP |
| `04_data.sql` | dữ liệu mẫu tiếng Việt cho cả 9 bảng | SUCO_APP |
| `05_queries.sql` | 2 view web đọc trực tiếp + 17 truy vấn mẫu (JOIN, lồng, GROUP BY...) | SUCO_APP |
| `06_users_roles.sql` | cấp quyền bảng cho 2 role, user demo `U_NHANVIEN`/`U_QUANTRI` | SYSTEM |
| `07_quan_tri_user.sql` | profile, quản trị user, role nhiều cấp, GRANT/REVOKE mẫu | SYSTEM |
| `99_drop_all.sql` | xóa sạch | SYSTEM |

## 2. Build và chạy web

Điều kiện: Oracle đang chạy (service `FREEPDB1`, cổng 1521) và đã dựng CSDL ở mục 1.

**Cách 1: Maven** (cần cài Maven 3.9.x và `JAVA_HOME` trỏ tới JDK):

```
mvn spring-boot:run
```

**Cách 2: chạy file jar đã build** (không cần Maven, sau khi `mvn package`):

```
java -jar target\suco-web-1.0.0.jar
```

Nếu cổng 8080 đã bị chiếm thì tắt tiến trình cũ trước (Windows: `Get-NetTCPConnection -LocalPort 8080`).

Mở http://localhost:8080. Cấu hình kết nối trong `src/main/resources/application.properties` (user
`SUCO_APP`, mật khẩu `SuCo_App_123`).

## 3. Tài khoản demo

Mật khẩu được băm PBKDF2-HMAC-SHA256 + salt ngẫu nhiên (`util/MatKhau.java`) trước khi lưu vào
`NGUOI_DUNG.MAT_KHAU`.

| Vai trò | Email | Mật khẩu |
|---|---|---|
| ADMIN | `hung.nv@utc.edu.vn` | `admin123` |
| ADMIN | `ha.ttt@utc.edu.vn` | `admin123` |
| USER | `anh.pd@st.utc.edu.vn` (Phạm Đức Anh) | `123456` |
| USER | `lan.lt@utc.edu.vn` (Lê Thị Lan) | `123456` |
| USER | các user còn lại trong `sql/04_data.sql` | `123456` |

Tài khoản Oracle demo (file `06_users_roles.sql`): `U_NHANVIEN` / `Nhanvien_123` (role `ROLE_APP_USER`),
`U_QUANTRI` / `Quantri_123` (role `ROLE_APP_ADMIN`). Web luôn kết nối bằng `SUCO_APP`; hai user này chỉ để
demo phân quyền trong SQL.

## 4. Chức năng

1. **Đăng nhập** (`/login`): kiểm tra email + mật khẩu trong `NGUOI_DUNG`, lưu vào `HttpSession`. USER -> `/user`, ADMIN -> `/admin`, `/logout` để thoát. Tài khoản `KHONG_HOAT_DONG` không đăng nhập được.
2. **/user**: form báo sự cố (thiết bị, danh mục lấy từ CSDL, ảnh đính kèm tùy chọn), số đếm sự cố của mình theo trạng thái, khối **"sự cố hay gặp"** (đọc view `V_SU_CO_THUONG_GAP`) và bảng sự cố của chính mình.
   - **/user/{id}**: xem chi tiết sự cố của mình, nguyên nhân/cách khắc phục do IT ghi, lịch sử xử lý; khi sự cố đã `HOAN_THANH` thì **gửi phản hồi** (mức hài lòng 1-5 + nhận xét, mỗi sự cố chỉ 1 lần) và khối **trao đổi** để nhắn qua lại với IT bất kỳ lúc nào (không giới hạn số lần, có thể đính kèm file).
3. **/admin**: (chỉ ADMIN) bảng mọi sự cố, 4 ô số liệu bấm vào để lọc theo trạng thái; trang chi tiết đổi trạng thái, ghi nguyên nhân, cách khắc phục, chọn hoặc **tạo luôn bài kinh nghiệm** từ nội dung vừa nhập, xem phản hồi của người báo, **trao đổi** qua lại với người báo, **xóa sự cố** (kèm lịch sử).
   - **/admin/thiet-bi**: danh sách, thêm/sửa/xóa thiết bị (nhà cung cấp, ngày lắp đặt, hướng dẫn sử dụng), **nhập từ Excel**. Thiết bị đã có sự cố thì Oracle chặn xóa (khóa ngoại).
   - **/admin/phong**: danh sách, thêm/sửa/xóa phòng. Phòng còn thiết bị thì không xóa được.
   - **/admin/danh-muc**: danh sách, thêm/sửa/xóa danh mục sự cố. Tên không được trùng; danh mục còn sự cố hoặc bài kinh nghiệm thì không xóa được.
   - **/admin/nguoi-dung**: danh sách (phân trang), thêm/sửa người dùng, đổi vai trò, đặt lại mật khẩu, **vô hiệu hóa/kích hoạt lại** (xóa mềm). Email không được trùng; admin không tự vô hiệu hóa hoặc tự hạ quyền chính mình.
   - **/admin/kinh-nghiem**: thêm/sửa/xóa bài kinh nghiệm. Bài đang được một sự cố tham chiếu thì không xóa được.
   - **/admin/thong-ke**: số liệu nhanh + biểu đồ cột (CSS thuần) theo tháng/trạng thái/danh mục/phòng/admin/thiết bị, **xuất Excel** danh sách sự cố.
4. **/kinh-nghiem**: danh sách bài kinh nghiệm dạng lưới thẻ, lọc theo danh mục, tìm nhanh theo tiêu đề.

**Giao diện:** dùng được cả trên điện thoại và máy tính (Bootstrap 5, menu dọc cố định trên desktop / ngăn
kéo offcanvas trên mobile, bảng tự xếp thành thẻ dưới 768px). Mọi danh sách có ô tìm nhanh lọc ngay trên
trang (bỏ dấu tiếng Việt khi gõ).

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

Ví dụ luồng "admin đổi trạng thái": `POST /admin/{id}` -> `AdminController.capNhat` -> `SuCoRepo.capNhat`
(UPDATE `SU_CO`, gán `MA_ADMIN_XU_LY` = admin đăng nhập) -> trigger `TRG_SU_CO_HOAN_THANH` gán ngày hoàn
thành và `TRG_SU_CO_LICH_SU` ghi `LICH_SU_XU_LY` -> redirect về `GET /admin/{id}` hiển thị lịch sử mới.

## 6. Cấu trúc thư mục

```
pom.xml
sql/    00_run_all.sql, 01_tablespace_user.sql .. 07_quan_tri_user.sql, 99_drop_all.sql
src/main/java/vn/utc/suco/{model,repository,controller,config,util}
src/main/resources/{application.properties, templates/*.html, static/css/app.css, static/js/app.js}
uploads/ (ảnh/file đính kèm người dùng tải lên, tự tạo khi chạy, map ra URL /uploads/**)
```
