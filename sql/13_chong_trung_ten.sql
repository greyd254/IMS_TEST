-- =====================================================================
-- 13_chong_trung_ten.sql
-- CHẠY BẰNG: SUCO_APP, kết nối vào FREEPDB1
-- MỤC ĐÍCH : (TÙY CHỌN) chặn trùng tên ngay ở CSDL:
--            - PHONG: không có 2 phòng cùng tòa, cùng tầng, cùng tên
--            - THIET_BI: không có 2 thiết bị cùng phòng, cùng loại, cùng tên
--            So sánh không phân biệt hoa thường, bỏ khoảng trắng hai đầu
--            (function-based unique index).
--            web-java đã tự kiểm tra trùng trước khi INSERT/UPDATE nên chạy
--            được cả khi CHƯA có file này; index chỉ là lớp bảo vệ cuối
--            (trường hợp 2 người lưu cùng lúc, hoặc INSERT tay bằng SQL).
--            Lưu ý: CSDL dùng chung với web-oracle (bản đã nộp), chạy file
--            này thì web-oracle gặp tên trùng sẽ ra trang lỗi ORA-00001.
-- =====================================================================
SET DEFINE OFF

-- Bước 1: kiểm tra dữ liệu hiện có. Nếu 2 câu dưới trả về dòng nào thì
-- phải đổi tên/xóa bớt các dòng đó trước, nếu không CREATE INDEX báo ORA-01452.
SELECT UPPER(TRIM(TOA_NHA)) AS TOA_NHA, TANG, UPPER(TRIM(TEN_PHONG)) AS TEN_PHONG, COUNT(*) AS SO_DONG
FROM PHONG
GROUP BY UPPER(TRIM(TOA_NHA)), TANG, UPPER(TRIM(TEN_PHONG))
HAVING COUNT(*) > 1;

SELECT MA_PHONG, LOAI_TB, UPPER(TRIM(TEN_TB)) AS TEN_TB, COUNT(*) AS SO_DONG
FROM THIET_BI
GROUP BY MA_PHONG, LOAI_TB, UPPER(TRIM(TEN_TB))
HAVING COUNT(*) > 1;

-- Bước 2: tạo index. TOA_NHA, TEN_PHONG NOT NULL nên mọi dòng đều được kiểm tra;
-- 2 phòng cùng để trống TANG vẫn bị coi là trùng (Oracle chỉ bỏ qua khi TẤT CẢ cột khóa NULL).
CREATE UNIQUE INDEX UQ_PHONG_TEN ON PHONG (UPPER(TRIM(TOA_NHA)), TANG, UPPER(TRIM(TEN_PHONG)));

CREATE UNIQUE INDEX UQ_THIET_BI_TEN ON THIET_BI (MA_PHONG, LOAI_TB, UPPER(TRIM(TEN_TB)));

SELECT INDEX_NAME, TABLE_NAME, UNIQUENESS FROM USER_INDEXES
WHERE INDEX_NAME IN ('UQ_PHONG_TEN', 'UQ_THIET_BI_TEN');
