# Tiến độ bài tập lớn Oracle - Quản lý sự cố CNTT

Cập nhật: 26/09/2026. Đề: "Đề số 30 - Đề mở" (`De Thi Oracle Liên thông UTC 2026.pdf`, học phần Công nghệ Oracle, 10 điểm).

## Đã xong (đều đã chạy thật và kiểm tra)

| Mục đề (điểm) | Việc đã làm | Ở đâu |
|---|---|---|
| 1 (0,5) Giới thiệu, thực thể, quan hệ | Chương 1 báo cáo + sơ đồ ER | `docs/BaoCao_Oracle_QuanLySuCo.docx`, `docs/so_do_ER_SUCO_APP.png` |
| 2 (2,5) CSDL | User `SUCO_APP`, tablespace `TS_SUCO`, 8 bảng, 2 trigger, 2 view, ~80 bản ghi | `sql/01..05`, `sql/11` |
| 3 (1) Truy vấn | 17 truy vấn (yêu cầu tối thiểu 10) | `sql/05_queries.sql` |
| 4 (5) Quản trị Oracle | Instance (NOMOUNT/MOUNT/READ ONLY/restricted), PDB, tablespace + datafile, ARCHIVELOG, RMAN khôi phục datafile mất, profile, user, role, expdp/impdp | `sql/06`, `07`, `08`; nhật ký thật ở `sql/ketqua/` |
| 5 (1) Web | Spring Boot: đăng nhập, báo sự cố, xử lý sự cố, kho kinh nghiệm, quản lý thiết bị/phòng/danh mục (thêm/sửa/xóa), quản lý người dùng (xóa mềm), **phản hồi sau xử lý, "sự cố hay gặp"** | `web-oracle/` |
| Báo cáo Word | 43 trang, Times New Roman 13, mục lục, số trang | `docs/BaoCao_Oracle_QuanLySuCo.docx` |

## Việc còn lại (làm tiếp)

1. **Báo cáo Word**: điền trang bìa (giảng viên, lớp, họ tên + MSV, đang để `[...]`); chụp và dán **7 ảnh màn hình** vào các khung `[Chèn ảnh chụp màn hình: ...]` ở mục 5.2 (đăng nhập, báo sự cố, danh sách sự cố admin, chi tiết sự cố, kho kinh nghiệm, quản lý thiết bị, quản lý người dùng); sau khi sửa, bấm chuột phải mục lục -> Update Field. Có thể thêm ảnh chụp kết quả chạy SQL*Plus/RMAN nếu muốn báo cáo sinh động hơn.
2. Đọc lại báo cáo một lượt, sửa cho đúng ý nhóm (tên nhóm, nơi ghi "Hà Nội", số thành viên).
3. **Chuẩn bị trình bày**: đề yêu cầu "máy tính chạy Oracle để trình bày các nội dung đã làm". Nên tập chạy lại các bước chính: web, một truy vấn, `08` (khóa user), RMAN. Kịch bản thử nghiệm có sẵn trong `sql/ketqua/kichban/`.
4. **Nộp**: file mềm Word share qua Drive cho giảng viên (hạn tối đa 3 ngày sau ngày thi, quá hạn trừ 50%), in 1 quyển cho mỗi nhóm.
5. (Tùy chọn) dọn máy sau khi xong, xem mục "Trạng thái máy" bên dưới.

## Cách chạy lại

- **Web**: `cd web-oracle` rồi `mvn spring-boot:run`, hoặc chạy sẵn: `java -jar web-oracle\target\suco-web-1.0.0.jar`, mở http://localhost:8080.
  Máy **chưa cài Maven** (đã dùng bản portable trong thư mục tạm, có thể mất). Nếu cần build lại: cài Maven hoặc dùng Maven có sẵn trong IntelliJ.
  Tài khoản demo: admin `hung.nv@utc.edu.vn` / `admin123`; user `anh.pd@st.utc.edu.vn` / `123456`.
  Mật khẩu trong CSDL được **băm PBKDF2** (không còn plain text). CSDL đã dựng từ trước: chạy thêm `sql/09_bam_mat_khau.sql` bằng SUCO_APP. In hash mới: `javac -d out web-oracle/src/main/java/vn/utc/suco/util/MatKhau.java tools/BamMatKhau.java` rồi `java -cp out BamMatKhau <mật khẩu>`.
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

