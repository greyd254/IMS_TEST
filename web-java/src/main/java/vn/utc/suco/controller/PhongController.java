package vn.utc.suco.controller;

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
import vn.utc.suco.repository.PhongRepo;
import vn.utc.suco.util.TimKiem;

/**
 * Admin quản lý phòng: thêm, sửa, xóa (/admin/** đã được AuthInterceptor chặn cho USER).
 * Giúp mở rộng khu vực hỗ trợ (thêm phòng mới) mà không cần vào thẳng CSDL.
 */
@Controller
public class PhongController {
    private static final Map<String, String> LOAI = new LinkedHashMap<>();

    static {
        LOAI.put("PHONG_MAY", "Phòng máy");
        LOAI.put("PHONG_HOC", "Phòng học");
        LOAI.put("VAN_PHONG", "Văn phòng");
    }

    private static final String TRUNG = "Đã có phòng cùng tên ở cùng tòa nhà, cùng tầng";

    private final PhongRepo phongRepo;

    public PhongController(PhongRepo phongRepo) {
        this.phongRepo = phongRepo;
    }

    /** Danh sách phòng (phân trang, tìm theo ?q=) kèm form thêm mới. */
    @GetMapping("/admin/phong")
    public String danhSach(@RequestParam(required = false) String q, @RequestParam(required = false) Integer trang,
                           Model model) {
        String tuKhoa = TimKiem.chuanHoa(q);
        long tong = phongRepo.dem(tuKhoa);
        int so = Trang.gioiHan(trang, tong);
        model.addAttribute("trang", new Trang<>(phongRepo.timTrang(tuKhoa, so), so, tong,
                UriComponentsBuilder.fromPath("/admin/phong").queryParamIfPresent("q", Optional.ofNullable(tuKhoa))));
        model.addAttribute("q", tuKhoa);
        model.addAttribute("dsLoai", LOAI);
        return "phong";
    }

    @PostMapping("/admin/phong")
    public String them(@RequestParam String tenPhong, @RequestParam String toaNha,
                       @RequestParam(required = false) Integer tang,
                       @RequestParam String loaiPhong, RedirectAttributes ra) {
        String loi = kiemTra(null, tenPhong, toaNha, tang, loaiPhong);
        if (loi != null) {
            ra.addFlashAttribute("loi", loi);
            return "redirect:/admin/phong";
        }
        try {
            phongRepo.taoMoi(tenPhong.trim(), toaNha.trim(), tang, loaiPhong);
        } catch (DuplicateKeyException e) {
            // Chỉ xảy ra khi đã chạy sql/13 (index UQ_PHONG_TEN) và có người thêm trùng cùng lúc
            ra.addFlashAttribute("loi", TRUNG);
            return "redirect:/admin/phong";
        }
        ra.addFlashAttribute("thongBao", "Đã thêm phòng");
        return "redirect:/admin/phong";
    }

    @GetMapping("/admin/phong/{maPhong}/sua")
    public String formSua(@PathVariable Long maPhong, Model model) {
        var p = phongRepo.timTheoMa(maPhong);
        if (p.isEmpty()) {
            return "redirect:/admin/phong";
        }
        model.addAttribute("p", p.get());
        model.addAttribute("dsLoai", LOAI);
        return "phong-sua";
    }

    @PostMapping("/admin/phong/{maPhong}")
    public String capNhat(@PathVariable Long maPhong, @RequestParam String tenPhong, @RequestParam String toaNha,
                          @RequestParam(required = false) Integer tang, @RequestParam String loaiPhong,
                          RedirectAttributes ra) {
        String loi = kiemTra(maPhong, tenPhong, toaNha, tang, loaiPhong);
        if (loi != null) {
            ra.addFlashAttribute("loi", loi);
            return "redirect:/admin/phong/" + maPhong + "/sua";
        }
        try {
            phongRepo.capNhat(maPhong, tenPhong.trim(), toaNha.trim(), tang, loaiPhong);
        } catch (DuplicateKeyException e) {
            ra.addFlashAttribute("loi", TRUNG);
            return "redirect:/admin/phong/" + maPhong + "/sua";
        }
        ra.addFlashAttribute("thongBao", "Đã cập nhật phòng");
        return "redirect:/admin/phong";
    }

    @PostMapping("/admin/phong/{maPhong}/xoa")
    public String xoa(@PathVariable Long maPhong, RedirectAttributes ra) {
        try {
            phongRepo.xoa(maPhong);
            ra.addFlashAttribute("thongBao", "Đã xóa phòng");
        } catch (DataIntegrityViolationException e) {
            // Khóa ngoại FK_THIET_BI_PHONG: phòng này còn thiết bị
            ra.addFlashAttribute("loi", "Không thể xóa: phòng này còn thiết bị. Hãy chuyển hoặc xóa thiết bị trước.");
        }
        return "redirect:/admin/phong";
    }

    /** Kiểm tra dữ liệu form thêm/sửa; trả về câu báo lỗi, hoặc null nếu hợp lệ. maPhong = null khi thêm mới. */
    private String kiemTra(Long maPhong, String tenPhong, String toaNha, Integer tang, String loaiPhong) {
        if (tenPhong == null || tenPhong.isBlank() || toaNha == null || toaNha.isBlank()
                || !LOAI.containsKey(loaiPhong)) {
            return "Vui lòng nhập tên phòng, tòa nhà và chọn loại phòng hợp lệ";
        }
        if (tang != null && (tang < 0 || tang > 99)) {
            return "Tầng phải trong khoảng 0 đến 99";   // khớp ô nhập; cột TANG là NUMBER(2)
        }
        if (phongRepo.tonTaiTrung(tenPhong.trim(), toaNha.trim(), tang, maPhong)) {
            return TRUNG;
        }
        return null;
    }
}
