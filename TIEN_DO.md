# Tiến độ bài tập lớn Oracle - Quản lý sự cố CNTT

Cập nhật: 26/09/2026. Đề: "Đề số 30 - Đề mở" (`De Thi Oracle Liên thông UTC 2026.pdf`, học phần Công nghệ Oracle, 10 điểm).

## Đã xong (đều đã chạy thật và kiểm tra)

| Mục đề (điểm) | Việc đã làm | Ở đâu |
|---|---|---|
| 1 (0,5) Giới thiệu, thực thể, quan hệ | Chương 1 báo cáo + sơ đồ ER | `docs/BaoCao_Oracle_QuanLySuCo.docx`, `docs/so_do_ER_SUCO_APP.png` |
| 2 (2,5) CSDL | User `SUCO_APP`, tablespace `TS_SUCO`, 7 bảng, 28 ràng buộc, 2 trigger, 1 view, 77 bản ghi | `sql/01..05` |
| 3 (1) Truy vấn | 15 truy vấn (yêu cầu tối thiểu 10) | `sql/05_queries.sql` |
| 4 (5) Quản trị Oracle | Instance (NOMOUNT/MOUNT/READ ONLY/restricted), PDB, tablespace + datafile, ARCHIVELOG, RMAN khôi phục datafile mất, profile, user, role, expdp/impdp | `sql/06`, `07`, `08`; nhật ký thật ở `sql/ketqua/` |
| 5 (1) Web | Spring Boot: đăng nhập, báo sự cố, xử lý sự cố, kho kinh nghiệm, **quản lý thiết bị (thêm/sửa/xóa), quản lý người dùng (thêm/sửa/xóa), xóa sự cố** | `web/` |
| Báo cáo Word | 43 trang, Times New Roman 13, mục lục, số trang | `docs/BaoCao_Oracle_QuanLySuCo.docx` |

## Việc còn lại (làm tiếp)

1. **Báo cáo Word**: điền trang bìa (giảng viên, lớp, họ tên + MSV, đang để `[...]`); chụp và dán **7 ảnh màn hình** vào các khung `[Chèn ảnh chụp màn hình: ...]` ở mục 5.2 (đăng nhập, báo sự cố, danh sách sự cố admin, chi tiết sự cố, kho kinh nghiệm, quản lý thiết bị, quản lý người dùng); sau khi sửa, bấm chuột phải mục lục -> Update Field. Có thể thêm ảnh chụp kết quả chạy SQL*Plus/RMAN nếu muốn báo cáo sinh động hơn.
2. Đọc lại báo cáo một lượt, sửa cho đúng ý nhóm (tên nhóm, nơi ghi "Hà Nội", số thành viên).
3. **Chuẩn bị trình bày**: đề yêu cầu "máy tính chạy Oracle để trình bày các nội dung đã làm". Nên tập chạy lại các bước chính: web, một truy vấn, `08` (khóa user), RMAN. Kịch bản thử nghiệm có sẵn trong `sql/ketqua/kichban/`.
4. **Nộp**: file mềm Word share qua Drive cho giảng viên (hạn tối đa 3 ngày sau ngày thi, quá hạn trừ 50%), in 1 quyển cho mỗi nhóm.
5. (Tùy chọn) dọn máy sau khi xong, xem mục "Trạng thái máy" bên dưới.

## Cách chạy lại

- **Web**: `cd web` rồi `mvn spring-boot:run`, hoặc chạy sẵn: `java -jar web\target\suco-web-1.0.0.jar`, mở http://localhost:8080.
  Máy **chưa cài Maven** (đã dùng bản portable trong thư mục tạm, có thể mất). Nếu cần build lại: cài Maven hoặc dùng Maven có sẵn trong IntelliJ.
  Tài khoản demo: admin `hung.nv@utc.edu.vn` / `admin123`; user `anh.pd@st.utc.edu.vn` / `123456`.
  Mật khẩu trong CSDL được **băm PBKDF2** (không còn plain text). CSDL đã dựng từ trước: chạy thêm `sql/09_bam_mat_khau.sql` bằng SUCO_APP. In hash mới: `javac -d out web/src/main/java/vn/utc/suco/util/MatKhau.java tools/BamMatKhau.java` rồi `java -cp out BamMatKhau <mật khẩu>`.
