package vn.utc.suco.model;

import java.time.LocalDateTime;

/** Một dòng của view V_SU_CO_CHI_TIET (sự cố kèm tên người báo, admin, thiết bị, phòng, danh mục). */
public record SuCo(Long maSc, String tieuDe, String moTa,
                   Long maNguoiBao, String tenNguoiBao, String tenAdmin,
                   Long maTb, String tenTb, String tenPhong,
                   Long maDm, String tenDm,
                   String mucUuTien, String trangThai,
                   String nguyenNhan, String cachKhacPhuc, Long maBaiKn,
                   LocalDateTime ngayTao, LocalDateTime ngayHoanThanh) {
}
