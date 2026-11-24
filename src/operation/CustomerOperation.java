package operation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.Customer;

public class CustomerOperation {
    private static CustomerOperation instance;

    private CustomerOperation() {
    }

    public static CustomerOperation getInstance() {
        if (instance == null) {
            instance = new CustomerOperation();
        }
        return instance;
    }

    public boolean validateEmail(String userEmail) {
        return userEmail != null && userEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public boolean validateMobile(String userMobile) {
        return userMobile != null && userMobile.matches("^(04|03)[0-9]{8}$");
    }
    
    public boolean registerCustomer(String userName, String userPassword, String userEmail, String userMobile) {
        UserOperation userOp = UserOperation.getInstance();

        if(!userOp.validateUsername(userName) || !userOp.validatePassword(userPassword) || !validateEmail(userEmail) || !validateMobile(userMobile)) {
            return false;
        }
        if(userOp.checkUsernameExist(userName)) {
            return false;
        }

        String id = userOp.generateUniqueUserId();
        String encryptedPassword = userOp.encryptPassword(userPassword);
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));

        Customer customer = new Customer(id, userName, encryptedPassword, timeNow, "customer", userEmail, userMobile);
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/users.txt", true))) {
            bw.write(customer.toString());
            bw.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
