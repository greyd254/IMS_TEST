package vn.utc.suco.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.utc.suco.repository.DanhMucRepo;

/** Admin quản lý danh mục sự cố: thêm, sửa, xóa (/admin/** đã được AuthInterceptor chặn cho USER). */
@Controller
public class DanhMucController {
    private final DanhMucRepo danhMucRepo;

    public DanhMucController(DanhMucRepo danhMucRepo) {
        this.danhMucRepo = danhMucRepo;
    }

    @GetMapping("/admin/danh-muc")
    public String danhSach(Model model) {
        model.addAttribute("dsDanhMuc", danhMucRepo.timTatCa());
        return "danh-muc";
    }

    @PostMapping("/admin/danh-muc")
    public String them(@RequestParam String tenDm, @RequestParam(required = false) String moTa,
                       RedirectAttributes ra) {
        if (tenDm == null || tenDm.isBlank()) {
            ra.addFlashAttribute("loi", "Vui lòng nhập tên danh mục");
            return "redirect:/admin/danh-muc";
        }
        try {
            danhMucRepo.taoMoi(tenDm.trim(), moTa);
            ra.addFlashAttribute("thongBao", "Đã thêm danh mục");
        } catch (DuplicateKeyException e) {
            // Vi phạm UQ_DANH_MUC_TEN
            ra.addFlashAttribute("loi", "Tên danh mục này đã tồn tại");
        }
        return "redirect:/admin/danh-muc";
    }

    @GetMapping("/admin/danh-muc/{maDm}/sua")
    public String formSua(@PathVariable Long maDm, Model model) {
        var dm = danhMucRepo.timTheoMa(maDm);
        if (dm.isEmpty()) {
            return "redirect:/admin/danh-muc";
        }
        model.addAttribute("dm", dm.get());
        return "danh-muc-sua";
    }

    @PostMapping("/admin/danh-muc/{maDm}")
    public String capNhat(@PathVariable Long maDm, @RequestParam String tenDm,
                          @RequestParam(required = false) String moTa, RedirectAttributes ra) {
        if (tenDm == null || tenDm.isBlank()) {
            ra.addFlashAttribute("loi", "Vui lòng nhập tên danh mục");
            return "redirect:/admin/danh-muc/" + maDm + "/sua";
        }
        try {
            danhMucRepo.capNhat(maDm, tenDm.trim(), moTa);
        } catch (DuplicateKeyException e) {
            ra.addFlashAttribute("loi", "Tên danh mục này đã tồn tại");
            return "redirect:/admin/danh-muc/" + maDm + "/sua";
        }
        ra.addFlashAttribute("thongBao", "Đã cập nhật danh mục");
        return "redirect:/admin/danh-muc";
    }

    @PostMapping("/admin/danh-muc/{maDm}/xoa")
    public String xoa(@PathVariable Long maDm, RedirectAttributes ra) {
        try {
            danhMucRepo.xoa(maDm);
            ra.addFlashAttribute("thongBao", "Đã xóa danh mục");
        } catch (DataIntegrityViolationException e) {
            // Khóa ngoại: còn sự cố hoặc bài kinh nghiệm thuộc danh mục này
            ra.addFlashAttribute("loi", "Không thể xóa: còn sự cố hoặc bài kinh nghiệm thuộc danh mục này");
        }
        return "redirect:/admin/danh-muc";
    }
}
