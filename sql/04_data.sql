-- =====================================================================
-- 04_data.sql
-- CHẠY BẰNG: SUCO_APP, kết nối vào FREEPDB1 (sau khi đã chạy 02 và 03)
-- MỤC ĐÍCH : dữ liệu mẫu tiếng Việt cho 9 bảng.
-- LƯU Ý    : KHÔNG ghi cột MA_xxx (IDENTITY tự sinh 1,2,3... theo thứ tự INSERT),
--            nên các FK bên dưới tham chiếu theo số thứ tự dòng đã chèn.
--            => chỉ chạy file này trên bảng RỖNG (muốn chạy lại thì chạy 99_drop_all
--               rồi 02, 03, 04). Mật khẩu plain text CHỈ dùng cho demo bài tập.
-- =====================================================================
SET DEFINE OFF

-- 1. NGUOI_DUNG (10 dòng: MA_ND 1-2 là ADMIN, 3-10 là USER)
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Nguyễn Văn Hùng',  'hung.nv@utc.edu.vn',  'PBKDF2$120000$fY+9x/A0wBfvWPJFgeFj0A==$Lu8triqDKGHWc4NpV2NzbTAI+Fk4NhWtCkOMSxHcSuA=',  'Trung tâm Công nghệ thông tin', 'ADMIN');
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Trần Thị Thu Hà',  'ha.ttt@utc.edu.vn',   'PBKDF2$120000$XAo60bnEE4Af5EoEODrTJw==$32Vbr+UdMEfIhigiYwKBPh54RupXpPNE9pKjJNm0FqA=',  'Trung tâm Công nghệ thông tin', 'ADMIN');
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Phạm Đức Anh',     'anh.pd@st.utc.edu.vn', 'PBKDF2$120000$jxpm88XplJYh56VwD5cKgg==$gBPOUrnSKz2XK5laH7LmePliHf+PCuv73AJhMrD2fPs=',   'Sinh viên Khoa Công nghệ thông tin', 'USER');
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Lê Thị Lan',       'lan.lt@utc.edu.vn',   'PBKDF2$120000$dVYpFu1zCMxa9TGtjKO/Eg==$AJTFS1xyctv1kuWa/Eo2xFojIw4hYDdnmjRqsP1CV04=',    'Khoa Kinh tế vận tải', 'USER');
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Vũ Minh Tuấn',     'tuan.vm@utc.edu.vn',  'PBKDF2$120000$g6uoANlDXjCg8RtB9h26jg==$Dsl3EfttEvLW6+rWwVe3NH97BZ1tOIj6bmTGDhsUdbk=',    'Khoa Cơ khí', 'USER');
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Đỗ Thanh Hằng',    'hang.dt@utc.edu.vn',  'PBKDF2$120000$SbufqQqJ92BkWOs4g7gI8g==$bEx7rNUaT8Te0gHoEMLRjjZkxy69TOnNM7FZQa1guFA=',    'Phòng Đào tạo', 'USER');
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Hoàng Văn Nam',    'nam.hv@utc.edu.vn',   'PBKDF2$120000$8arO7BtiOims6mUYmFTQLQ==$n1w+BQb0SmpDe/vwdmQNseW6grxlpDWFsYQtrAarkXM=',    'Khoa Công trình', 'USER');
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Ngô Quỳnh Trang',  'trang.nq@st.utc.edu.vn', 'PBKDF2$120000$IiSNI+xzXBjDoz6uor0RcA==$vFjeMrwDAEZWGOlzWZR+V+1nTH4+9yQkW3Tp/DnsvKw=', 'Sinh viên Khoa Kinh tế vận tải', 'USER');
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Bùi Xuân Trường',  'truong.bx@st.utc.edu.vn', 'PBKDF2$120000$gnEJMmDqsP+wqSSbpL7GPA==$V8kpsTImFW6McpqTuZaJ817yXWeaAtpgy1EUg+5Uhdg=', 'Sinh viên Khoa Công trình', 'USER');
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES ('Đặng Thị Ngọc',    'ngoc.dt@utc.edu.vn',  'PBKDF2$120000$19VxlYmKE33DhgSOPr3Qfw==$TWCZ9UkgSjn5Kw/thhnfF+yYhazVTB6CPtWAuQtk7EU=',    'Phòng Kế hoạch - Tài chính', 'USER');

-- 2. PHONG (10 dòng)
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng máy 1 (A2-101)', 'A2', 1, 'PHONG_MAY');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng máy 2 (A2-102)', 'A2', 1, 'PHONG_MAY');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng máy 5 (A2-205)', 'A2', 2, 'PHONG_MAY');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng học A6-301',    'A6', 3, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng học A6-402',    'A6', 4, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Giảng đường H1-201',  'H1', 2, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Giảng đường H1-305',  'H1', 3, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng Đào tạo (A1-105)', 'A1', 1, 'VAN_PHONG');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng Kế hoạch - Tài chính (A1-208)', 'A1', 2, 'VAN_PHONG');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Văn phòng Khoa CNTT (A3-101)', 'A3', 1, 'VAN_PHONG');

-- 3. THIET_BI (12 dòng, MA_PHONG tham chiếu PHONG 1-10)
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy tính PC-A2101-05',        'MAY_TINH', 1, 'HONG');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy tính PC-A2101-12',        'MAY_TINH', 1, 'HOAT_DONG');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy tính PC-A2102-03',        'MAY_TINH', 2, 'DANG_SUA');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Switch Cisco tầng 2 nhà A2',  'THIET_BI_MANG', 3, 'HOAT_DONG');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy chiếu Epson A6-301',      'MAY_CHIEU', 4, 'HONG');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy chiếu Panasonic A6-402',  'MAY_CHIEU', 5, 'HOAT_DONG');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy chiếu Sony H1-201',       'MAY_CHIEU', 6, 'DANG_SUA');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy chiếu BenQ H1-305',       'MAY_CHIEU', 7, 'HOAT_DONG');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy in HP LaserJet Phòng Đào tạo', 'MAY_IN', 8, 'HONG');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy tính Dell phòng Kế hoạch - Tài chính', 'MAY_TINH', 9, 'HOAT_DONG');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Access Point WiFi tầng 3 nhà H1', 'THIET_BI_MANG', 7, 'HOAT_DONG');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG) VALUES ('Máy tính Văn phòng Khoa CNTT', 'MAY_TINH', 10, 'HOAT_DONG');

