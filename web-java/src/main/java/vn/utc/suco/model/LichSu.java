package vn.utc.suco.model;

import java.time.LocalDateTime;

/** Một dòng lịch sử đổi trạng thái (bảng LICH_SU_XU_LY, do trigger Oracle ghi). */
public class LichSu {
    private LocalDateTime thoiGian;
    private String tenNguoiCapNhat;
    private TrangThaiSuCo trangThaiCu;
    private TrangThaiSuCo trangThaiMoi;
    private String ghiChu;

    public LichSu() {
    }

    public LichSu(LocalDateTime thoiGian, String tenNguoiCapNhat, TrangThaiSuCo trangThaiCu,
                 TrangThaiSuCo trangThaiMoi, String ghiChu) {
        this.thoiGian = thoiGian;
        this.tenNguoiCapNhat = tenNguoiCapNhat;
        this.trangThaiCu = trangThaiCu;
        this.trangThaiMoi = trangThaiMoi;
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getTenNguoiCapNhat() {
        return tenNguoiCapNhat;
    }

    public void setTenNguoiCapNhat(String tenNguoiCapNhat) {
        this.tenNguoiCapNhat = tenNguoiCapNhat;
    }

    public TrangThaiSuCo getTrangThaiCu() {
        return trangThaiCu;
    }

    public void setTrangThaiCu(TrangThaiSuCo trangThaiCu) {
        this.trangThaiCu = trangThaiCu;
    }

    public TrangThaiSuCo getTrangThaiMoi() {
        return trangThaiMoi;
    }

    public void setTrangThaiMoi(TrangThaiSuCo trangThaiMoi) {
        this.trangThaiMoi = trangThaiMoi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return "LichSu{" + trangThaiCu + " -> " + trangThaiMoi + ", luc=" + thoiGian + "}";
    }
}
