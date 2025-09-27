package com.example.gymworkout.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "gymworkout.db"
        private const val DATABASE_VERSION = 1
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
            CREATE TABLE WORKOUT_SESSIONS (
                session_id INTEGER PRIMARY KEY,
                user_id INTEGER NOT NULL,
                workout_date DATE NOT NULL,
                start_time TIME,
                end_time TIME,
                notes TEXT,
                FOREIGN KEY (user_id) REFERENCES USERS(user_id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE SETS (
                set_id INTEGER PRIMARY KEY,
                session_id INTEGER NOT NULL,
                exercise_id INTEGER NOT NULL,
                set_number INTEGER NOT NULL,
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
            (1, 1, 'Bench Press', 'Compound chest exercise', 'Lie on bench, lower bar to chest, push back up'),
            (2, 2, 'Bicep Curl', 'Isolation for biceps', 'Hold dumbbells, curl up, lower slowly'),
            (3, 3, 'Squat', 'Compound lower body exercise', 'Stand with barbell, squat down, push back up'),
            (4, 4, 'Plank', 'Core stability hold', 'Hold body straight supported by forearms and toes')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO WORKOUT_SESSIONS (session_id, user_id, workout_date, start_time, end_time, notes)
            VALUES
            (1, 1, '2025-09-20', '08:00:00', '09:00:00', 'Good energy, strong performance'),
            (2, 2, '2025-09-21', '18:30:00', '19:15:00', 'Felt tired, shortened workout')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO SETS (set_id, session_id, exercise_id, set_number, weight_used)
            VALUES
            (1, 1, 1, 1, 60),
            (2, 1, 1, 2, 65),
            (3, 1, 2, 1, 12),
            (4, 2, 3, 1, 80),
            (5, 2, 4, 1, 0)
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database schema upgrades here
        db.execSQL("DROP TABLE IF EXISTS USERS")
        db.execSQL("DROP TABLE IF EXISTS MUSCLE_GROUPS")
        db.execSQL("DROP TABLE IF EXISTS EXERCISES")
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

    fun checkUser(email: String, password_hash: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM USERS WHERE email = ? AND password_hash = ?", arrayOf(email, password_hash))
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    fun isUserRegistered(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM USERS WHERE email = ?", arrayOf(email))
        val count = cursor.count
        cursor.close()
        return count > 0
    }
}