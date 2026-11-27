package model;

import java.util.List;


public class OrderListResult {
    private final List<Order> orderList;
    private final int currentPage;
    private final int totalPages;


    public OrderListResult(List<Order> orderList, int currentPage, int totalPages) {
        this.orderList = orderList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }


    public List<Order> getOrderList() {
        return orderList;
    }

   
    public int getCurrentPage() {
        return currentPage;
    }

   
    public int getTotalPages() {
        return totalPages;
    }
}