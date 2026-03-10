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

The project follows a "local-first" clean architecture with a direct-repository pattern.

### Data Layer
- **SQLite Database:** Managed by `DatabaseHelper.kt`. It handles schema creation, data migration, and complex analytical queries.
  - **Key Tables:** `USERS`, `MUSCLE_GROUPS`, `EXERCISES`, `WORKOUT`, `WORKOUT_EXERCISE`, `WORKOUT_SESSIONS`, and `SETS`.
  - **Relational Integrity:** Extensive use of foreign keys and `user_id` scoping to ensure multi-user data isolation.
- **Repository Pattern:** `WorkoutRepository.kt` acts as the single source of truth, orchestrating data flow between the local SQLite database and the Firestore cloud sync layer. It directly provides data to Fragments and Activities.
- **Models:** Type-safe Kotlin Data Classes representing domain and UI entities.
  - **Core Entities:** `Exercise`, `WorkoutSession`, `Set`, `MuscleGroup`.
  - **Analytical Models:** `SessionStats` (calculates durations and volumes), `HistoryModels` (sealed `HistoryItem` for multi-type RecyclerViews).

### State Management & Lifecycle
- **Direct Repository Pattern:** Fragments and Activities directly instantiate and interact with `WorkoutRepository`. 
- **Modern Android Tools:** While `lifecycle-viewmodel-ktx` is a dependency, the app currently bypasses ViewModels in favor of a simpler, Repository-centric state management approach.
- **UI Flow:** Uses `ActivityResultLauncher` for inter-activity communication (e.g., returning from exercise selection).

### Cloud Synchronization (Firestore)
- **Automatic Backup:** Writes are performed locally and then asynchronously synced to Firestore.
- **Path Structure:** `users/{uid}/workouts/{workoutId}/sessions/{sessionId}/exercises/{exerciseId}`.
- **Restore Logic:** Recursive restoration of the entire user graph from Firestore, allowing seamless cross-device data mobility.

---

## 📂 Key Files & Their Responsibilities

| File Path | Description |
|-----------|-------------|
| `data/db/DatabaseHelper.kt` | **Core Data Engine.** Contains SQL schema and analytical queries (e.g., streak calculations). |
| `data/repository/WorkoutRepository.kt` | **Data Orchestrator.** Bridges SQLite and Firestore; provides business logic for persistence and stats. |
| `data/model/SessionStats.kt` | **Stats Utility.** Calculates session duration, total volume, and provides formatted UI strings. |
| `ui/main/HomeFragment.kt` | **Dashboard.** Manages dynamic greetings, streak progress, and "Last Workout" summaries. |
| `ui/adapters/HistoryAdapter.kt` | **History Engine.** Implements a multi-type RecyclerView for grouped workout history (headers and cards). |
| `data/sync/FirestoreSyncManager.kt` | **Cloud Manager.** Interfaces with Firestore SDK for batched writes and recursive fetches. |

---

## 🎨 UI & UX Standards

The app adheres to modern Material 3 design principles with several specialized patterns:

### Dashboard & Analytics
- **Streak Tracking:** Uses `CircularProgressIndicator` on the Home screen to visualize weekly workout goals.
- **Dynamic Greetings:** Context-aware greetings based on the time of day.
- **Last Workout Card:** Provides a high-level summary of the most recent session using `SessionStats`.

### Advanced RecyclerViews
- **Multi-Type History:** `HistoryAdapter` uses a sealed class (`HistoryItem`) to render month headers and detailed workout cards within the same list.
- **Dynamic Rows:** Workout cards in history lists dynamically inflate exercise summaries to avoid nested scroll performance issues.

### Toolbar & Navigation
- **Dynamic Tinting:** Toolbars adapt color schemes based on the theme state.
- **Material 3 Dialogs:** Every user-facing dialog uses `MaterialAlertDialogBuilder` for consistent styling.

---

## 📝 Key Workflows

### Session Tracking
1. A user starts a workout from the dashboard.
2. `WorkoutActivity` creates a local `WORKOUT_SESSIONS` entry.
3. Users add exercises; these are linked via `SETS`.
4. On "Finish", the session duration is calculated and the entire graph is synced to Firestore.

### Cloud Data Restoration
1. On login, `FirestoreSyncManager` checks for existing cloud data.
2. If local data is empty, it auto-restores.
3. Restoration clears the local user-specific tables and repopulates them using `CONFLICT_REPLACE`.

---

## 🧪 Testing & Validation
- **Local Persistence:** Verified via SQLite unit tests and manual inspection of `gymworkout1.db`.
- **Sync Reliability:** Validated by monitoring Firestore console updates in real-time.
- **UI Integrity:** Checked via layout inspections for Material 3 spacing and color compliance.
