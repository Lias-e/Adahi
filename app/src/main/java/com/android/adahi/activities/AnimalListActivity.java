package com.android.adahi.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.adahi.R;
import com.android.adahi.adapters.AnimalAdapter;
import com.android.adahi.models.Animal;

import java.util.ArrayList;
import java.util.List;

/**
 * AnimalListActivity displays a list of available Adahi animals.
 * Uses a predefined local list of animals.
 */
public class AnimalListActivity extends AppCompatActivity {

    private static final String TAG = "AnimalListActivity";
    private RecyclerView animalRecyclerView;
    private AnimalAdapter animalAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_animal_list);

        // Handle edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        initializeViews();

        // Load predefined animals
        loadPredefinedAnimals();
    }

    private void initializeViews() {
        animalRecyclerView = findViewById(R.id.animalRecyclerView);
        animalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        progressBar = findViewById(R.id.progressBar);

        animalAdapter = new AnimalAdapter(this, animal -> Log.d(TAG, "Animal selected: " + animal.getName()));
        animalRecyclerView.setAdapter(animalAdapter);
    }

    private void loadPredefinedAnimals() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        List<Animal> animals = new ArrayList<>();

        animals.add(new Animal("1", "كبش أولاد جلال", "كباش", 78000.0, 58.0, "سلالة محلية أصيلة، فحص بيطري معتمد، ومناسبة للحجز المباشر.", "", 12, "أولاد جلال", "18 شهر", "ذكر", "مفحوص بيطرياً", "سوق بئر توتة"));
        animals.add(new Animal("2", "عجل محلي", "أبقار", 245000.0, 230.0, "عجل بتغذية طبيعية، متوفر في نقطة بيع موثقة.", "", 4, "محلي", "24 شهر", "ذكر", "مفحوص بيطرياً", "نقطة بيع الحميز"));
        animals.add(new Animal("3", "جدي السلالة الحمراء", "ماعز", 42000.0, 28.0, "مناسب للمعاينة والحجز بعربون، وزن متوسط.", "", 9, "السلالة الحمراء", "14 شهر", "أنثى", "سليم صحياً", "مزرعة الدولة - القبة"));
        animals.add(new Animal("4", "جمل صحراوي", "إبل", 340000.0, 410.0, "حيوان قوي ومختار وفق المعايير الصحية.", "", 2, "صحراوي", "30 شهر", "ذكر", "فحص بيطري معتمد", "سوق بئر توتة"));
        animals.add(new Animal("5", "كبش وهراني", "كباش", 65000.0, 52.0, "سلالة محلية مناسبة للعائلات، متوفر بعدة نقاط بيع.", "", 7, "وهراني", "20 شهر", "ذكر", "سليم صحياً", "نقطة بيع الحميز"));

        animalAdapter.setAnimals(animals);
        
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }
}
