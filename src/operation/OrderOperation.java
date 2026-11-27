package operation;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import model.Order;
import model.OrderListResult;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class OrderOperation {
    private static final String ORDERS_FILE = "data/orders.txt";
    private static final String PRODUCTS_FILE = "data/products.txt";
    private static final String USERS_FILE = "data/users.txt";
    private static final String FIGURE_DIR = "data/figure";
    private static final int PAGE_SIZE = 10;
    private static final int TEST_CUSTOMER_COUNT = 10;
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
    private static final String[] MONTHS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private static OrderOperation instance;
    private static boolean fxInitialized;

    private final Random random = new Random();

    private OrderOperation() {
    }

    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }

    public String generateUniqueOrderId() {
        Set<String> existingIds = new HashSet<>();
        for (Order order : readAllOrders()) {
            existingIds.add(order.getOrderId());
        }

        String candidate;
        do {
            candidate = "o_" + (10000 + random.nextInt(90000));
        } while (existingIds.contains(candidate));
        return candidate;
    }

    public boolean createAnOrder(String customerId, String productId, String createTime) {
        if (customerId == null || customerId.trim().isEmpty()
                || productId == null || productId.trim().isEmpty()) {
            return false;
        }

        String orderTime = createTime == null || createTime.trim().isEmpty()
                ? LocalDateTime.now().format(TIME_FORMATTER)
                : createTime.trim();
        Order order = new Order(generateUniqueOrderId(), customerId.trim(), productId.trim(), orderTime);
        File file = new File(ORDERS_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            writer.write(order.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return false;
        }

        List<Order> orders = readAllOrders();
        boolean removed = orders.removeIf(order -> order.getOrderId().equals(orderId.trim()));
        return removed && writeAllOrders(orders);
    }

    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : readAllOrders()) {
            if (customerId == null || customerId.trim().isEmpty()
                    || order.getUserId().equals(customerId.trim())) {
                filtered.add(order);
            }
        }

        int totalPages = (int) Math.ceil(filtered.size() / (double) PAGE_SIZE);
        if (totalPages == 0) {
            return new OrderListResult(new ArrayList<>(), 1, 0);
        }

        int validPage = Math.max(1, Math.min(pageNumber, totalPages));
        int start = (validPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filtered.size());
        return new OrderListResult(new ArrayList<>(filtered.subList(start, end)), validPage, totalPages);
    }

    public void generateTestOrderData() {
        List<String> productIds = new ArrayList<>(loadProductPriceMap().keySet());
        if (productIds.isEmpty()) {
            throw new IllegalStateException("No products are available for test orders.");
        }

        List<String> customerIds = createTestCustomers();
        if (customerIds.size() != TEST_CUSTOMER_COUNT) {
            throw new IllegalStateException("Could not create all test customers.");
        }
        int year = LocalDateTime.now().getYear();
        for (String customerId : customerIds) {
            int orderCount = 50 + random.nextInt(151);
            for (int i = 0; i < orderCount; i++) {
                String productId = productIds.get(random.nextInt(productIds.size()));
                int month = 1 + random.nextInt(12);
                int day = 1 + random.nextInt(28);
                int hour = random.nextInt(24);
                int minute = random.nextInt(60);
                int second = random.nextInt(60);
                String orderTime = String.format(
                        "%02d-%02d-%04d_%02d:%02d:%02d",
                        day, month, year, hour, minute, second);
                if (!createAnOrder(customerId, productId, orderTime)) {
                    throw new IllegalStateException("Could not save generated order data.");
                }
            }
        }
    }

    public void generateSingleCustomerConsumptionFigure(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
        Map<String, Number> monthlyTotals = createMonthlyConsumptionData(customerId.trim());
        saveBarChart(
                "Monthly Consumption - Customer " + customerId,
                "Month",
                "Total Consumption",
                monthlyTotals,
                FIGURE_DIR + "/single_customer_consumption_" + customerId + ".png");
    }

    public void generateAllCustomersConsumptionFigure() {
        Map<String, Number> monthlyTotals = createMonthlyConsumptionData(null);
        saveBarChart(
                "All Customers Monthly Consumption",
                "Month",
                "Total Consumption",
                monthlyTotals,
                FIGURE_DIR + "/all_customers_consumption.png");
    }

    public void generateAllTop10BestSellersFigure() {
        Map<String, Integer> salesCount = new HashMap<>();
        for (Order order : readAllOrders()) {
            salesCount.put(order.getProId(), salesCount.getOrDefault(order.getProId(), 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedSales = new ArrayList<>(salesCount.entrySet());
        sortedSales.sort(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                .reversed()
                .thenComparing(Map.Entry::getKey));

        Map<String, Number> topProducts = new LinkedHashMap<>();
        int limit = Math.min(10, sortedSales.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = sortedSales.get(i);
            topProducts.put(entry.getKey(), entry.getValue());
        }

        saveBarChart(
                "Top 10 Best-Selling Products",
                "Product ID",
                "Units Sold",
                topProducts,
                FIGURE_DIR + "/top_10_best_sellers.png");
    }

    public void deleteAllOrders() {
        File file = new File(ORDERS_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
            writer.write("");
        } catch (IOException e) {
            throw new IllegalStateException("Could not clear order data.", e);
        }
    }

    private List<String> createTestCustomers() {
        List<String> customerIds = new ArrayList<>();
        CustomerOperation customerOperation = CustomerOperation.getInstance();
        for (int i = 0; i < TEST_CUSTOMER_COUNT; i++) {
            for (int attempt = 0; attempt < 100; attempt++) {
                String userName = "test_customer_" + randomLetters(8);
                String email = userName + "@example.com";
                String mobile = String.format("04%08d", random.nextInt(100_000_000));
                if (customerOperation.registerCustomer(userName, "Order12345", email, mobile)) {
                    String customerId = findCustomerId(userName);
                    if (customerId != null) {
                        customerIds.add(customerId);
                    }
                    break;
                }
            }
        }
        return customerIds;
    }

    private String randomLetters(int length) {
        StringBuilder value = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            value.append((char) ('a' + random.nextInt(26)));
        }
        return value.toString();
    }

    private Map<String, Number> createMonthlyConsumptionData(String customerId) {
        double[] totals = new double[MONTHS.length];
        Map<String, Double> prices = loadProductPriceMap();
        for (Order order : readAllOrders()) {
            if (customerId != null && !order.getUserId().equals(customerId)) {
                continue;
            }
            int monthIndex = extractMonthIndex(order.getOrderTime());
            if (monthIndex >= 0) {
                totals[monthIndex] += prices.getOrDefault(order.getProId(), 0.0);
            }
        }

        Map<String, Number> data = new LinkedHashMap<>();
        for (int i = 0; i < MONTHS.length; i++) {
            data.put(MONTHS[i], totals[i]);
        }
        return data;
    }

    private void saveBarChart(String title, String xLabel, String yLabel,
                              Map<String, Number> data, String outputPath) {
        ensureJavaFxInitialized();
        File outputFile = new File(outputPath);
        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Could not create chart output directory.");
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Exception> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel(xLabel);
                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel(yLabel);

                BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
                chart.setTitle(title);
                chart.setLegendVisible(false);
                chart.setAnimated(false);

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                for (Map.Entry<String, Number> entry : data.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
                chart.getData().add(series);

                Scene scene = new Scene(chart, 900, 600);
                chart.applyCss();
                chart.layout();
                WritableImage image = scene.snapshot(null);
                if (!ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile)) {
                    throw new IOException("PNG writer is unavailable.");
                }
            } catch (Exception e) {
                failure.set(e);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Chart generation was interrupted.", e);
        }
        if (failure.get() != null) {
            throw new IllegalStateException("Could not generate chart: " + title, failure.get());
        }
    }

    private void ensureJavaFxInitialized() {
        synchronized (OrderOperation.class) {
            if (fxInitialized) {
                return;
            }
            try {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(latch::countDown);
                latch.await();
            } catch (IllegalStateException e) {
                fxInitialized = true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("JavaFX startup was interrupted.", e);
            }
            fxInitialized = true;
        }
    }

    private int extractMonthIndex(String orderTime) {
        if (orderTime == null) {
            return -1;
        }
        try {
            return LocalDateTime.parse(orderTime, TIME_FORMATTER).getMonthValue() - 1;
        } catch (RuntimeException e) {
            return -1;
        }
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
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(line);
                    String orderId = json.optString("order_id", "");
                    String userId = json.optString("user_id", "");
                    String productId = json.optString("pro_id", "");
                    String orderTime = json.optString("order_time", "");
                    if (!orderId.isEmpty() && !userId.isEmpty()
                            && !productId.isEmpty() && !orderTime.isEmpty()) {
                        orders.add(new Order(orderId, userId, productId, orderTime));
                    }
                } catch (RuntimeException ignored) {
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return orders;
    }

    private boolean writeAllOrders(List<Order> orders) {
        File file = new File(ORDERS_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
            for (Order order : orders) {
                writer.write(order.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Map<String, Double> loadProductPriceMap() {
        Map<String, Double> prices = new HashMap<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) {
            return prices;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    JSONObject product = new JSONObject(line);
                    String productId = product.optString("pro_id", "");
                    if (!productId.isEmpty() && product.has("pro_current_price")) {
                        prices.put(productId, product.getDouble("pro_current_price"));
                    }
                } catch (RuntimeException ignored) {
                }
            }
        } catch (IOException e) {
            return new HashMap<>();
        }
        return prices;
    }

    private String findCustomerId(String userName) {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    JSONObject user = new JSONObject(line);
                    if ("customer".equals(user.optString("user_role"))
                            && userName.equals(user.optString("user_name"))) {
                        return user.optString("user_id", null);
                    }
                } catch (RuntimeException ignored) {
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
