-- =====================================================================
-- 05_queries.sql
-- CHẠY BẰNG: SUCO_APP, kết nối vào FREEPDB1 (sau khi đã có dữ liệu ở file 04)
-- MỤC ĐÍCH : VIEW phục vụ web + 15 truy vấn mẫu (cơ bản, JOIN, lồng, GROUP BY...).
-- =====================================================================
SET DEFINE OFF
SET LINESIZE 250
SET PAGESIZE 100
COLUMN TIEU_DE FORMAT A50
COLUMN TEN_TB FORMAT A40
COLUMN TEN_PHONG FORMAT A35
COLUMN HO_TEN FORMAT A25
COLUMN TEN_DM FORMAT A28

-- ---------------------------------------------------------------------
-- VIEW V_SU_CO_CHI_TIET: ghép SU_CO với người báo, admin, thiết bị, phòng, danh mục.
-- Web đọc từ view này để khỏi phải JOIN lại nhiều lần.
-- Dùng LEFT JOIN cho admin và thiết bị vì 2 cột này có thể NULL.
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW V_SU_CO_CHI_TIET AS
SELECT sc.MA_SC,
       sc.TIEU_DE,
       sc.MO_TA,
       sc.MA_NGUOI_BAO,
       nb.HO_TEN   AS TEN_NGUOI_BAO,
       sc.MA_ADMIN_XU_LY,
       ad.HO_TEN   AS TEN_ADMIN,
       sc.MA_TB,
       tb.TEN_TB,
       p.TEN_PHONG,
       p.TOA_NHA,
       sc.MA_DM,
       dm.TEN_DM,
       sc.MUC_UU_TIEN,
       sc.TRANG_THAI,
       sc.NGUYEN_NHAN,
       sc.CACH_KHAC_PHUC,
       sc.MA_BAI_KN,
       sc.ANH_DINH_KEM,
       sc.NGAY_TAO,
       sc.NGAY_HOAN_THANH
  FROM SU_CO sc
  JOIN NGUOI_DUNG nb ON nb.MA_ND = sc.MA_NGUOI_BAO
  LEFT JOIN NGUOI_DUNG ad ON ad.MA_ND = sc.MA_ADMIN_XU_LY
  LEFT JOIN THIET_BI tb ON tb.MA_TB = sc.MA_TB
  LEFT JOIN PHONG p ON p.MA_PHONG = tb.MA_PHONG
  JOIN DANH_MUC_SU_CO dm ON dm.MA_DM = sc.MA_DM;

-- Q1. Truy vấn cơ bản có điều kiện: sự cố mức CAO chưa hoàn thành, mới nhất trước
SELECT MA_SC, TIEU_DE, TRANG_THAI, NGAY_TAO
  FROM SU_CO
 WHERE MUC_UU_TIEN = 'CAO' AND TRANG_THAI <> 'HOAN_THANH'
 ORDER BY NGAY_TAO DESC;

-- Q2. Tìm kiếm theo mẫu: sự cố có tiêu đề chứa từ "máy chiếu" (LIKE, không phân biệt hoa/thường)
SELECT MA_SC, TIEU_DE, TRANG_THAI
  FROM SU_CO
 WHERE LOWER(TIEU_DE) LIKE '%máy chiếu%';

-- Q3. JOIN 3 bảng: thiết bị đang hỏng hoặc đang sửa kèm phòng và tòa nhà
SELECT tb.MA_TB, tb.TEN_TB, tb.TINH_TRANG, p.TEN_PHONG, p.TOA_NHA
  FROM THIET_BI tb
  JOIN PHONG p ON p.MA_PHONG = tb.MA_PHONG
 WHERE tb.TINH_TRANG IN ('HONG', 'DANG_SUA')
 ORDER BY p.TOA_NHA, p.TEN_PHONG;

-- Q4. JOIN 5 bảng (qua view): chi tiết sự cố đã hoàn thành cùng người báo, admin, phòng
SELECT MA_SC, TIEU_DE, TEN_NGUOI_BAO, TEN_ADMIN, TEN_PHONG, TEN_DM, NGAY_TAO, NGAY_HOAN_THANH
  FROM V_SU_CO_CHI_TIET
 WHERE TRANG_THAI = 'HOAN_THANH'
 ORDER BY NGAY_TAO;

-- Q5. Truy vấn lồng với IN: người dùng đã từng báo ít nhất một sự cố mức CAO
SELECT MA_ND, HO_TEN, DON_VI
  FROM NGUOI_DUNG
 WHERE MA_ND IN (SELECT MA_NGUOI_BAO FROM SU_CO WHERE MUC_UU_TIEN = 'CAO');

-- Q6. Truy vấn lồng với EXISTS: admin đã xử lý ít nhất một sự cố
SELECT nd.MA_ND, nd.HO_TEN
  FROM NGUOI_DUNG nd
 WHERE nd.VAI_TRO = 'ADMIN'
   AND EXISTS (SELECT 1 FROM SU_CO sc WHERE sc.MA_ADMIN_XU_LY = nd.MA_ND);

