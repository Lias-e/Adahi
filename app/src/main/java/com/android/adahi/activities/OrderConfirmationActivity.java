package com.android.adahi.activities;

import android.os.Bundle;
import android.util.Log;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.adahi.R;
import com.android.adahi.models.Order;
import com.android.adahi.utils.ChargilyCheckoutClient;
import com.android.adahi.utils.AnimalUiUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * OrderConfirmationActivity displays the order summary and confirmation.
 */
public class OrderConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "OrderConfirmationActivity";
    private static final String CHARGILY_RESERVATION_PAYMENT_URL = "http://pay.chargily.com/test/payment-links/01kstvtc3dfd9w76t3eyb3805c";
    private static final String CHARGILY_ARBOUN_PAYMENT_URL = "http://pay.chargily.com/test/payment-links/01kstw3m7cq60eyynqr5crd0n5";

    // UI Components
    private TextView orderIdTextView;
    private TextView customerNameTextView;
    private TextView customerEmailTextView;
    private TextView customerPhoneTextView;
    private TextView wilayaTextView;
    private TextView orderTypeTextView;
    private TextView orderItemsTextView;
    private TextView totalPriceTextView;
    private TextView orderStatusTextView;
    private View paymentSectionCard;
    private TextView paymentSectionTitleTextView;
    private TextView paymentSectionNoteTextView;
    private Button openPaymentLinkButton;
    private Button copyPaymentLinkButton;
    private Button verifyPaymentButton;
    private Button backButton;

    // Order data
    private Order currentOrder;
    private String currentOrderId;
    private String orderCollectionName;
    private FirebaseFirestore firestore;
    private ListenerRegistration orderListener;
    private String pendingDeepLinkResult;
    private boolean deepLinkHandled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_confirmation);

        firestore = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        retrieveOrderData();
        displayOrderConfirmation();
        handlePaymentReturnIntent(getIntent());
        setupButtonListeners();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handlePaymentReturnIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startOrderListener();
    }

    private void initializeViews() {
        orderIdTextView = findViewById(R.id.orderIdTextView);
        customerNameTextView = findViewById(R.id.customerNameTextView);
        customerEmailTextView = findViewById(R.id.customerEmailTextView);
        customerPhoneTextView = findViewById(R.id.customerPhoneTextView);
        wilayaTextView = findViewById(R.id.wilayaTextView);
        orderTypeTextView = findViewById(R.id.comuneTextView);
        orderItemsTextView = findViewById(R.id.orderItemsTextView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        orderStatusTextView = findViewById(R.id.orderStatusTextView);
        paymentSectionCard = findViewById(R.id.paymentSectionCard);
        paymentSectionTitleTextView = findViewById(R.id.paymentSectionTitleTextView);
        paymentSectionNoteTextView = findViewById(R.id.paymentSectionNoteTextView);
        openPaymentLinkButton = findViewById(R.id.openPaymentLinkButton);
        copyPaymentLinkButton = findViewById(R.id.copyPaymentLinkButton);
        verifyPaymentButton = findViewById(R.id.verifyPaymentButton);
        backButton = findViewById(R.id.backButton);

        orderIdTextView.setClickable(true);
        orderIdTextView.setFocusable(true);
    }

    private void retrieveOrderData() {
        currentOrder = (Order) getIntent().getSerializableExtra("order");
        if (currentOrder == null) {
            Uri data = getIntent() != null ? getIntent().getData() : null;
            if (isPaymentReturnDeepLink(data)) {
                currentOrderId = data.getQueryParameter("orderId");
                orderCollectionName = data.getQueryParameter("collectionName");
                fetchOrderFromDeepLink();
                return;
            }

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
            String orderType = "reserve".equals(currentOrder.getOrderType())
                    ? getString(R.string.order_type_reserve)
                    : getString(R.string.order_type_buy);
            orderTypeTextView.setText(getString(R.string.order_type_label, orderType));

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

            totalPriceTextView.setText(getString(R.string.fee_label, AnimalUiUtils.formatPrice(currentOrder.getFeeAmount())));
            orderStatusTextView.setText(getString(R.string.status_label, currentOrder.getStatus()));
            orderCollectionName = "reserve".equals(currentOrder.getOrderType()) ? "reserve_orders" : "buy_orders";
            configurePaymentSection();
            startOrderListener();
            maybeHandlePendingDeepLink();

        } catch (Exception e) {
            Log.e(TAG, "Error displaying order confirmation", e);
        }
    }

    private void fetchOrderFromDeepLink() {
        if (firestore == null || currentOrderId == null || currentOrderId.trim().isEmpty() || orderCollectionName == null || orderCollectionName.trim().isEmpty()) {
            Toast.makeText(this, R.string.error_order_data_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestore.collection(orderCollectionName)
                .document(currentOrderId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Order order = snapshot.toObject(Order.class);
                    if (order == null) {
                        Toast.makeText(this, R.string.error_order_data_not_found, Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    currentOrder = order;
                    if (currentOrder.getOrderId() == null || currentOrder.getOrderId().trim().isEmpty()) {
                        currentOrder.setOrderId(currentOrderId);
                    }
                    displayOrderConfirmation();
                })
                .addOnFailureListener(error -> {
                    Log.e(TAG, "Failed to load order from deep link", error);
                    Toast.makeText(this, R.string.error_order_data_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private boolean isPaymentReturnDeepLink(Uri uri) {
        return uri != null
                && "adahi".equals(uri.getScheme())
                && "payment-return".equals(uri.getHost());
    }

    private void handlePaymentReturnIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Uri uri = intent.getData();
        if (!isPaymentReturnDeepLink(uri)) {
            return;
        }

        pendingDeepLinkResult = uri.getQueryParameter("result");
        deepLinkHandled = false;
        maybeHandlePendingDeepLink();
    }

    private void maybeHandlePendingDeepLink() {
        if (deepLinkHandled || pendingDeepLinkResult == null || pendingDeepLinkResult.trim().isEmpty()) {
            return;
        }

        if ("success".equalsIgnoreCase(pendingDeepLinkResult)) {
            if (currentOrder == null || currentOrder.getCheckoutId() == null || currentOrder.getCheckoutId().trim().isEmpty()) {
                return;
            }

            deepLinkHandled = true;
            Toast.makeText(this, R.string.payment_return_verifying, Toast.LENGTH_SHORT).show();
            verifyPaymentStatus();
            return;
        }

        if ("failure".equalsIgnoreCase(pendingDeepLinkResult)) {
            deepLinkHandled = true;
            Toast.makeText(this, R.string.payment_return_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void configurePaymentSection() {
        boolean isReservationOrder = currentOrder != null && "reserve".equals(currentOrder.getOrderType());
        boolean isBuyOrder = currentOrder != null && "buy".equals(currentOrder.getOrderType());
        boolean isPaymentOrder = isReservationOrder || isBuyOrder;
        boolean isPaid = currentOrder != null && "paid".equalsIgnoreCase(currentOrder.getPaymentStatus());
        if (paymentSectionCard != null) {
            paymentSectionCard.setVisibility(isPaymentOrder ? View.VISIBLE : View.GONE);
        }

        if (!isPaymentOrder) {
            return;
        }

        if (paymentSectionTitleTextView != null) {
            paymentSectionTitleTextView.setText(isReservationOrder
                    ? R.string.reservation_payment_title
                    : R.string.arboun_payment_title);
        }

        if (paymentSectionNoteTextView != null) {
            if (isPaid) {
                paymentSectionNoteTextView.setText("تم تأكيد الدفع عبر Chargily Pay.");
            } else {
                paymentSectionNoteTextView.setText(isReservationOrder
                        ? getString(R.string.reservation_payment_note)
                        : getString(R.string.arboun_payment_note));
            }
        }

        if (openPaymentLinkButton != null) {
            openPaymentLinkButton.setEnabled(!isPaid);
            openPaymentLinkButton.setText(isPaid ? "تم الدفع" : getString(R.string.reservation_payment_button));
            openPaymentLinkButton.setOnClickListener(v -> openChargilyPaymentLink());
        }

        if (copyPaymentLinkButton != null) {
            copyPaymentLinkButton.setEnabled(!isPaid);
            copyPaymentLinkButton.setOnClickListener(v -> copyPaymentLinkToClipboard());
        }

        if (verifyPaymentButton != null) {
            verifyPaymentButton.setEnabled(!isPaid);
            verifyPaymentButton.setText(isPaid ? getString(R.string.reservation_payment_verified_button) : getString(R.string.reservation_payment_verify_button));
            verifyPaymentButton.setOnClickListener(v -> verifyPaymentStatus());
        }
    }

    private void verifyPaymentStatus() {
        if (currentOrder == null || currentOrder.getCheckoutId() == null || currentOrder.getCheckoutId().trim().isEmpty()) {
            Toast.makeText(this, R.string.reservation_payment_not_ready, Toast.LENGTH_SHORT).show();
            return;
        }

        if (verifyPaymentButton != null) {
            verifyPaymentButton.setEnabled(false);
        }

        ChargilyCheckoutClient.fetchCheckoutStatus(currentOrder.getCheckoutId(), new ChargilyCheckoutClient.CheckoutStatusCallback() {
            @Override
            public void onSuccess(String status) {
                runOnUiThread(() -> applyCheckoutStatus(status));
            }

            @Override
            public void onError(String message, Exception exception) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Failed to verify payment status", exception);
                    if (verifyPaymentButton != null) {
                        verifyPaymentButton.setEnabled(true);
                    }
                    Toast.makeText(OrderConfirmationActivity.this, message == null ? getString(R.string.reservation_payment_verify_failed) : message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void applyCheckoutStatus(String status) {
        String normalizedStatus = status == null ? "pending" : status.trim().toLowerCase();
        String nextOrderStatus;
        String nextPaymentStatus;

        switch (normalizedStatus) {
            case "paid":
                nextOrderStatus = "Paid";
                nextPaymentStatus = "paid";
                break;
            case "failed":
                nextOrderStatus = "Payment Failed";
                nextPaymentStatus = "failed";
                break;
            case "canceled":
                nextOrderStatus = "Payment Canceled";
                nextPaymentStatus = "canceled";
                break;
            default:
                nextOrderStatus = "Pending Payment";
                nextPaymentStatus = "pending";
                break;
        }

        if (currentOrder == null || currentOrderId == null || orderCollectionName == null) {
            if (verifyPaymentButton != null) {
                verifyPaymentButton.setEnabled(true);
            }
            return;
        }

        currentOrder.setStatus(nextOrderStatus);
        currentOrder.setPaymentStatus(nextPaymentStatus);

        firestore.collection(orderCollectionName)
                .document(currentOrderId)
                .set(currentOrder)
                .addOnSuccessListener(unused -> {
                    orderStatusTextView.setText(getString(R.string.status_label, currentOrder.getStatus()));
                    configurePaymentSection();
                    int messageRes = "paid".equals(nextPaymentStatus)
                            ? R.string.payment_return_success
                            : R.string.reservation_payment_verify_pending;
                    Toast.makeText(OrderConfirmationActivity.this, messageRes, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(error -> {
                    Log.e(TAG, "Failed to persist verified payment status", error);
                    if (verifyPaymentButton != null) {
                        verifyPaymentButton.setEnabled(true);
                    }
                    Toast.makeText(OrderConfirmationActivity.this, R.string.error_firestore_save_failed, Toast.LENGTH_SHORT).show();
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
                    String updatedOrderType = "reserve".equals(currentOrder.getOrderType())
                            ? getString(R.string.order_type_reserve)
                            : getString(R.string.order_type_buy);
                    orderTypeTextView.setText(getString(R.string.order_type_label, updatedOrderType));
                    totalPriceTextView.setText(getString(R.string.fee_label, AnimalUiUtils.formatPrice(currentOrder.getFeeAmount())));
                    orderStatusTextView.setText(getString(R.string.status_label, currentOrder.getStatus()));
                    configurePaymentSection();
                });
    }

    private void openChargilyPaymentLink() {
        try {
            String checkoutUrl = currentOrder != null && currentOrder.getCheckoutUrl() != null && !currentOrder.getCheckoutUrl().trim().isEmpty()
                    ? currentOrder.getCheckoutUrl()
                    : getFallbackPaymentUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening Chargily payment link", e);
            Toast.makeText(this, R.string.reservation_payment_link_open_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void copyPaymentLinkToClipboard() {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboardManager == null) {
                Toast.makeText(this, R.string.error_order_copy_failed, Toast.LENGTH_SHORT).show();
                return;
            }

            String checkoutUrl = currentOrder != null && currentOrder.getCheckoutUrl() != null && !currentOrder.getCheckoutUrl().trim().isEmpty()
                    ? currentOrder.getCheckoutUrl()
                    : getFallbackPaymentUrl();
            clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string.reservation_payment_button), checkoutUrl));
            Toast.makeText(this, R.string.reservation_payment_link_copied, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error copying Chargily payment link", e);
            Toast.makeText(this, R.string.error_order_copy_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFallbackPaymentUrl() {
        if (currentOrder != null && "buy".equals(currentOrder.getOrderType())) {
            return CHARGILY_ARBOUN_PAYMENT_URL;
        }
        return CHARGILY_RESERVATION_PAYMENT_URL;
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
