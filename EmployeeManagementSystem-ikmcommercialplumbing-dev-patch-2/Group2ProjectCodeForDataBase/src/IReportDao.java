public interface IReportDao {

    void printEmployeeHistoryReport(int targetId);

    public void printJobTitleReportByMonth(int year, int month) ;


     void printDivisionReportByMonth(int year, int month) ;


    void runReport(String sql, String header) ;
}