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

        animalAdapter = new AnimalAdapter(this, animal -> {
            Log.d(TAG, "Animal selected: " + animal.getName());
        });
        animalRecyclerView.setAdapter(animalAdapter);
    }

    /**
     * Loads a hardcoded list of animals available in Algeria.
     */
    private void loadPredefinedAnimals() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        List<Animal> animals = new ArrayList<>();
        
        // Localized and relevant animals for Algeria
        animals.add(new Animal("1", "Mouton Ouled Djellal", "Mouton", 65000.0, 45.0, "Race locale très prisée, excellente qualité de viande.", "", 10));
        animals.add(new Animal("2", "Mouton Hamra", "Mouton", 58000.0, 38.0, "Race rustique de l\'Ouest algérien.", "", 15));
        animals.add(new Animal("3", "Chèvre du Sahara", "Chèvre", 32000.0, 25.0, "Viande tendre et savoureuse.", "", 8));
        animals.add(new Animal("4", "Veau de Sétif", "Veau", 220000.0, 350.0, "Jeune veau élevé naturellement.", "", 5));

        animalAdapter.setAnimals(animals);
        
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }
}
