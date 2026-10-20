package vn.utc.suco.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Dùng khi gửi tin nhắn trao đổi: nhận ảnh, PDF, Word, TXT, ZIP. */
@Component
public class LuuTepTraoDoi extends BoLuuTru {
    private static final Map<String, String> DUOI_TEP;

    static {
        Map<String, String> m = new HashMap<>();
        m.put("image/jpeg", "jpg");
        m.put("image/png", "png");
        m.put("image/gif", "gif");
        m.put("image/webp", "webp");
        m.put("application/pdf", "pdf");
        m.put("application/msword", "doc");
        m.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        m.put("text/plain", "txt");
        m.put("application/zip", "zip");
        DUOI_TEP = Map.copyOf(m);
    }

    public LuuTepTraoDoi(@Value("${app.upload-dir:uploads}") String uploadDir) {
        super(uploadDir);
    }

    @Override
    protected Map<String, String> duoiHopLe() {
        return DUOI_TEP;
    }

    @Override
    protected String thongBaoLoiLoai() {
        return "Chỉ nhận ảnh, PDF, Word (.doc/.docx), TXT hoặc ZIP";
    }
}
