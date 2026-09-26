package vn.utc.suco.model;

/** Thiết bị kèm tên phòng: dùng cho dropdown chọn thiết bị và cho trang quản lý thiết bị của admin. */
public record ThietBi(Long maTb, String tenTb, String loaiTb, String tinhTrang, Long maPhong, String tenPhong) {
}
