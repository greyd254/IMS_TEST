package vn.utc.suco.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Băm mật khẩu bằng PBKDF2-HMAC-SHA256 kèm salt ngẫu nhiên (chỉ dùng thư viện có sẵn của JDK).
 * Chuỗi lưu trong cột MAT_KHAU: PBKDF2$<số vòng lặp>$<salt base64>$<hash base64>
 * Băm là một chiều: CSDL không bao giờ chứa mật khẩu gốc, kể cả DBA cũng không đọc lại được.
 */
public final class MatKhau {
    private static final String TIEN_TO = "PBKDF2";
    private static final int VONG_LAP = 120_000;
    private static final int SALT_BYTE = 16;
    private static final int HASH_BIT = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private MatKhau() {
    }

    public static String bam(String matKhau) {
        byte[] salt = new byte[SALT_BYTE];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(matKhau, salt, VONG_LAP);
        Base64.Encoder b64 = Base64.getEncoder();
        return TIEN_TO + "$" + VONG_LAP + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(hash);
    }

    /** true nếu mật khẩu nhập vào khớp chuỗi đã băm; chuỗi lưu sai định dạng (vd: còn plain text cũ) thì trả false. */
    public static boolean khop(String matKhau, String daBam) {
        if (matKhau == null || daBam == null) {
            return false;
        }
        String[] p = daBam.split("[$]");
        if (p.length != 4 || !TIEN_TO.equals(p[0])) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(p[2]);
            byte[] mong = Base64.getDecoder().decode(p[3]);
            byte[] thuc = pbkdf2(matKhau, salt, Integer.parseInt(p[1]));
            return MessageDigest.isEqual(mong, thuc); // so sánh thời gian không đổi
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(String matKhau, byte[] salt, int vongLap) {
        try {
            PBEKeySpec spec = new PBEKeySpec(matKhau.toCharArray(), salt, vongLap, HASH_BIT);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
