package vn.utc.suco.repository;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vn.utc.suco.model.ThongKeDong;

/** Truy vấn tổng hợp (COUNT/AVG/GROUP BY) cho trang /admin/thong-ke. Chỉ đọc, không có thao tác ghi. */
@Repository
public class ThongKeRepo {
    private static final RowMapper<ThongKeDong> MAPPER =
            (rs, i) -> new ThongKeDong(rs.getString("NHAN"), rs.getInt("SO_LUONG"));

    /** Số tháng hiển thị trên biểu đồ xu hướng */
    private static final int SO_THANG = 12;

    private final JdbcTemplate jdbc;

    public ThongKeRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Số sự cố theo danh mục, nhiều nhất trước, tối đa 8 danh mục. */
    public List<ThongKeDong> theoDanhMuc() {
        return jdbc.query("SELECT dm.TEN_DM AS NHAN, COUNT(*) AS SO_LUONG FROM SU_CO sc "
                        + "JOIN DANH_MUC_SU_CO dm ON dm.MA_DM = sc.MA_DM "
                        + "GROUP BY dm.TEN_DM ORDER BY COUNT(*) DESC FETCH FIRST 8 ROWS ONLY",
                MAPPER);
    }

    /** Top 5 phòng có nhiều sự cố nhất (chỉ tính sự cố có gắn thiết bị). */
    public List<ThongKeDong> theoPhong() {
        return jdbc.query("SELECT p.TEN_PHONG AS NHAN, COUNT(*) AS SO_LUONG FROM SU_CO sc "
                        + "JOIN THIET_BI tb ON tb.MA_TB = sc.MA_TB JOIN PHONG p ON p.MA_PHONG = tb.MA_PHONG "
                        + "GROUP BY p.TEN_PHONG ORDER BY COUNT(*) DESC FETCH FIRST 5 ROWS ONLY",
                MAPPER);
    }

    /** Số sự cố theo mức ưu tiên, đủ 3 mức kể cả mức chưa có sự cố (điền 0) để biểu đồ luôn 3 dòng. */
    public List<ThongKeDong> theoUuTien() {
        return jdbc.query("SELECT m.NHAN, COUNT(sc.MA_SC) AS SO_LUONG FROM ("
                        + "SELECT 'THAP' AS MA, 'Thấp' AS NHAN, 1 AS TT FROM DUAL "
                        + "UNION ALL SELECT 'TRUNG_BINH', 'Trung bình', 2 FROM DUAL "
                        + "UNION ALL SELECT 'CAO', 'Cao', 3 FROM DUAL) m "
                        + "LEFT JOIN SU_CO sc ON sc.MUC_UU_TIEN = m.MA GROUP BY m.NHAN, m.TT ORDER BY m.TT",
                MAPPER);
    }

    /** Số thiết bị theo loại, nhãn tiếng Việt khớp ThietBiController.LOAI. */
    public List<ThongKeDong> theoLoaiThietBi() {
        return jdbc.query("SELECT CASE LOAI_TB WHEN 'MAY_TINH' THEN 'Máy tính' WHEN 'MAY_CHIEU' THEN 'Máy chiếu' "
                        + "WHEN 'MAY_IN' THEN 'Máy in' WHEN 'THIET_BI_MANG' THEN 'Thiết bị mạng' ELSE 'Khác' END AS NHAN, "
                        + "COUNT(*) AS SO_LUONG FROM THIET_BI GROUP BY LOAI_TB ORDER BY COUNT(*) DESC",
                MAPPER);
    }

    /** Số thiết bị theo tình trạng, đủ 3 tình trạng kể cả chưa có thiết bị (điền 0) để biểu đồ luôn 3 dòng. */
    public List<ThongKeDong> theoTinhTrangThietBi() {
        return jdbc.query("SELECT m.NHAN, COUNT(tb.MA_TB) AS SO_LUONG FROM ("
                        + "SELECT 'HOAT_DONG' AS MA, 'Hoạt động' AS NHAN, 1 AS TT FROM DUAL "
                        + "UNION ALL SELECT 'DANG_SUA', 'Đang sửa', 2 FROM DUAL "
                        + "UNION ALL SELECT 'HONG', 'Hỏng', 3 FROM DUAL) m "
                        + "LEFT JOIN THIET_BI tb ON tb.TINH_TRANG = m.MA GROUP BY m.NHAN, m.TT ORDER BY m.TT",
                MAPPER);
    }

