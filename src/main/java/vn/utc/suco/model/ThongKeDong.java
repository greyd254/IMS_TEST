package vn.utc.suco.model;

/** Một dòng thống kê dạng nhãn + số lượng (danh mục, phòng, loại thiết bị...), dùng để vẽ biểu đồ cột. */
public class ThongKeDong {
    private String nhan;
    private int soLuong;

    public ThongKeDong() {
    }

    public ThongKeDong(String nhan, int soLuong) {
        this.nhan = nhan;
        this.soLuong = soLuong;
    }

    public String getNhan() {
        return nhan;
    }

    public void setNhan(String nhan) {
        this.nhan = nhan;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    @Override
    public String toString() {
        return "ThongKeDong{nhan='" + nhan + "', soLuong=" + soLuong + "}";
    }
}
