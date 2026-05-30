package com.android.adahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.android.adahi.R;
import com.android.adahi.models.Animal;
import com.android.adahi.utils.AnimalUiUtils;

public class AnimalDetailActivity extends AppCompatActivity {

    private String animalId;
    private String animalName;
    private String animalType;
    private double animalPrice;
    private double animalWeight;
    private String animalDescription;
    private String animalBreed;
    private String animalAge;
    private String animalGender;
    private String animalHealthStatus;
    private String animalSalesPoint;

    private TextView animalNameText;
    private TextView animalTypeText;
    private TextView animalWeightText;
    private TextView animalDescriptionText;
    private TextView breedText;
    private TextView ageText;
    private TextView genderText;
    private TextView salesPointText;
    
    private ImageView heroImage;
    private ImageView backButton;
    private Button directPurchaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_animal_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readIntentExtras();
        bindViews();
        populateViews();
        setupActions();
    }

    private void readIntentExtras() {
        Intent intent = getIntent();
        animalId = intent.getStringExtra("animal_id");
        animalName = intent.getStringExtra("animal_name");
        animalType = intent.getStringExtra("animal_type");
        animalPrice = intent.getDoubleExtra("animal_price", 0);
        animalWeight = intent.getDoubleExtra("animal_weight", 0);
        animalDescription = intent.getStringExtra("animal_description");
        animalBreed = intent.getStringExtra("animal_breed");
        animalAge = intent.getStringExtra("animal_age");
        animalGender = intent.getStringExtra("animal_gender");
        animalHealthStatus = intent.getStringExtra("animal_health_status");
        animalSalesPoint = intent.getStringExtra("animal_sales_point");
    }

    private void bindViews() {
        animalNameText = findViewById(R.id.animalDetailName);
        animalTypeText = findViewById(R.id.animalDetailType);
        animalWeightText = findViewById(R.id.animalDetailWeight);
        animalDescriptionText = findViewById(R.id.animalDescriptionText);
        breedText = findViewById(R.id.animalBreedText);
        ageText = findViewById(R.id.animalAgeText);
        genderText = findViewById(R.id.animalGenderText);
        salesPointText = findViewById(R.id.animalSalesPointText);
        heroImage = findViewById(R.id.animalHeroImage);
        backButton = findViewById(R.id.backButton);
        directPurchaseButton = findViewById(R.id.directPurchaseButton);
    }

    private void populateViews() {
        animalNameText.setText(animalName != null ? animalName : "");
        animalTypeText.setText(getString(R.string.animal_type_label, animalType != null ? animalType : ""));
        animalWeightText.setText(getString(R.string.animal_weight_label, animalWeight));
        animalDescriptionText.setText(safeValue(animalDescription));
        breedText.setText(getString(R.string.breed_label, safeValue(animalBreed)));
        ageText.setText(getString(R.string.age_label, safeValue(animalAge)));
        genderText.setText(getString(R.string.gender_label, safeValue(animalGender)));
        salesPointText.setText(getString(R.string.sales_point_current_label, safeValue(animalSalesPoint)));

        Animal previewAnimal = new Animal();
        previewAnimal.setId(animalId);
        previewAnimal.setName(animalName);
        previewAnimal.setType(animalType);
        previewAnimal.setPrice(animalPrice);
        previewAnimal.setWeight(animalWeight);
        previewAnimal.setDescription(animalDescription);
        previewAnimal.setBreed(animalBreed);
        previewAnimal.setAge(animalAge);
        previewAnimal.setGender(animalGender);
        previewAnimal.setHealthStatus(animalHealthStatus);
        previewAnimal.setSalesPoint(animalSalesPoint);

        Glide.with(this)
            .load(AnimalUiUtils.resolveImageUrl(previewAnimal))
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(heroImage);
    }

    private String safeValue(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());
        directPurchaseButton.setOnClickListener(v -> openOrderForm("buy"));
    }

    private void openOrderForm(String orderType) {
        Intent intent = new Intent(this, OrderFormActivity.class);
        intent.putExtra("animal_id", animalId);
        intent.putExtra("animal_name", animalName);
        intent.putExtra("animal_type", animalType);
        intent.putExtra("animal_price", animalPrice);
        intent.putExtra("animal_weight", animalWeight);
        intent.putExtra("animal_description", animalDescription);
        intent.putExtra("animal_breed", animalBreed);
        intent.putExtra("animal_age", animalAge);
        intent.putExtra("animal_gender", animalGender);
        intent.putExtra("animal_health_status", animalHealthStatus);
        intent.putExtra("animal_sales_point", animalSalesPoint);
        intent.putExtra("order_type", orderType);
        startActivity(intent);
    }

}
