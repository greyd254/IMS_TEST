package vn.utc.suco.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.utc.suco.model.Phong;
import vn.utc.suco.model.ThietBi;
import vn.utc.suco.util.ExcelThietBi;

@Repository
public class ThietBiRepo {
    private static final String SELECT =
            "SELECT tb.MA_TB, tb.TEN_TB, tb.LOAI_TB, tb.TINH_TRANG, tb.MA_PHONG, p.TEN_PHONG, "
                    + "tb.NHA_CUNG_CAP, tb.NGAY_LAP_DAT, tb.HDSD FROM THIET_BI tb "
                    + "JOIN PHONG p ON p.MA_PHONG = tb.MA_PHONG ";

    private static final RowMapper<ThietBi> MAPPER = (rs, i) -> new ThietBi(
            rs.getLong("MA_TB"), rs.getString("TEN_TB"), rs.getString("LOAI_TB"),
            rs.getString("TINH_TRANG"), rs.getLong("MA_PHONG"), rs.getString("TEN_PHONG"),
            rs.getString("NHA_CUNG_CAP"), toLd(rs.getDate("NGAY_LAP_DAT")), rs.getString("HDSD"));

    private static LocalDate toLd(Date d) {
        return d == null ? null : d.toLocalDate();
    }

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
                        rs.getObject("TANG", Integer.class), rs.getString("LOAI_PHONG")));
    }

    /**
     * Nhãn để chọn phòng trong file Excel nhập thiết bị -> mã phòng. Tên phòng có thể trùng giữa các tòa/tầng,
     * khi đó nhãn kèm mã ("Phòng 101 [#7]") để không ghi nhầm vào phòng khác.
     */
    public Map<String, Long> nhanPhong() {
        Map<String, Long> kq = new LinkedHashMap<>();
        jdbc.query("SELECT MA_PHONG, TEN_PHONG, COUNT(*) OVER (PARTITION BY UPPER(TRIM(TEN_PHONG))) AS SO_CUNG_TEN "
                        + "FROM PHONG ORDER BY TOA_NHA, TEN_PHONG",
                rs -> {
                    long ma = rs.getLong("MA_PHONG");
                    String ten = rs.getString("TEN_PHONG").trim();
                    kq.put(rs.getInt("SO_CUNG_TEN") > 1 ? ten + " [#" + ma + "]" : ten, ma);
                });
        return kq;
    }

    /** Khóa chống trùng của mọi thiết bị đã có: đọc một lần thay vì mỗi dòng trong file lại truy vấn CSDL. */
    public Set<String> khoaDaCo() {
        Set<String> kq = new HashSet<>();
        jdbc.query("SELECT MA_PHONG, LOAI_TB, TEN_TB FROM THIET_BI",
                rs -> {
                    kq.add(ExcelThietBi.khoa(rs.getLong("MA_PHONG"), rs.getString("LOAI_TB"), rs.getString("TEN_TB")));
                });
        return kq;
    }

    /** Thêm nhiều thiết bị bằng một lệnh batch; @Transactional để lỗi giữa chừng thì không dòng nào được ghi. */
    @Transactional
    public void taoNhieu(List<ExcelThietBi.Dong> ds) {
        jdbc.batchUpdate("INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT, HDSD) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                ds, 200, (ps, d) -> {
                    ps.setString(1, d.getTenTb());
                    ps.setString(2, d.getLoaiTb());
                    ps.setLong(3, d.getMaPhong());
                    ps.setString(4, d.getTinhTrang());
                    ps.setString(5, d.getNhaCungCap());
                    ps.setDate(6, d.getNgayLapDat() == null ? null : Date.valueOf(d.getNgayLapDat()));
                    ps.setString(7, d.getHdsd());
                });
    }

    public void taoMoi(String tenTb, String loaiTb, Long maPhong, String tinhTrang, String nhaCungCap,
                       LocalDate ngayLapDat, String hdsd) {
        jdbc.update("INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT, HDSD) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                tenTb, loaiTb, maPhong, tinhTrang, nhaCungCap, ngayLapDat == null ? null : Date.valueOf(ngayLapDat),
                hdsd);
    }

    public void capNhat(Long maTb, String tenTb, String loaiTb, Long maPhong, String tinhTrang, String nhaCungCap,
                        LocalDate ngayLapDat, String hdsd) {
        jdbc.update("UPDATE THIET_BI SET TEN_TB = ?, LOAI_TB = ?, MA_PHONG = ?, TINH_TRANG = ?, NHA_CUNG_CAP = ?, "
                        + "NGAY_LAP_DAT = ?, HDSD = ? WHERE MA_TB = ?",
                tenTb, loaiTb, maPhong, tinhTrang, nhaCungCap, ngayLapDat == null ? null : Date.valueOf(ngayLapDat),
                hdsd, maTb);
    }

    /** Xóa thiết bị. Nếu đã có sự cố tham chiếu, Oracle từ chối (ORA-02292, khóa ngoại FK_SU_CO_THIET_BI). */
    public void xoa(Long maTb) {
        jdbc.update("DELETE FROM THIET_BI WHERE MA_TB = ?", maTb);
    }
}
