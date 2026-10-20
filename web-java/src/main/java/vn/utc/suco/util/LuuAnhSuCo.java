package vn.utc.suco.util;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Dùng khi báo sự cố: chỉ nhận ảnh (cột SU_CO.ANH_DINH_KEM). */
@Component
public class LuuAnhSuCo extends BoLuuTru {
    private static final Map<String, String> DUOI_ANH = Map.of(
            "image/jpeg", "jpg", "image/png", "png", "image/gif", "gif", "image/webp", "webp");

    public LuuAnhSuCo(@Value("${app.upload-dir:uploads}") String uploadDir) {
        super(uploadDir);
    }

    @Override
    protected Map<String, String> duoiHopLe() {
        return DUOI_ANH;
    }

    @Override
    protected String thongBaoLoiLoai() {
        return "Chỉ nhận ảnh JPG, PNG, GIF hoặc WEBP";
    }
}
