import io.IOInterface;
import javafx.application.Platform;
import model.Admin;
import model.Customer;
import model.Product;
import model.ProductListResult;
import model.OrderListResult;
import model.Order;
import operation.OrderOperation;
import operation.ProductOperation;
import model.User;
import operation.AdminOperation;
import operation.CustomerOperation;
import operation.UserOperation;
import model.CustomerListResult;
import java.util.ArrayList;
import java.util.List;



public class Main {
    private static final IOInterface io = IOInterface.getInstance();
    private static final UserOperation userOperation = UserOperation.getInstance();
    private static final AdminOperation adminOperation = AdminOperation.getInstance();
    private static final CustomerOperation customerOperation = CustomerOperation.getInstance();
    private static final OrderOperation orderOperation = OrderOperation.getInstance();
    private static final ProductOperation productOperation = ProductOperation.getInstance();

    public static void main(String[] args) {
        adminOperation.registerAdmin();
        runMenu();
    }

    public static void runMenu() {
        while (true) {
            try {
                io.mainMenu();
                String[] choice = io.getUserInput("Enter your choice: ", 1);
                if (choice == null || choice.length < 1) continue;
                switch (choice[0].trim()) {
                    case "1":
                        handleLogin();
                        break;
                    case "2":
                        handleRegister();
                        break;
                    case "3":
                        io.printMessage("Goodbye!");
                        Platform.exit();
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
        if (inputs == null || inputs.length < 2) return;
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
                if (choice == null || choice.length < 1) continue;
                switch(choice[0].trim()) {
                    case "1":
                        handleShowAdminProducts();
                        break;
                    case "4":
                        handleShowOrders(null);
                        break;
                    case "5":
                        orderOperation.generateTestOrderData();
                        io.printMessage("Test customer and order data generated.");
                        break;
                    case "6":
                        handleGenerateAllFigures();
                        break;
                    case "7":
                        handleDeleteAllData();
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
                String[] choice = io.getUserInput("Enter your choice: ", 2);
                if (choice == null || choice.length < 1) continue;
                switch (choice[0].trim()) {
                    case "1":
                        io.printObject(customer);
                        break;
                    case "2":
                        handleUpdateProfile(customer);
                        break;
                    case "3":
                        handleShowProducts(customer, choice[1].trim());
                        break;
                    case "4":
                        handleShowOrders(customer.getUserId());
                        break;
                    case "5":
                        orderOperation.generateSingleCustomerConsumptionFigure(customer.getUserId());
                        io.printMessage("Your consumption figure was saved in data/figure.");
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
        String[] inputs = io.getUserInput("Enter name, pass, email, and mobile (seperated by space): ",4);
        if (inputs == null || inputs.length < 4) return;
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
            io.printErrorMessage("Register", "Invalid password. Must be at least 5 characters with 1 letter and 1 number.");
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
        if (inputs == null || inputs.length < 2) return;
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
                    io.printErrorMessage("UpdateProfile", "Username already exists. Please choose another.");
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
                io.printErrorMessage("UpdateProfile", "Unknown attribute. Choose username, password, email, or mobile.");
                return;
        }

        boolean ok = customerOperation.updateProfile(attr, val, customer);
        if (ok) {
            io.printMessage("Profile updated successfully!");
        } else {
            io.printErrorMessage("UpdateProfile", "Update failed. Invalid attribute, duplicate username, or invalid format.");
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

            String[] nav = io.getUserInput("Enter 'n' for next page, 'p' for previous page, 'b' to go back, or a customer ID to view details: ", 1);
            if (nav == null || nav.length < 1) continue;
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
                    if (pageRes == null || pageRes.getCustomers() == null) continue;
                    for (Customer c : pageRes.getCustomers()) {
                        if (c != null && cmd.equals(c.getUserId())) {
                            found = c;
                            break;
                        }
                    }
                    if (found != null) break;
                }
                if (found != null) {
                    io.printObject(found);
                } else {
                    io.printErrorMessage("ShowCustomers", "Customer ID not found: " + cmd);
                }
            }
        }
    }

    private static void handleShowOrders(String customerId) {
        int page = 1;
        boolean adminView = customerId == null;
        while (true) {
            OrderListResult result = orderOperation.getOrderList(customerId, page);
            if (result == null || result.getOrderList() == null || result.getOrderList().isEmpty()) {
                io.printMessage("No orders found.");
                return;
            }

            io.showList(adminView ? "admin" : "customer", "Order", result.getOrderList(),
                    result.getCurrentPage(), result.getTotalPages());

            String[] input = adminView
                    ? io.getUserInput("Enter n, p, b, or d followed by an order ID to delete: ", 2)
                    : io.getUserInput("Enter n, p, or b: ", 1);
            if (input == null || input.length == 0) {
                continue;
            }

            String command = input[0].trim();
            if ("b".equalsIgnoreCase(command)) {
                return;
            }
            if ("n".equalsIgnoreCase(command)) {
                if (result.getCurrentPage() < result.getTotalPages()) {
                    page = result.getCurrentPage() + 1;
                } else {
                    io.printErrorMessage("ShowOrders", "You are already on the last page.");
                }
                continue;
            }
            if ("p".equalsIgnoreCase(command)) {
                if (result.getCurrentPage() > 1) {
                    page = result.getCurrentPage() - 1;
                } else {
                    io.printErrorMessage("ShowOrders", "You are already on the first page.");
                }
                continue;
            }
            if (adminView && "d".equalsIgnoreCase(command)) {
                String orderId = input[1].trim();
                if (orderId.isEmpty()) {
                    io.printErrorMessage("ShowOrders", "Enter the order ID to delete.");
                } else if (orderOperation.deleteOrder(orderId)) {
                    io.printMessage("Order " + orderId + " deleted.");
                    page = result.getCurrentPage();
                } else {
                    io.printErrorMessage("ShowOrders", "Order ID not found: " + orderId);
                }
                continue;
            }
            io.printErrorMessage("ShowOrders", "Invalid command.");
        }
    }

    private static void handleShowProducts(Customer customer, String keyword) {
        List<Product> products = new ArrayList<>();
        int page = 1;
        if (keyword == null || keyword.isEmpty()) {
            ProductListResult firstPage = productOperation.getProductList(1);
            if (firstPage == null || firstPage.getTotalPages() == 0) {
                io.printMessage("No products found.");
                return;
            }
            for (int currentPage = 1; currentPage <= firstPage.getTotalPages(); currentPage++) {
                ProductListResult result = productOperation.getProductList(currentPage);
                if (result != null && result.getProductList() != null) {
                    products.addAll(result.getProductList());
                }
            }
        } else {
            products = productOperation.getProductListByKeyword(keyword);
        }

        if (products == null || products.isEmpty()) {
            io.printMessage("No products found.");
            return;
        }

        int totalPages = (int) Math.ceil(products.size() / 10.0);
        while (true) {
            page = Math.max(1, Math.min(page, totalPages));
            int start = (page - 1) * 10;
            int end = Math.min(start + 10, products.size());
            List<Product> pageProducts = products.subList(start, end);
            io.showList("customer", "Product", pageProducts, page, totalPages);
            String[] input = io.getUserInput("Enter n, p, b, or a product ID to purchase: ", 1);
            String command = input[0].trim();

            if ("b".equalsIgnoreCase(command)) {
                return;
            }
            if ("n".equalsIgnoreCase(command)) {
                if (page < totalPages) {
                    page++;
                } else {
                    io.printErrorMessage("ShowProducts", "You are already on the last page.");
                }
                continue;
            }
            if ("p".equalsIgnoreCase(command)) {
                if (page > 1) {
                    page--;
                } else {
                    io.printErrorMessage("ShowProducts", "You are already on the first page.");
                }
                continue;
            }

            Product selected = null;
            for (Product product : pageProducts) {
                if (product.getProId().equalsIgnoreCase(command)) {
                    selected = product;
                    break;
                }
            }
            if (selected == null) {
                io.printErrorMessage("ShowProducts", "Product ID is not on this page: " + command);
            } else if (orderOperation.createAnOrder(customer.getUserId(), selected.getProId(), null)) {
                io.printMessage("Order placed for " + selected.getProName() + ".");
            } else {
                io.printErrorMessage("ShowProducts", "Could not place the order.");
            }
        }
    }

    private static void handleShowAdminProducts() {
        int page = 1;
        while (true) {
            ProductListResult result = productOperation.getProductList(page);
            if (result == null || result.getProductList() == null || result.getProductList().isEmpty()) {
                io.printMessage("No products found.");
                return;
            }

            page = result.getCurrentPage();
            io.showList("admin", "Product", result.getProductList(), page, result.getTotalPages());
            String[] input = io.getUserInput("Enter n, p, b, or d followed by a product ID to delete: ", 2);
            String command = input[0].trim();

            if ("b".equalsIgnoreCase(command)) {
                return;
            }
            if ("n".equalsIgnoreCase(command)) {
                if (page < result.getTotalPages()) {
                    page++;
                } else {
                    io.printErrorMessage("ShowProducts", "You are already on the last page.");
                }
                continue;
            }
            if ("p".equalsIgnoreCase(command)) {
                if (page > 1) {
                    page--;
                } else {
                    io.printErrorMessage("ShowProducts", "You are already on the first page.");
                }
                continue;
            }
            if ("d".equalsIgnoreCase(command)) {
                String productId = input[1].trim();
                if (productId.isEmpty()) {
                    io.printErrorMessage("ShowProducts", "Enter the product ID to delete.");
                } else if (productOperation.deleteProduct(productId)) {
                    io.printMessage("Product " + productId + " deleted.");
                } else {
                    io.printErrorMessage("ShowProducts", "Product ID not found: " + productId);
                }
                continue;
            }
            io.printErrorMessage("ShowProducts", "Invalid command.");
        }
    }

    private static void handleGenerateAllFigures() {
        try {
            productOperation.generateCategoryFigure();
            productOperation.generateDiscountFigure();
            productOperation.generateLikesCountFigure();
            productOperation.generateDiscountLikesCountFigure();
            CustomerListResult customers = customerOperation.getCustomerList(1);
            if (customers != null) {
                for (int page = 1; page <= customers.getTotalPages(); page++) {
                    CustomerListResult customerPage = page == 1
                            ? customers
                            : customerOperation.getCustomerList(page);
                    if (customerPage == null || customerPage.getCustomers() == null) {
                        continue;
                    }
                    for (Customer customer : customerPage.getCustomers()) {
                        orderOperation.generateSingleCustomerConsumptionFigure(customer.getUserId());
                    }
                }
            }
            orderOperation.generateAllCustomersConsumptionFigure();
            orderOperation.generateAllTop10BestSellersFigure();
            io.printMessage("Statistical figures were saved in data/figure.");
        } catch (Exception e) {
            io.printErrorMessage("GenerateFigures", "Could not generate figures: " + e.getMessage());
        }
    }

    private static void handleDeleteAllData() {
        String[] confirmation = io.getUserInput("Type DELETE to remove all customer, product, and order data: ", 1);
        if (confirmation.length == 0 || !"DELETE".equals(confirmation[0])) {
            io.printMessage("Delete all data cancelled.");
            return;
        }

        try {
            customerOperation.deleteAllCustomers();
            productOperation.deleteAllProducts();
            orderOperation.deleteAllOrders();
            io.printMessage("All customer, product, and order data deleted.");
        } catch (Exception e) {
            io.printErrorMessage("DeleteAllData", "Could not delete all data: " + e.getMessage());
        }
    }
}

    

