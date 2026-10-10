import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Vẽ sơ đồ ER của schema SUCO_APP bằng cách ĐỌC TỪ ĐIỂN DỮ LIỆU ORACLE (user_tab_columns, user_constraints,
 * user_cons_columns), nên sơ đồ luôn khớp với CSDL thật. Không cần thư viện ngoài ngoài JDBC driver.
 *
 * Chạy (từ thư mục gốc dự án):
 *   java -cp <đường dẫn ojdbc17.jar> tools/VeSoDoER.java [url] [user] [password] [file_png]
 * Mặc định: jdbc:oracle:thin:@//localhost:1521/FREEPDB1, SUCO_APP, SuCo_App_123, docs/so_do_ER_SUCO_APP.png
 *
 * Ký hiệu: PK = khóa chính, FK = khóa ngoại, UK = duy nhất, * = NOT NULL.
 * Đầu "cha": thanh | (đúng một) hoặc vòng tròn + thanh (không hoặc một, khi cột FK cho phép NULL).
 * Đầu "con": chân chim + vòng tròn (không hoặc nhiều), hoặc thanh + vòng tròn (không hoặc một,
 * khi cột khóa ngoại có ràng buộc UNIQUE - ví dụ mỗi sự cố chỉ có tối đa một PHAN_HOI).
 */
public class VeSoDoER {
    record Col(String name, String type, boolean notNull, boolean pk, boolean fk, boolean uk) { }

    static class Tab {
        String name;
        List<Col> cols = new ArrayList<>();
        double x, y, w, h;
        double cx() { return x + w / 2; }
        double right() { return x + w; }
        double bottom() { return y + h; }
    }

    // Khóa ngoại: bảng con.cột -> bảng cha. uniq = cột con có UNIQUE (quan hệ 1-1).
    record Fk(String child, String col, String parent, boolean nullable, boolean uniq) { }

    static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    static final Font F_HEAD = new Font("Segoe UI", Font.BOLD, 17);
    static final Font F_COL = new Font("Consolas", Font.PLAIN, 15);
    static final Font F_KEY = new Font("Segoe UI", Font.BOLD, 12);
    static final Font F_LBL = new Font("Segoe UI", Font.ITALIC, 14);
    static final Font F_LEG = new Font("Segoe UI", Font.PLAIN, 13);
    static final int ROW_H = 25, HEAD_H = 34, PAD = 12, KEY_W = 54;   // KEY_W đủ chỗ cho nhãn "PK,FK" / "FK,UK"
    static final Color C_HEAD = new Color(0x1F, 0x4E, 0x79), C_PK = new Color(0xFF, 0xF2, 0xCC),
            C_FK = new Color(0xDD, 0xEB, 0xF7), C_LINE = new Color(0x40, 0x40, 0x40), C_BORDER = new Color(0x1F, 0x4E, 0x79);

