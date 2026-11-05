package vn.utc.suco.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.Phong;
import vn.utc.suco.model.Trang;
import vn.utc.suco.util.TimKiem;

/** Quản lý phòng (máy, học, văn phòng): dùng khi mở rộng khu vực hỗ trợ mà không cần sửa trực tiếp CSDL. */
@Repository
public class PhongRepo {
    private static final String SELECT = "SELECT MA_PHONG, TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG FROM PHONG ";

    private static final RowMapper<Phong> MAPPER = (rs, i) -> new Phong(
            rs.getLong("MA_PHONG"), rs.getString("TEN_PHONG"), rs.getString("TOA_NHA"),
            rs.getObject("TANG", Integer.class), rs.getString("LOAI_PHONG"));

    private final JdbcTemplate jdbc;

    public PhongRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Một trang phòng (trang số so, đếm từ 1); q = null nghĩa là không lọc theo từ khóa. */
    public List<Phong> timTrang(String q, int so) {
        List<Object> thamSo = new ArrayList<>();
        String where = dieuKienLoc(q, thamSo);
        thamSo.add(Trang.viTriDau(so));
        thamSo.add(Trang.KICH_THUOC);
        return jdbc.query(SELECT + where + "ORDER BY TOA_NHA, TANG, TEN_PHONG, MA_PHONG OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                MAPPER, thamSo.toArray());
    }

    /** Tổng số phòng khớp từ khóa (để tính số trang). */
    public long dem(String q) {
        List<Object> thamSo = new ArrayList<>();
        Long tong = jdbc.queryForObject("SELECT COUNT(*) FROM PHONG " + dieuKienLoc(q, thamSo), Long.class,
                thamSo.toArray());
        return tong == null ? 0 : tong;
    }

    /** Tìm theo tên phòng, tòa nhà, loại phòng (mã PHONG_MAY đổi "_" thành khoảng trắng). */
    private static String dieuKienLoc(String q, List<Object> thamSo) {
        return q == null ? "" : "WHERE " + TimKiem.dieuKien(q, thamSo, "TEN_PHONG", "TOA_NHA",
                "REPLACE(LOAI_PHONG, '_', ' ')") + " ";
    }

    public Optional<Phong> timTheoMa(Long maPhong) {
        return jdbc.query(SELECT + "WHERE MA_PHONG = ?", MAPPER, maPhong).stream().findFirst();
    }

    /**
     * Đã có phòng khác cùng tòa, cùng tầng, cùng tên (không phân biệt hoa thường, bỏ khoảng trắng hai đầu)?
     * Tầng để trống được coi là một giá trị (hai phòng cùng không ghi tầng vẫn tính là cùng tầng).
     * boQuaMaPhong = phòng đang sửa (null khi thêm mới) để không tự so với chính nó.
     */
    public boolean tonTaiTrung(String tenPhong, String toaNha, Integer tang, Long boQuaMaPhong) {
        String sql = "SELECT COUNT(*) FROM PHONG WHERE UPPER(TRIM(TOA_NHA)) = UPPER(TRIM(?)) "
                + "AND UPPER(TRIM(TEN_PHONG)) = UPPER(TRIM(?)) AND MA_PHONG <> ? ";
        long boQua = boQuaMaPhong == null ? -1L : boQuaMaPhong;
        Integer dem = tang == null
                ? jdbc.queryForObject(sql + "AND TANG IS NULL", Integer.class, toaNha, tenPhong, boQua)
                : jdbc.queryForObject(sql + "AND TANG = ?", Integer.class, toaNha, tenPhong, boQua, tang);
        return dem != null && dem > 0;
    }

    public void taoMoi(String tenPhong, String toaNha, Integer tang, String loaiPhong) {
        jdbc.update("INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES (?, ?, ?, ?)",
                tenPhong, toaNha, tang, loaiPhong);
    }

    public void capNhat(Long maPhong, String tenPhong, String toaNha, Integer tang, String loaiPhong) {
        jdbc.update("UPDATE PHONG SET TEN_PHONG = ?, TOA_NHA = ?, TANG = ?, LOAI_PHONG = ? WHERE MA_PHONG = ?",
                tenPhong, toaNha, tang, loaiPhong, maPhong);
    }

    /** Xóa phòng. Nếu còn thiết bị đặt trong phòng, Oracle từ chối (ORA-02292, khóa ngoại FK_THIET_BI_PHONG). */
    public void xoa(Long maPhong) {
        jdbc.update("DELETE FROM PHONG WHERE MA_PHONG = ?", maPhong);
    }
}
