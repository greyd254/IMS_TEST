const fs = require('fs');
const L = require('./lib.js');
const { D, FONT, W_PORTRAIT, P, bullet, H1, H2, H3, code, table, resultTable, tblCaption, gap, figure, placeholder, readLines, extract, cleanLog, logSections } = L;
const { Document, Packer, Paragraph, TextRun, AlignmentType, PageOrientation, LevelFormat, Footer, PageNumber, TableOfContents, BorderStyle } = D;

const ROOT = 'C:/Users/GreyD/IdeaProjects/IMS';
const SQLD = ROOT + '/sql', KQ = SQLD + '/ketqua', WEB = ROOT + '/web/src/main';
const data = JSON.parse(fs.readFileSync(__dirname + '/dulieu.json', 'utf8'));
const OUT = ROOT + '/docs/BaoCao_Oracle_QuanLySuCo.docx';

const tidy = (l) => l.replace(/ {3,}/g, '  ').replace(/-{41,}/g, '-'.repeat(40));
const tidyAll = (a) => a.map(tidy);
const dropHelp = (a) => a.filter(l => !/^Help:/.test(l));

// ================= mo ta cot / khoa ngoai =================
const MOTA = {
  'NGUOI_DUNG.MA_ND': 'Mã người dùng, tự tăng', 'NGUOI_DUNG.HO_TEN': 'Họ và tên', 'NGUOI_DUNG.EMAIL': 'Email đăng nhập, không trùng',
  'NGUOI_DUNG.MAT_KHAU': 'Mật khẩu (plain text, chỉ dùng demo)', 'NGUOI_DUNG.DON_VI': 'Đơn vị công tác hoặc học tập',
  'NGUOI_DUNG.VAI_TRO': 'Vai trò: USER hoặc ADMIN', 'NGUOI_DUNG.NGAY_TAO': 'Ngày tạo tài khoản',
  'PHONG.MA_PHONG': 'Mã phòng, tự tăng', 'PHONG.TEN_PHONG': 'Tên phòng', 'PHONG.TOA_NHA': 'Tòa nhà (A1, A2, A6, H1...)',
  'PHONG.TANG': 'Tầng', 'PHONG.LOAI_PHONG': 'PHONG_MAY, PHONG_HOC hoặc VAN_PHONG',
  'THIET_BI.MA_TB': 'Mã thiết bị, tự tăng', 'THIET_BI.TEN_TB': 'Tên thiết bị', 'THIET_BI.LOAI_TB': 'MAY_TINH, MAY_CHIEU, MAY_IN, THIET_BI_MANG, KHAC',
  'THIET_BI.MA_PHONG': 'Phòng đặt thiết bị', 'THIET_BI.TINH_TRANG': 'HOAT_DONG, HONG hoặc DANG_SUA',
  'DANH_MUC_SU_CO.MA_DM': 'Mã danh mục, tự tăng', 'DANH_MUC_SU_CO.TEN_DM': 'Tên danh mục, không trùng', 'DANH_MUC_SU_CO.MO_TA': 'Mô tả danh mục',
  'BAI_KINH_NGHIEM.MA_BAI': 'Mã bài, tự tăng', 'BAI_KINH_NGHIEM.TIEU_DE': 'Tiêu đề bài', 'BAI_KINH_NGHIEM.MA_DM': 'Danh mục của bài',
  'BAI_KINH_NGHIEM.TRIEU_CHUNG': 'Triệu chứng của sự cố', 'BAI_KINH_NGHIEM.NGUYEN_NHAN': 'Nguyên nhân thường gặp',
  'BAI_KINH_NGHIEM.GIAI_PHAP': 'Cách khắc phục', 'BAI_KINH_NGHIEM.MA_NGUOI_VIET': 'Admin viết bài', 'BAI_KINH_NGHIEM.NGAY_TAO': 'Ngày viết bài',
  'SU_CO.MA_SC': 'Mã sự cố, tự tăng', 'SU_CO.TIEU_DE': 'Tiêu đề sự cố', 'SU_CO.MO_TA': 'Mô tả chi tiết',
  'SU_CO.MA_NGUOI_BAO': 'Người báo sự cố', 'SU_CO.MA_ADMIN_XU_LY': 'Admin xử lý (NULL khi chưa ai nhận)', 'SU_CO.MA_TB': 'Thiết bị gặp sự cố (có thể NULL)',
  'SU_CO.MA_DM': 'Danh mục sự cố', 'SU_CO.MUC_UU_TIEN': 'THAP, TRUNG_BINH hoặc CAO', 'SU_CO.TRANG_THAI': 'MOI, DANG_XU_LY hoặc HOAN_THANH',
  'SU_CO.NGUYEN_NHAN': 'Nguyên nhân do admin ghi', 'SU_CO.CACH_KHAC_PHUC': 'Cách khắc phục do admin ghi', 'SU_CO.MA_BAI_KN': 'Bài kinh nghiệm liên quan',
  'SU_CO.NGAY_TAO': 'Thời điểm báo sự cố', 'SU_CO.NGAY_HOAN_THANH': 'Thời điểm hoàn thành (trigger tự gán)',
  'LICH_SU_XU_LY.MA_LS': 'Mã dòng lịch sử, tự tăng', 'LICH_SU_XU_LY.MA_SC': 'Sự cố được ghi lịch sử', 'LICH_SU_XU_LY.MA_NGUOI_CAP_NHAT': 'Người đổi trạng thái',
  'LICH_SU_XU_LY.TRANG_THAI_CU': 'Trạng thái trước khi đổi', 'LICH_SU_XU_LY.TRANG_THAI_MOI': 'Trạng thái sau khi đổi', 'LICH_SU_XU_LY.GHI_CHU': 'Ghi chú', 'LICH_SU_XU_LY.THOI_GIAN': 'Thời điểm ghi',
};
const YNGHIA_FK = {
  'THIET_BI.MA_PHONG': 'Mỗi thiết bị đặt trong một phòng', 'BAI_KINH_NGHIEM.MA_DM': 'Mỗi bài kinh nghiệm thuộc một danh mục',
  'BAI_KINH_NGHIEM.MA_NGUOI_VIET': 'Mỗi bài do một admin viết', 'SU_CO.MA_NGUOI_BAO': 'Mỗi sự cố do một người dùng báo',
  'SU_CO.MA_ADMIN_XU_LY': 'Admin nhận xử lý sự cố', 'SU_CO.MA_TB': 'Thiết bị gặp sự cố', 'SU_CO.MA_DM': 'Danh mục phân loại sự cố',
  'SU_CO.MA_BAI_KN': 'Bài kinh nghiệm tham chiếu khi xử lý', 'LICH_SU_XU_LY.MA_SC': 'Lịch sử đổi trạng thái của một sự cố',
  'LICH_SU_XU_LY.MA_NGUOI_CAP_NHAT': 'Người thực hiện việc đổi trạng thái',
};
const tabByName = Object.fromEntries(data.bang.map(b => [b.ten, b]));
const loaiRB = { P: 'PRIMARY KEY', R: 'FOREIGN KEY', U: 'UNIQUE', C: 'CHECK' };

// ================= TRANG BIA =================
const c = (t, o = {}) => new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: o.before || 0, after: o.after ?? 120, line: 340 },
  children: [new TextRun({ text: t, font: FONT, size: o.size || 28, bold: o.bold, italics: o.italics })] });
const cover = [
  c('TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI', { bold: true, size: 28, after: 40 }),
  c('KHOA CÔNG NGHỆ THÔNG TIN', { bold: true, size: 28, after: 900 }),
  c('BÁO CÁO BÀI TẬP LỚN', { bold: true, size: 40, after: 160 }),
  c('HỌC PHẦN: CÔNG NGHỆ ORACLE', { bold: true, size: 30, after: 700 }),
  c('ĐỀ TÀI', { bold: true, size: 28, after: 80 }),
  c('XÂY DỰNG CƠ SỞ DỮ LIỆU HỆ THỐNG QUẢN LÝ SỰ CỐ CNTT VÀ KHO KINH NGHIỆM XỬ LÝ TẠI TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI',
    { bold: true, size: 32, after: 100 }),
  c('(Đề số 30 - Đề mở)', { italics: true, size: 26, after: 1000 }),
  c('Giảng viên hướng dẫn: [Điền họ tên giảng viên]', { size: 26, after: 80 }),
  c('Lớp: [Điền tên lớp]', { size: 26, after: 80 }),
  c('Nhóm sinh viên thực hiện:', { size: 26, after: 60 }),
  c('1. [Họ tên] - MSV: [.........]', { size: 26, after: 40 }),
  c('2. [Họ tên] - MSV: [.........]', { size: 26, after: 40 }),
  c('3. [Họ tên] - MSV: [.........]', { size: 26, after: 900 }),
  c('Hà Nội, năm 2026', { italics: true, size: 26, after: 0 }),
];

// ================= MUC LUC =================
const tocPart = [
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 240 }, children: [new TextRun({ text: 'MỤC LỤC', bold: true, font: FONT, size: 32 })] }),
  new TableOfContents('Mục lục', { hyperlink: true, headingStyleRange: '1-3' }),
];

