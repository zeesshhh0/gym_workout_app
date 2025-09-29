package com.example.gymworkout.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "gymworkout1.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables here
        db.execSQL("""
            CREATE TABLE USERS (
                user_id INTEGER PRIMARY KEY,
                username TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE MUSCLE_GROUPS (
                muscle_group_id INTEGER PRIMARY KEY,
                name TEXT NOT NULL UNIQUE,
                body_region TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE EXERCISES (
                exercise_id INTEGER PRIMARY KEY,
                muscle_group_id INTEGER NOT NULL,
                exercise_name TEXT NOT NULL,
                description TEXT,
                instructions TEXT,
                FOREIGN KEY (muscle_group_id) REFERENCES MUSCLE_GROUPS(muscle_group_id)
            )
        """.trimIndent())


        db.execSQL("""
            CREATE TABLE WORKOUT (
                workout_id INTEGER PRIMARY KEY,
                user_id INTEGER NOT NULL,
                workout_name TEXT NOT NULL,
                description TEXT,
                FOREIGN KEY (user_id) REFERENCES USERS(user_id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE WORKOUT_EXERCISE (
                workout_exercise_id INTEGER PRIMARY KEY,
                workout_id INTEGER NOT NULL,
                exercise_id INTEGER NOT NULL,
                FOREIGN KEY (workout_id) REFERENCES WORKOUT(workout_id),
                FOREIGN KEY (exercise_id) REFERENCES EXERCISES(exercise_id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE WORKOUT_SESSIONS (
                session_id INTEGER PRIMARY KEY,
                workout_id INTEGER NOT NULL,
                workout_date DATE NOT NULL,
                start_time TIME,
                end_time TIME,
                notes TEXT,
                FOREIGN KEY (workout_id) REFERENCES WORKOUT(workout_id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE SETS (
                set_id INTEGER PRIMARY KEY,
                session_id INTEGER NOT NULL,
                exercise_id INTEGER NOT NULL,
                set_number INTEGER NOT NULL,
                reps INTEGER NOT NULL,
                weight_used REAL,
                FOREIGN KEY (session_id) REFERENCES WORKOUT_SESSIONS(session_id),
                FOREIGN KEY (exercise_id) REFERENCES EXERCISES(exercise_id)
            )
        """.trimIndent())

        // Insert sample data
        db.execSQL("""
            INSERT INTO USERS (user_id, username, email, password_hash, created_at)
            VALUES
            (1, 'FitJohn2024', 'john@example.com', 'hash_john_123', '2025-09-01 10:00:00'),
            (2, 'StrongSara', 'sara@example.com', 'hash_sara_123', '2025-09-05 14:30:00')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO MUSCLE_GROUPS (muscle_group_id, name, body_region)
            VALUES
            (1, 'Chest', 'Upper Body'),
            (2, 'Biceps', 'Upper Body'),
            (3, 'Quadriceps', 'Lower Body'),
            (4, 'Abs', 'Core')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO EXERCISES (exercise_id, muscle_group_id, exercise_name, description, instructions)
            VALUES
            (1, 1, 'Bench Press', 'Compound chest exercise', '1. Lie flat on a bench with your feet planted firmly on the floor for stability. Grip the barbell with hands slightly wider than shoulder-width, palms facing forward. 2. Unrack the bar by straightening your arms, holding it directly above your chest. 3. Inhale and slowly lower the bar to your mid-chest, keeping your elbows at about a 45-degree angle to your body to avoid shoulder strain. 4. Pause briefly when the bar touches your chest, then exhale and press the bar upward explosively, extending your arms fully without locking your elbows. 5. Squeeze your chest at the top of the movement. 6. Repeat for the desired number of reps, maintaining control and avoiding arching your back excessively.'),
            (2, 2, 'Bicep Curl', 'Isolation for biceps', '1. Stand with your feet shoulder-width apart, holding a dumbbell in each hand with palms facing forward and arms fully extended at your sides. 2. Keep your elbows close to your torso and your shoulders relaxed. 3. Exhale and curl the dumbbells upward toward your shoulders by contracting your biceps, keeping your upper arms stationary. 4. Squeeze your biceps at the top of the movement for a second. 5. Inhale and slowly lower the dumbbells back to the starting position, resisting the weight to maintain tension. 6. Repeat for reps, avoiding swinging your body or using momentum to lift the weights.'),
            (3, 3, 'Squat', 'Compound lower body exercise', '1. Stand with your feet shoulder-width apart, toes slightly turned out, and place a barbell across your upper back (trapezius), gripping it wider than shoulder-width. 2. Engage your core, keep your chest up, and look straight ahead. 3. Inhale and initiate the squat by bending your knees and hips simultaneously, lowering your body as if sitting back into a chair until your thighs are at least parallel to the floor. 4. Keep your knees tracking over your toes and your back neutral. 5. Exhale and drive through your heels to stand back up, extending your hips and knees fully at the top. 6. Repeat for reps, ensuring proper depth and form to target the quads effectively.'),
            (4, 4, 'Plank', 'Core stability hold', '1. Start in a forearm plank position: Place your forearms on the floor with elbows directly under your shoulders, palms flat or fists clenched. 2. Extend your legs behind you, balancing on your toes, with your body forming a straight line from head to heels. 3. Engage your core, glutes, and quads to prevent your hips from sagging or piking up. 4. Hold this position while breathing steadily, avoiding holding your breath. 5. Aim to maintain the hold for a set duration (e.g., 20-60 seconds), focusing on tension throughout your core. 6. To end, gently lower your knees to the floor; repeat for multiple sets as needed.'),
            (5, 1, 'Decline Bench Press', 'Compound exercise targeting lower chest', '1. Set the bench to a decline angle of about 15-30 degrees. Lie down with your feet secured under the pads. 2. Grip the barbell with hands slightly wider than shoulder-width, palms facing forward. 3. Unrack the bar and hold it above your lower chest with arms extended. 4. Inhale and slowly lower the bar to your lower chest, keeping elbows at about 45 degrees from your body. 5. Pause briefly, then exhale and press the bar back up to the starting position, fully extending your arms without locking elbows. 6. Repeat for desired reps, maintaining control throughout.'),
            (6, 1, 'Cable Crossovers', 'Isolation exercise for chest definition', '1. Stand in the center of a cable machine with pulleys set high. Attach handles to each pulley and select appropriate weight. 2. Grab the handles with palms facing down and step forward slightly, creating tension in the cables. 3. With a slight bend in your elbows, bring your hands together in front of your chest in a sweeping arc motion, squeezing your chest at the peak. 4. Pause for a second, then slowly return to the starting position, feeling the stretch in your chest. 5. Keep your core engaged and avoid using momentum; focus on controlled movement. 6. Repeat for reps, alternating the leading hand if desired.'),
            (7, 1, 'Chest Dips', 'Bodyweight compound for chest and triceps', '1. Grip the parallel bars with hands shoulder-width apart, palms facing inward. 2. Jump up or step onto the bars, straightening your arms to lift your body, keeping elbows slightly bent. 3. Lean forward slightly to emphasize the chest, cross your ankles for stability. 4. Inhale and lower your body by bending elbows until your chest feels a stretch, aiming for elbows at 90 degrees. 5. Exhale and push through your palms to lift back to the starting position, squeezing your chest. 6. Avoid shrugging shoulders; add weight via a belt if advanced. Repeat controlled reps.'),
            (8, 1, 'Push-Ups', 'Bodyweight exercise for overall chest strength', '1. Start in a plank position with hands placed slightly wider than shoulder-width on the floor, body in a straight line from head to heels. 2. Engage your core and glutes to maintain alignment. 3. Inhale and bend your elbows to lower your chest toward the floor, keeping elbows at 45 degrees from your body. 4. Go down until your chest nearly touches the ground. 5. Exhale and push through your palms to return to the starting position, fully extending arms without locking elbows. 6. Modify on knees if needed; repeat for reps, focusing on form over speed.'),
            (9, 1, 'Dumbbell Bench Press', 'Free-weight compound for balanced chest development', '1. Lie flat on a bench with feet planted on the floor for stability. Hold a dumbbell in each hand above your chest, palms facing forward. 2. Position the dumbbells at shoulder level with elbows bent at 90 degrees. 3. Inhale and slowly lower the dumbbells to the sides of your chest, keeping wrists straight. 4. Pause briefly, then exhale and press the dumbbells upward, bringing them together at the top without clanging. 5. Squeeze your chest at the peak contraction. 6. Repeat for reps, ensuring symmetrical movement to avoid imbalances.'),
            (10, 2, 'EZ-Bar Curls', 'Barbell variation for biceps peak', '1. Stand with feet shoulder-width apart, holding an EZ-bar with an underhand grip, hands at the angled sections. 2. Keep elbows close to your torso and shoulders relaxed. 3. Exhale and curl the bar upward toward your chest by contracting your biceps, keeping upper arms stationary. 4. Squeeze at the top for a second. 5. Inhale and slowly lower the bar back to the starting position, fully extending arms without locking elbows. 6. Avoid swinging; use controlled tempo and repeat for reps.'),
            (11, 2, 'Hammer Curls', 'Neutral-grip curl for brachialis and forearms', '1. Stand with feet shoulder-width, holding a dumbbell in each hand with palms facing inward (neutral grip). 2. Keep elbows tucked at your sides. 3. Exhale and curl the dumbbells upward simultaneously, maintaining the neutral grip throughout. 4. Bring them to shoulder height, squeezing your biceps and forearms. 5. Inhale and lower slowly to the start, resisting the weight. 6. Alternate arms if preferred; repeat for balanced reps per side.'),
            (12, 2, 'Concentration Curls', 'Seated isolation for biceps focus', '1. Sit on a bench with legs spread, holding a dumbbell in one hand. Rest your elbow on the inside of your thigh. 2. Let the dumbbell hang down fully extended. 3. Exhale and curl the dumbbell toward your shoulder, focusing solely on the biceps contraction. 4. Squeeze at the top, then inhale and lower slowly, feeling the stretch. 5. Avoid using body momentum; switch arms after completing reps on one side. 6. Repeat for equal sets per arm.'),
            (13, 2, 'Cable Curls', 'Constant tension curl using cables', '1. Attach a straight bar to a low pulley on a cable machine. Stand facing the machine with feet shoulder-width. 2. Grip the bar underhand, elbows at sides. 3. Exhale and curl the bar upward, keeping upper arms fixed and wrists straight. 4. Pause at the top contraction. 5. Inhale and lower controlled, maintaining tension without letting the stack rest. 6. Repeat for reps, adjusting weight for full range of motion.'),
            (14, 2, 'Chin-Ups', 'Bodyweight pull for biceps and back', '1. Grip a pull-up bar with hands shoulder-width, palms facing you (supinated grip). 2. Hang fully extended, engaging your core. 3. Exhale and pull your body upward by bending elbows, leading with your chest toward the bar. 4. Aim to bring your chin above the bar, squeezing biceps. 5. Inhale and lower slowly to the start without swinging. 6. Use assistance bands if needed; repeat for reps.'),
            (15, 3, 'Front Squat', 'Barbell squat emphasizing quads', '1. Stand with feet shoulder-width, barbell across your front shoulders, elbows high, hands crossed or gripping. 2. Engage core and keep chest up. 3. Inhale and squat down by bending knees and hips, keeping back straight until thighs are parallel to floor. 4. Drive through heels to stand back up, exhaling at the top. 5. Avoid leaning forward; maintain upright torso. 6. Repeat for reps, using safety bars if in a rack.'),
            (16, 3, 'Leg Press', 'Machine-based quad dominant press', '1. Sit in the leg press machine with feet hip-width on the platform, knees bent at 90 degrees. 2. Grip handles and release safety locks. 3. Exhale and push the platform away by extending knees, keeping feet flat. 4. Go until legs are almost straight without locking knees. 5. Inhale and lower the platform controlled until knees are at 90 degrees. 6. Repeat, focusing on quad engagement over momentum.'),
            (17, 3, 'Bulgarian Split Squat', 'Single-leg exercise for quad balance', '1. Stand lunge-distance from a bench, place one foot behind on the bench. Hold dumbbells if added weight. 2. Lower by bending front knee until thigh is parallel to floor, back knee toward ground. 3. Keep front knee over toe, torso upright. 4. Exhale and push through front heel to stand. 5. Complete reps on one leg before switching. 6. Focus on stability; use bodyweight for beginners.'),
            (18, 3, 'Step-Ups', 'Functional quad builder with elevation', '1. Stand facing a sturdy box or bench at knee height, feet hip-width. 2. Step up with one foot, driving through heel to lift body onto the box. 3. Bring the other foot up to stand fully. 4. Step down controlled with the leading foot first. 5. Alternate legs or complete reps per side. 6. Add dumbbells for intensity; keep core tight throughout.'),
            (19, 3, 'Hack Squat', 'Machine squat for quad isolation', '1. Position yourself in the hack squat machine with shoulders under pads, feet on platform shoulder-width. 2. Release safety handles and stand tall. 3. Inhale and squat down by bending knees, keeping back against pad until thighs parallel. 4. Exhale and push through heels to extend legs. 5. Avoid locking knees at top. 6. Repeat controlled, adjusting foot placement for emphasis.'),
            (20, 4, 'Bicycle Crunches', 'Dynamic ab exercise for obliques', '1. Lie on your back with hands behind head, knees bent, feet off floor. 2. Lift shoulders off mat, engaging core. 3. Bring right elbow to left knee while extending right leg straight. 4. Twist and switch sides in a pedaling motion. 5. Exhale on each twist, focusing on oblique contraction. 6. Repeat for reps, keeping movements slow and controlled.'),
            (21, 4, 'Hanging Leg Raises', 'Hanging core exercise for lower abs', '1. Hang from a pull-up bar with hands shoulder-width, body straight. 2. Engage core to avoid swinging. 3. Exhale and lift knees toward chest, tilting pelvis slightly. 4. Pause at top, then inhale and lower legs controlled. 5. For advanced, keep legs straight. 6. Repeat, focusing on abs over hip flexors.'),
            (22, 4, 'Russian Twists', 'Rotational exercise for obliques', '1. Sit on floor with knees bent, feet flat, leaning back slightly. Hold a weight or clasp hands. 2. Lift feet off ground for challenge. 3. Twist torso to one side, bringing hands/weight beside hip. 4. Return to center and twist to other side. 5. Exhale on each twist, keeping core tight. 6. Repeat for reps, maintaining balance.'),
            (23, 4, 'Mountain Climbers', 'Cardio-core plank variation', '1. Start in high plank position, hands under shoulders, body straight. 2. Drive one knee toward chest quickly, then switch legs in a running motion. 3. Keep hips level and core engaged. 4. Alternate at a brisk pace, exhaling with each drive. 5. Avoid bouncing; focus on form. 6. Continue for time or reps.'),
            (24, 4, 'Woodchoppers', 'Functional rotational core move', '1. Stand with feet wide, holding a cable or medicine ball at one side near shoulder. 2. Squat slightly and rotate torso to pull across body diagonally downward. 3. Pivot on back foot, engaging obliques. 4. Return controlled to start. 5. Complete reps on one side before switching. 6. Exhale on the chop, keeping motion fluid.')
            """.trimIndent())

        db.execSQL("""
            INSERT INTO WORKOUT (workout_id, user_id, workout_name, description)
            VALUES
            (1, 1, 'Morning Workout', 'A workout to start the day'),
            (2, 2, 'Evening Workout', 'A workout to end the day')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO WORKOUT_EXERCISE (workout_exercise_id, workout_id, exercise_id)
            VALUES
            (1, 1, 1),
            (2, 1, 2),
            (3, 2, 3),
            (4, 2, 4)
        """.trimIndent())

        db.execSQL("""
            INSERT INTO WORKOUT_SESSIONS (session_id, workout_id, workout_date, start_time, end_time, notes)
            VALUES
            (1, 1, '2025-09-20', '08:00:00', '09:00:00', 'Good energy, strong performance'),
            (2, 2, '2025-09-21', '18:30:00', '19:15:00', 'Felt tired, shortened workout')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO SETS (set_id, session_id, exercise_id, set_number, weight_used, REPS)
            VALUES
            (1, 1, 1, 1, 60, 12),
            (2, 1, 1, 2, 65, 12),
            (3, 1, 2, 1, 12, 12),
            (4, 2, 3, 1, 80, 12),
            (5, 2, 4, 1, 0, 12)
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database schema upgrades here
        db.execSQL("DROP TABLE IF EXISTS USERS")
        db.execSQL("DROP TABLE IF EXISTS MUSCLE_GROUPS")
        db.execSQL("DROP TABLE IF EXISTS EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS WORKOUT")
        db.execSQL("DROP TABLE IF EXISTS WORKOUT_EXERCISE")
        db.execSQL("DROP TABLE IF EXISTS WORKOUT_SESSIONS")
        db.execSQL("DROP TABLE IF EXISTS SETS")
        onCreate(db)
    }

    fun addUser(username: String, email: String, password_hash: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("email", email)
        contentValues.put("password_hash", password_hash)
        return db.insert("USERS", null, contentValues)
    }

    fun checkUser(email: String, password_hash: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM USERS WHERE email = ? AND password_hash = ?", arrayOf(email, password_hash))
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
        }
        cursor.close()
        return userId
    }

    fun isUserRegistered(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM USERS WHERE email = ?", arrayOf(email))
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    fun getAllExercises(): List<com.example.gymworkout.data.model.Exercise> {
        val exercises = mutableListOf<com.example.gymworkout.data.model.Exercise>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
            SELECT e.exercise_id, e.exercise_name, mg.name as muscle_group
            FROM EXERCISES e
            INNER JOIN MUSCLE_GROUPS mg ON e.muscle_group_id = mg.muscle_group_id
        """, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("exercise_name"))
                val muscleGroup = cursor.getString(cursor.getColumnIndexOrThrow("muscle_group"))
                exercises.add(com.example.gymworkout.data.model.Exercise(id, name, muscleGroup))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return exercises
    }

    fun getExerciseInstructions(exerciseName: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT instructions FROM EXERCISES WHERE exercise_name = ?", arrayOf(exerciseName))
        var instructions: String? = null
        if (cursor.moveToFirst()) {
            instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
        }
        cursor.close()
        return instructions
    }

    fun addExercise(muscleGroupId: Int, name: String, description: String, instructions: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("muscle_group_id", muscleGroupId)
        contentValues.put("exercise_name", name)
        contentValues.put("description", description)
        contentValues.put("instructions", instructions)
        return db.insert("EXERCISES", null, contentValues)
    }

    fun addWorkout(userId: Int, workoutName: String, description: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("user_id", userId)
        contentValues.put("workout_name", workoutName)
        contentValues.put("description", description)
        return db.insert("WORKOUT", null, contentValues)
    }

    fun addExerciseToWorkout(workoutId: Int, exerciseId: Int): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("workout_id", workoutId)
        contentValues.put("exercise_id", exerciseId)
        return db.insert("WORKOUT_EXERCISE", null, contentValues)
    }

    fun getExercisesForWorkout(workoutId: Int): List<com.example.gymworkout.data.model.Exercise> {
        val exercises = mutableListOf<com.example.gymworkout.data.model.Exercise>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
            SELECT e.exercise_id, e.exercise_name, mg.name as muscle_group, we.workout_id
            FROM EXERCISES e
            INNER JOIN MUSCLE_GROUPS mg ON e.muscle_group_id = mg.muscle_group_id
            INNER JOIN WORKOUT_EXERCISE we ON e.exercise_id = we.exercise_id
            WHERE we.workout_id = ?
        """, arrayOf(workoutId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("exercise_name"))
                val muscleGroup = cursor.getString(cursor.getColumnIndexOrThrow("muscle_group"))
                val wId = cursor.getInt(cursor.getColumnIndexOrThrow("workout_id"))
                val exercise = com.example.gymworkout.data.model.Exercise(id, name, muscleGroup)
                exercise.workoutId = wId
                exercises.add(exercise)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return exercises
    }

    fun getOrCreateWorkoutSession(workoutId: Int): Int {
        val db = this.writableDatabase
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.format(java.util.Date())
        val cursor = db.rawQuery("SELECT session_id FROM WORKOUT_SESSIONS WHERE workout_id = ? AND workout_date = ?", arrayOf(workoutId.toString(), date))
        if (cursor.moveToFirst()) {
            val sessionId = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
            cursor.close()
            return sessionId
        } else {
            val contentValues = ContentValues()
            contentValues.put("workout_id", workoutId)
            contentValues.put("workout_date", date)
            return db.insert("WORKOUT_SESSIONS", null, contentValues).toInt()
        }
    }

    fun addSet(workoutId: Int, exerciseId: Int, sets: Int, reps: Int, weight: Float) {
        val sessionId = getOrCreateWorkoutSession(workoutId)
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("session_id", sessionId)
        contentValues.put("exercise_id", exerciseId)
        contentValues.put("set_number", sets)
        contentValues.put("reps", reps)
        contentValues.put("weight_used", weight)
        db.insert("SETS", null, contentValues)
    }

    fun getSetsForExercise(sessionId: Int, exerciseId: Int): List<com.example.gymworkout.data.model.Set> {
        val sets = mutableListOf<com.example.gymworkout.data.model.Set>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SETS WHERE session_id = ? AND exercise_id = ?", arrayOf(sessionId.toString(), exerciseId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("set_id"))
                val sId = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
                val eId = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"))
                val setNumber = cursor.getInt(cursor.getColumnIndexOrThrow("set_number"))
                val reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"))
                val weightUsed = cursor.getFloat(cursor.getColumnIndexOrThrow("weight_used"))
                sets.add(com.example.gymworkout.data.model.Set(id, sId, eId, setNumber, weightUsed, reps))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sets
    }

    fun getAllWorkoutSessions(): List<com.example.gymworkout.data.model.WorkoutSession> {
        val sessions = mutableListOf<com.example.gymworkout.data.model.WorkoutSession>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
            SELECT ws.session_id, ws.workout_id, w.workout_name, ws.workout_date, ws.start_time, ws.end_time, ws.notes
            FROM WORKOUT_SESSIONS ws
            INNER JOIN WORKOUT w ON ws.workout_id = w.workout_id
            ORDER BY ws.workout_date DESC, ws.start_time DESC
        """, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
                val workoutId = cursor.getInt(cursor.getColumnIndexOrThrow("workout_id"))
                val workoutName = cursor.getString(cursor.getColumnIndexOrThrow("workout_name"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("workout_date"))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"))
                val notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
                sessions.add(com.example.gymworkout.data.model.WorkoutSession(id, workoutId, workoutName, date, startTime, endTime, notes))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sessions
    }

    fun getExercisesForSession(sessionId: Int): List<com.example.gymworkout.data.model.Exercise> {
        val exercises = mutableListOf<com.example.gymworkout.data.model.Exercise>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
            SELECT DISTINCT e.exercise_id, e.exercise_name, mg.name as muscle_group
            FROM EXERCISES e
            INNER JOIN MUSCLE_GROUPS mg ON e.muscle_group_id = mg.muscle_group_id
            INNER JOIN SETS s ON e.exercise_id = s.exercise_id
            WHERE s.session_id = ?
            ORDER BY e.exercise_name ASC
        """, arrayOf(sessionId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("exercise_name"))
                val muscleGroup = cursor.getString(cursor.getColumnIndexOrThrow("muscle_group"))
                exercises.add(com.example.gymworkout.data.model.Exercise(id, name, muscleGroup))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return exercises
    }

    fun getSetsForExerciseInSession(sessionId: Int, exerciseId: Int): List<com.example.gymworkout.data.model.Set> {
        val sets = mutableListOf<com.example.gymworkout.data.model.Set>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SETS WHERE session_id = ? AND exercise_id = ? ORDER BY set_number ASC", arrayOf(sessionId.toString(), exerciseId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("set_id"))
                val sId = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
                val eId = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"))
                val setNumber = cursor.getInt(cursor.getColumnIndexOrThrow("set_number"))
                val reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"))
                val weightUsed = cursor.getFloat(cursor.getColumnIndexOrThrow("weight_used"))
                sets.add(com.example.gymworkout.data.model.Set(id, sId, eId, setNumber, weightUsed, reps))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sets
    }
}