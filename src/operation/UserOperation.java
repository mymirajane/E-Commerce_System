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
}
