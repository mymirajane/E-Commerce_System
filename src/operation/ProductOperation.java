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
}