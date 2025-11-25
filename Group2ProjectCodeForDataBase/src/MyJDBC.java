import java.util.Scanner;

public class EmployeeManagementSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            displayMainMenu();
            System.out.print("Enter your choice (0-4): ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleEmployeeReports(scanner);
                    break;
                case 2:
                    handleEmployeeManagement(scanner);
                    break;
                case 3:
                    handlePayrollOperations(scanner);
                    break;
                case 4:
                    handleSystemAdmin(scanner);
                    break;
                case 0:
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }

        } while (choice != 0);

        scanner.close();
    }

    public static void displayMainMenu() {
        System.out.println("\nEMPLOYEE MANAGEMENT SYSTEM - MAIN MENU");
        System.out.println("==========================================");
        System.out.println("1. EMPLOYEE REPORTS");
        System.out.println("2. EMPLOYEE MANAGEMENT");
        System.out.println("3. PAYROLL OPERATIONS");
        System.out.println("4. SYSTEM ADMINISTRATION");
        System.out.println("0. EXIT");
        System.out.println("==========================================");
    }

    public static void handleEmployeeReports(Scanner scanner) {
        int subChoice;
        do {
            System.out.println("\n--- EMPLOYEE REPORTS ---");
            System.out.println("1. Full-time Employee Information with Pay Statement History");
            System.out.println("2. Total Pay for Month by Job Title");
            System.out.println("3. Total Pay for Month by Division");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select report option: ");
            subChoice = scanner.nextInt();
            scanner.nextLine();

            switch (subChoice) {
                case 1:
                    System.out.println("Displaying Full-time Employee Information with Pay Statement History...");
                    // add implementation
                    break;
                case 2:
                    System.out.println("Displaying Total Pay for Month by Job Title...");
                    // add implementation
                    break;
                case 3:
                    System.out.println("Displaying Total Pay for Month by Division...");

                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        } while (subChoice != 0);
    }

    public static void handleEmployeeManagement(Scanner scanner) {
        int subChoice;
        do {
            System.out.println("\n--- EMPLOYEE MANAGEMENT ---");
            System.out.println("1. Search Employee (Name, SSN, Employee ID)");
            System.out.println("2. Update Employee Data");
            System.out.println("3. Add New Employee");
            System.out.println("4. View All Employees");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select management option: ");
            subChoice = scanner.nextInt();
            scanner.nextLine();

            switch (subChoice) {
                case 1:
                    System.out.println("Searching for employee...");
                    // add implementation
                    break;
                case 2:
                    // add implementation
                    break;
                case 3:
                    System.out.println("Adding new employee...");
                    // add implementation
                    break;
                case 4:
                    System.out.println("Viewing all employees...");
                    // add implementation
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        } while (subChoice != 0);
    }

    public static void handlePayrollOperations(Scanner scanner) {
        int subChoice;
        do {
            System.out.println("\n--- PAYROLL OPERATIONS ---");
            System.out.println("1. Update Employee Salaries (Percentage Increase by Range)");
            System.out.println("2. Generate Pay Statements");
            System.out.println("3. View Pay Statement History");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select payroll option: ");
            subChoice = scanner.nextInt();
            scanner.nextLine();

            switch (subChoice) {
                case 1:
                    System.out.println("Updating employee salaries by percentage range...");
                    // add implementation
                    break;
                case 2:
                    System.out.println("Generating pay statements...");
                    // add implementation
                    break;
                case 3:
                    System.out.println("Viewing pay statement history...");
                    // add implementation
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        } while (subChoice != 0);
    }

    public static void handleSystemAdmin(Scanner scanner) {
        int subChoice;
        do {
            System.out.println("\n--- SYSTEM ADMINISTRATION ---");
            System.out.println("1. Database Management");
            System.out.println("2. Add SSN Column to Employee Table");

            System.out.println("0. Back to Main Menu");
            System.out.print("Select admin option: ");
            subChoice = scanner.nextInt();
            scanner.nextLine();

            switch (subChoice) {
                case 1:
                    System.out.println("Database Management...");
                    // add implementation
                    break;
                case 2:
                    System.out.println("Adding SSN column to employee table...");
                    // add implementation
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        } while (subChoice != 0);
    }
}