// ================= CHUONG 1 =================
const ch1 = [
  H1('CHƯƠNG 1. GIỚI THIỆU ĐỀ TÀI'),
  H2('1.1. Bối cảnh và lý do chọn đề tài'),
  P('Trường Đại học Giao thông Vận tải có nhiều tòa nhà (A1, A2, A3, A6, H1...) với hàng trăm thiết bị công nghệ thông tin phục vụ giảng dạy và làm việc: máy tính ở các phòng máy, máy chiếu ở phòng học và giảng đường, máy in ở phòng ban, thiết bị mạng và điểm truy cập WiFi. Khi một thiết bị hỏng hoặc một dịch vụ gặp lỗi (mất mạng, quên mật khẩu email, phần mềm báo lỗi bản quyền), người dùng thường báo bằng điện thoại hoặc trực tiếp, thông tin dễ bị thất lạc, khó theo dõi tiến độ và không lưu lại cách đã xử lý để lần sau tra cứu.'),
  P('Đề tài xây dựng một cơ sở dữ liệu Oracle quản lý sự cố CNTT và kho kinh nghiệm xử lý. Người dùng (sinh viên, giảng viên, cán bộ) báo sự cố xảy ra ở phòng nào, thiết bị nào. Bộ phận IT (admin) cập nhật trạng thái, ghi nguyên nhân và cách khắc phục. Những giải pháp tiêu biểu được tổng hợp thành bài kinh nghiệm theo danh mục để tra cứu lại, giúp rút ngắn thời gian xử lý các sự cố lặp lại. Đây cũng là đề tài phù hợp với học phần Công nghệ Oracle vì có đủ các đối tượng cơ sở dữ liệu cần thực hành: bảng và ràng buộc, khóa ngoại, trigger, view, truy vấn thống kê, phân quyền và quản trị hệ thống.'),
  H2('1.2. Mục tiêu và phạm vi'),
  P('Báo cáo trình bày quá trình xây dựng và kiểm chứng hệ thống theo năm nhóm yêu cầu của đề thi:'),
  bullet('**Thiết kế và xây dựng cơ sở dữ liệu:** 7 bảng đặt trong một user mới `SUCO_APP` và một tablespace mới `TS_SUCO`, có khóa chính, khóa ngoại, ràng buộc CHECK/UNIQUE, trigger, view và dữ liệu mẫu tiếng Việt.'),
  bullet('**Thiết kế truy vấn SQL:** 15 truy vấn gồm truy vấn cơ bản, JOIN, truy vấn lồng, nhóm dữ liệu và các hàm thống kê.'),
  bullet('**Quản trị Oracle:** quản lý instance, tablespace và datafile, khôi phục datafile bị mất bằng RMAN, quản trị người dùng, profile, phân quyền, role, xuất và nhập một schema bằng Data Pump.'),
  bullet('**Ứng dụng web Java** kết nối Oracle: đăng nhập, báo sự cố, xử lý sự cố, kho kinh nghiệm, quản lý thiết bị và người dùng.'),
  P('Phạm vi của hệ thống giới hạn ở quản lý sự cố và kho kinh nghiệm; không bao gồm quản lý tài sản, kho vật tư hay hợp đồng bảo trì. Mọi thao tác quản trị được chạy thật trên máy chủ Oracle và kết quả được ghi lại làm bằng chứng.'),
  H2('1.3. Môi trường và công cụ'),
  tblCaption('Môi trường và công cụ sử dụng'),
  table(['Thành phần', 'Phiên bản / giá trị'], [
    ['Hệ quản trị CSDL', 'Oracle AI Database 26ai Free, Release 23.26.3.0.0'],
    ['Cơ sở dữ liệu', 'Kiến trúc CDB/PDB; PDB mặc định FREEPDB1, cổng 1521; instance FREE'],
    ['Hệ điều hành', 'Windows 10 Enterprise LTSC 2021'],
    ['Ngôn ngữ, JDK', 'Java, JDK 26.0.2.1 (biên dịch ở mức release 21)'],
    ['Framework web', 'Spring Boot 4.1.1 (Spring MVC, Thymeleaf, JdbcTemplate)'],
    ['JDBC driver', 'com.oracle.database.jdbc:ojdbc17:23.26.3.0.0'],
    ['Công cụ build', 'Apache Maven 3.9.16'],
    ['Giao diện web', 'Bootstrap 5.3.3 (CDN)'],
    ['Công cụ Oracle', 'SQL*Plus, RMAN, Data Pump (expdp, impdp)'],
  ], [2900, 6171]),
  gap(),
  H2('1.4. Mô tả các thực thể'),
  P('Hệ thống gồm 7 thực thể. Tên bảng và tên cột viết hoa, tiếng Việt không dấu, nối bằng dấu gạch dưới. Khóa chính của mọi bảng là cột số tự tăng (`GENERATED BY DEFAULT AS IDENTITY`).'),
  tblCaption('Các thực thể của hệ thống'),
  table(['Thực thể (bảng)', 'Mô tả', 'Số cột', 'Số bản ghi mẫu'], [
    ['NGUOI_DUNG', 'Sinh viên, giảng viên, cán bộ và quản trị viên (admin); có vai trò USER hoặc ADMIN', tabByName.NGUOI_DUNG.cot.length, tabByName.NGUOI_DUNG.soDong],
    ['PHONG', 'Phòng máy, phòng học, văn phòng, theo tòa nhà và tầng', tabByName.PHONG.cot.length, tabByName.PHONG.soDong],
    ['THIET_BI', 'Thiết bị CNTT đặt trong phòng (máy tính, máy chiếu, máy in, thiết bị mạng)', tabByName.THIET_BI.cot.length, tabByName.THIET_BI.soDong],
    ['DANH_MUC_SU_CO', 'Danh mục phân loại sự cố và bài kinh nghiệm (Máy tính, Máy chiếu, Mạng...)', tabByName.DANH_MUC_SU_CO.cot.length, tabByName.DANH_MUC_SU_CO.soDong],
    ['BAI_KINH_NGHIEM', 'Bài kinh nghiệm xử lý: triệu chứng, nguyên nhân, giải pháp', tabByName.BAI_KINH_NGHIEM.cot.length, tabByName.BAI_KINH_NGHIEM.soDong],
    ['SU_CO', 'Sự cố do người dùng báo; admin cập nhật trạng thái, nguyên nhân, cách khắc phục', tabByName.SU_CO.cot.length, tabByName.SU_CO.soDong],
    ['LICH_SU_XU_LY', 'Nhật ký mỗi lần sự cố đổi trạng thái (do trigger ghi)', tabByName.LICH_SU_XU_LY.cot.length, tabByName.LICH_SU_XU_LY.soDong],
  ], [2100, 4471, 1000, 1500], { centerCols: [2, 3], monoCols: [0] }),
  gap(),
  H2('1.5. Mối quan hệ giữa các thực thể'),
  P('Các bảng liên kết với nhau bằng 10 khóa ngoại. Bảng `SU_CO` là trung tâm: một sự cố do một người dùng báo, có thể được một admin xử lý, có thể gắn với một thiết bị, thuộc một danh mục và có thể tham chiếu một bài kinh nghiệm. Mỗi lần trạng thái sự cố thay đổi sẽ có thêm một dòng trong `LICH_SU_XU_LY`. Cột khóa ngoại cho phép NULL thì quan hệ ở phía bảng cha là tùy chọn (không hoặc một).'),
  tblCaption('Các khóa ngoại (quan hệ một - nhiều, đọc từ từ điển dữ liệu Oracle)'),
  table(['Bảng con', 'Cột khóa ngoại', 'Bảng cha', 'Bắt buộc', 'Ý nghĩa'],
    data.rangBuoc.filter(x => x.loai === 'R').map(x => {
      const col = tabByName[x.bang].cot.find(cc => cc.ten === x.cot);
      return [x.bang, x.cot, x.cha, col.notNull ? 'Có' : 'Không (NULL)', YNGHIA_FK[x.bang + '.' + x.cot] || ''];
    }), [1750, 1900, 1750, 1071, 2600], { size: 20, monoCols: [0, 1, 2], centerCols: [3] }),
  gap(),
];

// ================= HINH ER (trang ngang) =================
// Trang ngang chi chua hinh + chu thich de hinh duoc phong to toi da
const ERH = 640, ERW = Math.round(ERH * 2780 / 2414);
ch1.push(
  H2('1.6. Sơ đồ quan hệ (ER)'),
  P('Sơ đồ quan hệ được trình bày ở trang ngang kế tiếp (Hình 1), vẽ tự động từ từ điển dữ liệu Oracle bằng `tools/VeSoDoER.java` nên khớp hoàn toàn với cơ sở dữ liệu thật; ký hiệu được chú thích ngay dưới sơ đồ.'),
);
const erSection = [
  ...figure(ROOT + '/docs/so_do_ER_SUCO_APP.png', ERW, ERH, 'Sơ đồ quan hệ của schema SUCO_APP (đọc từ từ điển dữ liệu Oracle)'),
];

// ================= CHUONG 2 =================
const consCau = extract(SQLD + '/02_tables.sql', /^CREATE TABLE SU_CO/, l => /^\);/.test(l));
const trg1 = extract(SQLD + '/03_triggers.sql', /^CREATE OR REPLACE TRIGGER TRG_SU_CO_LICH_SU/, l => l.trim() === '/');
const trg2 = extract(SQLD + '/03_triggers.sql', /^CREATE OR REPLACE TRIGGER TRG_SU_CO_HOAN_THANH/, l => l.trim() === '/');
const viewSql = extract(SQLD + '/05_queries.sql', /^CREATE OR REPLACE VIEW/, l => /;\s*$/.test(l));

const bangSections = [];
data.bang.forEach((b, i) => {
  bangSections.push(H3(`2.2.${i + 1}. Bảng ${b.ten}`));
  bangSections.push(tblCaption(`Cấu trúc bảng ${b.ten}`));
  bangSections.push(table(['Tên cột', 'Kiểu dữ liệu', 'Khóa', 'NOT NULL', 'Ý nghĩa'],
    b.cot.map(cc => [cc.ten, cc.kieu, cc.khoa || '', cc.notNull ? 'Có' : '', MOTA[b.ten + '.' + cc.ten] || '']),
    [2200, 1600, 800, 900, 3571], { size: 20, monoCols: [0, 1], centerCols: [2, 3] }));
  bangSections.push(gap());
});

