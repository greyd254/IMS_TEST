package vn.utc.suco.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Bắt lỗi dữ liệu gửi lên sai kiểu (VD ô số nhận chữ, mã trên đường dẫn không phải số) hoặc thiếu ô bắt buộc
 * cho mọi controller: quay lại trang trước kèm thông báo, thay vì hiện trang lỗi 400 của Spring.
 */
@ControllerAdvice
public class XuLyLoiChung {

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public String duLieuKhongHopLe(HttpServletRequest request) {
        // @ExceptionHandler không nhận RedirectAttributes, nên ghi thẳng vào flash map của request
        RequestContextUtils.getOutputFlashMap(request).put("loi", "Dữ liệu nhập không hợp lệ, vui lòng kiểm tra lại");
        return "redirect:" + trangTruoc(request.getHeader("Referer"));
    }

    /** Chỉ lấy phần đường dẫn của Referer, để không bị chuyển hướng sang trang web khác. */
    private static String trangTruoc(String referer) {
        try {
            URI uri = URI.create(referer);
            if (uri.getRawPath() != null && uri.getRawPath().startsWith("/")) {
                return uri.getRawPath() + (uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "");
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            // Không có hoặc sai Referer: về trang chủ
        }
        return "/";
    }
}
