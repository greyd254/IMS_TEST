-- =====================================================================
-- 05_views.sql
-- CHẠY BẰNG: SUCO_APP, kết nối vào FREEPDB1 (sau khi đã có dữ liệu ở file 04)
-- MỤC ĐÍCH : 2 VIEW web đọc trực tiếp (bắt buộc phải có để chạy web).
-- =====================================================================
SET DEFINE OFF

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

-- ---------------------------------------------------------------------
-- VIEW V_SU_CO_THUONG_GAP: đếm số sự cố theo từng danh mục để biết "sự cố hay gặp".
-- Tính bằng truy vấn thay vì lưu thành cột trên SU_CO, vì đây là giá trị SUY RA được
-- (COUNT), lưu cứng sẽ phải tự đồng bộ tay và dễ lệch với dữ liệu thật.
-- Web (trang người dùng) đọc từ view này.
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW V_SU_CO_THUONG_GAP AS
SELECT dm.MA_DM, dm.TEN_DM, COUNT(sc.MA_SC) AS SO_LAN_BAO
  FROM DANH_MUC_SU_CO dm
  LEFT JOIN SU_CO sc ON sc.MA_DM = dm.MA_DM
 GROUP BY dm.MA_DM, dm.TEN_DM;

-- Kiểm tra: phải thấy 2 view
SELECT view_name FROM user_views ORDER BY view_name;
