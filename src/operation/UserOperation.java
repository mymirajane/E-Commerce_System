package operation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import model.Admin;
import model.Customer;
import model.User;
import org.json.JSONObject;

public class UserOperation {
    private static UserOperation instance;

    private UserOperation() {
    }

    public static UserOperation getInstance() {
        if (instance == null) {
            instance = new UserOperation();
        }
        return instance;
    }

    public String generateUniqueUserId() {
        Random random = new Random();
        while (true) {
            StringBuilder sb = new StringBuilder("u_");
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            String newUserId = sb.toString();

            boolean exists = false;
            try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("\"" + newUserId + "\"")) {
                        exists = true;
                        break;
                    }
                }
            } catch (IOException e) {
            }
            if (!exists) {
                return newUserId;
            }
        }
    }

    public String encryptPassword(String userPassword) {
        if (userPassword == null)
            return "";
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < 2 * userPassword.length(); i++) {
            randStr.append(chars.charAt(random.nextInt(chars.length())));
        }

        StringBuilder result = new StringBuilder("^^");
        for (int i = 0; i < userPassword.length(); i++) {
            result.append(randStr.charAt(2 * i));
            result.append(randStr.charAt(2 * i + 1));
            result.append(userPassword.charAt(i));
        }
        result.append("$$");
        return result.toString();
    }

    public String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null || !encryptedPassword.startsWith("^^") || !encryptedPassword.endsWith("$$")
                || encryptedPassword.length() < 4) {
            return "";
        }
        String body = encryptedPassword.substring(2, encryptedPassword.length() - 2);
        StringBuilder result = new StringBuilder();
        for (int i = 2; i < body.length(); i += 3) {
            result.append(body.charAt(i));
        }
        return result.toString();
    }

    public boolean validateUsername(String userName) {
        return userName != null && userName.matches("^[a-zA-Z_]{5,}$");
    }

    public boolean validatePassword(String userPassword) {
            if (userPassword == null || userPassword.length() < 5) {
                return false;
            }
            boolean hasLetter = false;
            boolean hasDigit = false;
            for (int i = 0; i < userPassword.length(); i++) {
            char c = userPassword.charAt(i);

            if (Character.isLetter(c)) {
            hasLetter = true;
               } else if (Character.isDigit(c)) {
               hasDigit = true;
               }
            }
               return hasLetter && hasDigit;
        }

    public boolean checkUsernameExist(String userName) {
        if (userName == null || userName.trim().isEmpty()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                JSONObject json = new JSONObject(line);
                if (json.optString("user_name").equals(userName)) {
                    return true;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    public User login(String userName, String userPassword) {
        if (userName == null || userPassword == null)
            return null;
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                JSONObject json = new JSONObject(line);

                if (json.optString("user_name").equals(userName)) {
                    String encrypted = json.optString("user_password");
                    String decrypted = decryptPassword(encrypted);

                    if (decrypted.equals(userPassword)) {
                        String id = json.optString("user_id");
                        String regTime = json.optString("user_register_time");
                        String role = json.optString("user_role");

                        if ("admin".equals(role)) {
                            return new Admin(id, userName, encrypted, regTime, role);
                        } else {
                            String email = json.optString("user_email");
                            String mobile = json.optString("user_mobile");
                            return new Customer(id, userName, encrypted, regTime, role, email, mobile);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}
