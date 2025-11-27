import java.util.ArrayList;
import java.util.Scanner;
import java.sql.SQLException;

public class Main {
    private static ArrayList<Employee> employeeInTheSystem;

    public static void main(String[] args) {
        EmployeeDao dao = new EmployeeDao();
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        try {
            employeeInTheSystem = dao.getAllEmployees();
            System.out.println("Loaded " + employeeInTheSystem.size() + " employees from database.");
        } catch (SQLException e) {
            System.out.println(" Warning: Could not load initial data. Database might be empty or unreachable.");
            employeeInTheSystem = new ArrayList<>(); // Ensure list is not null
        }

        System.out.println("=== EMPLOYEE MANAGEMENT SYSTEM ===");

        while (choice != 0) {
            System.out.println("\n-----------------------------------------");
            System.out.println("1. Search Employee (Name, ID, or SSN)");
            System.out.println("2. Update Employee Info");
            System.out.println("3. Apply 3.2% Salary Raise");
            System.out.println("4. Add New Employee");
            System.out.println("5. Report: Total Pay by Division");
            System.out.println("6. Report: Total Pay by Job Title");
            System.out.println("7. Report: Employee History");
            System.out.println("8. Print All Employees (Table View)");
            System.out.println("9. Delete Employee"); // Added missing menu item
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            // Safe integer input handling
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear bad input
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        System.out.println("\n--- SEARCH EMPLOYEE ---");
                        System.out.print("Enter Name, SSN, or ID: ");
                        String searchKey = scanner.nextLine();
                        dao.searchEmployee(searchKey);
                        break;

                    case 2: // Update Employee Info
                        System.out.println("\n--- UPDATE EMPLOYEE ---");
                        System.out.print("Enter Employee ID to Update: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine();

                        // Get fresh object from DB to ensure accuracy
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
                                        empToUpdate.setSalary(scanner.nextDouble());
                                        scanner.nextLine();
                                        break;
                                    case 6:
                                        System.out.print("Enter New SSN: ");
                                        empToUpdate.setSSN(scanner.nextLine());
                                        break;
                                    case 0:
                                        updating = false;
                                        break;
                                    default:
                                        System.out.println("Invalid selection.");
                                }

                                if (subChoice != 0) {
                                    dao.updateEmployee(empToUpdate);
                                    // Also update local list to match DB
                                    // (Optional, but keeps memory consistent)
                                    Employee localRef = findInLocalList(updateId);
                                    if (localRef != null) {
                                        // Update local fields... or simpler: reload list later
                                        // For this assignment, db update is the critical part.
                                    }
                                }
                            }
                        }
                        break;

                    case 3:
                        System.out.println("\n--- SALARY RAISE ---");
                        System.out.print("Percentage (e.g. 3.2): ");
                        double percentage = scanner.nextDouble();
                        System.out.print("Lower Bound Salary: ");
                        double lowerBound = scanner.nextDouble();
                        System.out.print("Upper Bound Salary: ");
                        double upperBound = scanner.nextDouble();
                        scanner.nextLine(); // Fix
                        dao.applySalaryRaise(percentage, lowerBound, upperBound);
                        break;

                    case 4:
                        addEmployeeUI(scanner, dao);
                        break;

                    case 5:
                        dao.printDivisionReport();
                        break;

                    case 6:
                        dao.printJobTitleReport();
                        break;

                    case 7:
                        System.out.print("Enter Employee ID to view history: ");
                        int historyId = scanner.nextInt();
                        scanner.nextLine();
                        dao.printEmployeeHistoryReport(historyId);
                        break;

                    case 8:
                        dao.printOutEmployeeTable();
                        break;

                    case 9:
                        System.out.println("\n--- DELETE EMPLOYEE ---");
                        // Show table so they know who to delete
                        dao.printOutEmployeeTable();

                        System.out.print("Enter Employee ID to Delete: ");
                        int targetId = scanner.nextInt();
                        scanner.nextLine();

                        // 1. Delete from Database
                        int rowsDeleted = dao.deleteEmployee(targetId);

                        // 2. If successful, delete from Local Memory
                        if (rowsDeleted > 0) {
                            Employee employeeToDelete = findInLocalList(targetId);
                            removeFromLocalList(employeeToDelete);
                        }
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

    // --- HELPER METHODS ---

    public static int getUpdateChoice(Scanner scanner) {
        System.out.println("\n--- UPDATE FIELD ---");
        System.out.println("1. First Name");
        System.out.println("2. Last Name");
        System.out.println("3. Email");
        System.out.println("4. Hire Date");
        System.out.println("5. Salary");
        System.out.println("6. SSN");
        System.out.println("0. Return to Main Menu");
        System.out.print("Select field: ");

        int subChoice = scanner.nextInt();
        scanner.nextLine();
        return subChoice;
    }

    public static void addEmployeeUI(Scanner scanner, EmployeeDao dao) {
        System.out.println("\n--- ADD NEW EMPLOYEE ---");

        System.out.print("Enter First Name: ");
        String fname = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lname = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter SSN: ");
        String ssn = scanner.nextLine();

        System.out.print("Enter Hire Date (YYYY-MM-DD): ");
        String hireDate = scanner.nextLine();

        // 1. Create with ID 0
        Employee newEmployee = new Employee(fname, lname, email, 0, salary, ssn, hireDate);

        // 2. Insert and Get New ID
        int newId = dao.addEmployee(newEmployee);

        if (newId != -1) {
            // Update the object with the real ID
            newEmployee.setId(newId);

            // Add to local list
            if (employeeInTheSystem != null) {
                employeeInTheSystem.add(newEmployee);
            }

            System.out.println("✅ Employee added! ID: " + newId);

            LinkingEmployeeUI(scanner, dao, newId);
        } else {
            System.out.println(" Failed to add employee.");
        }
    }

    public static void LinkingEmployeeUI(Scanner scanner, EmployeeDao dao, int targetId) {
        System.out.println("\n--- ASSIGN DIVISION & JOB TITLE ---");

        dao.printOutDivision();
        System.out.print("Enter Division ID from list: ");
        int divisionId = scanner.nextInt();
        scanner.nextLine();
        dao.linkingdivision(targetId, divisionId);

        dao.printOutJobtitle();
        System.out.print("Enter Job Title ID from list: ");
        int jobTitleId = scanner.nextInt();
        scanner.nextLine();
        dao.linkemployejobtitles(targetId, jobTitleId);

        System.out.println("\n✅ Setup Complete for Employee ID " + targetId);
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
            System.out.println("memory refreshed.");
        }
    }
}