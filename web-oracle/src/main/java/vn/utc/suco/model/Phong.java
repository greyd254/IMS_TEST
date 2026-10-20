package vn.utc.suco.model;

/** Phòng (máy, học, văn phòng). Dùng cho quản lý phòng và dropdown chọn phòng khi thêm/sửa thiết bị. */
public record Phong(Long maPhong, String tenPhong, String toaNha, Integer tang, String loaiPhong) {
}
