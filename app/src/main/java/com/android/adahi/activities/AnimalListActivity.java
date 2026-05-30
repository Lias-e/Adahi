package com.android.adahi.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.android.adahi.adapters.SheepBreedAvailabilityAdapter;
import com.android.adahi.models.SheepBreedStatus;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Shows sheep breeds available in the selected wilaya.
 */
public class AnimalListActivity extends AppCompatActivity {

    private static final String TAG = "AnimalListActivity";
    private static final String WILAYAS_COLLECTION = "wilayas";

    private Spinner wilayaSelector;
    private TextView wilayaSummaryTextView;
    private TextView emptyStateTextView;
    private ProgressBar progressBar;
    private RecyclerView breedRecyclerView;
    private SheepBreedAvailabilityAdapter breedAdapter;
    private FirebaseFirestore firestore;
    private String selectedWilaya;

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
        setupWilayaSpinner();
        showIntroState();
    }

    private void initializeViews() {
        wilayaSelector = findViewById(R.id.wilayaSelector);
        wilayaSummaryTextView = findViewById(R.id.wilayaSummaryTextView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        progressBar = findViewById(R.id.progressBar);
        breedRecyclerView = findViewById(R.id.breedRecyclerView);

        breedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        breedAdapter = new SheepBreedAvailabilityAdapter(this);
        breedRecyclerView.setAdapter(breedAdapter);
    }

    private void setupWilayaSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getWilayaItems());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wilayaSelector.setAdapter(adapter);
        wilayaSelector.setSelection(0, false);
        wilayaSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedWilaya = null;
                    showIntroState();
                    return;
                }

                selectedWilaya = parent.getItemAtPosition(position).toString();
                wilayaSummaryTextView.setText(getString(R.string.sheep_wilaya_selected, selectedWilaya));
                loadSheepBreedsForWilaya(selectedWilaya);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedWilaya = null;
                showIntroState();
            }
        });
    }

    private List<String> getWilayaItems() {
        List<String> items = new ArrayList<>();
        String[] wilayas = getResources().getStringArray(R.array.wilayas_array);
        Collections.addAll(items, wilayas);
        return items;
    }

    private void showIntroState() {
        breedAdapter.setItems(new ArrayList<>());
        emptyStateTextView.setText(R.string.sheep_wilaya_prompt);
        emptyStateTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        wilayaSummaryTextView.setText(R.string.sheep_wilaya_prompt_short);
    }

    private void showLoadingState() {
        emptyStateTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showEmptyState(String message) {
        progressBar.setVisibility(View.GONE);
        breedAdapter.setItems(new ArrayList<>());
        emptyStateTextView.setText(message);
        emptyStateTextView.setVisibility(View.VISIBLE);
    }

    private void loadSheepBreedsForWilaya(String wilaya) {
        if (wilaya == null || wilaya.trim().isEmpty()) {
            showIntroState();
            return;
        }

        showLoadingState();

        DocumentReference directReference = firestore.collection(WILAYAS_COLLECTION).document(wilaya);
        directReference.get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot != null && snapshot.exists()) {
                        renderWilayaSnapshot(snapshot);
                        return;
                    }
                    queryWilayaByFields(wilaya);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Direct wilaya lookup failed", e);
                    queryWilayaByFields(wilaya);
                });
    }

    private void queryWilayaByFields(String wilaya) {
        firestore.collection(WILAYAS_COLLECTION)
                .whereEqualTo("name", wilaya)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> handleWilayaQueryResult(wilaya, snapshot))
                .addOnFailureListener(firstError -> firestore.collection(WILAYAS_COLLECTION)
                        .whereEqualTo("wilaya", wilaya)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(snapshot -> handleWilayaQueryResult(wilaya, snapshot))
                        .addOnFailureListener(secondError -> {
                            Log.e(TAG, "Could not load wilaya data", secondError);
                            showEmptyState(getString(R.string.sheep_wilaya_not_found, wilaya));
                        }));
    }

    private void handleWilayaQueryResult(String wilaya, @NonNull QuerySnapshot snapshot) {
        if (snapshot.isEmpty()) {
            showEmptyState(getString(R.string.sheep_wilaya_not_found, wilaya));
            return;
        }

        renderWilayaSnapshot(snapshot.getDocuments().get(0));
    }

    private void renderWilayaSnapshot(DocumentSnapshot snapshot) {
        List<SheepBreedStatus> statuses = extractBreedStatuses(snapshot);
        if (statuses.isEmpty()) {
            showEmptyState(getString(R.string.sheep_no_breeds_available));
            return;
        }

        statuses.sort(new Comparator<SheepBreedStatus>() {
            @Override
            public int compare(SheepBreedStatus left, SheepBreedStatus right) {
                if (left.isAvailable() != right.isAvailable()) {
                    return left.isAvailable() ? -1 : 1;
                }

                String leftName = left.getBreedName() == null ? "" : left.getBreedName();
                String rightName = right.getBreedName() == null ? "" : right.getBreedName();
                return leftName.compareToIgnoreCase(rightName);
            }
        });

        breedAdapter.setItems(statuses);
        emptyStateTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        String wilayaName = getWilayaDisplayName(snapshot);
        if (wilayaName != null && !wilayaName.trim().isEmpty()) {
            wilayaSummaryTextView.setText(getString(R.string.sheep_wilaya_selected, wilayaName));
        }
    }

    private String getWilayaDisplayName(DocumentSnapshot snapshot) {
        if (snapshot == null) {
            return selectedWilaya;
        }

        String[] candidateKeys = new String[]{"name", "wilaya", "wilayaName", "title"};
        for (String key : candidateKeys) {
            String value = snapshot.getString(key);
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }

        return snapshot.getId();
    }

    private List<SheepBreedStatus> extractBreedStatuses(DocumentSnapshot snapshot) {
        List<SheepBreedStatus> statuses = new ArrayList<>();
        Map<String, Object> data = snapshot.getData();
        if (data == null || data.isEmpty()) {
            return statuses;
        }

        Object breedContainer = firstNonNull(
                data.get("breedAvailability"),
                data.get("sheepBreeds"),
                data.get("breeds"),
                data.get("availability")
        );

        if (breedContainer instanceof Map) {
            parseBreedMap((Map<?, ?>) breedContainer, statuses);
        } else {
            parseDirectBreedFields(data, statuses);
        }

        return statuses;
    }

    private void parseBreedMap(Map<?, ?> breedMap, List<SheepBreedStatus> statuses) {
        for (Map.Entry<?, ?> entry : breedMap.entrySet()) {
            String breedName = String.valueOf(entry.getKey());
            Object rawValue = entry.getValue();
            statuses.add(createBreedStatus(breedName, rawValue));
        }
    }

    private void parseDirectBreedFields(Map<String, Object> data, List<SheepBreedStatus> statuses) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (isMetadataField(entry.getKey())) {
                continue;
            }
            statuses.add(createBreedStatus(entry.getKey(), entry.getValue()));
        }
    }

    private boolean isMetadataField(String key) {
        if (key == null) {
            return true;
        }

        String normalizedKey = key.toLowerCase(Locale.ROOT);
        return normalizedKey.equals("id")
                || normalizedKey.equals("name")
                || normalizedKey.equals("wilaya")
                || normalizedKey.equals("wilayaname")
                || normalizedKey.equals("title")
                || normalizedKey.equals("createdat")
                || normalizedKey.equals("updatedat")
                || normalizedKey.equals("breedavailability")
                || normalizedKey.equals("sheepbreeds")
                || normalizedKey.equals("breeds")
                || normalizedKey.equals("availability");
    }

    private SheepBreedStatus createBreedStatus(String breedName, Object rawValue) {
        boolean available = resolveAvailability(rawValue);
        String statusText = getString(available ? R.string.sheep_status_available : R.string.sheep_status_unavailable);
        String details = resolveDetails(rawValue, available);
        return new SheepBreedStatus(breedName, available, statusText, details);
    }

    private boolean resolveAvailability(Object rawValue) {
        if (rawValue instanceof Boolean) {
            return (Boolean) rawValue;
        }

        if (rawValue instanceof Number) {
            return ((Number) rawValue).intValue() > 0;
        }

        if (rawValue instanceof Map) {
            Map<?, ?> valueMap = (Map<?, ?>) rawValue;
            Object directAvailable = firstNonNull(valueMap.get("available"), valueMap.get("isAvailable"), valueMap.get("status"));
            return resolveAvailability(directAvailable);
        }

        if (rawValue instanceof String) {
            String normalized = ((String) rawValue).trim().toLowerCase(Locale.ROOT);
            if (normalized.isEmpty()) {
                return false;
            }
            if (normalized.contains("غير") || normalized.contains("not") || normalized.contains("unavailable") || normalized.contains("false") || normalized.contains("لا ")) {
                return false;
            }
            if (normalized.contains("متاح") || normalized.contains("available") || normalized.contains("true") || normalized.contains("yes") || normalized.contains("نعم")) {
                return true;
            }
        }

        return false;
    }

    private String resolveDetails(Object rawValue, boolean available) {
        if (rawValue instanceof Map) {
            Map<?, ?> valueMap = (Map<?, ?>) rawValue;
            Object note = firstNonNull(valueMap.get("details"), valueMap.get("note"), valueMap.get("reason"), valueMap.get("message"));
            if (note != null) {
                String value = String.valueOf(note).trim();
                if (!value.isEmpty()) {
                    return value;
                }
            }
        }

        return getString(available ? R.string.sheep_details_available : R.string.sheep_details_unavailable);
    }

    private Object firstNonNull(Object... values) {
        if (values == null) {
            return null;
        }

        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }

        return null;
    }
}