-- 4. DANH_MUC_SU_CO (8 dòng; MA_DM 7 và 8 cố ý chưa có bài kinh nghiệm để demo truy vấn)
INSERT INTO DANH_MUC_SU_CO (TEN_DM, MO_TA) VALUES ('Máy tính', 'Lỗi phần cứng, treo máy, chạy chậm, không khởi động');
INSERT INTO DANH_MUC_SU_CO (TEN_DM, MO_TA) VALUES ('Máy chiếu', 'Máy chiếu không lên hình, mờ, không nhận tín hiệu');
INSERT INTO DANH_MUC_SU_CO (TEN_DM, MO_TA) VALUES ('Mạng Internet / WiFi', 'Mất mạng, WiFi chập chờn, không nhận IP');
INSERT INTO DANH_MUC_SU_CO (TEN_DM, MO_TA) VALUES ('Phần mềm', 'Cài đặt, bản quyền, lỗi phần mềm giảng dạy');
INSERT INTO DANH_MUC_SU_CO (TEN_DM, MO_TA) VALUES ('Tài khoản và Email', 'Quên mật khẩu, tài khoản bị khóa');
INSERT INTO DANH_MUC_SU_CO (TEN_DM, MO_TA) VALUES ('Máy in', 'Kẹt giấy, in mờ, không kết nối được máy in');
INSERT INTO DANH_MUC_SU_CO (TEN_DM, MO_TA) VALUES ('Thiết bị ngoại vi', 'Chuột, bàn phím, loa, micro, bút trình chiếu');
INSERT INTO DANH_MUC_SU_CO (TEN_DM, MO_TA) VALUES ('Hệ thống quản lý đào tạo', 'Lỗi cổng thông tin sinh viên, đăng ký học phần');

-- 5. BAI_KINH_NGHIEM (9 dòng; người viết là admin 1 hoặc 2)
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Máy tính không khởi động được', 1, 'Bấm nút nguồn không có đèn, không có tiếng quạt hoặc máy kêu bíp liên tục.', 'Hỏng nguồn, lỏng RAM hoặc hết pin CMOS.', 'Kiểm tra dây nguồn, thay thử nguồn khác. Tháo RAM, lau chân tiếp xúc rồi cắm lại. Thay pin CMOS CR2032 nếu BIOS mất cấu hình.', 1);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Máy tính chạy chậm, hay bị treo', 1, 'Mở ứng dụng lâu, chuột giật, đôi khi đứng hình.', 'Ổ cứng HDD lỗi sector, thiếu RAM hoặc nhiều chương trình khởi động cùng Windows.', 'Kiểm tra sức khỏe ổ đĩa, tắt bớt chương trình khởi động, nâng cấp RAM hoặc thay ổ SSD.', 2);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Máy chiếu không nhận tín hiệu HDMI', 2, 'Máy chiếu báo No Signal dù laptop đã cắm cáp.', 'Cáp HDMI lỏng hoặc hỏng, chọn sai nguồn vào, laptop chưa xuất hình ra màn hình ngoài.', 'Cắm lại cáp hoặc thay cáp mới, chọn đúng nguồn HDMI trên máy chiếu, nhấn Windows + P chọn Duplicate.', 1);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Máy chiếu hình mờ hoặc ám màu', 2, 'Hình ảnh mờ, ám vàng hoặc có vệt tối.', 'Bụi bám lọc gió, bóng đèn sắp hết tuổi thọ hoặc lấy nét sai.', 'Vệ sinh lọc gió, chỉnh lại nét và keystone, thay bóng đèn khi số giờ sử dụng vượt định mức.', 2);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('WiFi kết nối được nhưng không vào được Internet', 3, 'Biểu tượng WiFi có dấu chấm than, mở web báo lỗi.', 'Access Point quá tải hoặc mất kết nối lên switch, cấp phát IP lỗi.', 'Khởi động lại Access Point, kiểm tra cáp mạng và cổng PoE trên switch, thử quên mạng rồi kết nối lại.', 1);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Mất mạng LAN cả phòng máy', 3, 'Toàn bộ máy trong phòng không có mạng cùng lúc.', 'Switch tầng bị treo hoặc lỏng cáp uplink.', 'Kiểm tra đèn cổng uplink, khởi động lại switch, thay cáp nối lên switch tổng nếu cần.', 2);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Quên mật khẩu hoặc không đăng nhập được email trường', 5, 'Đăng nhập báo sai mật khẩu hoặc tài khoản bị khóa.', 'Nhập sai nhiều lần làm tài khoản bị khóa tạm thời hoặc mật khẩu hết hạn.', 'Admin đặt lại mật khẩu trên hệ thống email, yêu cầu người dùng đổi mật khẩu ở lần đăng nhập đầu tiên.', 1);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Máy in báo kẹt giấy hoặc in bị mờ', 6, 'Đèn báo lỗi nhấp nháy, giấy ra bị nhăn hoặc chữ nhạt.', 'Giấy ẩm, con lăn mòn hoặc hộp mực sắp hết.', 'Mở nắp lấy giấy kẹt theo chiều đi của giấy, lau con lăn, lắc đều hoặc thay hộp mực.', 2);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Phần mềm báo lỗi bản quyền khi cài đặt', 4, 'Cài xong mở phần mềm báo License expired hoặc Cannot connect to license server.', 'Máy không kết nối được máy chủ license của trường hoặc sai phiên bản.', 'Kiểm tra kết nối tới máy chủ license, cài đúng phiên bản do trường cấp, khai báo lại địa chỉ máy chủ license.', 1);

