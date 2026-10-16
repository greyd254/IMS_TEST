package vn.utc.suco.controller;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.model.SuCo;
import vn.utc.suco.repository.BinhLuanRepo;
import vn.utc.suco.repository.DanhMucRepo;
import vn.utc.suco.repository.PhanHoiRepo;
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
    private final PhanHoiRepo phanHoiRepo;
    private final BinhLuanRepo binhLuanRepo;
    private final LuuAnh luuAnh;

    public SuCoController(SuCoRepo suCoRepo, ThietBiRepo thietBiRepo, DanhMucRepo danhMucRepo,
                          PhanHoiRepo phanHoiRepo, BinhLuanRepo binhLuanRepo, LuuAnh luuAnh) {
        this.suCoRepo = suCoRepo;
        this.thietBiRepo = thietBiRepo;
        this.danhMucRepo = danhMucRepo;
        this.phanHoiRepo = phanHoiRepo;
        this.binhLuanRepo = binhLuanRepo;
        this.luuAnh = luuAnh;
    }

    @GetMapping("/user")
    public String trangNguoiDung(HttpSession session, Model model) {
        NguoiDung nd = (NguoiDung) session.getAttribute("nguoiDung");
        model.addAttribute("dsSuCo", suCoRepo.timTheoNguoiBao(nd.maNd()));
        model.addAttribute("dsThietBi", thietBiRepo.timTatCa());   // dropdown lấy từ CSDL
        model.addAttribute("dsDanhMuc", danhMucRepo.timTatCa());   // dropdown lấy từ CSDL
        model.addAttribute("dsHayGap", suCoRepo.timSuCoHayGap());  // "sự cố hay gặp" theo danh mục
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
        model.addAttribute("phanHoi", phanHoiRepo.timTheoSuCo(maSc).orElse(null));
        model.addAttribute("dsBinhLuan", binhLuanRepo.timTheoSuCo(maSc));
        return "user-chi-tiet";
    }

    /** Người báo gửi tin nhắn trao đổi với IT (không giới hạn số lần, không cần chờ sự cố hoàn thành). */
    @PostMapping("/user/{maSc}/binh-luan")
    public String guiBinhLuan(@PathVariable Long maSc, @RequestParam(required = false) String noiDung,
                              @RequestParam(required = false) MultipartFile tep,
                              HttpSession session, RedirectAttributes ra) {
        NguoiDung nd = (NguoiDung) session.getAttribute("nguoiDung");
        SuCo suCo = suCoRepo.timTheoMa(maSc).filter(sc -> sc.maNguoiBao().equals(nd.maNd())).orElse(null);
        if (suCo == null) {
            return "redirect:/user";
        }
        if ((noiDung == null || noiDung.isBlank()) && (tep == null || tep.isEmpty())) {
            ra.addFlashAttribute("loi", "Nhập nội dung hoặc chọn file để gửi");
            return "redirect:/user/" + maSc;
        }
        String duongDanTep;
        try {
            duongDanTep = luuAnh.luuTep(tep);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("loi", e.getMessage());
            return "redirect:/user/" + maSc;
        } catch (IOException e) {
            ra.addFlashAttribute("loi", "Không lưu được file đính kèm, vui lòng thử lại");
            return "redirect:/user/" + maSc;
        }
        binhLuanRepo.taoMoi(maSc, nd.maNd(), noiDung == null || noiDung.isBlank() ? null : noiDung.trim(), duongDanTep);
        return "redirect:/user/" + maSc;
    }

    /**
     * Người báo gửi phản hồi sau khi sự cố đã HOAN_THANH. Chỉ cho gửi 1 lần (UQ_PHAN_HOI_SU_CO
     * chặn ở tầng CSDL, bắt DuplicateKeyException ở đây để tránh lộ lỗi SQL ra giao diện).
     */
    @PostMapping("/user/{maSc}/phan-hoi")
    public String guiPhanHoi(@PathVariable Long maSc, @RequestParam int mucHaiLong,
                             @RequestParam(required = false) String noiDung,
                             HttpSession session, RedirectAttributes ra) {
        NguoiDung nd = (NguoiDung) session.getAttribute("nguoiDung");
        SuCo suCo = suCoRepo.timTheoMa(maSc).filter(sc -> sc.maNguoiBao().equals(nd.maNd())).orElse(null);
        if (suCo == null) {
            return "redirect:/user";
        }
        if (!"HOAN_THANH".equals(suCo.trangThai())) {
            ra.addFlashAttribute("loi", "Chỉ gửi phản hồi được khi sự cố đã hoàn thành");
            return "redirect:/user/" + maSc;
        }
        if (mucHaiLong < 1 || mucHaiLong > 5) {
            ra.addFlashAttribute("loi", "Mức hài lòng phải từ 1 đến 5");
            return "redirect:/user/" + maSc;
        }
        try {
            phanHoiRepo.taoMoi(maSc, nd.maNd(), mucHaiLong, noiDung);
            ra.addFlashAttribute("thongBao", "Đã gửi phản hồi, cảm ơn bạn");
        } catch (DuplicateKeyException e) {
            ra.addFlashAttribute("loi", "Sự cố này đã được phản hồi rồi");
        }
        return "redirect:/user/" + maSc;
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
