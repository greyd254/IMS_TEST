package vn.utc.suco.util;

/** Ngoại lệ riêng của ứng dụng: ném ra khi người dùng tải lên file có loại (content-type) không được chấp nhận. */
public class FileKhongHopLeException extends RuntimeException {

    public FileKhongHopLeException(String thongBao) {
        super(thongBao);
    }
}
