package vn.utc.suco.model;

import java.time.LocalDateTime;

/** Bài kinh nghiệm xử lý sự cố (kèm tên danh mục và tên người viết). */
public record BaiKinhNghiem(Long maBai, String tieuDe, Long maDm, String tenDm,
                            String trieuChung, String nguyenNhan, String giaiPhap,
                            String tenNguoiViet, LocalDateTime ngayTao) {
}
