package model;

import org.json.JSONObject;


public class Order {
    private String orderId;
    private String userId;
    private String proId;
    private String orderTime;


    public Order(String orderId, String userId, String proId, String orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.proId = proId;
        this.orderTime = orderTime;
    }


    public Order() {
        this.orderId = "";
        this.userId = "";
        this.proId = "";
        this.orderTime = "";
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }


    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("order_id", this.orderId);
        json.put("user_id", this.userId);
        json.put("pro_id", this.proId);
        json.put("order_time", this.orderTime);
        return json.toString();
    }
}
