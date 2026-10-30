package vn.utc.suco.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import vn.utc.suco.model.Trang;
import vn.utc.suco.repository.ThietBiRepo;
import vn.utc.suco.util.TimKiem;

/** Admin quản lý thiết bị: danh sách, thêm, sửa, xóa (đường dẫn /admin/** đã được AuthInterceptor chặn cho USER). */
@Controller
public class ThietBiController {
    // Giá trị lưu trong CSDL (khớp CHECK của bảng THIET_BI) và nhãn tiếng Việt hiển thị
    private static final Map<String, String> LOAI = new LinkedHashMap<>();
    private static final Map<String, String> TINH_TRANG = new LinkedHashMap<>();

    static {
        LOAI.put("MAY_TINH", "Máy tính");
        LOAI.put("MAY_CHIEU", "Máy chiếu");
        LOAI.put("MAY_IN", "Máy in");
        LOAI.put("THIET_BI_MANG", "Thiết bị mạng");
        LOAI.put("KHAC", "Khác");
        TINH_TRANG.put("HOAT_DONG", "Hoạt động");
        TINH_TRANG.put("HONG", "Hỏng");
        TINH_TRANG.put("DANG_SUA", "Đang sửa");
    }

    /** Ngày lắp đặt sớm nhất chấp nhận (chặn năm gõ nhầm, VD 0202) */
    private static final LocalDate NGAY_NHO_NHAT = LocalDate.of(1990, 1, 1);
    private static final String TRUNG = "Phòng này đã có thiết bị cùng loại, cùng tên. Hãy đặt tên khác (VD thêm số thứ tự)";

    private final ThietBiRepo thietBiRepo;

    public ThietBiController(ThietBiRepo thietBiRepo) {
        this.thietBiRepo = thietBiRepo;
    }

    private void nhanDropdown(Model model) {
        model.addAttribute("dsLoai", LOAI);
        model.addAttribute("dsTinhTrang", TINH_TRANG);
        model.addAttribute("dsPhong", thietBiRepo.timTatCaPhong());
        model.addAttribute("ngayMin", NGAY_NHO_NHAT);   // giới hạn ô chọn ngày lắp đặt
        model.addAttribute("homNay", LocalDate.now());
    }

    /** Danh sách thiết bị (phân trang, tìm theo ?q=) kèm form thêm mới. */
    @GetMapping("/admin/thiet-bi")
    public String danhSach(@RequestParam(required = false) String q, @RequestParam(required = false) Integer trang,
                           Model model) {
        String tuKhoa = TimKiem.chuanHoa(q);
        long tong = thietBiRepo.dem(tuKhoa);
        int so = Trang.gioiHan(trang, tong);
        model.addAttribute("trang", new Trang<>(thietBiRepo.timTrang(tuKhoa, so), so, tong,
                UriComponentsBuilder.fromPath("/admin/thiet-bi").queryParamIfPresent("q", Optional.ofNullable(tuKhoa))));
        model.addAttribute("q", tuKhoa);
        nhanDropdown(model);
        return "thiet-bi";
    }

    @PostMapping("/admin/thiet-bi")
    public String them(@RequestParam String tenTb, @RequestParam String loaiTb, @RequestParam Long maPhong,
                       @RequestParam String tinhTrang, @RequestParam(required = false) String nhaCungCap,
                       @RequestParam(required = false) String ngayLapDat, @RequestParam(required = false) String hdsd,
                       RedirectAttributes ra) {
        String loi = kiemTra(null, tenTb, loaiTb, maPhong, tinhTrang, ngayLapDat);
        if (loi != null) {
            ra.addFlashAttribute("loi", loi);
            return "redirect:/admin/thiet-bi";
        }
        try {
            thietBiRepo.taoMoi(tenTb.trim(), loaiTb, maPhong, tinhTrang, nhaCungCap, docNgay(ngayLapDat), hdsd);
        } catch (DuplicateKeyException e) {
            // Chỉ xảy ra khi đã chạy sql/13 (index UQ_THIET_BI_TEN) và có người thêm trùng cùng lúc
            ra.addFlashAttribute("loi", TRUNG);
            return "redirect:/admin/thiet-bi";
        }
        ra.addFlashAttribute("thongBao", "Đã thêm thiết bị");
        return "redirect:/admin/thiet-bi";
    }

