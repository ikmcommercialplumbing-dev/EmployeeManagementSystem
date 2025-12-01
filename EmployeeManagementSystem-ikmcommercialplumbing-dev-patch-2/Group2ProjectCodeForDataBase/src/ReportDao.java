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
        String sql = "SELECT e.Fname, e.Lname, e.SSN, " +
                "d.Name AS division_name, jt.job_title, " +
                "p.pay_date, p.earnings " +
                "FROM employees e " +
                "LEFT JOIN employee_division ed ON e.empid = ed.empid " +
                "LEFT JOIN division d ON ed.div_ID = d.ID " +
                "LEFT JOIN employee_job_titles ej ON e.empid = ej.empid " +
                "LEFT JOIN job_titles jt ON ej.job_title_id = jt.job_title_id " +
                "LEFT JOIN payroll p ON e.empid = p.empid " +
                "WHERE e.empid = ? " +
                "ORDER BY p.pay_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setInt(1, targetId);

            try (ResultSet rs = psmt.executeQuery()) {
                System.out.println("\n--- PAY HISTORY FOR EMPLOYEE ID " + targetId + " ---");

                boolean found = false;
                String fname = null;
                String lname = null;
                String ssn = null;
                String divisionName = null;
                String jobTitle = null;

                while (rs.next()) {
                    if (!found) {
                        found = true;

                        fname = rs.getString("Fname");
                        lname = rs.getString("Lname");
                        ssn = rs.getString("SSN");
                        divisionName = rs.getString("division_name");
                        jobTitle = rs.getString("job_title");

                        System.out.printf("Name    : %s %s%n", fname, lname);
                        System.out.printf("SSN     : %s%n", ssn);
                        if (divisionName != null) {
                            System.out.printf("Division: %s%n", divisionName);
                        }
                        if (jobTitle != null) {
                            System.out.printf("Title   : %s%n", jobTitle);
                        }
                        System.out.println("---- Pay Statements ----");
                    }

                    System.out.printf("Date: %s | Pay: $%.2f%n",
                            rs.getDate("pay_date"),
                            rs.getDouble("earnings"));
                }

                if (!found) {
                    System.out.println("No history found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printJobTitleReportByMonth(int year, int month) {
        String sql =
                "SELECT jt.job_title, SUM(p.earnings) AS Total " +
                        "FROM job_titles jt " +
                        "JOIN employee_job_titles ejt ON jt.job_title_id = ejt.job_title_id " +
                        "JOIN employees e ON ejt.empid = e.empid " +
                        "JOIN payroll p ON e.empid = p.empid " +
                        "WHERE YEAR(p.pay_date) = ? AND MONTH(p.pay_date) = ? " +
                        "GROUP BY jt.job_title " +
                        "ORDER BY jt.job_title";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n=== JOB TITLE REPORT FOR " +
                        year + "-" + String.format("%02d", month) + " ===");
                while (rs.next()) {
                    String jobTitle = rs.getString("job_title");
                    double total = rs.getDouble("Total");
                    System.out.printf("%-25s : %.2f%n", jobTitle, total);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printDivisionReportByMonth(int year, int month) {
        String sql =
                "SELECT d.Name AS division_name, " +
                        "       SUM(p.earnings) AS total_pay " +
                        "FROM division d " +
                        "JOIN employee_division ed ON d.ID = ed.div_ID " +
                        "JOIN employees e ON ed.empid = e.empid " +
                        "JOIN payroll p ON e.empid = p.empid " +
                        "WHERE YEAR(p.pay_date) = ? AND MONTH(p.pay_date) = ? " +
                        "GROUP BY d.Name " +
                        "ORDER BY d.Name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n=== DIVISION REPORT FOR " +
                        year + "-" + String.format("%02d", month) + " ===");

                boolean hasData = false;

                while (rs.next()) {
                    hasData = true;
                    System.out.printf("%-20s : $%.2f%n",
                            rs.getString("division_name"),
                            rs.getDouble("total_pay"));
                }

                if (!hasData) {
                    System.out.println("No payroll data exists for this month.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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