package vn.utc.suco.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.utc.suco.model.BinhLuan;
import vn.utc.suco.model.NguoiDung;
import vn.utc.suco.model.SuCo;
import vn.utc.suco.repository.BinhLuanRepo;
import vn.utc.suco.repository.DanhMucRepo;
import vn.utc.suco.repository.KinhNghiemRepo;
import vn.utc.suco.repository.PhanHoiRepo;
import vn.utc.suco.repository.SuCoRepo;
import vn.utc.suco.util.ExcelSuCo;
import vn.utc.suco.util.LuuAnh;
import vn.utc.suco.util.TimKiem;

/** Màn hình admin (chỉ vai trò ADMIN, đã được AuthInterceptor chặn cho USER). */
@Controller
public class AdminController {
    private static final Set<String> TRANG_THAI = Set.of("MOI", "DANG_XU_LY", "HOAN_THANH");

    private final SuCoRepo suCoRepo;
    private final KinhNghiemRepo kinhNghiemRepo;
    private final DanhMucRepo danhMucRepo;
    private final PhanHoiRepo phanHoiRepo;
    private final BinhLuanRepo binhLuanRepo;
    private final LuuAnh luuAnh;

    public AdminController(SuCoRepo suCoRepo, KinhNghiemRepo kinhNghiemRepo, DanhMucRepo danhMucRepo,
                           PhanHoiRepo phanHoiRepo, BinhLuanRepo binhLuanRepo, LuuAnh luuAnh) {
        this.suCoRepo = suCoRepo;
        this.kinhNghiemRepo = kinhNghiemRepo;
        this.danhMucRepo = danhMucRepo;
        this.phanHoiRepo = phanHoiRepo;
        this.binhLuanRepo = binhLuanRepo;
        this.luuAnh = luuAnh;
    }

    /** Danh sách tất cả sự cố, lọc theo trạng thái (tham số ?trangThai=...). */
    @GetMapping("/admin")
    public String danhSach(@RequestParam(required = false) String trangThai, Model model) {
        String loc = (trangThai != null && TRANG_THAI.contains(trangThai)) ? trangThai : null;
        model.addAttribute("dsSuCo", suCoRepo.timTatCa(loc));
        model.addAttribute("trangThaiLoc", loc);
        model.addAttribute("dem", suCoRepo.demTheoTrangThai());   // số liệu cho các ô lọc nhanh
        return "admin";
    }

    /**
     * Polling AJAX cho trang danh sách: /admin tự hiện sự cố mới báo lên đầu bảng mà không cần F5.
     * Chỉ trả về gì đó khi đang xem "Tất cả" hoặc "Mới" (sự cố mới báo luôn có trạng thái MOI nên không
     * thể xuất hiện ở 2 bộ lọc còn lại) - app.js chỉ gọi endpoint này ở 2 chế độ xem đó (xem data-su-co-bat).
     */
    @GetMapping("/admin/moi")
    @ResponseBody
    public List<Map<String, Object>> suCoMoi(@RequestParam(required = false) String trangThai,
                                              @RequestParam(defaultValue = "0") Long sauMa) {
        String loc = (trangThai != null && TRANG_THAI.contains(trangThai)) ? trangThai : null;
        if (loc != null && !loc.equals("MOI")) {
            return List.of();
        }
        return suCoRepo.timMoiHon(loc, sauMa).stream().map(AdminController::jsonSuCo).toList();
    }

    private static final Map<String, String> NHAN_UU_TIEN = Map.of("THAP", "Thấp", "TRUNG_BINH", "Trung bình", "CAO", "Cao");
    private static final Map<String, String> BADGE_UU_TIEN =
            Map.of("THAP", "text-bg-info", "TRUNG_BINH", "text-bg-primary", "CAO", "text-bg-danger");
    private static final DateTimeFormatter DINH_DANG_LUC = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static Map<String, Object> jsonSuCo(SuCo sc) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("maSc", sc.maSc());
        m.put("tieuDe", sc.tieuDe());
        m.put("tenNguoiBao", sc.tenNguoiBao());
        m.put("tenTb", sc.tenTb());
        m.put("tenPhong", sc.tenPhong());
        m.put("tenDm", sc.tenDm());
        m.put("mucUuTienNhan", NHAN_UU_TIEN.get(sc.mucUuTien()));
        m.put("mucUuTienBadge", BADGE_UU_TIEN.get(sc.mucUuTien()));
        m.put("ngayTao", sc.ngayTao().format(DINH_DANG_LUC));
        return m;
    }

    /** Xuất các sự cố đang được lọc (mọi trang, không chỉ trang đang xem) ra file Excel. */
    @GetMapping("/admin/xuat-excel")
    public void xuatExcel(@RequestParam(required = false) String trangThai, @RequestParam(required = false) String q,
                          HttpServletResponse res) throws IOException {
        String loc = (trangThai != null && TRANG_THAI.contains(trangThai)) ? trangThai : null;
        // Lấy dữ liệu trước khi đặt header: lỗi CSDL vẫn ra trang lỗi bình thường, không ra file hỏng
        List<SuCo> ds = suCoRepo.timDeXuat(loc, TimKiem.chuanHoa(q));
        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        res.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"su-co-" + LocalDate.now() + ".xlsx\"");
        ExcelSuCo.ghi(ds, res.getOutputStream());
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
        model.addAttribute("phanHoi", phanHoiRepo.timTheoSuCo(maSc).orElse(null));
        model.addAttribute("dsBinhLuan", binhLuanRepo.timTheoSuCo(maSc));
        return "admin-chi-tiet";
    }

    /** Admin gửi tin nhắn trao đổi với người báo (không giới hạn số lần, mọi sự cố, kèm file được). */
    @PostMapping("/admin/{maSc}/binh-luan")
    public String guiBinhLuan(@PathVariable Long maSc, @RequestParam(required = false) String noiDung,
                              @RequestParam(required = false) MultipartFile tep,
                              HttpSession session, RedirectAttributes ra) {
        if (suCoRepo.timTheoMa(maSc).isEmpty()) {
            return "redirect:/admin";
        }
        if ((noiDung == null || noiDung.isBlank()) && (tep == null || tep.isEmpty())) {
            ra.addFlashAttribute("loi", "Nhập nội dung hoặc chọn file để gửi");
            return "redirect:/admin/" + maSc;
        }
        NguoiDung admin = (NguoiDung) session.getAttribute("nguoiDung");
        String duongDanTep;
        try {
            duongDanTep = luuAnh.luuTep(tep);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("loi", e.getMessage());
            return "redirect:/admin/" + maSc;
        } catch (IOException e) {
            ra.addFlashAttribute("loi", "Không lưu được file đính kèm, vui lòng thử lại");
            return "redirect:/admin/" + maSc;
        }
        binhLuanRepo.taoMoi(maSc, admin.maNd(), noiDung == null || noiDung.isBlank() ? null : noiDung.trim(),
                duongDanTep);
        return "redirect:/admin/" + maSc;
    }

    /** Polling AJAX: trang chi tiết gọi lại vài giây 1 lần để lấy tin nhắn mới mà không cần tải lại trang. */
    @GetMapping("/admin/{maSc}/binh-luan")
    @ResponseBody
    public List<Map<String, Object>> binhLuanMoi(@PathVariable Long maSc,
                                                  @RequestParam(defaultValue = "0") Long sauMa) {
        if (suCoRepo.timTheoMa(maSc).isEmpty()) {
            return List.of();
        }
        return binhLuanRepo.timMoiHon(maSc, sauMa).stream().map(BinhLuan::dangJson).toList();
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
