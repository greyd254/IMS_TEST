package vn.utc.suco.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/** Một tin nhắn trao đổi qua lại giữa người báo và admin trên một sự cố (bảng BINH_LUAN_SU_CO). */
public record BinhLuan(Long maBl, Long maSc, Long maNguoiGui, String tenNguoiGui, String vaiTroNguoiGui,
                       String noiDung, String tepDinhKem, LocalDateTime thoiGian) {

    private static final DateTimeFormatter DINH_DANG_LUC = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Dữ liệu rút gọn để trả JSON cho polling AJAX (thay vì serialize LocalDateTime trực tiếp, Jackson sẽ
     * biến nó thành mảng số [năm,tháng,ngày,giờ,phút] gây khó cho JS đọc).
     */
    public Map<String, Object> dangJson() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("maBl", maBl);
        m.put("tenNguoiGui", tenNguoiGui);
        m.put("laAdmin", "ADMIN".equals(vaiTroNguoiGui));
        m.put("noiDung", noiDung);
        m.put("tepDinhKem", tepDinhKem);
        m.put("laAnh", tepDinhKem != null && tepDinhKem.matches("(?i).*\\.(jpg|jpeg|png|gif|webp)$"));
        m.put("thoiGian", thoiGian.format(DINH_DANG_LUC));
        return m;
    }
}
