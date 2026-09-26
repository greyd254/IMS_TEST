package vn.utc.suco.controller;

import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.repository.NguoiDungRepo;

/** Admin quản lý người dùng: danh sách, thêm, sửa, xóa (/admin/** đã được AuthInterceptor chặn cho USER). */
@Controller
public class NguoiDungController {
    private static final Map<String, String> VAI_TRO = new LinkedHashMap<>();

    static {
        VAI_TRO.put("USER", "Người dùng");
        VAI_TRO.put("ADMIN", "Quản trị (IT)");
    }

    private final NguoiDungRepo nguoiDungRepo;

    public NguoiDungController(NguoiDungRepo nguoiDungRepo) {
        this.nguoiDungRepo = nguoiDungRepo;
    }

    @GetMapping("/admin/nguoi-dung")
    public String danhSach(Model model) {
        model.addAttribute("dsNguoiDung", nguoiDungRepo.timTatCa());
        model.addAttribute("dsVaiTro", VAI_TRO);
        return "nguoi-dung";
    }

    @PostMapping("/admin/nguoi-dung")
    public String them(@RequestParam String hoTen, @RequestParam String email, @RequestParam String matKhau,
                       @RequestParam(required = false) String donVi, @RequestParam String vaiTro,
                       RedirectAttributes ra) {
        if (!hopLe(hoTen, email, vaiTro) || matKhau.isBlank()) {
            ra.addFlashAttribute("loi", "Vui lòng nhập họ tên, email hợp lệ, mật khẩu và chọn vai trò");
            return "redirect:/admin/nguoi-dung";
        }
        try {
            nguoiDungRepo.taoMoi(hoTen.trim(), email.trim().toLowerCase(), matKhau, donVi, vaiTro);
            ra.addFlashAttribute("thongBao", "Đã thêm người dùng");
        } catch (DuplicateKeyException e) {
            // Vi phạm UQ_NGUOI_DUNG_EMAIL
            ra.addFlashAttribute("loi", "Email này đã được sử dụng");
        }
        return "redirect:/admin/nguoi-dung";
    }

    @GetMapping("/admin/nguoi-dung/{maNd}/sua")
    public String formSua(@PathVariable Long maNd, Model model) {
        var nd = nguoiDungRepo.timTheoMa(maNd);
        if (nd.isEmpty()) {
            return "redirect:/admin/nguoi-dung";
        }
        model.addAttribute("nd", nd.get());
        model.addAttribute("dsVaiTro", VAI_TRO);
        return "nguoi-dung-sua";
    }

    @PostMapping("/admin/nguoi-dung/{maNd}")
    public String capNhat(@PathVariable Long maNd, @RequestParam String hoTen, @RequestParam String email,
                          @RequestParam(required = false) String donVi, @RequestParam String vaiTro,
                          @RequestParam(required = false) String matKhau,
                          HttpSession session, RedirectAttributes ra) {
        NguoiDung toi = (NguoiDung) session.getAttribute("nguoiDung");
        if (!hopLe(hoTen, email, vaiTro)) {
            ra.addFlashAttribute("loi", "Dữ liệu không hợp lệ");
            return "redirect:/admin/nguoi-dung/" + maNd + "/sua";
        }
        // Admin không được tự hạ quyền của chính mình: luôn còn ít nhất một admin để quản trị hệ thống
        if (maNd.equals(toi.maNd()) && !"ADMIN".equals(vaiTro)) {
            ra.addFlashAttribute("loi", "Bạn không thể tự hạ quyền quản trị của chính mình");
            return "redirect:/admin/nguoi-dung/" + maNd + "/sua";
        }
        try {
            nguoiDungRepo.capNhat(maNd, hoTen.trim(), email.trim().toLowerCase(), donVi, vaiTro, matKhau);
        } catch (DuplicateKeyException e) {
            ra.addFlashAttribute("loi", "Email này đã được sử dụng bởi người khác");
            return "redirect:/admin/nguoi-dung/" + maNd + "/sua";
        }
        // Sửa chính mình thì cập nhật lại thông tin trong session để thanh menu hiển thị đúng
        if (maNd.equals(toi.maNd())) {
            nguoiDungRepo.timTheoMa(maNd).ifPresent(nd -> session.setAttribute("nguoiDung", nd));
        }
        ra.addFlashAttribute("thongBao", "Đã cập nhật người dùng");
        return "redirect:/admin/nguoi-dung";
    }

    @PostMapping("/admin/nguoi-dung/{maNd}/xoa")
    public String xoa(@PathVariable Long maNd, HttpSession session, RedirectAttributes ra) {
        NguoiDung toi = (NguoiDung) session.getAttribute("nguoiDung");
        if (maNd.equals(toi.maNd())) {
            ra.addFlashAttribute("loi", "Bạn không thể tự xóa tài khoản đang đăng nhập");
            return "redirect:/admin/nguoi-dung";
        }
        try {
            nguoiDungRepo.xoa(maNd);
            ra.addFlashAttribute("thongBao", "Đã xóa người dùng");
        } catch (DataIntegrityViolationException e) {
            // Khóa ngoại: người dùng này đã báo sự cố, xử lý sự cố, viết bài kinh nghiệm hoặc có trong lịch sử
            ra.addFlashAttribute("loi", "Không thể xóa: người dùng này đã có sự cố, bài kinh nghiệm hoặc lịch sử xử lý gắn với họ");
        }
        return "redirect:/admin/nguoi-dung";
    }

    private boolean hopLe(String hoTen, String email, String vaiTro) {
        return !hoTen.isBlank() && email.contains("@") && VAI_TRO.containsKey(vaiTro);
    }
}
