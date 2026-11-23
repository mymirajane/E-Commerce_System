import org.json.JSONObject;

/**
 * Represents a product in the e-commerce system.
 */
public class Product {
    private String proId;
    private String proModel;
    private String proCategory;
    private String proName;
    private double proCurrentPrice;
    private double proRawPrice;
    private double proDiscount;
    private int proLikesCount;

    /**
     * Constructs a product object with specified attributes.
     *
     * @param proId           Product ID (must be unique)
     * @param proModel        Product model
     * @param proCategory     Product category
     * @param proName         Product name
     * @param proCurrentPrice Current price of the product
     * @param proRawPrice     Original price of the product
     * @param proDiscount     Discount percentage
     * @param proLikesCount   Number of likes
     */
    public Product(String proId, String proModel, String proCategory, String proName,
                   double proCurrentPrice, double proRawPrice, double proDiscount, int proLikesCount) {
        this.proId = proId;
        this.proModel = proModel;
        this.proCategory = proCategory;
        this.proName = proName;
        this.proCurrentPrice = proCurrentPrice;
        this.proRawPrice = proRawPrice;
        this.proDiscount = proDiscount;
        this.proLikesCount = proLikesCount;
    }

    /**
     * Default constructor initializing attributes with default values.
     */
    public Product() {
        this.proId = "";
        this.proModel = "";
        this.proCategory = "";
        this.proName = "";
        this.proCurrentPrice = 0.0;
        this.proRawPrice = 0.0;
        this.proDiscount = 0.0;
        this.proLikesCount = 0;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getProModel() {
        return proModel;
    }

    public void setProModel(String proModel) {
        this.proModel = proModel;
    }

    public String getProCategory() {
        return proCategory;
    }

    public void setProCategory(String proCategory) {
        this.proCategory = proCategory;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public double getProCurrentPrice() {
        return proCurrentPrice;
    }

    public void setProCurrentPrice(double proCurrentPrice) {
        this.proCurrentPrice = proCurrentPrice;
    }

    public double getProRawPrice() {
        return proRawPrice;
    }

    public void setProRawPrice(double proRawPrice) {
        this.proRawPrice = proRawPrice;
    }

    public double getProDiscount() {
        return proDiscount;
    }

    public void setProDiscount(double proDiscount) {
        this.proDiscount = proDiscount;
    }

    public int getProLikesCount() {
        return proLikesCount;
    }

    public void setProLikesCount(int proLikesCount) {
        this.proLikesCount = proLikesCount;
    }

    /**
     * Returns the product information as a formatted JSON string.
     *
     * @return String in JSON-like format
     */
    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("pro_id", this.proId);
        json.put("pro_model", this.proModel);
        json.put("pro_category", this.proCategory);
        json.put("pro_name", this.proName);
        json.put("pro_current_price", this.proCurrentPrice);
        json.put("pro_raw_price", this.proRawPrice);
        json.put("pro_discount", this.proDiscount);
        json.put("pro_likes_count", this.proLikesCount);
        return json.toString();
    }
}