    public static void main(String[] a) throws Exception {
        String url = a.length > 0 ? a[0] : "jdbc:oracle:thin:@//localhost:1521/FREEPDB1";
        String user = a.length > 1 ? a[1] : "SUCO_APP";
        String pass = a.length > 2 ? a[2] : "SuCo_App_123";
        File out = new File(a.length > 3 ? a[3] : "docs/so_do_ER_SUCO_APP.png");

        Map<String, Tab> tabs = new LinkedHashMap<>();
        List<Fk> fks = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            docCauTruc(c, tabs, fks);
        }
        System.out.println("Doc tu Oracle: " + tabs.size() + " bang, " + fks.size() + " khoa ngoai");
        ve(tabs, fks, user, out);
        System.out.println("Da ve: " + out.getAbsolutePath());
    }

    /** Đọc bảng, cột, PK/UK/FK từ từ điển dữ liệu. */
    static void docCauTruc(Connection c, Map<String, Tab> tabs, List<Fk> fks) throws Exception {
        Map<String, java.util.Set<String>> pk = new HashMap<>(), uk = new HashMap<>(), fkCols = new HashMap<>();
        try (Statement s = c.createStatement()) {
            // Ràng buộc P (khóa chính), U (duy nhất) theo từng cột
            try (ResultSet r = s.executeQuery("SELECT k.constraint_type, cc.table_name, cc.column_name "
                    + "FROM user_constraints k JOIN user_cons_columns cc ON cc.constraint_name = k.constraint_name "
                    + "WHERE k.constraint_type IN ('P','U')")) {
                while (r.next()) {
                    (r.getString(1).equals("P") ? pk : uk)
                            .computeIfAbsent(r.getString(2), x -> new java.util.HashSet<>()).add(r.getString(3));
                }
            }
            // Khóa ngoại: bảng con, cột con, bảng cha (tra qua r_constraint_name)
            Map<String, Boolean> colNull = new HashMap<>();
            try (ResultSet r = s.executeQuery("SELECT table_name, column_name, nullable FROM user_tab_columns")) {
                while (r.next()) {
                    colNull.put(r.getString(1) + "." + r.getString(2), "Y".equals(r.getString(3)));
                }
            }
            try (ResultSet r = s.executeQuery("SELECT k.table_name, cc.column_name, p.table_name AS cha "
                    + "FROM user_constraints k "
                    + "JOIN user_cons_columns cc ON cc.constraint_name = k.constraint_name "
                    + "JOIN user_constraints p ON p.constraint_name = k.r_constraint_name "
                    + "WHERE k.constraint_type = 'R' ORDER BY k.table_name, k.constraint_name")) {
                while (r.next()) {
                    String t = r.getString(1), col = r.getString(2);
                    fks.add(new Fk(t, col, r.getString(3), colNull.get(t + "." + col),
                            uk.getOrDefault(t, java.util.Set.of()).contains(col)));
                    fkCols.computeIfAbsent(t, x -> new java.util.HashSet<>()).add(col);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT table_name, column_name, data_type, data_length, char_length, "
                    + "char_used, data_precision, data_scale, nullable FROM user_tab_columns "
                    + "WHERE table_name IN (SELECT table_name FROM user_tables) ORDER BY table_name, column_id")) {
                while (r.next()) {
                    String t = r.getString(1), col = r.getString(2), dt = r.getString(3);
                    String type = dt;
                    if (dt.equals("VARCHAR2")) {
                        type = "VARCHAR2(" + ("C".equals(r.getString(6)) ? r.getInt(5) : r.getInt(4)) + ")";
                    } else if (dt.equals("NUMBER") && r.getObject(7) != null) {
                        type = "NUMBER(" + r.getInt(7) + (r.getInt(8) > 0 ? "," + r.getInt(8) : "") + ")";
                    }
                    Tab tab = tabs.computeIfAbsent(t, x -> { Tab n = new Tab(); n.name = x; return n; });
                    tab.cols.add(new Col(col, type, "N".equals(r.getString(9)),
                            pk.getOrDefault(t, java.util.Set.of()).contains(col),
                            fkCols.getOrDefault(t, java.util.Set.of()).contains(col),
                            uk.getOrDefault(t, java.util.Set.of()).contains(col)));
                }
            }
        }
    }

    static void ve(Map<String, Tab> tabs, List<Fk> fks, String schema, File out) throws Exception {
        BufferedImage probe = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D pg = probe.createGraphics();
        FontMetrics fmH = pg.getFontMetrics(F_HEAD), fmC = pg.getFontMetrics(F_COL);
        // Kích thước từng bảng theo độ dài chữ
        for (Tab t : tabs.values()) {
            int wn = 0, wt = 0;
            for (Col c : t.cols) {
                wn = Math.max(wn, fmC.stringWidth(c.name() + (c.notNull() ? " *" : "")));
                wt = Math.max(wt, fmC.stringWidth(c.type()));
            }
            t.w = Math.max(fmH.stringWidth(t.name) + 2 * PAD, KEY_W + wn + 24 + wt + PAD);
            t.h = HEAD_H + t.cols.size() * ROW_H + 6;
        }
        pg.dispose();

        // Lưới bố trí 3x3: SU_CO ở giữa, các bảng liên quan xung quanh để đường nối không cắt qua bảng khác.
        // PHAN_HOI đặt ở góc trên bên phải: chỉ nối với SU_CO (qua hành lang giữa hàng 0 và hàng 1)
        // và NGUOI_DUNG (qua hành lang phía trên), nên không cắt qua đường nối nào có sẵn.
        String[][] grid = {
                {"PHONG", "THIET_BI", "PHAN_HOI"},
                {"NGUOI_DUNG", "SU_CO", "DANH_MUC_SU_CO"},
                {"LICH_SU_XU_LY", null, "BAI_KINH_NGHIEM"}};
        for (String[] row : grid) {
            for (String n : row) {
                if (n != null && !tabs.containsKey(n)) {
                    throw new IllegalStateException("Thieu bang " + n + " trong CSDL");
                }
            }
        }
        double[] colW = new double[3], rowH = new double[3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] != null) {
                    colW[c] = Math.max(colW[c], tabs.get(grid[r][c]).w);
                    rowH[r] = Math.max(rowH[r], tabs.get(grid[r][c]).h);
                }
            }
        }
        // top chừa chỗ cho tiêu đề và hành lang ngang phía trên (đường NGUOI_DUNG -> PHAN_HOI)
        double margin = 70, gapX = 150, gapY = 95, top = 124;
        double[] cx = new double[3], ry = new double[3];
        for (int c = 0; c < 3; c++) {
            cx[c] = margin + (c == 0 ? 0 : cx[c - 1] - margin + colW[c - 1] + gapX);
        }
        for (int r = 0; r < 3; r++) {
            ry[r] = top + (r == 0 ? 0 : ry[r - 1] - top + rowH[r - 1] + gapY);
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] != null) {
                    Tab t = tabs.get(grid[r][c]);
                    t.x = cx[c] + (colW[c] - t.w) / 2; // căn giữa trong cột
                    t.y = ry[r];
                }
            }
        }
        double W = cx[2] + colW[2] + margin;
        double bottomTables = ry[2] + rowH[2];
        double yLoop = bottomTables + 38;            // hành lang phía dưới cho đường nối dài
        double yTren = top - 28;                     // hành lang phía trên (NGUOI_DUNG -> PHAN_HOI)
        double yGiua01 = ry[1] - 42;                 // hành lang giữa hàng 0 và hàng 1 (SU_CO -> PHAN_HOI)
        double H = yLoop + 92;

        double S = 2; // hệ số phóng to để ảnh nét khi dán vào Word
        BufferedImage img = new BufferedImage((int) (W * S), (int) (H * S), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.scale(S, S);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int) W + 1, (int) H + 1);
        g.setColor(C_HEAD);
        g.setFont(F_TITLE);
        g.drawString("Sơ đồ quan hệ CSDL " + schema + " - Quản lý sự cố CNTT", (float) margin, 44);
        g.setFont(F_LEG);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Oracle AI Database 26ai Free - PDB FREEPDB1 - " + tabs.size() + " bảng, " + fks.size()
                + " khóa ngoại (đọc trực tiếp từ từ điển dữ liệu)", (float) margin, 68);

        Tab P = tabs.get("PHONG"), T = tabs.get("THIET_BI"), N = tabs.get("NGUOI_DUNG"), SC = tabs.get("SU_CO"),
                D = tabs.get("DANH_MUC_SU_CO"), B = tabs.get("BAI_KINH_NGHIEM"), L = tabs.get("LICH_SU_XU_LY"),
                PH = tabs.get("PHAN_HOI");

        // Mỗi khóa ngoại: đường đi từ bảng CHA sang bảng CON (danh sách điểm gấp khúc)
        g.setStroke(new BasicStroke(1.6f));
        for (Fk fk : fks) {
            List<Point2D> pts = new ArrayList<>();
            Tab ch = tabs.get(fk.child()), pa = tabs.get(fk.parent());
            double xs1 = SC.x + SC.w * 0.75, xs2 = SC.x + SC.w * 0.25;
            if (pa == P && ch == T) {
                double y = P.y + 50; pts.add(pt(P.right(), y)); pts.add(pt(T.x, y));
            } else if (pa == T && ch == SC) {
                pts.add(pt(T.cx(), T.bottom())); pts.add(pt(SC.cx(), SC.y));
            } else if (pa == N && ch == SC) {
                double y = N.y + (fk.col().equals("MA_NGUOI_BAO") ? 45 : 85);
                pts.add(pt(N.right(), y)); pts.add(pt(SC.x, y));
            } else if (pa == D && ch == SC) {
                double y = SC.y + 45; pts.add(pt(D.x, y)); pts.add(pt(SC.right(), y));
            } else if (pa == B && ch == SC) {
                double y = B.y + 45; pts.add(pt(B.x, y)); pts.add(pt(xs1, y)); pts.add(pt(xs1, SC.bottom()));
            } else if (pa == SC && ch == L) {
                double y = L.y + 45; pts.add(pt(xs2, SC.bottom())); pts.add(pt(xs2, y)); pts.add(pt(L.right(), y));
            } else if (pa == N && ch == L) {
                pts.add(pt(N.cx(), N.bottom())); pts.add(pt(L.cx(), L.y));
            } else if (pa == D && ch == B) {
                pts.add(pt(D.cx(), D.bottom())); pts.add(pt(B.cx(), B.y));
            } else if (pa == N && ch == B) {
                double y = N.y + 138;
                pts.add(pt(N.x, y)); pts.add(pt(28, y)); pts.add(pt(28, yLoop));
                pts.add(pt(B.cx() + 45, yLoop)); pts.add(pt(B.cx() + 45, B.bottom()));
            } else if (pa == SC && ch == PH) {
                // Lên hành lang giữa hàng 0 - hàng 1 rồi vòng sang phải, chui vào đáy PHAN_HOI
                double x = SC.x + SC.w * 0.78;
                pts.add(pt(x, SC.y)); pts.add(pt(x, yGiua01));
                pts.add(pt(PH.cx(), yGiua01)); pts.add(pt(PH.cx(), PH.bottom()));
            } else if (pa == N && ch == PH) {
                // Ra lề trái, lên hành lang phía trên, chạy sang phải rồi chui vào đỉnh PHAN_HOI
                double y = N.y + 62;
                pts.add(pt(N.x, y)); pts.add(pt(36, y)); pts.add(pt(36, yTren));
                pts.add(pt(PH.x + PH.w * 0.3, yTren)); pts.add(pt(PH.x + PH.w * 0.3, PH.y));
            } else if (pa == N && ch == N) {
                // Hai khóa ngoại tự tham chiếu: hai vòng nhỏ dưới đáy NGUOI_DUNG, nằm hai bên
                // đường MA_NGUOI_CAP_NHAT (đi thẳng xuống từ giữa đáy) để không chen nhau
                boolean tao = fk.col().equals("MA_NGUOI_TAO");
                // Sâu 52px để ký hiệu hai đầu (vẽ cách mép 11-23px) và nhãn không đè lên nhau
                double x1 = N.x + N.w * (tao ? 0.02 : 0.64), x2 = N.x + N.w * (tao ? 0.36 : 0.98);
                double y = N.bottom() + 52;
                pts.add(pt(x1, N.bottom())); pts.add(pt(x1, y));
                pts.add(pt(x2, y)); pts.add(pt(x2, N.bottom()));
            } else {
                System.out.println("CANH BAO: chua co duong noi cho " + fk);
                continue;
            }
            veCanh(g, pts, fk);
        }

        for (Tab t : tabs.values()) {
            veBang(g, t);
        }

        // Chú thích ký hiệu
        double ly = H - 56;
        g.setFont(F_LEG);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Chú thích:", (float) margin, (float) ly + 14);
        g.setColor(C_PK); g.fillRect((int) margin + 80, (int) ly, 22, 18);
        g.setColor(C_BORDER); g.drawRect((int) margin + 80, (int) ly, 22, 18);
        g.setColor(Color.DARK_GRAY); g.drawString("PK: khóa chính", (int) margin + 108, (int) ly + 14);
        g.setColor(C_FK); g.fillRect((int) margin + 250, (int) ly, 22, 18);
        g.setColor(C_BORDER); g.drawRect((int) margin + 250, (int) ly, 22, 18);
        g.setColor(Color.DARK_GRAY); g.drawString("FK: khóa ngoại", (int) margin + 278, (int) ly + 14);
        g.drawString("UK: duy nhất     * : NOT NULL", (int) margin + 420, (int) ly + 14);
        g.drawString("Đầu cha:  ||  đúng một (FK NOT NULL);  O|  không hoặc một (FK cho phép NULL).     "
                + "Đầu con:  chân chim + O  không hoặc nhiều;  O|  không hoặc một (cột FK có UNIQUE).",
                (float) margin, (float) ly + 40);
        g.dispose();

        File dir = out.getAbsoluteFile().getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }
        ImageIO.write(img, "png", out);
    }

    static Point2D pt(double x, double y) {
        return new Point2D.Double(x, y);
    }

    static void veBang(Graphics2D g, Tab t) {
        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(t.x, t.y, t.w, t.h));
        g.setColor(C_HEAD);
        g.fill(new Rectangle2D.Double(t.x, t.y, t.w, HEAD_H));
        g.setFont(F_HEAD);
        g.setColor(Color.WHITE);
        FontMetrics fh = g.getFontMetrics();
        g.drawString(t.name, (float) (t.x + (t.w - fh.stringWidth(t.name)) / 2), (float) (t.y + 21));
        double y = t.y + HEAD_H;
        for (Col c : t.cols) {
            if (c.pk()) {
                g.setColor(C_PK);
                g.fill(new Rectangle2D.Double(t.x + 1, y, t.w - 2, ROW_H));
            } else if (c.fk()) {
                g.setColor(C_FK);
                g.fill(new Rectangle2D.Double(t.x + 1, y, t.w - 2, ROW_H));
            }
            String key = (c.pk() ? "PK" : "") + (c.pk() && c.fk() ? "," : "") + (c.fk() ? "FK" : "")
                    + (c.uk() && !c.pk() ? (c.fk() ? ",UK" : "UK") : "");
            g.setFont(F_KEY);
            g.setColor(new Color(0x7F, 0x60, 0x00));
            g.drawString(key, (float) (t.x + 6), (float) (y + 15));
            g.setFont(F_COL);
            g.setColor(Color.BLACK);
            g.drawString(c.name() + (c.notNull() ? " *" : ""), (float) (t.x + KEY_W), (float) (y + 15));
            g.setColor(new Color(0x59, 0x59, 0x59));
            FontMetrics fc = g.getFontMetrics();
            g.drawString(c.type(), (float) (t.x + t.w - PAD - fc.stringWidth(c.type())), (float) (y + 15));
            y += ROW_H;
        }
        g.setColor(C_BORDER);
        g.setStroke(new BasicStroke(1.8f));
        g.draw(new Rectangle2D.Double(t.x, t.y, t.w, t.h));
    }

    /** Vẽ đường nối cha -> con kèm ký hiệu hai đầu và nhãn tên cột khóa ngoại. */
    static void veCanh(Graphics2D g, List<Point2D> pts, Fk fk) {
        g.setColor(C_LINE);
        g.setStroke(new BasicStroke(1.6f));
        for (int i = 0; i + 1 < pts.size(); i++) {
            g.draw(new Line2D.Double(pts.get(i), pts.get(i + 1)));
        }
        Point2D p0 = pts.get(0), p1 = pts.get(1);
        Point2D q0 = pts.get(pts.size() - 1), q1 = pts.get(pts.size() - 2);
        dauCha(g, p0, huong(p0, p1), fk.nullable());
        if (fk.uniq()) {
            dauCha(g, q0, huong(q0, q1), true);   // cột FK có UNIQUE: phía con là "không hoặc một"
        } else {
            dauCon(g, q0, huong(q0, q1));
        }

        // Nhãn: đặt ở giữa đoạn dài nhất
        int best = 0;
        double bl = -1;
        for (int i = 0; i + 1 < pts.size(); i++) {
            double l = pts.get(i).distance(pts.get(i + 1));
            if (l > bl) { bl = l; best = i; }
        }
        Point2D m = pt((pts.get(best).getX() + pts.get(best + 1).getX()) / 2,
                (pts.get(best).getY() + pts.get(best + 1).getY()) / 2);
        g.setFont(F_LBL);
        FontMetrics f = g.getFontMetrics();
        int tw = f.stringWidth(fk.col());
        boolean ngang = Math.abs(pts.get(best).getY() - pts.get(best + 1).getY()) < 0.5;
        double lx = ngang ? m.getX() - tw / 2.0 : m.getX() + 6;
        double ly = ngang ? m.getY() - 6 : m.getY() + 4;
        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(lx - 2, ly - 12, tw + 4, 16));
        g.setColor(new Color(0xC0, 0x39, 0x2B));
        g.drawString(fk.col(), (float) lx, (float) ly);
    }

    /** Vector đơn vị từ điểm a hướng về b. */
    static double[] huong(Point2D a, Point2D b) {
        double dx = b.getX() - a.getX(), dy = b.getY() - a.getY(), l = Math.hypot(dx, dy);
        return new double[]{dx / l, dy / l};
    }

    /** Đầu cha: 1 thanh + (thanh nữa nếu bắt buộc | vòng tròn nếu cho phép NULL). */
    static void dauCha(Graphics2D g, Point2D p, double[] d, boolean tuyChon) {
        double nx = -d[1], ny = d[0];
        g.setColor(C_LINE);
        g.setStroke(new BasicStroke(1.8f));
        thanh(g, p, d, nx, ny, 11);
        if (tuyChon) {
            circle(g, pt(p.getX() + d[0] * 20, p.getY() + d[1] * 20), 5);
        } else {
            thanh(g, p, d, nx, ny, 18);
        }
    }

    /** Đầu con: chân chim + vòng tròn (không hoặc nhiều). */
    static void dauCon(Graphics2D g, Point2D p, double[] d) {
        double nx = -d[1], ny = d[0];
        g.setColor(C_LINE);
        g.setStroke(new BasicStroke(1.8f));
        Point2D goc = pt(p.getX() + d[0] * 15, p.getY() + d[1] * 15);
        g.draw(new Line2D.Double(goc, pt(p.getX() + nx * 8, p.getY() + ny * 8)));
        g.draw(new Line2D.Double(goc, p));
        g.draw(new Line2D.Double(goc, pt(p.getX() - nx * 8, p.getY() - ny * 8)));
        circle(g, pt(p.getX() + d[0] * 23, p.getY() + d[1] * 23), 5);
    }

    static void thanh(Graphics2D g, Point2D p, double[] d, double nx, double ny, double khoang) {
        double bx = p.getX() + d[0] * khoang, by = p.getY() + d[1] * khoang;
        g.draw(new Line2D.Double(bx + nx * 7, by + ny * 7, bx - nx * 7, by - ny * 7));
    }

    static void circle(Graphics2D g, Point2D c, double r) {
        Ellipse2D e = new Ellipse2D.Double(c.getX() - r, c.getY() - r, 2 * r, 2 * r);
        g.setColor(Color.WHITE);
        g.fill(e);
        g.setColor(C_LINE);
        g.draw(e);
    }
}
