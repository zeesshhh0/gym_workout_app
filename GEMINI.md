# GymWorkout - Project Documentation

This project is a comprehensive Android application designed for gym workout tracking. It allows users to create workouts, add exercises to those workouts, and record their progress over multiple sessions.

## 🛠 Project Versions & Tech Stack

- **Android Gradle Plugin (AGP):** 8.8.1
- **Kotlin:** 1.9.24
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
