package vn.utc.suco.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.repository.NguoiDungRepo;

/** Đăng nhập / đăng xuất bằng HttpSession. */
@Controller
public class AuthController {
    private final NguoiDungRepo nguoiDungRepo;

    public AuthController(NguoiDungRepo nguoiDungRepo) {
        this.nguoiDungRepo = nguoiDungRepo;
    }

    @GetMapping("/")
    public String trangChu() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String formDangNhap(HttpSession session) {
        // Đã đăng nhập rồi thì đưa thẳng tới trang của vai trò tương ứng
        NguoiDung nd = (NguoiDung) session.getAttribute("nguoiDung");
        return nd == null ? "login" : trangSauDangNhap(nd);
    }

    @PostMapping("/login")
    public String dangNhap(@RequestParam String email, @RequestParam String matKhau,
                           HttpSession session, Model model) {
        Optional<NguoiDung> nd = nguoiDungRepo.dangNhap(email.trim(), matKhau);
        if (nd.isEmpty()) {
            model.addAttribute("loi", "Email hoặc mật khẩu không đúng");
            return "login";
        }
        session.setAttribute("nguoiDung", nd.get()); // lưu người dùng vào session
        return trangSauDangNhap(nd.get());
    }

    @GetMapping("/logout")
    public String dangXuat(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /** USER -> /user, ADMIN -> /admin */
    private String trangSauDangNhap(NguoiDung nd) {
        return nd.laAdmin() ? "redirect:/admin" : "redirect:/user";
    }
}
