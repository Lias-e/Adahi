package com.android.adahi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.adahi.models.Order;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * LocalStorageManager handles all local data persistence using SharedPreferences.
 * Manages:
 * - Order storage and retrieval
 * - Order history
 * - User preferences
 */
public class LocalStorageManager {

    private static final String TAG = "LocalStorageManager";
    private static final String PREFS_NAME = "AdahiAppPrefs";
    private static final String ORDERS_KEY = "orders";
    private static final String LAST_ORDER_ID = "last_order_id";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    /**
     * Constructor - Initialize SharedPreferences and Gson
     */
    public LocalStorageManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Save a new order to local storage
     * @param order Order object to save
     * @return Order ID
     */
    public String saveOrder(Order order) {
        try {
            // Generate unique order ID if not present
            if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
                String orderId = "ORD-" + System.currentTimeMillis();
                order.setOrderId(orderId);
            }

            // Get existing orders
            List<Order> orders = getAllOrders();
            orders.add(order);

            // Save updated orders list
            String ordersJson = gson.toJson(orders);
            sharedPreferences.edit()
                    .putString(ORDERS_KEY, ordersJson)
                    .putLong(LAST_ORDER_ID, System.currentTimeMillis())
                    .apply();

            Log.d(TAG, "Order saved: " + order.getOrderId());
            return order.getOrderId();

        } catch (Exception e) {
            Log.e(TAG, "Error saving order: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Retrieve all orders from local storage
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        try {
            String ordersJson = sharedPreferences.getString(ORDERS_KEY, "[]");
            Order[] ordersArray = gson.fromJson(ordersJson, Order[].class);
            List<Order> orders = new ArrayList<>();
            if (ordersArray != null) {
                for (Order order : ordersArray) {
                    orders.add(order);
                }
            }
            Log.d(TAG, "Retrieved " + orders.size() + " orders");
            return orders;

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving orders: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Retrieve a specific order by ID
     * @param orderId Order ID to retrieve
     * @return Order object or null if not found
     */
    public Order getOrderById(String orderId) {
        try {
            List<Order> orders = getAllOrders();
            for (Order order : orders) {
                if (order.getOrderId().equals(orderId)) {
                    Log.d(TAG, "Order found: " + orderId);
                    return order;
                }
            }
            Log.w(TAG, "Order not found: " + orderId);
            return null;

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving order: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Delete an order from local storage
     * @param orderId Order ID to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteOrder(String orderId) {
        try {
            List<Order> orders = getAllOrders();
            orders.removeIf(order -> order.getOrderId().equals(orderId));

            String ordersJson = gson.toJson(orders);
            sharedPreferences.edit()
                    .putString(ORDERS_KEY, ordersJson)
                    .apply();

            Log.d(TAG, "Order deleted: " + orderId);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error deleting order: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Update an existing order
     * @param order Updated order object
     * @return true if updated, false otherwise
     */
    public boolean updateOrder(Order order) {
        try {
            if (order.getOrderId() == null) {
                Log.e(TAG, "Cannot update order without ID");
                return false;
            }

            List<Order> orders = getAllOrders();
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getOrderId().equals(order.getOrderId())) {
                    orders.set(i, order);
                    String ordersJson = gson.toJson(orders);
                    sharedPreferences.edit()
                            .putString(ORDERS_KEY, ordersJson)
                            .apply();
                    Log.d(TAG, "Order updated: " + order.getOrderId());
                    return true;
                }
            }
            Log.w(TAG, "Order not found for update: " + order.getOrderId());
            return false;

        } catch (Exception e) {
            Log.e(TAG, "Error updating order: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Clear all orders from local storage
     */
    public void clearAllOrders() {
        try {
            sharedPreferences.edit()
                    .remove(ORDERS_KEY)
                    .apply();
            Log.d(TAG, "All orders cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing orders: " + e.getMessage(), e);
        }
    }

    /**
     * Get the number of stored orders
     * @return Number of orders
     */
    public int getOrderCount() {
        return getAllOrders().size();
    }
}