const ch2 = [
  H1('CHƯƠNG 2. XÂY DỰNG CƠ SỞ DỮ LIỆU'),
  H2('2.1. Tạo tablespace và user riêng cho đề tài'),
  P('Theo yêu cầu của đề, các bảng của đề tài phải nằm trong một user mới và dữ liệu lưu trong một tablespace mới, không dùng tablespace có sẵn. Script `01_tablespace_user.sql` chạy bằng user SYSTEM, kết nối vào PDB `FREEPDB1`, thực hiện các bước sau:'),
  bullet('Tạo tablespace `TS_SUCO` gồm một datafile 100 MB, tự động mở rộng mỗi lần 10 MB, tối đa 500 MB. Thư mục datafile được lấy từ datafile của tablespace SYSTEM trong PDB để script chạy được trên cả Windows và Linux.'),
  bullet('Tạo user `SUCO_APP` với tablespace mặc định `TS_SUCO` và hạn mức không giới hạn trên tablespace này.'),
  bullet('Cấp các quyền hệ thống tối thiểu: CREATE SESSION, TABLE, SEQUENCE, TRIGGER, VIEW, PROCEDURE.'),
  bullet('Tạo hai role ứng dụng `ROLE_APP_USER` và `ROLE_APP_ADMIN` (quyền trên bảng được cấp ở script 06).'),
  ...code([
    "CREATE TABLESPACE TS_SUCO DATAFILE '<thư_mục_PDB>\\ts_suco01.dbf'",
    "  SIZE 100M AUTOEXTEND ON NEXT 10M MAXSIZE 500M;",
    "",
    "CREATE USER SUCO_APP IDENTIFIED BY \"SuCo_App_123\"",
    "  DEFAULT TABLESPACE TS_SUCO TEMPORARY TABLESPACE TEMP",
    "  QUOTA UNLIMITED ON TS_SUCO;",
    "",
    "GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE,",
    "      CREATE TRIGGER, CREATE VIEW, CREATE PROCEDURE TO SUCO_APP;",
    "CREATE ROLE ROLE_APP_USER;",
    "CREATE ROLE ROLE_APP_ADMIN;",
  ]),
  P('Kết quả kiểm tra sau khi chạy: user `SUCO_APP` có tablespace mặc định `TS_SUCO`, tablespace ở trạng thái ONLINE và toàn bộ 7 bảng nằm trong `TS_SUCO` (xem bảng kiểm tra ở mục 4.5).'),
  H2('2.2. Thiết kế và tạo bảng'),
  P('Script `02_tables.sql` chạy bằng user `SUCO_APP`. Các bảng được tạo theo thứ tự bảng cha trước, bảng con sau để khóa ngoại luôn tham chiếu tới bảng đã tồn tại: NGUOI_DUNG, PHONG, THIET_BI, DANH_MUC_SU_CO, BAI_KINH_NGHIEM, SU_CO, LICH_SU_XU_LY. Các cột chuỗi dùng `VARCHAR2(n CHAR)` để độ dài tính theo ký tự, tránh lỗi tràn khi lưu tiếng Việt có dấu (mỗi ký tự có thể chiếm 2 đến 3 byte). Các bảng dưới đây được lấy trực tiếp từ từ điển dữ liệu Oracle.'),
  ...bangSections,
  P('Ví dụ lệnh tạo bảng trung tâm `SU_CO`, có đủ khóa chính, năm khóa ngoại và các ràng buộc CHECK đặt tên rõ ràng:', { keepNext: true }),
  ...code(consCau),
  H2('2.3. Các ràng buộc toàn vẹn'),
  P(`Mọi ràng buộc đều được đặt tên theo quy ước PK_, FK_, UQ_, CK_ để dễ nhận biết khi Oracle báo lỗi. Tổng cộng có ${data.rangBuoc.length} ràng buộc: ${data.rangBuoc.filter(x => x.loai === 'P').length} khóa chính, ${data.rangBuoc.filter(x => x.loai === 'R').length} khóa ngoại, ${data.rangBuoc.filter(x => x.loai === 'U').length} ràng buộc duy nhất và ${data.rangBuoc.filter(x => x.loai === 'C').length} ràng buộc CHECK (không kể các ràng buộc NOT NULL do hệ thống đặt tên tự động).`),
  tblCaption('Danh sách ràng buộc (đọc từ USER_CONSTRAINTS)'),
  table(['Tên ràng buộc', 'Loại', 'Bảng', 'Nội dung'],
    data.rangBuoc.map(x => [x.ten, loaiRB[x.loai], x.bang,
      x.loai === 'C' ? x.dieuKien : x.loai === 'R' ? `${x.cot} -> ${x.cha}` : x.cot]),
    [2500, 1350, 1800, 3421], { size: 18, monoCols: [0, 2, 3] }),
  gap(),
  P('Ràng buộc `CK_SU_CO_NGAY_HT` bảo đảm ngày hoàn thành không thể trước ngày tạo sự cố. Ràng buộc duy nhất `UQ_NGUOI_DUNG_EMAIL` ngăn hai tài khoản trùng email và được dùng khi đăng nhập.'),
  H2('2.4. Trigger'),
  P('Script `03_triggers.sql` tạo hai trigger trên bảng `SU_CO` để nghiệp vụ được thực thi ngay trong cơ sở dữ liệu, không phụ thuộc ứng dụng nào gọi vào.'),
  H3('2.4.1. TRG_SU_CO_LICH_SU'),
  P('Trigger AFTER UPDATE chỉ chạy khi cột `TRANG_THAI` thực sự đổi giá trị (mệnh đề WHEN). Nó ghi một dòng vào `LICH_SU_XU_LY` gồm sự cố, người cập nhật (lấy từ `:NEW.MA_ADMIN_XU_LY`), trạng thái cũ và trạng thái mới.', { keepNext: true }),
  ...code(trg1),
  H3('2.4.2. TRG_SU_CO_HOAN_THANH'),
  P('Trigger BEFORE UPDATE tự gán `NGAY_HOAN_THANH = SYSDATE` khi trạng thái chuyển sang HOAN_THANH. Nếu sự cố được mở lại thì ngày hoàn thành bị xóa để số liệu thống kê không sai. Dùng BEFORE vì cần sửa giá trị `:NEW` trước khi dòng được ghi xuống.', { keepNext: true }),
  ...code(trg2),
  H3('2.4.3. Kiểm thử trigger'),
  P('Kiểm thử được thực hiện thật thông qua ứng dụng web (admin đổi trạng thái một sự cố thử) và trực tiếp bằng SQL:'),
  tblCaption('Kết quả kiểm thử trigger và ràng buộc'),
  table(['Thao tác', 'Kết quả quan sát'], [
    ['Admin đổi trạng thái MOI -> DANG_XU_LY', 'LICH_SU_XU_LY có thêm 1 dòng (MOI -> DANG_XU_LY), MA_NGUOI_CAP_NHAT = mã admin'],
    ['Admin đổi trạng thái DANG_XU_LY -> HOAN_THANH', 'Có thêm dòng thứ hai; NGAY_HOAN_THANH được tự gán bằng thời điểm hiện tại'],
    ['Mở lại sự cố (UPDATE TRANG_THAI = \'MOI\')', 'NGAY_HOAN_THANH bị xóa (NULL)'],
    ['UPDATE NGAY_HOAN_THANH = NGAY_TAO - 1', 'ORA-02290: check constraint (SUCO_APP.CK_SU_CO_NGAY_HT) violated'],
    ['UPDATE TRANG_THAI = \'XYZ\'', 'ORA-02290: check constraint (SUCO_APP.CK_SU_CO_TRANG_THAI) violated'],
  ], [3600, 5471], { size: 21 }),
  gap(),
  H2('2.5. Nhập dữ liệu mẫu'),
  P('Script `04_data.sql` nhập dữ liệu tiếng Việt có dấu, nhất quán với khóa ngoại và không vi phạm ràng buộc CHECK. Dữ liệu dùng tên người Việt, các tòa nhà A1, A2, A3, A6, H1 và các phòng máy, giảng đường thực tế. Sự cố được rải từ tháng 4 đến tháng 9 năm 2026, đủ ba trạng thái, nhiều danh mục và nhiều mức ưu tiên để các truy vấn thống kê có ý nghĩa. Mỗi bảng có ít nhất 8 bản ghi (đề thi yêu cầu tối thiểu 5).'),
  tblCaption('Số bản ghi mẫu của từng bảng (đếm trực tiếp từ CSDL)'),
  table(['Bảng', 'Số bản ghi'], [...data.bang.map(b => [b.ten, String(b.soDong)]), ['Tổng', String(data.bang.reduce((s, b) => s + b.soDong, 0))]],
    [4500, 2000], { monoCols: [0], centerCols: [1] }),
  gap(),
  P('Các khóa chính dùng cột IDENTITY nên câu lệnh INSERT không ghi cột mã; Oracle tự sinh 1, 2, 3... theo thứ tự chèn, và các khóa ngoại trong dữ liệu mẫu tham chiếu theo thứ tự đó. Ví dụ:', { keepNext: true }),
  ...code([
    "INSERT INTO NGUOI_DUNG (HO_TEN, EMAIL, MAT_KHAU, DON_VI, VAI_TRO) VALUES",
    "  ('Nguyễn Văn Hùng', 'hung.nv@utc.edu.vn', 'admin123', 'Trung tâm Công nghệ thông tin', 'ADMIN');",
    "",
    "INSERT INTO SU_CO VALUES (DEFAULT, 'Máy chiếu H1-305 không nhận HDMI',",
    "  'Giảng viên cắm laptop nhưng máy chiếu báo No Signal.', 7, 2, 8, 2, 'CAO', 'HOAN_THANH',",
    "  'Cáp HDMI âm tường bị lỏng đầu nối.', 'Cắm lại đầu nối và thay cáp HDMI mới.', 3,",
    "  TIMESTAMP '2026-05-08 08:30:00', TIMESTAMP '2026-05-08 11:00:00');",
  ]),
  P('Mật khẩu người dùng lưu dạng văn bản thường chỉ nhằm mục đích demo bài tập; trong hệ thống thật cần băm mật khẩu bằng thuật toán một chiều có muối.'),
  H2('2.6. View V_SU_CO_CHI_TIET'),
  P('View ghép `SU_CO` với người báo, admin xử lý, thiết bị, phòng và danh mục để ứng dụng web không phải JOIN lại nhiều lần. Admin và thiết bị dùng LEFT JOIN vì hai cột này có thể NULL.', { keepNext: true }),
  ...code(viewSql),
];

