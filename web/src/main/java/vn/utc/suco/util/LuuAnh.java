package vn.utc.suco.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Lưu file đính kèm (ảnh sự cố, file trao đổi) vào một thư mục trên đĩa (ngoài classpath, để còn ghi
 * được khi chạy file .jar đã đóng gói) rồi trả về đường dẫn URL để lưu vào CSDL.
 * Thư mục thật được WebConfig ánh xạ vào đường dẫn URL "/uploads/**".
 */
@Component
public class LuuAnh {
    private static final Map<String, String> DUOI_ANH = Map.of(
            "image/jpeg", "jpg", "image/png", "png", "image/gif", "gif", "image/webp", "webp");

    // Tin nhắn trao đổi nhận thêm vài loại file văn phòng thường dùng, ngoài ảnh
    private static final Map<String, String> DUOI_TEP;

    static {
        Map<String, String> m = new HashMap<>(DUOI_ANH);
        m.put("application/pdf", "pdf");
        m.put("application/msword", "doc");
        m.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        m.put("text/plain", "txt");
        m.put("application/zip", "zip");
        DUOI_TEP = Map.copyOf(m);
    }

    private final Path thuMuc;

    public LuuAnh(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.thuMuc = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    /** Dùng khi báo sự cố: chỉ nhận ảnh (cột SU_CO.ANH_DINH_KEM). */
    public String luu(MultipartFile file) throws IOException {
        return luu(file, DUOI_ANH, "Chỉ nhận ảnh JPG, PNG, GIF hoặc WEBP");
    }

    /** Dùng khi gửi tin nhắn trao đổi: nhận thêm PDF, Word, TXT, ZIP ngoài ảnh. */
    public String luuTep(MultipartFile file) throws IOException {
        return luu(file, DUOI_TEP, "Chỉ nhận ảnh, PDF, Word (.doc/.docx), TXT hoặc ZIP");
    }

    /** @return đường dẫn URL (bắt đầu bằng /uploads/) để lưu vào CSDL, hoặc null nếu không có file. */
    private String luu(MultipartFile file, Map<String, String> duoiHopLe, String thongBaoLoi) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String duoi = duoiHopLe.get(file.getContentType());
        if (duoi == null) {
            throw new IllegalArgumentException(thongBaoLoi);
        }
        Files.createDirectories(thuMuc);
        String tenFile = UUID.randomUUID() + "." + duoi;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, thuMuc.resolve(tenFile), StandardCopyOption.REPLACE_EXISTING);
        }
        return "/uploads/" + tenFile;
    }
}