-- 6. SU_CO (15 dòng: 3 trạng thái, đủ danh mục, ngày tạo từ tháng 4 đến tháng 9/2026)
--    Cột: TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI,
--         NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH
--    Ghi rõ danh sách cột: bảng còn cột ANH_DINH_KEM (để NULL), nếu viết "INSERT INTO SU_CO VALUES (...)"
--    thì thiếu 1 giá trị -> ORA-00947 và cả 15 dòng không vào được (kéo theo lịch sử, phản hồi, trao đổi).
-- 6.1 Sự cố đã HOAN_THANH (chèn trực tiếp, nên phải tự điền NGAY_HOAN_THANH vì trigger chỉ chạy khi UPDATE)
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('WiFi tầng 3 nhà H1 chập chờn', 'Sinh viên học ở H1-305 không vào được mạng, WiFi lúc có lúc không.', 8, 1, 11, 3, 'CAO', 'HOAN_THANH', 'Access Point bị treo do quá tải.', 'Khởi động lại Access Point và nâng cấp firmware.', 5, TIMESTAMP '2026-04-12 13:30:00', TIMESTAMP '2026-04-13 10:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy chiếu H1-305 không nhận HDMI', 'Giảng viên cắm laptop nhưng máy chiếu báo No Signal.', 7, 2, 8, 2, 'CAO', 'HOAN_THANH', 'Cáp HDMI âm tường bị lỏng đầu nối.', 'Cắm lại đầu nối và thay cáp HDMI mới.', 3, TIMESTAMP '2026-05-08 08:30:00', TIMESTAMP '2026-05-08 11:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy tính PC-A2101-12 chạy rất chậm', 'Mở phần mềm lập trình mất hơn 5 phút.', 3, 1, 2, 1, 'THAP', 'HOAN_THANH', 'Ổ cứng HDD bị bad sector.', 'Thay ổ SSD 256GB và cài lại hệ điều hành.', 2, TIMESTAMP '2026-05-20 09:00:00', TIMESTAMP '2026-05-22 15:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Không đăng nhập được email trường', 'Báo sai mật khẩu dù nhập đúng.', 4, 2, NULL, 5, 'TRUNG_BINH', 'HOAN_THANH', 'Tài khoản bị khóa do nhập sai nhiều lần.', 'Mở khóa và đặt lại mật khẩu.', 7, TIMESTAMP '2026-06-03 08:10:00', TIMESTAMP '2026-06-03 09:45:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Switch tầng 2 nhà A2 mất kết nối phòng máy 5', 'Cả phòng 30 máy không có mạng, đang chuẩn bị thi thực hành.', 5, 1, 4, 3, 'CAO', 'HOAN_THANH', 'Cáp uplink bị lỏng sau khi vệ sinh phòng.', 'Cắm chặt cáp uplink và khởi động lại switch.', 6, TIMESTAMP '2026-06-18 07:45:00', TIMESTAMP '2026-06-19 08:30:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy in Phòng Đào tạo in bị mờ', 'Bản in ra chữ nhạt, khó đọc.', 6, 2, 9, 6, 'THAP', 'HOAN_THANH', 'Hộp mực gần hết.', 'Thay hộp mực mới.', 8, TIMESTAMP '2026-07-07 10:00:00', TIMESTAMP '2026-07-10 14:20:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Không cài được AutoCAD do lỗi license', 'Cài xong mở phần mềm báo Cannot connect to license server.', 9, 1, 12, 4, 'TRUNG_BINH', 'HOAN_THANH', 'Máy chưa khai báo đúng địa chỉ máy chủ license.', 'Khai báo lại địa chỉ máy chủ license của trường.', 9, TIMESTAMP '2026-08-04 09:30:00', TIMESTAMP '2026-08-06 16:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Tài khoản cổng thông tin sinh viên bị khóa', 'Không đăng nhập được để đăng ký học phần.', 4, 1, NULL, 5, 'TRUNG_BINH', 'HOAN_THANH', 'Tài khoản bị khóa do quá số lần đăng nhập sai.', 'Mở khóa tài khoản và hướng dẫn đổi mật khẩu.', 7, TIMESTAMP '2026-08-25 08:00:00', TIMESTAMP '2026-08-25 10:30:00');
-- 6.2 Sự cố DANG_XU_LY
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy tính PC-A2101-05 không khởi động', 'Bấm nút nguồn máy không lên, không có đèn.', 3, 1, 1, 1, 'CAO', 'DANG_XU_LY', 'Nghi hỏng bộ nguồn.', NULL, 1, TIMESTAMP '2026-09-10 08:15:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy tính PC-A2102-03 báo lỗi RAM', 'Máy kêu bíp liên tục khi bật.', 3, 2, 3, 1, 'TRUNG_BINH', 'DANG_XU_LY', 'Thanh RAM lỏng hoặc hỏng.', NULL, NULL, TIMESTAMP '2026-09-05 14:00:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy chiếu H1-201 hình mờ, ám vàng', 'Hình ảnh chiếu lên màn bị ám vàng, khó nhìn.', 7, 2, 7, 2, 'TRUNG_BINH', 'DANG_XU_LY', 'Nghi bóng đèn sắp hết tuổi thọ.', NULL, 4, TIMESTAMP '2026-09-15 07:50:00', NULL);
-- 6.3 Sự cố MOI (chưa có admin xử lý)
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy chiếu A6-301 không lên hình', 'Bật máy chiếu nhưng đèn báo đỏ, không có hình.', 5, NULL, 5, 2, 'CAO', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-09-22 07:30:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy in Phòng Đào tạo báo kẹt giấy', 'Máy in báo kẹt giấy liên tục dù đã lấy giấy ra.', 6, NULL, 9, 6, 'TRUNG_BINH', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-09-20 15:10:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy tính phòng Kế hoạch - Tài chính không vào được mạng nội bộ', 'Không truy cập được phần mềm kế toán dùng chung.', 10, NULL, 10, 3, 'CAO', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-09-24 09:05:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy chiếu A6-402 bị rung hình', 'Hình chiếu rung nhẹ khi giảng viên chiếu slide.', 8, NULL, 6, 2, 'THAP', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-09-23 13:40:00', NULL);

-- 7. LICH_SU_XU_LY (13 dòng, khớp với các sự cố ở trên)
--    Cột: MA_SC, MA_NGUOI_CAP_NHAT, TRANG_THAI_CU, TRANG_THAI_MOI, GHI_CHU, THOI_GIAN
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 1,  1, 'MOI', 'DANG_XU_LY', 'Tiếp nhận, đang kiểm tra Access Point', TIMESTAMP '2026-04-12 14:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 1,  1, 'DANG_XU_LY', 'HOAN_THANH', 'Đã khởi động lại và nâng cấp firmware', TIMESTAMP '2026-04-13 10:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 2,  2, 'MOI', 'DANG_XU_LY', 'Đến phòng kiểm tra cáp HDMI', TIMESTAMP '2026-05-08 09:30:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 2,  2, 'DANG_XU_LY', 'HOAN_THANH', 'Đã thay cáp HDMI', TIMESTAMP '2026-05-08 11:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 3,  1, 'MOI', 'DANG_XU_LY', 'Kiểm tra ổ cứng, phát hiện bad sector', TIMESTAMP '2026-05-21 10:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 3,  1, 'DANG_XU_LY', 'HOAN_THANH', 'Đã thay SSD và cài lại máy', TIMESTAMP '2026-05-22 15:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 5,  1, 'MOI', 'DANG_XU_LY', 'Khẩn: kiểm tra switch trước giờ thi', TIMESTAMP '2026-06-18 08:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 5,  1, 'DANG_XU_LY', 'HOAN_THANH', 'Đã cắm lại cáp uplink, mạng hoạt động', TIMESTAMP '2026-06-19 08:30:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 6,  2, 'MOI', 'DANG_XU_LY', 'Kiểm tra hộp mực', TIMESTAMP '2026-07-08 09:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 6,  2, 'DANG_XU_LY', 'HOAN_THANH', 'Đã thay hộp mực mới', TIMESTAMP '2026-07-10 14:20:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 9,  1, 'MOI', 'DANG_XU_LY', 'Đang kiểm tra bộ nguồn', TIMESTAMP '2026-09-11 09:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 10, 2, 'MOI', 'DANG_XU_LY', 'Đang thử thay thanh RAM', TIMESTAMP '2026-09-06 08:30:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 11, 2, 'MOI', 'DANG_XU_LY', 'Chờ nhập bóng đèn thay thế', TIMESTAMP '2026-09-16 09:15:00');

