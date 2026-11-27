package model;

import java.util.List;
import java.util.ArrayList;


public class OrderListResult {
    private List<Order> orderList;
    private int currentPage;
    private int totalPages;


    public OrderListResult() {
        this(new ArrayList<>(), 0, 0);
    }


    public OrderListResult(List<Order> orderList, int currentPage, int totalPages) {
        this.orderList = orderList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }


    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
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
