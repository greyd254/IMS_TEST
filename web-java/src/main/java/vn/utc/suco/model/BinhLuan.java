package vn.utc.suco.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Một tin nhắn trao đổi qua lại giữa người báo và admin trên một sự cố (bảng BINH_LUAN_SU_CO). */
public class BinhLuan {
    private Long maBl;
    private Long maSc;
    private Long maNguoiGui;
    private String tenNguoiGui;
    private VaiTro vaiTroNguoiGui;
    private String noiDung;
    private String tepDinhKem;
    private LocalDateTime thoiGian;

    public BinhLuan() {
    }

    public BinhLuan(Long maBl, Long maSc, Long maNguoiGui, String tenNguoiGui, VaiTro vaiTroNguoiGui,
                    String noiDung, String tepDinhKem, LocalDateTime thoiGian) {
        this.maBl = maBl;
        this.maSc = maSc;
        this.maNguoiGui = maNguoiGui;
        this.tenNguoiGui = tenNguoiGui;
        this.vaiTroNguoiGui = vaiTroNguoiGui;
        this.noiDung = noiDung;
        this.tepDinhKem = tepDinhKem;
        this.thoiGian = thoiGian;
    }

    public Long getMaBl() {
        return maBl;
    }

    public void setMaBl(Long maBl) {
        this.maBl = maBl;
    }

    public Long getMaSc() {
        return maSc;
    }

    public void setMaSc(Long maSc) {
        this.maSc = maSc;
    }

    public Long getMaNguoiGui() {
        return maNguoiGui;
    }

    public void setMaNguoiGui(Long maNguoiGui) {
        this.maNguoiGui = maNguoiGui;
    }

    public String getTenNguoiGui() {
        return tenNguoiGui;
    }

    public void setTenNguoiGui(String tenNguoiGui) {
        this.tenNguoiGui = tenNguoiGui;
    }

    public VaiTro getVaiTroNguoiGui() {
        return vaiTroNguoiGui;
    }

    public void setVaiTroNguoiGui(VaiTro vaiTroNguoiGui) {
        this.vaiTroNguoiGui = vaiTroNguoiGui;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getTepDinhKem() {
        return tepDinhKem;
    }

    public void setTepDinhKem(String tepDinhKem) {
        this.tepDinhKem = tepDinhKem;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    /** Tin nhắn này có phải của admin (IT) gửi hay không - dùng để tô màu khác trong khung chat. */
    public boolean cuaAdmin() {
        return vaiTroNguoiGui == VaiTro.ADMIN;
    }

    private static final DateTimeFormatter DINH_DANG_LUC = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Dữ liệu rút gọn để trả JSON cho polling AJAX (thay vì serialize LocalDateTime trực tiếp, Jackson sẽ
     * biến nó thành mảng số [năm,tháng,ngày,giờ,phút] gây khó cho JS đọc).
     */
    public Map<String, Object> dangJson() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("maBl", maBl);
        m.put("tenNguoiGui", tenNguoiGui);
        m.put("laAdmin", cuaAdmin());
        m.put("noiDung", noiDung);
        m.put("tepDinhKem", tepDinhKem);
        m.put("laAnh", tepDinhKem != null && tepDinhKem.matches("(?i).*\\.(jpg|jpeg|png|gif|webp)$"));
        m.put("thoiGian", thoiGian.format(DINH_DANG_LUC));
        return m;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BinhLuan that)) {
            return false;
        }
        return Objects.equals(maBl, that.maBl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maBl);
    }

    @Override
    public String toString() {
        return "BinhLuan{maBl=" + maBl + ", maSc=" + maSc + "}";
    }
}
