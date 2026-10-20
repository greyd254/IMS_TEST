package vn.utc.suco.model;

/** Người dùng đăng nhập (không chứa mật khẩu để không lưu mật khẩu vào session). */
public record NguoiDung(Long maNd, String hoTen, String email, String donVi, String vaiTro, String sdt,
                        String trangThai) {
    public boolean laAdmin() {
        return "ADMIN".equals(vaiTro);
    }

    public boolean dangHoatDong() {
        return "HOAT_DONG".equals(trangThai);
    }
}
