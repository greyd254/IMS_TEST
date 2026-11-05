package vn.utc.suco.model;

/** Tình trạng thiết bị (cột THIET_BI.TINH_TRANG, ràng buộc CK_THIET_BI_TINH_TRANG). */
public enum TinhTrangThietBi implements CoNhan {
    HOAT_DONG("Hoạt động", "text-bg-success"),
    HONG("Hỏng", "text-bg-danger"),
    DANG_SUA("Đang sửa", "text-bg-warning");

    private final String nhan;
    private final String lopBadge;

    TinhTrangThietBi(String nhan, String lopBadge) {
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
