package vn.utc.suco.model;

import java.util.Objects;

/** Danh mục sự cố (Máy tính, Máy chiếu, Mạng...), dùng chung cho sự cố và kho kinh nghiệm. */
public class DanhMuc {
    private Long maDm;
    private String tenDm;
    private String moTa;

    public DanhMuc() {
    }

    public DanhMuc(Long maDm, String tenDm, String moTa) {
        this.maDm = maDm;
        this.tenDm = tenDm;
        this.moTa = moTa;
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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DanhMuc that)) {
            return false;
        }
        return Objects.equals(maDm, that.maDm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDm);
    }

    @Override
    public String toString() {
        return "DanhMuc{maDm=" + maDm + ", tenDm='" + tenDm + "', moTa='" + moTa + "'}";
    }
}
