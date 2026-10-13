# Community Events Android App

## 1. Project Overview & Purpose
**Community Events** is a modern, mobile-friendly Android application built with **Jetpack Compose**, **Firebase Cloud Firestore**, **Firebase Cloud Storage**, and **OkHttp**. It allows community members and organizers to discover local events, view event details, add new community events with optional image uploads, and interact with a public REST API demo.

## 2. Features
- **Home / Events Screen**: Displays a chronological list of community events retrieved in real-time from Firestore, complete with images, dates, locations, and descriptions. Includes loading states, empty state handling, error handling, and pull/retry support.
- **Event Details Screen**: Detailed view showing the full event image, title, formatted date, location, and comprehensive description.
- **Add Event Form**: Allows users to input a title, date (`YYYY-MM-DD`), location, and description, select an image from their device, validate inputs, and securely save the event to Firestore and Firebase Storage.
- **REST API Demo Screen**: Demonstrates real HTTP networking using OkHttp against the public JSONPlaceholder API (`https://jsonplaceholder.typicode.com/posts/1`), displaying response ID, title, and body with loading and error handling.
- **Input Validation & Error Handling**: Robust validation for required fields and exact `YYYY-MM-DD` date formatting, preventing blank submissions and handling network/database/storage exceptions gracefully without crashing.

## 3. Technologies Used
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose & Material 3 (Blue & White styling)
- **Database**: Firebase Cloud Firestore (NoSQL document database)
- **Object Storage**: Firebase Cloud Storage (Blob/object storage for images)
- **Networking**: OkHttp & Kotlin Coroutines (for non-main thread API requests)
- **Image Loading**: Coil Compose

## 4. Firestore Event Document Structure
Each document in the `events` collection contains the following fields:
- `title` (String): The title of the community event.
- `date` (String): The event date in `YYYY-MM-DD` format.
- `location` (String): The venue or address of the event.
- `description` (String): Full details about the event.
- `imageUrl` (String): Public HTTPS download URL pointing to the image file in Firebase Storage (or empty string `""` if no image was attached).

## 5. REST API Integration
- **Endpoint**: `https://jsonplaceholder.typicode.com/posts/1`
- **Method**: GET
- **Response Format**: JSON object containing `userId`, `id`, `title`, and `body`.
- **Explanation**: The app executes an asynchronous HTTP request off the main thread using OkHttp and parses the JSON response using `org.json.JSONObject`.

## 6. Firebase Configuration & Setup Instructions
1. **Create a Firebase Project**: Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. **Register Android App**:
   - Package name: `za.co.communityevents`
   - Download the generated `google-services.json` file.
   - Place `google-services.json` inside the `app/` directory of your project.
3. **Enable Firestore**:
   - In the Firebase Console, navigate to **Firestore Database** and create a database (start in test mode for development).
4. **Enable Firebase Storage**:
   - Navigate to **Storage** and set up a default storage bucket (`event-images/`).
   - *Note*: Firebase Storage is object/blob storage because it stores raw binary files (images) as objects, whereas Firestore stores structured event metadata (strings/numbers).
5. **Security Rules (Test/Development)**:
   - For classroom demonstrations, ensure rules allow read/write or secure them appropriately before production deployment.

## 7. How to Run the App
1. Open the project in **Android Studio** (Koala or newer).
2. Ensure `google-services.json` is placed in the `app/` folder.
3. Sync project with Gradle files.
4. Connect an Android device (API 26+) or start an Android Emulator.
5. Click the **Run 'app'** button in Android Studio.

## 8. Suggested Git Commit Messages
- `feat: initialize project structure and Gradle dependencies`
- `feat: add Jetpack Compose UI themes and navigation screens`
- `feat: implement Firebase Firestore repository and event list state`
- `feat: add image selection and Firebase Storage upload functionality`
- `feat: implement Add Event form with robust input validation`
- `feat: add OkHttp REST API demo integration with JSONPlaceholder`
- `docs: add comprehensive README and setup documentation`
