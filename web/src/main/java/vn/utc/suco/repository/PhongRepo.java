package vn.utc.suco.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.Phong;

/** Quản lý phòng (máy, học, văn phòng): dùng khi mở rộng khu vực hỗ trợ mà không cần sửa trực tiếp CSDL. */
@Repository
public class PhongRepo {
    private static final String SELECT = "SELECT MA_PHONG, TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG FROM PHONG ";

    private static final RowMapper<Phong> MAPPER = (rs, i) -> new Phong(
            rs.getLong("MA_PHONG"), rs.getString("TEN_PHONG"), rs.getString("TOA_NHA"),
            rs.getInt("TANG"), rs.getString("LOAI_PHONG"));

    private final JdbcTemplate jdbc;

    public PhongRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Phong> timTatCa() {
        return jdbc.query(SELECT + "ORDER BY TOA_NHA, TANG, TEN_PHONG", MAPPER);
    }

    public Optional<Phong> timTheoMa(Long maPhong) {
        return jdbc.query(SELECT + "WHERE MA_PHONG = ?", MAPPER, maPhong).stream().findFirst();
    }

    public void taoMoi(String tenPhong, String toaNha, int tang, String loaiPhong) {
        jdbc.update("INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES (?, ?, ?, ?)",
                tenPhong, toaNha, tang, loaiPhong);
    }

    public void capNhat(Long maPhong, String tenPhong, String toaNha, int tang, String loaiPhong) {
        jdbc.update("UPDATE PHONG SET TEN_PHONG = ?, TOA_NHA = ?, TANG = ?, LOAI_PHONG = ? WHERE MA_PHONG = ?",
                tenPhong, toaNha, tang, loaiPhong, maPhong);
    }

    /** Xóa phòng. Nếu còn thiết bị đặt trong phòng, Oracle từ chối (ORA-02292, khóa ngoại FK_THIET_BI_PHONG). */
    public void xoa(Long maPhong) {
        jdbc.update("DELETE FROM PHONG WHERE MA_PHONG = ?", maPhong);
    }
}
