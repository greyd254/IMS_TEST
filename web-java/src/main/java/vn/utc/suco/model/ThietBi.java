package vn.utc.suco.model;

import java.time.LocalDate;
import java.util.Objects;

/** Thiết bị kèm tên phòng: dùng cho dropdown chọn thiết bị và cho trang quản lý thiết bị của admin. */
public class ThietBi {
    private Long maTb;
    private String tenTb;
    private String loaiTb;
    private TinhTrangThietBi tinhTrang;
    private Long maPhong;
    private String tenPhong;
    private String nhaCungCap;
    private LocalDate ngayLapDat;
    private String hdsd;

    public ThietBi() {
    }

    public ThietBi(Long maTb, String tenTb, String loaiTb, TinhTrangThietBi tinhTrang, Long maPhong,
                   String tenPhong, String nhaCungCap, LocalDate ngayLapDat, String hdsd) {
        this.maTb = maTb;
        this.tenTb = tenTb;
        this.loaiTb = loaiTb;
        this.tinhTrang = tinhTrang;
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.nhaCungCap = nhaCungCap;
        this.ngayLapDat = ngayLapDat;
        this.hdsd = hdsd;
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

    public String getLoaiTb() {
        return loaiTb;
    }

    public void setLoaiTb(String loaiTb) {
        this.loaiTb = loaiTb;
    }

    public TinhTrangThietBi getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(TinhTrangThietBi tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public Long getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(Long maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(String nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public LocalDate getNgayLapDat() {
        return ngayLapDat;
    }

    public void setNgayLapDat(LocalDate ngayLapDat) {
        this.ngayLapDat = ngayLapDat;
    }

    public String getHdsd() {
        return hdsd;
    }

    public void setHdsd(String hdsd) {
        this.hdsd = hdsd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ThietBi that)) {
            return false;
        }
        return Objects.equals(maTb, that.maTb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTb);
    }

    @Override
    public String toString() {
        return "ThietBi{maTb=" + maTb + ", tenTb='" + tenTb + "', tinhTrang=" + tinhTrang + "}";
    }
}
