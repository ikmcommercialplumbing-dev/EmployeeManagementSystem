import java.sql.*;
import java.util.ArrayList;

public class EmployeeDao implements IEmployeeDao {
    private static final String URL = "jdbc:mysql://localhost:3306/group2erdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Bangster1862";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void searchEmployee(String searchKey) {
        String sql = "SELECT e.*, d.Name as division_name, jt.job_title " +
                "FROM employees e " +
                "LEFT JOIN employee_division ed ON e.empid = ed.empid " +
                "LEFT JOIN division d ON ed.div_ID = d.ID " +
                "LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "LEFT JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
                "WHERE " +
                "e.Fname LIKE ? OR " +
                "e.Lname LIKE ? OR " +
                "CONCAT(e.Fname, ' ', e.Lname) LIKE ? OR " +
                "e.SSN = ? OR " +
                "CAST(e.empid AS CHAR) = ?";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchKey + "%";

            psmt.setString(1, searchPattern);
            psmt.setString(2, searchPattern);
            psmt.setString(3, searchPattern);
            psmt.setString(4, searchKey);
            psmt.setString(5, searchKey);

            try (ResultSet resultSet = psmt.executeQuery()) {
                boolean foundAny = false;

                while (resultSet.next()) {
                    foundAny = true;

                    String firstName = resultSet.getString("Fname");
                    String lastName = resultSet.getString("Lname");
                    int empId = resultSet.getInt("empid");
                    String ssn = resultSet.getString("SSN");
                    String email = resultSet.getString("email");
                    double salary = resultSet.getDouble("Salary");
                    String hireDate = resultSet.getString("HireDate");
                    String division = resultSet.getString("division_name");
                    String jobTitle = resultSet.getString("job_title");

                    System.out.println("--------------------------------------------------");
                    System.out.println("FOUND EMPLOYEE: " + firstName + " " + lastName);
                    System.out.println("ID: " + empId + " | SSN: " + ssn);
                    System.out.println("Email: " + email);
                    System.out.println("Hired: " + hireDate);
                    System.out.printf("Salary: $%.2f%n", salary);
                    System.out.println("Division: " + (division != null ? division : "Not assigned"));
                    System.out.println("Job Title: " + (jobTitle != null ? jobTitle : "Not assigned"));
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

    @Override
    public void updateEmployee(Employee emp) {

        // 1. Load BEFORE data
        String beforeSql = "SELECT Fname, Lname, Email, Salary, SSN, HireDate FROM employees WHERE empid=?";
        String updateSql = "UPDATE employees SET Fname=?, Lname=?, email=?, Salary=?, SSN=?, HireDate=? WHERE empid=?";

        try (Connection conn = getConnection()) {

            // Load BEFORE snapshot
            String oldName = "", oldEmail = "", oldSSN = "", oldHire = "";
            double oldSalary = 0;

            try (PreparedStatement beforeStmt = conn.prepareStatement(beforeSql)) {
                beforeStmt.setInt(1, emp.getID());
                try (ResultSet rs = beforeStmt.executeQuery()) {
                    if (rs.next()) {
                        oldName = rs.getString("Fname") + " " + rs.getString("Lname");
                        oldEmail = rs.getString("Email");
                        oldSalary = rs.getDouble("Salary");
                        oldSSN = rs.getString("SSN");
                        oldHire = rs.getString("HireDate");
                    }
                }
            }

            // 2. Perform UPDATE
            try (PreparedStatement psmt = conn.prepareStatement(updateSql)) {

                psmt.setString(1, emp.getFname());
                psmt.setString(2, emp.getLname());
                psmt.setString(3, emp.getEmail());
                psmt.setDouble(4, emp.getSalary());
                psmt.setString(5, emp.getSSN());
                psmt.setString(6, emp.getHireDate());
                psmt.setInt(7, emp.getID());

                int rowsUpdated = psmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("\n=== EMPLOYEE UPDATED SUCCESSFULLY ===");
                    System.out.println("EMPLOYEE ID: " + emp.getID());

                    System.out.println("\n--- BEFORE ---");
                    System.out.println("Name:   " + oldName);
                    System.out.println("Email:  " + oldEmail);
                    System.out.println("Salary: $" + oldSalary);
                    System.out.println("SSN:    " + oldSSN);
                    System.out.println("Hired:  " + oldHire);

                    System.out.println("\n--- AFTER ---");
                    System.out.println("Name:   " + emp.getFname() + " " + emp.getLname());
                    System.out.println("Email:  " + emp.getEmail());
                    System.out.println("Salary: $" + emp.getSalary());
                    System.out.println("SSN:    " + emp.getSSN());
                    System.out.println("Hired:  " + emp.getHireDate());
                    System.out.println("======================================\n");

                } else {
                    System.out.println(" Update failed: ID " + emp.getID() + " not found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void applySalaryRaise(double percentage, double minSalary, double maxSalary) {

        String beforeSql = "SELECT empid, Fname, Lname, Salary FROM employees WHERE Salary BETWEEN ? AND ?";
        String updateSql = "UPDATE employees SET Salary = Salary * (1 + (? / 100)) WHERE Salary BETWEEN ? AND ?";

        try (Connection conn = getConnection()) {

            System.out.println("\n=== BEFORE RAISE ===");
            try (PreparedStatement beforeStmt = conn.prepareStatement(beforeSql)) {
                beforeStmt.setDouble(1, minSalary);
                beforeStmt.setDouble(2, maxSalary);
                try (ResultSet rs = beforeStmt.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        System.out.printf("ID %d | %-15s | Salary: $%.2f%n",
                                rs.getInt("empid"),
                                rs.getString("Fname") + " " + rs.getString("Lname"),
                                rs.getDouble("Salary"));
                    }
                    if (!found) {
                        System.out.println("No employees in range.");
                        return;
                    }
                }
            }

            // UPDATE
            try (PreparedStatement psmt = conn.prepareStatement(updateSql)) {
                psmt.setDouble(1, percentage);
                psmt.setDouble(2, minSalary);
                psmt.setDouble(3, maxSalary);
                int updated = psmt.executeUpdate();

                // AFTER snapshot
                System.out.println("\n=== AFTER RAISE ===");
                try (PreparedStatement afterStmt = conn.prepareStatement(beforeSql)) {
                    afterStmt.setDouble(1, minSalary);
                    afterStmt.setDouble(2, maxSalary);
                    try (ResultSet rs = afterStmt.executeQuery()) {
                        while (rs.next()) {
                            System.out.printf("ID %d | %-15s | New Salary: $%.2f%n",
                                    rs.getInt("empid"),
                                    rs.getString("Fname") + " " + rs.getString("Lname"),
                                    rs.getDouble("Salary"));
                        }
                    }
                }

                System.out.println("\nEmployees updated: " + updated);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateSalaryBelowThreshold(double newSalary, double threshold) {
        String selectSql = "SELECT empid, Fname, Lname, Salary FROM employees WHERE Salary < ?";
        String updateSql = "UPDATE employees SET Salary = ? WHERE Salary < ?";

        try (Connection conn = getConnection()) {

            // 1. Load BEFORE snapshot
            System.out.println("\n=== BEFORE UPDATE (Employees Below Threshold) ===");
            try (PreparedStatement beforeStmt = conn.prepareStatement(selectSql)) {
                beforeStmt.setDouble(1, threshold);
                try (ResultSet rs = beforeStmt.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        System.out.printf("ID %d | %-15s | Salary: $%.2f%n",
                                rs.getInt("empid"),
                                rs.getString("Fname") + " " + rs.getString("Lname"),
                                rs.getDouble("Salary"));
                    }
                    if (!found) {
                        System.out.println("No employees below $" + threshold);
                        return;
                    }
                }
            }

            try (PreparedStatement psmt = conn.prepareStatement(updateSql)) {
                psmt.setDouble(1, newSalary);
                psmt.setDouble(2, threshold);

                int affected = psmt.executeUpdate();

                System.out.println("\n=== AFTER UPDATE ===");
                try (PreparedStatement afterStmt = conn.prepareStatement(selectSql)) {
                    afterStmt.setDouble(1, newSalary + 1);  // show any that were updated
                    try (ResultSet rs = afterStmt.executeQuery()) {
                        while (rs.next()) {
                            System.out.printf("ID %d | %-15s | New Salary: $%.2f%n",
                                    rs.getInt("empid"),
                                    rs.getString("Fname") + " " + rs.getString("Lname"),
                                    rs.getDouble("Salary"));
                        }
                    }
                }

                System.out.println("\nEmployees updated: " + affected);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int addEmployee(Employee emp) {
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
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

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

    @Override
    public int deleteEmployee(int targetId) throws SQLException {
        String deleteDivisions = "DELETE FROM employee_division WHERE empid = ?";
        String deleteJobs = "DELETE FROM employee_job_titles WHERE empid = ?";
        String deletePayroll = "DELETE FROM payroll WHERE empid = ?";
        String deleteEmployee = "DELETE FROM employees WHERE empid = ?";

        try (Connection conn = getConnection()) {

            try (PreparedStatement psmt = conn.prepareStatement(deleteDivisions)) {
                psmt.setInt(1, targetId);
                psmt.executeUpdate();
            }

            try (PreparedStatement psmt = conn.prepareStatement(deleteJobs)) {
                psmt.setInt(1, targetId);
                psmt.executeUpdate();
            }

            try (PreparedStatement psmt = conn.prepareStatement(deletePayroll)) {
                psmt.setInt(1, targetId);
                psmt.executeUpdate();
            }

            try (PreparedStatement psmt = conn.prepareStatement(deleteEmployee)) {
                psmt.setInt(1, targetId);
                int rowsDeleted = psmt.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println(" Employee ID " + targetId + " and all associated records deleted.");
                } else {
                    System.out.println(" Employee ID " + targetId + " not found.");
                }
                return rowsDeleted;
            }
        } catch (SQLException e) {
            System.err.println("Database Error during deletion: " + e.getMessage());
            throw e;
        }
    }

    public void printOutEmployeeTable() {
        String sql = "SELECT e.empid, e.Fname, e.Lname, e.email, e.Salary, e.SSN, e.HireDate, " +
                "d.Name as DivName, jt.job_title as JobTitle " +
                "FROM employees e " +
                "LEFT JOIN employee_division ed ON e.empid = ed.empid " +
                "LEFT JOIN division d ON ed.div_ID = d.ID " +
                "LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "LEFT JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
                "ORDER BY e.empid ASC";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()) {

            System.out.println("\n=========================================================================================================================================");
            System.out.printf("%-5s | %-20s | %-25s | %-12s | %-11s | %-12s | %-15s | %-15s%n",
                    "ID", "NAME", "EMAIL", "SALARY", "SSN", "HIRED", "DIVISION", "JOB TITLE");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("empid");
                String name = rs.getString("Fname") + " " + rs.getString("Lname");
                String email = rs.getString("email");
                double salary = rs.getDouble("Salary");
                String ssn = rs.getString("SSN");
                String hired = rs.getString("HireDate");

                String division = rs.getString("DivName");
                if (division == null) division = "N/A";

                String job = rs.getString("JobTitle");
                if (job == null) job = "N/A";

                System.out.printf("%-5d | %-20s | %-25s | $%-11.2f | %-11s | %-12s | %-15s | %-15s%n",
                        id, name, email, salary, ssn, hired, division, job);
            }
            System.out.println("=========================================================================================================================================\n");

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

    public void updateEmployeeDivision(int empId, int newDivId) {
        String deleteSql = "DELETE FROM employee_division WHERE empid = ?";
        String insertSql = "INSERT INTO employee_division (empid, div_ID) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                delStmt.setInt(1, empId);
                delStmt.executeUpdate();
            }
            try (PreparedStatement insStmt = conn.prepareStatement(insertSql)) {
                insStmt.setInt(1, empId);
                insStmt.setInt(2, newDivId);
                insStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmployeeJobTitle(int empId, int newJobId) {
        String deleteSql = "DELETE FROM employee_job_titles WHERE empid = ?";
        String insertSql = "INSERT INTO employee_job_titles (empid, job_title_id) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                delStmt.setInt(1, empId);
                delStmt.executeUpdate();
            }
            try (PreparedStatement insStmt = conn.prepareStatement(insertSql)) {
                insStmt.setInt(1, empId);
                insStmt.setInt(2, newJobId);
                insStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

