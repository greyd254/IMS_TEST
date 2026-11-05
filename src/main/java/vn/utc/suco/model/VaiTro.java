package vn.utc.suco.model;

/** Vai trò người dùng (cột NGUOI_DUNG.VAI_TRO, ràng buộc CK_NGUOI_DUNG_VAI_TRO: 'USER' hoặc 'ADMIN'). */
public enum VaiTro implements CoNhan {
    ADMIN("Quản trị (IT)", "text-bg-danger"),
    USER("Người dùng", "text-bg-secondary");

    private final String nhan;
    private final String lopBadge;

    VaiTro(String nhan, String lopBadge) {
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
