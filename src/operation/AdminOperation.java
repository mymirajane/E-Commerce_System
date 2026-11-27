package operation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.Admin;


public class AdminOperation {
    private static AdminOperation instance;

    private AdminOperation() {
    }

    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }

    public void registerAdmin() {
        UserOperation userOp = UserOperation.getInstance();
        if (userOp.checkUsernameExist("admin")) {
            return;
        }

        String id = userOp.generateUniqueUserId();
        String encryptedPass = userOp.encryptPassword("admin123");
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));

        Admin admin = new Admin(id, "admin", encryptedPass, timeNow, "admin");

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
            bw.write(admin.toString());
            bw.newLine();
        } catch (IOException e) {
        }
    }
}