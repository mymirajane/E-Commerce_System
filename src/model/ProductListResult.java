package model;

import java.util.ArrayList;
import java.util.List;


public class ProductListResult {
    private List<Product> productList;
    private int currentPage;
    private int totalPages;


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


    public List<Product> getProductList() {
        return productList;
    }


    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }


    public int getCurrentPage() {
        return currentPage;
    }


    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }


    public int getTotalPages() {
        return totalPages;
    }


    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}