package vn.utc.suco.controller;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.utc.suco.model.ThongKeCot;
import vn.utc.suco.model.ThongKeDong;
import vn.utc.suco.repository.SuCoRepo;
import vn.utc.suco.repository.ThongKeRepo;

/** Trang thống kê tổng quan cho admin (/admin/thong-ke): chỉ xem số liệu, không có thao tác chỉnh sửa. */
@Controller
public class ThongKeController {
    private final SuCoRepo suCoRepo;
    private final ThongKeRepo thongKeRepo;

    public ThongKeController(SuCoRepo suCoRepo, ThongKeRepo thongKeRepo) {
        this.suCoRepo = suCoRepo;
        this.thongKeRepo = thongKeRepo;
    }

    @GetMapping("/admin/thong-ke")
    public String thongKe(Model model) {
        Map<String, Integer> demTrangThai = suCoRepo.demTheoTrangThai();
        int tongSuCo = demTrangThai.values().stream().mapToInt(Integer::intValue).sum();
        model.addAttribute("tongSuCo", tongSuCo);
        model.addAttribute("demTrangThai", demTrangThai);

        model.addAttribute("theoTrangThai", bieuDo(List.of(
                new ThongKeDong("Mới", demTrangThai.getOrDefault("MOI", 0)),
                new ThongKeDong("Đang xử lý", demTrangThai.getOrDefault("DANG_XU_LY", 0)),
                new ThongKeDong("Hoàn thành", demTrangThai.getOrDefault("HOAN_THANH", 0)))));
        model.addAttribute("theoDanhMuc", bieuDo(thongKeRepo.theoDanhMuc()));
        model.addAttribute("theoPhong", bieuDo(thongKeRepo.theoPhong()));
        model.addAttribute("theoThang", bieuDo(thongKeRepo.theoThang()));
        model.addAttribute("theoUuTien", bieuDo(thongKeRepo.theoUuTien()));
        model.addAttribute("theoLoaiTb", bieuDo(thongKeRepo.theoLoaiThietBi()));
        model.addAttribute("theoTinhTrangTb", bieuDo(thongKeRepo.theoTinhTrangThietBi()));
        model.addAttribute("topAdmin", bieuDo(thongKeRepo.topAdminXuLy()));

        model.addAttribute("tongThietBi", thongKeRepo.demThietBi());
        model.addAttribute("thietBiHong", thongKeRepo.demThietBiHong());
        model.addAttribute("tongNguoiDung", thongKeRepo.demNguoiDungHoatDong());
        model.addAttribute("tongBaiKn", thongKeRepo.demBaiKinhNghiem());
        model.addAttribute("soPhanHoi", thongKeRepo.demPhanHoi());
        model.addAttribute("diemHaiLong", thongKeRepo.diemHaiLongTrungBinh());
        model.addAttribute("gioXuLy", hienThiGio(thongKeRepo.gioXuLyTrungBinh()));
        return "thong-ke";
    }

    private List<ThongKeCot> bieuDo(List<ThongKeDong> ds) {
        int max = ds.stream().mapToInt(ThongKeDong::getSoLuong).max().orElse(0);
        return ds.stream()
                .map(d -> new ThongKeCot(d.getNhan(), d.getSoLuong(),
                        max == 0 ? 0 : Math.round(d.getSoLuong() * 100f / max)))
                .toList();
    }

    /** "2 ngày 5 giờ" cho dễ đọc thay vì in thẳng số giờ thập phân. */
    private String hienThiGio(Double gio) {
        if (gio == null) {
            return null;
        }
        long tongPhut = Math.round(gio * 60);
        long ngay = tongPhut / (24 * 60);
        long gioLe = (tongPhut % (24 * 60)) / 60;
        if (ngay > 0) {
            return ngay + " ngày " + gioLe + " giờ";
        }
        if (gioLe > 0) {
            return gioLe + " giờ";
        }
        return "Dưới 1 giờ";
    }
}
