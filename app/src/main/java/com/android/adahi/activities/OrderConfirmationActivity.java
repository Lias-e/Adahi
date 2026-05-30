package com.android.adahi.activities;

import android.os.Bundle;
import android.util.Log;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.adahi.R;
import com.android.adahi.data.LocalOrderDbHelper;
import com.android.adahi.models.Order;
import com.android.adahi.utils.AnimalUiUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * OrderConfirmationActivity displays the order summary and confirmation.
 */
public class OrderConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "OrderConfirmationActivity";
    private static final String BUY_ORDERS_COLLECTION = "buy_orders";
    private static final String RESERVE_ORDERS_COLLECTION = "reserve_orders";
    // UI Components
    private ImageView backButton;
    private TextView orderIdTextView;
    private TextView customerNameTextView;
    private TextView customerEmailTextView;
    private TextView customerPhoneTextView;
    private TextView wilayaTextView;
    private TextView orderItemsTextView;
    private TextView orderStatusTextView;
    private Button submitOrderButton;
    private View buttonContainer;

    // Order data
    private Order currentOrder;
    private String currentOrderId;
    private String orderCollectionName;
    private FirebaseFirestore firestore;
    private LocalOrderDbHelper localDb;
    private ListenerRegistration orderListener;
    private String pendingDeepLinkResult;
    private boolean deepLinkHandled;
    private boolean allowSubmitOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_confirmation);

        firestore = FirebaseFirestore.getInstance();
        localDb = new LocalOrderDbHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        retrieveOrderData();
        if (currentOrder != null) {
            displayOrderConfirmation();
        } else if (currentOrderId != null && !currentOrderId.trim().isEmpty()) {
            fetchOrderById();
        }
        setupButtonListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startOrderListener();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        orderIdTextView = findViewById(R.id.orderIdTextView);
        customerNameTextView = findViewById(R.id.customerNameTextView);
        customerEmailTextView = findViewById(R.id.customerEmailTextView);
        customerPhoneTextView = findViewById(R.id.customerPhoneTextView);
        wilayaTextView = findViewById(R.id.wilayaTextView);
        orderItemsTextView = findViewById(R.id.orderItemsTextView);
        orderStatusTextView = findViewById(R.id.orderStatusTextView);
        buttonContainer = findViewById(R.id.buttonContainer);
        submitOrderButton = findViewById(R.id.submitOrderButton);

        orderIdTextView.setClickable(true);
        orderIdTextView.setFocusable(true);
    }

    private void retrieveOrderData() {
        Intent intent = getIntent();
        currentOrder = (Order) intent.getSerializableExtra("order");
        currentOrderId = intent.getStringExtra("order_id");
        orderCollectionName = intent.getStringExtra("order_collection");
        allowSubmitOrder = intent.getBooleanExtra("allow_submit", false);

        if (currentOrder != null) {
            if (currentOrder.getOrderId() == null || currentOrder.getOrderId().trim().isEmpty()) {
                if (currentOrderId != null && !currentOrderId.trim().isEmpty()) {
                    currentOrder.setOrderId(currentOrderId);
                } else {
                    currentOrder.setOrderId("ORD-" + System.currentTimeMillis());
                }
            }
            currentOrderId = currentOrder.getOrderId();
            if (orderCollectionName == null || orderCollectionName.trim().isEmpty()) {
                orderCollectionName = "reserve".equals(currentOrder.getOrderType()) ? RESERVE_ORDERS_COLLECTION : BUY_ORDERS_COLLECTION;
            }
            updateSubmitButtonVisibility();
            return;
        }

        if (currentOrderId == null || currentOrderId.trim().isEmpty()) {
            Log.e(TAG, "No order data received");
            Toast.makeText(this, R.string.error_order_data_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayOrderConfirmation() {
        if (currentOrder == null) return;

        try {
            currentOrderId = currentOrder.getOrderId() != null ? currentOrder.getOrderId() : "ORD-" + System.currentTimeMillis();
            orderIdTextView.setText(getString(R.string.order_id_label, currentOrderId));
            orderIdTextView.setOnClickListener(v -> copyOrderIdToClipboard());

            customerNameTextView.setText(getString(R.string.name_label, currentOrder.getCustomerName()));
            customerEmailTextView.setText(getString(R.string.nin_label, currentOrder.getCustomerEmail()));
            customerPhoneTextView.setText(getString(R.string.phone_label, currentOrder.getCustomerPhone()));
            
            wilayaTextView.setText(getString(R.string.wilaya_label, currentOrder.getWilaya()));

            StringBuilder itemsText = new StringBuilder(getString(R.string.order_items_header)).append("\n");
            if (currentOrder.getItems() != null) {
                for (Order.OrderItem item : currentOrder.getItems()) {
                    itemsText.append(getString(R.string.item_summary,
                            item.getAnimalName(),
                            item.getQuantity(),
                            AnimalUiUtils.formatPrice(item.getPricePerUnit()),
                            AnimalUiUtils.formatPrice(item.getSubtotal())
                    ));
                }
            }
            orderItemsTextView.setText(itemsText.toString());

            orderStatusTextView.setText(getString(R.string.status_label, currentOrder.getStatus()));
            orderCollectionName = "reserve".equals(currentOrder.getOrderType()) ? RESERVE_ORDERS_COLLECTION : BUY_ORDERS_COLLECTION;
            updateSubmitButtonVisibility();
            startOrderListener();

        } catch (Exception e) {
            Log.e(TAG, "Error displaying order confirmation", e);
        }
    }

    private void fetchOrderById() {
        if (firestore == null || currentOrderId == null || currentOrderId.trim().isEmpty()) {
            Toast.makeText(this, R.string.error_order_data_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchOrderFromCollection(BUY_ORDERS_COLLECTION, order -> {
            if (order != null) {
                currentOrder = order;
                orderCollectionName = BUY_ORDERS_COLLECTION;
                displayOrderConfirmation();
                return;
            }

            fetchOrderFromCollection(RESERVE_ORDERS_COLLECTION, reserveOrder -> {
                if (reserveOrder != null) {
                    currentOrder = reserveOrder;
                    orderCollectionName = RESERVE_ORDERS_COLLECTION;
                    displayOrderConfirmation();
                    return;
                }

                Log.e(TAG, "Failed to load order by id: " + currentOrderId);
                Toast.makeText(this, R.string.error_order_data_not_found, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void fetchOrderFromCollection(String collectionName, OrderLoadCallback callback) {
        firestore.collection(collectionName)
                .document(currentOrderId)
                .get()
                .addOnSuccessListener(snapshot -> callback.onResult(snapshot.toObject(Order.class)))
                .addOnFailureListener(error -> {
                    Log.e(TAG, "Failed to load order from collection: " + collectionName, error);
                    callback.onResult(null);
                });
    }

    private interface OrderLoadCallback {
        void onResult(Order order);
    }

    private void updateSubmitButtonVisibility() {
        if (submitOrderButton == null) {
            return;
        }
        buttonContainer.setVisibility(allowSubmitOrder ? View.VISIBLE : View.GONE);
        submitOrderButton.setVisibility(allowSubmitOrder ? View.VISIBLE : View.GONE);
        submitOrderButton.setOnClickListener(v -> submitOrder());
    }

    private void submitOrder() {
        if (currentOrder == null || currentOrderId == null || currentOrderId.trim().isEmpty()) {
            Toast.makeText(this, R.string.error_order_data_not_found, Toast.LENGTH_SHORT).show();
            return;
        }

        String collection = "reserve".equals(currentOrder.getOrderType()) ? RESERVE_ORDERS_COLLECTION : BUY_ORDERS_COLLECTION;
        currentOrder.setOrderId(currentOrderId);
        currentOrder.setStatus("Submitted");

        submitOrderButton.setEnabled(false);
        firestore.collection(collection)
                .document(currentOrderId)
                .set(currentOrder)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Order submitted to Firestore: " + currentOrderId);
                    localDb.insertOrder(currentOrderId, System.currentTimeMillis());
                    Toast.makeText(this, R.string.confirm_order_dialog_positive, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, OrderTrackingActivity.class));
                    finish();
                })
                .addOnFailureListener(error -> {
                    Log.e(TAG, "Error submitting order", error);
                    submitOrderButton.setEnabled(true);
                    Toast.makeText(this, R.string.error_firestore_save_failed, Toast.LENGTH_SHORT).show();
                });
    }


    private void startOrderListener() {
        if (firestore == null || currentOrderId == null || currentOrderId.trim().isEmpty() || orderCollectionName == null) {
            return;
        }

        if (orderListener != null) {
            orderListener.remove();
        }

        orderListener = firestore.collection(orderCollectionName)
                .document(currentOrderId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening for order updates", error);
                        return;
                    }

                    if (snapshot == null || !snapshot.exists()) {
                        return;
                    }

                    Order updatedOrder = snapshot.toObject(Order.class);
                    if (updatedOrder == null) {
                        return;
                    }

                    currentOrder = updatedOrder;
                    orderIdTextView.setText(getString(R.string.order_id_label, currentOrderId));
                    customerNameTextView.setText(getString(R.string.name_label, currentOrder.getCustomerName()));
                    customerEmailTextView.setText(getString(R.string.nin_label, currentOrder.getCustomerEmail()));
                    customerPhoneTextView.setText(getString(R.string.phone_label, currentOrder.getCustomerPhone()));
                    wilayaTextView.setText(getString(R.string.wilaya_label, currentOrder.getWilaya()));
                    orderStatusTextView.setText(getString(R.string.status_label, currentOrder.getStatus()));
                });
    }
    

    private void copyOrderIdToClipboard() {
        if (currentOrderId == null || currentOrderId.trim().isEmpty()) {
            Toast.makeText(this, R.string.error_order_data_not_found, Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            Toast.makeText(this, R.string.error_order_copy_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string.order_id_label_short), currentOrderId));
        Toast.makeText(this, R.string.order_id_copied, Toast.LENGTH_SHORT).show();
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> finish());
        updateSubmitButtonVisibility();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (orderListener != null) {
            orderListener.remove();
            orderListener = null;
        }
    }
}
