package vn.utc.suco.util;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import vn.utc.suco.model.SuCo;

/**
 * Ghi danh sách sự cố ra file Excel .xlsx. Dùng SXSSF (ghi dần, chỉ giữ một ít dòng trong bộ nhớ) nên không tốn RAM
 * dù danh sách dài. Mọi ô chữ đều lưu dạng chuỗi nên nội dung người dùng nhập như "=1+1" không bị Excel chạy thành công thức.
 */
public final class ExcelSuCo {
    private static final String[] TIEU_DE = {"Mã", "Tiêu đề", "Mô tả", "Người báo", "Thiết bị", "Phòng", "Danh mục",
            "Ưu tiên", "Trạng thái", "Admin xử lý", "Nguyên nhân", "Cách khắc phục", "Ngày báo", "Ngày hoàn thành"};
    // Đặt sẵn độ rộng: tự co theo nội dung (autoSizeColumn) rất chậm và cần phông chữ trên máy chủ
    private static final int[] DO_RONG = {7, 40, 50, 24, 28, 28, 22, 12, 13, 24, 40, 40, 17, 17};

    /** Giới hạn số ký tự trong một ô của Excel */
    private static final int TOI_DA_KY_TU = 32767;

    private ExcelSuCo() {
    }

    public static void ghi(List<SuCo> ds, OutputStream ra) throws IOException {
        SXSSFWorkbook wb = new SXSSFWorkbook(100);
        try {
            Sheet sh = wb.createSheet("Sự cố");
            Font dam = wb.createFont();
            dam.setBold(true);
            CellStyle kieuDau = wb.createCellStyle();
            kieuDau.setFont(dam);
            kieuDau.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            kieuDau.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle kieuNgay = wb.createCellStyle();
            kieuNgay.setDataFormat(wb.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

            Row dau = sh.createRow(0);
            for (int i = 0; i < TIEU_DE.length; i++) {
                Cell o = dau.createCell(i);
                o.setCellValue(TIEU_DE[i]);
                o.setCellStyle(kieuDau);
                sh.setColumnWidth(i, DO_RONG[i] * 256);
            }
            sh.createFreezePane(0, 1);   // luôn thấy tiêu đề khi cuộn
            sh.setAutoFilter(new CellRangeAddress(0, Math.max(ds.size(), 1), 0, TIEU_DE.length - 1));

            int dong = 1;
            for (SuCo sc : ds) {
                Row r = sh.createRow(dong++);
                r.createCell(0).setCellValue(sc.getMaSc());
                chu(r, 1, sc.getTieuDe());
                chu(r, 2, sc.getMoTa());
                chu(r, 3, sc.getTenNguoiBao());
                chu(r, 4, sc.getTenTb());
                chu(r, 5, sc.getTenPhong());
                chu(r, 6, sc.getTenDm());
                chu(r, 7, sc.getMucUuTien().getNhan());
                chu(r, 8, sc.getTrangThai().getNhan());
                chu(r, 9, sc.getTenAdmin());
                chu(r, 10, sc.getNguyenNhan());
                chu(r, 11, sc.getCachKhacPhuc());
                ngay(r, 12, sc.getNgayTao(), kieuNgay);
                ngay(r, 13, sc.getNgayHoanThanh(), kieuNgay);
            }
            wb.write(ra);
        } finally {
            wb.close();
            wb.dispose();   // xóa file tạm SXSSF
        }
    }

    private static void chu(Row r, int cot, String giaTri) {
        if (giaTri != null) {
            r.createCell(cot).setCellValue(giaTri.length() > TOI_DA_KY_TU ? giaTri.substring(0, TOI_DA_KY_TU) : giaTri);
        }
    }

    private static void ngay(Row r, int cot, LocalDateTime giaTri, CellStyle kieu) {
        if (giaTri != null) {
            Cell o = r.createCell(cot);
            o.setCellValue(giaTri);
            o.setCellStyle(kieu);
        }
    }
}
