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

Mở terminal trong thư mục `sql/`. File có tiếng Việt có dấu (UTF-8): nên dùng **SQLcl** hoặc SQL Developer. Nếu dùng SQL*Plus trên Windows, chạy `chcp 65001` và `set NLS_LANG=.AL32UTF8` trước. Trên macOS/Linux chạy `export NLS_LANG=.AL32UTF8` (Terminal phải để UTF-8); nếu không, tiếng Việt trong `04_data.sql` bị lưu thành ký tự lỗi `?`.

### 1A. Chưa cài gì: dựng CSDL từ đầu (làm theo cách này)

Kết nối bằng SQLcl/SQL*Plus vào `localhost:1521/FREEPDB1`, đứng trong `sql/`, rồi chạy **một lệnh**:

```
@00_run_all.sql
```

Lệnh hỏi mật khẩu SYSTEM một lần rồi tự chạy lần lượt 01 -> 06 và 08 (tạo tablespace, user, bảng, trigger, dữ liệu mẫu, view, phân quyền, quản trị user). Xong là chạy được web (mục 2), không cần bước nào khác. Dữ liệu mẫu đã có sẵn mật khẩu băm.

Làm lại từ đầu: `@99_drop_all.sql` (xóa sạch, tắt web trước) rồi `@00_run_all.sql`.

### 1B. Đã cài rồi: chỉ chạy phần cần thêm

Không chạy lại `00_run_all.sql` (sẽ lỗi vì đối tượng đã tồn tại). Chọn đúng file theo việc cần làm:

| Cần làm | File | Chạy bằng |
|---|---|---|
| CSDL dựng từ **bản cũ** (mật khẩu còn plain text), đăng nhập web bị lỗi | `09_bam_mat_khau.sql` (băm PBKDF2 mật khẩu 10 user mẫu) | SUCO_APP |
| Xem lại 15 truy vấn mẫu, hoặc tạo lại view `V_SU_CO_CHI_TIET` (web bắt buộc phải có view này) | `05_queries.sql` | SUCO_APP |
| Tạo lại phân quyền, user demo `U_NHANVIEN`, `U_QUANTRI` | `06_users_roles.sql` | SYSTEM |
| Demo profile, khóa/mở khóa user, quota, role nhiều cấp | `08_quan_tri_user.sql` | SYSTEM |
| Demo quản trị: instance, tablespace, RMAN, expdp/impdp | `07_admin_demo.sql` | SYSDBA, **chạy thủ công từng khối** |

Riêng `07_admin_demo.sql` có lệnh SHUTDOWN, DROP TABLESPACE, RMAN: đọc comment và sửa biến `dir`, `dir2` (đường dẫn datafile) trước khi chạy. `C:\ORADATA\` chỉ là ví dụ Windows (Oracle chạy trong Docker thì dùng `/opt/oracle/oradata/`).

Ví dụ: `sql system@localhost:1521/FREEPDB1` rồi `@08_quan_tri_user.sql`.

### Bảng tổng hợp các file

| File | Nội dung | Chạy bằng |
|---|---|---|
| `00_run_all.sql` | gọi 01 -> 06, 08 | SYSTEM (tự đổi user) |
| `01_tablespace_user.sql` | tablespace `TS_SUCO`, user `SUCO_APP` | SYSTEM |
| `02_tables.sql`, `03_triggers.sql`, `04_data.sql` | bảng, trigger, dữ liệu mẫu | SUCO_APP |
| `05_queries.sql` | view + truy vấn mẫu | SUCO_APP |
| `06_users_roles.sql` | phân quyền, user demo | SYSTEM |
| `07_admin_demo.sql` | kịch bản quản trị | SYSDBA |
| `08_quan_tri_user.sql` | profile, quản trị user | SYSTEM |
| `09_bam_mat_khau.sql` | nâng cấp CSDL bản cũ | SUCO_APP |
| `99_drop_all.sql` | xóa sạch | SYSTEM |

Báo cáo Word: `docs/BaoCao_Oracle_QuanLySuCo.docx`. Log các lần chạy thật của 07 và 08 (SQL*Plus, expdp, impdp) nằm trong `sql/ketqua/`, dùng làm bằng chứng cho báo cáo.

## 2. Build và chạy web

Điều kiện: Oracle đang chạy (service `FREEPDB1`, cổng 1521) và đã dựng CSDL ở mục 1.

**Cách 1: Maven** (cần cài Maven 3.9.x và `JAVA_HOME` trỏ tới JDK; sau khi cài phải mở terminal mới). `pom.xml` nằm trong `web/`, không phải thư mục gốc:

```
cd web
mvn spring-boot:run
```

**Cách 2: chạy file jar đã build** (không cần Maven). Đứng trong `web/`:

```
java -jar target\suco-web-1.0.0.jar
```

Nếu đứng ở thư mục gốc thì dùng `java -jar web\target\suco-web-1.0.0.jar`. Nếu cổng 8080 đã bị chiếm thì tắt tiến trình cũ (`Get-NetTCPConnection -LocalPort 8080`) trước.

**Chia sẻ tạm qua internet (tùy chọn):** cài `cloudflared`, rồi `cloudflared tunnel --url http://localhost:8080`. Link `https://….trycloudflare.com` nằm trong log, đổi mỗi lần chạy. Web có tài khoản demo mật khẩu yếu nên chỉ chia sẻ cho người tin cậy và tắt tunnel (Ctrl+C) khi xong.

Mở http://localhost:8080. Cấu hình kết nối trong `web/src/main/resources/application.properties` (user `SUCO_APP`, mật khẩu `SuCo_App_123`). Đóng gói: `mvn package` (ra `web/target/suco-web-1.0.0.jar`).

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
sql/     00_run_all.sql, 01..09, 99_drop_all.sql; ketqua/ (log chạy thật của phần quản trị, kịch bản thử trong ketqua/kichban/)
web/     pom.xml, src/main/java/vn/utc/suco/{model,repository,controller,config,util}, src/main/resources/{application.properties,templates}
docs/    BaoCao_Oracle_QuanLySuCo.docx (báo cáo Word), so_do_ER_SUCO_APP.png (sơ đồ ER)
tools/   BamMatKhau.java (in hash mật khẩu), VeSoDoER.java (vẽ ER), RunSql.java, baocao/ (dựng báo cáo Word)
```

Tiến độ và các việc còn lại: xem [TIEN_DO.md](TIEN_DO.md).
