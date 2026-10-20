package vn.utc.suco.model;

/** Trạng thái tài khoản (cột NGUOI_DUNG.TRANG_THAI, ràng buộc CK_NGUOI_DUNG_TRANG_THAI). */
public enum TrangThaiNguoiDung implements CoNhan {
    HOAT_DONG("Hoạt động", "text-bg-success"),
    KHONG_HOAT_DONG("Đã khóa", "text-bg-secondary");

    private final String nhan;
    private final String lopBadge;

    TrangThaiNguoiDung(String nhan, String lopBadge) {
        this.nhan = nhan;
        this.lopBadge = lopBadge;
    }

    @Override
    public String getNhan() {
        return nhan;
    }

    @Override
    public String getLopBadge() {
        return lopBadge;
    }
}
