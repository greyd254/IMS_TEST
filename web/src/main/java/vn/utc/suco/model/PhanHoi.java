package vn.utc.suco.model;

import java.time.LocalDate;

/** Phản hồi của người báo sự cố sau khi sự cố đã HOAN_THANH (đánh giá mức hài lòng 1-5). */
public record PhanHoi(Long maPh, Long maSc, Long maNguoiPhanHoi, int mucHaiLong, String noiDung,
                      LocalDate ngayPhanHoi) {
}
