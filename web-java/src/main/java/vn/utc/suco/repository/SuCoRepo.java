package vn.utc.suco.repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.utc.suco.model.LichSu;
import vn.utc.suco.model.MucUuTien;
import vn.utc.suco.model.SuCo;
import vn.utc.suco.model.SuCoHayGap;
import vn.utc.suco.model.Trang;
import vn.utc.suco.model.TrangThaiSuCo;
import vn.utc.suco.util.TimKiem;

@Repository
public class SuCoRepo {
    // Đọc từ view V_SU_CO_CHI_TIET (đã JOIN sẵn, xem sql/05_queries.sql)
    private static final String SELECT =
            "SELECT MA_SC, TIEU_DE, MO_TA, MA_NGUOI_BAO, TEN_NGUOI_BAO, TEN_ADMIN, MA_TB, TEN_TB, TEN_PHONG, "
                    + "MA_DM, TEN_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, ANH_DINH_KEM, "
                    + "NGAY_TAO, NGAY_HOAN_THANH FROM V_SU_CO_CHI_TIET ";

    private static final RowMapper<SuCo> MAPPER = (rs, i) -> new SuCo(
            rs.getLong("MA_SC"), rs.getString("TIEU_DE"), rs.getString("MO_TA"),
            rs.getLong("MA_NGUOI_BAO"), rs.getString("TEN_NGUOI_BAO"), rs.getString("TEN_ADMIN"),
            rs.getObject("MA_TB", Long.class), rs.getString("TEN_TB"), rs.getString("TEN_PHONG"),
            rs.getLong("MA_DM"), rs.getString("TEN_DM"), MucUuTien.valueOf(rs.getString("MUC_UU_TIEN")),
            TrangThaiSuCo.valueOf(rs.getString("TRANG_THAI")),
            rs.getString("NGUYEN_NHAN"), rs.getString("CACH_KHAC_PHUC"), rs.getObject("MA_BAI_KN", Long.class),
            rs.getString("ANH_DINH_KEM"),
            toLdt(rs.getTimestamp("NGAY_TAO")), toLdt(rs.getTimestamp("NGAY_HOAN_THANH")));

    private static LocalDateTime toLdt(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }

    private final JdbcTemplate jdbc;

