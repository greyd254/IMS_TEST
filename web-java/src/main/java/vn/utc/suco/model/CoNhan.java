package vn.utc.suco.model;

/**
 * Hợp đồng chung cho mọi enum "trạng thái/vai trò/mức độ": mỗi loại tự quyết định nhãn tiếng Việt
 * và màu badge Bootstrap của riêng nó. Nơi gọi (Java hoặc template) chỉ cần gọi getNhan()/getLopBadge()
 * mà không cần biết đang cầm VaiTro, TrangThaiSuCo hay MucUuTien - đây là tính đa hình (polymorphism).
 */
public interface CoNhan {
    String getNhan();

    String getLopBadge();
}
