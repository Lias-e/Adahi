package com.android.adahi.activities;

import android.os.Bundle;
import android.util.Log;
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
import com.android.adahi.utils.AnimalUiUtils;

/**
 * OrderConfirmationActivity displays the order summary and confirmation.
 */
public class OrderConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "OrderConfirmationActivity";

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
    private Button backButton;

    // Order data
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_confirmation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        retrieveOrderData();
        displayOrderConfirmation();
        setupButtonListeners();
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
        backButton = findViewById(R.id.backButton);
    }

    private void retrieveOrderData() {
        currentOrder = (Order) getIntent().getSerializableExtra("order");
        if (currentOrder == null) {
            Log.e(TAG, "No order data received");
            Toast.makeText(this, R.string.error_order_data_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayOrderConfirmation() {
        if (currentOrder == null) return;

        try {
            String orderId = currentOrder.getOrderId() != null ? currentOrder.getOrderId() : "ORD-" + System.currentTimeMillis();
            orderIdTextView.setText(getString(R.string.order_id_label, orderId));

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

        } catch (Exception e) {
            Log.e(TAG, "Error displaying order confirmation", e);
        }
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> finish());
    }
}
