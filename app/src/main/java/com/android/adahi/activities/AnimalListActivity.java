package com.android.adahi.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.android.adahi.utils.SampleDataGenerator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * AnimalListActivity displays a list of available Adahi animals.
 * Loads animals from Firebase Firestore.
 */
public class AnimalListActivity extends AppCompatActivity {

    private static final String TAG = "AnimalListActivity";
    private static final String ANIMALS_COLLECTION = "animals";
    private RecyclerView animalRecyclerView;
    private AnimalAdapter animalAdapter;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private ListenerRegistration animalsListener;

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

        firestore = FirebaseFirestore.getInstance();

        // Load animals from Firestore
        loadAnimalsFromFirestore();
    }

    private void initializeViews() {
        animalRecyclerView = findViewById(R.id.animalRecyclerView);
        animalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        progressBar = findViewById(R.id.progressBar);

        animalAdapter = new AnimalAdapter(this, animal -> Log.d(TAG, "Animal selected: " + animal.getName()));
        animalRecyclerView.setAdapter(animalAdapter);
    }

    private void loadAnimalsFromFirestore() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        try {
            if (animalsListener != null) {
                animalsListener.remove();
            }

            animalsListener = firestore.collection(ANIMALS_COLLECTION)
                    .addSnapshotListener((snapshot, error) -> {
                        if (error != null) {
                            Log.e(TAG, "Failed to load animals from Firestore", error);
                            showFallbackAnimals("Failed to load animals from Firestore.");
                            return;
                        }

                        List<Animal> animals = new ArrayList<>();
                        if (snapshot != null) {
                            for (DocumentSnapshot animalSnapshot : snapshot.getDocuments()) {
                                Animal animal = animalSnapshot.toObject(Animal.class);
                                if (animal != null) {
                                    if (animal.getId() == null || animal.getId().trim().isEmpty()) {
                                        animal.setId(animalSnapshot.getId());
                                    }
                                    animals.add(animal);
                                }
                            }
                        }

                        if (animals.isEmpty()) {
                            Log.w(TAG, "No animals found in Firestore, using sample data.");
                            showFallbackAnimals("Firestore is empty, showing sample animals.");
                            return;
                        }

                        animalAdapter.setAnimals(animals);
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error loading animals from Firestore", e);
            showFallbackAnimals("Firestore is unavailable, showing sample animals.");
        }
    }

    private void showFallbackAnimals(String message) {
        animalAdapter.setAnimals(SampleDataGenerator.generateSampleAnimals());
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animalsListener != null) {
            animalsListener.remove();
            animalsListener = null;
        }
    }
}
