package vn.utc.suco.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
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
import vn.utc.suco.model.TinhTrangThietBi;
import vn.utc.suco.model.Trang;
import vn.utc.suco.util.ExcelThietBi;
import vn.utc.suco.util.TimKiem;

@Repository
public class ThietBiRepo {
    private static final String SELECT =
            "SELECT tb.MA_TB, tb.TEN_TB, tb.LOAI_TB, tb.TINH_TRANG, tb.MA_PHONG, p.TEN_PHONG, "
                    + "tb.NHA_CUNG_CAP, tb.NGAY_LAP_DAT, tb.HDSD FROM THIET_BI tb "
                    + "JOIN PHONG p ON p.MA_PHONG = tb.MA_PHONG ";

    private static final RowMapper<ThietBi> MAPPER = (rs, i) -> new ThietBi(
            rs.getLong("MA_TB"), rs.getString("TEN_TB"), rs.getString("LOAI_TB"),
            TinhTrangThietBi.valueOf(rs.getString("TINH_TRANG")), rs.getLong("MA_PHONG"), rs.getString("TEN_PHONG"),
            rs.getString("NHA_CUNG_CAP"), toLd(rs.getDate("NGAY_LAP_DAT")), rs.getString("HDSD"));

    private static LocalDate toLd(Date d) {
        return d == null ? null : d.toLocalDate();
    }

    private final JdbcTemplate jdbc;

    public ThietBiRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Tất cả thiết bị kèm tên phòng, cho dropdown chọn thiết bị khi báo sự cố. */
    public List<ThietBi> timTatCa() {
        return jdbc.query(SELECT + "ORDER BY p.TOA_NHA, p.TEN_PHONG, tb.TEN_TB", MAPPER);
    }

    /** Một trang thiết bị (trang số so, đếm từ 1) cho màn quản lý; q = null nghĩa là không lọc theo từ khóa. */
    public List<ThietBi> timTrang(String q, int so) {
        List<Object> thamSo = new ArrayList<>();
        String where = dieuKienLoc(q, thamSo);
        thamSo.add(Trang.viTriDau(so));
        thamSo.add(Trang.KICH_THUOC);
        return jdbc.query(SELECT + where + "ORDER BY p.TOA_NHA, p.TEN_PHONG, tb.TEN_TB, tb.MA_TB "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", MAPPER, thamSo.toArray());
    }

    /** Tổng số thiết bị khớp từ khóa (để tính số trang). */
    public long dem(String q) {
        List<Object> thamSo = new ArrayList<>();
        Long tong = jdbc.queryForObject("SELECT COUNT(*) FROM THIET_BI tb JOIN PHONG p ON p.MA_PHONG = tb.MA_PHONG "
                + dieuKienLoc(q, thamSo), Long.class, thamSo.toArray());
        return tong == null ? 0 : tong;
    }

    /** Tìm theo tên, nhà cung cấp, phòng, loại (mã MAY_IN đổi "_" thành khoảng trắng để gõ "may in" vẫn ra). */
    private static String dieuKienLoc(String q, List<Object> thamSo) {
        return q == null ? "" : "WHERE " + TimKiem.dieuKien(q, thamSo, "tb.TEN_TB", "tb.NHA_CUNG_CAP", "p.TEN_PHONG",
                "REPLACE(tb.LOAI_TB, '_', ' ')") + " ";
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

    /**
     * Đã có thiết bị khác cùng phòng, cùng loại, cùng tên (không phân biệt hoa thường, bỏ khoảng trắng hai đầu)?
     * boQuaMaTb = thiết bị đang sửa (null khi thêm mới) để không tự so với chính nó.
     */
    public boolean tonTaiTrung(String tenTb, String loaiTb, Long maPhong, Long boQuaMaTb) {
        Integer dem = jdbc.queryForObject("SELECT COUNT(*) FROM THIET_BI WHERE MA_PHONG = ? AND LOAI_TB = ? "
                        + "AND UPPER(TRIM(TEN_TB)) = UPPER(TRIM(?)) AND MA_TB <> ?",
                Integer.class, maPhong, loaiTb, tenTb, boQuaMaTb == null ? -1L : boQuaMaTb);
        return dem != null && dem > 0;
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
