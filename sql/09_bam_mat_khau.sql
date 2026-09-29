-- =====================================================================
-- 09_bam_mat_khau.sql
-- CHẠY BẰNG: SUCO_APP, kết nối vào FREEPDB1 (chỉ cần với CSDL ĐÃ dựng từ trước khi có băm mật khẩu;
--            dựng mới từ 02 + 04 thì không cần file này).
-- MỤC ĐÍCH : mở rộng cột MAT_KHAU để chứa chuỗi băm PBKDF2 và thay mật khẩu plain text của 10 user mẫu
--            bằng chuỗi băm (mật khẩu đăng nhập không đổi: admin123 / 123456).
-- LƯU Ý    : user tạo thêm qua web trước đây vẫn đang lưu plain text và sẽ KHÔNG đăng nhập được nữa;
--            admin vào "Quản lý người dùng" > Sửa để đặt lại mật khẩu (web sẽ băm khi lưu).
-- =====================================================================
SET DEFINE OFF
ALTER TABLE NGUOI_DUNG MODIFY (MAT_KHAU VARCHAR2(200 CHAR));

UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$fY+9x/A0wBfvWPJFgeFj0A==$Lu8triqDKGHWc4NpV2NzbTAI+Fk4NhWtCkOMSxHcSuA=' WHERE EMAIL = 'hung.nv@utc.edu.vn';
UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$XAo60bnEE4Af5EoEODrTJw==$32Vbr+UdMEfIhigiYwKBPh54RupXpPNE9pKjJNm0FqA=' WHERE EMAIL = 'ha.ttt@utc.edu.vn';
UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$jxpm88XplJYh56VwD5cKgg==$gBPOUrnSKz2XK5laH7LmePliHf+PCuv73AJhMrD2fPs=' WHERE EMAIL = 'anh.pd@st.utc.edu.vn';
UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$dVYpFu1zCMxa9TGtjKO/Eg==$AJTFS1xyctv1kuWa/Eo2xFojIw4hYDdnmjRqsP1CV04=' WHERE EMAIL = 'lan.lt@utc.edu.vn';
UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$g6uoANlDXjCg8RtB9h26jg==$Dsl3EfttEvLW6+rWwVe3NH97BZ1tOIj6bmTGDhsUdbk=' WHERE EMAIL = 'tuan.vm@utc.edu.vn';
UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$SbufqQqJ92BkWOs4g7gI8g==$bEx7rNUaT8Te0gHoEMLRjjZkxy69TOnNM7FZQa1guFA=' WHERE EMAIL = 'hang.dt@utc.edu.vn';
UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$8arO7BtiOims6mUYmFTQLQ==$n1w+BQb0SmpDe/vwdmQNseW6grxlpDWFsYQtrAarkXM=' WHERE EMAIL = 'nam.hv@utc.edu.vn';
UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$IiSNI+xzXBjDoz6uor0RcA==$vFjeMrwDAEZWGOlzWZR+V+1nTH4+9yQkW3Tp/DnsvKw=' WHERE EMAIL = 'trang.nq@st.utc.edu.vn';
UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$gnEJMmDqsP+wqSSbpL7GPA==$V8kpsTImFW6McpqTuZaJ817yXWeaAtpgy1EUg+5Uhdg=' WHERE EMAIL = 'truong.bx@st.utc.edu.vn';
UPDATE NGUOI_DUNG SET MAT_KHAU = 'PBKDF2$120000$19VxlYmKE33DhgSOPr3Qfw==$TWCZ9UkgSjn5Kw/thhnfF+yYhazVTB6CPtWAuQtk7EU=' WHERE EMAIL = 'ngoc.dt@utc.edu.vn';

COMMIT;
SELECT EMAIL, SUBSTR(MAT_KHAU, 1, 20) AS MAT_KHAU FROM NGUOI_DUNG ORDER BY MA_ND;