## Cập nhật 07/10/2026 (góp ý mở rộng từ thành viên trong nhóm, làm sau khi nộp bài để không ảnh hưởng báo cáo đã viết)

- **CSDL** (đã ALTER TABLE trực tiếp trên DB thật qua `sql/11_mo_rong_du_lieu.sql`; nếu rebuild từ đầu bằng `00_run_all.sql` thì đã có sẵn trong `02_tables.sql`/`04_data.sql`/`05_queries.sql`, không cần chạy lại file 11):
  - `NGUOI_DUNG`: thêm `SDT`, `TRANG_THAI` (HOAT_DONG/KHONG_HOAT_DONG, mặc định HOAT_DONG), `MA_NGUOI_TAO`, `NGAY_XOA`, `MA_NGUOI_XOA` (2 cột sau tự tham chiếu NGUOI_DUNG). Xóa người dùng giờ là **xóa mềm** (đổi TRANG_THAI, không DELETE); tài khoản KHONG_HOAT_DONG không đăng nhập được.
  - `THIET_BI`: thêm `NHA_CUNG_CAP`, `NGAY_LAP_DAT`, `HDSD` (đều nullable).
  - `PHONG.TANG`: bỏ NOT NULL (văn phòng 1 tầng không cần ghi).
  - Bảng mới `PHAN_HOI`: phản hồi của người báo sau khi sự cố HOAN_THANH (mức hài lòng 1-5 + nhận xét), mỗi sự cố chỉ 1 phản hồi (UNIQUE).
  - View mới `V_SU_CO_THUONG_GAP`: đếm sự cố theo danh mục — cố tình làm view/truy vấn thay vì cột tần suất tĩnh trên SU_CO để khỏi phải tự đồng bộ tay.
  - `sql/05_queries.sql`: từ 15 lên 17 truy vấn (Q16 top danh mục hay gặp, Q17 điểm hài lòng trung bình theo admin).
  - **Ảnh `docs/so_do_ER_SUCO_APP.png`**: lúc đó chưa cập nhật được vì `tools/VeSoDoER.java` viết tay theo đúng 7 bảng cũ. **Đã xử lý xong ngày 10/10/2026** (xem mục cuối file).
- **Web**: CRUD danh mục sự cố (`/admin/danh-muc`, trước đó danh mục chỉ sửa được qua INSERT tay); nút "Vô hiệu hóa/Kích hoạt" người dùng thay cho xóa cứng; thêm SĐT, nhà cung cấp, ngày lắp đặt, HDSD vào form thêm/sửa; người dùng gửi phản hồi (1-5 + nhận xét) ở trang chi tiết sự cố khi đã HOAN_THANH, admin xem phản hồi (chỉ đọc) ở trang chi tiết xử lý; trang `/user` hiển thị khối "Sự cố hay gặp" theo danh mục.
- Đã `mvn compile` sạch và chạy migration SQL thật trên DB đang có dữ liệu (dùng `tools/RunSql.java` + ojdbc17 jar trong `~/.m2`), kèm vài dòng dữ liệu demo an toàn (không dùng ID cố định, chỉ thêm khi còn thiếu). **Chưa test lại qua giao diện trình duyệt** — nên bật web rồi thử: thêm/sửa thiết bị có nhà cung cấp, vô hiệu hóa/kích hoạt người dùng, CRUD danh mục, báo sự cố xong gửi phản hồi — trước khi coi là xong hẳn.

## Cập nhật 10/10/2026 (sắp xếp lại giao diện web cho gọn, dùng được trên cả điện thoại và máy tính)

Chỉ đụng tới `web/src/main/resources` (template + CSS + 1 file JS nhỏ) và 2 file Java nhỏ, **không đổi CSDL, không đổi đường dẫn (URL) nào** nên các truy vấn/báo cáo đã viết vẫn đúng.

