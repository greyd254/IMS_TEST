package vn.utc.suco.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.repository.DanhMucRepo;
import vn.utc.suco.repository.KinhNghiemRepo;

/** Kho kinh nghiệm: USER và ADMIN đều xem được; chỉ ADMIN được thêm/sửa/xóa (dưới /admin/kinh-nghiem). */
@Controller
public class KinhNghiemController {
    private final KinhNghiemRepo kinhNghiemRepo;
    private final DanhMucRepo danhMucRepo;

    public KinhNghiemController(KinhNghiemRepo kinhNghiemRepo, DanhMucRepo danhMucRepo) {
        this.kinhNghiemRepo = kinhNghiemRepo;
        this.danhMucRepo = danhMucRepo;
    }

    /** Danh sách bài, lọc theo danh mục (?maDm=...). */
    @GetMapping("/kinh-nghiem")
    public String danhSach(@RequestParam(required = false) Long maDm, Model model) {
        model.addAttribute("dsBai", kinhNghiemRepo.timTheoDanhMuc(maDm));
        model.addAttribute("dsDanhMuc", danhMucRepo.timTatCa());
        model.addAttribute("maDmLoc", maDm);
        return "kinh-nghiem";
    }

    @GetMapping("/kinh-nghiem/{maBai}")
    public String chiTiet(@PathVariable Long maBai, Model model) {
        var bai = kinhNghiemRepo.timTheoMa(maBai);
        if (bai.isEmpty()) {
            return "redirect:/kinh-nghiem";
        }
        model.addAttribute("bai", bai.get());
        return "kinh-nghiem-chi-tiet";
    }

    /** Quản lý kho kinh nghiệm (admin): danh sách kèm form thêm mới. */
    @GetMapping("/admin/kinh-nghiem")
    public String quanLy(Model model) {
        model.addAttribute("dsBai", kinhNghiemRepo.timTheoDanhMuc(null));
        model.addAttribute("dsDanhMuc", danhMucRepo.timTatCa());
        return "kinh-nghiem-quan-ly";
    }

    @PostMapping("/admin/kinh-nghiem")
    public String them(@RequestParam String tieuDe, @RequestParam Long maDm,
                       @RequestParam(required = false) String trieuChung,
                       @RequestParam(required = false) String nguyenNhan,
                       @RequestParam String giaiPhap,
                       HttpSession session, RedirectAttributes ra) {
        if (tieuDe.isBlank() || giaiPhap.isBlank()) {
            ra.addFlashAttribute("loi", "Vui lòng nhập tiêu đề, chọn danh mục và nhập giải pháp");
            return "redirect:/admin/kinh-nghiem";
        }
        NguoiDung admin = (NguoiDung) session.getAttribute("nguoiDung");
        kinhNghiemRepo.taoMoi(tieuDe.trim(), maDm, trieuChung, nguyenNhan, giaiPhap.trim(), admin.maNd());
        ra.addFlashAttribute("thongBao", "Đã thêm bài kinh nghiệm. Bây giờ có thể chọn bài này khi xử lý sự cố.");
        return "redirect:/admin/kinh-nghiem";
    }

    @GetMapping("/admin/kinh-nghiem/{maBai}/sua")
    public String formSua(@PathVariable Long maBai, Model model) {
        var bai = kinhNghiemRepo.timTheoMa(maBai);
        if (bai.isEmpty()) {
            return "redirect:/admin/kinh-nghiem";
        }
        model.addAttribute("bai", bai.get());
        model.addAttribute("dsDanhMuc", danhMucRepo.timTatCa());
        return "kinh-nghiem-sua";
    }

    @PostMapping("/admin/kinh-nghiem/{maBai}")
    public String capNhat(@PathVariable Long maBai, @RequestParam String tieuDe, @RequestParam Long maDm,
                          @RequestParam(required = false) String trieuChung,
                          @RequestParam(required = false) String nguyenNhan,
                          @RequestParam String giaiPhap, RedirectAttributes ra) {
        if (tieuDe.isBlank() || giaiPhap.isBlank()) {
            ra.addFlashAttribute("loi", "Dữ liệu không hợp lệ");
            return "redirect:/admin/kinh-nghiem/" + maBai + "/sua";
        }
        kinhNghiemRepo.capNhat(maBai, tieuDe.trim(), maDm, trieuChung, nguyenNhan, giaiPhap.trim());
        ra.addFlashAttribute("thongBao", "Đã cập nhật bài kinh nghiệm");
        return "redirect:/admin/kinh-nghiem";
    }

    @PostMapping("/admin/kinh-nghiem/{maBai}/xoa")
    public String xoa(@PathVariable Long maBai, RedirectAttributes ra) {
        try {
            kinhNghiemRepo.xoa(maBai);
            ra.addFlashAttribute("thongBao", "Đã xóa bài kinh nghiệm");
        } catch (DataIntegrityViolationException e) {
            // Khóa ngoại FK_SU_CO_BAI_KN: bài này đang được một sự cố tham chiếu
            ra.addFlashAttribute("loi", "Không thể xóa: bài này đang được gắn với một sự cố. Hãy bỏ chọn ở sự cố đó trước.");
        }
        return "redirect:/admin/kinh-nghiem";
    }
}
