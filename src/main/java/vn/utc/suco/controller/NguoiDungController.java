package vn.utc.suco.controller;

import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.model.Trang;
import vn.utc.suco.repository.NguoiDungRepo;
import vn.utc.suco.util.TimKiem;

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
    public String danhSach(@RequestParam(required = false) String q, @RequestParam(required = false) Integer trang,
                           Model model) {
        String tuKhoa = TimKiem.chuanHoa(q);
        long tong = nguoiDungRepo.dem(tuKhoa);
        int so = Trang.gioiHan(trang, tong);
        model.addAttribute("trang", new Trang<>(nguoiDungRepo.timTrang(tuKhoa, so), so, tong,
                UriComponentsBuilder.fromPath("/admin/nguoi-dung").queryParamIfPresent("q", Optional.ofNullable(tuKhoa))));
        model.addAttribute("q", tuKhoa);
        model.addAttribute("dsVaiTro", VAI_TRO);
        return "nguoi-dung";
    }

    @PostMapping("/admin/nguoi-dung")
    public String them(@RequestParam String hoTen, @RequestParam String email, @RequestParam String matKhau,
                       @RequestParam(required = false) String donVi, @RequestParam String vaiTro,
                       @RequestParam(required = false) String sdt,
                       HttpSession session, RedirectAttributes ra) {
        if (!hopLe(hoTen, email, vaiTro) || matKhau.isBlank()) {
            ra.addFlashAttribute("loi", "Vui lòng nhập họ tên, email hợp lệ, mật khẩu và chọn vai trò");
            return "redirect:/admin/nguoi-dung";
        }
        NguoiDung toi = (NguoiDung) session.getAttribute("nguoiDung");
        try {
            nguoiDungRepo.taoMoi(hoTen.trim(), email.trim().toLowerCase(), matKhau, donVi, vaiTro, sdt, toi.maNd());
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
            nguoiDungRepo.capNhat(maNd, hoTen.trim(), email.trim().toLowerCase(), donVi, vaiTro, sdt, matKhau);
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

    /** Xóa mềm: chuyển tài khoản sang KHONG_HOAT_DONG thay vì DELETE, để giữ lại lịch sử gắn với người này. */
    @PostMapping("/admin/nguoi-dung/{maNd}/xoa")
    public String xoa(@PathVariable Long maNd, @RequestParam(required = false) String q,
                      @RequestParam(required = false) Integer trang, HttpSession session, RedirectAttributes ra) {
        giuViTri(ra, q, trang);
        NguoiDung toi = (NguoiDung) session.getAttribute("nguoiDung");
        if (maNd.equals(toi.maNd())) {
            ra.addFlashAttribute("loi", "Bạn không thể tự vô hiệu hóa tài khoản đang đăng nhập");
            return "redirect:/admin/nguoi-dung";
        }
        nguoiDungRepo.voHieuHoa(maNd, toi.maNd());
        ra.addFlashAttribute("thongBao", "Đã vô hiệu hóa người dùng (không đăng nhập được, dữ liệu cũ vẫn giữ nguyên)");
        return "redirect:/admin/nguoi-dung";
    }

    @PostMapping("/admin/nguoi-dung/{maNd}/kich-hoat")
    public String kichHoat(@PathVariable Long maNd, @RequestParam(required = false) String q,
                           @RequestParam(required = false) Integer trang, RedirectAttributes ra) {
        giuViTri(ra, q, trang);
        nguoiDungRepo.kichHoatLai(maNd);
        ra.addFlashAttribute("thongBao", "Đã kích hoạt lại người dùng");
        return "redirect:/admin/nguoi-dung";
    }

    /** Sau khi thao tác ngay trên danh sách, quay lại đúng trang và từ khóa đang xem thay vì về trang 1. */
    private static void giuViTri(RedirectAttributes ra, String q, Integer trang) {
        ra.addAttribute("q", TimKiem.chuanHoa(q));
        ra.addAttribute("trang", trang);
    }

    private boolean hopLe(String hoTen, String email, String vaiTro) {
        return !hoTen.isBlank() && email.contains("@") && VAI_TRO.containsKey(vaiTro);
    }
}
