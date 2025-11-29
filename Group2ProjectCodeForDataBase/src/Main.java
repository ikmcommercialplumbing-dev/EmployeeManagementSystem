import java.util.ArrayList;
import java.util.Scanner;
import java.sql.SQLException;

public class Main {
    private static ArrayList<Employee> employeeInTheSystem;
    private static EmployeeDao employeeDao;
    private static ReportDao reportDao;
    private static LookupDao lookupDao;

    public static void main(String[] args) {
        // Initialize DAOs
        employeeDao = new EmployeeDao();
        reportDao = new ReportDao();
        lookupDao = new LookupDao();

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        try {
            employeeInTheSystem = employeeDao.getAllEmployees();
            System.out.println("Loaded " + employeeInTheSystem.size() + " employees from database.");
        } catch (SQLException e) {
            System.out.println(" Warning: Could not load initial data. Database might be empty or unreachable.");
            employeeInTheSystem = new ArrayList<>();
        }

        System.out.println("=== EMPLOYEE MANAGEMENT SYSTEM ===");

        while (choice != 0) {
            System.out.println("\n-----------------------------------------");
            System.out.println("1. Search Employee (Name, ID, or SSN)");
            System.out.println("2. Update Employee Info");
            System.out.println("3. Apply Salary Raise");
            System.out.println("4. Add New Employee");
            System.out.println("5. Report: Total Pay by Division");
            System.out.println("6. Report: Total Pay by Job Title");
            System.out.println("7. Report: Employee History");
            System.out.println("8. Print All Employees (Table View)");
            System.out.println("9. Delete Employee");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        searchEmployeeUI(scanner);
                        break;

                    case 2:
                        updateEmployeeUI(scanner);
                        break;

                    case 3:
                        applySalaryRaiseUI(scanner);
                        break;

                    case 4:
                        addEmployeeUI(scanner);
                        break;

                    case 5:
                        reportDao.printDivisionReport();
                        break;

                    case 6:
                        reportDao.printJobTitleReport();
                        break;

                    case 7:
                        printEmployeeHistoryUI(scanner);
                        break;

                    case 8:
                        employeeDao.printOutEmployeeTable();
                        break;

                    case 9:
                        deleteEmployeeUI(scanner);
                        break;

                    case 0:
                        System.out.println("Exiting System. Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }

    private static void searchEmployeeUI(Scanner scanner) {
        System.out.println("\n--- SEARCH EMPLOYEE ---");
        employeeDao.printOutEmployeeTable();
        System.out.print("Enter Name, SSN, or ID: ");
        String searchKey = scanner.nextLine();


        if (searchKey == null || searchKey.trim().isEmpty()) {
            System.out.println(" Search criteria cannot be empty. Please enter Name, SSN, or ID.");
            return;
        }

        employeeDao.searchEmployee(searchKey);
    }

    private static void updateEmployeeUI(Scanner scanner) {
        System.out.println("\n--- UPDATE EMPLOYEE ---");
        employeeDao.printOutEmployeeTable();
        System.out.print("Enter Employee ID to Update: ");
        int updateId = scanner.nextInt();
        scanner.nextLine();

        Employee empToUpdate = findInLocalList(updateId);

        if (empToUpdate == null) {
            System.out.println(" Employee not found.");
        } else {
            System.out.println("Editing: " + empToUpdate.getFname() + " " + empToUpdate.getLname());
            boolean updating = true;

            while (updating) {
                int subChoice = getUpdateChoice(scanner);

                switch (subChoice) {
                    case 1:
                        System.out.print("Enter New First Name: ");
                        empToUpdate.setFname(scanner.nextLine());
                        break;
                    case 2:
                        System.out.print("Enter New Last Name: ");
                        empToUpdate.setLname(scanner.nextLine());
                        break;
                    case 3:
                        System.out.print("Enter New Email: ");
                        empToUpdate.setEmail(scanner.nextLine());
                        break;
                    case 4:
                        System.out.print("Enter New Hire Date (YYYY-MM-DD): ");
                        empToUpdate.setHireDate(scanner.nextLine());
                        break;
                    case 5:
                        System.out.print("Enter New Salary: ");
                        empToUpdate.setSalary(getSafeDoubleInput(scanner, ""));
                        break;
                    case 6:
                        System.out.print("Enter New SSN: ");
                        String newSSN = scanner.nextLine();
                        if (isValidSSN(newSSN)) {
                            empToUpdate.setSSN(newSSN);
                        } else {
                            System.out.println(" Invalid SSN format. Must be 9 digits only.");
                            continue;
                        }
                        break;
                    case 7:
                        System.out.println("--- Current Divisions ---");
                        lookupDao.printOutDivision();
                        System.out.print("Enter New Division ID: ");
                        int newDivId = scanner.nextInt();
                        scanner.nextLine();
                        employeeDao.updateEmployeeDivision(updateId, newDivId);
                        System.out.println("Division updated.");
                        break;
                    case 8:
                        System.out.println("--- Current Job Titles ---");
                        lookupDao.printOutJobtitle();
                        System.out.print("Enter New Job Title ID: ");
                        int newJobId = scanner.nextInt();
                        scanner.nextLine();
                        employeeDao.updateEmployeeJobTitle(updateId, newJobId);
                        System.out.println("Job Title updated.");
                        break;
                    case 0:
                        updating = false;
                        break;
                    default:
                        System.out.println("Invalid selection.");
                }

                if (subChoice >= 1 && subChoice <= 6 && subChoice != 0) {
                    employeeDao.updateEmployee(empToUpdate);
                }
            }
        }
    }

    private static void applySalaryRaiseUI(Scanner scanner) {
        System.out.println("\n--- BULK SALARY RAISE ---");
        employeeDao.printOutEmployeeTable();

        double percentage = getSafeDoubleInput(scanner, "Enter Percentage (e.g. 3.2): ");

        if (percentage < 0) {
            System.out.println("Percentage cannot be negative.");
            System.out.println("Percentage cannot be negative.");
            return;
        }

        System.out.println("\nDefine Salary Range:");
        double minSal = getSafeDoubleInput(scanner, "Minimum Salary (0 for none): ");
        double maxSal = getSafeDoubleInput(scanner, "Maximum Salary (e.g. 1000000): ");

        if (minSal > maxSal) {
            System.out.println(" Error: Min salary cannot be higher than Max salary.");
        } else {
            employeeDao.applySalaryRaise(percentage, minSal, maxSal);
        }
    }

    private static void addEmployeeUI(Scanner scanner) {

        System.out.println("\n--- ADD NEW EMPLOYEE ---");
        employeeDao.printOutEmployeeTable();

        System.out.print("Enter First Name: ");
        String fname = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lname = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        double salary = getSafeDoubleInput(scanner, "Enter Salary: ");

        System.out.print("Enter SSN: ");
        String ssn = scanner.nextLine();

        System.out.print("Enter Hire Date (YYYY-MM-DD): ");
        String hireDate = scanner.nextLine();

        Employee newEmployee = new Employee(fname, lname, email, 0, salary, ssn, hireDate);

        int newId = employeeDao.addEmployee(newEmployee);

        if (newId != -1) {
            newEmployee.setId(newId);
            employeeInTheSystem.add(newEmployee);
            System.out.println(" Employee added! ID: " + newId);
            linkingEmployeeUI(scanner, newId);
        } else {
            System.out.println(" Failed to add employee.");
        }
    }

    private static void printEmployeeHistoryUI(Scanner scanner) {

        System.out.print("Enter Employee ID to view history: ");
        int historyId = scanner.nextInt();
        scanner.nextLine();
        reportDao.printEmployeeHistoryReport(historyId);
    }

    private static void deleteEmployeeUI(Scanner scanner) {
        System.out.println("\n--- DELETE EMPLOYEE ---");
        employeeDao.printOutEmployeeTable();

        System.out.print("Enter Employee ID to Delete: ");
        int targetId = scanner.nextInt();
        scanner.nextLine();

        try {
            int rowsDeleted = employeeDao.deleteEmployee(targetId);

            if (rowsDeleted > 0) {
                Employee employeeToDelete = findInLocalList(targetId);
                removeFromLocalList(employeeToDelete);
                System.out.println(" Employee ID " + targetId + " deleted successfully.");
            } else {
                System.out.println(" Error: Employee ID " + targetId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println(" Error deleting employee: " + e.getMessage());
        }
    }

    public static int getUpdateChoice(Scanner scanner) {
        System.out.println("\n--- UPDATE FIELD ---");
        System.out.println("1. First Name");
        System.out.println("2. Last Name");
        System.out.println("3. Email");
        System.out.println("4. Hire Date");
        System.out.println("5. Salary");
        System.out.println("6. SSN");
        System.out.println("7. Division");
        System.out.println("8. Job Title");
        System.out.println("0. Return to Main Menu");
        System.out.print("Select field: ");

        int subChoice = scanner.nextInt();
        scanner.nextLine();
        return subChoice;
    }

    public static void linkingEmployeeUI(Scanner scanner, int targetId) {
        System.out.println("\n--- ASSIGN DIVISION & JOB TITLE ---");

        lookupDao.printOutDivision();
        System.out.print("Enter Division ID from list: ");
        int divisionId = scanner.nextInt();
        scanner.nextLine();
        employeeDao.linkingdivision(targetId, divisionId);

        lookupDao.printOutJobtitle();
        System.out.print("Enter Job Title ID from list: ");
        int jobTitleId = scanner.nextInt();
        scanner.nextLine();
        employeeDao.linkemployejobtitles(targetId, jobTitleId);

        System.out.println("\n Setup Complete for Employee ID " + targetId);
        employeeDao.printOutEmployeeTable();

    }

    private static Employee findInLocalList(int targetId) {
        if (employeeInTheSystem == null) return null;
        for (Employee emp : employeeInTheSystem) {
            if (emp.getID() == targetId) {
                return emp;
            }
        }
        return null;
    }

    private static void removeFromLocalList(Employee emp) {
        if (employeeInTheSystem != null && emp != null) {
            employeeInTheSystem.remove(emp);
            System.out.println("Memory refreshed.");
        }
    }

    public static double getSafeDoubleInput(Scanner scanner, String prompt) {
        while (true) {
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }
            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } else {
                System.out.println(" Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static boolean isValidSSN(String ssn) {
        return ssn != null && ssn.matches("\\d{9}");
    }
}