-- 8. Dữ liệu mẫu cho các cột/bảng bổ sung (SĐT, người tạo, nhà cung cấp, ngày lắp đặt, HDSD, tầng NULL, phản hồi)
UPDATE NGUOI_DUNG SET SDT = '0912345678' WHERE MA_ND = 1;
UPDATE NGUOI_DUNG SET SDT = '0923456789' WHERE MA_ND = 2;
UPDATE NGUOI_DUNG SET SDT = '0934567890' WHERE MA_ND = 3;
-- Các tài khoản USER demo do admin 1 tạo sẵn (minh họa MA_NGUOI_TAO)
UPDATE NGUOI_DUNG SET MA_NGUOI_TAO = 1 WHERE MA_ND IN (3, 4, 5, 6, 7, 8, 9, 10);

UPDATE THIET_BI SET NHA_CUNG_CAP = 'FPT Shop', NGAY_LAP_DAT = DATE '2023-09-01' WHERE MA_TB = 2;
UPDATE THIET_BI SET NHA_CUNG_CAP = 'Epson Việt Nam', NGAY_LAP_DAT = DATE '2021-08-20' WHERE MA_TB = 5;
UPDATE THIET_BI SET NHA_CUNG_CAP = 'Dell Việt Nam', NGAY_LAP_DAT = DATE '2022-03-15',
       HDSD = 'Khởi động lại bằng nút nguồn giữ 5 giây nếu treo máy. Không rút dây mạng khi đang cập nhật phần mềm.'
 WHERE MA_TB = 10;

-- Văn phòng nhỏ, chỉ 1 tầng nên không cần ghi số tầng (minh họa TANG cho phép NULL)
UPDATE PHONG SET TANG = NULL WHERE LOAI_PHONG = 'VAN_PHONG';

-- Phản hồi của người báo sau khi sự cố đã HOAN_THANH (MA_SC 1-4)
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (1, 8, 5, 'Xử lý nhanh, mạng ổn định trở lại ngay trong buổi học.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (2, 7, 4, 'Khắc phục kịp giờ giảng, cảm ơn bộ phận IT.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (3, 3, 5, 'Máy chạy mượt hẳn sau khi thay ổ SSD.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (4, 4, 3, 'Đã xử lý được nhưng mất hơi lâu mới mở lại được tài khoản.');

-- 9. BINH_LUAN_SU_CO (6 dòng): trao đổi qua lại giữa người báo và admin trên sự cố chưa xong
--    Cột: MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN (TEP_DINH_KEM để NULL vì chưa có file thật trong uploads/)
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (9,  3, 'Máy này em cần dùng cho buổi thực hành thứ 6, khi nào sửa xong ạ?', TIMESTAMP '2026-09-11 10:00:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (9,  1, 'Đã đặt bộ nguồn thay thế, dự kiến thứ 5 có hàng. Tạm thời em dùng máy PC-A2101-12 nhé.', TIMESTAMP '2026-09-11 14:30:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (11, 7, 'Tuần sau lớp tôi có buổi bảo vệ đồ án ở H1-201, mong IT xử lý sớm.', TIMESTAMP '2026-09-16 08:00:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (11, 2, 'Bóng đèn đang về, nếu chưa kịp IT sẽ mượn máy chiếu di động cho thầy.', TIMESTAMP '2026-09-16 09:20:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (11, 7, 'Cảm ơn, vậy tôi chờ thông báo.', TIMESTAMP '2026-09-16 09:45:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (12, 5, 'Đèn báo đỏ nhấp nháy 3 lần rồi tắt, tôi đã thử rút điện cắm lại nhưng không được.', TIMESTAMP '2026-09-22 08:00:00');

-- =====================================================================
-- 10. DỮ LIỆU BỔ SUNG CHO DEMO PHÂN TRANG (mỗi trang 10 dòng, xem Trang.KICH_THUOC)
--     Sau phần này: 25 người dùng, 25 phòng, 30 thiết bị, 12 bài KN, 35 sự cố
--     => các danh sách Người dùng / Phòng / Thiết bị / Sự cố đều có 3-4 trang.
--     Mã tiếp tục từ các phần trên: MA_ND 11-25, MA_PHONG 11-25, MA_TB 13-30,
--     MA_BAI 10-12, MA_SC 16-35. Mật khẩu: admin mới 'admin123', user mới '123456'.
-- =====================================================================

-- 10.1 NGUOI_DUNG 11-25 (11 là ADMIN, 12-25 là USER; 25 đã bị vô hiệu hóa - xóa mềm)
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Lý Quốc Bảo',      'bao.lq@utc.edu.vn',      'PBKDF2$120000$dIQNh7ayQ4JW3YFwVzb9qg==$mn8yZYPy0tqvb9BX7vqP6hJgO/xWL3NLM9xrsHFrJmA=', 'Trung tâm Công nghệ thông tin', 'ADMIN', '0945678901', 1);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Trịnh Văn Khánh',  'khanh.tv@st.utc.edu.vn', 'PBKDF2$120000$dgqCH4AhEsD8IEwe0dsk3Q==$Go3lG2rbJSPVmRlRiLrcuzIxPKCHX6Js7fdDc0DjH08=', 'Sinh viên Khoa Công nghệ thông tin', 'USER', '0356789012', 1);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Phan Thị Mai',     'mai.pt@utc.edu.vn',      'PBKDF2$120000$TjjRkKnOu4KYE0Tq3CdsEQ==$uNThc4Z++OjE/4dyJiVANxNlhqBlfwEl8A8U+KZDkS4=', 'Khoa Công nghệ thông tin', 'USER', '0367890123', 1);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Nguyễn Hữu Phúc',  'phuc.nh@utc.edu.vn',     'PBKDF2$120000$a2n2UobdTQI576M+lOYb6g==$PZA2CaBzW0mgjRAE2LmWNWJY/IQrEJ/lAgnlW9VyQqI=', 'Khoa Điện - Điện tử', 'USER', NULL, 2);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Trương Minh Đức',  'duc.tm@utc.edu.vn',      'PBKDF2$120000$iv8F8+cUQ1OIuxcXNIZPvA==$RKUNilE2O38OAJN2EHhZW73apkTG56Qn/8sbD9Z9rFw=', 'Khoa Công nghệ thông tin', 'USER', '0378901234', 1);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Lại Thị Hồng',     'hong.lt@utc.edu.vn',     'PBKDF2$120000$FP897b90hGUPx4O3kF+A4w==$rqoooxu8j+z60Efq/0hmQGlfDPOLC9Igw0smNKEQW+c=', 'Phòng Công tác sinh viên', 'USER', '0389012345', 2);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Mai Văn Toàn',     'toan.mv@utc.edu.vn',     'PBKDF2$120000$DG0DlktcKXZ6fxg2NPZbSw==$pefis9A4JLXG8bJG04dWkC4lK8qe+GQ0ORwNDAJ33Lk=', 'Phòng Công tác sinh viên', 'USER', NULL, 11);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Đinh Thị Thảo',    'thao.dt@st.utc.edu.vn',  'PBKDF2$120000$4KZfaY+2H3L52yMqzJYtag==$UEHU0K4DY2YUMdoluM1gp/BEh97HekfYOKObazdXt1c=', 'Sinh viên Khoa Điện - Điện tử', 'USER', '0391234567', NULL);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Tạ Quang Huy',     'huy.tq@utc.edu.vn',      'PBKDF2$120000$n7Db/lruMbainY3AB5O/zg==$PtPY5dErmjpRpzujH2ywx5L9o7ke4CrgNGHdYMWjfxk=', 'Thư viện', 'USER', '0702345678', 11);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Cao Thị Yến',      'yen.ct@utc.edu.vn',      'PBKDF2$120000$mvmGz9OieQrhE1igChdGXg==$6BWFUtTok76cC5YerH7FCu0beaxSYddRGFaQy2cCz7E=', 'Khoa Vận tải - Kinh tế', 'USER', NULL, 2);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Kiều Văn Long',    'long.kv@st.utc.edu.vn',  'PBKDF2$120000$z3dhO5UHjSe+Stelhzjy/g==$VG3wy/UuEWFabr9AqeN3lHjtWyY19Hoo2PBvJ4WLhFs=', 'Sinh viên Khoa Cơ khí', 'USER', '0813456789', NULL);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Lương Thị Nhung',  'nhung.lt@st.utc.edu.vn', 'PBKDF2$120000$Yput8UdTt+LNZFBW7F0bZA==$YT41yGcl9WGQ78RteKJAh6cttsnDU508Bkb9606z9ME=', 'Sinh viên Khoa Công nghệ thông tin', 'USER', NULL, NULL);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Hà Văn Thắng',     'thang.hv@utc.edu.vn',    'PBKDF2$120000$uornSfZCbkxQIfKMVB1yPg==$6cOF/3Ac81AakPoM7J/9r0u6ytXHcqBHJql9GfgUrcw=', 'Khoa Công trình', 'USER', '0824567890', 1);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Dương Thị Quyên',  'quyen.dt@st.utc.edu.vn', 'PBKDF2$120000$Z70hWpgpmBLwlGAyCMpIcw==$q3BUiulSXkvjtS9H9Rv96gbH3TuS4ajEsyZQ0j4ud10=', 'Sinh viên Khoa Kinh tế vận tải', 'USER', NULL, NULL);
INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO, SDT, MA_NGUOI_TAO) VALUES ('Văn Công Sơn',     'son.vc@utc.edu.vn',      'PBKDF2$120000$3/8pGQeY690a6YBxSIrUcQ==$vQR2ydnISkeYvDs/Ol9eYedKGK67uGSU3VXYV//ekHQ=', 'Khoa Cơ khí', 'USER', '0835678901', 2);
UPDATE NGUOI_DUNG SET TRANG_THAI = 'KHONG_HOAT_DONG', NGAY_XOA = DATE '2026-10-15', MA_NGUOI_XOA = 1 WHERE EMAIL = 'son.vc@utc.edu.vn';