// ================= CHUONG 3 =================
const KYTHUAT = ['WHERE, AND, ORDER BY', 'LIKE, LOWER', 'JOIN 3 bảng', 'JOIN nhiều bảng qua view', 'Truy vấn lồng với IN',
  'Truy vấn lồng với EXISTS', 'NOT EXISTS', 'Subquery trong WHERE, hàm AVG', 'Subquery trong FROM, FETCH FIRST', 'GROUP BY, HAVING',
  'TO_CHAR, GROUP BY', 'COUNT, AVG, MAX, MIN, SUM', 'GROUP BY, FETCH FIRST WITH TIES', 'LEFT JOIN, COUNT', 'GROUP BY nhiều cột'];
const ch3 = [
  H1('CHƯƠNG 3. THIẾT KẾ TRUY VẤN SQL'),
  P(`Script \`05_queries.sql\` gồm ${data.truyVan.length} truy vấn (đề thi yêu cầu tối thiểu 10) chạy bằng user \`SUCO_APP\`. Các truy vấn bao quát: truy vấn cơ bản có điều kiện, JOIN nhiều bảng, truy vấn lồng (IN, EXISTS, subquery trong WHERE và trong FROM), nhóm dữ liệu với GROUP BY và HAVING, các hàm thống kê COUNT, AVG, MAX, MIN, SUM, tính thời gian xử lý trung bình, top thiết bị và phòng có nhiều sự cố, thống kê theo tháng, admin xử lý nhiều nhất, sự cố mức CAO chưa hoàn thành, danh mục chưa có bài kinh nghiệm. Kết quả dưới đây được chạy thật trên cơ sở dữ liệu mẫu.`),
  tblCaption('Kỹ thuật SQL được minh họa ở từng câu'),
  table(['Câu', 'Kỹ thuật'], KYTHUAT.map((k, i) => [String(i + 1), k]), [900, 8171], { size: 21, centerCols: [0] }),
  gap(),
];
data.truyVan.forEach((q, i) => {
  const text = q.moTa.replace(/^Q\d+\.\s*/, '');
  const idx = text.indexOf(':');
  const head = idx > 0 ? text.slice(0, idx) : text;
  const rest = idx > 0 ? text.slice(idx + 1).trim() : null;
  ch3.push(H2(`3.${i + 1}. Câu ${i + 1}: ${head}`));
  if (rest) ch3.push(P(`Mục đích: ${rest.charAt(0).toUpperCase() + rest.slice(1)}${/[.;]$/.test(rest) ? '' : '.'}`, { keepNext: true }));
  ch3.push(...code(q.sql));
  ch3.push(tblCaption(`Kết quả câu ${i + 1} (${q.dong.length} dòng)`));
  ch3.push(resultTable(q.cot, q.dong));
  ch3.push(gap());
});

// ================= CHUONG 4 =================
const s01 = logSections(KQ + '/01_instance.log');
const s02 = logSections(KQ + '/02_restricted_session.log');
const s03 = logSections(KQ + '/03_tablespace_datafile.log');
const sec = (S, k, max = 40) => tidyAll(dropHelp(S[k] || []).filter(l => !/^SQL> (HOST|PROMPT|SELECT)/.test(l) || /SELECT/.test(l) && false)).slice(0, max);
const secKeep = (S, k, max = 40) => tidyAll(dropHelp(S[k] || [])).filter(l => !/^SQL> (HOST|PROMPT)/.test(l) && !/^\d+ rows? selected\./.test(l)).slice(0, max);

const lockLog = (() => {
  const A = cleanLog(KQ + '/05_quan_tri_user.log');
  const i = A.findIndex(l => /CONNECT U_DEMO\/sai_mat_khau_1/.test(l));
  const j = A.findIndex(l => /DANG_NHAP_BANG/.test(l));
  return A.slice(i, j + 4).filter(l => !/^SQL> --/.test(l)).map(tidy);
})();
const expLog = readLines(KQ + '/04b_expdp.log').map(l => l.trim()).filter(l => /exported|Dump file set|ORADATA|successfully completed|Connected to|Starting/.test(l));
const impLog = readLines(KQ + '/04c_impdp.log').map(l => l.trim()).filter(l => /imported|successfully completed|Connected to|Starting|error/.test(l));

