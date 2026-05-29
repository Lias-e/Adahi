# Seeder module

This module provides a simple one-time seeding tool to upload sample `animals` to Firebase Firestore.

## Prerequisites

- A Firebase project with Firestore enabled.
- A Service Account JSON file with Database Admin privileges.

## Usage (development only)

1. Set environment variables:

```bash
export FIREBASE_SERVICE_ACCOUNT=/path/to/serviceAccountKey.json
# Optional: override the Firebase project id if needed
# export FIREBASE_PROJECT_ID=<project-id>
```

On Windows (PowerShell):

```powershell
$env:FIREBASE_SERVICE_ACCOUNT = 'C:\path\to\serviceAccountKey.json'
# Optional override
# $env:FIREBASE_PROJECT_ID = '<project-id>'
```

2. Run the seeder with Gradle:

```bash
./gradlew :seeder:run
```

## Notes

- This script is intended for development environments only. Do not include service account credentials in source control.
- The seeder writes only to the `animals` collection in Firestore.
- It does not create or seed `orders`.
- Existing entries with the same `id` key will be overwritten.
- Default target project: `adhahi-73fad`
