package vn.utc.suco.model;

/** Mức ưu tiên xử lý sự cố (cột SU_CO.MUC_UU_TIEN, ràng buộc CK_SU_CO_UU_TIEN). */
public enum MucUuTien implements CoNhan {
    THAP("Thấp", "text-bg-info"),
    TRUNG_BINH("Trung bình", "text-bg-primary"),
    CAO("Cao", "text-bg-danger");

    private final String nhan;
    private final String lopBadge;

    MucUuTien(String nhan, String lopBadge) {
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
