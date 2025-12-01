import java.sql.*;

public class LookupDao implements ILookupDao {
    private static final String URL = "jdbc:mysql://localhost:3306/group2erdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Bangster1862";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void printOutDivision() {
        String sql = "SELECT ID, Name FROM division ORDER BY ID ASC";
        printOptionList(sql, "DIVISIONS");
    }

    public void printOutJobtitle() {
        String sql = "SELECT job_title_id, job_title FROM job_titles ORDER BY job_title_id ASC";
        printOptionList(sql, "JOB TITLES");
    }

    public void printOptionList(String sql, String title) {
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {
            System.out.println("\n--- " + title + " ---");
            while (rs.next()) {
                System.out.printf("%d. %s%n", rs.getInt(1), rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void printAvailablePayrollMonths() {
        String sql =
                "SELECT DISTINCT YEAR(pay_date) AS yr, MONTH(pay_date) AS mon " +
                        "FROM payroll " +
                        "ORDER BY yr DESC, mon DESC";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {

            System.out.println("\n--- AVAILABLE PAYROLL PERIODS (YEAR-MONTH) ---");
            int index = 1;
            while (rs.next()) {
                int year = rs.getInt("yr");
                int month = rs.getInt("mon");
                System.out.printf("%d. %d-%02d%n", index, year, month);
                index++;
            }

            if (index == 1) {
                System.out.println("No payroll records found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}