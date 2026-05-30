package com.android.adahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

/**
 * OrderFormActivity allows users to enter customer information and confirm either a buy order or a reservation.
 */
public class OrderFormActivity extends AppCompatActivity {

    private static final String TAG = "OrderFormActivity";
    private static final double BUY_FEE = 3000d;

    // UI Components
    private TextView selectedAnimalName;
    private TextView selectedAnimalPrice;
    private EditText customerNameInput;
    private EditText customerEmailInput;
    private EditText customerPhoneInput;
    private Button confirmButton;
    private Button cancelButton;
    private TextView formTitleTextView;
    private TextView feeValueTextView;
    private TextView feeNoteTextView;

    // Animal details
    private String animalId;
    private String animalName;
    private double animalPrice;
    private String orderType;
    private double feeAmount;
    private String collectionName;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_form);

        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        retrieveAnimalDetails();
        configureMode();
        setupButtonListeners();

        if (savedInstanceState != null) {
            restoreSavedData(savedInstanceState);
        }
    }

    private void initializeViews() {
        selectedAnimalName = findViewById(R.id.selectedAnimalName);
        selectedAnimalPrice = findViewById(R.id.selectedAnimalPrice);
        customerNameInput = findViewById(R.id.customerNameInput);
        customerEmailInput = findViewById(R.id.customerEmailInput);
        customerPhoneInput = findViewById(R.id.customerPhoneInput);
        confirmButton = findViewById(R.id.confirmOrderButton);
        cancelButton = findViewById(R.id.cancelOrderButton);
        formTitleTextView = findViewById(R.id.formTitleTextView);
        feeValueTextView = findViewById(R.id.feeValueTextView);
        feeNoteTextView = findViewById(R.id.feeNoteTextView);
        
        // Wilaya removed from buy form
    }

    private void retrieveAnimalDetails() {
        Intent intent = getIntent();
        animalId = intent.getStringExtra("animal_id");
        animalName = intent.getStringExtra("animal_name");
        animalPrice = intent.getDoubleExtra("animal_price", 0);
        orderType = intent.getStringExtra("order_type");

        if (orderType == null || orderType.trim().isEmpty()) {
            orderType = "buy";
        }

        if (animalName != null) {
            selectedAnimalName.setText(animalName);
            selectedAnimalPrice.setText(AnimalUiUtils.formatPrice(animalPrice));
        }
    }

    private void configureMode() {
        // Only buy flow supported
        orderType = "buy";
        feeAmount = BUY_FEE;
        if (formTitleTextView != null) {
            formTitleTextView.setText(R.string.buy_now_title);
        }
        if (feeValueTextView != null) {
            feeValueTextView.setText(AnimalUiUtils.formatPrice(feeAmount));
        }
        if (feeNoteTextView != null) {
            feeNoteTextView.setText(R.string.arboun_note);
        }
        confirmButton.setText(R.string.buy_confirm_button);
        cancelButton.setText(R.string.cancel_button);
    }

    private void setupButtonListeners() {
        confirmButton.setOnClickListener(v -> handleConfirmOrder());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void handleConfirmOrder() {
        if (!validateInputs()) return;

        confirmButton.setEnabled(false);

        try {
            String customerName = customerNameInput.getText().toString().trim();
            String customerEmail = customerEmailInput.getText().toString().trim();
            String customerPhone = customerPhoneInput.getText().toString().trim();
            String wilaya = "";
            int quantity = 1;

            Order order = new Order(customerName, customerEmail, customerPhone, wilaya, orderType);
            order.setFeeAmount(feeAmount);
            order.setTotalPrice(feeAmount);
            order.setStatus("Pending Payment");
            order.setPaymentStatus("creating_checkout");
            order.addItem(new Order.OrderItem(animalId, animalName, quantity, animalPrice));
            order.calculateTotalPrice();

            collectionName = "buy_orders";
            String orderId = db.collection(collectionName).document().getId();
            order.setOrderId(orderId);

            db.collection(collectionName)
                    .document(orderId)
                    .set(order)
                    .addOnSuccessListener(unused -> {
                        Log.d(TAG, "Order saved to Firestore: " + orderId);
                        createChargilyCheckout(order, collectionName);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving order to Firestore", e);
                        confirmButton.setEnabled(true);
                        Toast.makeText(this, R.string.error_firestore_save_failed, Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            confirmButton.setEnabled(true);
            Log.e(TAG, "Error confirming order", e);
        }
    }

    private void navigateToConfirmation(Order order) {
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
        finish();
    }

    private void createChargilyCheckout(Order order, String collectionName) {
        ChargilyCheckoutClient.createCheckout(order, collectionName,
                new ChargilyCheckoutClient.Callback() {
                    @Override
                    public void onSuccess(ChargilyCheckoutClient.ChargilyCheckoutResponse response) {
                        runOnUiThread(() -> {
                            order.setCheckoutId(response.getCheckoutId());
                            order.setCheckoutUrl(response.getCheckoutUrl());
                            order.setPaymentStatus("checkout_created");

                            db.collection(collectionName)
                                    .document(order.getOrderId())
                                    .set(order)
                                    .addOnSuccessListener(unused -> {
                                        Log.d(TAG, "Chargily checkout created for order: " + order.getOrderId());
                                        navigateToConfirmation(order);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating order with checkout data", e);
                                        Toast.makeText(OrderFormActivity.this, R.string.error_firestore_save_failed, Toast.LENGTH_SHORT).show();
                                    });
                        });
                    }

                    @Override
                    public void onError(String message, Exception exception) {
                        runOnUiThread(() -> {
                            Log.e(TAG, "Failed to create Chargily checkout: " + message, exception);
                            order.setPaymentStatus("checkout_failed");
                            order.setStatus("Payment Setup Failed");
                            db.collection(collectionName)
                                    .document(order.getOrderId())
                                    .set(order)
                                    .addOnCompleteListener(task -> {
                                        confirmButton.setEnabled(true);
                                        String errorMessage = message != null ? message : getString(R.string.error_firestore_save_failed);
                                        Toast.makeText(OrderFormActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                    });
                        });
                    }
                });
    }

    private boolean validateInputs() {
        if (customerNameInput.getText().toString().trim().isEmpty()) {
            customerNameInput.setError(getString(R.string.error_field_required));
            return false;
        }
        if (customerPhoneInput.getText().toString().trim().isEmpty()) {
            customerPhoneInput.setError(getString(R.string.error_field_required));
            return false;
        }
        // wilaya not required for buy flow
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("customerName", customerNameInput.getText().toString());
        outState.putString("customerEmail", customerEmailInput.getText().toString());
        outState.putString("customerPhone", customerPhoneInput.getText().toString());
    }

    private void restoreSavedData(Bundle savedInstanceState) {
        customerNameInput.setText(savedInstanceState.getString("customerName", ""));
        customerEmailInput.setText(savedInstanceState.getString("customerEmail", ""));
        customerPhoneInput.setText(savedInstanceState.getString("customerPhone", ""));
    }
}
