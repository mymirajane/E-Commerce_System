package operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import model.CustomerListResult;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.Customer;
import java.io.File;
import java.io.RandomAccessFile;


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
        
        File file = new File("data/users.txt");
        boolean needNewLine = false;
        if (file.exists() && file.length() > 0) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(file.length() -1);
                if (raf.readByte() != '\n') {
                needNewLine = true;
                }
            } catch (IOException e) {
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/users.txt", true))) {
            if (needNewLine) bw.newLine();
            bw.write(customer.toString());
            bw.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public boolean updateProfile(String attributeName, String value, Customer customerObject) {
        if (customerObject == null || attributeName == null || value == null) return false;

        switch (attributeName.toLowerCase()) {
            case "user_name":
            case "username":
               if (!UserOperation.getInstance().validateUsername(value)) return false;
               if (!value.equals(customerObject.getUserName()) && UserOperation.getInstance().checkUsernameExist(value)) return false;
                customerObject.setUserName(value);
                break;

            case "user_password":
            case "password":
                if(!UserOperation.getInstance().validatePassword(value)) return false;
                customerObject.setUserPassword(UserOperation.getInstance().encryptPassword(value));
            break;

            case "user_email":
            case "email":
                if (!validateEmail(value)) return false;
                customerObject.setUserEmail(value);
                break;

            case "user_mobile":
            case "mobile":
                if (!validateMobile(value)) return false;
                customerObject.setUserMobile(value);
                break;
            default:
            return false;    
        }

        List<String> lines = new ArrayList<>();
        boolean updated = false;
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                JSONObject json = new JSONObject(line);
                if (json.optString("user_id").equals(customerObject.getUserId())) {
                    lines.add(customerObject.toString());
                    updated = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (Exception e) {
            return false;
        } 

        if (updated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/users.txt", false))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public boolean deleteCustomer(String customerId) {
        if (customerId == null) return false;
        List<String> remaining = new ArrayList<>();
        boolean deleted = false;
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                JSONObject json = new JSONObject(line);
                if ("customer".equals(json.optString("user_role")) && json.optString("user_id").equals(customerId)) {
                    deleted = true;
                } else {
                    remaining.add(line);
                }
            }
        } catch (Exception e) {
            return false;
        }

        if (deleted) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/users.txt", false))) {
                for (String l : remaining) {
                    bw.write(l);
                    bw.newLine();
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public void deleteAllCustomers() {
        List<String> keepLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                JSONObject json = new JSONObject(line);
                if (!"customer".equals(json.optString("user_role"))) {
                    keepLines.add(line);
                }
            }
        } catch (Exception e) {
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/users.txt", false))) {
            for (String l : keepLines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
        }
    }

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> all = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                JSONObject json = new JSONObject(line);
                if ("customer".equals(json.optString("user_role"))) {
                    all.add(new Customer(json.optString("user_id"), json.optString("user_name"), json.optString("user_password"), json.optString("user_register_time"), json.optString("user_role"),json.optString("user_email"), json.optString("user_mobile")));
                }
            }
        } catch (Exception e) {
        }

        int pageSize = 10;
        int totalPages = (int) Math.ceil(all.size() / (double) pageSize);
        List<Customer> pageList = new ArrayList<>();
        int start = (pageNumber - 1) * pageSize;
        if (pageNumber >= 1 && start < all.size()) {
            int end = Math.min(start + pageSize, all.size());
            pageList = new ArrayList<>(all.subList(start, end));
        }
        return new CustomerListResult(pageList, pageNumber, totalPages);
    }
}
