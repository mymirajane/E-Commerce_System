package operation;

import model.Product;
import model.ProductListResult;
import org.json.JSONObject;
import java.io.*;
import java.util.*;

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
            // Bỏ qua lỗi đọc/ghi file
        }
    }

    // Hàm phụ: Đọc toàn bộ sản phẩm từ data/products.txt thành List<Product>
    private List<Product> readAllProducts() {
        List<Product> list = new ArrayList<>();
        File file = new File(PRODUCT_FILE);
        if (!file.exists() || file.length() == 0) {
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
            // Bỏ qua lỗi
        }
        return list;
    }

    // 2. Lấy danh sách sản phẩm theo trang (mỗi trang 10 món)
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

    // 3. Tìm 1 sản phẩm theo đúng proId
    public Product getProductById(String productId) {
        List<Product> allProducts = readAllProducts();
        for (Product p : allProducts) {
            if (p.getProId().equals(productId)) {
                return p;
            }
        }
        return null;
    }

    // 4. Tìm các sản phẩm có tên chứa từ khóa (không phân biệt hoa thường)
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
}