    /** Top 5 admin xử lý xong nhiều sự cố nhất. */
    public List<ThongKeDong> topAdminXuLy() {
        return jdbc.query("SELECT nd.HO_TEN AS NHAN, COUNT(*) AS SO_LUONG FROM SU_CO sc "
                        + "JOIN NGUOI_DUNG nd ON nd.MA_ND = sc.MA_ADMIN_XU_LY "
                        + "WHERE sc.TRANG_THAI = 'HOAN_THANH' "
                        + "GROUP BY nd.HO_TEN ORDER BY COUNT(*) DESC FETCH FIRST 5 ROWS ONLY",
                MAPPER);
    }

    /**
     * Số sự cố theo tháng, đủ 12 tháng gần nhất kể cả tháng chưa có sự cố nào (điền 0), để vẽ biểu đồ xu hướng
     * không bị "nhảy cóc" khi có tháng trống. Nhãn ngắn ("T11") để 12 cột vẫn vừa màn hình điện thoại;
     * tháng 1 và cột đầu tiên kèm năm ("T1 2026").
     */
    public List<ThongKeDong> theoThang() {
        Map<YearMonth, Integer> dem = new LinkedHashMap<>();
        YearMonth batDau = YearMonth.now().minusMonths(SO_THANG - 1);
        for (int i = 0; i < SO_THANG; i++) {
            dem.put(batDau.plusMonths(i), 0);
        }
        jdbc.query("SELECT TO_CHAR(NGAY_TAO, 'YYYY-MM') AS THANG, COUNT(*) AS SO_LUONG FROM SU_CO "
                        + "WHERE NGAY_TAO >= ADD_MONTHS(TRUNC(SYSDATE, 'MM'), ?) "
                        + "GROUP BY TO_CHAR(NGAY_TAO, 'YYYY-MM')",
                rs -> {
                    dem.put(YearMonth.parse(rs.getString("THANG")), rs.getInt("SO_LUONG"));
                },
                -(SO_THANG - 1));
        List<ThongKeDong> ketQua = new ArrayList<>();
        dem.forEach((thang, soLuong) -> {
            // Cột đầu là tháng 12 thì không ghi năm, vì ngay cạnh là "T1 2026" và 2 số năm sẽ dính nhau trên điện thoại
            boolean kemNam = thang.getMonthValue() == 1
                    || (thang.equals(batDau) && thang.getMonthValue() != 12);
            ketQua.add(new ThongKeDong("T" + thang.getMonthValue() + (kemNam ? " " + thang.getYear() : ""), soLuong));
        });
        return ketQua;
    }

    public int demThietBi() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM THIET_BI", Integer.class);
    }

    public int demThietBiHong() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM THIET_BI WHERE TINH_TRANG = 'HONG'", Integer.class);
    }

    public int demNguoiDungHoatDong() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM NGUOI_DUNG WHERE TRANG_THAI = 'HOAT_DONG'", Integer.class);
    }

    public int demBaiKinhNghiem() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM BAI_KINH_NGHIEM", Integer.class);
    }

    public int demPhanHoi() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM PHAN_HOI", Integer.class);
    }

    /** Thời gian xử lý trung bình (giờ) của các sự cố đã HOAN_THANH; null nếu chưa có sự cố nào xong. */
    public Double gioXuLyTrungBinh() {
        return jdbc.queryForObject("SELECT AVG(NGAY_HOAN_THANH - NGAY_TAO) * 24 FROM SU_CO "
                + "WHERE TRANG_THAI = 'HOAN_THANH' AND NGAY_HOAN_THANH IS NOT NULL", Double.class);
    }

    /** Điểm hài lòng trung bình (1-5) theo phản hồi đã nhận; null nếu chưa có phản hồi nào. */
    public Double diemHaiLongTrungBinh() {
        return jdbc.queryForObject("SELECT AVG(MUC_HAI_LONG) FROM PHAN_HOI", Double.class);
    }
}
