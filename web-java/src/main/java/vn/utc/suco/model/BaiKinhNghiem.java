package vn.utc.suco.model;

import java.time.LocalDateTime;
import java.util.Objects;

/** Bài kinh nghiệm xử lý sự cố (kèm tên danh mục và tên người viết). */
public class BaiKinhNghiem {
    private Long maBai;
    private String tieuDe;
    private Long maDm;
    private String tenDm;
    private String trieuChung;
    private String nguyenNhan;
    private String giaiPhap;
    private String tenNguoiViet;
    private LocalDateTime ngayTao;

    public BaiKinhNghiem() {
    }

    public BaiKinhNghiem(Long maBai, String tieuDe, Long maDm, String tenDm, String trieuChung,
                         String nguyenNhan, String giaiPhap, String tenNguoiViet, LocalDateTime ngayTao) {
        this.maBai = maBai;
        this.tieuDe = tieuDe;
        this.maDm = maDm;
        this.tenDm = tenDm;
        this.trieuChung = trieuChung;
        this.nguyenNhan = nguyenNhan;
        this.giaiPhap = giaiPhap;
        this.tenNguoiViet = tenNguoiViet;
        this.ngayTao = ngayTao;
    }

    public Long getMaBai() {
        return maBai;
    }

    public void setMaBai(Long maBai) {
        this.maBai = maBai;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
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

    public String getTrieuChung() {
        return trieuChung;
    }

    public void setTrieuChung(String trieuChung) {
        this.trieuChung = trieuChung;
    }

    public String getNguyenNhan() {
        return nguyenNhan;
    }

    public void setNguyenNhan(String nguyenNhan) {
        this.nguyenNhan = nguyenNhan;
    }

    public String getGiaiPhap() {
        return giaiPhap;
    }

    public void setGiaiPhap(String giaiPhap) {
        this.giaiPhap = giaiPhap;
    }

    public String getTenNguoiViet() {
        return tenNguoiViet;
    }

    public void setTenNguoiViet(String tenNguoiViet) {
        this.tenNguoiViet = tenNguoiViet;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaiKinhNghiem that)) {
            return false;
        }
        return Objects.equals(maBai, that.maBai);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maBai);
    }

    @Override
    public String toString() {
        return "BaiKinhNghiem{maBai=" + maBai + ", tieuDe='" + tieuDe + "'}";
    }
}
