package operation;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

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
        if (userPassword == null) return "";
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
        if (encryptedPassword == null || !encryptedPassword.startsWith("^^") || !encryptedPassword.endsWith("$$") || encryptedPassword.length() < 4) {
            return "";
        }
        String body = encryptedPassword.substring(2, encryptedPassword.length() - 2);
        StringBuilder result = new StringBuilder();
        for (int i = 2; i < body.length(); i += 3) {
            result.append(body.charAt(i));
        }
        return result.toString();
        }
    }
