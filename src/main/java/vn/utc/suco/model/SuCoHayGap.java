package vn.utc.suco.model;

/** Một dòng của view V_SU_CO_THUONG_GAP: danh mục kèm số lần đã được báo (dùng để hiển thị "sự cố hay gặp"). */
public class SuCoHayGap {
    private Long maDm;
    private String tenDm;
    private int soLanBao;

    public SuCoHayGap() {
    }

    public SuCoHayGap(Long maDm, String tenDm, int soLanBao) {
        this.maDm = maDm;
        this.tenDm = tenDm;
        this.soLanBao = soLanBao;
    }

    public Long getMaDm() {
        return maDm;
    }

    public void setMaDm(Long maDm) {
        this.maDm = maDm;
    }

    public String getTenDm() {
        return tenDm;
    }

    public void setTenDm(String tenDm) {
        this.tenDm = tenDm;
    }

    public int getSoLanBao() {
        return soLanBao;
    }

    public void setSoLanBao(int soLanBao) {
        this.soLanBao = soLanBao;
    }

    @Override
    public String toString() {
        return "SuCoHayGap{tenDm='" + tenDm + "', soLanBao=" + soLanBao + "}";
    }
}
