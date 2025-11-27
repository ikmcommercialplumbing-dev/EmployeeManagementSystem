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

    public void setFname(String Newfname){
        this.Fname=Newfname;

    }

    public void setLname(String NewLname){
        this.Lname=NewLname;
    }
    public void  setEmail(String NewEmail){
        this.Email=NewEmail;
    }

    public String getLname(){
        return Lname;
    }

    public String getEmail(){
        return Email;
    }

    public abstract String GetRole();
}
