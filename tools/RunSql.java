import java.nio.file.*;
import java.sql.*;
import java.util.*;

/** Chạy file .sql kiểu SQL*Plus đơn giản: args = user pass file [stopOnError] */
public class RunSql {
    public static void main(String[] a) throws Exception {
        String url = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1";
        boolean stop = a.length > 3;
        try (Connection c = DriverManager.getConnection(url, a[0], a[1])) {
            List<String> lines = Files.readAllLines(Path.of(a[2]));
            StringBuilder sb = new StringBuilder();
            boolean plsql = false;
            int ok = 0, err = 0;
            for (String raw : lines) {
                String t = raw.trim();
                if (sb.length() == 0) {
                    String u = t.toUpperCase();
                    if (t.isEmpty() || t.startsWith("--")) continue;
                    if (u.startsWith("SET ") || u.startsWith("SHOW ") || u.startsWith("COLUMN ")
                            || u.startsWith("DEFINE ") || u.startsWith("ACCEPT ") || u.startsWith("CONNECT ")
                            || u.startsWith("@") || u.startsWith("SPOOL")) continue;
                    plsql = u.startsWith("DECLARE") || u.startsWith("BEGIN") || u.matches("CREATE( OR REPLACE)? (TRIGGER|PROCEDURE|FUNCTION).*");
                }
                if (t.startsWith("--") && sb.length() > 0) continue;
                boolean end = plsql ? t.equals("/") : t.endsWith(";");
                if (!end) { sb.append(raw).append("\n"); continue; }
                if (!plsql) sb.append(raw, 0, raw.lastIndexOf(';'));
                String sql = sb.toString().trim();
                sb.setLength(0);
                try (Statement s = c.createStatement()) {
                    boolean rs = s.execute(sql);
                    ok++;
                    String head = sql.replaceAll("\\s+", " ");
                    if (rs) {
                        System.out.println("> " + head.substring(0, Math.min(90, head.length())));
                        try (ResultSet r = s.getResultSet()) {
                            ResultSetMetaData m = r.getMetaData();
                            int n = m.getColumnCount(), rows = 0;
                            StringBuilder h = new StringBuilder();
                            for (int i = 1; i <= n; i++) h.append(m.getColumnLabel(i)).append(" | ");
                            System.out.println("  " + h);
                            while (r.next() && rows++ < 20) {
                                StringBuilder l = new StringBuilder();
                                for (int i = 1; i <= n; i++) l.append(r.getString(i)).append(" | ");
                                System.out.println("  " + l);
                            }
                        }
                    }
                } catch (SQLException e) {
                    err++;
                    String head = sql.replaceAll("\\s+", " ");
                    System.out.println("!! LOI: " + e.getMessage().split("\n")[0] + "\n   SQL: " + head.substring(0, Math.min(110, head.length())));
                    if (stop) break;
                }
            }
            System.out.println("=== " + a[2] + ": ok=" + ok + " loi=" + err);
        }
    }
}
