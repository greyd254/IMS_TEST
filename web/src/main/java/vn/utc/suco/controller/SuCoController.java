package vn.utc.suco.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.repository.DanhMucRepo;
import vn.utc.suco.repository.SuCoRepo;
import vn.utc.suco.repository.ThietBiRepo;

/** Màn hình người dùng: báo sự cố và xem sự cố của chính mình. */
@Controller
public class SuCoController {
    private static final Set<String> MUC_UU_TIEN = Set.of("THAP", "TRUNG_BINH", "CAO");

    private final SuCoRepo suCoRepo;
    private final ThietBiRepo thietBiRepo;
    private final DanhMucRepo danhMucRepo;

    public SuCoController(SuCoRepo suCoRepo, ThietBiRepo thietBiRepo, DanhMucRepo danhMucRepo) {
        this.suCoRepo = suCoRepo;
        this.thietBiRepo = thietBiRepo;
        this.danhMucRepo = danhMucRepo;
    }

    @GetMapping("/user")
    public String trangNguoiDung(HttpSession session, Model model) {
        NguoiDung nd = (NguoiDung) session.getAttribute("nguoiDung");
        model.addAttribute("dsSuCo", suCoRepo.timTheoNguoiBao(nd.maNd()));
        model.addAttribute("dsThietBi", thietBiRepo.timTatCa());   // dropdown lấy từ CSDL
        model.addAttribute("dsDanhMuc", danhMucRepo.timTatCa());   // dropdown lấy từ CSDL
        return "user";
    }

    @PostMapping("/user/bao-cao")
    public String baoSuCo(@RequestParam String tieuDe, @RequestParam(required = false) String moTa,
                          @RequestParam(required = false) Long maTb, @RequestParam Long maDm,
                          @RequestParam String mucUuTien,
                          HttpSession session, RedirectAttributes ra) {
        NguoiDung nd = (NguoiDung) session.getAttribute("nguoiDung");
        if (tieuDe.isBlank() || !MUC_UU_TIEN.contains(mucUuTien)) {
            ra.addFlashAttribute("loi", "Vui lòng nhập tiêu đề và chọn mức ưu tiên hợp lệ");
            return "redirect:/user";
        }
        suCoRepo.taoMoi(tieuDe.trim(), moTa, nd.maNd(), maTb, maDm, mucUuTien);
        ra.addFlashAttribute("thongBao", "Đã gửi báo cáo sự cố");
        return "redirect:/user";
    }
}
