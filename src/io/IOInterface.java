package io;

import java.util.List;
import java.util.Scanner;


public class IOInterface {
    private static IOInterface instance;
    private final Scanner scanner;

    private IOInterface() {
        this.scanner = new Scanner(System.in);
    }

 
    public static IOInterface getInstance() {
        if (instance == null) {
            instance = new IOInterface();
        }
        return instance;
    }

    public String[] getUserInput(String message, int numOfArgs) {
        System.out.print(message);
        String line = "";
        if (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
        }

        String[] result = new String[numOfArgs];
        for (int i = 0; i < numOfArgs; i++) {
            result[i] = "";
        }

        if (!line.isEmpty()) {
            String[] parts = line.split("\\s+");
            int limit = Math.min(parts.length, numOfArgs);
            for (int i = 0; i < limit; i++) {
                result[i] = parts[i];
            }
        }
        return result;
    }

    public void mainMenu() {
        System.out.println("====== E-Commerce System ======");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Quit");
        System.out.println("===============================");
    }

    public void adminMenu() {
        System.out.println("====== Admin Menu ======");
        System.out.println("1. Show products");
        System.out.println("2. Add customers");
        System.out.println("3. Show customers");
        System.out.println("4. Show orders");
        System.out.println("5. Generate test data");
        System.out.println("6. Generate all statistical figures");
        System.out.println("7. Delete all data");
        System.out.println("8. Logout");
        System.out.println("========================");
    }

    public void customerMenu() {
        System.out.println("====== Customer Menu ======");
        System.out.println("1. Show profile");
        System.out.println("2. Update profile");
        System.out.println("3. Show products (enter \"3\" or \"3 keyword\")");
        System.out.println("4. Show history orders");
        System.out.println("5. Generate all consumption figures");
        System.out.println("6. Logout");
        System.out.println("===========================");
    }

    
    public void showList(String userRole, String listType, List<?> objectList,
                         int pageNumber, int totalPages) {
        System.out.println("------ " + listType + " List (Page " + pageNumber
                + "/" + totalPages + ") [" + userRole + "] ------");
        if (objectList == null || objectList.isEmpty()) {
            System.out.println("No " + listType.toLowerCase() + " records found.");
        } else {
            for (int i = 0; i < objectList.size(); i++) {
                System.out.println((i + 1) + ". " + objectList.get(i).toString());
            }
        }
        System.out.println("Page " + pageNumber + " of " + totalPages);
        System.out.println("Enter 'n' for next page, 'p' for previous page, or 'b' to go back.");
    }

    public void printErrorMessage(String errorSource, String errorMessage) {
        System.out.println("[Error in " + errorSource + "]: " + errorMessage);
    }

   
    public void printMessage(String message) {
        System.out.println(message);
    }

   
    public void printObject(Object targetObject) {
        if (targetObject == null) {
            System.out.println("No object data available.");
        } else {
            System.out.println(targetObject.toString());
        }
    }
}