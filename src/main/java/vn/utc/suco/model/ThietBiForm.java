package vn.utc.suco.model;

/** Dữ liệu form thêm/sửa thiết bị, map từ body của request POST. */
public class ThietBiForm {
    private String tenTb;
    private String loaiTb;
    private Long maPhong;
    private String tinhTrang;
    private String nhaCungCap;
    private String ngayLapDat;
    private String hdsd;

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

    public Long getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(Long maPhong) {
        this.maPhong = maPhong;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(String nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public String getNgayLapDat() {
        return ngayLapDat;
    }

    public void setNgayLapDat(String ngayLapDat) {
        this.ngayLapDat = ngayLapDat;
    }

    public String getHdsd() {
        return hdsd;
    }

    public void setHdsd(String hdsd) {
        this.hdsd = hdsd;
    }
}
