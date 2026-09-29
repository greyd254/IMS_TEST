-- =====================================================================
-- 04_data.sql
-- CHẠY BẰNG: SUCO_APP, kết nối vào FREEPDB1 (sau khi đã chạy 02 và 03)
-- MỤC ĐÍCH : dữ liệu mẫu tiếng Việt cho 7 bảng.
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
-- 6.1 Sự cố đã HOAN_THANH (chèn trực tiếp, nên phải tự điền NGAY_HOAN_THANH vì trigger chỉ chạy khi UPDATE)
INSERT INTO SU_CO VALUES (DEFAULT, 'WiFi tầng 3 nhà H1 chập chờn', 'Sinh viên học ở H1-305 không vào được mạng, WiFi lúc có lúc không.', 8, 1, 11, 3, 'CAO', 'HOAN_THANH', 'Access Point bị treo do quá tải.', 'Khởi động lại Access Point và nâng cấp firmware.', 5, TIMESTAMP '2026-04-12 13:30:00', TIMESTAMP '2026-04-13 10:00:00');
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy chiếu H1-305 không nhận HDMI', 'Giảng viên cắm laptop nhưng máy chiếu báo No Signal.', 7, 2, 8, 2, 'CAO', 'HOAN_THANH', 'Cáp HDMI âm tường bị lỏng đầu nối.', 'Cắm lại đầu nối và thay cáp HDMI mới.', 3, TIMESTAMP '2026-05-08 08:30:00', TIMESTAMP '2026-05-08 11:00:00');
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy tính PC-A2101-12 chạy rất chậm', 'Mở phần mềm lập trình mất hơn 5 phút.', 3, 1, 2, 1, 'THAP', 'HOAN_THANH', 'Ổ cứng HDD bị bad sector.', 'Thay ổ SSD 256GB và cài lại hệ điều hành.', 2, TIMESTAMP '2026-05-20 09:00:00', TIMESTAMP '2026-05-22 15:00:00');
INSERT INTO SU_CO VALUES (DEFAULT, 'Không đăng nhập được email trường', 'Báo sai mật khẩu dù nhập đúng.', 4, 2, NULL, 5, 'TRUNG_BINH', 'HOAN_THANH', 'Tài khoản bị khóa do nhập sai nhiều lần.', 'Mở khóa và đặt lại mật khẩu.', 7, TIMESTAMP '2026-06-03 08:10:00', TIMESTAMP '2026-06-03 09:45:00');
INSERT INTO SU_CO VALUES (DEFAULT, 'Switch tầng 2 nhà A2 mất kết nối phòng máy 5', 'Cả phòng 30 máy không có mạng, đang chuẩn bị thi thực hành.', 5, 1, 4, 3, 'CAO', 'HOAN_THANH', 'Cáp uplink bị lỏng sau khi vệ sinh phòng.', 'Cắm chặt cáp uplink và khởi động lại switch.', 6, TIMESTAMP '2026-06-18 07:45:00', TIMESTAMP '2026-06-19 08:30:00');
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy in Phòng Đào tạo in bị mờ', 'Bản in ra chữ nhạt, khó đọc.', 6, 2, 9, 6, 'THAP', 'HOAN_THANH', 'Hộp mực gần hết.', 'Thay hộp mực mới.', 8, TIMESTAMP '2026-07-07 10:00:00', TIMESTAMP '2026-07-10 14:20:00');
INSERT INTO SU_CO VALUES (DEFAULT, 'Không cài được AutoCAD do lỗi license', 'Cài xong mở phần mềm báo Cannot connect to license server.', 9, 1, 12, 4, 'TRUNG_BINH', 'HOAN_THANH', 'Máy chưa khai báo đúng địa chỉ máy chủ license.', 'Khai báo lại địa chỉ máy chủ license của trường.', 9, TIMESTAMP '2026-08-04 09:30:00', TIMESTAMP '2026-08-06 16:00:00');
INSERT INTO SU_CO VALUES (DEFAULT, 'Tài khoản cổng thông tin sinh viên bị khóa', 'Không đăng nhập được để đăng ký học phần.', 4, 1, NULL, 5, 'TRUNG_BINH', 'HOAN_THANH', 'Tài khoản bị khóa do quá số lần đăng nhập sai.', 'Mở khóa tài khoản và hướng dẫn đổi mật khẩu.', 7, TIMESTAMP '2026-08-25 08:00:00', TIMESTAMP '2026-08-25 10:30:00');
-- 6.2 Sự cố DANG_XU_LY
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy tính PC-A2101-05 không khởi động', 'Bấm nút nguồn máy không lên, không có đèn.', 3, 1, 1, 1, 'CAO', 'DANG_XU_LY', 'Nghi hỏng bộ nguồn.', NULL, 1, TIMESTAMP '2026-09-10 08:15:00', NULL);
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy tính PC-A2102-03 báo lỗi RAM', 'Máy kêu bíp liên tục khi bật.', 3, 2, 3, 1, 'TRUNG_BINH', 'DANG_XU_LY', 'Thanh RAM lỏng hoặc hỏng.', NULL, NULL, TIMESTAMP '2026-09-05 14:00:00', NULL);
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy chiếu H1-201 hình mờ, ám vàng', 'Hình ảnh chiếu lên màn bị ám vàng, khó nhìn.', 7, 2, 7, 2, 'TRUNG_BINH', 'DANG_XU_LY', 'Nghi bóng đèn sắp hết tuổi thọ.', NULL, 4, TIMESTAMP '2026-09-15 07:50:00', NULL);
-- 6.3 Sự cố MOI (chưa có admin xử lý)
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy chiếu A6-301 không lên hình', 'Bật máy chiếu nhưng đèn báo đỏ, không có hình.', 5, NULL, 5, 2, 'CAO', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-09-22 07:30:00', NULL);
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy in Phòng Đào tạo báo kẹt giấy', 'Máy in báo kẹt giấy liên tục dù đã lấy giấy ra.', 6, NULL, 9, 6, 'TRUNG_BINH', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-09-20 15:10:00', NULL);
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy tính phòng Kế hoạch - Tài chính không vào được mạng nội bộ', 'Không truy cập được phần mềm kế toán dùng chung.', 10, NULL, 10, 3, 'CAO', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-09-24 09:05:00', NULL);
INSERT INTO SU_CO VALUES (DEFAULT, 'Máy chiếu A6-402 bị rung hình', 'Hình chiếu rung nhẹ khi giảng viên chiếu slide.', 8, NULL, 6, 2, 'THAP', 'MOI', NULL, NULL, NULL, TIMESTAMP '2026-09-23 13:40:00', NULL);

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

COMMIT;

-- Kiểm tra số dòng của từng bảng
SELECT 'NGUOI_DUNG' AS BANG, COUNT(*) AS SO_DONG FROM NGUOI_DUNG UNION ALL
SELECT 'PHONG', COUNT(*) FROM PHONG UNION ALL
SELECT 'THIET_BI', COUNT(*) FROM THIET_BI UNION ALL
SELECT 'DANH_MUC_SU_CO', COUNT(*) FROM DANH_MUC_SU_CO UNION ALL
SELECT 'BAI_KINH_NGHIEM', COUNT(*) FROM BAI_KINH_NGHIEM UNION ALL
SELECT 'SU_CO', COUNT(*) FROM SU_CO UNION ALL
SELECT 'LICH_SU_XU_LY', COUNT(*) FROM LICH_SU_XU_LY;
