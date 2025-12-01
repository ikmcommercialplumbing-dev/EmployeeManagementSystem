public interface IReportDao {

    void printEmployeeHistoryReport(int targetId);

    void printJobTitleReport();


    void printDivisionReport();


    void runReport(String sql, String header) ;
}