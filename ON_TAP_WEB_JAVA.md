# Ôn tập phần Java của `web-java/` (hệ thống quản lý sự cố CNTT)

Tài liệu tóm tắt những gì phần Java trong `web-java/` đã làm, kèm bộ câu hỏi vấn đáp có gợi ý trả lời.
Mọi đường dẫn file bên dưới đều tính từ `web-java/src/main/java/vn/utc/suco/`.

Dùng cho **báo cáo/vấn đáp môn Java** — chỉ nói phần Java (OOP, Spring MVC, bảo mật tầng web...). Hai môn dùng
chung 1 project cho tiện, không phải do 2 giáo viên yêu cầu làm chung, nên phần Oracle (thiết kế bảng, ràng
buộc, trigger, view, quản trị Oracle...) không lặp lại sâu ở đây — xem riêng ở `ON_TAP_ORACLE.md`. Tài liệu
này chỉ nhắc Oracle ở mức "Java gọi gì, Oracle trả gì" (JDBC, transaction, exception) vì đó là kiến thức Java.

---

## 1. Tổng quan

| Mục | Nội dung |
|---|---|
| Loại ứng dụng | Web MVC, render HTML ở server (không phải REST API + SPA) — có thêm vài endpoint JSON nhỏ (`@ResponseBody`) cho polling AJAX, xem mục 9.I |
| Framework | Spring Boot 4.1.1 (`spring-boot-starter-webmvc`, `-thymeleaf`, `-jdbc`) |
| Ngôn ngữ | Java, biên dịch ở mức `release 21` (chạy được trên JDK mới hơn) |
| CSDL | Oracle (PDB `FREEPDB1`, user `SUCO_APP`), driver `ojdbc17` |
| Truy cập dữ liệu | `JdbcTemplate` + SQL viết tay (KHÔNG dùng JPA/Hibernate) |
| Connection pool | HikariCP (Spring Boot tự cấu hình khi có starter-jdbc) |
| View | Thymeleaf + Bootstrap, thêm 1 file `static/js/app.js` để tìm nhanh trên trang |
| Bảo mật | Tự viết: `HttpSession` + `HandlerInterceptor` (KHÔNG dùng Spring Security); mật khẩu băm PBKDF2 |
| Quy mô | 49 file Java, khoảng 4.300 dòng: 1 lớp khởi động, 2 config, 10 controller, 19 model, 9 repository, 8 util |

**Khác nhau giữa `web-oracle/` và `web-java/`:** dùng chung CSDL và giao diện. `web-java/` viết lại phần Java để thể hiện OOP rõ hơn:
- 11 `record` được đổi thành class thường (field `private`, getter/setter, `equals`/`hashCode`/`toString`).
- Thêm 5 `enum` (`VaiTro`, `TrangThaiNguoiDung`, `TrangThaiSuCo`, `MucUuTien`, `TinhTrangThietBi`), cả 5 cùng implement interface `CoNhan`.
- `LuuAnh` được tách thành lớp trừu tượng `BoLuuTru` và 2 lớp con `LuuAnhSuCo`, `LuuTepTraoDoi`.
- Thêm exception riêng `FileKhongHopLeException`.

### Kiến trúc phân tầng

```
Trình duyệt
   │  HTTP request (GET/POST form)
   ▼
AuthInterceptor (config)        ← chặn nếu chưa đăng nhập / không phải ADMIN
   ▼
Controller (controller/*)       ← nhận tham số, kiểm tra dữ liệu, gọi repo, đặt dữ liệu vào Model
   ▼
Repository (repository/*)       ← SQL + JdbcTemplate, đổi ResultSet thành đối tượng model
   ▼
Oracle (bảng, view, trigger, ràng buộc)
   ▲
Model (model/*)                 ← đối tượng dữ liệu đi qua các tầng
   ▼
Thymeleaf template (resources/templates/*.html) ← tạo HTML trả về
```

Lưu ý: dự án **không có tầng Service**, controller gọi thẳng repository. Một số nghiệp vụ được đẩy xuống Oracle (trigger ghi lịch sử, trigger gán ngày hoàn thành, ràng buộc UNIQUE/CHECK/FK).

---

## 2. Khởi động và cấu hình

### `SucoApplication.java`
- `@SpringBootApplication` gộp 3 annotation: `@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan`. Spring quét gói `vn.utc.suco` và các gói con để tìm `@Controller`, `@Repository`, `@Component`, `@Configuration`.
- `main` gọi `SpringApplication.run(...)`, chạy Tomcat nhúng ở cổng 8080.

### `application.properties`
- Thông tin kết nối datasource (url, user, password, driver).
- `spring.thymeleaf.cache=false`: sửa HTML là thấy ngay, không phải khởi động lại.
- `server.servlet.encoding.*`: ép UTF-8 để hiện đúng tiếng Việt.
- `app.upload-dir=uploads`: thuộc tính tự đặt, đọc bằng `@Value("${app.upload-dir:uploads}")` (phần sau dấu `:` là giá trị mặc định).
- `spring.servlet.multipart.max-file-size=5MB`: giới hạn dung lượng file tải lên.

### `config/AuthInterceptor.java` (implements `HandlerInterceptor`)
- `preHandle` chạy **trước** khi vào controller:
  - `req.getSession(false)`: lấy session nếu đã có, **không tạo mới**.
  - Không có thuộc tính `"nguoiDung"` trong session → `redirect /login`, trả `false` để dừng request.
  - URL bắt đầu bằng `/admin` mà người dùng không phải admin (`!nd.laAdmin()`) → `redirect /user`.
- Đây là cách **phân quyền theo vai trò** (USER/ADMIN) mà không cần Spring Security.

### `config/WebConfig.java` (implements `WebMvcConfigurer`)
- `addInterceptors`: chỉ gắn interceptor cho `/user/**`, `/admin/**`, `/kinh-nghiem/**`. Các trang `/login`, `/logout`, file tĩnh không bị chặn.
- `addResourceHandlers`: ánh xạ URL `/uploads/**` tới thư mục thật trên đĩa, nhờ vậy ảnh đã tải lên mở được bằng trình duyệt.
- Nhận `AuthInterceptor` qua **constructor injection**.

---

## 3. Tầng Model: phần OOP chính

### 3.1. Các lớp thực thể (đóng gói, encapsulation)
`NguoiDung`, `SuCo`, `ThietBi`, `Phong`, `DanhMuc`, `BaiKinhNghiem`, `PhanHoi`, `BinhLuan`, `LichSu`, `SuCoHayGap`, `ThongKeDong`, `ThongKeCot`.

Các lớp này viết cùng một khuôn:
- Field `private`, truy cập qua getter/setter (JavaBean). Thymeleaf đọc `${suCo.tieuDe}` bằng cách gọi `getTieuDe()`.
- 2 constructor: rỗng (quy ước JavaBean) và đầy đủ tham số (repository dùng khi tạo đối tượng).
- `equals`/`hashCode` **chỉ dựa trên khóa chính** (`maNd`, `maSc`...): 2 đối tượng cùng mã thì được coi là bằng nhau. Có dùng *pattern matching for instanceof* (`if (!(o instanceof SuCo that))`, có từ Java 16).
- `toString()` ghi đè để in log, debug cho dễ đọc.
- Một số **phương thức nghiệp vụ** đặt ngay trong model:
  - `NguoiDung.laAdmin()`, `NguoiDung.dangHoatDong()`
  - `SuCo.daHoanThanh()`: dùng để ẩn/hiện form phản hồi, và để controller kiểm tra trước khi cho gửi phản hồi.
  - `BinhLuan.cuaAdmin()`: tô màu tin nhắn trong khung chat.
- `NguoiDung` **cố ý không có field mật khẩu**, vì đối tượng này được lưu vào session.
- Field kiểu enum thay cho String (`SuCo.trangThai` có kiểu `TrangThaiSuCo`, `NguoiDung.vaiTro` có kiểu `VaiTro`...). Kiểu ngày dùng `LocalDateTime`/`LocalDate` (java.time), không dùng `java.util.Date`.
- Field có thể null dùng kiểu bọc (`Long maTb`, `Integer tang`), field luôn có giá trị dùng kiểu nguyên thủy (`int mucHaiLong`, `int soLuong`).

