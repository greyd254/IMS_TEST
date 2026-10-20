package vn.utc.suco.model;

import java.time.LocalDateTime;

/** Một dòng lịch sử đổi trạng thái (bảng LICH_SU_XU_LY, do trigger Oracle ghi). */
public record LichSu(LocalDateTime thoiGian, String tenNguoiCapNhat,
                     String trangThaiCu, String trangThaiMoi, String ghiChu) {
}
