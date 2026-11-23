package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to hold a paginated list of Product objects along with pagination metadata.
 */
public class ProductListResult {
    private List<Product> productList;
    private int currentPage;
    private int totalPages;

    /**
     * Default constructor initializing attributes with default values.
     */
    public ProductListResult() {
        this.productList = new ArrayList<>();
        this.currentPage = 0;
        this.totalPages = 0;
    }

    /**
     * Constructs a ProductListResult object with specified pagination details.
     *
     * @param productList List of Product objects on the current page
     * @param currentPage Current page number
     * @param totalPages  Total number of pages
     */
    public ProductListResult(List<Product> productList, int currentPage, int totalPages) {
        this.productList = productList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    /**
     * Gets the list of products on the current page.
     *
     * @return List of Product objects
     */
    public List<Product> getProductList() {
        return productList;
    }

    /**
     * Sets the list of products on the current page.
     *
     * @param productList List of Product objects
     */
    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    /**
     * Gets the current page number.
     *
     * @return Current page number
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Sets the current page number.
     *
     * @param currentPage Current page number
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Gets the total number of pages.
     *
     * @return Total number of pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the total number of pages.
     *
     * @param totalPages Total number of pages
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}