- **Menu**: máy tính = cột dọc cố định bên trái, chia nhóm "Xử lý hằng ngày" / "Dữ liệu nền", tô sáng mục đang mở (mỗi trang truyền tên mục vào fragment `layout :: menu('...')`). Điện thoại = thanh trên + ngăn kéo trượt (Bootstrap offcanvas) thay cho menu xổ đẩy nội dung như trước.
- **Bảng dữ liệu**: dưới 768px tự xếp mỗi dòng thành một thẻ, mỗi ô có nhãn ở bên trái (lớp `.bang-gon` + thuộc tính `data-label` trên `<td>`), hết cảnh phải kéo ngang trên điện thoại.
- **Ô tìm nhanh** ở mọi danh sách (`static/js/app.js`): lọc ngay trên trang, bỏ dấu tiếng Việt nên gõ "may in" vẫn ra "Máy in".
- **Trang `/admin`**: 4 ô số liệu Tất cả / Mới / Đang xử lý / Hoàn thành, bấm vào là lọc (thay ô select cũ). Số đếm lấy bằng `SuCoRepo.demTheoTrangThai()` (GROUP BY trên SU_CO).
- **Trang chi tiết sự cố**: máy tính chia 2 cột — trái là thông tin + phản hồi + lịch sử, phải là form cập nhật, không phải cuộn dài như trước.
- **Trang `/user`**: thêm 3 ô đếm sự cố của chính mình, form báo sự cố và khối "Sự cố hay gặp" nằm cạnh nhau trên màn rộng.
- **Các trang quản lý** (thiết bị, phòng, danh mục, người dùng, kho kinh nghiệm): form "Thêm" thu gọn sau một nút, mở ra khi cần (tự mở lại nếu lần gửi trước bị lỗi), nên danh sách hiện ngay đầu trang.
- **Kho kinh nghiệm**: danh sách dạng lưới thẻ thay cho list-group.
- Đã kiểm tra thật bằng Chrome ở 2 khổ 1440px và 390px cho các trang chính; ô tìm, nút Thêm và menu điện thoại chạy đúng, không lỗi JS.

**Lưu ý vận hành gặp hôm nay**: Oracle không mở được vì Fast Recovery Area 5 GB đã đầy 100% (43 archive log ~4,84 GB) → `ORA-19809` rồi `ORA-16038`, instance tự tắt khi OPEN. Cách xử lý đã làm: `STARTUP MOUNT` → RMAN `crosscheck archivelog all; delete noprompt archivelog all; delete noprompt obsolete;` → `ALTER DATABASE OPEN; ALTER PLUGGABLE DATABASE ALL OPEN;`. Sau đó FRA còn 0,37 GB. Vì DB vẫn ở ARCHIVELOG nên log sẽ đầy lại; nếu không cần demo RMAN nữa thì cân nhắc đưa về NOARCHIVELOG (xem mục "Trạng thái máy Oracle" ở trên).

### Vẽ lại sơ đồ ER (10/10/2026)

`tools/VeSoDoER.java` đã sửa xong, ảnh `docs/so_do_ER_SUCO_APP.png` giờ **đủ 8 bảng, 14 khóa ngoại**, đọc trực tiếp từ từ điển dữ liệu Oracle nên khớp CSDL thật:

- Thêm `PHAN_HOI` vào lưới bố trí ở ô trên cùng bên phải; nối với `SU_CO` qua hành lang ngang giữa hàng 0 và hàng 1, nối với `NGUOI_DUNG` qua hành lang phía trên (đã nới `top` từ 92 lên 124 để có chỗ).
- Vẽ 2 khóa ngoại tự tham chiếu của `NGUOI_DUNG` (`MA_NGUOI_TAO`, `MA_NGUOI_XOA`) thành hai vòng nhỏ dưới đáy bảng, nằm hai bên đường `MA_NGUOI_CAP_NHAT`.
- Phân biệt quan hệ 1-1: cột khóa ngoại có UNIQUE (`PHAN_HOI.MA_SC`) vẽ đầu con là `O|` (không hoặc một) thay vì chân chim; nhãn khóa trong bảng hiện `FK,UK`.
- Chạy lại: `java -cp %USERPROFILE%\.m2\repository\com\oracle\database\jdbc\ojdbc17\23.26.3.0.0\ojdbc17-23.26.3.0.0.jar tools\VeSoDoER.java` (mặc định ghi đè `docs/so_do_ER_SUCO_APP.png`). Nếu sau này thêm bảng/khóa ngoại mới mà chưa khai báo đường vẽ, chương trình in `CANH BAO: chua co duong noi cho ...` — phải thêm nhánh vẽ trong `VeSoDoER.java`.

