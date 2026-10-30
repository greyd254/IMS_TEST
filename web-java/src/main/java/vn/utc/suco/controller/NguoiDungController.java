package vn.utc.suco.controller;

import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
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

    private static final Pattern SDT_HOP_LE = Pattern.compile("0\\d{9}");

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
                       @RequestParam(required = false) String xacNhanMatKhau,
                       @RequestParam(required = false) String donVi, @RequestParam String vaiTro,
                       @RequestParam(required = false) String sdt,
                       HttpSession session, RedirectAttributes ra) {
        String loi = !hopLe(hoTen, email, vaiTro) || matKhau.isBlank()
                ? "Vui lòng nhập họ tên, email hợp lệ, mật khẩu và chọn vai trò"
                : kiemTraMatKhauVaSdt(matKhau, xacNhanMatKhau, sdt);
        if (loi != null) {
            ra.addFlashAttribute("loi", loi);
            return "redirect:/admin/nguoi-dung";
        }
        NguoiDung toi = (NguoiDung) session.getAttribute("nguoiDung");
        try {
            nguoiDungRepo.taoMoi(hoTen.trim(), email.trim().toLowerCase(), matKhau, donVi, vaiTro, chuanHoaSdt(sdt),
                    toi.getMaNd());
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
                          @RequestParam(required = false) String sdt,
                          @RequestParam(required = false) String matKhau,
                          @RequestParam(required = false) String xacNhanMatKhau,
                          HttpSession session, RedirectAttributes ra) {
        NguoiDung toi = (NguoiDung) session.getAttribute("nguoiDung");
        // Mật khẩu để trống = không đổi, nên chỉ so xác nhận khi có nhập
        String loi = !hopLe(hoTen, email, vaiTro) ? "Dữ liệu không hợp lệ"
                : kiemTraMatKhauVaSdt(matKhau == null || matKhau.isBlank() ? null : matKhau, xacNhanMatKhau, sdt);
        if (loi != null) {
            ra.addFlashAttribute("loi", loi);
            return "redirect:/admin/nguoi-dung/" + maNd + "/sua";
        }
        // Admin không được tự hạ quyền của chính mình: luôn còn ít nhất một admin để quản trị hệ thống
        if (maNd.equals(toi.getMaNd()) && !"ADMIN".equals(vaiTro)) {
            ra.addFlashAttribute("loi", "Bạn không thể tự hạ quyền quản trị của chính mình");
            return "redirect:/admin/nguoi-dung/" + maNd + "/sua";
        }
        try {
            nguoiDungRepo.capNhat(maNd, hoTen.trim(), email.trim().toLowerCase(), donVi, vaiTro, chuanHoaSdt(sdt),
                    matKhau);
        } catch (DuplicateKeyException e) {
            ra.addFlashAttribute("loi", "Email này đã được sử dụng bởi người khác");
            return "redirect:/admin/nguoi-dung/" + maNd + "/sua";
        }
        // Sửa chính mình thì cập nhật lại thông tin trong session để thanh menu hiển thị đúng
        if (maNd.equals(toi.getMaNd())) {
            nguoiDungRepo.timTheoMa(maNd).ifPresent(nd -> session.setAttribute("nguoiDung", nd));
        }
        ra.addFlashAttribute("thongBao", "Đã cập nhật người dùng");
        return "redirect:/admin/nguoi-dung";
    }

    /** Xóa mềm: chuyển tài khoản sang KHONG_HOAT_DONG thay vì DELETE, để giữ lại lịch sử gắn với người này. */
    @PostMapping("/admin/nguoi-dung/{maNd}/xoa")
    public String xoa(@PathVariable Long maNd, HttpSession session, RedirectAttributes ra) {
        NguoiDung toi = (NguoiDung) session.getAttribute("nguoiDung");
        if (maNd.equals(toi.getMaNd())) {
            ra.addFlashAttribute("loi", "Bạn không thể tự vô hiệu hóa tài khoản đang đăng nhập");
            return "redirect:/admin/nguoi-dung";
        }
        nguoiDungRepo.voHieuHoa(maNd, toi.getMaNd());
        ra.addFlashAttribute("thongBao", "Đã vô hiệu hóa người dùng (không đăng nhập được, dữ liệu cũ vẫn giữ nguyên)");
        return "redirect:/admin/nguoi-dung";
    }

    @PostMapping("/admin/nguoi-dung/{maNd}/kich-hoat")
    public String kichHoat(@PathVariable Long maNd, RedirectAttributes ra) {
        nguoiDungRepo.kichHoatLai(maNd);
        ra.addFlashAttribute("thongBao", "Đã kích hoạt lại người dùng");
        return "redirect:/admin/nguoi-dung";
    }

    private boolean hopLe(String hoTen, String email, String vaiTro) {
        return !hoTen.isBlank() && email.contains("@") && VAI_TRO.containsKey(vaiTro);
    }

    /**
     * Mật khẩu (null = không đổi, bỏ qua) phải khớp ô xác nhận; số điện thoại (không bắt buộc) phải là
     * số di động/cố định Việt Nam 10 chữ số bắt đầu bằng 0. Trả về câu báo lỗi, hoặc null nếu hợp lệ.
     */
    private String kiemTraMatKhauVaSdt(String matKhau, String xacNhanMatKhau, String sdt) {
        if (matKhau != null && !matKhau.equals(xacNhanMatKhau)) {
            return "Mật khẩu xác nhận không khớp";
        }
        String s = chuanHoaSdt(sdt);
        if (s != null && !SDT_HOP_LE.matcher(s).matches()) {
            return "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0 (VD 0912345678)";
        }
        return null;
    }

    /** Bỏ khoảng trắng, dấu chấm, gạch ngang người dùng hay gõ kèm (0912.345.678); để trống = null. */
    private static String chuanHoaSdt(String sdt) {
        if (sdt == null) {
            return null;
        }
        String s = sdt.replaceAll("[\\s.\\-]", "");
        return s.isEmpty() ? null : s;
    }
}