const ch4 = [
  H1('CHƯƠNG 4. QUẢN TRỊ CƠ SỞ DỮ LIỆU ORACLE'),
  P('Toàn bộ nội dung chương này được thực hiện thật trên máy chủ Oracle 26ai Free và kịch bản đầy đủ nằm trong `07_admin_demo.sql` và `08_quan_tri_user.sql`. Các trích đoạn kết quả bên dưới lấy từ các tệp nhật ký trong thư mục `sql/ketqua/`. Do Oracle 26ai dùng kiến trúc đa tenant nên cần phân biệt hai mức: **CDB$ROOT** (toàn instance) và **PDB FREEPDB1** (cơ sở dữ liệu chứa dữ liệu đề tài); mỗi khối lệnh đều ghi rõ chạy ở mức nào và bằng user nào (SYSDBA xác thực hệ điều hành).'),
  // ---- 4.1
  H2('4.1. Quản lý Instance'),
  P('Instance Oracle đi qua các trạng thái khởi động NOMOUNT, MOUNT rồi OPEN. Bảng dưới tóm tắt từng chế độ đã minh họa (chạy bằng SYSDBA ở CDB$ROOT):'),
  tblCaption('Các chế độ khởi động và tắt cơ sở dữ liệu'),
  table(['Lệnh', 'Trạng thái (V$INSTANCE / V$DATABASE)', 'Ý nghĩa'], [
    ['SHUTDOWN IMMEDIATE', 'Database closed, dismounted, instance shut down', 'Tắt ngay, hoàn tác các giao dịch dang dở'],
    ['STARTUP NOMOUNT', 'STARTED', 'Chỉ khởi động instance (SGA, tiến trình nền), chưa đọc control file'],
    ['ALTER DATABASE MOUNT', 'MOUNTED', 'Đọc control file; dùng cho sao lưu, khôi phục, bật ARCHIVELOG'],
    ['ALTER DATABASE OPEN READ ONLY', 'READ ONLY', 'Mở chỉ đọc, không ghi được dữ liệu'],
    ['STARTUP (sau SHUTDOWN)', 'OPEN, READ WRITE', 'Khởi động bình thường qua NOMOUNT, MOUNT, OPEN'],
    ['ALTER PLUGGABLE DATABASE ALL OPEN', 'PDB: READ WRITE', 'Mở các PDB (PDB không tự mở khi instance khởi động lại)'],
  ], [2700, 3000, 3371], { size: 20, monoCols: [0] }),
  gap(),
  P('Nhật ký thực tế khi chuyển qua các trạng thái:', { keepNext: true }),
  ...code([...secKeep(s01, '3', 12), '', ...secKeep(s01, '4', 10), '', ...secKeep(s01, '5', 12)]),
  H3('4.1.1. Đóng, mở PDB ở chế độ READ ONLY và READ WRITE'),
  P('Ở mức PDB, có thể đóng và mở riêng FREEPDB1. Khi PDB mở READ ONLY, câu lệnh ghi dữ liệu bị Oracle từ chối với lỗi ORA-16000 nhưng vẫn đọc được toàn bộ 15 sự cố:', { keepNext: true }),
  ...code([...secKeep(s01, '7', 30).filter(l => !/^\s*$/.test(l))]),
  P('Sau đó đóng PDB và mở lại READ WRITE để trở lại hoạt động bình thường:', { keepNext: true }),
  ...code(secKeep(s01, '8', 14)),
  H3('4.1.2. RESTRICTED SESSION'),
  P('Chế độ RESTRICTED SESSION chỉ cho phép người dùng có quyền RESTRICTED SESSION (như DBA) đăng nhập, dùng khi bảo trì. Sau khi bật trong FREEPDB1, user thường `SUCO_APP` bị từ chối, còn SYSDBA vẫn thao tác được; tắt chế độ này thì mọi người đăng nhập lại bình thường:', { keepNext: true }),
  ...code([...secKeep(s02, '1', 14).filter(l => !/ALTER SESSION/.test(l)), '', 'Đăng nhập bằng SUCO_APP qua JDBC khi đang RESTRICTED:',
    'java.sql.SQLException: ORA-01035: Login denied. Database is in RESTRICTED mode.', '', ...secKeep(s02, '3', 10)]),
  // ---- 4.2
  H2('4.2. Quản lý Tablespace và Datafile'),
  P('Các thao tác thực hiện trên tablespace demo `TS_DEMO` (không đụng tới `TS_SUCO` hay tablespace hệ thống), datafile đặt ở `C:\\ORADATA\\`. Chạy bằng SYSDBA trong PDB FREEPDB1.'),
  tblCaption('Các thao tác trên tablespace và datafile'),
  table(['Thao tác', 'Lệnh chính', 'Kết quả'], [
    ['Thêm tablespace', 'CREATE SMALLFILE TABLESPACE TS_DEMO DATAFILE \'C:\\ORADATA\\ts_demo01.dbf\' SIZE 20M AUTOEXTEND ON NEXT 5M MAXSIZE 100M', 'Tablespace created'],
    ['Thêm datafile', 'ALTER TABLESPACE TS_DEMO ADD DATAFILE \'...ts_demo02.dbf\' SIZE 10M', 'Đạt (chỉ với SMALLFILE)'],
    ['Đổi kích thước datafile', 'ALTER DATABASE DATAFILE \'...ts_demo01.dbf\' RESIZE 30M', 'Datafile 20 MB -> 30 MB'],
    ['Bật/tắt tự mở rộng', 'ALTER DATABASE DATAFILE \'...ts_demo02.dbf\' AUTOEXTEND ON NEXT 5M MAXSIZE 100M / AUTOEXTEND OFF', 'autoextensible YES -> NO'],
    ['Đổi tên tablespace', 'ALTER TABLESPACE TS_DEMO RENAME TO TS_DEMO_MOI', 'Tablespace altered'],
    ['Di chuyển datafile', 'OFFLINE, copy file, ALTER TABLESPACE ... RENAME DATAFILE, ONLINE', 'Datafile chuyển sang thư mục demo_move; dữ liệu còn nguyên'],
    ['Offline/Online', 'ALTER TABLESPACE TS_DEMO_MOI OFFLINE NORMAL / ONLINE', 'Offline: truy vấn báo ORA-00376; Online: bình thường'],
    ['Read only/Read write', 'ALTER TABLESPACE TS_DEMO_MOI READ ONLY / READ WRITE', 'Ghi khi READ ONLY báo ORA-00372'],
    ['Xóa tablespace', 'DROP TABLESPACE TS_DEMO_MOI INCLUDING CONTENTS AND DATAFILES', 'Không còn tablespace và file trên đĩa'],
  ], [1900, 4571, 2600], { size: 19, centerCols: [] }),
  gap(),
  H3('4.2.1. Truy vấn thông tin tablespace và datafile'),
  P('Các khung nhìn từ điển dữ liệu dùng để tra cứu: `DBA_TABLESPACES` (trạng thái, loại quản lý extent), `DBA_DATA_FILES` (tên file, kích thước, autoextend, kích thước tối đa), `DBA_FREE_SPACE` (dung lượng còn trống), `DBA_SEGMENTS` (đối tượng trong tablespace) và `V$DATAFILE` (trạng thái file).', { keepNext: true }),
  ...code(tidyAll(secKeep(s03, '11', 22))),
  H3('4.2.2. Hai hạn chế của Oracle Free được phát hiện khi thực hành'),
  bullet('**Tablespace mặc định là BIGFILE:** lệnh `ADD DATAFILE` báo `ORA-32771: cannot add file to bigfile tablespace`. Khắc phục bằng cách tạo tablespace với từ khóa `SMALLFILE`.'),
  bullet('**Không di chuyển được datafile online:** lệnh `ALTER DATABASE MOVE DATAFILE` báo `ORA-00439: feature not enabled: online move datafile` vì bản Free không có tính năng này. Khắc phục bằng cách di chuyển theo cách offline (bên dưới); cách này dùng được ở mọi phiên bản.'),
  ...code(tidyAll([...secKeep(s03, '8a', 6), '', ...secKeep(s03, '8b', 14)])),
  // ---- 4.3
  H2('4.3. Khôi phục datafile bị mất bằng RMAN'),
  H3('4.3.1. Điều kiện cần'),
  P('Khôi phục datafile đã mất cần (a) cơ sở dữ liệu chạy chế độ ARCHIVELOG để có redo lưu trữ, (b) có bản sao lưu từ trước khi mất file và (c) có nơi lưu bản sao lưu. Máy thực hành ban đầu ở chế độ NOARCHIVELOG và chưa có Fast Recovery Area nên đã được cấu hình như sau (SYSDBA, CDB$ROOT):', { keepNext: true }),
  ...code([
    "SQL> ALTER SYSTEM SET db_recovery_file_dest_size=5G SCOPE=BOTH;",
    "SQL> ALTER SYSTEM SET db_recovery_file_dest='C:\\ORADATA\\fra' SCOPE=BOTH;",
    "SQL> SHUTDOWN IMMEDIATE",
    "SQL> STARTUP MOUNT",
    "SQL> ALTER DATABASE ARCHIVELOG;",
    "SQL> ALTER DATABASE OPEN;",
    "SQL> ALTER PLUGGABLE DATABASE ALL OPEN;",
    "",
    "LOG_MODE     OPEN_MODE",
    "ARCHIVELOG   READ WRITE",
    "",
    "Database log mode            Archive Mode",
    "Automatic archival           Enabled",
    "Archive destination          USE_DB_RECOVERY_FILE_DEST",
    "C:\\ORADATA\\FRA\\FREE\\ARCHIVELOG\\2026_09_25\\O1_MF_1_141_OCF9KFQ3_.ARC   (archive log đầu tiên)",
  ]),
  H3('4.3.2. Kịch bản khôi phục'),
  P('Kịch bản dùng tablespace demo riêng `TS_DEMO`, không liên quan tới `TS_SUCO` hay SYSTEM. Bảng thử `DEMO_KHOI_PHUC` được tạo trong tablespace này với dòng dữ liệu thứ nhất trước khi sao lưu và dòng thứ hai sau khi sao lưu, để chứng minh RMAN áp dụng lại được redo.'),
  tblCaption('Các bước khôi phục datafile bị mất'),
  table(['Bước', 'Thực hiện', 'Công cụ'], [
    ['1', 'Tạo TS_DEMO, bảng DEMO_KHOI_PHUC, thêm dòng 1', 'SQL*Plus (SYSDBA, PDB)'],
    ['2', 'Sao lưu tablespace: BACKUP TABLESPACE FREEPDB1:TS_DEMO', 'RMAN (kết nối CDB root)'],
    ['3', 'Thêm dòng 2 sau khi sao lưu; ALTER TABLESPACE TS_DEMO OFFLINE IMMEDIATE', 'SQL*Plus'],
    ['4', 'Xóa file ts_demo01.dbf bằng hệ điều hành (mô phỏng sự cố)', 'Hệ điều hành'],
    ['5', 'RESTORE và RECOVER TABLESPACE FREEPDB1:TS_DEMO', 'RMAN'],
    ['6', 'ALTER TABLESPACE TS_DEMO ONLINE; kiểm tra dữ liệu', 'SQL*Plus'],
  ], [900, 5871, 2300], { size: 21, centerCols: [0] }),
  gap(),
  P('Nhật ký thực tế khi sao lưu:', { keepNext: true }),
  ...code([
    "RMAN> BACKUP TABLESPACE FREEPDB1:TS_DEMO;",
    "Starting backup at 25-SEP-26",
    "input datafile file number=00020 name=C:\\ORADATA\\TS_DEMO01.DBF",
    "channel ORA_DISK_1: starting piece 1 at 25-SEP-26",
    "piece handle=C:\\ORADATA\\FRA\\FREE\\1044...F661\\BACKUPSET\\2026_09_25\\O1_MF_NNNDF_TAG20260925T235018_OCF9LC69_.BKP",
    "Finished backup at 25-SEP-26",
    "",
    "BS Key  Type LV Size       Device Type Elapsed Time Completion Time",
    "1       Full    6.14M      DISK        00:00:00     25-SEP-26",
  ]),
  P('Sau khi xóa file, truy vấn bảng báo lỗi; RMAN khôi phục và áp dụng redo thành công:', { keepNext: true }),
  ...code([
    "SQL> SELECT COUNT(*) FROM SYSTEM.DEMO_KHOI_PHUC;",
    "ORA-00376: file 20 cannot be read at this time",
    "ORA-01110: data file 20: 'C:\\ORADATA\\TS_DEMO01.DBF'",
    "",
    "RMAN> RESTORE TABLESPACE FREEPDB1:TS_DEMO;",
    "channel ORA_DISK_1: restoring datafile 00020 to C:\\ORADATA\\TS_DEMO01.DBF",
    "channel ORA_DISK_1: restored backup piece 1",
    "Finished restore at 25-SEP-26",
    "",
    "RMAN> RECOVER TABLESPACE FREEPDB1:TS_DEMO;",
    "starting media recovery",
    "media recovery complete, elapsed time: 00:00:00",
    "Finished recover at 25-SEP-26",
  ]),
  P('Kết quả kiểm tra sau khi đưa tablespace online: cả hai dòng dữ liệu đều còn, kể cả dòng thêm sau khi sao lưu, chứng tỏ khôi phục hoàn chỉnh:', { keepNext: true }),
  ...code([
    "TABLESPACE_NAME   STATUS",
    "TS_DEMO           ONLINE",
    "",
    "        ID GHI_CHU",
    "         1 truoc khi backup",
    "         2 sau khi backup",
  ]),
  // ---- 4.4
  H2('4.4. Quản trị người dùng, profile và phân quyền'),
  H3('4.4.1. Phân quyền bằng role cho ứng dụng'),
  P('Script `06_users_roles.sql` (chạy bằng SYSTEM) cấp quyền trên các bảng của `SUCO_APP` cho hai role rồi gán role cho hai user demo `U_NHANVIEN` và `U_QUANTRI`. Quyền được cấp cho role thay vì cấp trực tiếp cho từng user để dễ quản lý.'),
  tblCaption('Quyền của hai role trên các đối tượng của SUCO_APP'),
  table(['Đối tượng', 'ROLE_APP_USER', 'ROLE_APP_ADMIN'], [
    ['SU_CO', 'SELECT, INSERT', 'SELECT, INSERT, UPDATE, DELETE'],
    ['DANH_MUC_SU_CO, BAI_KINH_NGHIEM, PHONG, THIET_BI', 'SELECT', 'SELECT, INSERT, UPDATE, DELETE'],
    ['NGUOI_DUNG, LICH_SU_XU_LY', 'Không có quyền', 'SELECT, INSERT, UPDATE, DELETE'],
    ['V_SU_CO_CHI_TIET (view)', 'SELECT', 'SELECT'],
  ], [3900, 2300, 2871], { size: 21 }),
  gap(),
  P('Kiểm thử: `U_NHANVIEN` đọc được `SU_CO` và view nhưng lệnh UPDATE bị từ chối (Oracle 23ai báo ORA-41900 thay cho ORA-01031 của bản cũ) và không thấy bảng `NGUOI_DUNG` (ORA-00942); `U_QUANTRI` đọc được `NGUOI_DUNG`. Có thể thu hồi quyền bằng REVOKE (ví dụ `REVOKE INSERT ON SUCO_APP.SU_CO FROM ROLE_APP_USER`) và cấp lại bằng GRANT.'),
  H3('4.4.2. Profile'),
  P('Profile là tập giới hạn về mật khẩu và tài nguyên gán cho user. Tham số `RESOURCE_LIMIT` của instance đang là TRUE nên các giới hạn phiên có hiệu lực. Hai profile được tạo:'),
  tblCaption('Các giới hạn của hai profile'),
  table(['Tham số', 'PROFILE_NHANVIEN', 'PROFILE_QUANTRI', 'Ý nghĩa'], [
    ['FAILED_LOGIN_ATTEMPTS', '3', '5', 'Số lần đăng nhập sai trước khi khóa tài khoản'],
    ['PASSWORD_LOCK_TIME', '1/24 (1 giờ)', '1/24 (1 giờ)', 'Thời gian khóa tự động'],
    ['PASSWORD_LIFE_TIME', '90 ngày', '60 ngày', 'Tuổi thọ mật khẩu'],
    ['PASSWORD_GRACE_TIME', '7 ngày', 'mặc định', 'Thời gian cảnh báo trước khi hết hạn'],
    ['PASSWORD_REUSE_MAX', '5', 'mặc định', 'Không dùng lại 5 mật khẩu gần nhất'],
    ['SESSIONS_PER_USER', '2', '5', 'Số phiên đồng thời tối đa'],
    ['IDLE_TIME', '30 phút', '60 phút', 'Ngắt phiên không hoạt động'],
    ['CONNECT_TIME', '480 phút', 'mặc định', 'Thời gian tối đa của một phiên'],
  ], [2700, 1700, 1700, 2971], { size: 19, monoCols: [0] }),
  gap(),
  H3('4.4.3. Tạo, sửa, khóa, mở khóa và xóa user'),
  P('Script `08_quan_tri_user.sql` tạo user tạm `U_DEMO` gán profile `PROFILE_NHANVIEN`, thực hiện lần lượt các thao tác quản trị rồi xóa user này khi kết thúc:'),
  tblCaption('Các thao tác quản trị người dùng đã thực hiện'),
  table(['Thao tác', 'Lệnh', 'Kết quả'], [
    ['Tạo user', 'CREATE USER U_DEMO IDENTIFIED BY ... DEFAULT TABLESPACE USERS QUOTA 5M ON USERS PROFILE PROFILE_NHANVIEN', 'Tài khoản OPEN, đúng profile'],
    ['Đổi mật khẩu', 'ALTER USER U_DEMO IDENTIFIED BY ...', 'User altered'],
    ['Đổi quota', 'ALTER USER U_DEMO QUOTA 20M ON USERS', 'DBA_TS_QUOTAS hiển thị 20 MB'],
    ['Đổi profile', 'ALTER USER U_DEMO PROFILE PROFILE_QUANTRI', 'Cột PROFILE đổi theo'],
    ['Bắt đổi mật khẩu', 'ALTER USER U_DEMO PASSWORD EXPIRE', 'ACCOUNT_STATUS = EXPIRED'],
    ['Khóa tài khoản', 'ALTER USER U_DEMO ACCOUNT LOCK', 'ACCOUNT_STATUS = LOCKED'],
    ['Mở khóa', 'ALTER USER U_DEMO ACCOUNT UNLOCK', 'ACCOUNT_STATUS = OPEN'],
    ['Xóa user', 'DROP USER U_DEMO CASCADE', 'User dropped'],
  ], [1900, 4571, 2600], { size: 19 }),
  gap(),
  P('Cơ chế khóa tự động của profile được kiểm chứng: đăng nhập sai mật khẩu 3 lần liên tiếp thì lần thứ tư, dù nhập đúng mật khẩu, vẫn bị từ chối vì tài khoản đã bị khóa (ORA-28000). Admin mở khóa bằng `ALTER USER ... ACCOUNT UNLOCK` rồi đăng nhập lại được:', { keepNext: true }),
  ...code(lockLog),
  H3('4.4.4. Quyền hệ thống, quyền đối tượng và role nhiều cấp'),
  P('Role có thể chứa role khác. Role `ROLE_TRUONG_NHOM` được tạo chứa `ROLE_APP_USER` và quyền hệ thống CREATE VIEW, rồi gán cho `U_DEMO`; user này còn được cấp CREATE TABLE và quyền SELECT trên `SUCO_APP.PHONG` kèm `WITH GRANT OPTION` (được phép cấp tiếp cho người khác). Các khung nhìn `DBA_ROLE_PRIVS`, `DBA_SYS_PRIVS`, `DBA_TAB_PRIVS`, `SESSION_ROLES` và `SESSION_PRIVS` dùng để kiểm tra:', { keepNext: true }),
  ...code([
    "GRANTEE            GRANTED_ROLE",
    "ROLE_TRUONG_NHOM   ROLE_APP_USER",
    "U_DEMO             ROLE_TRUONG_NHOM",
    "",
    "GRANTEE            PRIVILEGE",
    "ROLE_TRUONG_NHOM   CREATE VIEW",
    "U_DEMO             CREATE SESSION",
    "U_DEMO             CREATE TABLE",
    "",
    "GRANTEE  TABLE_NAME  PRIVILEGE  GRANTABLE",
    "U_DEMO   PHONG       SELECT     YES",
  ]),
  P('Khi `U_DEMO` đăng nhập, `SESSION_ROLES` hiển thị cả `ROLE_TRUONG_NHOM` và `ROLE_APP_USER` (role lồng nhau được kích hoạt), đọc được `PHONG` (10 dòng) và `SU_CO` (15 dòng), tạo được bảng riêng nhưng UPDATE `SU_CO` bị từ chối. Sau đó các quyền được thu hồi bằng REVOKE và user cùng role thử bị xóa. Hai profile và việc gán chúng cho `U_NHANVIEN`, `U_QUANTRI` được giữ lại.'),
  // ---- 4.5
  H2('4.5. Xuất và nhập một schema bằng Data Pump'),
  P('Data Pump (`expdp`, `impdp`) làm việc với thư mục Oracle (DIRECTORY) chứ không trực tiếp với đường dẫn hệ điều hành. Các bước: tạo DIRECTORY và cấp quyền, xuất schema `SUCO_APP`, tạo user đích `SUCO_APP2`, nhập với `REMAP_SCHEMA` (đổi chủ sở hữu) và `REMAP_TABLESPACE` (đổi tablespace), kiểm tra.'),
  ...code([
    "-- SYSTEM, PDB FREEPDB1",
    "CREATE OR REPLACE DIRECTORY DP_DIR AS 'C:\\ORADATA\\dpdump';",
    "GRANT READ, WRITE ON DIRECTORY DP_DIR TO SUCO_APP;",
    "",
    "-- Cửa sổ lệnh Windows: xuất schema",
    "expdp SUCO_APP/******@localhost:1521/FREEPDB1 schemas=SUCO_APP directory=DP_DIR",
    "      dumpfile=suco_app.dmp logfile=exp_suco_app.log reuse_dumpfiles=yes",
  ]),
  P('Kết quả xuất (7 bảng, 77 dòng, kèm view, trigger, chỉ mục, ràng buộc):', { keepNext: true }),
  ...code(tidyAll(expLog.map(l => l.replace(/\s{2,}/g, '  ')).filter(l => !/^\.\s\.\sexported/.test(l) || true))),
  P('Lần nhập đầu tiên thất bại hàng loạt với ORA-01918 (user \'SUCO_APP2\' does not exist) vì tệp xuất do user thường tạo ra không chứa lệnh tạo user và `REMAP_SCHEMA` không tự tạo user đích. Cần tạo user đích trước:', { keepNext: true }),
  ...code([
    "CREATE USER SUCO_APP2 IDENTIFIED BY \"SuCo_App2_123\" DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;",
    "GRANT CREATE SESSION TO SUCO_APP2;",
    "",
    "impdp SYSTEM/******@localhost:1521/FREEPDB1 directory=DP_DIR dumpfile=suco_app.dmp",
    "      logfile=imp_suco_app2.log remap_schema=SUCO_APP:SUCO_APP2 remap_tablespace=TS_SUCO:USERS",
  ]),
  P('Kết quả nhập lần hai thành công, không lỗi:', { keepNext: true }),
  ...code(tidyAll(impLog.map(l => l.replace(/\s{2,}/g, '  ')))),
  P('Đối chiếu schema gốc và schema mới sau khi nhập cho thấy hai schema giống hệt nhau về số dòng và số đối tượng; điểm khác duy nhất là tablespace chứa bảng (theo `REMAP_TABLESPACE`). Schema thử `SUCO_APP2` được xóa sau khi kiểm tra.', { keepNext: true }),
  tblCaption('So sánh schema gốc SUCO_APP và schema nhập SUCO_APP2'),
  table(['Tiêu chí', 'SUCO_APP (gốc)', 'SUCO_APP2 (nhập)'], [
    ['Số dòng SU_CO', '15', '15'], ['Số dòng LICH_SU_XU_LY', '13', '13'],
    ['Số bảng / view / trigger', '7 / 1 / 2', '7 / 1 / 2'], ['Số chỉ mục', '12', '12'],
    ['Ràng buộc PRIMARY KEY / FOREIGN KEY', '7 / 10', '7 / 10'], ['Ràng buộc CHECK / UNIQUE', '9 / 2', '9 / 2'],
    ['Tablespace của bảng SU_CO', 'TS_SUCO', 'USERS (theo REMAP_TABLESPACE)'],
  ], [3800, 2500, 2771], { size: 21, centerCols: [1, 2] }),
  gap(),
];