-- Q7. NOT EXISTS: danh mục CHƯA có bài kinh nghiệm nào (cần bổ sung nội dung)
SELECT dm.MA_DM, dm.TEN_DM
  FROM DANH_MUC_SU_CO dm
 WHERE NOT EXISTS (SELECT 1 FROM BAI_KINH_NGHIEM b WHERE b.MA_DM = dm.MA_DM);

-- Q8. Subquery trong WHERE: sự cố đã xong có thời gian xử lý LỚN HƠN mức trung bình (ngày)
SELECT MA_SC, TIEU_DE, ROUND(NGAY_HOAN_THANH - NGAY_TAO, 2) AS SO_NGAY_XU_LY
  FROM SU_CO
 WHERE TRANG_THAI = 'HOAN_THANH'
   AND (NGAY_HOAN_THANH - NGAY_TAO) >
       (SELECT AVG(NGAY_HOAN_THANH - NGAY_TAO) FROM SU_CO WHERE TRANG_THAI = 'HOAN_THANH')
 ORDER BY SO_NGAY_XU_LY DESC;

-- Q9. Subquery trong FROM (inline view): top 3 thiết bị có nhiều sự cố nhất
SELECT t.TEN_TB, t.SO_SU_CO
  FROM (SELECT tb.TEN_TB, COUNT(*) AS SO_SU_CO
          FROM SU_CO sc
          JOIN THIET_BI tb ON tb.MA_TB = sc.MA_TB
         GROUP BY tb.TEN_TB) t
 ORDER BY t.SO_SU_CO DESC
 FETCH FIRST 3 ROWS ONLY;

-- Q10. GROUP BY + HAVING: phòng có từ 2 sự cố trở lên (top phòng nhiều sự cố nhất)
SELECT p.TEN_PHONG, p.TOA_NHA, COUNT(*) AS SO_SU_CO
  FROM SU_CO sc
  JOIN THIET_BI tb ON tb.MA_TB = sc.MA_TB
  JOIN PHONG p ON p.MA_PHONG = tb.MA_PHONG
 GROUP BY p.TEN_PHONG, p.TOA_NHA
HAVING COUNT(*) >= 2
 ORDER BY SO_SU_CO DESC;

-- Q11. Số sự cố theo tháng
SELECT TO_CHAR(NGAY_TAO, 'YYYY-MM') AS THANG, COUNT(*) AS SO_SU_CO
  FROM SU_CO
 GROUP BY TO_CHAR(NGAY_TAO, 'YYYY-MM')
 ORDER BY THANG;

-- Q12. Các hàm gộp COUNT/AVG/MAX/MIN/SUM: thời gian xử lý (ngày) theo danh mục, chỉ tính sự cố đã xong
SELECT dm.TEN_DM,
       COUNT(*)                                          AS SO_SU_CO,
       ROUND(AVG(sc.NGAY_HOAN_THANH - sc.NGAY_TAO), 2)   AS TB_NGAY,
       ROUND(MAX(sc.NGAY_HOAN_THANH - sc.NGAY_TAO), 2)   AS LAU_NHAT,
       ROUND(MIN(sc.NGAY_HOAN_THANH - sc.NGAY_TAO), 2)   AS NHANH_NHAT,
       ROUND(SUM(sc.NGAY_HOAN_THANH - sc.NGAY_TAO), 2)   AS TONG_NGAY
  FROM SU_CO sc
  JOIN DANH_MUC_SU_CO dm ON dm.MA_DM = sc.MA_DM
 WHERE sc.TRANG_THAI = 'HOAN_THANH'
 GROUP BY dm.TEN_DM
 ORDER BY TB_NGAY DESC;

-- Q13. Admin xử lý nhiều sự cố nhất (COUNT theo admin, lấy người đứng đầu)
SELECT nd.HO_TEN, COUNT(*) AS SO_SU_CO_XU_LY
  FROM SU_CO sc
  JOIN NGUOI_DUNG nd ON nd.MA_ND = sc.MA_ADMIN_XU_LY
 GROUP BY nd.HO_TEN
 ORDER BY SO_SU_CO_XU_LY DESC
 FETCH FIRST 1 ROWS WITH TIES;

-- Q14. LEFT JOIN: số sự cố của MỌI người dùng (kể cả người chưa báo sự cố nào = 0)
SELECT nd.HO_TEN, nd.DON_VI, COUNT(sc.MA_SC) AS SO_SU_CO_DA_BAO
  FROM NGUOI_DUNG nd
  LEFT JOIN SU_CO sc ON sc.MA_NGUOI_BAO = nd.MA_ND
 WHERE nd.VAI_TRO = 'USER'
 GROUP BY nd.HO_TEN, nd.DON_VI
 ORDER BY SO_SU_CO_DA_BAO DESC, nd.HO_TEN;

-- Q15. Thống kê theo trạng thái và mức ưu tiên (GROUP BY nhiều cột)
SELECT TRANG_THAI, MUC_UU_TIEN, COUNT(*) AS SO_SU_CO
  FROM SU_CO
 GROUP BY TRANG_THAI, MUC_UU_TIEN
 ORDER BY TRANG_THAI, MUC_UU_TIEN;
