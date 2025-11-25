import java.net.ConnectException;
import java.sql.*;

public class EmployeeDao implements IEmployeeDao {
    private static final String URL = "jdbc:mysql://localhost:3306/group2erdb";
    private static final String user = "root";
    private static final String Password = "Bangster1862";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, user, Password);
    }

    @Override
    public void Serach_Employee(String nameToFind) {
        String sql = "SELECT * FROM employees WHERE Fname = ?";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setString(1, nameToFind);

            // 3. GET RESULTS
            try (ResultSet resultSet = psmt.executeQuery()) {

                boolean foundAny = false;

                while (resultSet.next()) {
                    foundAny = true;

                    int empid = resultSet.getInt("empid");
                    String fName = resultSet.getString("Fname");
                    String lName = resultSet.getString("Lname");
                    String email = resultSet.getString("email");
                    String hireDate = resultSet.getString("HireDate");
                    double salary = resultSet.getDouble("Salary");

                    String ssn = resultSet.getString("SSN");

                    System.out.println("--------------------------------------------------");
                    System.out.println("FOUND EMPLOYEE: " + fName + " " + lName);
                    System.out.println("   ID: " + empid + " | SSN: " + ssn);
                    System.out.println("   Email: " + email);
                    System.out.println("   Hired: " + hireDate);
                    // "%.2f" formats the salary to show 2 decimal places (cents)
                    System.out.printf("   Salary: $%.2f%n", salary);
                    System.out.println("--------------------------------------------------");
                }

                if (!foundAny) {
                    System.out.println("No records found for first name: " + nameToFind);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Update_Employee(int ID, String ColumnName) {
        String SQL = "Update employee SET " + ColumnName + "WHERE empid = ?";
        try {
            Connection conn = getConnection();
            PreparedStatement psmt = conn.prepareStatement(SQL);
            ResultSet resultSet = se

            try {
                psmt.setString(1, ColumnName);

                if ( psmt.setInt(2, ID);)


            }

        } catch (SQLException e) {


        }
    }

    // FIXED 1: Change 'void' to 'Employee' so it can actually return the object
    @Override
    public Employee getEmployeeById(int id) {

        String SQL = "SELECT * FROM employees WHERE empid = ?";

        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(SQL)) {

            psmt.setInt(1, id);

            ResultSet rs = psmt.executeQuery();

            if(rs.next()){
                // FIXED 3: SSN must be getString (to keep leading zeros)
                // Ensure this order matches your Employee constructor exactly!
                return new Employee(
                        rs.getString("Fname"),
                        rs.getString("Lname"),
                        rs.getString("email"),
                        rs.getInt("empid"),
                        rs.getDouble("Salary"),
                        rs.getString("SSN"), // Changed from getInt
                        rs.getString("HireDate")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}