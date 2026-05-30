package com.android.adahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.adahi.R;
import com.android.adahi.adapters.AnimalAdapter;
import com.android.adahi.models.Animal;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows sheep breeds available in the selected wilaya.
 */
public class AnimalListActivity extends AppCompatActivity {

    private static final String TAG = "AnimalListActivity";
    private static final String WILAYAS_COLLECTION = "wilayas";

    private TextView emptyStateTextView;
    private ProgressBar progressBar;
    private RecyclerView animalRecyclerView;
    private AnimalAdapter animalAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_animal_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseFirestore.getInstance();
        initializeViews();
        showIntroState();
        loadAnimalsFromFirestore();
    }

    private void initializeViews() {
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        progressBar = findViewById(R.id.progressBar);
        animalRecyclerView = findViewById(R.id.breedRecyclerView);
        findViewById(R.id.ordersButton).setOnClickListener(v ->
                startActivity(new Intent(this, OrderTrackingActivity.class)));

        animalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        animalAdapter = new AnimalAdapter(this, animal -> {
            // Adapter handles detail navigation internally
        });
        animalRecyclerView.setAdapter(animalAdapter);
    }

    private void showIntroState() {
        animalAdapter.setAnimals(new ArrayList<>());
        emptyStateTextView.setText(R.string.animal_list_empty);
        emptyStateTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showLoadingState() {
        emptyStateTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showEmptyState(String message) {
        progressBar.setVisibility(View.GONE);
        animalAdapter.setAnimals(new ArrayList<>());
        emptyStateTextView.setText(message);
        emptyStateTextView.setVisibility(View.VISIBLE);
    }

    private void loadAnimalsFromFirestore() {
        showLoadingState();
        firestore.collection("animals")
                .orderBy("name")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Failed to load animals", e);
                        showEmptyState(getString(R.string.error_loading_animals));
                        return;
                    }

                    if (snapshots == null || snapshots.isEmpty()) {
                        showEmptyState(getString(R.string.animal_list_empty));
                        return;
                    }

                    List<Animal> animals = snapshots.toObjects(Animal.class);
                    // Ensure ids are set from document ids when missing
                    for (int i = 0; i < snapshots.size(); i++) {
                        if (i < animals.size()) {
                            Animal a = animals.get(i);
                            String docId = snapshots.getDocuments().get(i).getId();
                            if (a.getId() == null || a.getId().trim().isEmpty()) {
                                a.setId(docId);
                            }
                        }
                    }

                    animalAdapter.setAnimals(animals);
                    emptyStateTextView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                });
    }
}
