package vn.utc.suco.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

/**
 * Lưu file đính kèm vào một thư mục trên đĩa (ngoài classpath, để còn ghi được khi chạy file .jar đã
 * đóng gói) rồi trả về đường dẫn URL để lưu vào CSDL. Thư mục thật được WebConfig ánh xạ vào "/uploads/**".
 * Đây là lớp trừu tượng (abstract class): phần lưu file xuống đĩa giống nhau cho mọi nơi gọi, chỉ có
 * danh sách loại file được phép (duoiHopLe) và thông báo lỗi là khác nhau giữa các lớp con.
 */
public abstract class BoLuuTru {
    private final Path thuMuc;

    protected BoLuuTru(String thuMucConfig) {
        this.thuMuc = Path.of(thuMucConfig).toAbsolutePath().normalize();
    }

    /** @return đường dẫn URL (bắt đầu bằng /uploads/) để lưu vào CSDL, hoặc null nếu không có file. */
    public final String luu(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String duoi = duoiHopLe().get(file.getContentType());
        if (duoi == null) {
            throw new FileKhongHopLeException(thongBaoLoiLoai());
        }
        Files.createDirectories(thuMuc);
        String tenFile = UUID.randomUUID() + "." + duoi;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, thuMuc.resolve(tenFile), StandardCopyOption.REPLACE_EXISTING);
        }
        return "/uploads/" + tenFile;
    }

    /** Map content-type (vd "image/png") -> đuôi file lưu xuống đĩa (vd "png"). Mỗi lớp con tự định nghĩa. */
    protected abstract Map<String, String> duoiHopLe();

    /** Thông báo lỗi hiển thị cho người dùng khi tải lên loại file không nằm trong duoiHopLe(). */
    protected abstract String thongBaoLoiLoai();
}
