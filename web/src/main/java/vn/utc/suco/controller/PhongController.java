package vn.utc.suco.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.utc.suco.repository.PhongRepo;

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

    private final PhongRepo phongRepo;

    public PhongController(PhongRepo phongRepo) {
        this.phongRepo = phongRepo;
    }

    @GetMapping("/admin/phong")
    public String danhSach(Model model) {
        model.addAttribute("dsPhong", phongRepo.timTatCa());
        model.addAttribute("dsLoai", LOAI);
        return "phong";
    }

    @PostMapping("/admin/phong")
    public String them(@RequestParam String tenPhong, @RequestParam String toaNha, @RequestParam Integer tang,
                       @RequestParam String loaiPhong, RedirectAttributes ra) {
        if (!hopLe(tenPhong, toaNha, tang, loaiPhong)) {
            ra.addFlashAttribute("loi", "Vui lòng nhập tên phòng, tòa nhà, tầng và chọn loại phòng hợp lệ");
            return "redirect:/admin/phong";
        }
        phongRepo.taoMoi(tenPhong.trim(), toaNha.trim(), tang, loaiPhong);
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
                          @RequestParam Integer tang, @RequestParam String loaiPhong, RedirectAttributes ra) {
        if (!hopLe(tenPhong, toaNha, tang, loaiPhong)) {
            ra.addFlashAttribute("loi", "Dữ liệu không hợp lệ");
            return "redirect:/admin/phong/" + maPhong + "/sua";
        }
        phongRepo.capNhat(maPhong, tenPhong.trim(), toaNha.trim(), tang, loaiPhong);
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

    private boolean hopLe(String tenPhong, String toaNha, Integer tang, String loaiPhong) {
        return tenPhong != null && !tenPhong.isBlank() && toaNha != null && !toaNha.isBlank()
                && tang != null && LOAI.containsKey(loaiPhong);
    }
}