### 3.2. Interface `CoNhan` và 5 enum (trừu tượng, đa hình)
```java
public interface CoNhan {
    String getNhan();      // nhãn tiếng Việt, vd "Đang xử lý"
    String getLopBadge();  // class CSS Bootstrap, vd "text-bg-warning"
}
```
- `VaiTro`, `TrangThaiNguoiDung`, `TrangThaiSuCo`, `MucUuTien`, `TinhTrangThietBi` đều `implements CoNhan`.
- Mỗi hằng enum mang dữ liệu riêng qua **constructor của enum**, ví dụ `DANG_XU_LY("Đang xử lý", "text-bg-warning")`. Field `private final` nên giá trị không đổi được.
- **Đa hình:** fragment Thymeleaf `layout :: trangThai(t)` chỉ gọi `${t.nhan}` và `${t.lopBadge}`, nên dùng được cho mọi enum implement `CoNhan` mà không cần biết đó là loại enum nào.
- Tên hằng enum trùng với giá trị trong ràng buộc CHECK của Oracle, nên repository đổi được bằng `TrangThaiSuCo.valueOf(rs.getString("TRANG_THAI"))`.
- Lợi ích so với String: trình biên dịch kiểm tra kiểu, so sánh bằng `==` (`vaiTro == VaiTro.ADMIN`), không gõ nhầm được chuỗi.

### 3.3. Kế thừa: `ThongKeCot extends ThongKeDong`
- `ThongKeDong`: `nhan` + `soLuong`.
- `ThongKeCot` có thêm `phanTram` (độ dài thanh biểu đồ so với giá trị lớn nhất). Constructor gọi `super(nhan, soLuong)`, `toString()` được ghi đè và dùng `getNhan()` của lớp cha (vì field của cha là `private`).
- Lý do tách lớp: Thymeleaf không tính được max trên danh sách đối tượng, nên phần trăm được tính sẵn ở Java.

### 3.4. `Trang<T>` (generic) — phân trang (bổ sung gần đây)
- Dùng chung cho 4 danh sách có phân trang: sự cố (`/admin`), thiết bị, phòng, người dùng. `T` là kiểu của một dòng (`SuCo`, `ThietBi`, `Phong`, `NguoiDung`), nên viết 1 lớp dùng được cho cả 4 — đây là ví dụ **generics** rõ nhất trong dự án.
- Field: `ds` (danh sách của đúng trang này), `so` (trang hiện tại, đếm từ 1), `tong` (tổng số dòng khớp bộ lọc), `duongDan` (`UriComponentsBuilder` giữ các tham số lọc/tìm kiếm đang dùng, để sinh link sang trang khác không mất bộ lọc).
- Các hàm `static` dùng chung: `tinhSoTrang(tong)`, `gioiHan(so, tong)` (ép số trang người dùng gửi lên về khoảng hợp lệ), `viTriDau(so)` (tính OFFSET).
- Repository chỉ lấy đúng 10 dòng (`KICH_THUOC`) của trang đang xem bằng `OFFSET ? ROWS FETCH NEXT ? ROWS ONLY`, và có một hàm `dem(...)` riêng chạy `SELECT COUNT(*)` với cùng điều kiện lọc để `Trang` biết tổng số trang — **không** lấy hết dữ liệu về Java rồi mới cắt trang.

---

## 4. Tầng Util

### 4.1. `BoLuuTru` (lớp trừu tượng) theo mẫu Template Method
```java
public abstract class BoLuuTru {
    public final String luu(MultipartFile file) throws IOException { ... }   // khung thuật toán, không cho override
    protected abstract Map<String, String> duoiHopLe();                     // lớp con tự định nghĩa
    protected abstract String thongBaoLoiLoai();
}
```
Thứ tự xử lý trong `luu()`:
1. File rỗng hoặc null thì trả `null` (ảnh không bắt buộc).
2. Tra `duoiHopLe().get(file.getContentType())`. Không có trong danh sách thì ném `FileKhongHopLeException(thongBaoLoiLoai())`.
3. `Files.createDirectories` tạo thư mục nếu chưa có.
4. Đặt tên file bằng `UUID.randomUUID() + "." + duoi`, để không trùng tên và không dùng tên gốc do người dùng gửi lên (tránh path traversal như `../../`).
5. `try-with-resources` mở `InputStream` rồi `Files.copy`, stream tự đóng.
6. Trả về `"/uploads/<ten>"`, chuỗi này được lưu vào CSDL.

- `final` ở `luu()` để lớp con không đổi được khung xử lý. Constructor là `protected` vì chỉ lớp con mới gọi.
- **`LuuAnhSuCo`**: chỉ nhận ảnh jpg/png/gif/webp, dùng cho `SU_CO.ANH_DINH_KEM`. Dùng `Map.of(...)`, là map bất biến.
- **`LuuTepTraoDoi`**: nhận ảnh, pdf, doc/docx, txt, zip (9 loại), dùng cho tin nhắn trao đổi. Map được dựng bằng khối `static { }` với `HashMap`, sau đó `Map.copyOf` để thành map bất biến.
- Cả 2 lớp con là `@Component`. Controller khai báo đúng kiểu cần dùng (`LuuAnhSuCo` hoặc `LuuTepTraoDoi`) nên Spring inject không bị nhầm.

### 4.2. `FileKhongHopLeException extends RuntimeException`
- Là **unchecked exception** riêng của ứng dụng. Controller bắt riêng nó (để hiện thông báo cho người dùng) và bắt riêng `IOException` (lỗi ghi đĩa).
- Kế thừa `RuntimeException` nên `luu()` không phải khai báo `throws` cho loại lỗi này.

### 4.3. `MatKhau`: băm mật khẩu bằng PBKDF2
- `final class` và constructor `private`: là lớp tiện ích, không cho tạo đối tượng, không cho kế thừa. Chỉ có phương thức `static`.
- `bam(matKhau)`: sinh salt ngẫu nhiên 16 byte bằng `SecureRandom`, chạy PBKDF2-HMAC-SHA256 với 120.000 vòng lặp, ra hash 256 bit. Kết quả lưu dạng `PBKDF2$120000$<salt base64>$<hash base64>`.
- `khop(matKhau, daBam)`: tách chuỗi theo dấu `$`, lấy salt và số vòng, băm lại mật khẩu nhập vào rồi so sánh bằng `MessageDigest.isEqual`. Hàm này **so sánh trong thời gian không đổi**, chống tấn công dò theo thời gian (timing attack). Chuỗi sai định dạng thì trả `false`, không ném lỗi.
- Ý nghĩa: băm một chiều nên không giải ngược được. Salt khiến 2 người cùng mật khẩu vẫn có hash khác nhau, chống rainbow table. Nhiều vòng lặp làm brute-force chậm đi.
- Chỉ dùng thư viện có sẵn trong JDK (`javax.crypto`).

