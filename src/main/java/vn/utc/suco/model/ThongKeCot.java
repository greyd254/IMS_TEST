package vn.utc.suco.model;

/**
 * Một dòng biểu đồ cột: kế thừa ThongKeDong (nhãn + số lượng) và thêm % độ dài thanh so với giá trị
 * lớn nhất trong cùng danh sách (Thymeleaf không có hàm max() trên danh sách đối tượng nên tính ở tầng Java).
 */
public class ThongKeCot extends ThongKeDong {
    private int phanTram;

    public ThongKeCot() {
    }

    public ThongKeCot(String nhan, int soLuong, int phanTram) {
        super(nhan, soLuong);
        this.phanTram = phanTram;
    }

    public int getPhanTram() {
        return phanTram;
    }

    public void setPhanTram(int phanTram) {
        this.phanTram = phanTram;
    }

    @Override
    public String toString() {
        return "ThongKeCot{nhan='" + getNhan() + "', soLuong=" + getSoLuong() + ", phanTram=" + phanTram + "}";
    }
}
