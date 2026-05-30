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
import com.android.adahi.utils.AnimalUiUtils;

/**
 * OrderFormActivity allows users to enter customer information and confirm either a buy order or a reservation.
 */
public class OrderFormActivity extends AppCompatActivity {

    private static final String TAG = "OrderFormActivity";
    private static final double BUY_FEE = 0d;

    // UI Components
    private TextView selectedAnimalName;
    private TextView selectedAnimalPrice;
    private EditText customerNameInput;
    private EditText customerEmailInput;
    private EditText customerPhoneInput;
    private Spinner wilayaSpinner;
    private Button confirmButton;
    private Button cancelButton;
    private TextView formTitleTextView;

    // Animal details
    private String animalId;
    private String animalName;
    private double animalPrice;
    private String orderType;
    private double feeAmount;
    private String collectionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupWilayaSpinner();
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
        wilayaSpinner = findViewById(R.id.wilayaSpinner);
        confirmButton = findViewById(R.id.confirmOrderButton);
        cancelButton = findViewById(R.id.cancelOrderButton);
        formTitleTextView = findViewById(R.id.formTitleTextView);
    }

    private void setupWilayaSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.wilayas_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wilayaSpinner.setAdapter(adapter);
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
        confirmButton.setText(R.string.confirm_button);
        cancelButton.setText(R.string.cancel_button);
    }

    private void setupButtonListeners() {
        confirmButton.setOnClickListener(v -> prepareOrderPreview());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void prepareOrderPreview() {
        if (!validateInputs()) return;

        try {
            String customerName = customerNameInput.getText().toString().trim();
            String customerEmail = customerEmailInput.getText().toString().trim();
            String customerPhone = customerPhoneInput.getText().toString().trim();
            String wilaya = wilayaSpinner.getSelectedItemPosition() > 0
                    ? String.valueOf(wilayaSpinner.getSelectedItem())
                    : "";
            int quantity = 1;

            Order order = new Order(customerName, customerEmail, customerPhone, wilaya, orderType);
            order.setOrderId("ORD-" + System.currentTimeMillis());
            order.setFeeAmount(feeAmount);
            order.setTotalPrice(feeAmount);
            order.setStatus("Submitted");
            order.setPaymentStatus("none");
            order.addItem(new Order.OrderItem(animalId, animalName, quantity, animalPrice));
            order.calculateTotalPrice();

            navigateToConfirmation(order);

        } catch (Exception e) {
            Log.e(TAG, "Error confirming order", e);
        }
    }

    private void navigateToConfirmation(Order order) {
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra("order", order);
        intent.putExtra("allow_submit", true);
        startActivity(intent);
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
        if (wilayaSpinner.getSelectedItemPosition() <= 0) {
            Toast.makeText(this, R.string.error_field_required, Toast.LENGTH_SHORT).show();
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
        outState.putInt("wilayaPosition", wilayaSpinner.getSelectedItemPosition());
    }

    private void restoreSavedData(Bundle savedInstanceState) {
        customerNameInput.setText(savedInstanceState.getString("customerName", ""));
        customerEmailInput.setText(savedInstanceState.getString("customerEmail", ""));
        customerPhoneInput.setText(savedInstanceState.getString("customerPhone", ""));
        int wilayaPosition = savedInstanceState.getInt("wilayaPosition", 0);
        if (wilayaSpinner.getAdapter() != null && wilayaPosition < wilayaSpinner.getAdapter().getCount()) {
            wilayaSpinner.setSelection(wilayaPosition);
        }
    }
}
