package vn.utc.suco.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * File Excel để nhập thiết bị hàng loạt: tạo file mẫu (có sẵn danh sách thả xuống) và đọc lại file người dùng tải lên.
 * Chỉ kiểm tra dữ liệu trong file, chưa ghi CSDL: controller chỉ ghi khi không còn dòng lỗi nào (nhập cả file hoặc không nhập).
 */
public final class ExcelThietBi {
    /** Giới hạn số dòng mỗi lần nhập, tránh giữ quá nhiều dữ liệu trong bộ nhớ */
    public static final int SO_DONG_TOI_DA = 1000;

    /** Số dòng lỗi hiện ra trên trang, phần còn lại chỉ báo tổng số */
    private static final int SO_LOI_HIEN = 20;

    private static final String[] TIEU_DE = {"Tên thiết bị *", "Loại *", "Phòng *", "Tình trạng", "Nhà cung cấp",
            "Ngày lắp đặt (dd/mm/yyyy)", "Hướng dẫn sử dụng"};
    private static final int[] DO_RONG = {34, 18, 34, 14, 26, 24, 50};

    private static final LocalDate NGAY_NHO_NHAT = LocalDate.of(1990, 1, 1);
    private static final DateTimeFormatter NGAY_VN = DateTimeFormatter.ofPattern("d/M/uuuu");

    private ExcelThietBi() {
    }

    /** Một dòng hợp lệ đọc được từ file. */
    public static final class Dong {
        private final String tenTb;
        private final String loaiTb;
        private final long maPhong;
        private final String tinhTrang;
        private final String nhaCungCap;
        private final LocalDate ngayLapDat;
        private final String hdsd;

        Dong(String tenTb, String loaiTb, long maPhong, String tinhTrang, String nhaCungCap, LocalDate ngayLapDat,
             String hdsd) {
            this.tenTb = tenTb;
            this.loaiTb = loaiTb;
            this.maPhong = maPhong;
            this.tinhTrang = tinhTrang;
            this.nhaCungCap = nhaCungCap;
            this.ngayLapDat = ngayLapDat;
            this.hdsd = hdsd;
        }

        public String getTenTb() {
            return tenTb;
        }

        public String getLoaiTb() {
            return loaiTb;
        }

        public long getMaPhong() {
            return maPhong;
        }

        public String getTinhTrang() {
            return tinhTrang;
        }

        public String getNhaCungCap() {
            return nhaCungCap;
        }

        public LocalDate getNgayLapDat() {
            return ngayLapDat;
        }

        public String getHdsd() {
            return hdsd;
        }
    }

    /** Kết quả đọc file: các dòng hợp lệ và các câu báo lỗi (có số dòng Excel). Có lỗi thì không nhập dòng nào. */
    public static final class KetQua {
        private final List<Dong> dsDong = new ArrayList<>();
        private final List<String> dsLoi = new ArrayList<>();

        public List<Dong> getDsDong() {
            return dsDong;
        }

        public List<String> getDsLoi() {
            return dsLoi;
        }

        void themLoi(String loi) {
            dsLoi.add(loi);
        }

        /** Rút gọn danh sách lỗi để hiển thị. */
        public List<String> loiDeHien() {
            if (dsLoi.size() <= SO_LOI_HIEN) {
                return dsLoi;
            }
            List<String> kq = new ArrayList<>(dsLoi.subList(0, SO_LOI_HIEN));
            kq.add("... và " + (dsLoi.size() - SO_LOI_HIEN) + " lỗi khác");
            return kq;
        }
    }

    /** Khóa so trùng: cùng phòng, cùng loại, cùng tên (không phân biệt hoa thường, bỏ khoảng trắng hai đầu). */
    public static String khoa(long maPhong, String loaiTb, String tenTb) {
        return maPhong + "|" + loaiTb + "|" + chuanHoa(tenTb);
    }

