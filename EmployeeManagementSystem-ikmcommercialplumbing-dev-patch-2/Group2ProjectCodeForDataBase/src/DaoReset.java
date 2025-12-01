import java.sql.*;

public class DaoReset {

    private static final String URL = "jdbc:mysql://localhost:3306/group2erdb?allowMultiQueries=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Bangster1862";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void resetDatabase() {

        System.out.println("\n=== RESETTING DATABASE — PLEASE WAIT ===");

        String schemaSQL = """
            SET FOREIGN_KEY_CHECKS = 0;
            DROP TABLE IF EXISTS employee_job_titles;
            DROP TABLE IF EXISTS employee_division;
            DROP TABLE IF EXISTS payroll;
            DROP TABLE IF EXISTS employees;
            DROP TABLE IF EXISTS division;
            DROP TABLE IF EXISTS job_titles;
            SET FOREIGN_KEY_CHECKS = 1;

            CREATE TABLE division (
                ID INT PRIMARY KEY AUTO_INCREMENT,
                Name VARCHAR(100) NOT NULL,
                addressLine1 VARCHAR(100),
                addressLine2 VARCHAR(100),
                city VARCHAR(50),
                state VARCHAR(50),
                country VARCHAR(50),
                postalCode VARCHAR(20)
            );

            CREATE TABLE job_titles (
                job_title_id INT PRIMARY KEY AUTO_INCREMENT,
                job_title VARCHAR(100) NOT NULL
            );

            CREATE TABLE employees (
                empid INT PRIMARY KEY AUTO_INCREMENT,
                Fname VARCHAR(50) NOT NULL,
                Lname VARCHAR(50) NOT NULL,
                Email VARCHAR(100) UNIQUE,
                Salary DECIMAL(10,2) NOT NULL,
                SSN VARCHAR(9) UNIQUE NOT NULL,
                HireDate DATE NOT NULL
            );

            CREATE TABLE employee_division (
                empid INT,
                div_ID INT,
                PRIMARY KEY (empid, div_ID),
                FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
                FOREIGN KEY (div_ID) REFERENCES division(ID) ON DELETE CASCADE
            );

            CREATE TABLE employee_job_titles (
                empid INT,
                job_title_id INT,
                PRIMARY KEY (empid, job_title_id),
                FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE,
                FOREIGN KEY (job_title_id) REFERENCES job_titles(job_title_id) ON DELETE CASCADE
            );

            CREATE TABLE payroll (
                payroll_id INT PRIMARY KEY AUTO_INCREMENT,
                empid INT NOT NULL,
                pay_date DATE NOT NULL,
                earnings DECIMAL(10,2) NOT NULL,
                fed_tax DECIMAL(10,2),
                fed_med DECIMAL(10,2),
                fed_SS DECIMAL(10,2),
                state_tax DECIMAL(10,2),
                retire_401k DECIMAL(10,2),
                health_care DECIMAL(10,2),
                FOREIGN KEY (empid) REFERENCES employees(empid) ON DELETE CASCADE
            );

            INSERT INTO division (Name, addressLine1, city, state, country, postalCode) VALUES
            ('Engineering', '101 Tech Drive', 'Atlanta', 'GA', 'USA', '30303'),
            ('Sales', '45 Commercial Blvd', 'New York', 'NY', 'USA', '10001'),
            ('Human Resources', '808 People St', 'Chicago', 'IL', 'USA', '60601'),
            ('Executive', '1 Executive Tower', 'San Francisco', 'CA', 'USA', '94105');

            INSERT INTO job_titles (job_title) VALUES
            ('Software Engineer'),
            ('Senior Developer'),
            ('Sales Manager'),
            ('HR Specialist'),
            ('CEO');

            INSERT INTO employees (Fname, Lname, Email, HireDate, Salary, SSN) VALUES
            ('John', 'Doe', 'john.doe@tech.com', '2022-01-15', 60000.00, '111223333'),
            ('Jane', 'Smith', 'jane.smith@tech.com', '2021-03-10', 120000.00, '444556666'),
            ('Bob', 'Brown', 'bob.brown@tech.com', '2023-06-01', 45000.00, '777889999'),
            ('Alice', 'Wonderland', 'alice.w@tech.com', '2020-05-20', 85000.00, '999001111'),
            ('Tony', 'Stark', 'tony@stark.com', '2019-01-01', 250000.00, '123456789');

            INSERT INTO employee_division VALUES
            (1, 1), (2, 1), (3, 2), (4, 3), (5, 4);

            INSERT INTO employee_job_titles VALUES
            (1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

            INSERT INTO payroll (pay_date, earnings, empid) VALUES
            ('2023-10-31', 5000.00, 1),
            ('2023-09-30', 5000.00, 1),
            ('2023-08-31', 5000.00, 1),

            ('2023-10-31', 10000.00, 2),
            ('2023-09-30', 10000.00, 2),
            ('2023-08-31', 10000.00, 2),

            ('2023-10-31', 3750.00, 3),
            ('2023-09-30', 3750.00, 3),
            ('2023-08-31', 3750.00, 3),

            ('2023-10-31', 7083.33, 4),
            ('2023-09-30', 7083.33, 4),
            ('2023-08-31', 7083.33, 4),

            ('2023-10-31', 20833.33, 5),
            ('2023-09-30', 20833.33, 5),
            ('2023-08-31', 20833.33, 5);
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(schemaSQL);

            System.out.println("\n=== RESET COMPLETE ===");
            System.out.println("✓ All tables dropped");
            System.out.println("✓ New tables created");
            System.out.println("✓ Sample data reinserted");
            System.out.println("=====================================\n");

        } catch (SQLException e) {
            System.out.println("RESET ERROR: " + e.getMessage());
        }
    }
}