-- 10.2 PHONG 11-25 (văn phòng 1 tầng để TANG = NULL như phần trên)
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng máy 3 (A2-201)', 'A2', 2, 'PHONG_MAY');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng máy 4 (A2-202)', 'A2', 2, 'PHONG_MAY');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng máy 6 (A2-301)', 'A2', 3, 'PHONG_MAY');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng máy 7 (A2-302)', 'A2', 3, 'PHONG_MAY');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng học A6-201',    'A6', 2, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng học A6-305',    'A6', 3, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng học A6-501',    'A6', 5, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Giảng đường H1-101',  'H1', 1, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Giảng đường H1-401',  'H1', 4, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Giảng đường H3-202',  'H3', 2, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng thí nghiệm mạng (A3-204)', 'A3', 2, 'PHONG_MAY');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng học A8-303',    'A8', 3, 'PHONG_HOC');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng Công tác sinh viên (A1-110)', 'A1', NULL, 'VAN_PHONG');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Phòng đọc điện tử Thư viện (T1-201)', 'T1', 2, 'PHONG_MAY');
INSERT INTO PHONG (TEN_PHONG, TOA_NHA, TANG, LOAI_PHONG) VALUES ('Văn phòng Khoa Công trình (A4-102)', 'A4', NULL, 'VAN_PHONG');

-- 10.3 THIET_BI 13-30 (MA_PHONG 11-25 vừa thêm)
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy tính PC-A2201-01',      'MAY_TINH', 11, 'HOAT_DONG', 'FPT Shop', DATE '2023-09-01');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy tính PC-A2201-17',      'MAY_TINH', 11, 'HONG', 'FPT Shop', DATE '2023-09-01');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy tính PC-A2202-08',      'MAY_TINH', 12, 'HOAT_DONG', 'Phong Vũ', DATE '2024-02-20');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy tính PC-A2301-22',      'MAY_TINH', 13, 'DANG_SUA', 'Phong Vũ', DATE '2022-08-10');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy chiếu Epson A2-302',    'MAY_CHIEU', 14, 'HOAT_DONG', 'Epson Việt Nam', DATE '2024-08-15');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy chiếu ViewSonic A6-201', 'MAY_CHIEU', 15, 'HOAT_DONG', 'Hoàng Hà Mobile', DATE '2022-01-12');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy chiếu Epson A6-305',    'MAY_CHIEU', 16, 'HONG', 'Epson Việt Nam', DATE '2020-09-05');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy chiếu Sony A6-501',     'MAY_CHIEU', 17, 'HOAT_DONG', 'Sony Việt Nam', DATE '2023-03-18');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Bộ loa và micro H1-101',    'KHAC', 18, 'DANG_SUA', 'Điện máy Xanh', DATE '2021-11-30');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy chiếu Panasonic H1-401', 'MAY_CHIEU', 19, 'HOAT_DONG', 'Panasonic Việt Nam', DATE '2022-06-25');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Màn hình tương tác H3-202', 'KHAC', 20, 'HOAT_DONG', 'Samsung Việt Nam', DATE '2025-01-08');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Router Cisco phòng thí nghiệm mạng', 'THIET_BI_MANG', 21, 'HOAT_DONG', 'Cisco Việt Nam', DATE '2021-05-14');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Switch tầng 2 nhà A3',      'THIET_BI_MANG', 21, 'HONG', 'Cisco Việt Nam', DATE '2019-10-02');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy chiếu BenQ A8-303',     'MAY_CHIEU', 22, 'HOAT_DONG', 'BenQ Việt Nam', DATE '2024-09-01');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy in Canon phòng Công tác sinh viên', 'MAY_IN', 23, 'HOAT_DONG', 'Canon Việt Nam', DATE '2023-04-11');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy tính tra cứu thư viện TV-05', 'MAY_TINH', 24, 'HOAT_DONG', 'Dell Việt Nam', DATE '2022-12-05');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Access Point WiFi thư viện tầng 2', 'THIET_BI_MANG', 24, 'DANG_SUA', 'Ubiquiti', DATE '2023-07-20');
INSERT INTO THIET_BI (TEN_TB, LOAI_TB, MA_PHONG, TINH_TRANG, NHA_CUNG_CAP, NGAY_LAP_DAT) VALUES ('Máy in Brother Khoa Công trình', 'MAY_IN', 25, 'HONG', 'Brother Việt Nam', DATE '2020-03-16');

