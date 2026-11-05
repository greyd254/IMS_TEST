package vn.utc.suco.model;

/**
 * Một dòng biểu đồ cột đã tính sẵn: nhãn, số lượng, và % độ dài thanh so với giá trị lớn nhất
 * trong cùng danh sách (Thymeleaf không có hàm max() trên danh sách đối tượng nên tính ở tầng Java).
 */
public record ThongKeCot(String nhan, int soLuong, int phanTram) {
}
