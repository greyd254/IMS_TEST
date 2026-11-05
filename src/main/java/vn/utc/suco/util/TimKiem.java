package vn.utc.suco.util;

import java.util.List;

/**
 * Dựng điều kiện tìm kiếm cho ô "Tìm" ở các danh sách có phân trang.
 * Tìm không phân biệt hoa thường và không dấu (COLLATE BINARY_AI của Oracle: gõ "may in" ra "Máy in",
 * "dao tao" ra "Đào tạo"), giống ô tìm nhanh bằng JS trước đây nhưng tìm trên toàn bộ bảng chứ không chỉ trang đang xem.
 */
public final class TimKiem {
    private TimKiem() {
    }

    /** Từ khóa đã bỏ khoảng trắng hai đầu; null nếu để trống (không lọc). */
    public static String chuanHoa(String q) {
        return q == null || q.isBlank() ? null : q.trim();
    }

    /**
     * Điều kiện "cột1 LIKE ... OR cột2 LIKE ..." (đã bọc ngoặc) và thêm tham số tương ứng vào thamSo.
     * Ký tự % và _ trong từ khóa được thoát để hiểu theo nghĩa đen.
     */
    public static String dieuKien(String q, List<Object> thamSo, String... cot) {
        String mau = "%" + q.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < cot.length; i++) {
            if (i > 0) {
                sb.append(" OR ");
            }
            sb.append(cot[i]).append(" COLLATE BINARY_AI LIKE ? ESCAPE '\\'");
            thamSo.add(mau);
        }
        return sb.append(")").toString();
    }
}
