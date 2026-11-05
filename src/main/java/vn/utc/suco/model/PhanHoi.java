package vn.utc.suco.model;

import java.time.LocalDate;
import java.util.Objects;

/** Phản hồi của người báo sự cố sau khi sự cố đã HOAN_THANH (đánh giá mức hài lòng 1-5). */
public class PhanHoi {
    private Long maPh;
    private Long maSc;
    private Long maNguoiPhanHoi;
    private int mucHaiLong;
    private String noiDung;
    private LocalDate ngayPhanHoi;

    public PhanHoi() {
    }

    public PhanHoi(Long maPh, Long maSc, Long maNguoiPhanHoi, int mucHaiLong, String noiDung,
                   LocalDate ngayPhanHoi) {
        this.maPh = maPh;
        this.maSc = maSc;
        this.maNguoiPhanHoi = maNguoiPhanHoi;
        this.mucHaiLong = mucHaiLong;
        this.noiDung = noiDung;
        this.ngayPhanHoi = ngayPhanHoi;
    }

    public Long getMaPh() {
        return maPh;
    }

    public void setMaPh(Long maPh) {
        this.maPh = maPh;
    }

    public Long getMaSc() {
        return maSc;
    }

    public void setMaSc(Long maSc) {
        this.maSc = maSc;
    }

    public Long getMaNguoiPhanHoi() {
        return maNguoiPhanHoi;
    }

    public void setMaNguoiPhanHoi(Long maNguoiPhanHoi) {
        this.maNguoiPhanHoi = maNguoiPhanHoi;
    }

    public int getMucHaiLong() {
        return mucHaiLong;
    }

    public void setMucHaiLong(int mucHaiLong) {
        this.mucHaiLong = mucHaiLong;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public LocalDate getNgayPhanHoi() {
        return ngayPhanHoi;
    }

    public void setNgayPhanHoi(LocalDate ngayPhanHoi) {
        this.ngayPhanHoi = ngayPhanHoi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PhanHoi that)) {
            return false;
        }
        return Objects.equals(maPh, that.maPh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPh);
    }

    @Override
    public String toString() {
        return "PhanHoi{maPh=" + maPh + ", mucHaiLong=" + mucHaiLong + "}";
    }
}
