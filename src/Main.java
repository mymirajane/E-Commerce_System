import io.IOInterface;
import model.Admin;
import model.Customer;
import model.User;
import operation.AdminOperation;
import operation.CustomerOperation;
import operation.UserOperation;

public class Main {
    private static final IOInterface io = IOInterface.getInstance();
    private static final UserOperation userOperation = UserOperation.getInstance();
    private static final AdminOperation adminOperation = AdminOperation.getInstance();
    private static final CustomerOperation customerOperation = CustomerOperation.getInstance();

    public static void main(String[] args) {
        AdminOperation.getInstance().registerAdmin();
        runMenu();
    }

    public static void runMenu() {
        while (true) {
            try {
                io.mainMenu();
                String[] choice = io.getUserInput("Enter your choice: ", 1);

                switch (choice[0].trim()) {
                    case "1":
                        handleLogin();
                        break;
                    case "2":
                        io.printMessage("Registration will be implemented next.");
                        break;
                    case "3":
                        io.printMessage("Goodbye!");
                        return;
                    default:
                        io.printErrorMessage("Main.runMenu", "Invalid choice! Please enter 1, 2 or 3.");
                }
            } catch (Exception e) {
                io.printErrorMessage("Main", "Unexpected error " + e.getMessage());
            }
        }
    }

    private static void handleLogin() {
        String[] inputs = io.getUserInput("Enter username and password: ", 2);
        String username = inputs[0].trim();
        String password = inputs[1].trim();

        if (username.isEmpty() || password.isEmpty()) {
            io.printErrorMessage("Login", "Username and password cannot be empty.");
            return;
        }

        User user = userOperation.login(username, password);
        if (user == null) {
            io.printErrorMessage("Login", "Invalid username or password.");
            return;
        }

        io.printMessage("Login successful. Welcome, " + user.getUserName() + "!");
        if (user instanceof Admin) {
            handleAdminMenu((Admin) user);
        } else if (user instanceof Customer) {
            handleCustomerMenu((Customer) user);
        }
    }

    private static void handleAdminMenu(Admin admin) {
        while (true) {
            try {
                io.adminMenu();
                String[] choice = io.getUserInput("Enter your choice: ", 1);
                if ("8".equals(choice[0].trim())) {
                    io.printMessage("Logged out.");
                    return;
                }
                io.printMessage("Admin feature " + choice[0] + " will be connected on Day 5.");
            } catch (Exception e) {
                io.printErrorMessage("AdminMenu", "Error: " + e.getMessage());
            }
        }
    }

    private static void handleCustomerMenu(Customer customer) {
        while (true) {
            try {
                io.customerMenu();
                String[] choice = io.getUserInput("Enter your choice: ", 1);
                if ("6".equals(choice[0].trim())) {
                    io.printMessage("Logged out.");
                    return;
                }
                io.printMessage("Customer feature " + choice[0] + " will be connected on Day 5. ");
            } catch (Exception e) {
                io.printErrorMessage("CustomerMenu", "Error: " + e.getMessage());
            }
        }
    }
}
