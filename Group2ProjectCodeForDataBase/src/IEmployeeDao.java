public interface IEmployeeDao {

    void Serach_Employee(String fname);

    void Update_Employee(int id,String setType);


    void applySalaryRaise(Employee salary);

    void getDivsionReport();

    void AddEmployee(Employee employee );

    void GetEmployeeID(int id);
}