**Tách `web/` thành `web-oracle/` + `web-java/` (20/10/2026):** theo góp ý "record khó giải thích với người mới", đổi tên
`web/` thành `web-oracle/` (giữ nguyên 100%, vẫn là bản đã nộp cho môn Oracle) và thêm `web-java/` — bản sao cùng chức
năng, cùng CSDL, nhưng viết lại phần Java cho đồ án Java riêng của nhóm:
- 11 `record` trong `model/` → `class` thường (field `private`, constructor, getter/setter, `equals`/`hashCode`/`toString` viết tay).
- Thêm enum `VaiTro`, `TrangThaiNguoiDung`, `TrangThaiSuCo`, `MucUuTien`, `TinhTrangThietBi` (trước đây là chuỗi `"ADMIN"`, `"HOAT_DONG"`...); cả 5 cùng implement interface mới `CoNhan` (`getNhan()`/`getLopBadge()`) để fragment Thymeleaf hiển thị badge mà không cần biết đang cầm enum nào (đa hình).
- `LuuAnh.java` (có 2 method lưu file gần giống nhau) tách thành `abstract class BoLuuTru` + 2 lớp con `LuuAnhSuCo`/`LuuTepTraoDoi` (kế thừa + trừu tượng + đa hình thật, không phải ví dụ giả); `ThongKeCot extends ThongKeDong` (kế thừa thêm % cột).
- Thêm exception riêng `FileKhongHopLeException extends RuntimeException` thay cho `IllegalArgumentException` dùng chung khi tải file sai loại.
- **Bẫy đã gặp khi test thật** (đăng nhập bằng 2 tài khoản tạo tạm qua `tools/RunSql.java`, xóa ngay sau khi xong): Thymeleaf/SpringEL **không** tự so sánh được enum với chuỗi theo kiểu `${obj.enumField == 'CHUOI'}` trong bản Spring Boot 4.1 này (dù một số tài liệu Thymeleaf cũ nói có) — `th:selected` của 3 dropdown (vai trò, trạng thái sự cố, tình trạng thiết bị) và bộ lọc đếm `dsSuCo.?[trangThai == '...']` ở `/user` đều im lặng sai (không báo lỗi, chỉ không chọn/không lọc đúng) cho tới khi sửa thành `${obj.enumField.name() == 'CHUOI'}`. Ngoài ra `Map<String,String>` tra theo field enum (`dsVaiTro[nd.vaiTro]`, `dsTinhTrang[tb.tinhTrang]`) cũng vỡ vì khóa Map vẫn là String — đổi sang gọi trực tiếp `nd.vaiTro.nhan`/`.lopBadge` qua interface `CoNhan`. Nhớ test lại bằng tài khoản thật (không chỉ nhìn code) nếu sau này đổi field String khác sang enum.

Còn lại duy nhất: **báo cáo Word `docs/BaoCao_Oracle_QuanLySuCo.docx`** vẫn là bản đã nộp 26/09, chưa có `PHAN_HOI`, các chức năng web thêm sau và giao diện mới. Nếu cập nhật thì nên lưu sang tên khác để giữ nguyên bản đã nộp.

## Cập nhật 31/10/2026 (sửa các lỗi được báo lại ở phần Admin của `web-java/`; `web-oracle/` giữ nguyên)

