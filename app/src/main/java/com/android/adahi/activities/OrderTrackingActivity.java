package com.android.adahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.adahi.R;

public class OrderTrackingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_tracking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.addTrackingButton).setOnClickListener(v ->
                Toast.makeText(this, R.string.tracking_add_button, Toast.LENGTH_SHORT).show());

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        LinearLayout homeTab = findViewById(R.id.navHomeTab);
        LinearLayout ordersTab = findViewById(R.id.navOrdersTab);
        LinearLayout profileTab = findViewById(R.id.navProfileTab);

        homeTab.setOnClickListener(v -> {
            startActivity(new Intent(this, AnimalListActivity.class));
            finish();
        });
        ordersTab.setOnClickListener(v -> { });
        profileTab.setOnClickListener(v ->
                Toast.makeText(this, R.string.nav_profile, Toast.LENGTH_SHORT).show());

        ((ImageView) findViewById(R.id.navHomeIcon)).setSelected(false);
        ((ImageView) findViewById(R.id.navOrdersIcon)).setSelected(true);
    }
}