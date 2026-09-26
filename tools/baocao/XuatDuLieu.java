import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

/** Xuat cau truc bang, rang buoc va ket qua 15 truy van tu Oracle ra JSON (UTF-8) cho bao cao. */
public class XuatDuLieu {
    static String js(String s) {
        if (s == null) return "null";
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': b.append("\\\""); break;
                case '\\': b.append("\\\\"); break;
                case '\n': b.append("\\n"); break;
                case '\r': break;
                case '\t': b.append("\\t"); break;
                default: b.append(c);
            }
        }
        return b.append('"').toString();
    }

    static String val(ResultSet r, int i) throws SQLException {
        String s = r.getString(i);
        if (s == null) return "NULL";
        if (s.matches("\\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d(\\.0)?")) s = s.substring(0, 16);
        return s;
    }

    public static void main(String[] a) throws Exception {
        String sqlFile = a[0], out = a[1];
        StringBuilder o = new StringBuilder("{\n");
        try (Connection c = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/FREEPDB1", "SUCO_APP", "SuCo_App_123");
             Statement s = c.createStatement()) {
            // ---- cau truc bang
            String[] order = {"NGUOI_DUNG", "PHONG", "THIET_BI", "DANH_MUC_SU_CO", "BAI_KINH_NGHIEM", "SU_CO", "LICH_SU_XU_LY"};
            Set<String> pk = new HashSet<>(), uk = new HashSet<>(), fk = new HashSet<>();
            try (ResultSet r = s.executeQuery("SELECT k.constraint_type, cc.table_name, cc.column_name FROM user_constraints k "
                    + "JOIN user_cons_columns cc ON cc.constraint_name = k.constraint_name WHERE k.constraint_type IN ('P','U','R')")) {
                while (r.next()) {
                    String key = r.getString(2) + "." + r.getString(3);
                    switch (r.getString(1)) { case "P" -> pk.add(key); case "U" -> uk.add(key); default -> fk.add(key); }
                }
            }
            o.append("\"bang\": [\n");
            for (int t = 0; t < order.length; t++) {
                int rows;
                try (ResultSet r = s.executeQuery("SELECT COUNT(*) FROM " + order[t])) { r.next(); rows = r.getInt(1); }
                o.append("  {\"ten\": ").append(js(order[t])).append(", \"soDong\": ").append(rows).append(", \"cot\": [\n");
                try (ResultSet r = s.executeQuery("SELECT column_name, data_type, data_length, char_length, char_used, data_precision, data_scale, nullable, identity_column "
                        + "FROM user_tab_columns WHERE table_name = '" + order[t] + "' ORDER BY column_id")) {
                    boolean first = true;
                    while (r.next()) {
                        String dt = r.getString(2), type = dt;
                        if (dt.equals("VARCHAR2")) type = "VARCHAR2(" + ("C".equals(r.getString(5)) ? r.getInt(4) : r.getInt(3)) + ")";
                        else if (dt.equals("NUMBER") && r.getObject(6) != null) type = "NUMBER(" + r.getInt(6) + (r.getInt(7) > 0 ? "," + r.getInt(7) : "") + ")";
                        String key = order[t] + "." + r.getString(1);
                        String khoa = (pk.contains(key) ? "PK" : "") + (pk.contains(key) && fk.contains(key) ? ",FK" : (fk.contains(key) ? "FK" : ""))
                                + (uk.contains(key) && !pk.contains(key) && !fk.contains(key) ? "UK" : "");
                        if (!first) o.append(",\n");
                        first = false;
                        o.append("    {\"ten\": ").append(js(r.getString(1))).append(", \"kieu\": ").append(js(type))
                                .append(", \"notNull\": ").append("N".equals(r.getString(8))).append(", \"khoa\": ").append(js(khoa))
                                .append(", \"identity\": ").append("YES".equals(r.getString(9))).append("}");
                    }
                }
                o.append("\n  ]}").append(t + 1 < order.length ? ",\n" : "\n");
            }
            o.append("],\n");

            // ---- rang buoc (khong tinh NOT NULL tu dong SYS_)
            o.append("\"rangBuoc\": [\n");
            boolean first = true;
            try (ResultSet r = s.executeQuery("SELECT k.constraint_name, k.constraint_type, k.table_name, k.search_condition, "
                    + "(SELECT LISTAGG(cc.column_name, ', ') WITHIN GROUP (ORDER BY cc.position) FROM user_cons_columns cc WHERE cc.constraint_name = k.constraint_name) AS cots, "
                    + "(SELECT p.table_name FROM user_constraints p WHERE p.constraint_name = k.r_constraint_name) AS cha "
                    + "FROM user_constraints k WHERE k.constraint_name NOT LIKE 'SYS\\_%' ESCAPE '\\' AND k.constraint_type IN ('P','R','U','C') "
                    + "ORDER BY k.table_name, DECODE(k.constraint_type,'P',1,'U',2,'R',3,4), k.constraint_name")) {
                while (r.next()) {
                    // Cot 4 (search_condition) la kieu LONG: phai doc dung thu tu cot va chi doc mot lan
                    String ten = r.getString(1), loai = r.getString(2), bang = r.getString(3), dk = r.getString(4);
                    String cots = r.getString(5), cha = r.getString(6);
                    if (dk != null) dk = dk.trim();
                    if (!first) o.append(",\n");
                    first = false;
                    o.append("  {\"ten\": ").append(js(ten)).append(", \"loai\": ").append(js(loai))
                            .append(", \"bang\": ").append(js(bang)).append(", \"dieuKien\": ").append(js(dk))
                            .append(", \"cot\": ").append(js(cots)).append(", \"cha\": ").append(js(cha)).append("}");
                }
            }
            o.append("\n],\n");

            // ---- 15 truy van tu file 05_queries.sql (lay doan comment "-- Qn." lam mo ta)
            List<String> lines = Files.readAllLines(Path.of(sqlFile), StandardCharsets.UTF_8);
            o.append("\"truyVan\": [\n");
            first = true;
            String moTa = null;
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                String t = line.trim();
                if (sb.length() == 0) {
                    if (t.startsWith("-- Q")) { moTa = t.substring(3).trim(); continue; }
                    if (moTa == null || !t.toUpperCase().startsWith("SELECT")) continue;
                }
                sb.append(line).append("\n");
                if (t.endsWith(";")) {
                    String sql = sb.toString().trim();
                    sb.setLength(0);
                    String q = sql.substring(0, sql.length() - 1);
                    if (!first) o.append(",\n");
                    first = false;
                    o.append("  {\"moTa\": ").append(js(moTa)).append(", \"sql\": ").append(js(sql)).append(", \"cot\": [");
                    try (ResultSet r = s.executeQuery(q)) {
                        ResultSetMetaData m = r.getMetaData();
                        int n = m.getColumnCount();
                        for (int i = 1; i <= n; i++) o.append(i > 1 ? ", " : "").append(js(m.getColumnLabel(i)));
                        o.append("], \"dong\": [");
                        int rows = 0;
                        while (r.next() && rows < 20) {
                            o.append(rows > 0 ? ", " : "").append("[");
                            for (int i = 1; i <= n; i++) o.append(i > 1 ? ", " : "").append(js(val(r, i)));
                            o.append("]");
                            rows++;
                        }
                        o.append("]}");
                    }
                    moTa = null;
                }
            }
            o.append("\n]\n}\n");
        }
        Files.writeString(Path.of(out), o.toString(), StandardCharsets.UTF_8);
        System.out.println("Da ghi " + out);
    }
}
