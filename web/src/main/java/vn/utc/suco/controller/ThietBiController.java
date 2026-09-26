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
import vn.utc.suco.repository.ThietBiRepo;

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

    private final ThietBiRepo thietBiRepo;

    public ThietBiController(ThietBiRepo thietBiRepo) {
        this.thietBiRepo = thietBiRepo;
    }

    private void nhanDropdown(Model model) {
        model.addAttribute("dsLoai", LOAI);
        model.addAttribute("dsTinhTrang", TINH_TRANG);
        model.addAttribute("dsPhong", thietBiRepo.timTatCaPhong());
    }

    /** Danh sách thiết bị kèm form thêm mới. */
    @GetMapping("/admin/thiet-bi")
    public String danhSach(Model model) {
        model.addAttribute("dsThietBi", thietBiRepo.timTatCa());
        nhanDropdown(model);
        return "thiet-bi";
    }

    @PostMapping("/admin/thiet-bi")
    public String them(@RequestParam String tenTb, @RequestParam String loaiTb, @RequestParam Long maPhong,
                       @RequestParam String tinhTrang, RedirectAttributes ra) {
        if (!hopLe(tenTb, loaiTb, tinhTrang)) {
            ra.addFlashAttribute("loi", "Vui lòng nhập tên thiết bị và chọn loại, tình trạng hợp lệ");
            return "redirect:/admin/thiet-bi";
        }
        thietBiRepo.taoMoi(tenTb.trim(), loaiTb, maPhong, tinhTrang);
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
                          @RequestParam Long maPhong, @RequestParam String tinhTrang, RedirectAttributes ra) {
        if (!hopLe(tenTb, loaiTb, tinhTrang)) {
            ra.addFlashAttribute("loi", "Dữ liệu không hợp lệ");
            return "redirect:/admin/thiet-bi/" + maTb + "/sua";
        }
        thietBiRepo.capNhat(maTb, tenTb.trim(), loaiTb, maPhong, tinhTrang);
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

    private boolean hopLe(String tenTb, String loaiTb, String tinhTrang) {
        return !tenTb.isBlank() && LOAI.containsKey(loaiTb) && TINH_TRANG.containsKey(tinhTrang);
    }
}
