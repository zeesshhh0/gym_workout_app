# GymWorkout - Project Documentation

This project is a comprehensive Android application designed for gym workout tracking. It allows users to create workouts, add exercises to those workouts, and record their progress over multiple sessions.

## 🛠 Project Versions & Tech Stack

- **Android Gradle Plugin (AGP):** 8.8.1
- **Kotlin:** 2.1.0
- **Compile/Target SDK:** 35
- **Minimum SDK:** 28
- **JVM Target:** 11
- **UI Components:** Material 3, ViewBinding, RecyclerView, BottomNavigationView
- **Data Persistence:** SQLite (via `SQLiteOpenHelper`)
- **Cloud Infrastructure:** Firebase (Authentication, Firestore)

---

## 🏗 Project Architecture & How It Works

The project follows a clean architecture approach with a clear separation of concerns between the UI, Domain, and Data layers.

### Data Layer
- **SQLite Database:** Managed by `DatabaseHelper.kt`. It handles schema creation, data insertion, and complex queries.
  - **Key Tables:** `USERS`, `MUSCLE_GROUPS`, `EXERCISES`, `WORKOUT`, `WORKOUT_EXERCISE`, `WORKOUT_SESSIONS`, and `SETS`.
  - **Relational Integrity:** Extensive use of foreign keys and `user_id` scoping to ensure multi-user data isolation.
- **Repository Pattern:** `WorkoutRepository.kt` acts as the single source of truth, orchestrating data flow between the local SQLite database and the Firestore cloud sync layer.
- **Models:** Type-safe Kotlin Data Classes (e.g., `Exercise`, `WorkoutSession`, `Set`, `SessionStats`) representing domain entities.

### Cloud Synchronization (Firestore)
- **Automatic Backup:** The app implements a "local-first" strategy. Writes are performed locally and then asynchronously synced to Firestore.
- **Path Structure:** `users/{uid}/workouts/{workoutId}/sessions/{sessionId}/exercises/{exerciseId}`.
- **Restore Logic:** Recursive restoration of the entire user graph from Firestore, allowing seamless cross-device data mobility.

### UI Layer
- **Architecture:** Activity-based containers (`HomeActivity`, `WorkoutActivity`) hosting various Fragments and specialized Activities for deep-dives (e.g., `SessionDetailActivity`, `ExerciseInstructionsActivity`).
- **State Management:** Uses traditional Android lifecycle patterns combined with `ActivityResultLauncher` for inter-activity communication.

---

## 📂 Key Files & Their Responsibilities

| File Path | Description |
|-----------|-------------|
| `data/db/DatabaseHelper.kt` | **Core Data Engine.** Contains the SQL schema, migration logic, and complex analytical queries (e.g., streaks). |
| `data/repository/WorkoutRepository.kt` | **Data Orchestrator.** Bridges SQLite and Firestore. Handles all business logic for data persistence. |
| `data/sync/FirestoreSyncManager.kt` | **Cloud Manager.** Directly interfaces with the Firestore SDK for batched writes, deletes, and recursive fetches. |
| `ui/main/HomeActivity.kt` | **Main Shell.** Manages top-level navigation via a `BottomNavigationView`. |
| `ui/workout/WorkoutActivity.kt` | **Live Session Tracker.** Manages the real-time creation of workouts, adding exercises, and recording sets. |
| `ui/adapters/` | **UI Bridges.** Contains highly optimized `RecyclerView` adapters for Workouts, Sessions, and Exercises. |

---

## 🎨 UI & UX Standards

The app adheres to modern Material 3 design principles to ensure a visually appealing and functional experience.

### Toolbar & Navigation
- **Dynamic Tinting:** Toolbars use adaptive color schemes (`colorPrimary` on `colorSurface` or `colorOnPrimary` on `colorPrimary`) to maintain high contrast and accessibility.
- **Consistent Navigation:** Every secondary screen features a standard Material "Back" button, ensuring predictable user flows.
- **Theme Overlays:** Uses `ThemeOverlay.Material3.AppBarOverlay` for menu icon consistency across different background states.

### Material 3 Dialogs
- **Universal Refactoring:** Every dialog in the app utilizes `MaterialAlertDialogBuilder`.
- **Global Styling:** Controlled via `materialAlertDialogTheme` in `themes.xml` for unified rounded corners, typography, and button placement.
- **Dark Mode Support:** Fully compliant with dark theme variants, ensuring readability and aesthetic consistency.

---

## 📝 Key Workflows

### Session Tracking
1. A user starts a workout from the dashboard or a saved template.
2. `WorkoutActivity` creates a local `WORKOUT_SESSIONS` entry.
3. Users add exercises; these are linked via `SETS`.
4. On "Finish", the session duration is calculated and the entire graph is synced to Firestore.

### Cloud Data Restoration
1. On login, the app checks for existing cloud data via `FirestoreSyncManager`.
2. If local data is empty, it auto-restores.
3. If a conflict exists, it prompts the user using a `MaterialAlertDialog`.
4. Restoration clears the local user-specific tables and repopulates them using `CONFLICT_REPLACE`.

---

## 🧪 Testing & Validation
- **Local Persistence:** Verified via SQLite unit tests and manual inspection of the `gymworkout1.db`.
- **Sync Reliability:** Validated by monitoring Firestore console updates in real-time during session completions.
- **UI Integrity:** Checked via layout inspections for Material 3 spacing and color compliance.
