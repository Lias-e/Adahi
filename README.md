# QUICK START GUIDE - Adahi Application

## 📌 Project Structure at a Glance

```
Adahi/
├── app/
│   ├── build.gradle.kts .......................... Dependencies & Build Configuration
│   ├── src/main/
│   │   ├── AndroidManifest.xml ................... Activity Declarations
│   │   ├── java/com/android/adahi/
│   │   │   ├── MainActivity.java ................. Entry Point
│   │   │   ├── activities/
│   │   │   │   ├── AnimalListActivity.java ....... Browse Animals
│   │   │   │   ├── OrderFormActivity.java ........ Place Order
│   │   │   │   └── OrderConfirmationActivity.java  Order Summary
│   │   │   ├── adapters/
│   │   │   │   └── AnimalAdapter.java ........... RecyclerView Adapter
│   │   │   ├── models/
│   │   │   │   ├── Animal.java .................. Animal Data Model
│   │   │   │   └── Order.java ................... Order Data Model
│   │   │   └── utils/
│   │   │       ├── LocalStorageManager.java ..... Data Persistence
│   │   │       └── SampleDataGenerator.java ..... Demo Data
│   │   └── res/
│   │       ├── layout/
│   │       │   ├── activity_main.xml
│   │       │   ├── activity_animal_list.xml
│   │       │   ├── activity_order_form.xml
│   │       │   ├── activity_order_confirmation.xml
│   │       │   └── item_animal.xml
│   │       ├── drawable/
│   │       │   └── item_background.xml
│   │       └── values/
│   │           └── strings.xml
│   └── build.gradle.kts
│
├── IMPLEMENTATION_SUMMARY.md ................... Full Project Overview
├── FIREBASE_INTEGRATION_GUIDE.md .............. Firebase Setup Instructions
└── README.md (this file)

```

---

## 🚀 Quick Navigation

### Want to... → Check this file:

| Task                      | File                                                                         |
| ------------------------- | ---------------------------------------------------------------------------- |
| **Add a new Activity**    | `AndroidManifest.xml` + Create new Activity class                            |
| **Modify Animal display** | `item_animal.xml` (layout) + `AnimalAdapter.java` (logic)                    |
| **Change Order flow**     | `OrderFormActivity.java` (input) → `OrderConfirmationActivity.java` (output) |
| **Add new data fields**   | `Animal.java` or `Order.java` (models)                                       |
| **Store order data**      | `LocalStorageManager.java` (currently uses SharedPreferences)                |
| **Get sample animals**    | `SampleDataGenerator.java`                                                   |
| **Style app**             | `res/values/strings.xml` + `res/drawable/`                                   |
| **Add Firebase**          | Follow `FIREBASE_INTEGRATION_GUIDE.md`                                       |

---

## 🔧 Key Components Explained

### 1. **Activities** (Navigation)

```
MainActivity
    ↓ (Redirects to)
AnimalListActivity
    ↓ (Click animal)
OrderFormActivity
    ↓ (Submit)
OrderConfirmationActivity
    ↓ (Back)
AnimalListActivity
```

### 2. **Data Flow**

```
Animal Model
    ↓
SampleDataGenerator (creates demo data)
    ↓
AnimalAdapter (displays in RecyclerView)
    ↓
AnimalListActivity (shows list)
    ↓
Intent passes animal data to OrderFormActivity
    ↓
Order Model (created from form)
    ↓
LocalStorageManager (saves to SharedPreferences)
    ↓
OrderConfirmationActivity (displays summary)
```

### 3. **UI Hierarchy**

```
All Layouts use ConstraintLayout
├── Strings in values/strings.xml
├── Dimensions: hardcoded (can extract to dimens.xml)
├── Colors: Android default (can add colors.xml)
└── Drawables: item_background.xml
```

---

## 📝 Important Code Snippets

### Launch the App

File: `MainActivity.java`

```java
startActivity(new Intent(this, AnimalListActivity.class));
finish();
```

### Display RecyclerView

File: `AnimalListActivity.java`

```java
animalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
animalAdapter = new AnimalAdapter(this, animal -> { });
animalRecyclerView.setAdapter(animalAdapter);
```

### Save Order Locally

File: `LocalStorageManager.java`

```java
LocalStorageManager manager = new LocalStorageManager(context);
String orderId = manager.saveOrder(order);
```

### Pass Data Between Activities

File: `AnimalAdapter.java` (sending)

```java
Intent intent = new Intent(context, OrderFormActivity.class);
intent.putExtra("animal_id", animal.getId());
context.startActivity(intent);
```

File: `OrderFormActivity.java` (receiving)

```java
animalId = getIntent().getStringExtra("animal_id");
```

---

## ⚙️ Configuration

### Add New String Resource

File: `res/values/strings.xml`

```xml
<string name="new_string">New Value</string>
```

### Update Dependencies

File: `app/build.gradle.kts`

```gradle
dependencies {
    implementation("com.library:library:version")
}
```

### Change App Theme