// ================= CHUONG 5 =================
const rCapNhat = extract(WEB + '/java/vn/utc/suco/repository/SuCoRepo.java', /public void capNhat\(Long maSc/, l => /^    }$/.test(l));
const rXoa = extract(WEB + '/java/vn/utc/suco/repository/SuCoRepo.java', /@Transactional/, l => /^    }$/.test(l));
const rInter = extract(WEB + '/java/vn/utc/suco/config/AuthInterceptor.java', /public boolean preHandle/, l => /^    }$/.test(l));
const props = readLines(WEB + '/resources/application.properties').filter(l => l.trim() !== '');
const ch5 = [
  H1('CHƯƠNG 5. ỨNG DỤNG WEB KẾT NỐI ORACLE'),
  H2('5.1. Công nghệ và kiến trúc'),
  P('Ứng dụng web được viết bằng Java theo mô hình MVC đơn giản, dễ giải thích: **Controller** nhận yêu cầu, gọi **Repository** (câu SQL viết tay bằng `JdbcTemplate`) truy vấn Oracle, đưa dữ liệu (model) ra **View** Thymeleaf. Không dùng JPA/Hibernate, Spring Security hay tầng Service để giữ mã nguồn ngắn gọn và thấy rõ từng câu SQL gửi xuống Oracle.'),
  tblCaption('Công nghệ sử dụng trong ứng dụng web'),
  table(['Thành phần', 'Công nghệ'], [
    ['Nền tảng', 'Spring Boot 4.1.1, Java (release 21, chạy trên JDK 26)'],
    ['Tầng web (Controller)', 'Spring MVC: AuthController, SuCoController, AdminController, ThietBiController, NguoiDungController, KinhNghiemController'],
    ['Tầng dữ liệu (Repository)', 'JdbcTemplate: NguoiDungRepo, SuCoRepo, ThietBiRepo, DanhMucRepo, KinhNghiemRepo'],
    ['Giao diện (View)', 'Thymeleaf, Bootstrap 5 (CDN), một layout chung có thanh menu và nút Đăng xuất'],
    ['Phiên đăng nhập', 'HttpSession; AuthInterceptor chặn trang khi chưa đăng nhập hoặc sai vai trò'],
    ['Kết nối', 'HikariCP + Oracle JDBC ojdbc17 tới FREEPDB1 bằng user SUCO_APP'],
  ], [2800, 6271], { size: 21 }),
  gap(),
  P('Luồng xử lý một yêu cầu: trình duyệt gửi HTTP, `AuthInterceptor` kiểm tra phiên và vai trò, Controller gọi Repository, Repository chạy câu SQL trên Oracle (bảng, view, trigger), kết quả được đưa vào Model rồi View Thymeleaf sinh HTML trả về. Ví dụ khi admin đổi trạng thái một sự cố: `POST /admin/{id}` -> `AdminController.capNhat` -> `SuCoRepo.capNhat` (UPDATE `SU_CO`, gán `MA_ADMIN_XU_LY` là admin đang đăng nhập) -> trigger `TRG_SU_CO_HOAN_THANH` gán ngày hoàn thành và `TRG_SU_CO_LICH_SU` ghi lịch sử -> chuyển hướng về trang chi tiết hiển thị lịch sử mới.'),
  H3('5.1.1. Cấu trúc thư mục'),
  ...code([
    "web/",
    "  pom.xml                                   (Spring Boot 4.1.1, ojdbc17 23.26.3.0.0)",
    "  src/main/java/vn/utc/suco/",
    "    SucoApplication.java",
    "    model/        NguoiDung, SuCo, LichSu, BaiKinhNghiem, DanhMuc, ThietBi, Phong  (record)",
    "    repository/   NguoiDungRepo, SuCoRepo, KinhNghiemRepo, DanhMucRepo, ThietBiRepo",
    "    controller/   AuthController, SuCoController, AdminController,",
    "                  ThietBiController, NguoiDungController, KinhNghiemController",
    "    config/       AuthInterceptor, WebConfig",
    "  src/main/resources/",
    "    application.properties",
    "    templates/    layout, login, user, admin, admin-chi-tiet, kinh-nghiem, kinh-nghiem-chi-tiet,",
    "                  thiet-bi, thiet-bi-sua, nguoi-dung, nguoi-dung-sua",
  ]),
  H3('5.1.2. Cấu hình kết nối Oracle'),
  ...code(props),
  H2('5.2. Các chức năng'),
  P('Ứng dụng có các màn hình sau; tài khoản demo: quản trị `hung.nv@utc.edu.vn` / `admin123`, người dùng `anh.pd@st.utc.edu.vn` / `123456`.'),
  H3('5.2.1. Đăng nhập và phân quyền trang'),
  P('Người dùng nhập email và mật khẩu; ứng dụng kiểm tra trong bảng `NGUOI_DUNG` bằng câu lệnh có tham số (chống SQL injection) rồi lưu người dùng vào `HttpSession`. Vai trò USER được chuyển tới `/user`, ADMIN tới `/admin`. Chưa đăng nhập thì mọi trang khác chuyển về `/login`; USER truy cập `/admin/**` bị chuyển về `/user`.'),
  ...placeholder('Màn hình đăng nhập'),
  H3('5.2.2. Báo sự cố và xem sự cố của tôi (USER)'),
  P('Form báo sự cố gồm tiêu đề, mô tả, thiết bị, danh mục và mức ưu tiên; danh sách thiết bị và danh mục được lấy từ cơ sở dữ liệu. Bên dưới là bảng các sự cố do chính người đó báo kèm trạng thái.'),
  ...placeholder('Màn hình báo sự cố của người dùng'),
  H3('5.2.3. Quản lý sự cố (ADMIN)'),
  P('Admin xem bảng tất cả sự cố, lọc theo trạng thái. Trang chi tiết cho phép đổi trạng thái, ghi nguyên nhân, cách khắc phục, chọn bài kinh nghiệm liên quan và hiển thị bảng lịch sử xử lý đọc từ `LICH_SU_XU_LY`. Admin cũng có thể xóa sự cố (kèm lịch sử).'),
  ...placeholder('Danh sách sự cố của admin, có lọc theo trạng thái'),
  ...placeholder('Trang chi tiết sự cố: form cập nhật và lịch sử xử lý'),
  H3('5.2.4. Kho kinh nghiệm'),
  P('Người dùng và admin đều xem được danh sách bài kinh nghiệm, lọc theo danh mục và xem chi tiết gồm triệu chứng, nguyên nhân, giải pháp, người viết.'),
  ...placeholder('Kho kinh nghiệm: danh sách và trang chi tiết bài'),
  H3('5.2.5. Quản lý thiết bị (ADMIN)'),
  P('Trang `/admin/thiet-bi` hiển thị danh sách và cho phép thêm, sửa, xóa thiết bị. Thiết bị đã có sự cố tham chiếu không xóa được: Oracle từ chối do khóa ngoại `FK_SU_CO_THIET_BI` (ORA-02292) và ứng dụng bắt lỗi để hiển thị thông báo dễ hiểu.'),
  ...placeholder('Quản lý thiết bị: danh sách, thêm, sửa, xóa'),
  H3('5.2.6. Quản lý người dùng (ADMIN)'),
  P('Trang `/admin/nguoi-dung` cho phép thêm, sửa, xóa người dùng, đổi vai trò, đặt lại mật khẩu (để trống thì giữ nguyên). Các quy tắc an toàn: email không được trùng (ràng buộc UNIQUE), admin không tự xóa hoặc tự hạ quyền chính mình để luôn còn ít nhất một admin, người dùng đã có sự cố hoặc bài viết không xóa được (khóa ngoại). Mật khẩu không bao giờ được đọc ra giao diện.'),
  ...placeholder('Quản lý người dùng: danh sách, thêm, sửa, xóa'),
  H2('5.3. Mã nguồn tiêu biểu'),
  P('Câu lệnh cập nhật sự cố trong `SuCoRepo`: gán `MA_ADMIN_XU_LY` là admin đang đăng nhập để trigger biết ai là người cập nhật khi ghi lịch sử:', { keepNext: true }),
  ...code(rCapNhat),
  P('Xóa sự cố phải xóa bảng con `LICH_SU_XU_LY` trước, dùng `@Transactional` để hai lệnh DELETE cùng thành công hoặc cùng hoàn tác:', { keepNext: true }),
  ...code(rXoa),
  P('`AuthInterceptor` chặn truy cập khi chưa đăng nhập hoặc không đúng vai trò:', { keepNext: true }),
  ...code(rInter),
  H2('5.4. Phân quyền ở ứng dụng và ở Oracle'),
  P('Hai lớp phân quyền bổ sung cho nhau. Ở Oracle, `ROLE_APP_USER` chỉ có SELECT và INSERT trên `SU_CO`, còn `ROLE_APP_ADMIN` có toàn quyền; ứng dụng phản ánh đúng phân chia đó: USER chỉ thêm và xem sự cố, ADMIN mới sửa và xóa. Ứng dụng kết nối bằng chính user chủ schema `SUCO_APP` nên việc phân quyền trong web do `AuthInterceptor` đảm nhiệm, còn hai role Oracle được minh họa trực tiếp bằng các user `U_NHANVIEN` và `U_QUANTRI` ở mục 4.4.'),
  H2('5.5. Kiểm thử ứng dụng'),
  P('Ứng dụng được kiểm thử bằng các yêu cầu HTTP thật tới máy chủ đang chạy kết nối Oracle, kết hợp đối chiếu dữ liệu trong cơ sở dữ liệu. Dữ liệu thử được xóa sạch sau mỗi lần kiểm thử.'),
  tblCaption('Các trường hợp kiểm thử ứng dụng web'),
  table(['STT', 'Trường hợp', 'Kết quả mong đợi và thực tế', 'Kết quả'], [
    ['1', 'Đăng nhập sai mật khẩu', 'Hiện thông báo lỗi, ở lại trang đăng nhập', 'Đạt'],
    ['2', 'USER đăng nhập', 'Chuyển tới /user', 'Đạt'],
    ['3', 'ADMIN đăng nhập', 'Chuyển tới /admin', 'Đạt'],
    ['4', 'USER truy cập /admin', 'Bị chuyển về /user', 'Đạt'],
    ['5', 'Chưa đăng nhập vào /kinh-nghiem', 'Chuyển về /login', 'Đạt'],
    ['6', 'USER báo sự cố mới', 'Sự cố xuất hiện trong danh sách của người đó và của admin', 'Đạt'],
    ['7', 'Admin lọc theo trạng thái', 'Tất cả: 15, Mới: 4, Hoàn thành: 8 (khớp dữ liệu)', 'Đạt'],
    ['8', 'Admin đổi trạng thái, ghi nguyên nhân', 'Lưu đúng; trigger ghi 2 dòng lịch sử, tự gán ngày hoàn thành', 'Đạt'],
    ['9', 'Gửi trạng thái không hợp lệ', 'Bị từ chối, báo lỗi', 'Đạt'],
    ['10', 'Kho kinh nghiệm: lọc theo danh mục', 'Tất cả: 9 bài; danh mục Máy chiếu: 2 bài; xem chi tiết đúng nội dung', 'Đạt'],
    ['11', 'Thêm, sửa, xóa thiết bị', 'Số thiết bị 12 -> 13 -> 12; dữ liệu sửa hiển thị đúng', 'Đạt'],
    ['12', 'Xóa thiết bị đang có sự cố', 'Oracle chặn bằng khóa ngoại, web báo lỗi dễ hiểu, dữ liệu không đổi', 'Đạt'],
    ['13', 'Xóa sự cố', 'Sự cố và 2 dòng lịch sử cùng bị xóa, không còn dòng mồ côi', 'Đạt'],
    ['14', 'Thêm, sửa, xóa người dùng', 'Đăng nhập được bằng tài khoản mới; đổi mật khẩu và vai trò đúng; số người dùng 10 -> 11 -> 10', 'Đạt'],
    ['15', 'Email trùng (thêm và sửa)', 'Bị chặn, báo "Email này đã được sử dụng"', 'Đạt'],
    ['16', 'Admin tự xóa hoặc tự hạ quyền', 'Bị chặn, báo lỗi', 'Đạt'],
    ['17', 'Xóa người dùng đang có dữ liệu', 'Oracle chặn bằng khóa ngoại, web báo lỗi dễ hiểu', 'Đạt'],
    ['18', 'USER gửi thẳng lệnh xóa tới /admin/...', 'Bị chuyển về /user, dữ liệu không đổi', 'Đạt'],
  ], [700, 2900, 4471, 1000], { size: 19, centerCols: [0, 3] }),
  gap(),
];

