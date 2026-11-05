package vn.utc.suco.model;

/** Trạng thái xử lý sự cố (cột SU_CO.TRANG_THAI, ràng buộc CK_SU_CO_TRANG_THAI). */
public enum TrangThaiSuCo implements CoNhan {
    MOI("Mới", "text-bg-secondary"),
    DANG_XU_LY("Đang xử lý", "text-bg-warning"),
    HOAN_THANH("Hoàn thành", "text-bg-success");

    private final String nhan;
    private final String lopBadge;

    TrangThaiSuCo(String nhan, String lopBadge) {
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
