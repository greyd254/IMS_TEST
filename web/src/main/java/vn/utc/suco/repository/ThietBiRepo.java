package vn.utc.suco.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.Phong;
import vn.utc.suco.model.ThietBi;

@Repository
public class ThietBiRepo {
    private static final String SELECT =
            "SELECT tb.MA_TB, tb.TEN_TB, tb.LOAI_TB, tb.TINH_TRANG, tb.MA_PHONG, p.TEN_PHONG FROM THIET_BI tb "
                    + "JOIN PHONG p ON p.MA_PHONG = tb.MA_PHONG ";

    private static final RowMapper<ThietBi> MAPPER = (rs, i) -> new ThietBi(
            rs.getLong("MA_TB"), rs.getString("TEN_TB"), rs.getString("LOAI_TB"),
            rs.getString("TINH_TRANG"), rs.getLong("MA_PHONG"), rs.getString("TEN_PHONG"));

    private final JdbcTemplate jdbc;

    public ThietBiRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Thiết bị kèm tên phòng để chọn trong dropdown và hiển thị danh sách. */
    public List<ThietBi> timTatCa() {
        return jdbc.query(SELECT + "ORDER BY p.TOA_NHA, p.TEN_PHONG, tb.TEN_TB", MAPPER);
    }

    public Optional<ThietBi> timTheoMa(Long maTb) {
        return jdbc.query(SELECT + "WHERE tb.MA_TB = ?", MAPPER, maTb).stream().findFirst();
    }

    public List<Phong> timTatCaPhong() {
        return jdbc.query("SELECT MA_PHONG, TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG FROM PHONG ORDER BY TOA_NHA, TEN_PHONG",
                (rs, i) -> new Phong(rs.getLong("MA_PHONG"), rs.getString("TEN_PHONG"), rs.getString("TOA_NHA"),
                        rs.getInt("TANG"), rs.getString("LOAI_PHONG")));
    }

    public void taoMoi(String tenTb, String loaiTb, Long maPhong, String tinhTrang) {
        jdbc.update("INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES (?, ?, ?, ?)",
                tenTb, loaiTb, maPhong, tinhTrang);
    }

    public void capNhat(Long maTb, String tenTb, String loaiTb, Long maPhong, String tinhTrang) {
        jdbc.update("UPDATE THIET_BI SET TEN_TB = ?, LOAI_TB = ?, MA_PHONG = ?, TINH_TRANG = ? WHERE MA_TB = ?",
                tenTb, loaiTb, maPhong, tinhTrang, maTb);
    }

    /** Xóa thiết bị. Nếu đã có sự cố tham chiếu, Oracle từ chối (ORA-02292, khóa ngoại FK_SU_CO_THIET_BI). */
    public void xoa(Long maTb) {
        jdbc.update("DELETE FROM THIET_BI WHERE MA_TB = ?", maTb);
    }
}
