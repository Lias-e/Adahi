# Firebase Integration Guide for Adahi Application

## Overview

This guide provides step-by-step instructions to integrate Firebase with the Adahi Ordering Application for cloud data synchronization and authentication.

---

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add Project"
3. Enter project name: "Adahi"
4. Accept terms and create project
5. Wait for project initialization

---

## Step 2: Register Android App

1. In Firebase Console, click the Android icon
2. Enter package name: `com.android.adahi`
3. Enter app name: "Adahi"
4. Download `google-services.json`
5. Place it in: `app/` directory (at same level as build.gradle.kts)

---

## Step 3: Update Application Code

### In OrderFormActivity.java - Add Firebase Upload:

```java
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

// Inside handleConfirmOrder() method, after saving to local storage:

private void syncOrderToFirebase(Order order) {
    try {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(order.getOrderId());

        reference.setValue(order).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Order synced to Firebase: " + order.getOrderId());
            } else {
                Log.e(TAG, "Error syncing to Firebase: " + task.getException());
            }
        });
    } catch (Exception e) {
        Log.e(TAG, "Error uploading order: " + e.getMessage());
    }
}
```

### In AnimalListActivity.java - Fetch from Firebase:

```java
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

// Replace loadAnimals() method:

private void loadAnimals() {
    try {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("animals");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Animal> animals = new ArrayList<>();
                for (DataSnapshot animalSnapshot : snapshot.getChildren()) {
                    Animal animal = animalSnapshot.getValue(Animal.class);
                    if (animal != null) {
                        animals.add(animal);
                    }
                }
                animalAdapter.setAnimals(animals);
                Log.d(TAG, "Loaded " + animals.size() + " animals from Firebase");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error loading animals: " + error.getMessage());
                Toast.makeText(AnimalListActivity.this,
                    "Error loading animals", Toast.LENGTH_SHORT).show();
                // Fallback to local data
                loadLocalAnimals();
            }
        });
    } catch (Exception e) {
        Log.e(TAG, "Error: " + e.getMessage());
        loadLocalAnimals();
    }
}

private void loadLocalAnimals() {
    List<Animal> animals = SampleDataGenerator.generateSampleAnimals();
    animalAdapter.setAnimals(animals);
}
```

---

## Step 4: Configure Firebase Database Rules

In Firebase Console:

1. Go to "Realtime Database"
2. Click "Rules" tab
3. Replace with:

```json
{
  "rules": {
    "animals": {
      ".read": true,
      ".write": false
    },
    "orders": {
      ".read": true,
      ".write": true,
      "$uid": {
        ".validate": "newData.hasChildren(['orderId', 'customerName', 'totalPrice'])"
      }
    }
  }
}
```

---

## Step 5: Add Sample Animals to Firebase (One-time Setup)

Use Firebase Console or run this code once:

```java
// Add this to AnimalListActivity onCreate (run once, then remove)
private void uploadSampleAnimals() {
    try {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("animals");

        List<Animal> animals = SampleDataGenerator.generateSampleAnimals();
        for (Animal animal : animals) {
            reference.child(animal.getId()).setValue(animal)
                .addOnSuccessListener(aVoid ->
                    Log.d(TAG, "Animal uploaded: " + animal.getName()))
                .addOnFailureListener(e ->
                    Log.e(TAG, "Error uploading: " + e.getMessage()));
        }
    } catch (Exception e) {
        Log.e(TAG, "Error: " + e.getMessage());
    }
}
```

---

## Step 6: Optional - Add Authentication

### In MainActivity.java:

```java
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

private FirebaseAuth mAuth;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);

    // Initialize Firebase Auth
    mAuth = FirebaseAuth.getInstance();

    // Check if user is signed in
    if (mAuth.getCurrentUser() != null) {
        startActivity(new Intent(this, AnimalListActivity.class));
    } else {
        // Show login screen (implement LoginActivity)
    }
    finish();
}
```

---

## Step 7: Test Firebase Connection

1. Run the application
2. Create an order
3. In Firebase Console, go to "Realtime Database"
4. Verify order appears in `orders` node
5. Check structure matches Order model

---

## Firebase Database Structure

```
Firebase Realtime Database
├── animals/
│   ├── 1/
│   │   ├── id: "1"
│   │   ├── name: "White Sheep"
│   │   ├── type: "Sheep"
│   │   ├── price: 50000
│   │   ├── weight: 45.5
│   │   ├── description: "..."
│   │   ├── quantity: 5
│   │   └── imageUrl: ""
│   ├── 2/
│   └── ...
│
└── orders/
    ├── ORD-1234567890/
    │   ├── orderId: "ORD-1234567890"
    │   ├── customerName: "John Doe"
    │   ├── customerEmail: "john@example.com"
    │   ├── customerPhone: "+92 300 1234567"
    │   ├── customerAddress: "123 Main St"
    │   ├── totalPrice: 100000
    │   ├── orderDate: 1234567890000
    │   ├── status: "Pending"
    │   ├── specialInstructions: "..."
    │   └── items/
    │       ├── 0/
    │       │   ├── animalId: "1"
    │       │   ├── animalName: "White Sheep"
    │       │   ├── quantity: 2
    │       │   ├── pricePerUnit: 50000
    │       │   └── subtotal: 100000
    │       └── ...
    └── ORD-9876543210/
        └── ...
```

---

## Security Considerations

1. **Data Validation**: Always validate data on client and server
2. **Encryption**: Enable Firebase security rules
3. **User Privacy**: Don't store unnecessary personal data
4. **Rate Limiting**: Implement rate limits in Firebase functions
5. **Backups**: Enable automated backups in Firebase Console

---

## Troubleshooting

### Issue: google-services.json not found

**Solution**: Ensure file is in `app/` directory, not `app/src/main/`

### Issue: Firebase connection timeout

**Solution**:

- Check internet connection
- Verify Firebase project is active
- Check Firebase Console for errors

### Issue: Data not syncing

**Solution**:

- Verify database rules allow write access
- Check order object structure matches database
- Enable Firebase Analytics to debug

### Issue: Authentication errors

**Solution**:

- Verify package name matches Firebase Console
- Download fresh google-services.json
- Check Firebase Auth is enabled in Console

---

## Performance Optimization

```java
// Use offline persistence
FirebaseDatabase.getInstance().setPersistenceEnabled(true);

// Limit query results
reference.limitToFirst(20).addValueEventListener(...);

// Use transactions for critical operations
reference.runTransaction(new Transaction.Handler() {
    @Override
    public Transaction.Result doTransaction(MutableData currentData) {
        // Atomic operation
        return Transaction.success(currentData);
    }
});
```

---

## Monitoring & Analytics

In Firebase Console:

1. Go to "Analytics"
2. View user engagement metrics
3. Track order conversion rates
4. Monitor app performance
5. View crash reports

---

## Resources

- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Firebase Realtime Database](https://firebase.google.com/docs/database)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Firebase Security Rules](https://firebase.google.com/docs/database/security)

---

## Deployment Checklist

- [ ] Firebase project created
- [ ] google-services.json added to project
- [ ] Firebase dependencies updated
- [ ] Authentication implemented (optional)
- [ ] Database rules configured
- [ ] Sample data uploaded
- [ ] Order sync implemented
- [ ] Animal list fetches from Firebase
- [ ] Error handling for offline mode
- [ ] Security rules tested
- [ ] Performance optimized
- [ ] Analytics enabled

---

**Status**: Ready for Firebase Integration

Follow these steps to seamlessly connect your Adahi application to Firebase for production-ready cloud synchronization.
