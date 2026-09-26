package vn.utc.suco.model;

/** Người dùng đăng nhập (không chứa mật khẩu để không lưu mật khẩu vào session). */
public record NguoiDung(Long maNd, String hoTen, String email, String donVi, String vaiTro) {
    public boolean laAdmin() {
        return "ADMIN".equals(vaiTro);
    }
}
