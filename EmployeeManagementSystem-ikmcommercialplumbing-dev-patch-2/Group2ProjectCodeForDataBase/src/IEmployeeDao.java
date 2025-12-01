import java.sql.SQLException;

public interface IEmployeeDao {

    void searchEmployee(String searchKey) throws SQLException;

    void updateEmployee(Employee emp) throws SQLException;


    void applySalaryRaise(double percentage, double minSalary, double maxSalary);
    int addEmployee(Employee emp) throws SQLException;

    int deleteEmployee(int id) throws SQLException;


//    void printDivisionReport() throws SQLException;
//
//    void printEmployeeHistoryReport(int targetId) throws SQLException;
//
//    void printJobTitleReport() throws SQLException;
}

