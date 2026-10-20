package vn.utc.suco.model;

import java.time.LocalDateTime;
import java.util.Objects;

/** Một dòng của view V_SU_CO_CHI_TIET (sự cố kèm tên người báo, admin, thiết bị, phòng, danh mục). */
public class SuCo {
    private Long maSc;
    private String tieuDe;
    private String moTa;
    private Long maNguoiBao;
    private String tenNguoiBao;
    private String tenAdmin;
    private Long maTb;
    private String tenTb;
    private String tenPhong;
    private Long maDm;
    private String tenDm;
    private MucUuTien mucUuTien;
    private TrangThaiSuCo trangThai;
    private String nguyenNhan;
    private String cachKhacPhuc;
    private Long maBaiKn;
    private String anhDinhKem;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayHoanThanh;

    public SuCo() {
    }

    public SuCo(Long maSc, String tieuDe, String moTa, Long maNguoiBao, String tenNguoiBao, String tenAdmin,
               Long maTb, String tenTb, String tenPhong, Long maDm, String tenDm, MucUuTien mucUuTien,
               TrangThaiSuCo trangThai, String nguyenNhan, String cachKhacPhuc, Long maBaiKn, String anhDinhKem,
               LocalDateTime ngayTao, LocalDateTime ngayHoanThanh) {
        this.maSc = maSc;
        this.tieuDe = tieuDe;
        this.moTa = moTa;
        this.maNguoiBao = maNguoiBao;
        this.tenNguoiBao = tenNguoiBao;
        this.tenAdmin = tenAdmin;
        this.maTb = maTb;
        this.tenTb = tenTb;
        this.tenPhong = tenPhong;
        this.maDm = maDm;
        this.tenDm = tenDm;
        this.mucUuTien = mucUuTien;
        this.trangThai = trangThai;
        this.nguyenNhan = nguyenNhan;
        this.cachKhacPhuc = cachKhacPhuc;
        this.maBaiKn = maBaiKn;
        this.anhDinhKem = anhDinhKem;
        this.ngayTao = ngayTao;
        this.ngayHoanThanh = ngayHoanThanh;
    }

    public Long getMaSc() {
        return maSc;
    }

    public void setMaSc(Long maSc) {
        this.maSc = maSc;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Long getMaNguoiBao() {
        return maNguoiBao;
    }

    public void setMaNguoiBao(Long maNguoiBao) {
        this.maNguoiBao = maNguoiBao;
    }

    public String getTenNguoiBao() {
        return tenNguoiBao;
    }

    public void setTenNguoiBao(String tenNguoiBao) {
        this.tenNguoiBao = tenNguoiBao;
    }

    public String getTenAdmin() {
        return tenAdmin;
    }

    public void setTenAdmin(String tenAdmin) {
        this.tenAdmin = tenAdmin;
    }

    public Long getMaTb() {
        return maTb;
    }

    public void setMaTb(Long maTb) {
        this.maTb = maTb;
    }

    public String getTenTb() {
        return tenTb;
    }

    public void setTenTb(String tenTb) {
        this.tenTb = tenTb;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
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

    public MucUuTien getMucUuTien() {
        return mucUuTien;
    }

    public void setMucUuTien(MucUuTien mucUuTien) {
        this.mucUuTien = mucUuTien;
    }

    public TrangThaiSuCo getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiSuCo trangThai) {
        this.trangThai = trangThai;
    }

    public String getNguyenNhan() {
        return nguyenNhan;
    }

    public void setNguyenNhan(String nguyenNhan) {
        this.nguyenNhan = nguyenNhan;
    }

    public String getCachKhacPhuc() {
        return cachKhacPhuc;
    }

    public void setCachKhacPhuc(String cachKhacPhuc) {
        this.cachKhacPhuc = cachKhacPhuc;
    }

    public Long getMaBaiKn() {
        return maBaiKn;
    }

    public void setMaBaiKn(Long maBaiKn) {
        this.maBaiKn = maBaiKn;
    }

    public String getAnhDinhKem() {
        return anhDinhKem;
    }

    public void setAnhDinhKem(String anhDinhKem) {
        this.anhDinhKem = anhDinhKem;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayHoanThanh() {
        return ngayHoanThanh;
    }

    public void setNgayHoanThanh(LocalDateTime ngayHoanThanh) {
        this.ngayHoanThanh = ngayHoanThanh;
    }

    /** Đã xử lý xong hay chưa - dùng ở giao diện để ẩn/hiện khối phản hồi, nút sửa... */
    public boolean daHoanThanh() {
        return trangThai == TrangThaiSuCo.HOAN_THANH;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SuCo that)) {
            return false;
        }
        return Objects.equals(maSc, that.maSc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSc);
    }

    @Override
    public String toString() {
        return "SuCo{maSc=" + maSc + ", tieuDe='" + tieuDe + "', trangThai=" + trangThai + "}";
    }
}
