package operation;

import model.Order;
import model.OrderListResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderOperation {
    private static OrderOperation instance;
    private static final String ORDERS_FILE = "data/orders.txt";
    private static final DateTimeFormatter TIME_FORMATTER =DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
    private final Random random = new Random();

    private OrderOperation(){
    }
    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }

    public void deleteAllOrders() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ORDERS_FILE, false), StandardCharsets.UTF_8))) {
            writer.write("");
        } catch (IOException e) {
        }
    }
    public void generateTestOrderData() {
        List<String> productIds = loadAvailableProductIds();
        if (productIds.isEmpty()) {
            for (int i = 1; i <= 15; i++) {
                productIds.add("p_" + String.format("%04d", i));
            }
        }

        CustomerOperation customerOp = CustomerOperation.getInstance();
        List<String> testCustomerIds = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String username = "test_user_" + (char) ('a' + i - 1);
            String password = "pass" + i + "234";
            String email = "testuser" + i + "@gmail.com";
            String mobile = "04" + String.format("%08d", 10000000 + i);
            customerOp.registerCustomer(username, password, email, mobile);
        }
        testCustomerIds.addAll(loadCustomerIds());
        int limit = Math.min(10, testCustomerIds.size());

        int year = LocalDateTime.now().getYear();
        for (int i = 0; i < limit; i++) {
            String customerId = testCustomerIds.get(i);
            int numOrders = 50 + random.nextInt(151);

            for (int j = 0; j < numOrders; j++) {
                String proId = productIds.get(random.nextInt(productIds.size()));
                int month = 1 + random.nextInt(12);
                int day = 1 + random.nextInt(28); 
                int hour = random.nextInt(24);
                int minute = random.nextInt(60);
                int second = random.nextInt(60);

                String orderTime = String.format("%02d-%02d-%04d_%02d:%02d:%02d",
                        day, month, year, hour, minute, second);
                createAnOrder(customerId, proId, orderTime);
            }
        }
    }

    private List<String> loadAvailableProductIds() {
        List<String> ids = new ArrayList<>();
        File file = new File("data/products.txt");
        if (!file.exists()) {
            return ids;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String proId = extractJsonValue(line, "pro_id");
                if (proId != null && !proId.isEmpty()) {
                    ids.add(proId);
                }
            }
        } catch (IOException e) {
        }
        return ids;
    }

    private List<String> loadCustomerIds() {
        List<String> ids = new ArrayList<>();
        File file = new File("data/users.txt");
        if (!file.exists()) {
            return ids;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_role\":\"customer\"") || line.contains("\"user_role\": \"customer\"")) {
                    String userId = extractJsonValue(line, "user_id");
                    if (userId != null && !userId.isEmpty()) {
                        ids.add(userId);
                    }
                }
            }
        } catch (IOException e) {
        }
        return ids;
    }

    public String generateUniqueOrderId() {
        Set<String> existingIds = new HashSet<>();
        for (var o : readAllOrders()) {
            existingIds.add(o.getOrderId());
        }

        String candidate;
        do {
            int num = 10000 + random.nextInt(90000);
            candidate = "o_" + num;
        } while (existingIds.contains(candidate));
        return candidate;
    }


    public boolean createAnOrder(String customerId, String productId, String createTime) {
        if (customerId == null || customerId.isEmpty() || productId == null || productId.isEmpty()) {
            return false;
        }
        String orderId = generateUniqueOrderId();
        String timeStr = (createTime == null || createTime.isEmpty())
                ? LocalDateTime.now().format(TIME_FORMATTER)
                : createTime;

        var order = new Order(orderId, customerId, productId, timeStr);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ORDERS_FILE, true), StandardCharsets.UTF_8))) {
            writer.write(order.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteOrder(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            return false;
        }
        List<Order> orders = readAllOrders();
        boolean removed = orders.removeIf(o -> o.getOrderId().equals(orderId));
        if (!removed) {
            return false;
        }
        return writeAllOrders(orders);
    }

    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> allOrders = readAllOrders();
        List<Order> filtered = new ArrayList<>();

        for (Order o : allOrders) {
            if (customerId == null || customerId.isEmpty() || o.getUserId().equals(customerId)) {
                filtered.add(o);
            }
        }

        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) filtered.size() / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }

        int validPage = Math.max(1, Math.min(pageNumber, totalPages));
        int start = (validPage - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());

        List<Order> pageList = (start < filtered.size())
                ? new ArrayList<>(filtered.subList(start, end))
                : new ArrayList<>();

        return new OrderListResult(pageList, validPage, totalPages);
    }

    private List<Order> readAllOrders() {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) {
            return orders;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Order order = parseOrderLine(line);
                    if (order != null) {
                        orders.add(order);
                    }
                }
            }
        } catch (IOException e) {
        }
        return orders;
    }

    private boolean writeAllOrders(List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ORDERS_FILE, false), StandardCharsets.UTF_8))) {
            for (Order o : orders) {
                writer.write(o.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Order parseOrderLine(String line) {
        String orderId = extractJsonValue(line, "order_id");
        String userId = extractJsonValue(line, "user_id");
        String proId = extractJsonValue(line, "pro_id");
        String orderTime = extractJsonValue(line, "order_time");
        if (orderId == null || userId == null || proId == null || orderTime == null) {
            return null;
        }
        return new Order(orderId, userId, proId, orderTime);
    }

    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) {
            return null;
        }
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1) {
            return null;
        }
        return json.substring(start, end);
    }
}
