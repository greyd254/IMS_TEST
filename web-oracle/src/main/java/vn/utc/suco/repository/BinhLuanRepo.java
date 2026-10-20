package vn.utc.suco.repository;

import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.BinhLuan;

@Repository
public class BinhLuanRepo {
    private static final RowMapper<BinhLuan> MAPPER = (rs, i) -> new BinhLuan(
            rs.getLong("MA_BL"), rs.getLong("MA_SC"), rs.getLong("MA_NGUOI_GUI"),
            rs.getString("TEN_NGUOI_GUI"), rs.getString("VAI_TRO_NGUOI_GUI"),
            rs.getString("NOI_DUNG"), rs.getString("TEP_DINH_KEM"),
            rs.getTimestamp("THOI_GIAN").toLocalDateTime());

    private final JdbcTemplate jdbc;

    public BinhLuanRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Trao đổi của một sự cố, mới nhất lên trước. */
    public List<BinhLuan> timTheoSuCo(Long maSc) {
        return jdbc.query("SELECT bl.MA_BL, bl.MA_SC, bl.MA_NGUOI_GUI, nd.HO_TEN AS TEN_NGUOI_GUI, "
                        + "nd.VAI_TRO AS VAI_TRO_NGUOI_GUI, bl.NOI_DUNG, bl.TEP_DINH_KEM, bl.THOI_GIAN "
                        + "FROM BINH_LUAN_SU_CO bl JOIN NGUOI_DUNG nd ON nd.MA_ND = bl.MA_NGUOI_GUI "
                        + "WHERE bl.MA_SC = ? ORDER BY bl.THOI_GIAN DESC, bl.MA_BL DESC",
                MAPPER, maSc);
    }

    /** Gửi 1 tin nhắn mới; noiDung hoặc tepDinhKem phải có ít nhất 1 cái (CK_BINH_LUAN_CO_NOI_DUNG). */
    public void taoMoi(Long maSc, Long maNguoiGui, String noiDung, String tepDinhKem) {
        jdbc.update("INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, TEP_DINH_KEM) VALUES (?, ?, ?, ?)",
                new Object[]{maSc, maNguoiGui, noiDung, tepDinhKem},
                new int[]{Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.VARCHAR});
    }
}
