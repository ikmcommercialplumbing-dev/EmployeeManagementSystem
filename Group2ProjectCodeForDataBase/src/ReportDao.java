import java.sql.*;

public class ReportDao implements IReportDao {
    private static final String URL = "jdbc:mysql://localhost:3306/group2erdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Bangster1862";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void printEmployeeHistoryReport(int targetId) {
        String sql = "SELECT e.Fname, e.Lname, e.SSN, p.pay_date, p.earnings " +
                "FROM employees e JOIN payroll p ON e.empid = p.empid " +
                "WHERE e.empid = ? ORDER BY p.pay_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setInt(1, targetId);
            try (ResultSet rs = psmt.executeQuery()) {
                System.out.println("\n--- PAY HISTORY FOR ID " + targetId + " ---");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.printf("Date: %s | Pay: $%.2f%n", rs.getString("pay_date"), rs.getDouble("earnings"));
                }
                if (!found) System.out.println("No history found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void printJobTitleReport() {
        String sql = "SELECT jt.job_title, SUM(p.earnings) AS Total FROM job_titles jt " +
                "JOIN employee_job_titles ejt ON jt.job_title_id = ejt.job_title_id " +
                "JOIN employees e ON ejt.empid = e.empid " +
                "JOIN payroll p ON e.empid = p.empid GROUP BY jt.job_title";
        runReport(sql, "JOB TITLE");
    }

    @Override
    public void printDivisionReport() {
        String sql = "SELECT d.Name, SUM(p.earnings) AS Total FROM division d " +
                "JOIN employee_division ed ON d.ID = ed.div_ID " +
                "JOIN employees e ON ed.empid = e.empid " +
                "JOIN payroll p ON e.empid = p.empid GROUP BY d.Name";
        runReport(sql, "DIVISION");
    }
    @Override
    public void runReport(String sql, String header) {
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {

            System.out.printf("%-20s | %s%n", header, "TOTAL PAY");
            System.out.println("------------------------------");
            while(rs.next()) {
                System.out.printf("%-20s | $%,.2f%n", rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}