    /** Form sửa một thiết bị. */
    @GetMapping("/admin/thiet-bi/{maTb}/sua")
    public String formSua(@PathVariable Long maTb, Model model) {
        var tb = thietBiRepo.timTheoMa(maTb);
        if (tb.isEmpty()) {
            return "redirect:/admin/thiet-bi";
        }
        model.addAttribute("tb", tb.get());
        nhanDropdown(model);
        return "thiet-bi-sua";
    }

    @PostMapping("/admin/thiet-bi/{maTb}")
    public String capNhat(@PathVariable Long maTb, @RequestParam String tenTb, @RequestParam String loaiTb,
                          @RequestParam Long maPhong, @RequestParam String tinhTrang,
                          @RequestParam(required = false) String nhaCungCap,
                          @RequestParam(required = false) String ngayLapDat,
                          @RequestParam(required = false) String hdsd, RedirectAttributes ra) {
        String loi = kiemTra(maTb, tenTb, loaiTb, maPhong, tinhTrang, ngayLapDat);
        if (loi != null) {
            ra.addFlashAttribute("loi", loi);
            return "redirect:/admin/thiet-bi/" + maTb + "/sua";
        }
        try {
            thietBiRepo.capNhat(maTb, tenTb.trim(), loaiTb, maPhong, tinhTrang, nhaCungCap, docNgay(ngayLapDat), hdsd);
        } catch (DuplicateKeyException e) {
            ra.addFlashAttribute("loi", TRUNG);
            return "redirect:/admin/thiet-bi/" + maTb + "/sua";
        }
        ra.addFlashAttribute("thongBao", "Đã cập nhật thiết bị");
        return "redirect:/admin/thiet-bi";
    }

    @PostMapping("/admin/thiet-bi/{maTb}/xoa")
    public String xoa(@PathVariable Long maTb, RedirectAttributes ra) {
        try {
            thietBiRepo.xoa(maTb);
            ra.addFlashAttribute("thongBao", "Đã xóa thiết bị");
        } catch (DataIntegrityViolationException e) {
            // Oracle từ chối do khóa ngoại FK_SU_CO_THIET_BI: thiết bị đang được sự cố tham chiếu
            ra.addFlashAttribute("loi", "Không thể xóa: thiết bị này đã có sự cố được báo. Hãy xóa các sự cố đó trước.");
        }
        return "redirect:/admin/thiet-bi";
    }

    /** Kiểm tra dữ liệu form thêm/sửa; trả về câu báo lỗi, hoặc null nếu hợp lệ. maTb = null khi thêm mới. */
    private String kiemTra(Long maTb, String tenTb, String loaiTb, Long maPhong, String tinhTrang, String ngayLapDat) {
        if (tenTb.isBlank() || !LOAI.containsKey(loaiTb) || !TINH_TRANG.containsKey(tinhTrang)) {
            return "Vui lòng nhập tên thiết bị và chọn loại, tình trạng hợp lệ";
        }
        LocalDate ngay;
        try {
            ngay = docNgay(ngayLapDat);
        } catch (DateTimeParseException e) {
            return "Ngày lắp đặt không hợp lệ";
        }
        if (ngay != null && (ngay.isBefore(NGAY_NHO_NHAT) || ngay.isAfter(LocalDate.now()))) {
            return "Ngày lắp đặt phải trong khoảng từ " + NGAY_NHO_NHAT.getYear() + " đến hôm nay";
        }
        if (thietBiRepo.tonTaiTrung(tenTb.trim(), loaiTb, maPhong, maTb)) {
            return TRUNG;
        }
        return null;
    }

    /**
     * Đọc ngày dạng yyyy-MM-dd (giá trị ô input type="date"); để trống = null.
     * Tự đọc thay vì để Spring đổi sang LocalDate, vì nếu Spring đổi lỗi (VD gõ năm 5 chữ số) thì người dùng
     * nhận trang lỗi 400 thay vì thông báo trên form.
     */
    private static LocalDate docNgay(String s) {
        return s == null || s.isBlank() ? null : LocalDate.parse(s.trim());
    }
}
