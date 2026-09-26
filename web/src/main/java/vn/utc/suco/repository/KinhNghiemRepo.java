package vn.utc.suco.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.BaiKinhNghiem;

@Repository
public class KinhNghiemRepo {
    private static final String SELECT =
            "SELECT b.MA_BAI, b.TIEU_DE, b.MA_DM, dm.TEN_DM, b.TRIEU_CHUNG, b.NGUYEN_NHAN, b.GIAI_PHAP, "
                    + "nd.HO_TEN, b.NGAY_TAO "
                    + "FROM BAI_KINH_NGHIEM b "
                    + "JOIN DANH_MUC_SU_CO dm ON dm.MA_DM = b.MA_DM "
                    + "JOIN NGUOI_DUNG nd ON nd.MA_ND = b.MA_NGUOI_VIET ";

    private static final RowMapper<BaiKinhNghiem> MAPPER = (rs, i) -> {
        Timestamp t = rs.getTimestamp("NGAY_TAO");
        return new BaiKinhNghiem(rs.getLong("MA_BAI"), rs.getString("TIEU_DE"), rs.getLong("MA_DM"),
                rs.getString("TEN_DM"), rs.getString("TRIEU_CHUNG"), rs.getString("NGUYEN_NHAN"),
                rs.getString("GIAI_PHAP"), rs.getString("HO_TEN"), t == null ? null : t.toLocalDateTime());
    };

    private final JdbcTemplate jdbc;

    public KinhNghiemRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Danh sách bài kinh nghiệm; maDm = null nghĩa là lấy tất cả danh mục. */
    public List<BaiKinhNghiem> timTheoDanhMuc(Long maDm) {
        if (maDm == null) {
            return jdbc.query(SELECT + "ORDER BY b.NGAY_TAO DESC, b.MA_BAI DESC", MAPPER);
        }
        return jdbc.query(SELECT + "WHERE b.MA_DM = ? ORDER BY b.NGAY_TAO DESC, b.MA_BAI DESC", MAPPER, maDm);
    }

    public Optional<BaiKinhNghiem> timTheoMa(Long maBai) {
        return jdbc.query(SELECT + "WHERE b.MA_BAI = ?", MAPPER, maBai).stream().findFirst();
    }
}
