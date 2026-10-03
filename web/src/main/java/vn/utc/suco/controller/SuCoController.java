package vn.utc.suco.controller;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.repository.DanhMucRepo;
import vn.utc.suco.repository.SuCoRepo;
import vn.utc.suco.repository.ThietBiRepo;
import vn.utc.suco.util.LuuAnh;

/** Màn hình người dùng: báo sự cố và xem sự cố của chính mình. */
@Controller
public class SuCoController {
    private static final Set<String> MUC_UU_TIEN = Set.of("THAP", "TRUNG_BINH", "CAO");

    private final SuCoRepo suCoRepo;
    private final ThietBiRepo thietBiRepo;
    private final DanhMucRepo danhMucRepo;
    private final LuuAnh luuAnh;

    public SuCoController(SuCoRepo suCoRepo, ThietBiRepo thietBiRepo, DanhMucRepo danhMucRepo, LuuAnh luuAnh) {
        this.suCoRepo = suCoRepo;
        this.thietBiRepo = thietBiRepo;
        this.danhMucRepo = danhMucRepo;
        this.luuAnh = luuAnh;
    }

    @GetMapping("/user")
    public String trangNguoiDung(HttpSession session, Model model) {
        NguoiDung nd = (NguoiDung) session.getAttribute("nguoiDung");
        model.addAttribute("dsSuCo", suCoRepo.timTheoNguoiBao(nd.maNd()));
        model.addAttribute("dsThietBi", thietBiRepo.timTatCa());   // dropdown lấy từ CSDL
        model.addAttribute("dsDanhMuc", danhMucRepo.timTatCa());   // dropdown lấy từ CSDL
        return "user";
    }

    /** Chi tiết sự cố của chính người dùng: trạng thái, nguyên nhân, cách khắc phục và lịch sử xử lý. */
    @GetMapping("/user/{maSc}")
    public String chiTietCuaToi(@PathVariable Long maSc, HttpSession session, Model model) {
        NguoiDung nd = (NguoiDung) session.getAttribute("nguoiDung");
        var suCo = suCoRepo.timTheoMa(maSc).filter(sc -> sc.maNguoiBao().equals(nd.maNd()));
        if (suCo.isEmpty()) {
            return "redirect:/user";
        }
        model.addAttribute("suCo", suCo.get());
        model.addAttribute("dsLichSu", suCoRepo.timLichSu(maSc));
        return "user-chi-tiet";
    }

    @PostMapping("/user/bao-cao")
    public String baoSuCo(@RequestParam String tieuDe, @RequestParam(required = false) String moTa,
                          @RequestParam(required = false) Long maTb, @RequestParam Long maDm,
                          @RequestParam String mucUuTien,
                          @RequestParam(required = false) MultipartFile anh,
                          HttpSession session, RedirectAttributes ra) {
        NguoiDung nd = (NguoiDung) session.getAttribute("nguoiDung");
        if (tieuDe.isBlank() || !MUC_UU_TIEN.contains(mucUuTien)) {
            ra.addFlashAttribute("loi", "Vui lòng nhập tiêu đề và chọn mức ưu tiên hợp lệ");
            return "redirect:/user";
        }
        String duongDanAnh;
        try {
            duongDanAnh = luuAnh.luu(anh);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("loi", e.getMessage());
            return "redirect:/user";
        } catch (IOException e) {
            ra.addFlashAttribute("loi", "Không lưu được ảnh đính kèm, vui lòng thử lại");
            return "redirect:/user";
        }
        suCoRepo.taoMoi(tieuDe.trim(), moTa, nd.maNd(), maTb, maDm, mucUuTien, duongDanAnh);
        ra.addFlashAttribute("thongBao", "Đã gửi báo cáo sự cố");
        return "redirect:/user";
    }
}
