import io.IOInterface;
import model.Admin;
import model.Customer;
import model.User;
import operation.AdminOperation;
import operation.CustomerOperation;
import operation.UserOperation;
import model.CustomerListResult;
import model.Product;
import model.ProductListResult;
import operation.ProductOperation;

    public class Main {

    private static final IOInterface io = IOInterface.getInstance();
    private static final ProductOperation productOperation = ProductOperation.getInstance();
    private static final UserOperation userOperation = UserOperation.getInstance();
    private static final AdminOperation adminOperation = AdminOperation.getInstance();
    private static final CustomerOperation customerOperation = CustomerOperation.getInstance();

    public static void main(String[] args) {
        adminOperation.registerAdmin();
        runMenu();
    }

    public static void runMenu() {
        while (true) {
            try {
                io.mainMenu();
                String[] choice = io.getUserInput("Enter your choice: ", 1);
                if (choice == null || choice.length < 1)
                    continue;
                switch (choice[0].trim()) {
                    case "1":
                        handleLogin();
                        break;
                    case "2":
                        handleRegister();
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
        if (inputs == null || inputs.length < 2)
            return;
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
                if (choice == null || choice.length < 1)
                    continue;
                switch (choice[0].trim()) {
                    case "1":
                        handleShowProducts("admin");
                        break;
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                        io.printMessage("Feature " + choice[0] + " will be connected by team.");
                        break;
                    case "2":
                        handleRegister();
                        break;
                    case "3":
                        handleShowCustomers();
                        break;
                    case "8":
                        io.printMessage("Logged out.");
                        return;
                    default:
                        io.printErrorMessage("AdminMenu", "Invalid choice. Please enter 1 to 8.");
                }
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
                if (choice == null || choice.length < 1)
                    continue;
                switch (choice[0].trim()) {
                    case "1":
                        io.printObject(customer);
                        break;
                    case "2":
                        handleUpdateProfile(customer);
                        break;
                    case "3":
                        handleShowProducts("Customer");
                    case "4":
                    case "5":
                        io.printMessage("Feature " + choice[0] + " will be connected by team.");
                        break;
                    case "6":
                        io.printMessage("Logged out.");
                        return;
                    default:
                        io.printErrorMessage("CustomerMenu", "Invalid choice. Please enter 1 to 6.");
                }

            } catch (Exception e) {
                io.printErrorMessage("CustomerMenu", "Error: " + e.getMessage());
            }
        }
    }

    private static void handleRegister() {
        String[] inputs = io.getUserInput("Enter name, pass, email, and mobile (seperated by space): ", 4);
        if (inputs == null || inputs.length < 4)
            return;
        String name = inputs[0].trim();
        String pass = inputs[1].trim();
        String email = inputs[2].trim();
        String mobile = inputs[3].trim();

        if (!userOperation.validateUsername(name)) {
            io.printErrorMessage("Register", "Invalid username. Use at least 5 letters or underscores.");
            return;
        }
        if (userOperation.checkUsernameExist(name)) {
            io.printErrorMessage("Register", "Username already exists. Please choose another.");
            return;
        }

        if (!userOperation.validatePassword(pass)) {
            io.printErrorMessage("Register",
                    "Invalid password. Must be at least 5 characters with 1 letter and 1 number.");
            return;
        }

        if (!customerOperation.validateEmail(email)) {
            io.printErrorMessage("Register", "Invalid email format (e.g., user@example.com).");
            return;
        }

        if (!customerOperation.validateMobile(mobile)) {
            io.printErrorMessage("Register", "Invalid mobile. Must be 10 digits starting with 03 or 04.");
            return;
        }

        boolean success = customerOperation.registerCustomer(name, pass, email, mobile);
        if (success) {
            io.printMessage("Registration successful! You can now log in.");
        } else {
            io.printErrorMessage("Register", "Registration failed. Please check your inputs.");
        }
    }

    private static void handleUpdateProfile(Customer customer) {
        String[] inputs = io.getUserInput("Enter attribute (username/password/email/mobile) and new value: ", 2);
        if (inputs == null || inputs.length < 2)
            return;
        String attr = inputs[0].trim();
        String val = inputs[1].trim();

        if (attr.isEmpty() || val.isEmpty()) {
            io.printErrorMessage("UpdateProfile", "Attribute and new value cannot be empty.");
            return;
        }

        switch (attr.toLowerCase()) {
            case "user_name":
            case "username":
                if (!userOperation.validateUsername(val)) {
                    io.printErrorMessage("UpdateProfile", "Invalid username. Use at least 5 letters or underscores.");
                    return;
                }
                if (!val.equals(customer.getUserName()) && userOperation.checkUsernameExist(val)) {
                    io.printErrorMessage("UpdateProfile", "Username already exists. please choose another.");
                    return;
                }
                break;

            case "user_password":
            case "password":
                if (!userOperation.validatePassword(val)) {
                    io.printErrorMessage("UpdateProfile",
                            "Invalid password. Must be at least 5 characters with 1 letter and 1 number.");
                    return;
                }
                break;

            case "user_email":
            case "email":
                if (!customerOperation.validateEmail(val)) {
                    io.printErrorMessage("UpdateProfile", "Invalid email format (e.g., user@example.com).");
                    return;
                }
                break;

            case "user_mobile":
            case "mobile":
                if (!customerOperation.validateMobile(val)) {
                    io.printErrorMessage("UpdateProfile", "Invalid mobile. Must be 10 digits starting with 03 or 04.");
                    return;
                }
                break;
            default:
                io.printErrorMessage("UpdateProfile",
                        "Unknown attribute. Choose username, password, email. or mobile.");
                return;
        }

        boolean ok = customerOperation.updateProfile(attr, val, customer);
        if (ok) {
            io.printMessage("Profile updated successfully!");
        } else {
            io.printErrorMessage("UpdateProfile",
                    "Update failed. Invalid attribute, duplicate username, or invalid format.");
        }
    }

    private static void handleShowCustomers() {
        int page = 1;
        while (true) {
            CustomerListResult result = customerOperation.getCustomerList(page);
            if (result == null || result.getCustomers() == null || result.getCustomers().isEmpty()) {
                io.printMessage("No customers found.");
                return;
            }

            io.showList("admin", "Customer", result.getCustomers(), result.getCurrentPage(), result.getTotalPages());

            String[] nav = io.getUserInput(
                    "Enter 'n' for next page, 'p' for previous page, 'b' to go back, or a customer ID to view details: ",
                    1);
            if (nav == null || nav.length < 1)
                continue;
            String cmd = nav[0].trim();

            if ("b".equalsIgnoreCase(cmd)) {
                return;
            } else if ("n".equalsIgnoreCase(cmd)) {
                if (page < result.getTotalPages()) {
                    page++;
                } else {
                    io.printErrorMessage("ShowCustomers", "You are already on the last page.");
                }
            } else if ("p".equalsIgnoreCase(cmd)) {
                if (page > 1) {
                    page--;
                } else {
                    io.printErrorMessage("ShowCustomers", "You are already on the first page.");
                }
            } else {

                Customer found = null;
                for (int p = 1; p <= result.getTotalPages(); p++) {
                    CustomerListResult pageRes = customerOperation.getCustomerList(p);
                    if (pageRes == null || pageRes.getCustomers() == null)
                        continue;
                    for (Customer c : pageRes.getCustomers()) {
                        if (c != null && cmd.equals(c.getUserId())) {
                            found = c;
                            break;
                        }
                    }
                    if (found != null)
                        break;
                }
                if (found != null) {
                    io.printObject(found);
                } else {
                    io.printErrorMessage("ShowCustomers", "Customer ID not found: " + cmd);
                }
            }
        }

    }
    private static void handleShowProducts(String userRole) {
        int page = 1;
        while (true) {
            ProductListResult result = productOperation.getProductList(page);
            if (result == null || result.getProductList() == null || result.getProductList().isEmpty()) {
                io.printMessage("No products found.");
                return;
            }

            io.showList(userRole, "Product", result.getProductList(), result.getCurrentPage(), result.getTotalPages());

            String[] nav = io.getUserInput("Enter 'n' for next page, 'p' for previous page, 'b' to go back, or a product ID to view details: ", 1);
            if (nav == null || nav.length < 1) continue;
            String cmd = nav[0].trim();

            if ("b".equalsIgnoreCase(cmd)) {
                return;
            } else if ("n".equalsIgnoreCase(cmd)) {
                if (page < result.getTotalPages()) {
                    page++;
                } else {
                    io.printErrorMessage("ShowProducts", "You are already on the last page.");
                }
            } else if ("p".equalsIgnoreCase(cmd)) {
                if (page > 1) {
                    page--;
                } else {
                    io.printErrorMessage("ShowProducts", "You are already on the first page.");
                }
            } else {
                Product found = productOperation.getProductById(cmd);
                if (found != null) {
                    io.printObject(found);
                } else {
                    io.printErrorMessage("ShowProducts", "Product ID not found: " + cmd);
                }
            }
        }
    }
}