### 4.4. `TimKiem` (bổ sung gần đây) — dựng điều kiện tìm kiếm dùng chung
- `final class`, constructor `private`, chỉ có 2 phương thức `static` (giống khuôn của `MatKhau`).
- `chuanHoa(q)`: `trim()`, trả `null` nếu rỗng (coi là không lọc).
- `dieuKien(q, thamSo, cot...)`: dựng chuỗi `"(cot1 COLLATE BINARY_AI LIKE ? ESCAPE '\\' OR cot2 ... )"`, thêm tham số `%từ khóa%` (đã escape `%`, `_`, `\`) vào `thamSo`. `COLLATE BINARY_AI` là collation của Oracle **so khớp không phân biệt hoa/thường và không phân biệt dấu tiếng Việt** (gõ "may in" vẫn ra "Máy in", "dao tao" ra "Đào tạo").
- Tên cột (`cot...`) do code trong repo truyền vào (hằng số, không phải dữ liệu người dùng) nên nối thẳng vào chuỗi SQL không phải lỗi SQL injection; chỉ giá trị tìm kiếm mới đi qua `?`.
- Dùng trong 4 repo có phân trang, mỗi repo chọn cột khác nhau (`SuCoRepo`: tiêu đề/người báo/thiết bị/phòng/danh mục; `PhongRepo`: tên phòng/tòa nhà/loại phòng...).

### 4.5. `ExcelSuCo`, `ExcelThietBi` (bổ sung gần đây) — xuất/nhập Excel bằng Apache POI
- Thêm dependency `poi-ooxml` vào `pom.xml`.
- **`ExcelSuCo.ghi(ds, out)`** (xuất danh sách sự cố đang lọc ở `/admin/xuat-excel`): dùng `SXSSFWorkbook` (streaming — chỉ giữ một số dòng gần nhất trong RAM, ghi dần ra đĩa/response) nên xuất được danh sách dài mà không tốn nhiều bộ nhớ, khác với `XSSFWorkbook` thường giữ cả workbook trong RAM. Mọi ô chữ ghi bằng `setCellValue(String)` (không phải công thức), nên nội dung người dùng nhập dạng `=1+1`/`=HYPERLINK(...)` không bị Excel chạy thành công thức khi mở file (chống CSV/Excel injection).
- **`ExcelThietBi`** (nhập thiết bị hàng loạt ở `/admin/thiet-bi`):
  - `ghiMau(...)`: xuất file mẫu dùng `XSSFWorkbook` thường (file nhỏ, không cần streaming), có cột **danh sách thả xuống** (`DataValidation`) cho Loại/Phòng/Tình trạng, lấy nguồn từ 1 sheet ẩn (`"DanhSach"`) vì Excel giới hạn công thức validation trực tiếp ở 255 ký tự.
  - `doc(...)`: đọc file người dùng tải lên, kiểm tra từng dòng (thiếu dữ liệu, sai định dạng ngày, trùng loại/phòng/tên thiết bị — cả với dữ liệu đã có trong CSDL và trùng ngay trong file), trả về `KetQua` gồm danh sách dòng hợp lệ + danh sách lỗi. Controller **chỉ ghi CSDL khi không còn dòng lỗi nào** (nhập cả file hoặc không nhập gì, tương tự tính chất "tất cả hoặc không" của transaction, nhưng ở đây là kiểm tra dữ liệu trước khi chạm CSDL, không phải rollback).
  - Dùng `DataFormatter` để đọc ô dạng chữ bất kể Excel lưu là số/ngày/chuỗi, và chuẩn hóa Unicode (`Normalizer.Form.NFC`) trước khi so khớp vì Excel/Mac có thể lưu tiếng Việt ở dạng tổ hợp dấu khác máy khác.

---

## 5. Tầng Repository (JdbcTemplate)

Khuôn chung của mỗi repo:
- `@Repository`, nhận `JdbcTemplate` qua constructor.
- Hằng `SELECT` dùng chung, và `RowMapper<T> MAPPER` viết bằng **lambda** `(rs, i) -> new T(...)`. `RowMapper` là functional interface.
- Tìm theo mã trả về `Optional<T>`: `jdbc.query(...).stream().findFirst()`, không trả `null`.
- **Mọi tham số đều truyền qua dấu `?`** (PreparedStatement), chống SQL injection.

| Repo | Điểm đáng chú ý |
|---|---|
| `NguoiDungRepo` | `dangNhap` lấy dòng theo `LOWER(EMAIL) = ?` và `TRANG_THAI = 'HOAT_DONG'`, sau đó kiểm tra mật khẩu **ở Java** bằng `MatKhau.khop` (không so sánh trong SQL). SELECT chung không lấy cột `MAT_KHAU`. `taoMoi`/`capNhat` băm mật khẩu trước khi lưu; `capNhat` để trống mật khẩu thì giữ nguyên mật khẩu cũ. **Xóa mềm** `voHieuHoa` (UPDATE trạng thái, ghi `NGAY_XOA`, `MA_NGUOI_XOA`) và `kichHoatLai`. `timTrang`/`dem` phân trang + tìm kiếm cho `/admin/nguoi-dung` (bổ sung gần đây). |
| `SuCoRepo` | Đọc từ **view** `V_SU_CO_CHI_TIET` (đã JOIN sẵn). `taoMoi`/`capNhat` truyền thêm mảng `int[] Types` để Oracle biết kiểu khi tham số là `null`. `capNhat` gán `MA_ADMIN_XU_LY` để trigger biết ai cập nhật. `xoa` có **`@Transactional`**: xóa các bảng con `BINH_LUAN_SU_CO`, `PHAN_HOI`, `LICH_SU_XU_LY` trước, rồi mới xóa `SU_CO`. `demTheoTrangThai` dùng `ResultSetExtractor` gom kết quả thành `LinkedHashMap`. `timSuCoHayGap` dùng `FETCH FIRST 5 ROWS ONLY`. `timTrang`/`dem` phân trang + tìm kiếm cho `/admin` (xem mục 4.4, 9.H). **`capNhat` (bổ sung gần đây)** cũng `@Transactional`: `SELECT ... FOR UPDATE` khóa dòng sự cố trước khi đọc giá trị cũ (chống 2 admin ghi đè nhau), rồi UPDATE, rồi tự ghi 1 dòng `LICH_SU_XU_LY` cho **mọi** lần lưu có thay đổi (không chỉ khi đổi trạng thái như trước) — xem mục 9.H. |
| `KinhNghiemRepo` | `taoMoi` dùng `KeyHolder` + `con.prepareStatement(sql, new String[]{"MA_BAI"})` để **lấy khóa tự sinh** (cột identity của Oracle) ngay sau khi INSERT. Giá trị này dùng để gắn bài mới vào sự cố. |
| `ThietBiRepo` | Đổi `LocalDate` sang `java.sql.Date.valueOf(...)` khi lưu, và `toLocalDate()` khi đọc. `timTrang`/`dem` phân trang + tìm kiếm cho `/admin/thiet-bi`. `nhanPhong()`/`khoaDaCo()` phục vụ nhập Excel (tra mã phòng theo tên, và khóa trùng của thiết bị đã có — xem mục 4.5). |
| `PhongRepo` | `rs.getObject("TANG", Integer.class)` để đọc được giá trị null (`getInt` sẽ trả 0). `timTrang`/`dem` phân trang + tìm kiếm. `tonTaiTrung(...)` kiểm tra trùng tên phòng (cùng tòa, cùng tầng, không phân biệt hoa thường) trước khi thêm/sửa, tầng để trống cũng được coi là "cùng tầng". |
| `PhanHoiRepo` | Mỗi sự cố chỉ có 1 phản hồi, do ràng buộc `UQ_PHAN_HOI_SU_CO` của Oracle đảm bảo. |
| `BinhLuanRepo` | JOIN `NGUOI_DUNG` để lấy tên và vai trò người gửi. |
| `ThongKeRepo` | Chỉ đọc: `COUNT`, `AVG`, `GROUP BY`, `CASE`, `DECODE`, `queryForObject(..., Integer.class)`. `theoThang()` tạo trước 6 tháng gần nhất với giá trị 0 (`YearMonth`), rồi ghi đè bằng kết quả SQL, để tháng không có sự cố vẫn hiện trên biểu đồ. `gioXuLyTrungBinh`: trong Oracle, `DATE - DATE` ra số ngày, nhân 24 thành giờ. |

**Xử lý lỗi ràng buộc:** repo không tự bắt lỗi. Spring dịch lỗi Oracle thành exception của Spring:
- Trùng UNIQUE (ORA-00001) → `DuplicateKeyException`
- Vi phạm khóa ngoại khi xóa (ORA-02292) → `DataIntegrityViolationException`

Controller bắt các exception này và hiện thông báo tiếng Việt cho người dùng.

---

## 6. Tầng Controller

Đặc điểm chung:
- `@Controller` (không phải `@RestController`): phương thức trả về **tên template**, hoặc chuỗi `"redirect:/..."`.
- `@GetMapping` dùng để hiển thị trang, `@PostMapping` dùng để thay đổi dữ liệu. Xóa cũng dùng POST.
- `@RequestParam` đọc tham số form hoặc query (`required = false` thì có thể vắng). `@PathVariable` đọc giá trị trong URL (`/admin/{maSc}`).
- Truyền dữ liệu sang view qua `Model.addAttribute`.
- **Mẫu PRG (Post/Redirect/Get):** sau POST luôn redirect, thông báo được truyền qua `RedirectAttributes.addFlashAttribute("thongBao"/"loi", ...)`. Flash attribute chỉ tồn tại qua đúng 1 lần redirect, nên bấm F5 không bị gửi lại form.
- **Kiểm tra dữ liệu bằng danh sách trắng:** `Set.of("MOI","DANG_XU_LY","HOAN_THANH")`, `Map` các loại thiết bị/phòng/vai trò (`LinkedHashMap` để giữ thứ tự hiện trong dropdown).
- Người đang đăng nhập được lấy bằng `(NguoiDung) session.getAttribute("nguoiDung")`.

| Controller | URL | Chức năng |
|---|---|---|
| `AuthController` | `/`, `GET/POST /login`, `/logout` | Đăng nhập thành công thì lưu `NguoiDung` vào session, rồi điều hướng theo vai trò (ADMIN tới `/admin`, USER tới `/user`). Đăng xuất gọi `session.invalidate()`. Đã đăng nhập mà vào `/login` thì được chuyển thẳng tới trang của mình. |
| `SuCoController` (USER) | `/user`, `/user/{maSc}`, `/user/bao-cao`, `/user/{maSc}/binh-luan`, `/user/{maSc}/phan-hoi` | Báo sự cố (kèm ảnh), xem sự cố của mình, nhắn tin trao đổi (kèm file), gửi phản hồi 1–5 sao. **Kiểm tra quyền sở hữu:** `.filter(sc -> sc.getMaNguoiBao().equals(nd.getMaNd()))`, không cho xem hoặc sửa sự cố của người khác bằng cách đổi số trên URL. Phản hồi chỉ gửi được khi `daHoanThanh()`, điểm phải từ 1 đến 5, và bắt `DuplicateKeyException` nếu gửi lần 2. **(Bổ sung gần đây)** thêm `GET /user/{maSc}/binh-luan?sauMa=` (`@ResponseBody`, cùng đường dẫn với POST nhưng khác HTTP method) để trang chi tiết tự lấy tin nhắn mới — xem mục 9.I. |
| `AdminController` | `/admin`, `/admin/xuat-excel`, `/admin/moi`, `/admin/{maSc}`, `POST /admin/{maSc}`, `/xoa`, `/binh-luan` | Danh sách sự cố **có phân trang + lọc theo trạng thái + tìm kiếm** (`?trang=&trangThai=&q=`, dùng `Trang<SuCo>` + `TimKiem`), kèm ô đếm số lượng; **xuất Excel** toàn bộ sự cố đang khớp bộ lọc (không chỉ trang đang xem) qua `ExcelSuCo`; trang chi tiết (lịch sử, phản hồi, trao đổi); cập nhật trạng thái, nguyên nhân, cách khắc phục, có thêm ô `ghiChu` tự do; **lưu vào kho kinh nghiệm**: nếu tick `luuKho` mà chưa chọn bài có sẵn thì tạo bài mới (`kinhNghiemRepo.taoMoi` trả về mã) rồi gắn mã đó vào sự cố. **(Bổ sung gần đây)** `GET /admin/{maSc}/binh-luan?sauMa=` (như trên) và `GET /admin/moi?trangThai=&sauMa=` (`@ResponseBody`) để `/admin` tự hiện sự cố mới báo lên đầu bảng — xem mục 9.I. |
| `KinhNghiemController` | `/kinh-nghiem/**` (cả 2 vai trò xem), `/admin/kinh-nghiem/**` (chỉ admin sửa) | Thêm, sửa, xóa bài. Xóa bài đang được sự cố tham chiếu thì báo lỗi. |
| `NguoiDungController` | `/admin/nguoi-dung/**` | Danh sách **có phân trang + tìm kiếm**. Thêm, sửa, vô hiệu hóa, kích hoạt lại. Không cho **admin tự hạ quyền mình** và không cho **tự vô hiệu hóa chính mình**. Sửa thông tin của chính mình thì **cập nhật lại session**. Email được `trim().toLowerCase()`. |
| `ThietBiController` | `/admin/thiet-bi`, `/mau-nhap`, `/nhap` | Danh sách **có phân trang + tìm kiếm**. Thêm, sửa, xóa (bắt lỗi khóa ngoại, trùng tên). `@RequestParam LocalDate ngayLapDat`: Spring tự đổi chuỗi `yyyy-MM-dd` từ `<input type="date">` sang `LocalDate`. **Nhập Excel hàng loạt** (bổ sung gần đây): `GET /mau-nhap` tải file mẫu (`ExcelThietBi.ghiMau`); `POST /nhap` đọc file người dùng tải lên (`ExcelThietBi.doc`), chỉ `INSERT` khi không còn dòng lỗi nào, báo lại danh sách lỗi nếu có. |
| `PhongController`, `DanhMucController` | `/admin/phong`, `/admin/danh-muc` | `PhongController`: danh sách có phân trang + tìm kiếm; thêm/sửa gọi `PhongRepo.tonTaiTrung` để báo trùng phòng trước khi đụng ràng buộc UNIQUE của Oracle. Cả hai bắt lỗi khóa ngoại (còn thiết bị, còn sự cố...) khi xóa. |
| `ThongKeController` | `/admin/thong-ke` | Gom số liệu, gọi `bieuDo()` để đổi `List<ThongKeDong>` thành `List<ThongKeCot>` bằng **Stream API** (`mapToInt().max()`, `map`, `toList()`). `hienThiGio()` đổi số giờ thập phân thành chuỗi kiểu "2 ngày 5 giờ". `getOrDefault` dùng cho trạng thái chưa có sự cố nào. Biểu đồ theo tháng nay lấy **12 tháng** gần nhất (trước là 6). |
| `XuLyLoiChung` (bổ sung gần đây) | áp dụng cho **mọi** controller | `@ControllerAdvice` + `@ExceptionHandler` đầu tiên của dự án (trước đây không có xử lý lỗi toàn cục). Bắt `MethodArgumentTypeMismatchException`/`MissingServletRequestParameterException` (tham số sai kiểu hoặc thiếu) và `MaxUploadSizeExceededException` (file tải lên vượt 5MB): quay lại trang trước (`Referer`, chỉ lấy phần path để không bị redirect sang domain khác) kèm thông báo lỗi, thay vì hiện trang lỗi 400/500 mặc định của Spring. |

---

## 7. Luồng nghiệp vụ chính

**Đăng nhập:** POST `/login` → `NguoiDungRepo.dangNhap` (lấy dòng theo email, kiểm tra PBKDF2) → `session.setAttribute("nguoiDung", nd)` → redirect theo vai trò. Ở mọi request sau, `AuthInterceptor` đọc session để kiểm tra.

**Báo sự cố:** form `multipart/form-data` → `SuCoController.baoSuCo` → kiểm tra tiêu đề và mức ưu tiên → `LuuAnhSuCo.luu(anh)` (có thể ném `FileKhongHopLeException`) → `SuCoRepo.taoMoi` (Oracle gán mặc định `MOI` và `SYSDATE`) → redirect kèm flash.

**Admin xử lý:** POST `/admin/{maSc}` → kiểm tra trạng thái → (tùy chọn) tạo bài kinh nghiệm và lấy khóa vừa sinh → `SuCoRepo.capNhat` (gán `MA_ADMIN_XU_LY`) → **trigger Oracle** `TRG_SU_CO_LICH_SU` ghi `LICH_SU_XU_LY`, `TRG_SU_CO_HOAN_THANH` gán `NGAY_HOAN_THANH`. **(Bổ sung gần đây)** `capNhat` tự `@Transactional`: `SELECT ... FOR UPDATE` khóa dòng sự cố trước khi đọc nguyên nhân/cách khắc phục cũ, UPDATE, rồi tự ghi/sửa dòng `LICH_SU_XU_LY` để **mọi** lần lưu (đổi trạng thái hay không) đều có vết — trước đây chỉ có dòng lịch sử khi trigger thấy trạng thái đổi, nên lưu lần 2 cùng trạng thái sẽ ghi đè nguyên nhân/cách khắc phục cũ mà không còn dấu vết gì.

**Phản hồi:** chỉ người báo, chỉ khi sự cố đã hoàn thành, chỉ 1 lần (Java kiểm tra, ràng buộc UNIQUE ở Oracle chặn thêm một lớp).

---

## 8. Điểm yếu nên biết trước (giáo viên có thể hỏi vặn)

Những điểm sau mình đọc ra từ code, chưa chạy thử. Nên biết để chủ động trả lời "em biết hạn chế này, hướng cải tiến là...":

1. **(Đã sửa) Xóa sự cố từng bị lỗi 500.** Trước đây `SuCoRepo.xoa` chỉ xóa `LICH_SU_XU_LY` rồi xóa `SU_CO`. Nhưng `PHAN_HOI` và `BINH_LUAN_SU_CO` cũng có khóa ngoại tới `SU_CO` và không có `ON DELETE CASCADE` (`sql/02_tables.sql:142`, `:157`), nên Oracle báo ORA-02292. Bây giờ hàm xóa lần lượt `BINH_LUAN_SU_CO`, `PHAN_HOI`, `LICH_SU_XU_LY`, cuối cùng mới xóa `SU_CO`, tất cả trong một `@Transactional`. Còn một hạn chế nhỏ: file đính kèm trong `uploads/` không bị xóa theo.
2. **Tạo bài kinh nghiệm và cập nhật sự cố không nằm chung transaction** (`AdminController.capNhat` gọi `kinhNghiemRepo.taoMoi` rồi mới gọi `suCoRepo.capNhat`, mỗi repo tự `@Transactional` riêng). Nếu UPDATE sự cố lỗi thì bài mới tạo vẫn còn trong kho. (Vẫn còn hạn chế này, dù `SuCoRepo.capNhat` bản thân nó đã được bọc transaction cho các bước bên trong nó — khóa dòng, UPDATE, ghi lịch sử — xem mục 9.D.59.)
3. **Không có chống CSRF** (vì không dùng Spring Security). Trang web khác có thể dựng form POST tới `/admin/.../xoa` khi admin đang đăng nhập.
4. **Session không đổi ID sau khi đăng nhập** (session fixation). Cách sửa: `invalidate()` session cũ rồi tạo session mới trước khi `setAttribute`.
5. **Người dùng bị vô hiệu hóa vẫn dùng được cho tới khi hết session**, vì interceptor chỉ đọc session, không kiểm tra lại CSDL.
6. **Kiểm tra loại file dựa vào `Content-Type` do trình duyệt gửi lên**, có thể bị giả. Kiểm tra chắc hơn là đọc vài byte đầu file (magic bytes).
7. **Enum có nhưng chưa dùng triệt để:** controller và repo vẫn nhận trạng thái, vai trò dạng `String`, kiểm tra bằng `Set`/`Map` riêng (`NguoiDungController.VAI_TRO` lặp lại nhãn đã có trong `VaiTro`). Hướng cải tiến: `@RequestParam TrangThaiSuCo trangThai`, để Spring tự đổi sang enum và báo lỗi nếu sai.
8. **Không có tầng Service** nên logic nghiệp vụ nằm trong controller. Không có unit test.
9. Mật khẩu CSDL ghi thẳng trong `application.properties`. Nên chuyển sang biến môi trường.
10. **(Bổ sung gần đây)** Polling AJAX (mục 9.I) không phải real-time thật: client tự hỏi lại theo chu kỳ cố định (4-5 giây) nên có độ trễ tối đa 1 chu kỳ, và mỗi trang mở sẽ tự gửi request riêng theo chu kỳ đó — không tối ưu bằng WebSocket/SSE (server tự đẩy dữ liệu, không cần hỏi lại) nếu có nhiều người dùng cùng lúc. Chọn polling vì đơn giản, không thêm dependency, phù hợp quy mô bài tập lớn.

---

## 9. Bộ câu hỏi ôn tập (có gợi ý trả lời)

### A. Tổng quan và kiến trúc
1. **Dự án dùng mô hình gì? Mỗi tầng làm gì?**
   MVC phân tầng. Controller nhận request; Repository làm việc với CSDL; Model là đối tượng dữ liệu; View là Thymeleaf. Thêm tầng config (interceptor) và util.
2. **Vì sao dùng JdbcTemplate mà không dùng JPA/Hibernate?**
   Môn học về Oracle nên cần viết SQL tường minh, dùng được view, trigger, `FETCH FIRST`, `DECODE`. Kiểm soát được câu lệnh. JdbcTemplate tự lo mở/đóng kết nối và dịch lỗi SQL thành exception của Spring.
3. **`@SpringBootApplication` gồm những gì?**
   `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`.
4. **Dependency Injection trong dự án làm thế nào?**
   Qua constructor (ví dụ `AdminController(SuCoRepo, ...)`). Spring tạo bean rồi truyền vào. Field để `final`, dễ test, không cần `@Autowired` vì lớp chỉ có 1 constructor.
5. **`@Controller` khác `@RestController` thế nào?**
   `@Controller` trả về tên view để render HTML. `@RestController` = `@Controller` + `@ResponseBody`, trả JSON/dữ liệu.
6. **`@Component`, `@Repository`, `@Controller`, `@Configuration` khác nhau ra sao?**
   Đều là stereotype để Spring quét thành bean. `@Repository` có thêm dịch exception của tầng dữ liệu. `@Configuration` chứa cấu hình. `@Controller` dùng cho tầng web.
7. **Bean mặc định có phạm vi (scope) gì? Có vấn đề gì với đa luồng không?**
   Singleton. Các bean ở đây không lưu trạng thái riêng cho từng request (chỉ có field `final` trỏ tới repo/JdbcTemplate) nên dùng chung giữa các luồng vẫn an toàn.

### B. OOP (giáo viên hay hỏi nhất)
8. **Chỉ ra 4 tính chất OOP trong code.**
   - Đóng gói: field `private` + getter/setter trong mọi model.
   - Kế thừa: `ThongKeCot extends ThongKeDong`; `LuuAnhSuCo`/`LuuTepTraoDoi extends BoLuuTru`; `FileKhongHopLeException extends RuntimeException`.
   - Đa hình: 5 enum cùng implement `CoNhan`, fragment Thymeleaf gọi `getNhan()` mà không cần biết là enum nào; `BoLuuTru.luu()` gọi `duoiHopLe()` và mỗi lớp con trả về danh sách riêng (đa hình lúc chạy, nhờ override).
   - Trừu tượng: `abstract class BoLuuTru`, `interface CoNhan`.
9. **Abstract class khác interface thế nào? Vì sao `BoLuuTru` là abstract class còn `CoNhan` là interface?**
   `BoLuuTru` có trạng thái (`thuMuc`), có constructor và có code dùng chung (`luu`), nên cần abstract class. `CoNhan` chỉ là "hợp đồng" gồm 2 phương thức, và enum không kế thừa được class (enum ngầm định đã `extends Enum`), chỉ implement được interface.
10. **Vì sao `luu()` khai báo `final`?**
    Để giữ cố định khung xử lý (kiểm tra loại, đặt tên UUID, ghi file). Lớp con chỉ được thay phần "loại file nào được phép". Đây là mẫu Template Method.
11. **Vì sao constructor của `BoLuuTru` là `protected`?**
    Lớp trừu tượng không tạo đối tượng trực tiếp được, chỉ lớp con gọi `super(...)`.
12. **Enum trong Java khác hằng số `static final String` ở điểm nào?**
    Enum an toàn kiểu, có field/constructor/phương thức, implement được interface, dùng được trong `switch`, so sánh bằng `==`, có sẵn `valueOf()` và `values()`.
13. **`TrangThaiSuCo.valueOf("ABC")` sẽ ra sao?**
    Ném `IllegalArgumentException`. Trong dự án không xảy ra vì ràng buộc CHECK ở Oracle chỉ cho phép đúng các giá trị đó.
14. **Vì sao chuyển từ `record` sang class thường?**
    Để thể hiện rõ đóng gói (getter/setter), constructor, override `equals`/`hashCode`/`toString`. Record là bất biến và tự sinh các phương thức này, khó giải thích. Đổi lại class dài hơn và có thể thay đổi giá trị (mutable).
15. **Vì sao `equals`/`hashCode` chỉ dùng khóa chính? Override `equals` mà không override `hashCode` thì sao?**
    Thực thể được xác định bằng mã. Nếu chỉ override `equals`, hai đối tượng bằng nhau có thể có hashCode khác nhau, làm `HashSet`/`HashMap` hoạt động sai.
16. **`o instanceof SuCo that` là cú pháp gì?**
    Pattern matching for instanceof (Java 16+): kiểm tra kiểu và ép kiểu trong 1 bước.
17. **`ThongKeCot.toString()` vì sao gọi `getNhan()` mà không dùng `nhan`?**
    `nhan` là `private` ở lớp cha nên lớp con không truy cập trực tiếp được.
18. **Overloading và overriding: chỉ ví dụ trong code.**
    Overloading: 2 constructor (rỗng và đầy đủ) của mỗi model. Overriding: `toString`, `equals`, `hashCode`, `getNhan`, `duoiHopLe`, `preHandle`, `addInterceptors`.
19. **Vì sao `MatKhau` là `final` và có constructor `private`?**
    Là lớp tiện ích chỉ có phương thức static, không cần tạo đối tượng và không cho kế thừa để sửa cách băm.
20. **`Long` khác `long` thế nào? Khi nào dùng loại nào?**
    `Long` là kiểu bọc, nhận được `null` (thiết bị không bắt buộc: `maTb`; tầng không có: `tang`). `long`/`int` không null được. Để ý `getLong()` của JDBC trả 0 khi cột null, nên code dùng `rs.getObject(col, Long.class)`.

### C. Exception
21. **Checked và unchecked exception khác nhau thế nào? `FileKhongHopLeException` thuộc loại nào, vì sao?**
    Unchecked (kế thừa `RuntimeException`), không bắt buộc khai báo `throws`. `IOException` là checked nên `luu()` phải khai báo `throws IOException`.
22. **Vì sao bắt `FileKhongHopLeException` trước, `IOException` sau?**
    Hai lỗi có nguyên nhân khác nhau nên thông báo khác nhau: lỗi do người dùng chọn sai loại file, và lỗi hệ thống khi ghi đĩa. Hai lớp này không có quan hệ cha con nên thứ tự catch không bắt buộc, nhưng tách riêng để thông báo rõ ràng.
23. **`try-with-resources` dùng ở đâu, có tác dụng gì?**
    `BoLuuTru.luu` với `InputStream`: stream tự `close()` kể cả khi có exception (yêu cầu đối tượng implement `AutoCloseable`).
24. **`DuplicateKeyException` và `DataIntegrityViolationException` từ đâu ra?**
    Spring dịch mã lỗi Oracle (ORA-00001, ORA-02292) thành các exception này. Controller bắt để hiện thông báo tiếng Việt thay vì trang lỗi SQL.
25. **Có xử lý lỗi toàn cục (`@ControllerAdvice`) không?**
    **(Cập nhật)** Có một phần: `XuLyLoiChung` (`@ControllerAdvice`) bắt lỗi tham số sai kiểu/thiếu (`MethodArgumentTypeMismatchException`, `MissingServletRequestParameterException`) và file tải lên quá lớn (`MaxUploadSizeExceededException`) cho **mọi** controller, quay lại trang trước kèm thông báo. Còn lại — lỗi nghiệp vụ như `DuplicateKeyException`, `DataIntegrityViolationException`, `FileKhongHopLeException` — vẫn được bắt **cục bộ trong từng controller** (để trả thông báo khác nhau theo từng tình huống), và lỗi nào không ai bắt thì vẫn ra trang lỗi mặc định của Spring.

### D. Truy cập dữ liệu và Oracle
26. **Làm sao chống SQL injection?**
    Mọi giá trị đều truyền qua `?`, JdbcTemplate dùng PreparedStatement. Không nối chuỗi dữ liệu người dùng vào SQL.
27. **`RowMapper` là gì? Vì sao viết được bằng lambda?**
    Là functional interface có 1 phương thức `mapRow(ResultSet, int)`, đổi 1 dòng kết quả thành 1 đối tượng.
28. **`RowMapper` khác `ResultSetExtractor` thế nào?**
    `RowMapper` xử lý từng dòng và trả `List`. `ResultSetExtractor` nhận cả `ResultSet` và tự duyệt `while (rs.next())`, dùng khi cần gom kết quả thành `Map` (`demTheoTrangThai`, `theoThang`).
29. **Vì sao `timTheoMa` trả `Optional`?**
    Để bắt nơi gọi phải xử lý trường hợp không tìm thấy (`orElse`, `isEmpty`, `filter`, `ifPresent`), tránh `NullPointerException`.
30. **`@Transactional` trong `SuCoRepo.xoa` có tác dụng gì? Bỏ đi thì sao?**
    4 lệnh DELETE (trao đổi, phản hồi, lịch sử, sự cố) cùng commit hoặc cùng rollback. Bỏ đi thì nếu lệnh cuối lỗi, dữ liệu con đã bị xóa mất trong khi sự cố vẫn còn. Thứ tự phải là xóa bảng con trước, bảng cha sau, vì khóa ngoại không có `ON DELETE CASCADE`.
31. **`@Transactional` hoạt động thế nào? Gọi phương thức đó từ bên trong cùng lớp thì sao?**
    Spring bọc bean bằng proxy, mở transaction trước khi gọi và commit/rollback sau. Gọi nội bộ (`this.xoa()`) không đi qua proxy nên mất transaction. Ở đây controller gọi repo từ bên ngoài nên không gặp vấn đề. Mặc định chỉ rollback khi có `RuntimeException`.
32. **Lấy khóa vừa sinh sau INSERT thế nào?**
    `KeyHolder` + `prepareStatement(sql, new String[]{"MA_BAI"})`. Oracle phải chỉ rõ tên cột, vì nếu không driver trả về ROWID.
33. **Vì sao có chỗ truyền thêm mảng `int[] Types`?**
    Khi tham số là `null` (ví dụ `maTb`, `anhDinhKem`), Oracle JDBC cần biết kiểu SQL để gọi `setNull`. Thiếu thông tin kiểu có thể gây lỗi "invalid column type".
34. **Việc gì do Java làm, việc gì giao cho Oracle?**
    Oracle: giá trị mặc định (`MOI`, `SYSDATE`), trigger ghi lịch sử và ngày hoàn thành, ràng buộc UNIQUE/CHECK/FK, view JOIN sẵn. Java: kiểm tra dữ liệu, phân quyền, băm mật khẩu, lưu file, điều hướng trang.
35. **Vì sao đăng nhập không so mật khẩu trong câu WHERE?**
    Mật khẩu đã băm kèm salt riêng cho từng người, nên phải lấy chuỗi băm (có salt) lên trước, rồi băm lại mật khẩu nhập vào bằng đúng salt đó và so sánh.
36. **Xóa mềm là gì? Vì sao người dùng xóa mềm, còn thiết bị thì xóa hẳn?**
    Xóa mềm là UPDATE trạng thái thành `KHONG_HOAT_DONG` thay vì DELETE. Người dùng gắn với rất nhiều dữ liệu (sự cố, bài viết, lịch sử) cần giữ lại và có thể kích hoạt lại. Thiết bị xóa hẳn được, nhưng FK sẽ chặn nếu thiết bị đã có sự cố.
37. **`HikariCP` là gì?**
    Connection pool: giữ sẵn một số kết nối để dùng lại, không phải mở kết nối mới cho mỗi request.

### E. Web, session và bảo mật
38. **Đăng nhập lưu ở đâu? Session hoạt động thế nào?**
    `HttpSession` ở server. Trình duyệt giữ cookie `JSESSIONID` để server nhận ra session. Đăng xuất gọi `invalidate()`.
39. **Vì sao `NguoiDung` không có field mật khẩu?**
    Để mật khẩu (kể cả bản băm) không nằm trong session và không lộ ra giao diện.
40. **Interceptor khác Filter thế nào?**
    Filter thuộc Servlet, chạy trước DispatcherServlet. Interceptor thuộc Spring MVC, chạy sau khi đã xác định controller, có `preHandle`/`postHandle`/`afterCompletion`.
41. **`getSession(false)` khác `getSession()` thế nào?**
    `false` thì không tạo session mới nếu chưa có, tránh tạo session thừa cho người chưa đăng nhập.
42. **User đổi URL thành `/user/5` (sự cố của người khác) thì sao? Vào `/admin` thì sao?**
    `filter(sc -> sc.getMaNguoiBao().equals(...))` cho kết quả rỗng nên bị redirect về `/user`. Vào `/admin` thì bị interceptor chuyển về `/user`.
43. **Vì sao dùng `.equals()` để so sánh `Long` mà không dùng `==`?**
    `==` so sánh tham chiếu. `Long` chỉ được cache trong khoảng -128..127, ngoài khoảng đó `==` có thể trả `false` dù giá trị bằng nhau.
44. **Mẫu PRG và flash attribute là gì?**
    Sau POST thì redirect sang GET, tránh gửi lại form khi bấm F5. Flash attribute được lưu tạm (trong session) và chỉ tồn tại qua 1 lần redirect.
45. **Băm mật khẩu ra sao? Salt để làm gì? Vì sao không dùng MD5/SHA-256 trực tiếp?**
    PBKDF2-HMAC-SHA256, 120.000 vòng, salt 16 byte. MD5/SHA tính quá nhanh nên brute-force dễ. PBKDF2 cố ý làm chậm, salt chống rainbow table.
46. **`MessageDigest.isEqual` khác `Arrays.equals` thế nào?**
    `isEqual` so sánh trong thời gian không đổi, không dừng sớm ở byte khác đầu tiên, nên chống được timing attack.
47. **Upload file an toàn thế nào?**
    Danh sách trắng content-type, đặt tên bằng UUID (không dùng tên gốc, tránh `../`), giới hạn 5MB, lưu ngoài classpath. Hạn chế: content-type có thể bị giả (xem mục 8).
48. **Ứng dụng còn lỗ hổng gì?**
    Thiếu CSRF, session fixation, session không kiểm tra lại trạng thái tài khoản, mật khẩu CSDL nằm trong file cấu hình (mục 8).

### F. Java hiện đại và Collections
49. **Stream API dùng ở đâu?**
    `ThongKeController.bieuDo` (`mapToInt().max().orElse(0)`, `map`, `toList()`), tính tổng `values().stream().mapToInt(Integer::intValue).sum()`, `jdbc.query(...).stream().findFirst()`, `filter(Objects::nonNull)` khi đăng nhập.
50. **Method reference là gì? Cho ví dụ.**
    `ThongKeDong::getSoLuong`, `Integer::intValue`, `java.util.Objects::nonNull`: cách viết gọn của lambda chỉ gọi 1 phương thức.
51. **Vì sao dùng `LinkedHashMap` mà không dùng `HashMap`?**
    `LinkedHashMap` giữ thứ tự thêm vào, dùng cho dropdown và cho dữ liệu 6 tháng theo đúng thứ tự thời gian.
52. **`Map.of` và `Map.copyOf` khác `new HashMap` thế nào?**
    Tạo map bất biến (thêm/xóa sẽ ném `UnsupportedOperationException`). `Map.of` nhận tối đa 10 cặp. `LuuTepTraoDoi` có 9 cặp, vẫn dùng được `Map.of`, nhưng code chọn dựng bằng `HashMap` trong khối `static` cho dễ đọc rồi `Map.copyOf` để khóa lại. Nếu bị hỏi thì trả lời thẳng như vậy, hoặc nói thêm: quá 10 cặp thì dùng `Map.ofEntries`.
53. **Khối `static { }` dùng để làm gì?**
    Chạy 1 lần khi lớp được nạp, để khởi tạo các hằng `static` phức tạp (`VAI_TRO`, `LOAI`, `DUOI_TEP`).
54. **`LocalDateTime` khác `java.util.Date`/`java.sql.Timestamp` thế nào?**
    `java.time` là API mới, bất biến, an toàn khi dùng nhiều luồng. JDBC trả `Timestamp`/`Date`, repo đổi bằng `toLocalDateTime()`/`toLocalDate()`, và có kiểm tra null.
55. **`var` là gì?** (`var bai = kinhNghiemRepo.timTheoMa(...)`)
    Suy luận kiểu biến cục bộ (Java 10+). Kiểu vẫn được xác định lúc biên dịch, ở đây là `Optional<BaiKinhNghiem>`.
56. **`YearMonth` dùng để làm gì?**
    Biểu diễn tháng/năm, cộng trừ tháng dễ dàng (`minusMonths`, `plusMonths`), `parse("2026-10")`.

### G. Câu hỏi "nếu... thì sao", "cải tiến thế nào"
57. **Muốn thêm trạng thái `TAM_DUNG` thì sửa những đâu?**
    Thêm hằng vào enum `TrangThaiSuCo` (nhãn, màu badge); sửa `Set TRANG_THAI` trong `AdminController`; sửa ràng buộc CHECK của Oracle; thêm lựa chọn trong template; cập nhật thống kê. Nhờ `CoNhan`, phần hiển thị badge không cần sửa.
58. **Muốn thêm loại lưu file mới (ví dụ chỉ nhận PDF cho bài kinh nghiệm)?**
    Tạo `LuuPdfKinhNghiem extends BoLuuTru`, override 2 phương thức và đánh dấu `@Component`. Không phải sửa `BoLuuTru`. Đây là nguyên lý Open/Closed.
59. **Hai admin cùng sửa một sự cố một lúc thì sao?**
    **(Cập nhật)** `SuCoRepo.capNhat` giờ chạy `SELECT ... FOR UPDATE` trong `@Transactional` trước khi đọc giá trị cũ: đây là **khóa bi quan (pessimistic locking)** ở mức dòng (row-level lock) của Oracle. Admin thứ 2 lưu cùng sự cố sẽ phải **chờ** transaction của admin thứ 1 commit/rollback xong mới đọc và ghi tiếp, nên không còn đọc nhầm giá trị cũ rồi ghi đè dựa trên dữ liệu đã lỗi thời (so với trước đây là "ai lưu sau thắng" mà không khóa gì cả). Hướng khác là khóa lạc quan (thêm cột version, so sánh trước khi UPDATE) — không cần khóa suốt thời gian giao dịch nhưng phải tự xử lý khi phát hiện xung đột.
60. **Muốn viết test thì test gì?**
    `MatKhau` (băm rồi kiểm tra khớp, chuỗi sai định dạng), `BoLuuTru` (sai loại file thì ném exception), `ThongKeController.hienThiGio`/`bieuDo` (danh sách rỗng, max = 0), controller dùng MockMvc.
61. **Muốn đổi sang Spring Security thì thay những gì?**
    Bỏ `AuthInterceptor`, cấu hình `SecurityFilterChain`, dùng `UserDetailsService` đọc `NGUOI_DUNG`, `PasswordEncoder` (có sẵn `Pbkdf2PasswordEncoder`). Được thêm sẵn CSRF và chống session fixation.
62. **Vì sao không có tầng Service? Có nên thêm không?**
    Dự án nhỏ nên controller gọi repo cho gọn. Nên thêm khi một nghiệp vụ dùng nhiều repo cùng lúc (ví dụ `AdminController.capNhat` tạo bài kinh nghiệm và cập nhật sự cố). Khi đó đặt `@Transactional` ở service để 2 thao tác là một khối.
63. **Muốn phân trang danh sách sự cố?**
    **(Đã làm — xem mục 9.H)** Thêm tham số `trang`, SQL dùng `OFFSET ? ROWS FETCH NEXT ? ROWS ONLY`, và `COUNT(*)` riêng để đếm tổng số dòng. Lớp `Trang<T>` (generic) gói việc này dùng chung cho sự cố, thiết bị, phòng, người dùng.

---

### H. Phân trang, tìm kiếm, xuất/nhập Excel, xử lý lỗi toàn cục (bổ sung gần đây)
64. **`Trang<T>` là lớp generic, vì sao không viết riêng 4 lớp `TrangSuCo`, `TrangThietBi`...?**
    4 danh sách (sự cố, thiết bị, phòng, người dùng) có cùng một khuôn phân trang (số trang, tổng dòng, các số trang hiện trên thanh phân trang, link giữ nguyên bộ lọc), chỉ khác kiểu dữ liệu của từng dòng. Generic (`<T>`) viết một lần, dùng lại cho cả 4, tránh lặp code — đổi lại các phương thức không biết gì về `T` ngoài việc chứa nó trong `List<T>`.
65. **Vì sao phân trang lấy `OFFSET ... FETCH NEXT ... ROWS ONLY` ở Oracle mà không lấy hết rồi cắt ở Java?**
    Lấy hết dữ liệu về rồi cắt ở Java vẫn tốn thời gian và băng thông truyền hết dữ liệu từ Oracle lên, dù chỉ hiển thị 10 dòng. Để Oracle cắt trực tiếp trong câu SQL thì chỉ 10 dòng cần hiển thị được truyền đi. Đổi lại phải gọi thêm 1 câu `SELECT COUNT(*)` riêng (cùng điều kiện lọc) để biết tổng số dòng/số trang.
66. **Tìm kiếm không phân biệt hoa thường và không dấu làm thế nào, có phải tự viết hàm bỏ dấu không?**
    Không tự viết. Dùng `COLLATE BINARY_AI` ngay trong câu SQL (`TEN_PHONG COLLATE BINARY_AI LIKE ?`): đây là collation có sẵn của Oracle, so khớp không phân biệt hoa/thường (`AI` = accent-insensitive, `BI` phần đầu = binary/case-insensitive theo cách Oracle đặt tên). Từ khóa vẫn truyền qua `?` như bình thường.
67. **Vì sao phải escape `%` và `_` trong từ khóa tìm kiếm (`TimKiem.dieuKien`)?**
    `%` và `_` là ký tự đại diện của `LIKE` (khớp nhiều ký tự / đúng 1 ký tự). Nếu người dùng gõ đúng ký tự `%` hoặc `_` mà không escape, nó sẽ được hiểu là ký tự đại diện chứ không phải ký tự cần tìm theo nghĩa đen. Dùng `ESCAPE '\\'` và thay `%`→`\%`, `_`→`\_` trước khi ghép vào mẫu `%...%`.
68. **Xuất Excel dùng `SXSSFWorkbook` khác `XSSFWorkbook` ở đâu?**
    `XSSFWorkbook` giữ toàn bộ workbook trong RAM — đơn giản, hợp cho file nhỏ (file mẫu nhập thiết bị). `SXSSFWorkbook` chỉ giữ một cửa sổ (window) các dòng gần nhất trong RAM, dòng cũ hơn được ghi dần ra đĩa tạm — hợp khi số dòng xuất ra có thể lớn (xuất toàn bộ sự cố đang lọc). Phải gọi `dispose()` sau khi `write()` để xóa file tạm SXSSF sinh ra.
69. **Vì sao ghi ô Excel bằng `setCellValue(String)` lại chống được "Excel injection" / "CSV injection"?**
    Nếu một ô bắt đầu bằng `=`, `+`, `-`, `@` thì nhiều phần mềm bảng tính hiểu đó là công thức khi mở file và có thể chạy lệnh (ví dụ `=HYPERLINK(...)`, hoặc với CSV cũ có thể gọi lệnh hệ điều hành qua DDE). Ghi bằng `setCellValue(String)` của POI lưu đúng nghĩa là **chuỗi văn bản** vào file `.xlsx`, không đánh dấu ô là công thức, nên Excel hiển thị nguyên văn `=1+1` chứ không tính toán.
70. **Nhập Excel kiểm tra dữ liệu ở đâu, ghi CSDL khi nào?**
    `ExcelThietBi.doc(...)` đọc và kiểm tra **toàn bộ** file trước (thiếu cột bắt buộc, sai định dạng ngày, loại/phòng/tình trạng không có trong danh mục, trùng thiết bị với CSDL hoặc trùng ngay trong file), trả về `KetQua` (danh sách dòng hợp lệ + danh sách lỗi). Controller chỉ gọi `INSERT` khi `KetQua` không còn lỗi nào — nhập "tất cả hoặc không gì cả" nhưng quyết định bằng kiểm tra dữ liệu ở Java trước, không phải bằng rollback transaction.
71. **`XuLyLoiChung` xử lý những lỗi gì, còn lỗi gì vẫn do controller tự bắt?**
    `XuLyLoiChung` (`@ControllerAdvice`) chỉ bắt các lỗi mang tính "hạ tầng" dùng chung cho mọi form: tham số sai kiểu/thiếu tham số bắt buộc, và file tải lên vượt quá dung lượng cho phép. Các lỗi nghiệp vụ gắn với từng chức năng (trùng email, xóa thiết bị còn sự cố, file ảnh sai loại...) vẫn được từng controller tự bắt để trả thông báo tiếng Việt phù hợp với đúng tình huống đó.
72. **`@ExceptionHandler` trong `XuLyLoiChung` lấy `RedirectAttributes` bằng cách nào mà phương thức không có tham số đó?**
    `@ExceptionHandler` không hỗ trợ tiêm `RedirectAttributes` như controller thường (lỗi xảy ra trước khi Spring xác định được controller đích để gắn flash attribute theo cách thông thường). Thay vào đó code lấy thẳng `HttpServletRequest`, gọi `RequestContextUtils.getOutputFlashMap(request)` để ghi trực tiếp vào flash map của request, rồi trả `"redirect:" + ...` như một chuỗi URL thủ công (chỉ lấy phần path của `Referer`, bỏ qua domain, để không bị lợi dụng redirect sang trang khác).

---

### I. Polling AJAX cho "Trao đổi" và "/admin tự hiện sự cố mới" (bổ sung gần đây)
73. **Vì sao gọi là "polling", nó khác gì so với real-time thật (WebSocket)?**
    Polling là client tự gọi lại server theo chu kỳ cố định (`setInterval` + `fetch`, 4 giây cho khung trao đổi, 5 giây cho danh sách sự cố) để hỏi "có gì mới không", chứ server không tự "đẩy" (push) dữ liệu khi có thay đổi. Đơn giản, không cần giữ kết nối mở, không thêm dependency, dùng lại được đúng cơ chế HTTP request/response đã có — hợp với quy mô nhỏ của bài tập lớn. Đổi lại có độ trễ tối đa 1 chu kỳ và tốn request lặp lại dù không có gì mới.
74. **Tham số `sauMa` dùng để làm gì? Vì sao không trả lại toàn bộ danh sách mỗi lần poll?**
    `sauMa` là mã lớn nhất (`MA_BL`/`MA_SC`) mà trang đang có, gửi lên để server chỉ trả **phần mới hơn** (`WHERE MA_BL > ?`). Nếu trả lại toàn bộ danh sách mỗi 4-5 giây thì vừa tốn băng thông, vừa phải tự dò xem dòng nào đã hiển thị rồi để không hiện trùng. Vì `MA_BL`/`MA_SC` là khóa `IDENTITY` tăng dần, nên "mã lớn hơn mã đã biết" tương đương với "mới hơn".
75. **Vì sao các endpoint này trả `List<Map<String, Object>>` thay vì trả thẳng đối tượng `BinhLuan`/`SuCo`?**
    Hai lý do. (1) Nếu trả `LocalDateTime` trực tiếp, Jackson (thư viện JSON của Spring Boot) sẽ mặc định serialize thành mảng số `[năm,tháng,ngày,giờ,phút]`, khó dùng ở JS — nên định dạng sẵn thành chuỗi `"dd/MM/yyyy HH:mm"` ở Java trước khi trả (`BinhLuan.dangJson()`). (2) Chỉ cần đúng vài trường để vẽ lại dòng/khung chat, không cần trả nguyên cả model (có field không nên lộ ra ngoài dạng JSON). Dùng `Map` dựng tay thay vì viết hẳn 1 lớp DTO vì chỉ dùng ở đúng chỗ này.
76. **Vì sao `/admin/moi` trả rỗng ngay khi `trangThai` là `DANG_XU_LY`/`HOAN_THANH`?**
    Vì sự cố vừa báo luôn ở trạng thái `MOI` (Oracle gán mặc định), nên không bao giờ xuất hiện dưới 2 bộ lọc kia — trả rỗng sớm (`if (loc != null && !loc.equals("MOI")) return List.of();`) để tránh chạy câu SQL vô ích. `admin.html` cũng tự tính sẵn điều kiện này vào thuộc tính `data-su-co-bat` để JS biết khi nào cần gọi, không phụ thuộc hoàn toàn vào việc server trả rỗng.
77. **Vì sao ở `web-java`, `/admin` chỉ tự hiện sự cố mới khi đang ở trang 1 và không gõ từ khóa tìm kiếm?**
    Vì danh sách sự cố ở `web-java` có phân trang + tìm kiếm phía server (`Trang<SuCo>`, tham số `q`). Nếu đang xem trang 2 hoặc đang lọc theo từ khóa mà cứ chèn dòng mới lên đầu bảng thì sẽ sai lệch với đúng nội dung của trang/bộ lọc đang xem (dòng không thuộc trang đó, hoặc không khớp từ khóa). Điều kiện `trang.so == 1 and q == null` được tính sẵn ở `admin.html` (Thymeleaf), JS chỉ đọc lại qua `data-su-co-bat`.
78. **Vì sao JS phải tự viết hàm `thoat()` (escape `& < > " '`) trước khi chèn `tieuDe`/`noiDung` vào trang?**
    Vì đây là dữ liệu do người dùng nhập (tiêu đề sự cố, nội dung trao đổi) và được chèn trực tiếp bằng `insertAdjacentHTML` ở phía client. Nếu không thoát, một người gõ nội dung dạng `<script>...</script>` sẽ được trình duyệt của người khác (admin hoặc người báo khác xem cùng trang) hiểu là mã HTML/JS thật — đây là XSS lưu trữ (stored XSS). Thymeleaf tự thoát khi dùng `th:text` ở phía server, nhưng khi tự dựng HTML bằng tay ở JS thì phải tự làm việc này.
79. **Ai cũng gọi được `/user/{maSc}/binh-luan` hay `/admin/moi` nếu biết URL không?**
    Không. `/user/**` và `/admin/**` vẫn nằm trong danh sách `AuthInterceptor` chặn như mọi trang khác (`WebConfig.addPathPatterns`), nên phải đăng nhập đúng vai trò. Riêng endpoint của `/user` còn tự kiểm tra thêm quyền sở hữu (`filter(sc -> sc.getMaNguoiBao().equals(nd.getMaNd()))`, giống cách trang xem chi tiết đã làm), để một người dùng không đọc được bình luận của sự cố người khác dù đoán đúng mã.
80. **Vì sao chọn chu kỳ 4 giây (trao đổi) và 5 giây (danh sách sự cố)? Có dừng lại khi không cần không?**
    Không theo quy tắc cứng nào, chọn theo cảm nhận trải nghiệm: đủ nhanh để thấy "gần như ngay" khi có người gửi tin/báo sự cố, nhưng không gọi quá dày để tránh tải CSDL không cần thiết. Có dừng hẳn việc gọi khi tab ẩn đi (lắng nghe sự kiện `visibilitychange`, kiểm tra `document.hidden`) để đỡ tốn request khi người dùng chuyển sang tab khác.
81. **Muốn nâng polling này thành real-time thật (kiểu Zalo/Telegram bản web) thì làm gì?**
    Đổi sang WebSocket (thêm `spring-boot-starter-websocket`, dùng STOMP + SockJS): server chủ động đẩy dữ liệu tới các client đang mở trang ngay khi có bình luận/sự cố mới, không cần client tự hỏi lại theo chu kỳ — giảm độ trễ và số request thừa. Đánh đổi: phải quản lý kết nối mở lâu dài phía server (tốn tài nguyên hơn khi nhiều người online cùng lúc) và cấu hình phức tạp hơn polling.

---

### Mẹo khi trả lời
- Mở sẵn trong IDE các file "đinh": `model/CoNhan.java` + `model/TrangThaiSuCo.java`, `util/BoLuuTru.java` + `util/LuuAnhSuCo.java`, `util/MatKhau.java`, `config/AuthInterceptor.java`, `repository/SuCoRepo.java` (chú ý `capNhat` có `FOR UPDATE`), và các file mới: `model/Trang.java` (generic phân trang), `util/TimKiem.java`, `util/ExcelSuCo.java`, `controller/XuLyLoiChung.java`, `model/BinhLuan.java` (`dangJson()`), `controller/AdminController.java` (`suCoMoi`, `binhLuanMoi`), `static/js/app.js` (2 khối polling cuối file).
- Khi được hỏi OOP, luôn trả lời theo khuôn: **khái niệm → file cụ thể → lợi ích trong dự án này**.
- Chủ động nói hạn chế (mục 8) kèm hướng sửa. Giáo viên thường đánh giá cao việc sinh viên biết code mình còn thiếu gì.
