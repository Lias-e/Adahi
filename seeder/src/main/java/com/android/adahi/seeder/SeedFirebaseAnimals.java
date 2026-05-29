package com.android.adahi.seeder;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple seeding tool to populate Firestore with sample animals.
 *
 * Usage (development only):
 * - Set environment variable `FIREBASE_SERVICE_ACCOUNT` to the path of your service account JSON file.
 * - Optionally set environment variable `FIREBASE_PROJECT_ID` if the service account does not include it.
 * - Run with Gradle: `./gradlew :seeder:run`
 *
 * This tool is intentionally kept out of the app sources and is intended to be run manually
 * in development environments only.
 */
public class SeedFirebaseAnimals {

    private static final String DEFAULT_PROJECT_ID = "adhahi-73fad";
    private static final String ANIMALS_COLLECTION = "animals";

    public static void main(String[] args) {
        try {
            String saPath = System.getenv("FIREBASE_SERVICE_ACCOUNT");
            String projectId = System.getenv("FIREBASE_PROJECT_ID");

            if (saPath == null || saPath.isEmpty()) {
                System.err.println("FIREBASE_SERVICE_ACCOUNT environment variable is not set.");
                System.exit(2);
            }
            if (projectId == null || projectId.isEmpty()) {
                projectId = DEFAULT_PROJECT_ID;
                System.out.println("FIREBASE_PROJECT_ID not set. Using default project id: " + projectId);
            }

            InputStream serviceAccount = new FileInputStream(saPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();

            FirebaseApp app;
            if (FirebaseApp.getApps().isEmpty()) {
                app = FirebaseApp.initializeApp(options);
            } else {
                app = FirebaseApp.getInstance();
            }

            Firestore firestore = FirestoreClient.getFirestore(app);
            CollectionReference animalsRef = firestore.collection(ANIMALS_COLLECTION);

            List<Map<String, Object>> animals = sampleAnimals();
            WriteBatch batch = firestore.batch();

            for (Map<String, Object> animal : animals) {
                Object idObj = animal.get("id");
                String id = idObj != null ? String.valueOf(idObj) : animalsRef.document().getId();
                DocumentReference documentReference = animalsRef.document(id);
                batch.set(documentReference, animal);
                System.out.println("Queued upload for animal id=" + id + " name=" + animal.get("name"));
            }

            batch.commit().get();
            System.out.println("Firestore seeding completed. Check the animals collection in Firebase console.");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static List<Map<String, Object>> sampleAnimals() {
        List<Map<String, Object>> list = new ArrayList<>();

        list.add(animal("1", "White Sheep", "Sheep", 50000, 45.5, "Healthy white sheep, perfect for Eid ul-Adha. Well-fed and vaccinated.", "", 5, "أولاد جلال", "18 month", "Male", "Vet-checked", "سوق بئر توتة"));
        list.add(animal("2", "Black Sheep", "Sheep", 55000, 48.0, "Premium quality black sheep with excellent health status.", "", 3, "Local", "20 month", "Male", "Vet-checked", "نقطة بيع الحميز"));
        list.add(animal("3", "Brown Goat", "Goat", 45000, 40.0, "Brown goat in perfect condition, ideal for the festival.", "", 7, "RedBreed", "14 month", "Female", "Healthy", "مزرعة الدولة - القبة"));
        list.add(animal("4", "White Goat", "Goat", 48000, 42.0, "White goat with white and brown markings, healthy and strong.", "", 4, "Mixed", "16 month", "Female", "Healthy", "نقطة بيع الحميز"));
        list.add(animal("5", "Red Cow", "Cow", 150000, 550.0, "Large red cow, healthy and well-nourished. Sufficient meat for 7-8 people.", "", 2, "Local", "36 month", "Male", "Healthy", "سوق بئر توتة"));
        list.add(animal("6", "Black Cow", "Cow", 160000, 580.0, "Premium quality black cow with excellent meat quality.", "", 2, "Premium", "40 month", "Male", "Healthy", "نقطة بيع الحميز"));
        list.add(animal("7", "Water Buffalo", "Buffalo", 200000, 650.0, "Strong water buffalo with premium meat quality. Sufficient for 10+ people.", "", 1, "BuffaloBreed", "48 month", "Male", "Healthy", "مزرعة الدولة - القبة"));

        return list;
    }

    private static Map<String, Object> animal(String id, String name, String type, double price, double weight,
                                              String description, String imageUrl, int quantity,
                                              String breed, String age, String gender, String healthStatus, String salesPoint) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("type", type);
        m.put("price", price);
        m.put("weight", weight);
        m.put("description", description);
        m.put("imageUrl", imageUrl);
        m.put("quantity", quantity);
        m.put("breed", breed);
        m.put("age", age);
        m.put("gender", gender);
        m.put("healthStatus", healthStatus);
        m.put("salesPoint", salesPoint);
        return m;
    }
}
