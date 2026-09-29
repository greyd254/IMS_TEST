package vn.utc.suco.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.util.MatKhau;

@Repository
public class NguoiDungRepo {
    // Không SELECT cột MAT_KHAU: mật khẩu không bao giờ được đưa ra giao diện
    private static final String SELECT = "SELECT MA_ND, HO_TEN, EMAIL, DON_VI, VAI_TRO FROM NGUOI_DUNG ";

    private static final RowMapper<NguoiDung> MAPPER = (rs, i) -> new NguoiDung(
            rs.getLong("MA_ND"), rs.getString("HO_TEN"), rs.getString("EMAIL"),
            rs.getString("DON_VI"), rs.getString("VAI_TRO"));

    private final JdbcTemplate jdbc;

    public NguoiDungRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Đăng nhập: tìm theo email (không phân biệt hoa thường), rồi băm mật khẩu nhập vào và so với chuỗi băm đã lưu.
     * Dùng ? để tránh SQL injection.
     */
    public Optional<NguoiDung> dangNhap(String email, String matKhau) {
        return jdbc.query("SELECT MA_ND, HO_TEN, EMAIL, DON_VI, VAI_TRO, MAT_KHAU FROM NGUOI_DUNG WHERE LOWER(EMAIL) = ?",
                        (rs, i) -> MatKhau.khop(matKhau, rs.getString("MAT_KHAU")) ? MAPPER.mapRow(rs, i) : null,
                        email.toLowerCase())
                .stream().filter(java.util.Objects::nonNull).findFirst();
    }

    public List<NguoiDung> timTatCa() {
        return jdbc.query(SELECT + "ORDER BY VAI_TRO, HO_TEN", MAPPER);
    }

    public Optional<NguoiDung> timTheoMa(Long maNd) {
        return jdbc.query(SELECT + "WHERE MA_ND = ?", MAPPER, maNd).stream().findFirst();
    }

    /** Thêm người dùng (mật khẩu được băm trước khi lưu). Email trùng bị Oracle từ chối (ràng buộc UQ_NGUOI_DUNG_EMAIL). */
    public void taoMoi(String hoTen, String email, String matKhau, String donVi, String vaiTro) {
        jdbc.update("INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES (?, ?, ?, ?, ?)",
                hoTen, email, MatKhau.bam(matKhau), donVi, vaiTro);
    }

    /** Sửa thông tin; matKhau rỗng nghĩa là giữ nguyên mật khẩu cũ. */
    public void capNhat(Long maNd, String hoTen, String email, String donVi, String vaiTro, String matKhau) {
        if (matKhau == null || matKhau.isBlank()) {
            jdbc.update("UPDATE NGUOI_DUNG SET HO_TEN = ?, EMAIL = ?, DON_VI = ?, VAI_TRO = ? WHERE MA_ND = ?",
                    hoTen, email, donVi, vaiTro, maNd);
        } else {
            jdbc.update("UPDATE NGUOI_DUNG SET HO_TEN = ?, EMAIL = ?, DON_VI = ?, VAI_TRO = ?, MAT_KHAU = ? WHERE MA_ND = ?",
                    hoTen, email, donVi, vaiTro, MatKhau.bam(matKhau), maNd);
        }
    }

    /** Xóa người dùng. Nếu đã báo sự cố / viết bài / có lịch sử thì Oracle từ chối (khóa ngoại, ORA-02292). */
    public void xoa(Long maNd) {
        jdbc.update("DELETE FROM NGUOI_DUNG WHERE MA_ND = ?", maNd);
    }
}