// ================= CHUONG 6 + PHU LUC =================
const ch6 = [
  H1('CHƯƠNG 6. KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN'),
  H2('6.1. Kết quả đạt được'),
  bullet('Xây dựng cơ sở dữ liệu quản lý sự cố CNTT gồm 7 bảng trong user `SUCO_APP` và tablespace `TS_SUCO` riêng, có 28 ràng buộc được đặt tên, 2 trigger, 1 view và dữ liệu mẫu tiếng Việt (77 bản ghi).'),
  bullet('Thiết kế 15 truy vấn SQL từ cơ bản đến nâng cao và chạy thật với kết quả đúng như dữ liệu mẫu.'),
  bullet('Thực hành đầy đủ quản trị Oracle: các chế độ instance, PDB, restricted session, tablespace và datafile, ARCHIVELOG, khôi phục datafile bị mất bằng RMAN, profile, người dùng, role và quyền, xuất nhập schema bằng Data Pump.'),
  bullet('Xây dựng ứng dụng web Java kết nối Oracle với các chức năng đăng nhập, báo sự cố, xử lý sự cố, kho kinh nghiệm, quản lý thiết bị và người dùng; nghiệp vụ lịch sử xử lý và ngày hoàn thành do trigger Oracle thực hiện.'),
  H2('6.2. Khó khăn và bài học'),
  bullet('Bản Oracle Free không hỗ trợ di chuyển datafile online (ORA-00439) và tạo tablespace mặc định kiểu BIGFILE (ORA-32771); phải chuyển sang cách di chuyển offline và dùng SMALLFILE.'),
  bullet('Data Pump không tự tạo user đích khi dùng REMAP_SCHEMA với tệp xuất từ user thường (ORA-01918); phải tạo user đích trước.'),
  bullet('Trong SQL*Plus, chú thích viết ngay sau dấu chấm phẩy trên cùng một dòng làm SQL*Plus không nhận ra hết lệnh; cần đặt chú thích trên dòng riêng.'),
  bullet('Khôi phục bằng RMAN đòi hỏi cơ sở dữ liệu chạy ARCHIVELOG và có sao lưu từ trước; nếu không có hai điều kiện này thì không khôi phục được.'),
  H2('6.3. Hạn chế và hướng phát triển'),
  bullet('Mật khẩu người dùng đang lưu dạng văn bản thường (chỉ dùng cho demo); cần băm bằng thuật toán một chiều có muối trước khi triển khai thật.'),
  bullet('Ứng dụng web kết nối bằng user chủ schema; hướng phát triển là dùng một user ứng dụng riêng gắn với các role `ROLE_APP_USER`, `ROLE_APP_ADMIN` để phân quyền ngay ở mức cơ sở dữ liệu.'),
  bullet('Bổ sung gửi thông báo khi sự cố đổi trạng thái, đính kèm ảnh chụp lỗi, báo cáo thống kê trên web và phân trang danh sách.'),
  bullet('Triển khai sao lưu định kỳ bằng RMAN và bật kiểm toán (audit) các thao tác nhạy cảm.'),
  H2('6.4. Tài liệu tham khảo'),
  bullet('Oracle Corporation, Oracle AI Database 26ai Documentation: Database Administrator\'s Guide; SQL Language Reference; Backup and Recovery User\'s Guide; Utilities (Data Pump).'),
  bullet('Oracle Corporation, Oracle JDBC Developer\'s Guide (driver ojdbc17).'),
  bullet('Spring Framework và Spring Boot Reference Documentation (Spring MVC, JdbcTemplate).'),
  bullet('Thymeleaf Documentation, Bootstrap 5 Documentation.'),
  bullet('Khoa Công nghệ thông tin, Trường Đại học Giao thông Vận tải: Đề thi kết thúc học phần Công nghệ Oracle, Liên thông, năm học 2025 - 2026.'),
  H1('PHỤ LỤC'),
  H2('Phụ lục A. Thứ tự chạy các script SQL'),
  tblCaption('Thứ tự và người chạy các script'),
  table(['Thứ tự', 'Tệp', 'Chạy bằng', 'Nội dung'], [
    ['1', '01_tablespace_user.sql', 'SYSTEM (FREEPDB1)', 'Tablespace TS_SUCO, user SUCO_APP, 2 role'],
    ['2', '02_tables.sql', 'SUCO_APP', 'Tạo 7 bảng, ràng buộc, chỉ mục'],
    ['3', '03_triggers.sql', 'SUCO_APP', '2 trigger trên SU_CO'],
    ['4', '04_data.sql', 'SUCO_APP', 'Dữ liệu mẫu'],
    ['5', '05_queries.sql', 'SUCO_APP', 'View và 15 truy vấn'],
    ['6', '06_users_roles.sql', 'SYSTEM', 'Phân quyền cho role, user demo'],
    ['7', '08_quan_tri_user.sql', 'SYSTEM', 'Profile, quản trị user, role nhiều cấp'],
    ['-', '07_admin_demo.sql', 'SYSDBA', 'Kịch bản quản trị (chạy thủ công từng khối)'],
    ['-', '00_run_all.sql / 99_drop_all.sql', 'SYSTEM', 'Chạy toàn bộ / xóa sạch để làm lại'],
  ], [900, 3000, 2000, 3171], { size: 19, centerCols: [0], monoCols: [1] }),
  gap(),
  H2('Phụ lục B. Cấu trúc thư mục dự án'),
  ...code([
    "IMS/",
    "  README.md, SO_DO_QUAN_HE.md",
    "  sql/                 00_run_all, 01..08, 99_drop_all",
    "  sql/ketqua/          nhật ký chạy thật (instance, restricted, tablespace, expdp, impdp, quản trị user)",
    "  docs/                so_do_ER_SUCO_APP.png, báo cáo",
    "  tools/VeSoDoER.java  vẽ sơ đồ ER từ từ điển dữ liệu Oracle",
    "  web/                 ứng dụng Spring Boot (mvn spring-boot:run)",
  ]),
  H2('Phụ lục C. Chạy ứng dụng web'),
  ...code(['cd web', 'mvn spring-boot:run', '# mở http://localhost:8080']),
];

