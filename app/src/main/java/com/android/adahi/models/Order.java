package com.android.adahi.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing an Order.
 * Contains information about the order including customer details and ordered animals.
 * Implements Serializable for Intent passing between Activities.
 */
public class Order implements Serializable {
    private String orderId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String wilaya;
    private String orderType;
    private List<OrderItem> items;
    private double totalPrice;
    private double feeAmount;
    private long orderDate;
    private String status; // e.g., Pending, Confirmed, Delivered
    private String paymentStatus;
    private String checkoutId;
    private String checkoutUrl;

    /**
     * Default constructor required for Firebase
     */
    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = System.currentTimeMillis();
        this.status = "Pending";
        this.paymentStatus = "pending";
    }

    /**
     * Constructor with customer details
     */
    public Order(String customerName, String customerEmail, String customerPhone, String wilaya, String orderType) {
        this();
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.wilaya = wilaya;
        this.orderType = orderType;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getWilaya() {
        return wilaya;
    }

    public void setWilaya(String wilaya) {
        this.wilaya = wilaya;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    /**
     * Calculate total price based on ordered items
     */
    public void calculateTotalPrice() {
        totalPrice = feeAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }

    /**
     * Nested class representing an item in an order
     */
    public static class OrderItem implements Serializable {
        private String animalId;
        private String animalName;
        private int quantity;
        private double pricePerUnit;
        private double subtotal;

        public OrderItem() {
        }

        public OrderItem(String animalId, String animalName, int quantity, double pricePerUnit) {
            this.animalId = animalId;
            this.animalName = animalName;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
            this.subtotal = quantity * pricePerUnit;
        }

        // Getters and Setters
        public String getAnimalId() {
            return animalId;
        }

        public void setAnimalId(String animalId) {
            this.animalId = animalId;
        }

        public String getAnimalName() {
            return animalName;
        }

        public void setAnimalName(String animalName) {
            this.animalName = animalName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.subtotal = quantity * pricePerUnit;
        }

        public double getPricePerUnit() {
            return pricePerUnit;
        }

        public void setPricePerUnit(double pricePerUnit) {
            this.pricePerUnit = pricePerUnit;
            this.subtotal = quantity * pricePerUnit;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }
    }
}
