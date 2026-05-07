package com.android.adahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.adahi.R;
import com.android.adahi.models.Order;
import com.android.adahi.utils.LocalStorageManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

/**
 * OrderFormActivity allows users to enter customer information and confirm order.
 * Orders are restricted to quantity 1 and use Wilaya/Comune for address.
 */
public class OrderFormActivity extends AppCompatActivity {

    private static final String TAG = "OrderFormActivity";

    // UI Components
    private TextView selectedAnimalName;
    private TextView selectedAnimalPrice;
    private EditText customerNameInput;
    private EditText customerEmailInput;
    private EditText customerPhoneInput;
    private EditText wilayaInput;
    private EditText comuneInput;
    private EditText specialInstructionsInput;
    private Button confirmButton;
    private Button cancelButton;

    // Animal details
    private String animalId;
    private String animalName;
    private double animalPrice;

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
        wilayaInput = findViewById(R.id.wilayaInput);
        comuneInput = findViewById(R.id.comuneInput);
        specialInstructionsInput = findViewById(R.id.specialInstructionsInput);
        confirmButton = findViewById(R.id.confirmOrderButton);
        cancelButton = findViewById(R.id.cancelOrderButton);
    }

    private void retrieveAnimalDetails() {
        Intent intent = getIntent();
        animalId = intent.getStringExtra("animal_id");
        animalName = intent.getStringExtra("animal_name");
        animalPrice = intent.getDoubleExtra("animal_price", 0);

        if (animalName != null) {
            selectedAnimalName.setText(animalName);
            selectedAnimalPrice.setText(getString(R.string.animal_price_label, String.format(Locale.getDefault(), "%.2f", animalPrice)));
        }
    }

    private void setupButtonListeners() {
        confirmButton.setOnClickListener(v -> handleConfirmOrder());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void handleConfirmOrder() {
        if (!validateInputs()) return;

        try {
            String customerName = customerNameInput.getText().toString().trim();
            String customerEmail = customerEmailInput.getText().toString().trim();
            String customerPhone = customerPhoneInput.getText().toString().trim();
            String wilaya = wilayaInput.getText().toString().trim();
            String comune = comuneInput.getText().toString().trim();
            int quantity = 1; // Restricted to 1
            String specialInstructions = specialInstructionsInput.getText().toString().trim();

            Order order = new Order(customerName, customerEmail, customerPhone, wilaya, comune);
            order.setSpecialInstructions(specialInstructions);
            order.addItem(new Order.OrderItem(animalId, animalName, quantity, animalPrice));
            order.calculateTotalPrice();

            // 1. Save to Local Storage
            LocalStorageManager storageManager = new LocalStorageManager(this);
            String orderId = storageManager.saveOrder(order);
            order.setOrderId(orderId);

            // 2. Save to Firestore
            db.collection("orders")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Order saved to Firestore: " + documentReference.getId());
                    navigateToConfirmation(order);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving order to Firestore", e);
                    Toast.makeText(this, "Order saved locally. Connection failed.", Toast.LENGTH_SHORT).show();
                    navigateToConfirmation(order);
                });

        } catch (Exception e) {
            Log.e(TAG, "Error confirming order", e);
        }
    }

    private void navigateToConfirmation(Order order) {
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
        finish();
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
        if (wilayaInput.getText().toString().trim().isEmpty()) {
            wilayaInput.setError(getString(R.string.error_field_required));
            return false;
        }
        if (comuneInput.getText().toString().trim().isEmpty()) {
            comuneInput.setError(getString(R.string.error_field_required));
            return false;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("customerName", customerNameInput.getText().toString());
        outState.putString("customerEmail", customerEmailInput.getText().toString());
        outState.putString("customerPhone", customerPhoneInput.getText().toString());
        outState.putString("wilaya", wilayaInput.getText().toString());
        outState.putString("comune", comuneInput.getText().toString());
        outState.putString("specialInstructions", specialInstructionsInput.getText().toString());
    }

    private void restoreSavedData(Bundle savedInstanceState) {
        customerNameInput.setText(savedInstanceState.getString("customerName", ""));
        customerEmailInput.setText(savedInstanceState.getString("customerEmail", ""));
        customerPhoneInput.setText(savedInstanceState.getString("customerPhone", ""));
        wilayaInput.setText(savedInstanceState.getString("wilaya", ""));
        comuneInput.setText(savedInstanceState.getString("comune", ""));
        specialInstructionsInput.setText(savedInstanceState.getString("specialInstructions", ""));
    }
}
