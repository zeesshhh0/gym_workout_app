# GymWorkout - Project Documentation

This project is a comprehensive Android application designed for gym workout tracking. It allows users to create workouts, add exercises to those workouts, and record their progress over multiple sessions.

## 🛠 Project Versions & Tech Stack

- **Android Gradle Plugin (AGP):** 8.8.1
- **Kotlin:** 2.1.0
- **Compile/Target SDK:** 35
- **Minimum SDK:** 28
- **JVM Target:** 11
- **UI Components:** ViewBinding, RecyclerView, Material Components, BottomNavigationView
- **Data Persistence:** SQLite (via `SQLiteOpenHelper`)

---

## 🏗 Project Architecture & How It Works

The project follows a traditional Android architecture using Activities and Fragments, with a dedicated data layer for SQLite database management.

### Data Layer
- **SQLite Database:** Managed by `DatabaseHelper.kt`. It handles schema creation, data insertion, and complex queries (like workout streaks and session statistics).
- **Models:** Plain Kotlin Data Classes located in `com.example.gymworkout.data.model` representing entities like `Exercise`, `MuscleGroup`, `Workout`, and `Set`.

### UI Layer
- **Main Entry:** `HomeActivity` serves as the primary container, hosting a `BottomNavigationView` to switch between `HomeFragment`, `SessionsFragment`, `ExercisesFragment`, and `ProfileFragment`.
- **User Authentication:** `LoginActivity` and `SignupActivity` manage user sessions using Firebase Authentication. Local user data is stored in the `USERS` table, keyed by the Firebase UID.
- **Workout Logic:** `WorkoutActivity` handles the live tracking of a workout session, allowing users to add exercises and sets in real-time.
- **Session History:** `SessionsFragment` lists past workouts, while `SessionDetailActivity` provides a deep dive into specific session stats and sets.

---

## 📂 Key Files & Their Responsibilities

| File Path | Description |
|-----------|-------------|
| `data/db/DatabaseHelper.kt` | **Core Data Engine.** Contains the SQL schema and all CRUD operations. If you need to add a new table or query, this is the place. |
| `ui/main/HomeActivity.kt` | **Main Container.** Manages fragment transitions and top-level navigation. |
| `ui/main/HomeFragment.kt` | **Dashboard.** Displays user greetings, workout streaks, and a summary of the latest workout. |
| `ui/workout/WorkoutActivity.kt` | **Live Tracking.** The most complex UI, managing the creation of workout sessions, adding exercises, and recording sets. |
| `ui/workout/SessionDetailActivity.kt` | **Historical Data.** Displays the breakdown of a completed session, including all exercises and their respective sets. |
| `data/model/` | **Domain Models.** Contains all data structures used throughout the app. |
| `ui/adapters/` | **UI Bridges.** Contains various `RecyclerView.Adapter` implementations for listing workouts, sessions, exercises, and sets. |

---

## 📝 Working with the Project

### Adding a New Exercise
1. Add the exercise metadata to the `EXERCISES` table via `DatabaseHelper.addExercise()`.
2. Ensure the `MUSCLE_GROUPS` table has the relevant muscle group.
3. The new exercise will automatically appear in the `ExercisesFragment` (Exercise Library).

### Modifying the Database Schema
1. Update the `DATABASE_VERSION` in `DatabaseHelper.kt`.
2. Implement the migration logic in `onUpgrade()`. *Note: The current implementation drops all tables on upgrade for simplicity during development.*

### Navigation & Fragments
- All main navigation is handled in `HomeActivity.kt` using the `navListener`.
- When adding a new top-level feature, add a menu item to `res/menu/bottom_nav_menu.xml` and update the `when` block in `HomeActivity.navListener`.

### Authentication & Session Management
- User session data (like `uid`) is managed by Firebase Authentication.
- Always check for `FirebaseAuth.getInstance().currentUser != null` in activities/fragments to ensure the user is properly authenticated.

---

## 🧪 Testing & Validation
- **Unit Tests:** Located in `src/test/java/` (e.g., `ExampleUnitTest.kt`).
- **Instrumentation Tests:** Located in `src/androidTest/java/` (e.g., `ExampleInstrumentedTest.kt`).
- Use `DatabaseHelper` sample data (inserted in `onCreate`) to quickly verify UI components during development.

---

## ☁️ Cloud Synchronization (Firestore)

The app now features robust cloud synchronization, backing up local SQLite data to **Firebase Firestore**.

### Sync Architecture
- **Local First:** SQLite remains the immediate source of truth for all operations to ensure offline functionality.
- **Cloud Backup:** Data is synced to Firestore under the following path structure:
  `users/{uid}/workouts/{workoutId}/sessions/{sessionId}/exercises/{exerciseId}`
- **Trigger Points:** Background sync occurs automatically when a user creates a workout, completes a session, or deletes records. This is handled transparently within the `WorkoutRepository`.

### Key Components
- `data/sync/FirestoreSyncManager.kt`: Handles all direct interactions with the Firestore SDK, including nested document retrieval for restoration and batched deletes.
- `data/sync/FirestoreMappers.kt`: Contains extension functions to convert Kotlin data models into Firestore-compatible `HashMap`s before writing.

### Data Restoration
Users can restore their data across devices using the **"Restore from Cloud"** button located in the `ProfileFragment`. This initiates a full recursive fetch of their Firestore graph, clears the local SQLite user data, and seamlessly re-inserts the cloud records into the local database using `CONFLICT_REPLACE`.

### Resilience & Error Handling
- **Offline Mode:** Firestore persistence is enabled, allowing the app to queue writes while offline and automatically sync upon reconnection.
- **Auto-Restore on Login:** Upon logging into an existing account (not during signup), the app automatically checks for cloud data. If the local database is empty, it restores the cloud data; if local data exists, it prompts the user to choose between their local or cloud versions.
- **Sync Safety:** All Firestore writes are guarded by authentication checks. Failures are logged with their document paths and reported to the user via non-blocking Snackbars, ensuring the app remains fully functional locally even if cloud sync is temporarily unavailable.
- **Data Integrity:** The restoration process uses the `CONFLICT_REPLACE` strategy in SQLite, ensuring that duplicate records are safely overwritten during the sync process.
