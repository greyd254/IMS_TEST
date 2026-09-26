package vn.utc.suco.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.utc.suco.model.NguoiDung;

/**
 * Chặn truy cập khi chưa đăng nhập (không dùng Spring Security):
 * - Chưa có "nguoiDung" trong session -> chuyển về /login.
 * - Vào /admin/** nhưng không phải ADMIN -> chuyển về /user.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        NguoiDung nd = session == null ? null : (NguoiDung) session.getAttribute("nguoiDung");
        if (nd == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        if (req.getRequestURI().startsWith(req.getContextPath() + "/admin") && !nd.laAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/user");
            return false;
        }
        return true;
    }
}
