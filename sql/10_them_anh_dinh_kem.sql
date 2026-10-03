-- =====================================================================
-- 10_them_anh_dinh_kem.sql
-- CHẠY BẰNG: SUCO_APP, kết nối vào FREEPDB1
-- MỤC ĐÍCH : thêm cột ảnh đính kèm cho sự cố (không bắt buộc), áp dụng cho
--            CSDL đã dựng từ trước (không cần drop/tạo lại). Nếu dựng CSDL
--            từ đầu bằng 00_run_all.sql thì cột này đã có sẵn trong 02_tables.sql,
--            không cần chạy file này.
-- =====================================================================
SET DEFINE OFF

ALTER TABLE SU_CO ADD ANH_DINH_KEM VARCHAR2(500 CHAR);

-- Cập nhật lại view để trả thêm cột ảnh cho web
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

-- Kiểm tra: phải thấy cột ANH_DINH_KEM
SELECT column_name FROM user_tab_columns WHERE table_name = 'SU_CO' ORDER BY column_id;
