package vn.utc.suco.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.support.RequestContextUtils;

/** File tải lên vượt giới hạn: quay lại trang trước kèm thông báo, thay vì hiện trang lỗi của Spring. */
@ControllerAdvice
public class XuLyFileQuaLon {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fileQuaLon(HttpServletRequest request) {
        RequestContextUtils.getOutputFlashMap(request).put("loi", "File quá lớn, chỉ được tải lên file tối đa 5MB");
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
