package vn.utc.suco.model;

import java.util.Objects;

/** Phòng (máy, học, văn phòng). Dùng cho quản lý phòng và dropdown chọn phòng khi thêm/sửa thiết bị. */
public class Phong {
    private Long maPhong;
    private String tenPhong;
    private String toaNha;
    private Integer tang;
    private String loaiPhong;

    public Phong() {
    }

    public Phong(Long maPhong, String tenPhong, String toaNha, Integer tang, String loaiPhong) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.toaNha = toaNha;
        this.tang = tang;
        this.loaiPhong = loaiPhong;
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

    public String getToaNha() {
        return toaNha;
    }

    public void setToaNha(String toaNha) {
        this.toaNha = toaNha;
    }

    public Integer getTang() {
        return tang;
    }

    public void setTang(Integer tang) {
        this.tang = tang;
    }

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Phong that)) {
            return false;
        }
        return Objects.equals(maPhong, that.maPhong);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhong);
    }

    @Override
    public String toString() {
        return "Phong{maPhong=" + maPhong + ", tenPhong='" + tenPhong + "'}";
    }
}
