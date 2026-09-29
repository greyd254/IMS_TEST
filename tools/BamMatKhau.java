import vn.utc.suco.util.MatKhau;

/** In chuỗi băm của các mật khẩu truyền vào (để dán vào script SQL). Chạy: xem TIEN_DO.md mục "Băm mật khẩu". */
public class BamMatKhau {
    public static void main(String[] a) {
        for (String p : a) {
            System.out.println(p + "\t" + MatKhau.bam(p));
        }
    }
}