File: `AndroidManifest.xml` (application tag)

```xml
android:theme="@style/Theme.Adahi"
```

---

## 🧪 Testing Checklist

- [ ] App launches and shows animal list
- [ ] Click animal navigates to order form
- [ ] Form validates all inputs
- [ ] Order confirmation shows correct details
- [ ] Back button returns to animal list
- [ ] Data persists after app restart
- [ ] No crashes on rotation
- [ ] Error messages display properly
- [ ] RecyclerView scrolls smoothly
- [ ] All fields accept expected input types

---

## 🐛 Common Issues & Solutions

### Issue: App crashes on startup

**Check**: Is `AnimalListActivity` declared in `AndroidManifest.xml`?

### Issue: RecyclerView doesn't show items

**Check**: Does `AnimalAdapter` have data? Call `setAnimals()` after creation.

### Issue: Form validation not working

**Check**: Are all EditText IDs correct in `activity_order_form.xml`?

### Issue: Orders not saving

**Check**: Does `LocalStorageManager.saveOrder()` complete without exceptions?

### Issue: Build fails

**Check**: Are all dependencies correctly added in `build.gradle.kts`?

---

## 📚 Code Standards Used

- **Naming**: camelCase for variables, PascalCase for classes
- **Comments**: JavaDoc for public methods, inline for complex logic
- **Error Handling**: Try-catch with logging
- **Logging**: `Log.d()` for debug, `Log.e()` for errors
- **Layout**: All use ConstraintLayout, no nested LinearLayouts

---

## 🔐 Data Storage Explanation

### Current Implementation (SharedPreferences)

- **Storage**: Device local storage
- **Format**: JSON (via Gson)
- **Access**: SharedPreferences API
- **Data Type**: Key-Value pairs
- **Persistence**: Survives app restart
- **File**: `/data/data/com.android.adahi/shared_prefs/AdahiAppPrefs.xml`

### Firebase Integration (Active)

- **Storage**: Cloud (Firebase Firestore)
- **Format**: JSON
- **Access**: Firebase SDK
- **Real-time**: Live updates across devices via snapshot listeners
- **Backup**: Managed by Firestore's cloud storage and replication

---

## 📱 Screen Specifications

| Screen           | Purpose        | Key Elements             |
| ---------------- | -------------- | ------------------------ |
| **Animal List**  | Browse animals | RecyclerView, Item cards |
| **Order Form**   | Enter details  | EditTexts, Buttons       |
| **Confirmation** | Review order   | TextViews, Order summary |

---

## 🎨 UI Colors & Styles

Current Implementation:

- **Primary**: Android default (darker_gray for headers)
- **Success**: holo_green_dark (prices, confirm buttons)
- **Warning**: holo_orange_dark (status)
- **Error**: holo_red_dark (cancel buttons)
- **Text**: black on white, white on dark

Can be enhanced by creating `res/values/colors.xml`:

```xml
<color name="primary">#3F51B5</color>
<color name="success">#4CAF50</color>
```

---

## 📊 Data Models Summary

### Animal

```java
String id, name, type
double price, weight
String description, imageUrl
int quantity
```

### Order

```java
String orderId, customerName, customerEmail
String customerPhone, customerAddress
List<OrderItem> items
double totalPrice
long orderDate
String status, specialInstructions
```

### OrderItem (nested in Order)

```java
String animalId, animalName
int quantity
double pricePerUnit, subtotal
```

---

## 🚀 Ready to Deploy?

Before submission/deployment:

1. ✅ Test all features thoroughly
2. ✅ Check for memory leaks
3. ✅ Verify all strings are in strings.xml
4. ✅ Add app icon (replace ic_launcher)
5. ✅ Update AndroidManifest.xml metadata
6. ✅ Create release APK via Build → Build Bundle(s)/APK(s) → Build APK(s)
7. ✅ Test APK on actual device
8. ✅ Add Firebase integration (optional for production)

---

## 📞 Support Resources

- **Errors**: Check Logcat window (Android Studio)
- **Layout Issues**: Use Layout Inspector (Tools → Layout Inspector)
- **Database**: Use Android Studio Database Inspector
- **Docs**: Refer to `IMPLEMENTATION_SUMMARY.md`
- **Firebase**: Refer to `FIREBASE_INTEGRATION_GUIDE.md`

---

## ✨ Features Implemented

✅ 4 Activities with proper navigation
✅ RecyclerView for animal list
✅ Order form with validation
✅ Order confirmation summary
✅ Local data persistence
✅ Sample data generation
✅ Proper error handling
✅ Complete documentation
✅ ConstraintLayout throughout
✅ Firebase ready (dependencies added)

---

**Last Updated**: 2026-05-07
**Version**: 1.0.0
**Status**: ✅ Ready for Testing & Submission

---

For detailed information, see:

- `IMPLEMENTATION_SUMMARY.md` - Full project overview
- `FIREBASE_INTEGRATION_GUIDE.md` - Cloud integration setup
- Individual Java files - Source code with comments
