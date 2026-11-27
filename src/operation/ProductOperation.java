package operation;

import model.Product;
import model.ProductListResult;
import org.json.JSONObject;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;

public class ProductOperation {
    private static ProductOperation instance = null;
    private static final String RAW_FILE = "data/raw/product.txt";
    private static final String PRODUCT_FILE = "data/products.txt";

    private ProductOperation() {
    }

    public static ProductOperation getInstance() {
        if (instance == null) {
            instance = new ProductOperation();
        }
        return instance;
    }

    public void extractProductsFromFiles() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(RAW_FILE));
            BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCT_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    writer.write(line.trim());
                    writer.newLine();
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
        }
    }

    private List<Product> readAllProducts() {
        List<Product> list = new ArrayList<>();
        File file = new File(PRODUCT_FILE);
        if (!file.exists()) {
            extractProductsFromFiles();
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    JSONObject obj = new JSONObject(line);
                    Product p = new Product(
                            obj.getString("pro_id"),
                            obj.getString("pro_model"),
                            obj.getString("pro_category"),
                            obj.getString("pro_name"),
                            obj.getDouble("pro_current_price"),
                            obj.getDouble("pro_raw_price"),
                            obj.getDouble("pro_discount"),
                            obj.getInt("pro_likes_count")
                    );
                    list.add(p);
                }
            }
            reader.close();
        } catch (Exception e) {
        }
        return list;
    }
    public ProductListResult getProductList(int pageNumber) {
        List<Product> allProducts = readAllProducts();
        int totalItems = allProducts.size();
        int pageSize = 10;

        if (totalItems == 0) {
            return new ProductListResult(new ArrayList<>(), 0, 0);
        }

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (pageNumber < 1) pageNumber = 1;
        if (pageNumber > totalPages) pageNumber = totalPages;

        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        List<Product> pageList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            pageList.add(allProducts.get(i));
        }

        return new ProductListResult(pageList, pageNumber, totalPages);
    }

    public Product getProductById(String productId) {
        List<Product> allProducts = readAllProducts();
        for (Product p : allProducts) {
            if (p.getProId().equals(productId)) {
                return p;
            }
        }
        return null;
    }

    public List<Product> getProductListByKeyword(String keyword) {
        List<Product> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }
        List<Product> allProducts = readAllProducts();
        String key = keyword.toLowerCase();

        for (Product p : allProducts) {
            if (p.getProName().toLowerCase().contains(key)) {
                result.add(p);
            }
        }
        return result;
    }
    private void saveAllProducts(List<Product> list) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCT_FILE));
            for (Product p : list) {
                writer.write(p.toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
        }
    }

    public boolean deleteProduct(String productId) {
        List<Product> allProducts = readAllProducts();
        boolean found = false;

        for (int i = 0; i < allProducts.size(); i++) {
            if (allProducts.get(i).getProId().equals(productId)) {
                allProducts.remove(i);
                found = true;
                break;
            }
        }

        if (found) {
            saveAllProducts(allProducts);
        }
        return found;
    }

    public void deleteAllProducts() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCT_FILE));
            writer.write("");
            writer.close();
        } catch (IOException e) {
        }
    }
    private void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }
    private void saveChartToFile(Chart chart, String fileName) {
        try {
            File folder = new File("data/figure");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException e) {
            }

            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    chart.setAnimated(false);
                    Scene scene = new Scene(chart, 800, 600);
                    chart.applyCss();
                    chart.layout();

                    WritableImage image = scene.snapshot(null);
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    ImageIO.write(bufferedImage, "png", new File("data/figure/" + fileName));
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
            latch.await();
        } catch (Exception e) {
        }
    }

    public void generateCategoryFigure() {
        initJavaFX();
        List<Product> products = readAllProducts();
        List<String> categories = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        for (Product p : products) {
            String cat = p.getProCategory();
            int index = categories.indexOf(cat);
            if (index == -1) {
                categories.add(cat);
                counts.add(1);
            } else {
                counts.set(index, counts.get(index) + 1);
            }
        }

        for (int i = 0; i < counts.size() - 1; i++) {
            for (int j = i + 1; j < counts.size(); j++) {
                if (counts.get(i) < counts.get(j)) {
                    int tempCount = counts.get(i);
                    counts.set(i, counts.get(j));
                    counts.set(j, tempCount);

                    String tempCat = categories.get(i);
                    categories.set(i, categories.get(j));
                    categories.set(j, tempCat);
                }
            }
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Products");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Total Products by Category");
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < categories.size(); i++) {
            series.getData().add(new XYChart.Data<>(categories.get(i), counts.get(i)));
        }
        barChart.getData().add(series);

        saveChartToFile(barChart, "category_figure.png");
    }

    public void generateDiscountFigure() {
        initJavaFX();
        List<Product> products = readAllProducts();
        int less30 = 0;
        int between30And60 = 0;
        int greater60 = 0;

        for (Product p : products) {
            double d = p.getProDiscount();
            if (d < 30) {
                less30++;
            } else if (d <= 60) {
                between30And60++;
            } else {
                greater60++;
            }
        }

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Product Discount Distribution");
        pieChart.getData().add(new PieChart.Data("< 30% (" + less30 + ")", less30));
        pieChart.getData().add(new PieChart.Data("30% - 60% (" + between30And60 + ")", between30And60));
        pieChart.getData().add(new PieChart.Data("> 60% (" + greater60 + ")", greater60));

        saveChartToFile(pieChart, "discount_figure.png");
    }

    public void generateLikesCountFigure() {
        initJavaFX();
        List<Product> products = readAllProducts();
        List<String> categories = new ArrayList<>();
        List<Integer> totalLikes = new ArrayList<>();

        for (Product p : products) {
            String cat = p.getProCategory();
            int index = categories.indexOf(cat);
            if (index == -1) {
                categories.add(cat);
                totalLikes.add(p.getProLikesCount());
            } else {
                totalLikes.set(index, totalLikes.get(index) + p.getProLikesCount());
            }
        }

        for (int i = 0; i < totalLikes.size() - 1; i++) {
            for (int j = i + 1; j < totalLikes.size(); j++) {
                if (totalLikes.get(i) > totalLikes.get(j)) {
                    int tempLikes = totalLikes.get(i);
                    totalLikes.set(i, totalLikes.get(j));
                    totalLikes.set(j, tempLikes);

                    String tempCat = categories.get(i);
                    categories.set(i, categories.get(j));
                    categories.set(j, tempCat);
                }
            }
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Likes Count");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Total Likes Count by Category");
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < categories.size(); i++) {
            series.getData().add(new XYChart.Data<>(categories.get(i), totalLikes.get(i)));
        }
        barChart.getData().add(series);

        saveChartToFile(barChart, "likes_count_figure.png");
    }

    public void generateDiscountLikesCountFigure() {
        initJavaFX();
        List<Product> products = readAllProducts();

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Discount (%)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Likes Count");

        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Likes Count vs Discount");
        scatterChart.setLegendVisible(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (Product p : products) {
            series.getData().add(new XYChart.Data<>(p.getProDiscount(), p.getProLikesCount()));
        }
        scatterChart.getData().add(series);

        saveChartToFile(scatterChart, "discount_likes_count_figure.png");
    }
}