    public SuCoRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Người dùng báo sự cố mới. Trạng thái mặc định 'MOI', ngày tạo mặc định SYSDATE (do Oracle gán).
     * anhDinhKem = đường dẫn ảnh chụp sự cố đã lưu ở thư mục uploads, có thể null (không bắt buộc).
     */
    public void taoMoi(String tieuDe, String moTa, Long maNguoiBao, Long maTb, Long maDm, String mucUuTien,
                       String anhDinhKem) {
        jdbc.update("INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_TB, MA_DM, MUC_UU_TIEN, ANH_DINH_KEM) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{tieuDe, moTa, maNguoiBao, maTb, maDm, mucUuTien, anhDinhKem},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
                        Types.VARCHAR});
    }

    /** Sự cố do chính người dùng này báo (màn hình /user). */
    public List<SuCo> timTheoNguoiBao(Long maNd) {
        return jdbc.query(SELECT + "WHERE MA_NGUOI_BAO = ? ORDER BY NGAY_TAO DESC, MA_SC DESC", MAPPER, maNd);
    }

    /**
     * Một trang sự cố cho admin (trang số so, đếm từ 1), mới nhất trước.
     * trangThai / q = null nghĩa là không lọc theo trạng thái / từ khóa.
     */
    public List<SuCo> timTrang(String trangThai, String q, int so) {
        List<Object> thamSo = new ArrayList<>();
        String where = dieuKienLoc(trangThai, q, thamSo);
        thamSo.add(Trang.viTriDau(so));
        thamSo.add(Trang.KICH_THUOC);
        return jdbc.query(SELECT + where + "ORDER BY NGAY_TAO DESC, MA_SC DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                MAPPER, thamSo.toArray());
    }

    /** Tổng số sự cố khớp bộ lọc (để tính số trang). */
    public long dem(String trangThai, String q) {
        List<Object> thamSo = new ArrayList<>();
        String where = dieuKienLoc(trangThai, q, thamSo);
        Long tong = jdbc.queryForObject("SELECT COUNT(*) FROM V_SU_CO_CHI_TIET " + where, Long.class, thamSo.toArray());
        return tong == null ? 0 : tong;
    }

    private static String dieuKienLoc(String trangThai, String q, List<Object> thamSo) {
        List<String> dk = new ArrayList<>();
        if (trangThai != null) {
            dk.add("TRANG_THAI = ?");
            thamSo.add(trangThai);
        }
        if (q != null) {
            dk.add(TimKiem.dieuKien(q, thamSo, "TIEU_DE", "TEN_NGUOI_BAO", "TEN_TB", "TEN_PHONG", "TEN_DM"));
        }
        return dk.isEmpty() ? "" : "WHERE " + String.join(" AND ", dk) + " ";
    }

    public Optional<SuCo> timTheoMa(Long maSc) {
        return jdbc.query(SELECT + "WHERE MA_SC = ?", MAPPER, maSc).stream().findFirst();
    }

    /**
     * Admin cập nhật sự cố. Gán MA_ADMIN_XU_LY = admin đang đăng nhập:
     * trigger TRG_SU_CO_LICH_SU dùng cột này làm "người cập nhật" khi ghi lịch sử,
     * còn TRG_SU_CO_HOAN_THANH tự gán NGAY_HOAN_THANH khi chuyển sang HOAN_THANH.
     * <p>
     * Mỗi lần lưu có thay đổi đều để lại một dòng LICH_SU_XU_LY (trước đây trigger chỉ ghi khi đổi trạng thái,
     * nên cập nhật lần 2 cùng trạng thái thì nguyên nhân/cách khắc phục cũ bị ghi đè mà không còn dấu vết):
     * <ul>
     *   <li>Đổi trạng thái: trigger đã chèn dòng lịch sử, ta chỉ thay câu ghi chú mặc định bằng nội dung lần này.</li>
     *   <li>Giữ nguyên trạng thái: trigger không chạy, ta tự chèn dòng lịch sử (trạng thái cũ = mới).</li>
     * </ul>
     * Trả về false nếu sự cố không tồn tại.
     */
    @Transactional
    public boolean capNhat(Long maSc, String trangThai, String nguyenNhan, String cachKhacPhuc,
                           Long maBaiKn, Long maAdmin, String ghiChu) {
        // FOR UPDATE: khóa dòng để hai admin lưu cùng lúc không đọc phải giá trị cũ của nhau
        List<String[]> cu = jdbc.query("SELECT TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC FROM SU_CO WHERE MA_SC = ? FOR UPDATE",
                (rs, i) -> new String[]{rs.getString(1), rs.getString(2), rs.getString(3)}, maSc);
        if (cu.isEmpty()) {
            return false;
        }
        String trangThaiCu = cu.get(0)[0];
        boolean doiTrangThai = !trangThaiCu.equals(trangThai);

        jdbc.update("UPDATE SU_CO SET TRANG_THAI = ?, NGUYEN_NHAN = ?, CACH_KHAC_PHUC = ?, "
                        + "MA_BAI_KN = ?, MA_ADMIN_XU_LY = ? WHERE MA_SC = ?",
                new Object[]{trangThai, nguyenNhan, cachKhacPhuc, maBaiKn, maAdmin, maSc},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC});

        String noiDung = ghiChuLichSu(ghiChu, cu.get(0)[1], nguyenNhan, cu.get(0)[2], cachKhacPhuc);
        if (noiDung == null) {
            return true;   // không đổi gì ngoài trạng thái (đã có dòng của trigger) hoặc không đổi gì cả
        }
        if (doiTrangThai) {
            jdbc.update("UPDATE LICH_SU_XU_LY SET GHI_CHU = ? WHERE MA_LS = "
                    + "(SELECT MAX(MA_LS) FROM LICH_SU_XU_LY WHERE MA_SC = ?)", noiDung, maSc);
        } else {
            jdbc.update("INSERT INTO LICH_SU_XU_LY (MA_SC, MA_NGUOI_CAP_NHAT, TRANG_THAI_CU, TRANG_THAI_MOI, GHI_CHU) "
                    + "VALUES (?, ?, ?, ?, ?)", maSc, maAdmin, trangThaiCu, trangThai, noiDung);
        }
        return true;
    }

    /** GHI_CHU là VARCHAR2(500 CHAR) */
    private static final int DO_DAI_GHI_CHU = 500;

    /**
     * Ghép nội dung một lần cập nhật để lưu vào LICH_SU_XU_LY.GHI_CHU: ghi chú admin nhập, cộng nguyên nhân /
     * cách khắc phục nếu lần này có sửa. Null nếu không có gì để ghi.
     */
    private static String ghiChuLichSu(String ghiChu, String nguyenNhanCu, String nguyenNhanMoi,
                                       String cachKhacPhucCu, String cachKhacPhucMoi) {
        List<String> phan = new ArrayList<>();
        if (ghiChu != null && !ghiChu.isBlank()) {
            phan.add(ghiChu.trim());
        }
        if (!Objects.equals(rong(nguyenNhanCu), rong(nguyenNhanMoi))) {
            phan.add("Nguyên nhân: " + (rong(nguyenNhanMoi) == null ? "(xóa)" : nguyenNhanMoi.trim()));
        }
        if (!Objects.equals(rong(cachKhacPhucCu), rong(cachKhacPhucMoi))) {
            phan.add("Khắc phục: " + (rong(cachKhacPhucMoi) == null ? "(xóa)" : cachKhacPhucMoi.trim()));
        }
        if (phan.isEmpty()) {
            return null;
        }
        String s = String.join(" | ", phan);
        return s.length() <= DO_DAI_GHI_CHU ? s : s.substring(0, DO_DAI_GHI_CHU - 1) + "…";
    }

    private static String rong(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    /**
     * Xóa sự cố. Phải xóa hết các bảng con có khóa ngoại tới SU_CO (không có ON DELETE CASCADE) trước:
     * BINH_LUAN_SU_CO, PHAN_HOI, LICH_SU_XU_LY, rồi mới xóa SU_CO; thiếu bảng nào Oracle báo ORA-02292.
     * @Transactional để các lệnh DELETE cùng thành công hoặc cùng hoàn tác (Oracle COMMIT/ROLLBACK).
     */
    @Transactional
    public void xoa(Long maSc) {
        jdbc.update("DELETE FROM BINH_LUAN_SU_CO WHERE MA_SC = ?", maSc);
        jdbc.update("DELETE FROM PHAN_HOI WHERE MA_SC = ?", maSc);
        jdbc.update("DELETE FROM LICH_SU_XU_LY WHERE MA_SC = ?", maSc);
        jdbc.update("DELETE FROM SU_CO WHERE MA_SC = ?", maSc);
    }

    /**
     * Top danh mục sự cố hay gặp nhất (đọc từ view V_SU_CO_THUONG_GAP, đếm bằng SQL thay vì lưu cột
     * tần suất tĩnh trên SU_CO để khỏi phải tự đồng bộ tay). Dùng cho trang người dùng.
     */
    public List<SuCoHayGap> timSuCoHayGap() {
        return jdbc.query("SELECT MA_DM, TEN_DM, SO_LAN_BAO FROM V_SU_CO_THUONG_GAP "
                        + "ORDER BY SO_LAN_BAO DESC FETCH FIRST 5 ROWS ONLY",
                (rs, i) -> new SuCoHayGap(rs.getLong("MA_DM"), rs.getString("TEN_DM"), rs.getInt("SO_LAN_BAO")));
    }

    /**
     * Đếm số sự cố theo từng trạng thái (GROUP BY), dùng cho các ô số liệu kiêm nút lọc nhanh ở
     * đầu trang /admin. Trạng thái nào chưa có sự cố thì không có trong Map (giao diện hiểu là 0).
     */
    public Map<String, Integer> demTheoTrangThai() {
        ResultSetExtractor<Map<String, Integer>> doc = rs -> {
            Map<String, Integer> dem = new LinkedHashMap<>();
            while (rs.next()) {
                dem.put(rs.getString("TRANG_THAI"), rs.getInt("SO_LUONG"));
            }
            return dem;
        };
        return jdbc.query("SELECT TRANG_THAI, COUNT(*) AS SO_LUONG FROM SU_CO GROUP BY TRANG_THAI", doc);
    }

    /** Lịch sử xử lý của một sự cố, cũ trước mới sau. */
    public List<LichSu> timLichSu(Long maSc) {
        return jdbc.query(
                "SELECT ls.THOI_GIAN, nd.HO_TEN, ls.TRANG_THAI_CU, ls.TRANG_THAI_MOI, ls.GHI_CHU "
                        + "FROM LICH_SU_XU_LY ls LEFT JOIN NGUOI_DUNG nd ON nd.MA_ND = ls.MA_NGUOI_CAP_NHAT "
                        + "WHERE ls.MA_SC = ? ORDER BY ls.THOI_GIAN, ls.MA_LS",
                (rs, i) -> new LichSu(toLdt(rs.getTimestamp("THOI_GIAN")), rs.getString("HO_TEN"),
                        toTrangThai(rs.getString("TRANG_THAI_CU")), toTrangThai(rs.getString("TRANG_THAI_MOI")),
                        rs.getString("GHI_CHU")),
                maSc);
    }

    private static TrangThaiSuCo toTrangThai(String ma) {
        return ma == null ? null : TrangThaiSuCo.valueOf(ma);
    }
}
