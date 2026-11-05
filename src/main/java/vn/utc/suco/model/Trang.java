package vn.utc.suco.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Một trang dữ liệu khi phân trang danh sách (sự cố, thiết bị, phòng). Lớp generic: T là kiểu của từng dòng.
 * Số trang đếm từ 1. Repo chỉ lấy đúng các dòng của trang này (OFFSET ... FETCH NEXT ... ROWS ONLY),
 * còn tổng số dòng dùng để tính số trang.
 */
public class Trang<T> {
    /** Số dòng mỗi trang */
    public static final int KICH_THUOC = 10;

    private final List<T> ds;
    private final int so;
    private final long tong;
    private final UriComponentsBuilder duongDan;

    /**
     * duongDan = đường dẫn của danh sách kèm các tham số lọc đang dùng (VD /admin?q=may&trangThai=MOI),
     * để link sang trang khác giữ nguyên bộ lọc.
     */
    public Trang(List<T> ds, int so, long tong, UriComponentsBuilder duongDan) {
        this.ds = ds;
        this.so = so;
        this.tong = tong;
        this.duongDan = duongDan;
    }

    /** Tổng số trang (ít nhất 1, kể cả khi không có dòng nào). */
    public static int tinhSoTrang(long tong) {
        return (int) Math.max(1, (tong + KICH_THUOC - 1) / KICH_THUOC);
    }

    /** Ép số trang người dùng gửi lên về khoảng hợp lệ [1, số trang]. */
    public static int gioiHan(Integer so, long tong) {
        return so == null ? 1 : Math.min(Math.max(so, 1), tinhSoTrang(tong));
    }

    /** Vị trí dòng đầu tiên của trang (dùng cho OFFSET, đếm từ 0). */
    public static int viTriDau(int so) {
        return (so - 1) * KICH_THUOC;
    }

    public List<T> getDs() {
        return ds;
    }

    public int getSo() {
        return so;
    }

    public long getTong() {
        return tong;
    }

    public int getSoTrang() {
        return tinhSoTrang(tong);
    }

    /** Thứ tự dòng đầu / cuối đang hiển thị, để ghi "Dòng 11-20 / 35". */
    public long getTuDong() {
        return tong == 0 ? 0 : viTriDau(so) + 1L;
    }

    public long getDenDong() {
        return viTriDau(so) + (long) ds.size();
    }

    /** Các số trang hiện trên thanh phân trang: tối đa 5 số quanh trang hiện tại. */
    public List<Integer> getCacSo() {
        int dau = Math.max(1, Math.min(so - 2, getSoTrang() - 4));
        int cuoi = Math.min(getSoTrang(), dau + 4);
        List<Integer> kq = new ArrayList<>();
        for (int i = dau; i <= cuoi; i++) {
            kq.add(i);
        }
        return kq;
    }

    /** Link tới trang số n, giữ nguyên các tham số lọc. */
    public String url(int n) {
        return duongDan.cloneBuilder().replaceQueryParam("trang", n).build().encode().toUriString();
    }

    @Override
    public String toString() {
        return "Trang{" + so + "/" + getSoTrang() + ", tong=" + tong + "}";
    }
}