-- 10.4 BAI_KINH_NGHIEM 10-12 (vẫn để danh mục 7 và 8 không có bài, giữ nguyên ý demo truy vấn)
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Màn hình xanh (BSOD) sau khi cập nhật Windows', 1, 'Máy khởi động lên một lúc thì hiện màn hình xanh và tự khởi động lại.', 'Driver card màn hình hoặc card mạng không tương thích với bản cập nhật mới.', 'Vào Safe Mode, gỡ bản cập nhật hoặc driver vừa cài, tải driver đúng phiên bản từ trang hãng rồi cài lại.', 11);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Microsoft Office báo chưa kích hoạt bản quyền', 4, 'Thanh tiêu đề Word/Excel hiện chữ Unlicensed Product, không lưu được tài liệu.', 'Máy chưa đăng nhập tài khoản Microsoft 365 của trường hoặc hết hạn kích hoạt.', 'Đăng nhập Office bằng email trường, chọn Account > Update License; nếu vẫn lỗi thì gỡ và cài lại bản Office do trường cấp.', 2);
INSERT INTO BAI_KINH_NGHIEM (TIEU_DE, MA_DM, TRIEU_CHUNG, NGUYEN_NHAN, GIAI_PHAP, MA_NGUOI_VIET) VALUES
 ('Máy tính không nhận địa chỉ IP (169.254.x.x)', 3, 'Cắm dây mạng nhưng báo No Internet, ipconfig ra địa chỉ 169.254.x.x.', 'Máy không liên lạc được với máy chủ DHCP: cáp hỏng, cổng switch bị khóa hoặc card mạng lỗi.', 'Thay cáp và thử cổng mạng khác, chạy ipconfig /release rồi /renew, kiểm tra cổng trên switch có bị tắt không.', 11);