// ================= RAP THANH TAI LIEU =================
const footer = () => new Footer({ children: [new Paragraph({ alignment: AlignmentType.CENTER,
  children: [new TextRun({ children: [PageNumber.CURRENT], font: FONT, size: 24 })] })] });
const portrait = (start) => ({ page: { size: { width: 11906, height: 16838 }, margin: { top: 1134, bottom: 1134, left: 1701, right: 1134 },
  ...(start ? { pageNumbers: { start } } : {}) } });

const doc = new Document({
  creator: 'Nhóm sinh viên', title: 'Báo cáo bài tập lớn Công nghệ Oracle - Quản lý sự cố CNTT',
  features: { updateFields: true },
  styles: {
    default: { document: { run: { font: FONT, size: 26 } } },
    paragraphStyles: [
      { id: 'Heading1', name: 'Heading 1', basedOn: 'Normal', next: 'Normal', quickFormat: true,
        run: { size: 32, bold: true, font: FONT, color: '000000' }, paragraph: { spacing: { before: 240, after: 240 }, outlineLevel: 0 } },
      { id: 'Heading2', name: 'Heading 2', basedOn: 'Normal', next: 'Normal', quickFormat: true,
        run: { size: 28, bold: true, font: FONT, color: '000000' }, paragraph: { spacing: { before: 240, after: 120 }, outlineLevel: 1 } },
      { id: 'Heading3', name: 'Heading 3', basedOn: 'Normal', next: 'Normal', quickFormat: true,
        run: { size: 26, bold: true, italics: true, font: FONT, color: '000000' }, paragraph: { spacing: { before: 200, after: 100 }, outlineLevel: 2 } },
    ],
  },
  numbering: { config: [{ reference: 'bullets', levels: [
    { level: 0, format: LevelFormat.BULLET, text: '\u2022', alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 720, hanging: 360 } } } },
    { level: 1, format: LevelFormat.BULLET, text: '\u2013', alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 1440, hanging: 360 } } } }] }] },
  sections: [
    { properties: portrait(), children: cover },
    { properties: portrait(1), footers: { default: footer() }, children: [...tocPart, ...ch1] },
    { properties: { page: { size: { width: 11906, height: 16838, orientation: PageOrientation.LANDSCAPE },
        margin: { top: 567, bottom: 800, left: 850, right: 850, header: 300, footer: 300 } } }, footers: { default: footer() }, children: erSection },
    { properties: portrait(), footers: { default: footer() }, children: [...ch2, ...ch3, ...ch4, ...ch5, ...ch6] },
  ],
});
Packer.toBuffer(doc).then(b => { fs.writeFileSync(OUT, b); console.log('Da ghi', OUT, Math.round(b.length / 1024) + ' KB'); });
