public abstract class Person{
    protected String Fname;
    protected String Lname;
    protected String Email;
    public Person(String Fname,String Lname,String Email){
        this.Fname=Fname;
        this.Lname=Lname;
        this.Email=Email;
    }
    public String  getFname(){
        return Fname;
    }
    public String getLname(){
        return Lname;
    }

    public String getEmail(){
        return Email;
    }

    public abstract String GetRole(String Role);
}
