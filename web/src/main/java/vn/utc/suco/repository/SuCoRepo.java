package vn.utc.suco.repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.utc.suco.model.LichSu;
import vn.utc.suco.model.SuCo;

@Repository
public class SuCoRepo {
    // Đọc từ view V_SU_CO_CHI_TIET (đã JOIN sẵn, xem sql/05_queries.sql)
    private static final String SELECT =
            "SELECT MA_SC, TIEU_DE, MO_TA, MA_NGUOI_BAO, TEN_NGUOI_BAO, TEN_ADMIN, MA_TB, TEN_TB, TEN_PHONG, "
                    + "MA_DM, TEN_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, "
                    + "NGAY_TAO, NGAY_HOAN_THANH FROM V_SU_CO_CHI_TIET ";

    private static final RowMapper<SuCo> MAPPER = (rs, i) -> new SuCo(
            rs.getLong("MA_SC"), rs.getString("TIEU_DE"), rs.getString("MO_TA"),
            rs.getLong("MA_NGUOI_BAO"), rs.getString("TEN_NGUOI_BAO"), rs.getString("TEN_ADMIN"),
            rs.getObject("MA_TB", Long.class), rs.getString("TEN_TB"), rs.getString("TEN_PHONG"),
            rs.getLong("MA_DM"), rs.getString("TEN_DM"), rs.getString("MUC_UU_TIEN"), rs.getString("TRANG_THAI"),
            rs.getString("NGUYEN_NHAN"), rs.getString("CACH_KHAC_PHUC"), rs.getObject("MA_BAI_KN", Long.class),
            toLdt(rs.getTimestamp("NGAY_TAO")), toLdt(rs.getTimestamp("NGAY_HOAN_THANH")));

    private static LocalDateTime toLdt(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }

    private final JdbcTemplate jdbc;

    public SuCoRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Người dùng báo sự cố mới. Trạng thái mặc định 'MOI', ngày tạo mặc định SYSDATE (do Oracle gán). */
    public void taoMoi(String tieuDe, String moTa, Long maNguoiBao, Long maTb, Long maDm, String mucUuTien) {
        jdbc.update("INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_TB, MA_DM, MUC_UU_TIEN) VALUES (?, ?, ?, ?, ?, ?)",
                new Object[]{tieuDe, moTa, maNguoiBao, maTb, maDm, mucUuTien},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR});
    }

    /** Sự cố do chính người dùng này báo (màn hình /user). */
    public List<SuCo> timTheoNguoiBao(Long maNd) {
        return jdbc.query(SELECT + "WHERE MA_NGUOI_BAO = ? ORDER BY NGAY_TAO DESC, MA_SC DESC", MAPPER, maNd);
    }

    /** Tất cả sự cố cho admin; trangThai = null nghĩa là không lọc. */
    public List<SuCo> timTatCa(String trangThai) {
        if (trangThai == null) {
            return jdbc.query(SELECT + "ORDER BY NGAY_TAO DESC, MA_SC DESC", MAPPER);
        }
        return jdbc.query(SELECT + "WHERE TRANG_THAI = ? ORDER BY NGAY_TAO DESC, MA_SC DESC", MAPPER, trangThai);
    }

    public Optional<SuCo> timTheoMa(Long maSc) {
        return jdbc.query(SELECT + "WHERE MA_SC = ?", MAPPER, maSc).stream().findFirst();
    }

    /**
     * Admin cập nhật sự cố. Gán MA_ADMIN_XU_LY = admin đang đăng nhập:
     * trigger TRG_SU_CO_LICH_SU dùng cột này làm "người cập nhật" khi ghi lịch sử,
     * còn TRG_SU_CO_HOAN_THANH tự gán NGAY_HOAN_THANH khi chuyển sang HOAN_THANH.
     */
    public void capNhat(Long maSc, String trangThai, String nguyenNhan, String cachKhacPhuc,
                        Long maBaiKn, Long maAdmin) {
        jdbc.update("UPDATE SU_CO SET TRANG_THAI = ?, NGUYEN_NHAN = ?, CACH_KHAC_PHUC = ?, "
                        + "MA_BAI_KN = ?, MA_ADMIN_XU_LY = ? WHERE MA_SC = ?",
                new Object[]{trangThai, nguyenNhan, cachKhacPhuc, maBaiKn, maAdmin, maSc},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC});
    }

    /**
     * Xóa sự cố. Phải xóa LICH_SU_XU_LY (bảng con, có khóa ngoại tới SU_CO) trước rồi mới xóa SU_CO.
     * @Transactional để 2 lệnh DELETE cùng thành công hoặc cùng hoàn tác (Oracle COMMIT/ROLLBACK).
     */
    @Transactional
    public void xoa(Long maSc) {
        jdbc.update("DELETE FROM LICH_SU_XU_LY WHERE MA_SC = ?", maSc);
        jdbc.update("DELETE FROM SU_CO WHERE MA_SC = ?", maSc);
    }

    /** Lịch sử xử lý của một sự cố, cũ trước mới sau. */
    public List<LichSu> timLichSu(Long maSc) {
        return jdbc.query(
                "SELECT ls.THOI_GIAN, nd.HO_TEN, ls.TRANG_THAI_CU, ls.TRANG_THAI_MOI, ls.GHI_CHU "
                        + "FROM LICH_SU_XU_LY ls LEFT JOIN NGUOI_DUNG nd ON nd.MA_ND = ls.MA_NGUOI_CAP_NHAT "
                        + "WHERE ls.MA_SC = ? ORDER BY ls.THOI_GIAN, ls.MA_LS",
                (rs, i) -> new LichSu(toLdt(rs.getTimestamp("THOI_GIAN")), rs.getString("HO_TEN"),
                        rs.getString("TRANG_THAI_CU"), rs.getString("TRANG_THAI_MOI"), rs.getString("GHI_CHU")),
                maSc);
    }
}