-- 10.5 SU_CO 16-35: 11 HOAN_THANH, 4 DANG_XU_LY, 5 MOI, trải từ tháng 6 đến tháng 11/2026
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy tính PC-A2201-01 bị màn hình xanh', 'Đang làm bài thực hành thì máy hiện màn hình xanh rồi tự khởi động lại.', 12, 11, 13, 1, 'TRUNG_BINH', 'HOAN_THANH', 'Driver card màn hình lỗi sau bản cập nhật Windows.', 'Gỡ driver cũ, cài driver đúng phiên bản từ trang hãng.', 10, TIMESTAMP '2026-06-09 08:20:00', TIMESTAMP '2026-06-10 10:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy chiếu A6-201 tự tắt sau khoảng 15 phút', 'Chiếu được một lúc thì máy chiếu tự tắt, đèn báo nhiệt độ sáng đỏ.', 14, 2, 18, 2, 'CAO', 'HOAN_THANH', 'Lọc gió bám bụi dày làm máy quá nhiệt.', 'Vệ sinh lọc gió và quạt tản nhiệt.', 4, TIMESTAMP '2026-06-25 07:40:00', TIMESTAMP '2026-06-25 11:30:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Office trên máy Văn phòng Khoa CNTT báo chưa kích hoạt', 'Word hiện Unlicensed Product, không lưu được văn bản.', 13, 1, 12, 4, 'THAP', 'HOAN_THANH', 'Máy chưa đăng nhập tài khoản Microsoft 365 của trường.', 'Đăng nhập lại bằng email trường và cập nhật license.', 11, TIMESTAMP '2026-07-14 09:00:00', TIMESTAMP '2026-07-15 16:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Router phòng thí nghiệm mạng mất cấu hình', 'Sau khi mất điện, các máy trong phòng thí nghiệm không ra được Internet.', 15, 11, 24, 3, 'CAO', 'HOAN_THANH', 'Cấu hình chưa được lưu vào startup-config nên mất khi khởi động lại.', 'Nạp lại cấu hình từ bản sao lưu và lưu vào startup-config.', NULL, TIMESTAMP '2026-07-28 13:00:00', TIMESTAMP '2026-07-29 09:30:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy in phòng Công tác sinh viên kẹt giấy', 'In giấy xác nhận sinh viên thì bị kẹt giấy ở khay sau.', 17, 2, 27, 6, 'TRUNG_BINH', 'HOAN_THANH', 'Giấy bị ẩm, con lăn kéo giấy bẩn.', 'Lấy giấy kẹt, lau con lăn, thay ram giấy mới.', 8, TIMESTAMP '2026-08-12 10:15:00', TIMESTAMP '2026-08-12 15:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Không vào được email sau khi đổi mật khẩu', 'Đổi mật khẩu xong thì điện thoại và máy tính đều báo sai mật khẩu.', 16, 11, NULL, 5, 'TRUNG_BINH', 'HOAN_THANH', 'Ứng dụng email trên điện thoại vẫn dùng mật khẩu cũ, đăng nhập sai nhiều lần nên tài khoản bị khóa.', 'Mở khóa tài khoản, hướng dẫn cập nhật mật khẩu mới trên các thiết bị.', 7, TIMESTAMP '2026-08-19 08:05:00', TIMESTAMP '2026-08-19 09:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy tra cứu thư viện TV-05 không có mạng', 'Máy báo No Internet, bạn đọc không tra cứu được tài liệu.', 19, 1, 28, 3, 'THAP', 'HOAN_THANH', 'Cáp mạng bị chuột cắn, máy không nhận được IP.', 'Thay cáp mạng mới, chạy lại ipconfig /renew.', 12, TIMESTAMP '2026-09-03 14:20:00', TIMESTAMP '2026-09-04 10:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy chiếu A6-501 không lên hình', 'Laptop đã cắm cáp HDMI nhưng máy chiếu báo No Signal.', 20, 2, 20, 2, 'CAO', 'HOAN_THANH', 'Máy chiếu đang chọn nguồn VGA thay vì HDMI.', 'Chọn lại nguồn HDMI, dán hướng dẫn sử dụng cạnh bàn giảng viên.', 3, TIMESTAMP '2026-09-17 07:35:00', TIMESTAMP '2026-09-17 08:40:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Chuột máy PC-A2202-08 không hoạt động', 'Di chuột không thấy con trỏ di chuyển, đã thử cắm lại.', 21, 11, 15, 7, 'THAP', 'HOAN_THANH', 'Chuột hỏng mắt đọc quang học.', 'Thay chuột mới.', NULL, TIMESTAMP '2026-10-02 09:10:00', TIMESTAMP '2026-10-02 10:30:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Lỗi đăng ký học phần trên cổng thông tin', 'Bấm Đăng ký thì trang quay mãi rồi báo lỗi 504, nhiều bạn cùng lớp bị như vậy.', 22, 1, NULL, 8, 'CAO', 'HOAN_THANH', 'Máy chủ cổng thông tin quá tải trong đợt đăng ký học phần.', 'Tăng tài nguyên máy chủ, chia lịch đăng ký theo khóa.', NULL, TIMESTAMP '2026-10-08 07:30:00', TIMESTAMP '2026-10-08 17:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy chiếu H1-401 hình bị nhòe', 'Chữ trên slide bị nhòe, chỉnh nét bằng tay không rõ hơn.', 23, 2, 22, 2, 'TRUNG_BINH', 'HOAN_THANH', 'Ống kính bám bụi và bị lệch sau khi di chuyển máy chiếu.', 'Lau ống kính, cố định lại giá treo và căn chỉnh nét.', 4, TIMESTAMP '2026-10-21 13:15:00', TIMESTAMP '2026-10-23 09:00:00');
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy tính PC-A2301-22 không lên màn hình', 'Máy có tiếng quạt nhưng màn hình không hiện gì.', 12, 11, 16, 1, 'CAO', 'DANG_XU_LY', 'Nghi lỏng RAM hoặc hỏng card màn hình.', NULL, 1, TIMESTAMP '2026-10-27 08:00:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Loa và micro giảng đường H1-101 bị rè', 'Nói vào micro thì loa rè và thỉnh thoảng hú to.', 14, 2, 21, 7, 'TRUNG_BINH', 'DANG_XU_LY', 'Dây micro bị đứt ngầm, ampli đặt quá gần loa.', NULL, NULL, TIMESTAMP '2026-10-30 07:45:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('WiFi thư viện tầng 2 chập chờn', 'Bạn đọc phản ánh WiFi tầng 2 lúc vào được lúc không, nhất là buổi chiều.', 19, 1, 29, 3, 'CAO', 'DANG_XU_LY', 'Access Point quá tải vào giờ cao điểm.', NULL, 5, TIMESTAMP '2026-11-03 09:30:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Switch tầng 2 nhà A3 hỏng nhiều cổng', 'Một nửa số máy phòng thí nghiệm mạng mất kết nối LAN.', 15, 11, 25, 3, 'CAO', 'DANG_XU_LY', 'Switch cũ, 8 cổng bị cháy do sét lan truyền.', NULL, 6, TIMESTAMP '2026-11-05 10:00:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy tính PC-A2201-17 không vào được Windows', 'Máy báo Automatic Repair liên tục, không vào được màn hình đăng nhập.', 24, NULL, 14, 1, 'TRUNG_BINH', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-11-09 08:10:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy chiếu A6-305 báo lỗi đèn', 'Đèn LAMP nhấp nháy đỏ, máy chiếu không bật lên được.', 18, NULL, 19, 2, 'CAO', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-11-10 07:20:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Máy in Khoa Công trình không in được', 'Gửi lệnh in thì máy báo Offline dù đã bật nguồn.', 23, NULL, 30, 6, 'TRUNG_BINH', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-11-10 14:00:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Không cài được MATLAB trên máy phòng máy 4', 'Cài đến 80% thì báo lỗi thiếu quyền quản trị.', 13, NULL, 15, 4, 'THAP', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-11-11 09:45:00', NULL);
INSERT INTO SU_CO (TIEU_DE, MO_TA, MA_NGUOI_BAO, MA_ADMIN_XU_LY, MA_TB, MA_DM, MUC_UU_TIEN, TRANG_THAI, NGUYEN_NHAN, CACH_KHAC_PHUC, MA_BAI_KN, NGAY_TAO, NGAY_HOAN_THANH) VALUES ('Quên mật khẩu email trường', 'Lâu không dùng nên quên mật khẩu, không nhận được mã khôi phục.', 20, NULL, NULL, 5, 'THAP', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-11-11 16:30:00', NULL);

-- 10.6 LICH_SU_XU_LY cho sự cố 16-30 (2 dòng mỗi sự cố đã xong, 1 dòng mỗi sự cố đang xử lý)
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 16, 11, 'MOI', 'DANG_XU_LY', 'Tiếp nhận, kiểm tra nhật ký lỗi của Windows', TIMESTAMP '2026-06-09 09:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 16, 11, 'DANG_XU_LY', 'HOAN_THANH', 'Đã cài lại driver card màn hình', TIMESTAMP '2026-06-10 10:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 17, 2,  'MOI', 'DANG_XU_LY', 'Đến phòng kiểm tra quạt và lọc gió', TIMESTAMP '2026-06-25 08:15:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 17, 2,  'DANG_XU_LY', 'HOAN_THANH', 'Đã vệ sinh, chạy thử 1 tiếng không tắt', TIMESTAMP '2026-06-25 11:30:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 18, 1,  'MOI', 'DANG_XU_LY', 'Hẹn giờ qua văn phòng khoa kiểm tra', TIMESTAMP '2026-07-15 08:30:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 18, 1,  'DANG_XU_LY', 'HOAN_THANH', 'Đã kích hoạt lại Office', TIMESTAMP '2026-07-15 16:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 19, 11, 'MOI', 'DANG_XU_LY', 'Kiểm tra cấu hình router', TIMESTAMP '2026-07-28 14:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 19, 11, 'DANG_XU_LY', 'HOAN_THANH', 'Đã nạp lại cấu hình và lưu startup-config', TIMESTAMP '2026-07-29 09:30:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 20, 2,  'MOI', 'DANG_XU_LY', 'Kiểm tra khay giấy và con lăn', TIMESTAMP '2026-08-12 13:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 20, 2,  'DANG_XU_LY', 'HOAN_THANH', 'Đã lấy giấy kẹt, lau con lăn', TIMESTAMP '2026-08-12 15:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 21, 11, 'MOI', 'DANG_XU_LY', 'Kiểm tra trạng thái tài khoản', TIMESTAMP '2026-08-19 08:20:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 21, 11, 'DANG_XU_LY', 'HOAN_THANH', 'Đã mở khóa tài khoản', TIMESTAMP '2026-08-19 09:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 22, 1,  'MOI', 'DANG_XU_LY', 'Kiểm tra cáp và cổng mạng', TIMESTAMP '2026-09-04 08:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 22, 1,  'DANG_XU_LY', 'HOAN_THANH', 'Đã thay cáp mạng', TIMESTAMP '2026-09-04 10:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 23, 2,  'MOI', 'DANG_XU_LY', 'Khẩn: lớp đang chờ, xuống phòng ngay', TIMESTAMP '2026-09-17 07:45:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 23, 2,  'DANG_XU_LY', 'HOAN_THANH', 'Đã chọn lại nguồn HDMI', TIMESTAMP '2026-09-17 08:40:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 24, 11, 'MOI', 'DANG_XU_LY', 'Mang chuột dự phòng xuống phòng máy', TIMESTAMP '2026-10-02 09:40:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 24, 11, 'DANG_XU_LY', 'HOAN_THANH', 'Đã thay chuột mới', TIMESTAMP '2026-10-02 10:30:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 25, 1,  'MOI', 'DANG_XU_LY', 'Phối hợp đơn vị quản trị máy chủ kiểm tra tải', TIMESTAMP '2026-10-08 08:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 25, 1,  'DANG_XU_LY', 'HOAN_THANH', 'Đã tăng tài nguyên máy chủ, cổng hoạt động bình thường', TIMESTAMP '2026-10-08 17:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 26, 2,  'MOI', 'DANG_XU_LY', 'Kiểm tra ống kính và giá treo', TIMESTAMP '2026-10-22 08:30:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 26, 2,  'DANG_XU_LY', 'HOAN_THANH', 'Đã lau ống kính, căn chỉnh lại', TIMESTAMP '2026-10-23 09:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 27, 11, 'MOI', 'DANG_XU_LY', 'Đang thử từng thanh RAM', TIMESTAMP '2026-10-27 09:30:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 28, 2,  'MOI', 'DANG_XU_LY', 'Chờ mua dây micro mới', TIMESTAMP '2026-10-30 10:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 29, 1,  'MOI', 'DANG_XU_LY', 'Đo tải Access Point, đề xuất lắp thêm 1 AP', TIMESTAMP '2026-11-03 14:00:00');
INSERT INTO LICH_SU_XU_LY VALUES (DEFAULT, 30, 11, 'MOI', 'DANG_XU_LY', 'Đã làm đề xuất mua switch thay thế', TIMESTAMP '2026-11-05 15:30:00');

