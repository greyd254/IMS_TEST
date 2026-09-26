package vn.utc.suco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.utc.suco.repository.DanhMucRepo;
import vn.utc.suco.repository.KinhNghiemRepo;

/** Kho kinh nghiệm: USER và ADMIN đều xem được. */
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
}