- **Người dùng**: ô mật khẩu đổi sang `type="password"`, thêm ô "Nhập lại mật khẩu" (form thêm + form sửa; JS `data-khop` trong `app.js` báo sớm, controller kiểm tra lại). SĐT không bắt buộc nhưng nếu nhập phải là 10 chữ số bắt đầu bằng 0 (bỏ qua dấu cách/chấm/gạch).
- **Sự cố, cập nhật bị ghi đè**: `SuCoRepo.capNhat` (@Transactional) giờ để lại một dòng `LICH_SU_XU_LY` cho **mỗi lần lưu có thay đổi**: giữ nguyên trạng thái thì web tự INSERT (trạng thái cũ = mới), đổi trạng thái thì trigger chèn như cũ và web thay câu ghi chú mặc định bằng nội dung lần này. Ghi chú = ô mới "Ghi chú lần cập nhật này" + nguyên nhân/cách khắc phục nếu có sửa (cắt ở 500 ký tự = độ dài cột). **Không đổi CSDL, không đổi trigger.**
- **Thiết bị, ngày không hợp lệ**: controller tự đọc `ngayLapDat` (String → LocalDate), sai định dạng hoặc ngoài khoảng 01/01/1990 đến hôm nay thì báo lỗi trên form; ô date có `min`/`max`. Thêm `XuLyLoiChung` (@ControllerAdvice: sai kiểu/thiếu tham số → quay lại trang trước kèm thông báo) và `templates/error.html` (trang lỗi tiếng Việt thay Whitelabel).
- **Chống trùng**: thiết bị cùng phòng + cùng loại + cùng tên, phòng cùng tòa + cùng tầng + cùng tên (không phân biệt hoa thường/khoảng trắng hai đầu; tầng trống tính là một giá trị) đều bị chặn ở controller (`tonTaiTrung` trong repo). Tầng phòng phải từ 0 đến 99. `sql/13_chong_trung_ten.sql` (**TÙY CHỌN, CHƯA CHẠY**) tạo thêm 2 unique index để chặn ở cả CSDL; chạy thì `web-oracle` gặp tên trùng sẽ ra trang lỗi ORA-00001.
- **Phân trang** sự cố / thiết bị / phòng: 10 dòng/trang (`model/Trang.java`, `OFFSET ... FETCH NEXT`), fragment `layout :: phanTrang`. Ô tìm của 3 trang này chuyển sang tìm phía server (`?q=`, fragment `layout :: oTim`, `util/TimKiem.java`) không dấu bằng `COLLATE BINARY_AI` ("may in" ra "Máy in", "dao tao" ra "Đào tạo"); link trang giữ `q` và `trangThai`. Các trang khác (danh mục, người dùng, kinh nghiệm) vẫn dùng ô tìm nhanh bằng JS.
- Đã chạy thật bằng curl trên CSDL thật với dữ liệu tạm (phòng/thiết bị/sự cố/user tên `ZZTEST`), xóa hết sau khi thử. **Chưa xem giao diện bằng trình duyệt** (vị trí ô tìm + thanh phân trang, thông báo JS mật khẩu không khớp).

## Cập nhật 02/11/2026 (góp ý của thành viên nhóm về trang thống kê, áp dụng cho cả `web-java/` và `web-oracle/`)

- **Thống kê**: biểu đồ có lại khoảng cách lề (trước đây `th:replace` đặt trên thẻ `.card-body` làm mất luôn thẻ đó). Bỏ biểu đồ "Theo trạng thái", thêm ô "Mới" để không mất số liệu; số bài kinh nghiệm chuyển thành ghi chú ở ô "Hài lòng" để giữ lưới 8 ô. "Sự cố theo tháng" chiếm một dòng, 12 tháng, nhãn ngắn ("T11", tháng 1 kèm năm). Xếp cặp: danh mục + phòng, ưu tiên + tình trạng, loại + admin; 2 thẻ cùng hàng cao bằng nhau (`h-100`). Ưu tiên và tình trạng luôn đủ 3 dòng (điền 0).
- **Xuất Excel** `GET /admin/xuat-excel?trangThai=&q=` (`util/ExcelSuCo.java`, Apache POI SXSSF): xuất mọi trang theo bộ lọc đang chọn. Ở `web-oracle`, ô tìm chỉ lọc bằng JS nên nút xuất gắn từ khóa lúc bấm (`data-kem-tu-khoa`) và server lọc theo tiêu đề, người báo, thiết bị, phòng, danh mục; ô tìm JS lọc trên mọi cột nên có thể lệch ở một số từ khóa (VD "Cao").
- **Nhập thiết bị từ Excel** `GET /admin/thiet-bi/mau-nhap`, `POST /admin/thiet-bi/nhap` (`util/ExcelThietBi.java`, giống hệt nhau ở 2 bản): file mẫu có danh sách thả xuống, nhập cả file hoặc không nhập gì, báo lỗi theo số dòng, chặn trùng với CSDL và trong file, tối đa 1000 dòng. Phòng trùng tên có nhãn kèm mã (`Tên [#7]`).
- **Phân trang Người dùng**: 10 dòng/trang, tìm phía server (họ tên, email, đơn vị, SĐT), vô hiệu hóa/kích hoạt xong quay lại đúng trang và từ khóa. `web-oracle` được thêm `Trang.java`, `TimKiem.java`, fragment `phanTrang`/`oTim` (các trang khác của `web-oracle` vẫn chưa phân trang).
- File quá 5MB: quay lại trang trước kèm thông báo (`XuLyLoiChung` ở `web-java`, `XuLyFileQuaLon` ở `web-oracle`). Thông báo này dùng biến `loi` nên ở các trang có form thu gọn sẽ làm form đó mở ra.
- **Không đổi CSDL.** Thêm dependency `poi-ooxml` 5.5.1. Đã chạy thật 88 kiểm tra trên cả 2 bản (dữ liệu tạm tên `ZZTEST`, đã xóa), mở file bằng Excel thật và xem ảnh chụp desktop/điện thoại.
- **Oracle tự tắt lúc 09:55 ngày 02/11/2026 vì FRA đầy** (ORA-19809 → ORA-16038). Đã tăng `DB_RECOVERY_FILE_DEST_SIZE` lên 7G (dùng 6496/7168MB, ổ C còn khoảng 6,5GB), chưa xóa archive log nên sẽ đầy lại nếu không dọn.

