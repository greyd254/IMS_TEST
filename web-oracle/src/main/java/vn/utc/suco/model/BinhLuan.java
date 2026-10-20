package vn.utc.suco.model;

import java.time.LocalDateTime;

/** Một tin nhắn trao đổi qua lại giữa người báo và admin trên một sự cố (bảng BINH_LUAN_SU_CO). */
public record BinhLuan(Long maBl, Long maSc, Long maNguoiGui, String tenNguoiGui, String vaiTroNguoiGui,
                       String noiDung, String tepDinhKem, LocalDateTime thoiGian) {
}
