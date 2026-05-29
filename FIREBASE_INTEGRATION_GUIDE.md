# Firebase Integration Guide for Adahi (Firestore)

## Overview

This guide shows how to connect the Adahi app to Firebase Cloud Firestore and how to seed the `animals` collection for development.

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
5. Place it in the `app/` directory (same level as `build.gradle.kts`)

---

## Step 3: Update Application Code (Firestore)

### In `OrderFormActivity.java` — Save orders to Firestore

```java
import com.google.firebase.firestore.FirebaseFirestore;

private void saveOrderToFirestore(Order order) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("orders")
      .document(order.getOrderId())
      .set(order)
      .addOnSuccessListener(aVoid -> Log.d(TAG, "Order saved: " + order.getOrderId()))
      .addOnFailureListener(e -> Log.e(TAG, "Error saving order: ", e));
}
```

### In `AnimalListActivity.java` — Read animals from Firestore

```java
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

private void loadAnimalsFromFirestore() {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("animals")
      .addSnapshotListener((QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
          if (e != null) {
              Log.e(TAG, "Listen failed.", e);
              showFallbackAnimals();
              return;
          }

          if (snapshots == null || snapshots.isEmpty()) {
              showFallbackAnimals();
              return;
          }

          List<Animal> animals = snapshots.toObjects(Animal.class);
          animalAdapter.setAnimals(animals);
      });
}
```

Use `showFallbackAnimals()` (the existing `SampleDataGenerator`) if Firestore is empty or unavailable.

---

## Step 4: Firestore Security Rules

Open the Firebase Console -> Firestore -> Rules and use a minimal starter:

```
service cloud.firestore {
  match /databases/{database}/documents {
    match /animals/{animalId} {
      allow read: if true;
      allow write: if false;
    }

    match /orders/{orderId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

Adjust rules before production — require authentication and proper validation for `orders` in real deployments.

---

## Step 5: Seed sample animals (recommended)

This project includes a small seeder module (`seeder`) that uses the Firebase Admin SDK to write to the `animals` collection. Instructions:

1. Obtain a Service Account JSON from the Firebase Console (Project Settings -> Service Accounts -> Generate new private key).
2. On your machine set the env var `FIREBASE_SERVICE_ACCOUNT` to the absolute path of that JSON file.
   - Optional: set `FIREBASE_PROJECT_ID` to `adhahi-73fad` if needed.

On macOS / Linux:

```bash
export FIREBASE_SERVICE_ACCOUNT=/path/to/serviceAccountKey.json
export FIREBASE_PROJECT_ID=adhahi-73fad
./gradlew :seeder:run
```

On Windows (PowerShell):

```powershell
$env:FIREBASE_SERVICE_ACCOUNT = 'C:\path\to\serviceAccountKey.json'
$env:FIREBASE_PROJECT_ID = 'adhahi-73fad'
./gradlew :seeder:run
```

The seeder will write sample documents to the `animals` collection. A JSON seed file is provided at `seeder/animals_seed.json` for inspection or other import workflows.

If you prefer a manual upload, open the Firestore Console and create documents in the `animals` collection using the sample objects in `seeder/animals_seed.json`.

---

## Step 6: Optional — Authentication

Initialize Firebase Auth in your app only if you require authenticated writes for orders:

```java
import com.google.firebase.auth.FirebaseAuth;

private FirebaseAuth mAuth;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAuth = FirebaseAuth.getInstance();
}
```

---

## Step 7: Test Firestore Connection

1. Run the application
2. Open the app and view the animal list
3. In Firebase Console, go to Firestore -> Data and inspect the `animals` collection
4. Create an order in-app and verify a document appears in the `orders` collection

---

## Firestore Data Structure (example)

Collections and documents in Firestore (each document under `animals` is an animal object):

```
Firestore (collections)
├── animals (collection)
│   ├── 1 (document)
│   │   ├── id: "1"
│   │   ├── name: "White Sheep"
│   │   ├── type: "Sheep"
│   │   ├── price: 50000
│   │   ├── weight: 45.5
│   │   ├── description: "..."
│   │   ├── quantity: 5
│   │   └── imageUrl: ""
│   ├── 2 (document)
│   └── ...
└── orders (collection)
    ├── ORD-1234567890 (document)
    │   ├── orderId: "ORD-1234567890"
    │   ├── customerName: "John Doe"
    │   ├── totalPrice: 100000
    │   └── items: [...]
    └── ...
```

---

## Security Considerations

- Validate inputs on client and server
- Restrict writes with Firestore Rules and require authentication for `orders`
- Do not commit service account keys to source control

---

## Troubleshooting

- `google-services.json` missing: ensure it's in `app/` (not `app/src/main/`)
- Firestore empty: run the `seeder` module locally with a service account, or import `seeder/animals_seed.json` via the Console
- Permission errors: check Firestore Rules and service account permissions

---

## Deployment Checklist

- [ ] Firebase project created
- [ ] `google-services.json` added to project
- [ ] Firestore dependencies updated
- [ ] Authentication implemented (if required)
- [ ] Firestore rules configured
- [ ] Sample data uploaded to `animals`
- [ ] Order sync implemented

---

**Status**: Firestore integration guide updated. Follow the seeder instructions to populate `animals` for development.