-- 10.7 PHAN_HOI cho một số sự cố đã xong (để trống 21 và 24 cho demo "chưa đánh giá")
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (16, 12, 4, 'Máy chạy ổn định lại, chỉ tiếc mất một buổi thực hành.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (17, 14, 5, 'Xử lý ngay trong buổi sáng, rất nhanh.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (18, 13, 4, 'Đã dùng được Office bình thường.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (19, 15, 5, 'Kịp cho buổi thực hành mạng hôm sau.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (20, 17, 3, 'Đã in được nhưng vẫn thỉnh thoảng kẹt giấy.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (22, 19, 4, 'Bạn đọc tra cứu được rồi, cảm ơn.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (23, 20, 5, 'Có mặt sau 10 phút, rất nhiệt tình.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (25, 22, 2, 'Phải chờ gần hết ngày mới đăng ký được, nhiều môn đã hết chỗ.');
INSERT INTO PHAN_HOI (MA_SC, MA_NGUOI_PHAN_HOI, MUC_HAI_LONG, NOI_DUNG) VALUES (26, 23, 4, 'Hình rõ hơn nhiều so với trước.');

-- 10.8 BINH_LUAN_SU_CO bổ sung
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (25, 22, 'Lớp em hôm nay là hạn cuối đăng ký, mong IT xử lý gấp ạ.', TIMESTAMP '2026-10-08 07:40:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (25, 1,  'Đã báo Phòng Đào tạo gia hạn đăng ký thêm 1 ngày, em yên tâm nhé.', TIMESTAMP '2026-10-08 10:15:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (27, 12, 'Máy này có dữ liệu bài tập lớn của nhóm em, khi sửa có bị mất không ạ?', TIMESTAMP '2026-10-27 10:00:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (27, 11, 'Lỗi phần cứng nên ổ cứng vẫn giữ nguyên, nhưng em nên sao lưu lên Drive cho chắc.', TIMESTAMP '2026-10-27 11:20:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (29, 19, 'Chiều nay có lớp kỹ năng thông tin 60 sinh viên dùng WiFi tầng 2.', TIMESTAMP '2026-11-04 08:00:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (29, 1,  'IT sẽ lắp tạm một Access Point di động trước 13h.', TIMESTAMP '2026-11-04 09:10:00');
INSERT INTO BINH_LUAN_SU_CO (MA_SC, MA_NGUOI_GUI, NOI_DUNG, THOI_GIAN) VALUES (32, 18, 'Thứ 2 tuần sau phòng này có lịch thi vấn đáp, cần máy chiếu.', TIMESTAMP '2026-11-10 08:00:00');

COMMIT;

-- Kiểm tra số dòng của từng bảng
SELECT 'NGUOI_DUNG' AS BANG, COUNT(*) AS SO_DONG FROM NGUOI_DUNG UNION ALL
SELECT 'PHONG', COUNT(*) FROM PHONG UNION ALL
SELECT 'THIET_BI', COUNT(*) FROM THIET_BI UNION ALL
SELECT 'DANH_MUC_SU_CO', COUNT(*) FROM DANH_MUC_SU_CO UNION ALL
SELECT 'BAI_KINH_NGHIEM', COUNT(*) FROM BAI_KINH_NGHIEM UNION ALL
SELECT 'SU_CO', COUNT(*) FROM SU_CO UNION ALL
SELECT 'LICH_SU_XU_LY', COUNT(*) FROM LICH_SU_XU_LY UNION ALL
SELECT 'PHAN_HOI', COUNT(*) FROM PHAN_HOI UNION ALL
SELECT 'BINH_LUAN_SU_CO', COUNT(*) FROM BINH_LUAN_SU_CO;
