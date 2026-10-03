package vn.utc.suco.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Lưu ảnh đính kèm sự cố vào một thư mục trên đĩa (ngoài classpath, để còn ghi được khi chạy file .jar
 * đã đóng gói) rồi trả về đường dẫn URL để lưu vào CSDL (cột SU_CO.ANH_DINH_KEM).
 * Thư mục thật được WebConfig ánh xạ vào đường dẫn URL "/uploads/**".
 */
@Component
public class LuuAnh {
    private static final Map<String, String> DUOI_HOP_LE = Map.of(
            "image/jpeg", "jpg", "image/png", "png", "image/gif", "gif", "image/webp", "webp");

    private final Path thuMuc;

    public LuuAnh(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.thuMuc = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    /** @return đường dẫn URL (bắt đầu bằng /uploads/) để lưu vào CSDL, hoặc null nếu không có file. */
    public String luu(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String duoi = DUOI_HOP_LE.get(file.getContentType());
        if (duoi == null) {
            throw new IllegalArgumentException("Chỉ nhận ảnh JPG, PNG, GIF hoặc WEBP");
        }
        Files.createDirectories(thuMuc);
        String tenFile = UUID.randomUUID() + "." + duoi;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, thuMuc.resolve(tenFile), StandardCopyOption.REPLACE_EXISTING);
        }
        return "/uploads/" + tenFile;
    }
}
