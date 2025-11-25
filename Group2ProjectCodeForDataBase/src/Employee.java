public class Employee extends Person{
    private int ID;
    private double Salary;
    private String SSN;
    private String HireDate;


    public Employee(String Fname,String Lname,String Email,int ID,double Salary,String SSN,String Hiredate){
        super(Fname,Lname,Email);
        this.ID=ID;
        this.Salary=Salary;
        this.SSN=SSN;
        this.HireDate=Hiredate;
    }

    public int getID(){
        return ID;
    }
    public double getSalary(){
        return Salary;

    }
    public String SSN(){
        return SSN;
    }

    public String getHireDate(){
        return HireDate;
    }

    @Override
    public String GetRole(String Role) {
        return Fname + "Role is "+ Role;
    }
}