    /** Dạng so sánh: chuẩn Unicode NFC (Excel/Mac có thể lưu tiếng Việt ở dạng tổ hợp), bỏ khoảng trắng, chữ thường. */
    private static String chuanHoa(String s) {
        return Normalizer.normalize(s.trim(), Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }

    /**
     * Ghi file mẫu: sheet "Thiết bị" để điền, các ô Loại/Phòng/Tình trạng là danh sách thả xuống lấy từ sheet ẩn
     * (không nhúng thẳng vào công thức vì Excel giới hạn 255 ký tự, danh sách phòng dễ vượt).
     */
    public static void ghiMau(Map<String, String> loai, Map<String, String> tinhTrang, Set<String> phong,
                              OutputStream ra) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Thiết bị");
            Row dau = sh.createRow(0);
            CellStyle kieuDau = kieuTieuDe(wb);
            for (int i = 0; i < TIEU_DE.length; i++) {
                Cell o = dau.createCell(i);
                o.setCellValue(TIEU_DE[i]);
                o.setCellStyle(kieuDau);
                sh.setColumnWidth(i, DO_RONG[i] * 256);
            }
            sh.createFreezePane(0, 1);

            Sheet ds = wb.createSheet("DanhSach");
            ghiCot(ds, 0, loai.values());
            ghiCot(ds, 1, phong);
            ghiCot(ds, 2, tinhTrang.values());
            wb.setSheetHidden(wb.getSheetIndex(ds), true);

            DataValidationHelper h = sh.getDataValidationHelper();
            themThaXuong(sh, h, 1, "DanhSach!$A$1:$A$" + loai.size());
            if (!phong.isEmpty()) {
                themThaXuong(sh, h, 2, "DanhSach!$B$1:$B$" + phong.size());
            }
            themThaXuong(sh, h, 3, "DanhSach!$C$1:$C$" + tinhTrang.size());
            wb.write(ra);
        }
    }

    private static void ghiCot(Sheet ds, int cot, Iterable<String> giaTri) {
        int i = 0;
        for (String g : giaTri) {
            Row r = ds.getRow(i) != null ? ds.getRow(i) : ds.createRow(i);
            r.createCell(cot).setCellValue(g);
            i++;
        }
    }

    private static void themThaXuong(Sheet sh, DataValidationHelper h, int cot, String nguon) {
        DataValidation dv = h.createValidation(h.createFormulaListConstraint(nguon),
                new CellRangeAddressList(1, SO_DONG_TOI_DA, cot, cot));
        dv.setShowErrorBox(true);
        dv.createErrorBox("Giá trị không hợp lệ", "Hãy chọn một giá trị trong danh sách");
        sh.addValidationData(dv);
    }

    private static CellStyle kieuTieuDe(Workbook wb) {
        Font dam = wb.createFont();
        dam.setBold(true);
        CellStyle k = wb.createCellStyle();
        k.setFont(dam);
        k.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        k.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return k;
    }

    /**
     * Đọc và kiểm tra toàn bộ file. loai/tinhTrang: mã trong CSDL -> nhãn; phong: nhãn phòng -> mã phòng;
     * daCo: khóa (xem {@link #khoa}) của các thiết bị đã có trong CSDL.
     * Ném IOException/lỗi của POI nếu không phải file Excel hợp lệ.
     */
    public static KetQua doc(InputStream vao, Map<String, String> loai, Map<String, String> tinhTrang,
                             Map<String, Long> phong, Set<String> daCo) throws IOException {
        KetQua kq = new KetQua();
        Map<String, String> traLoai = traCuu(loai);
        Map<String, String> traTinhTrang = traCuu(tinhTrang);
        Map<String, Long> traPhong = new HashMap<>();
        phong.forEach((nhan, ma) -> traPhong.put(chuanHoa(nhan), ma));
        Set<String> daGap = new HashSet<>();

        try (XSSFWorkbook wb = new XSSFWorkbook(vao)) {
            Sheet sh = wb.getSheetAt(0);
            DataFormatter dinhDang = new DataFormatter();
            dinhDang.setUseCachedValuesForFormulaCells(true);   // không tính lại công thức từ file người dùng gửi

            if (!dungMau(sh.getRow(0), dinhDang)) {
                kq.themLoi("File không đúng mẫu: dòng đầu phải là tiêu đề cột của file mẫu. "
                        + "Hãy tải file mẫu và điền vào đó.");
                return kq;
            }
            int soDong = 0;
            for (int i = 1; i <= sh.getLastRowNum(); i++) {
                Row r = sh.getRow(i);
                if (dongTrong(r, dinhDang)) {
                    continue;
                }
                if (++soDong > SO_DONG_TOI_DA) {
                    kq.themLoi("Mỗi lần chỉ nhập tối đa " + SO_DONG_TOI_DA + " thiết bị, hãy chia nhỏ file");
                    return kq;
                }
                docDong(r, i + 1, dinhDang, traLoai, traTinhTrang, traPhong, daCo, daGap, kq);
            }
            if (soDong == 0 && kq.getDsLoi().isEmpty()) {
                kq.themLoi("File chưa có dòng thiết bị nào");
            }
        }
        return kq;
    }

    private static void docDong(Row r, int dong, DataFormatter f, Map<String, String> traLoai,
                                Map<String, String> traTinhTrang, Map<String, Long> traPhong, Set<String> daCo,
                                Set<String> daGap, KetQua kq) {
        int loiTruoc = kq.getDsLoi().size();
        String ten = chu(r, 0, f);
        String nhanLoai = chu(r, 1, f);
        String nhanPhong = chu(r, 2, f);
        String nhanTinhTrang = chu(r, 3, f);
        String nhaCungCap = chu(r, 4, f);
        String hdsd = chu(r, 6, f);

        if (ten.isEmpty()) {
            kq.themLoi("Dòng " + dong + ": thiếu tên thiết bị");
        } else if (ten.length() > 150) {
            kq.themLoi("Dòng " + dong + ": tên thiết bị dài quá 150 ký tự");
        }
        String loai = traLoai.get(chuanHoa(nhanLoai));
        if (loai == null) {
            kq.themLoi("Dòng " + dong + (nhanLoai.isEmpty() ? ": thiếu loại thiết bị"
                    : ": không có loại thiết bị \"" + nhanLoai + "\""));
        }
        Long maPhong = traPhong.get(chuanHoa(nhanPhong));
        if (maPhong == null) {
            kq.themLoi("Dòng " + dong + (nhanPhong.isEmpty() ? ": thiếu phòng"
                    : ": không tìm thấy phòng \"" + nhanPhong + "\" (hãy chọn trong danh sách của file mẫu)"));
        }
        // Để trống tình trạng = đang hoạt động, giống giá trị mặc định của cột trong CSDL
        String tinhTrang = nhanTinhTrang.isEmpty() ? "HOAT_DONG" : traTinhTrang.get(chuanHoa(nhanTinhTrang));
        if (tinhTrang == null) {
            kq.themLoi("Dòng " + dong + ": không có tình trạng \"" + nhanTinhTrang + "\"");
        }
        if (nhaCungCap.length() > 150) {
            kq.themLoi("Dòng " + dong + ": nhà cung cấp dài quá 150 ký tự");
        }
        if (hdsd.length() > 2000) {
            kq.themLoi("Dòng " + dong + ": hướng dẫn sử dụng dài quá 2000 ký tự");
        }
        LocalDate ngay = null;
        try {
            ngay = docNgay(r.getCell(5), f);
            if (ngay != null && (ngay.isBefore(NGAY_NHO_NHAT) || ngay.isAfter(LocalDate.now()))) {
                kq.themLoi("Dòng " + dong + ": ngày lắp đặt phải trong khoảng từ " + NGAY_NHO_NHAT.getYear()
                        + " đến hôm nay");
            }
        } catch (DateTimeParseException e) {
            kq.themLoi("Dòng " + dong + ": ngày lắp đặt không hợp lệ (cần dạng dd/mm/yyyy)");
        }

        if (kq.getDsLoi().size() > loiTruoc) {
            return;
        }
        String khoa = khoa(maPhong, loai, ten);
        if (daCo.contains(khoa)) {
            kq.themLoi("Dòng " + dong + ": phòng \"" + nhanPhong + "\" đã có thiết bị cùng loại, cùng tên \"" + ten + "\"");
        } else if (!daGap.add(khoa)) {
            kq.themLoi("Dòng " + dong + ": trùng với một dòng phía trên trong file (cùng phòng, loại, tên)");
        } else {
            kq.getDsDong().add(new Dong(ten, loai, maPhong, tinhTrang, nhaCungCap.isEmpty() ? null : nhaCungCap, ngay,
                    hdsd.isEmpty() ? null : hdsd));
        }
    }

    /** Bảng tra chấp nhận cả nhãn ("Máy in") lẫn mã trong CSDL ("MAY_IN"). */
    private static Map<String, String> traCuu(Map<String, String> maNhan) {
        Map<String, String> kq = new HashMap<>();
        maNhan.forEach((ma, nhan) -> {
            kq.put(chuanHoa(ma), ma);
            kq.put(chuanHoa(nhan), ma);
        });
        return kq;
    }

    private static boolean dungMau(Row dau, DataFormatter f) {
        if (dau == null) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            if (!chuanHoa(TIEU_DE[i]).equals(chuanHoa(chu(dau, i, f)))) {
                return false;
            }
        }
        return true;
    }

    private static boolean dongTrong(Row r, DataFormatter f) {
        if (r == null) {
            return true;
        }
        for (int i = 0; i < TIEU_DE.length; i++) {
            if (!chu(r, i, f).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static String chu(Row r, int cot, DataFormatter f) {
        Cell o = r.getCell(cot);
        return o == null ? "" : f.formatCellValue(o).trim();
    }

    /** Ô ngày có thể là ngày thật của Excel, hoặc chữ dạng dd/mm/yyyy, yyyy-mm-dd; để trống = null. */
    private static LocalDate docNgay(Cell o, DataFormatter f) {
        if (o == null) {
            return null;
        }
        CellType kieu = o.getCellType() == CellType.FORMULA ? o.getCachedFormulaResultType() : o.getCellType();
        if (kieu == CellType.NUMERIC && DateUtil.isCellDateFormatted(o)) {
            return o.getLocalDateTimeCellValue().toLocalDate();
        }
        String s = f.formatCellValue(o).trim();
        if (s.isEmpty()) {
            return null;
        }
        return s.contains("/") ? LocalDate.parse(s, NGAY_VN) : LocalDate.parse(s);
    }
}
