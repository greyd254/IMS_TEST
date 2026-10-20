package vn.utc.suco.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.PhanHoi;

@Repository
public class PhanHoiRepo {
    private static final RowMapper<PhanHoi> MAPPER = (rs, i) -> new PhanHoi(
            rs.getLong("MA_PH"), rs.getLong("MA_SC"), rs.getLong("MA_NGUOI_PHAN_HOI"),
            rs.getInt("MUC_HAI_LONG"), rs.getString("NOI_DUNG"), toLd(rs.getDate("NGAY_PHAN_HOI")));

    private static LocalDate toLd(Date d) {
        return d == null ? null : d.toLocalDate();
    }

    private final JdbcTemplate jdbc;

    public PhanHoiRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Phản hồi của một sự cố, nếu đã có (UQ_PHAN_HOI_SU_CO: mỗi sự cố chỉ có 1 phản hồi). */
    public Optional<PhanHoi> timTheoSuCo(Long maSc) {
        return jdbc.query("SELECT MA_PH, MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG, NGAY_PHAN_HOI "
                        + "FROM PHAN_HOI WHERE MA_SC = ?", MAPPER, maSc)
                .stream().findFirst();
    }

    /** Lưu phản hồi mới. Gửi phản hồi lần 2 cho cùng sự cố bị Oracle từ chối (UQ_PHAN_HOI_SU_CO). */
    public void taoMoi(Long maSc, Long maNguoiPhanHoi, int mucHaiLong, String noiDung) {
        jdbc.update("INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (?, ?, ?, ?)",
                maSc, maNguoiPhanHoi, mucHaiLong, noiDung);
    }
}