## Cập nhật 08/11/2026 (báo cáo OOAD, chỉ nằm trên nhánh `web-java`)

- **Vị trí**: `BAO_CAO_OOAD.md` và `docs/uml/` chỉ có trên nhánh `web-java` (worktree `C:\Users\GreyD\IdeaProjects\IMS-web-java`), **không có trên `main`**. Báo cáo dùng lại bài toán của bản `web-java` cho môn OOAD. `main` chỉ có thêm file Word `docs/BaoCao_Java_QuanLySuCo.docx`.
- **Đã vẽ lại toàn bộ sơ đồ bằng PlantUML** theo cách vẽ trong bài giảng (`Desktop\tài liệu\Bai-01…Bai-11 M.pdf`): actor người que, biểu tượng boundary/control/entity, thanh kích hoạt, thông điệp đánh số, khung alt/opt/loop. Có 26 hình, từ Hình 1.1 đến 3.15. Báo cáo không còn khối mermaid nào.
  - Nguồn ở `docs/uml/src/*.puml` (kiểu dáng chung ở `_style.iuml`), ảnh PNG ở `docs/uml/`. Tên file khớp số hình, ví dụ `h3_2b_sd_uc6_uc7` là Hình 3.4.
  - Xuất lại ảnh bằng `sh docs/uml/render.sh <đường dẫn plantuml.jar>`. Cần Java, không cần Graphviz (dùng `!pragma layout smetana`). Bản đã dùng: PlantUML 1.2024.7. Font Arial không có ký hiệu toán (∉, ∧) nên phải viết bằng chữ.
- **Nội dung bổ sung**: §1.8 sơ đồ hoạt động (yêu cầu phi chức năng chuyển xuống §1.9). Chương 2 viết lại: 2.1 phân tích danh từ, 2.2 mô hình khái niệm, 2.3 CRC, 2.4 BCE, 2.5 SSD, 2.6 điểm mơ hồ. §3.1 thêm phân hoạch/phân tầng, §3.2 thêm sơ đồ giao tiếp UC6/UC7, §3.3 DCD tách theo nhóm, §3.4 thêm trạng thái NguoiDung và bảng đối chiếu sự kiện với mã nguồn, §3.6 ERD dùng ký hiệu chân chim. 4 bảng đặc tả UC ở §1.7 có thêm dòng "Tổng quan" và "Yêu cầu tham khảo" cho đủ 9 mục như Bài 4. Phụ lục có mục "Mã nguồn các sơ đồ UML".
- **Các thay đổi nội dung cần nhóm đọc lại**: UC16 là «extend» của UC6. Actor được tổng quát hóa: Người báo và Cán bộ CNTT kế thừa "Người dùng hệ thống", nên UC4 và UC8 thuộc về actor cha. Mọi sơ đồ đều đối chiếu với code thật, ví dụ UC5 kiểm tra chủ sở hữu rồi mới gọi `sc.daHoanThanh()`; ảnh quá 5MB bị `XuLyLoiChung` chặn trước khi vào Controller.
- Đã commit `d34b044` (web-java) và `a103083` (main), đã push.
- **Còn lại (nhóm tự làm)**: điền bảng thông tin đầu báo cáo (nhóm, lớp, thành viên, GVHD, ngày nộp) và bảng phân công ở Phụ lục. Tự quyết phần giả định ở §2.6. Xin giảng viên xác nhận đề tài, vì đề tài không nằm trong danh sách gợi ý. Đọc lại toàn bộ báo cáo trước khi nộp.
