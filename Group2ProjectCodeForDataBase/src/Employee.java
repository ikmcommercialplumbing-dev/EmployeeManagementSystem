public class Employee extends Person {
    private int id;
    private double salary;
    private String ssn;
    private String hireDate;

    public Employee(String f, String l, String e, int id, double s, String ssn, String date) {
        super(f, l, e);
        this.id = id;
        this.salary = s;
        this.ssn = ssn;
        this.hireDate = date;
    }


    public Employee(String f, String l, String e, double s, String ssn, String date) {
        super(f, l, e);
        this.id = 0;
        this.salary = s;
        this.ssn = ssn;
        this.hireDate = date;
    }

    public int getID() {
        return id;
    }
    public void setId(int id){
        this.id=id;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double newSalary) {
        this.salary = newSalary;
    }

    public String getSSN() {
        return ssn;
    }

    public void setSSN(String newSSN) {
        this.ssn = newSSN;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String newHireDate) {
        this.hireDate = newHireDate;
    }

    @Override
    public String GetRole() {
        return "Full Time Employee";
    }


}