- **Dựng lại CSDL từ đầu**: `sql/99_drop_all.sql` rồi `sql/00_run_all.sql` (cần mật khẩu SYSTEM). Cần SQL*Plus: `C:\app\GreyD\product\26ai\dbhomeFree\bin` (đặt `ORACLE_HOME`, `ORACLE_SID=FREE`; SYSDBA bằng `sqlplus / as sysdba`). File có tiếng Việt: chạy `chcp 65001` và `set NLS_LANG=.AL32UTF8` trước.
- **Vẽ lại sơ đồ ER** (sau khi sửa cấu trúc bảng): xem `SO_DO_QUAN_HE.md`.
- **Dựng lại báo cáo Word** (khi cần cập nhật số liệu): trong `tools/baocao/` chạy `npm install docx` một lần; `java -cp <ojdbc17.jar> XuatDuLieu.java <đường dẫn 05_queries.sql> dulieu.json`; `node build.js`; rồi `cap_nhat_muc_luc.ps1`. Lưu ý: chạy lại `build.js` sẽ **ghi đè** file Word, mất phần bạn đã chỉnh tay (bìa, ảnh). Chỉ dùng khi chưa chỉnh, hoặc lưu bản đã chỉnh sang tên khác trước.

## Trạng thái máy Oracle (khác so với ban đầu)

- Cơ sở dữ liệu đang ở chế độ **ARCHIVELOG** (ban đầu là NOARCHIVELOG); Fast Recovery Area `C:\ORADATA\fra` (5 GB). Archive log sẽ tích lũy dần. Muốn tắt: SYSDBA, `SHUTDOWN IMMEDIATE`, `STARTUP MOUNT`, `ALTER DATABASE NOARCHIVELOG`, `ALTER DATABASE OPEN`, mở lại PDB.
- File thừa có thể xóa tay: `C:\ORADATA\dpdump\suco_app.dmp` (+ log), bản sao lưu RMAN trong `C:\ORADATA\fra`, thư mục trống `C:\ORADATA\demo_move`.
- Các user/đối tượng của đề tài: `SUCO_APP`, `U_NHANVIEN`, `U_QUANTRI`, `TS_SUCO`, `ROLE_APP_USER`, `ROLE_APP_ADMIN`, `PROFILE_NHANVIEN`, `PROFILE_QUANTRI`, thư mục Oracle `DP_DIR`. Xóa sạch: `sql/99_drop_all.sql` (không xóa `DP_DIR`).
- `U_NHANVIEN` dùng profile khóa sau 3 lần nhập sai (khóa 1 giờ); mở khóa bằng `ALTER USER U_NHANVIEN ACCOUNT UNLOCK`.
- Không đụng tới `INDEX01.DBF`, `DATA02.DBF` và các thứ có sẵn của máy.

## Lưu ý, hạn chế đã biết

- Mật khẩu đã băm PBKDF2 + salt (báo cáo Word còn ghi plain text ở phần này, cần sửa lại cho khớp).
- Web kết nối bằng chính `SUCO_APP`; hai role Oracle được minh họa bằng `U_NHANVIEN`, `U_QUANTRI` ở phần SQL (đã ghi trong báo cáo mục 5.4).
- Oracle Free: **không** di chuyển datafile online (`ORA-00439`), tablespace mặc định là BIGFILE (cần `SMALLFILE` để thêm datafile); `impdp` cần tạo sẵn user đích. Đã sửa `07_admin_demo.sql` và ghi vào báo cáo.
- `07_admin_demo.sql` là kịch bản chạy thủ công từng khối, chưa chạy nguyên file một lần; phần đã kiểm chứng được chạy bằng các script tương đương trong `sql/ketqua/kichban/` và các lệnh trực tiếp (RMAN, expdp/impdp). Trong báo cáo, log RMAN, log bật ARCHIVELOG và bảng quyền ở mục 4.4.4 được chép lại từ kết quả thật, vì các lần chạy đó chưa lưu ra file.
- Khi test web bằng curl trên Windows, gửi tiếng Việt qua tham số dòng lệnh bị sai mã hóa; dùng `--data-urlencode "ten@file_utf8.txt"`.
