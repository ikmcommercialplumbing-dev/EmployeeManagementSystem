import java.sql.*;
import java.util.ArrayList;

public class EmployeeDao implements IEmployeeDao {
    // Database Credentials
    private static final String URL = "jdbc:mysql://localhost:3306/group2erdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Bangster1862";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // --- 1. SEARCH ---
    @Override
    public void searchEmployee(String searchKey) {
        String sql = "SELECT * FROM employees WHERE Fname = ? OR Lname = ? OR SSN = ? OR CAST(empid AS CHAR) = ?";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            // Bind the single search key to all 4 possible columns
            psmt.setString(1, searchKey);
            psmt.setString(2, searchKey);
            psmt.setString(3, searchKey);
            psmt.setString(4, searchKey);

            try (ResultSet resultSet = psmt.executeQuery()) {
                boolean foundAny = false;

                while (resultSet.next()) {
                    foundAny = true;
                    System.out.println("--------------------------------------------------");
                    System.out.println("FOUND: " + resultSet.getString("Fname") + " " + resultSet.getString("Lname"));
                    System.out.println("ID: " + resultSet.getInt("empid") + " | SSN: " + resultSet.getString("SSN"));
                    System.out.printf("Salary: $%.2f%n", resultSet.getDouble("Salary"));
                    System.out.println("--------------------------------------------------");
                }

                if (!foundAny) {
                    System.out.println(" No records found for: " + searchKey);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- 2. UPDATE ---
    @Override
    public void updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET Fname=?, Lname=?, email=?, Salary=?, SSN=?, HireDate=? WHERE empid=?";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setString(1, emp.getFname());
            psmt.setString(2, emp.getLname());
            psmt.setString(3, emp.getEmail());
            psmt.setDouble(4, emp.getSalary());
            psmt.setString(5, emp.getSSN());
            psmt.setString(6, emp.getHireDate());
            psmt.setInt(7, emp.getID()); // Where clause

            int rowsUpdated = psmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" Employee updated successfully!");
            } else {
                System.out.println(" Update failed: ID " + emp.getID() + " not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- 3. SALARY RAISE ---
    @Override
    public void applySalaryRaise(double percentage, double lowerBound, double upperBound) {
        String sql = "UPDATE employees SET Salary = Salary * (1.0 + (? / 100.0)) WHERE Salary >= ? AND Salary <= ?";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setDouble(1, percentage);
            psmt.setDouble(2, lowerBound);
            psmt.setDouble(3, upperBound);

            int affected = psmt.executeUpdate();
            System.out.println(" Salary raise applied! Employees affected: " + affected);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//    public Employee getEmployeeById(int id) {
//        String sql = "SELECT * FROM employees WHERE empid = ?";
//
//        try (Connection conn = getConnection();
//             PreparedStatement psmt = conn.prepareStatement(sql)) {
//
//            psmt.setInt(1, id);
//
//            try (ResultSet rs = psmt.executeQuery()) {
//                if (rs.next()) {
//                    return new Employee(
//                            rs.getString("Fname"),
//                            rs.getString("Lname"),
//                            rs.getString("email"),
//                            rs.getInt("empid"),
//                            rs.getDouble("Salary"),
//                            rs.getString("SSN"),
//                            rs.getString("HireDate")
//                    );
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public int deleteEmployee(int targetId) {
        String sql = "DELETE FROM employees WHERE empid = ?";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setInt(1, targetId);
            int rowsDeleted = psmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println(" Employee ID " + targetId + " deleted from database.");
            } else {
                System.out.println(" Employee ID " + targetId + " not found.");
            }
            return rowsDeleted;

        } catch (SQLException e) {
            System.err.println("Database Error during deletion: " + e.getMessage());
            // In a real app, you might check for Foreign Key violations here
            return 0;
        }
    }

    // --- 6. ADD EMPLOYEE (Returns generated ID) ---
    @Override
    public int addEmployee(Employee emp) {
        // NOTE: We do NOT insert 'empid'. The database auto-increments it.
        String sql = "INSERT INTO employees (Fname, Lname, Email, Salary, SSN, HireDate) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            psmt.setString(1, emp.getFname());
            psmt.setString(2, emp.getLname());
            psmt.setString(3, emp.getEmail());
            psmt.setDouble(4, emp.getSalary());
            psmt.setString(5, emp.getSSN());
            psmt.setString(6, emp.getHireDate());

            int affectedRows = psmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = psmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        emp.setId(newId);
                        return newId;
                    }
                }
            }
            return -1; // Failed to retrieve ID

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Failed to insert
        }
    }

    // --- 7. GET ALL (Returns List) ---
    public ArrayList<Employee> getAllEmployees() throws SQLException {
        ArrayList<Employee> employeesList = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet resultSet = psmt.executeQuery()) {

            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getString("Fname"),
                        resultSet.getString("Lname"),
                        resultSet.getString("email"),
                        resultSet.getInt("empid"),
                        resultSet.getDouble("Salary"),
                        resultSet.getString("SSN"),
                        resultSet.getString("HireDate")
                );
                employeesList.add(employee);
            }
            return employeesList;

        } catch (SQLException e) {
            System.err.println("Error reading employee table " + e.getMessage());
            throw e;
        }
    }

    // --- REPORTS ---
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
                while(rs.next()) {
                    found = true;
                    // Simple output logic
                    System.out.printf("Date: %s | Pay: $%.2f%n", rs.getString("pay_date"), rs.getDouble("earnings"));
                }
                if(!found) System.out.println("No history found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printJobTitleReport() {
        String sql = "SELECT jt.job_title, SUM(p.earnings) AS Total FROM job_titles jt " +
                "JOIN employee_job_titles ejt ON jt.job_title_id = ejt.job_title_id " +
                "JOIN employees e ON ejt.empid = e.empid " +
                "JOIN payroll p ON e.empid = p.empid GROUP BY jt.job_title";
        runReport(sql, "JOB TITLE");
    }

    public void printDivisionReport() {
        String sql = "SELECT d.Name, SUM(p.earnings) AS Total FROM division d " +
                "JOIN employee_division ed ON d.ID = ed.div_ID " +
                "JOIN employees e ON ed.empid = e.empid " +
                "JOIN payroll p ON e.empid = p.empid GROUP BY d.Name";
        runReport(sql, "DIVISION");
    }

    private void runReport(String sql, String header) {
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

    public void printOutEmployeeTable() {
        try {
            ArrayList<Employee> list = getAllEmployees();
            for(Employee e : list) {
                System.out.printf("ID: %d | %s %s | SSN: %s%n", e.getID(), e.getFname(), e.getLname(), e.getSSN());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void printOutDivision() {
        String sql = "SELECT ID, Name FROM division ORDER BY ID ASC";
        printOptionList(sql, "DIVISIONS");
    }

    public void printOutJobtitle() {
        String sql = "SELECT job_title_id, job_title FROM job_titles ORDER BY job_title_id ASC";
        printOptionList(sql, "JOB TITLES");
    }

    // Helper to print options
    private void printOptionList(String sql, String title) {
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

    public void linkingdivision(int empid, int divId) {
        String sql = "INSERT INTO employee_division (empid, div_ID) VALUES (?, ?)";
        executeLinkInsert(sql, empid, divId);
    }

    public void linkemployejobtitles(int empid, int jobTitleId) {
        String sql = "INSERT INTO employee_job_titles (empid, job_title_id) VALUES (?, ?)";
        executeLinkInsert(sql, empid, jobTitleId);
    }

    // Helper to avoid duplicate code for linking
    private void executeLinkInsert(String sql, int p1, int p2) {
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, p1);
            psmt.setInt(2, p2);
            psmt.executeUpdate();
            System.out.println(" Link created successfully.");
        } catch (SQLException e) {
            System.out.println(" Linking Error: " + e.getMessage());
        }
    }
}