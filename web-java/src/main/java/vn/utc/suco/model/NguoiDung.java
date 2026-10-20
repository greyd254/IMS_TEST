package vn.utc.suco.model;

import java.util.Objects;

/** Người dùng đăng nhập (không chứa mật khẩu để không lưu mật khẩu vào session). */
public class NguoiDung {
    private Long maNd;
    private String hoTen;
    private String email;
    private String donVi;
    private VaiTro vaiTro;
    private String sdt;
    private TrangThaiNguoiDung trangThai;

    public NguoiDung() {
    }

    public NguoiDung(Long maNd, String hoTen, String email, String donVi, VaiTro vaiTro, String sdt,
                     TrangThaiNguoiDung trangThai) {
        this.maNd = maNd;
        this.hoTen = hoTen;
        this.email = email;
        this.donVi = donVi;
        this.vaiTro = vaiTro;
        this.sdt = sdt;
        this.trangThai = trangThai;
    }

    public Long getMaNd() {
        return maNd;
    }

    public void setMaNd(Long maNd) {
        this.maNd = maNd;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public TrangThaiNguoiDung getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiNguoiDung trangThai) {
        this.trangThai = trangThai;
    }

    public boolean laAdmin() {
        return vaiTro == VaiTro.ADMIN;
    }

    public boolean dangHoatDong() {
        return trangThai == TrangThaiNguoiDung.HOAT_DONG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NguoiDung that)) {
            return false;
        }
        return Objects.equals(maNd, that.maNd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNd);
    }

    @Override
    public String toString() {
        return "NguoiDung{maNd=" + maNd + ", hoTen='" + hoTen + "', vaiTro=" + vaiTro + "}";
    }
}
