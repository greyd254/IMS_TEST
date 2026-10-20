package vn.utc.suco.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.model.TrangThaiNguoiDung;
import vn.utc.suco.model.VaiTro;
import vn.utc.suco.util.MatKhau;

@Repository
public class NguoiDungRepo {
    // Không SELECT cột MAT_KHAU: mật khẩu không bao giờ được đưa ra giao diện
    private static final String SELECT =
            "SELECT MA_ND, HO_TEN, EMAIL, DON_VI, VAI_TRO, SDT, TRANG_THAI FROM NGUOI_DUNG ";

    private static final RowMapper<NguoiDung> MAPPER = (rs, i) -> new NguoiDung(
            rs.getLong("MA_ND"), rs.getString("HO_TEN"), rs.getString("EMAIL"), rs.getString("DON_VI"),
            VaiTro.valueOf(rs.getString("VAI_TRO")), rs.getString("SDT"),
            TrangThaiNguoiDung.valueOf(rs.getString("TRANG_THAI")));

    private final JdbcTemplate jdbc;

    public NguoiDungRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Đăng nhập: tìm theo email (không phân biệt hoa thường) trong số tài khoản còn HOAT_DONG,
     * rồi băm mật khẩu nhập vào và so với chuỗi băm đã lưu. Dùng ? để tránh SQL injection.
     */
    public Optional<NguoiDung> dangNhap(String email, String matKhau) {
        return jdbc.query("SELECT MA_ND, HO_TEN, EMAIL, DON_VI, VAI_TRO, SDT, TRANG_THAI, MAT_KHAU FROM NGUOI_DUNG "
                        + "WHERE LOWER(EMAIL) = ? AND TRANG_THAI = 'HOAT_DONG'",
                        (rs, i) -> MatKhau.khop(matKhau, rs.getString("MAT_KHAU")) ? MAPPER.mapRow(rs, i) : null,
                        email.toLowerCase())
                .stream().filter(java.util.Objects::nonNull).findFirst();
    }

    public List<NguoiDung> timTatCa() {
        return jdbc.query(SELECT + "ORDER BY TRANG_THAI, VAI_TRO, HO_TEN", MAPPER);
    }

    public Optional<NguoiDung> timTheoMa(Long maNd) {
        return jdbc.query(SELECT + "WHERE MA_ND = ?", MAPPER, maNd).stream().findFirst();
    }

    /**
     * Thêm người dùng (mật khẩu được băm trước khi lưu). Email trùng bị Oracle từ chối (ràng buộc UQ_NGUOI_DUNG_EMAIL).
     * maNguoiTao = admin đang đăng nhập khi tạo tài khoản này (audit).
     */
    public void taoMoi(String hoTen, String email, String matKhau, String donVi, String vaiTro, String sdt,
                       Long maNguoiTao) {
        jdbc.update("INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                hoTen, email, MatKhau.bam(matKhau), donVi, vaiTro, sdt, maNguoiTao);
    }

    /** Sửa thông tin; matKhau rỗng nghĩa là giữ nguyên mật khẩu cũ. */
    public void capNhat(Long maNd, String hoTen, String email, String donVi, String vaiTro, String sdt,
                        String matKhau) {
        if (matKhau == null || matKhau.isBlank()) {
            jdbc.update("UPDATE NGUOI_DUNG SET HO_TEN = ?, EMAIL = ?, DON_VI = ?, VAI_TRO = ?, SDT = ? WHERE MA_ND = ?",
                    hoTen, email, donVi, vaiTro, sdt, maNd);
        } else {
            jdbc.update("UPDATE NGUOI_DUNG SET HO_TEN = ?, EMAIL = ?, DON_VI = ?, VAI_TRO = ?, SDT = ?, MAT_KHAU = ? "
                            + "WHERE MA_ND = ?",
                    hoTen, email, donVi, vaiTro, sdt, MatKhau.bam(matKhau), maNd);
        }
    }

    /**
     * Xóa mềm: chuyển TRANG_THAI sang KHONG_HOAT_DONG và ghi lại ai xóa, lúc nào (không DELETE).
     * Giữ nguyên được sự cố / bài kinh nghiệm / lịch sử đã gắn với người này, và có thể kích hoạt lại.
     * maNguoiXoa = admin đang đăng nhập khi thực hiện thao tác.
     */
    public void voHieuHoa(Long maNd, Long maNguoiXoa) {
        jdbc.update("UPDATE NGUOI_DUNG SET TRANG_THAI = 'KHONG_HOAT_DONG', NGAY_XOA = SYSDATE, MA_NGUOI_XOA = ? "
                        + "WHERE MA_ND = ?",
                maNguoiXoa, maNd);
    }

    /** Kích hoạt lại tài khoản đã bị vô hiệu hóa. */
    public void kichHoatLai(Long maNd) {
        jdbc.update("UPDATE NGUOI_DUNG SET TRANG_THAI = 'HOAT_DONG', NGAY_XOA = NULL, MA_NGUOI_XOA = NULL "
                        + "WHERE MA_ND = ?",
                maNd);
    }
}
