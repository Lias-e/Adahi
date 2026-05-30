package com.android.adahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.adahi.R;
import com.android.adahi.adapters.OrderTrackingAdapter;
import com.android.adahi.models.Order;
import com.android.adahi.data.LocalOrderDbHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderTrackingActivity extends AppCompatActivity {

    private static final String TAG = "OrderTrackingActivity";

    private RecyclerView ordersRecyclerView;
    private ProgressBar loadingProgressBar;
    private TextView emptyStateTextView;
    private TextView ordersCountTextView;
    private OrderTrackingAdapter adapter;
    private LocalOrderDbHelper localDb;
    private FirebaseFirestore firestore;
    private final List<Order> loadedOrders = new ArrayList<>();

    private static final String BUY_ORDERS_COLLECTION = "buy_orders";
    private static final String RESERVE_ORDERS_COLLECTION = "reserve_orders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_tracking);

        localDb = new LocalOrderDbHelper(this);
        firestore = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        ordersCountTextView = findViewById(R.id.ordersCountTextView);

        adapter = new OrderTrackingAdapter(this, this::openOrderDetails);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(adapter);

        loadOrdersFromLocalDb();
    }

    private void openOrderDetails(Order order) {
        if (order == null || order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
            return;
        }

        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra("order_id", order.getOrderId());
        startActivity(intent);
    }
    private void loadOrdersFromLocalDb() {
        setLoading(true);
        loadedOrders.clear();
        List<LocalOrderDbHelper.OrderRecord> records = localDb.getAllOrders();
        for (LocalOrderDbHelper.OrderRecord r : records) {
            Order o = new Order();
            o.setOrderId(r.orderId);
            o.setOrderDate(r.createdAt);
            loadedOrders.add(o);
            fetchOrderDetails(o);
        }

        loadedOrders.sort(Comparator.comparingLong(Order::getOrderDate).reversed());
        adapter.setOrders(loadedOrders);
        updateEmptyState();
        setLoading(false);
    }

    private void fetchOrderDetails(Order targetOrder) {
        if (firestore == null || targetOrder == null || targetOrder.getOrderId() == null || targetOrder.getOrderId().trim().isEmpty()) {
            return;
        }

        fetchOrderFromCollection(BUY_ORDERS_COLLECTION, order -> {
            if (order != null) {
                applyFetchedOrder(targetOrder, order);
                refreshOrders();
                return;
            }

            fetchOrderFromCollection(RESERVE_ORDERS_COLLECTION, reserveOrder -> {
                if (reserveOrder != null) {
                    applyFetchedOrder(targetOrder, reserveOrder);
                    refreshOrders();
                }
            }, targetOrder.getOrderId());
        }, targetOrder.getOrderId());
    }

    private void fetchOrderFromCollection(String collectionName, OrderResultCallback callback, String orderId) {
        firestore.collection(collectionName)
                .document(orderId)
                .get()
                .addOnSuccessListener(snapshot -> callback.onResult(snapshot.toObject(Order.class)))
                .addOnFailureListener(error -> {
                    Log.e(TAG, "Failed to load tracked order from collection: " + collectionName, error);
                    callback.onResult(null);
                });
    }

    private void applyFetchedOrder(Order targetOrder, Order sourceOrder) {
        targetOrder.setCustomerName(sourceOrder.getCustomerName());
        targetOrder.setWilaya(sourceOrder.getWilaya());
        targetOrder.setItems(sourceOrder.getItems());
        targetOrder.setTotalPrice(sourceOrder.getTotalPrice());
        targetOrder.setOrderType(sourceOrder.getOrderType());
        targetOrder.setFeeAmount(sourceOrder.getFeeAmount());
        targetOrder.setStatus(sourceOrder.getStatus());
    }

    private void refreshOrders() {
        loadedOrders.sort(Comparator.comparingLong(Order::getOrderDate).reversed());
        adapter.setOrders(loadedOrders);
        updateEmptyState();
    }

    private void setLoading(boolean loading) {
        loadingProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        ordersRecyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        if (loading) {
            emptyStateTextView.setVisibility(View.GONE);
        }
    }

    private void updateEmptyState() {
        boolean empty = loadedOrders.isEmpty();
        emptyStateTextView.setVisibility(empty ? View.VISIBLE : View.GONE);
        ordersRecyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        ordersCountTextView.setText(getString(R.string.tracking_orders_count, loadedOrders.size()));
    }

    private interface OrderResultCallback {
        void onResult(Order order);
    }
}