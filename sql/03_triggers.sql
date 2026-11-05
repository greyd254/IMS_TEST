-- =====================================================================
-- 03_triggers.sql
-- CHẠY BẰNG: SUCO_APP, kết nối vào FREEPDB1
-- MỤC ĐÍCH : 2 trigger trên SU_CO: tự ghi lịch sử và tự gán ngày hoàn thành.
-- =====================================================================
SET DEFINE OFF

-- ---------------------------------------------------------------------
-- TRG_SU_CO_LICH_SU
-- Sau khi cột TRANG_THAI bị UPDATE và giá trị thực sự đổi (mệnh đề WHEN),
-- ghi 1 dòng vào LICH_SU_XU_LY. Người cập nhật lấy từ :NEW.MA_ADMIN_XU_LY
-- (web gán admin đang đăng nhập vào cột này trước khi lưu).
-- ---------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SU_CO_LICH_SU
AFTER UPDATE OF TRANG_THAI ON SU_CO
FOR EACH ROW
WHEN (OLD.TRANG_THAI <> NEW.TRANG_THAI)
BEGIN
  INSERT INTO LICH_SU_XU_LY
         (MA_SC, MA_NGUOI_CAP_NHAT, TRANG_THAI_CU, TRANG_THAI_MOI, GHI_CHU)
  VALUES (:NEW.MA_SC, :NEW.MA_ADMIN_XU_LY, :OLD.TRANG_THAI, :NEW.TRANG_THAI,
          'Cập nhật trạng thái xử lý sự cố');
END;
/

-- ---------------------------------------------------------------------
-- TRG_SU_CO_HOAN_THANH
-- BEFORE UPDATE: khi trạng thái chuyển sang HOAN_THANH thì tự gán
-- NGAY_HOAN_THANH = SYSDATE. Nếu mở lại sự cố (chuyển khỏi HOAN_THANH)
-- thì xóa ngày hoàn thành để dữ liệu thống kê không bị sai.
-- Dùng BEFORE vì cần sửa giá trị :NEW trước khi dòng được ghi xuống.
-- ---------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SU_CO_HOAN_THANH
BEFORE UPDATE OF TRANG_THAI ON SU_CO
FOR EACH ROW
BEGIN
  IF :NEW.TRANG_THAI = 'HOAN_THANH' AND :OLD.TRANG_THAI <> 'HOAN_THANH' THEN
    :NEW.NGAY_HOAN_THANH := SYSDATE;
  ELSIF :NEW.TRANG_THAI <> 'HOAN_THANH' THEN
    :NEW.NGAY_HOAN_THANH := NULL;
  END IF;
END;
/

-- Kiểm tra: cả 2 trigger phải ở trạng thái ENABLED
SELECT trigger_name, triggering_event, status FROM user_triggers ORDER BY trigger_name;
