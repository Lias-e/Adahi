package com.android.adahi.activities;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
    private FirebaseFirestore db;
    private final List<Order> loadedOrders = new ArrayList<>();
    private int pendingCollections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_tracking);

        db = FirebaseFirestore.getInstance();

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

        adapter = new OrderTrackingAdapter(this);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(adapter);

        loadOrdersFromFirestore();
    }

    private void loadOrdersFromFirestore() {
        setLoading(true);
        loadedOrders.clear();
        pendingCollections = 2;

        loadCollection("buy_orders", "buy");
        loadCollection("reserve_orders", "reserve");
    }

    private void loadCollection(String collectionName, String defaultType) {
        db.collection(collectionName)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                        Order order = documentSnapshot.toObject(Order.class);
                        if (order != null) {
                            if (order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
                                order.setOrderId(documentSnapshot.getId());
                            }
                            if (order.getOrderType() == null || order.getOrderType().trim().isEmpty()) {
                                order.setOrderType(defaultType);
                            }
                            loadedOrders.add(order);
                        }
                    }
                    markCollectionComplete();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed loading " + collectionName, e);
                    markCollectionComplete();
                });
    }

    private void markCollectionComplete() {
        pendingCollections--;
        if (pendingCollections <= 0) {
            loadedOrders.sort(Comparator.comparingLong(Order::getOrderDate).reversed());
            adapter.setOrders(loadedOrders);
            updateEmptyState();
            setLoading(false);
        }
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
}