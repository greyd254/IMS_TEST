package vn.utc.suco.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.model.SuCo;
import vn.utc.suco.repository.DanhMucRepo;
import vn.utc.suco.repository.KinhNghiemRepo;
import vn.utc.suco.repository.SuCoRepo;

/** Màn hình admin (chỉ vai trò ADMIN, đã được AuthInterceptor chặn cho USER). */
@Controller
public class AdminController {
    private static final Set<String> TRANG_THAI = Set.of("MOI", "DANG_XU_LY", "HOAN_THANH");

    private final SuCoRepo suCoRepo;
    private final KinhNghiemRepo kinhNghiemRepo;
    private final DanhMucRepo danhMucRepo;

    public AdminController(SuCoRepo suCoRepo, KinhNghiemRepo kinhNghiemRepo, DanhMucRepo danhMucRepo) {
        this.suCoRepo = suCoRepo;
        this.kinhNghiemRepo = kinhNghiemRepo;
        this.danhMucRepo = danhMucRepo;
    }

    /** Danh sách tất cả sự cố, lọc theo trạng thái (tham số ?trangThai=...). */
    @GetMapping("/admin")
    public String danhSach(@RequestParam(required = false) String trangThai, Model model) {
        String loc = (trangThai != null && TRANG_THAI.contains(trangThai)) ? trangThai : null;
        model.addAttribute("dsSuCo", suCoRepo.timTatCa(loc));
        model.addAttribute("trangThaiLoc", loc);
        return "admin";
    }

    /** Chi tiết một sự cố: form cập nhật + lịch sử xử lý. */
    @GetMapping("/admin/{maSc}")
    public String chiTiet(@PathVariable Long maSc, Model model) {
        SuCo suCo = suCoRepo.timTheoMa(maSc).orElse(null);
        if (suCo == null) {
            return "redirect:/admin";
        }
        model.addAttribute("suCo", suCo);
        model.addAttribute("dsLichSu", suCoRepo.timLichSu(maSc));
        model.addAttribute("dsBaiKn", kinhNghiemRepo.timTheoDanhMuc(null));
        model.addAttribute("dsDanhMuc", danhMucRepo.timTatCa());
        return "admin-chi-tiet";
    }

    /**
     * Lưu cập nhật. Trigger Oracle sẽ tự ghi lịch sử và ngày hoàn thành.
     * Nếu tick "luuKho" và không chọn bài có sẵn, nguyên nhân + cách khắc phục được tạo thành bài mới trong kho
     * kinh nghiệm (thuộc danh mục maDmKho) và gắn vào sự cố.
     */
    @PostMapping("/admin/{maSc}")
    public String capNhat(@PathVariable Long maSc, @RequestParam String trangThai,
                          @RequestParam(required = false) String nguyenNhan,
                          @RequestParam(required = false) String cachKhacPhuc,
                          @RequestParam(required = false) Long maBaiKn,
                          @RequestParam(required = false) Boolean luuKho,
                          @RequestParam(required = false) Long maDmKho,
                          HttpSession session, RedirectAttributes ra) {
        if (!TRANG_THAI.contains(trangThai)) {
            ra.addFlashAttribute("loi", "Trạng thái không hợp lệ");
            return "redirect:/admin/" + maSc;
        }
        NguoiDung admin = (NguoiDung) session.getAttribute("nguoiDung");
        Long maBaiGan = maBaiKn;
        if (Boolean.TRUE.equals(luuKho) && maBaiKn == null) {
            if (nguyenNhan == null || nguyenNhan.isBlank() || cachKhacPhuc == null || cachKhacPhuc.isBlank()
                    || maDmKho == null) {
                ra.addFlashAttribute("loi", "Để lưu vào kho kinh nghiệm, cần nhập nguyên nhân, cách khắc phục và chọn lĩnh vực");
                return "redirect:/admin/" + maSc;
            }
            SuCo suCo = suCoRepo.timTheoMa(maSc).orElse(null);
            if (suCo == null) {
                return "redirect:/admin";
            }
            maBaiGan = kinhNghiemRepo.taoMoi(suCo.tieuDe(), maDmKho, suCo.moTa(), nguyenNhan.trim(),
                    cachKhacPhuc.trim(), admin.maNd());
        }
        suCoRepo.capNhat(maSc, trangThai, nguyenNhan, cachKhacPhuc, maBaiGan, admin.maNd());
        ra.addFlashAttribute("thongBao", "Đã lưu cập nhật sự cố");
        return "redirect:/admin/" + maSc;
    }

    /** Xóa sự cố (kèm lịch sử xử lý của nó). */
    @PostMapping("/admin/{maSc}/xoa")
    public String xoa(@PathVariable Long maSc, RedirectAttributes ra) {
        suCoRepo.xoa(maSc);
        ra.addFlashAttribute("thongBao", "Đã xóa sự cố #" + maSc);
        return "redirect:/admin";
    }
}
