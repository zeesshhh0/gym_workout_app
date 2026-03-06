package com.example.gymworkout.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "gymworkout1.db"
        private const val DATABASE_VERSION = 9
    }


    override fun onCreate(db: SQLiteDatabase) {
        // Create tables here
        db.execSQL("""
            CREATE TABLE USERS (
                user_id TEXT PRIMARY KEY,
                username TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password_hash TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE MUSCLE_GROUPS (
                muscle_group_id INTEGER PRIMARY KEY,
                name TEXT NOT NULL UNIQUE
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE EXERCISES (
                exercise_id INTEGER PRIMARY KEY,
                muscle_group_id INTEGER NOT NULL,
                exercise_name TEXT NOT NULL,
                instructions TEXT,
                FOREIGN KEY (muscle_group_id) REFERENCES MUSCLE_GROUPS(muscle_group_id)
            )
        """.trimIndent())


        db.execSQL("""
            CREATE TABLE WORKOUT (
                workout_id INTEGER PRIMARY KEY,
                user_id TEXT NOT NULL,
                workout_name TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES USERS(user_id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE WORKOUT_SESSIONS (
                session_id INTEGER PRIMARY KEY,
                workout_id INTEGER NOT NULL,
                workout_date DATE NOT NULL,
                start_time TIME,
                end_time TIME,
                user_id TEXT,
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
                is_completed INTEGER DEFAULT 0,
                user_id TEXT,
                FOREIGN KEY (session_id) REFERENCES WORKOUT_SESSIONS(session_id),
                FOREIGN KEY (exercise_id) REFERENCES EXERCISES(exercise_id)
            )
        """.trimIndent())

        // Insert sample data
        db.execSQL("""
            INSERT INTO USERS (user_id, username, email, password_hash, created_at)
            VALUES
            ('uid_john', 'FitJohn2024', 'john@example.com', 'hash_john_123', '2025-09-01 10:00:00'),
            ('uid_sara', 'StrongSara', 'sara@example.com', 'hash_sara_123', '2025-09-05 14:30:00'),
            ('local_user', 'Local Account', 'local', '', '2025-01-01 00:00:00')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO MUSCLE_GROUPS (muscle_group_id, name)
            VALUES
            (1, 'Chest'),
            (2, 'Biceps'),
            (3, 'Legs'),
            (4, 'Abs'),
            (5, 'Back'),
            (6, 'Shoulders'),
            (7, 'Triceps')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO EXERCISES (exercise_id, muscle_group_id, exercise_name, instructions)
            VALUES
            (1, 5, 'Lat Pulldown (Cable)', '1. Sit at a lat pulldown station and adjust the thigh pad to fit snugly. 2. Grasp the wide bar with an overhand grip, slightly wider than shoulder-width. 3. Keep your chest up, lean slightly back, and exhale as you pull the bar down to your upper chest. 4. Squeeze your shoulder blades together at the bottom of the movement. 5. Inhale and slowly return the bar to the starting position until your arms are fully extended. 6. Repeat for the desired number of reps, avoiding using momentum.'),
            (2, 5, 'Bent Over One Arm Row (Dumbbell)', '1. Place your left knee and left hand on a flat bench, keeping your back parallel to the floor. 2. Hold a dumbbell in your right hand with a neutral grip, arm fully extended toward the floor. 3. Exhale and pull the dumbbell straight up to the side of your torso, keeping your elbow close to your body. 4. Squeeze your back muscles at the top for a brief pause. 5. Inhale and slowly lower the dumbbell back to the starting position. 6. Complete the desired reps and switch sides.'),
            (3, 5, 'T Bar Row', '1. Stand over the T-bar machine with feet shoulder-width apart and knees slightly bent. 2. Hinge at the hips so your torso is almost parallel to the floor, keeping your back straight. 3. Grip the handles firmly with both hands. 4. Exhale and pull the weight toward your upper abdomen, retracting your shoulder blades. 5. Squeeze your back at the top, then inhale as you slowly lower the weight until your arms are fully extended. 6. Repeat for reps without jerking your torso.'),
            (4, 5, 'Seated Row (Cable)', '1. Sit at the low pulley cable station with your feet resting on the platform, knees slightly bent. 2. Grab the V-bar attachment with both hands, sitting tall with a straight back. 3. Exhale and pull the handles straight back toward your abdomen, keeping your elbows tucked close to your sides. 4. Squeeze your shoulder blades together firmly at the peak contraction. 5. Inhale and slowly extend your arms back to the starting position without leaning forward excessively. 6. Repeat for the desired reps.'),
            (5, 5, 'Iso-Lateral Row (Machine)', '1. Adjust the seat height so the chest pad is centered on your torso. 2. Sit facing the pad, place your feet flat on the floor, and grasp the handles. 3. Exhale and pull the handles back, driving your elbows behind you. 4. Focus on contracting your lat muscles at the top of the movement. 5. Inhale and slowly return the handles forward until your arms are fully extended. 6. Repeat for reps, keeping your chest pressed against the pad for stability.'),
            (6, 5, 'Bent Over Cable (Upper Se Chain)', '1. Attach a straight bar or handles to a low cable pulley. 2. Stand facing the machine, grasp the attachment, and hinge forward at the hips, keeping your back straight. 3. Let your arms fully extend toward the pulley. 4. Exhale and pull the attachment toward your midsection, squeezing your shoulder blades. 5. Pause briefly at the top of the contraction. 6. Inhale and slowly lower the weight back to the starting position; repeat for reps.'),
            (7, 5, 'Lat Pulldown - Underhand (Cable)', '1. Sit at a lat pulldown machine and adjust the thigh pad. 2. Grasp the bar with an underhand (supinated) grip, hands about shoulder-width apart. 3. Keep your chest elevated and lean back slightly. 4. Exhale and pull the bar down to your upper chest, driving your elbows down and back. 5. Squeeze your lats and biceps, then inhale and slowly return to the top position. 6. Repeat for reps, maintaining a controlled tempo.'),
            (8, 5, 'Lat Pulldown (Machine)', '1. Adjust the seat and thigh pads of the seated lat pulldown machine. 2. Grip the handles firmly overhead. 3. Keep your torso upright and core engaged. 4. Exhale and pull the handles down toward your shoulders, squeezing your back muscles. 5. Pause for a second at the bottom of the movement. 6. Inhale and control the handles back up to the starting position.'),
            (9, 2, 'Bicep Curl (Barbell)', '1. Stand tall holding a barbell with a shoulder-width, underhand grip. 2. Let the bar hang in front of your thighs, elbows close to your torso. 3. Exhale and curl the bar upward toward your shoulders, keeping your upper arms strictly stationary. 4. Squeeze your biceps forcefully at the top. 5. Inhale and slowly lower the bar back to the starting position. 6. Repeat for reps, avoiding using momentum from your hips.'),
            (10, 2, 'Bicep Curl (Cable)', '1. Attach a straight or EZ-bar to a low cable pulley. 2. Stand facing the machine, grabbing the bar with an underhand grip, feet shoulder-width apart. 3. Keep your elbows pinned to your sides. 4. Exhale and curl the bar up toward your chest. 5. Hold the contraction at the top for a moment. 6. Inhale and slowly lower the bar back to full extension, then repeat.'),
            (11, 2, 'Preacher Curl (Machine)', '1. Adjust the seat of the preacher curl machine so your upper arms rest flat on the pad, armpits snug over the top. 2. Grasp the handles with an underhand grip. 3. Exhale and curl the handles upward, focusing purely on the bicep contraction. 4. Squeeze hard at the top of the movement. 5. Inhale and slowly lower the weight until your arms are nearly fully extended (do not hyperextend). 6. Repeat for desired reps.'),
            (12, 2, 'Hammer Curl (Dumbbell)', '1. Stand holding a dumbbell in each hand with a neutral grip (palms facing each other) at your sides. 2. Keep your back straight and elbows locked at your waist. 3. Exhale and curl the weights up toward your shoulders, maintaining the neutral grip. 4. Squeeze the biceps and forearms at the top. 5. Inhale and lower the dumbbells slowly back to the start. 6. Repeat for reps without rocking your body.'),
            (13, 2, 'Bicep Curl (Dumbbell)', '1. Stand holding a dumbbell in each hand by your sides, palms facing forward. 2. Keep your chest up and elbows close to your body. 3. Exhale and curl both dumbbells toward your shoulders simultaneously. 4. Squeeze your biceps tight at the peak of the curl. 5. Inhale and slowly lower the dumbbells back down to full arm extension. 6. Repeat for reps, controlling the negative portion of the lift.'),
            (14, 2, 'One By One Biceps Curl (Cable)', '1. Stand between two low cable pulleys (or single) using a D-handle. 2. Grasp the handle in one hand with an underhand grip, keeping your elbow at your side. 3. Exhale and curl the handle upward toward your shoulder. 4. Squeeze the bicep at the top. 5. Inhale and lower the weight in a controlled motion. 6. Complete all reps on one arm, then switch to the other side.'),
            (15, 2, 'Concentration Curl (Dumbbell)', '1. Sit on the edge of a flat bench holding a dumbbell in one hand. 2. Rest the back of your working arm''s lower triceps against your inner thigh. 3. Let your arm hang down fully extended. 4. Exhale and curl the dumbbell upward toward your shoulder, isolating the bicep. 5. Pause and squeeze at the top, then inhale and lower slowly. 6. Complete the desired reps, then switch arms.'),
            (16, 2, 'Overhead Biceps Curl', '1. Stand in the middle of a dual cable station with the pulleys set to the highest position. 2. Grab a handle in each hand with an underhand grip, arms extended out to your sides parallel to the floor. 3. Keep your upper arms stationary. 4. Exhale and curl your hands toward your head. 5. Squeeze your biceps at peak contraction. 6. Inhale and return to the starting position; repeat for reps.'),
            (17, 6, 'Front Raise (Cable)', '1. Attach a straight bar or rope to a low pulley cable. 2. Stand facing away from the machine (with the cable running between your legs) or facing it, holding the attachment with an overhand grip. 3. Keep your arms straight with a slight bend in the elbows. 4. Exhale and raise your arms straight up in front of you until they are parallel to the floor. 5. Pause briefly at shoulder height. 6. Inhale and lower the weight slowly back down; repeat for reps.'),
            (18, 6, 'Seated Overhead Press (Dumbbell)', '1. Sit on an upright bench holding a dumbbell in each hand at shoulder height, palms facing forward. 2. Keep your back pressed firmly against the backrest and feet flat on the floor. 3. Exhale and push the dumbbells upward until your arms are fully extended overhead. 4. Pause at the top without clashing the weights together. 5. Inhale and slowly lower the dumbbells back to shoulder level. 6. Repeat for reps, maintaining tight core control.'),
            (19, 6, 'Lateral Raise (Dumbbell)', '1. Stand tall with a dumbbell in each hand by your sides, palms facing your body. 2. Maintain a slight bend in your elbows and lean slightly forward. 3. Exhale and raise your arms out to the sides until they are parallel to the floor. 4. Pause for a second at the top, focusing on the lateral deltoid. 5. Inhale and lower the weights slowly back to the starting position. 6. Repeat for reps, avoiding using momentum.'),
            (20, 6, 'Front Raise (Dumbbell)', '1. Stand holding a dumbbell in each hand in front of your thighs, palms facing your legs. 2. Keep your core tight and arms straight with a slight bend in the elbow. 3. Exhale and raise the dumbbells straight out in front of you until they reach shoulder height. 4. Hold the top position for a brief moment. 5. Inhale and slowly lower the dumbbells back to your thighs. 6. Repeat for reps.'),
            (21, 6, 'Face Pull (Cable)', '1. Attach a rope to a cable pulley set at upper-chest or face height. 2. Grasp the rope ends with a neutral or overhand grip and step back to create tension. 3. Exhale and pull the rope toward your face, letting your elbows flare outward and backward. 4. Squeeze your rear deltoids and upper back at peak contraction. 5. Inhale and slowly return your arms to full extension. 6. Repeat for reps.'),
            (22, 6, 'Upright Row (Dumbbell)', '1. Stand holding a pair of dumbbells in front of your thighs with an overhand grip. 2. Keep your back straight and chest up. 3. Exhale and pull the dumbbells straight up toward your chin, leading with your elbows. 4. Keep the weights close to your body and raise your elbows higher than your wrists. 5. Inhale and lower the dumbbells back down slowly. 6. Repeat for reps.'),
            (23, 6, 'Front Raise (Plate)', '1. Stand tall holding a weight plate with both hands at the 3 o''clock and 9 o''clock positions. 2. Let the plate rest in front of your thighs. 3. Exhale and raise the plate straight out in front of you until it is at eye level. 4. Hold the contraction at the top. 5. Inhale and lower the plate back down to the starting position under control. 6. Repeat for desired reps.'),
            (24, 6, 'Shrug (Dumbbell)', '1. Stand tall holding a heavy dumbbell in each hand by your sides, palms facing your body. 2. Keep your arms perfectly straight and your head in a neutral position. 3. Exhale and elevate your shoulders as high as possible toward your ears. 4. Hold the peak contraction at the top for a second. 5. Inhale and lower your shoulders back to the resting position. 6. Repeat for reps without rolling your shoulders.'),
            (25, 6, 'Seated Overhead Press (Barbell)', '1. Sit on an upright bench with a barbell racked at shoulder height, or carefully hoist it to your collarbone. 2. Grip the bar slightly wider than shoulder-width. 3. Exhale and press the bar overhead until your arms are fully extended. 4. Bring your head slightly forward as the bar clears your face. 5. Inhale and slowly lower the bar back to your upper chest. 6. Repeat for reps.'),
            (26, 6, 'Cross Cable Shoulders', '1. Stand in the center of a dual-cable setup with the pulleys at a low or high setting (depending on the target). 2. Grab the left cable with your right hand, and the right cable with your left hand. 3. Exhale and pull the cables across your body in a reverse fly motion. 4. Squeeze your rear delts and upper back at full extension. 5. Inhale and slowly let your arms return to the crossed starting position. 6. Repeat for reps.'),
            (27, 7, 'Triceps Extension (Barbell)', '1. Lie on a flat bench holding an EZ-bar or straight bar overhead with a close overhand grip. 2. Keep your upper arms stationary and perpendicular to the floor. 3. Inhale and lower the bar by bending your elbows until the bar is just above your forehead. 4. Exhale and use your triceps to extend your arms back to the starting position. 5. Squeeze the triceps at the top. 6. Repeat for reps, ensuring your elbows do not flare out.'),
            (28, 7, 'One Hand Tricep Overhead Curl', '1. Sit or stand holding a single dumbbell in one hand. 2. Extend your arm straight overhead. 3. Inhale and lower the dumbbell behind your head, bending only at the elbow. 4. Keep your upper arm still and close to your head. 5. Exhale and press the dumbbell back up to the starting position, squeezing the tricep. 6. Complete the reps and switch arms.'),
            (29, 7, 'Triceps Extension (Cable)', '1. Attach a rope or V-bar to an upper cable pulley. 2. Stand facing the machine and grab the attachment. 3. Tuck your elbows into your sides and lean slightly forward. 4. Exhale and push the attachment down until your arms are fully extended toward the floor. 5. Squeeze your triceps forcefully at the bottom. 6. Inhale and slowly return to the starting position; repeat for reps.'),
            (30, 7, 'Triceps Extension (Dumbbell)', '1. Sit on a bench with back support holding a dumbbell with both hands. 2. Press it overhead until your arms are fully extended, grasping the inner weight plate. 3. Inhale and lower the weight behind your head by bending your elbows. 4. Keep your elbows tucked in as much as possible. 5. Exhale and push the dumbbell back up to the top. 6. Repeat for the desired reps.'),
            (31, 7, 'Cable Kickback', '1. Set a cable pulley to a low setting and attach a single D-handle (or use no attachment). 2. Hinge forward at the hips, keeping your back straight, and grab the cable with one hand. 3. Tuck your working elbow up against your side, upper arm parallel to the floor. 4. Exhale and extend your arm backward until it is straight. 5. Squeeze the triceps, then inhale and return the forearm to a 90-degree angle. 6. Repeat and switch sides.'),
            (32, 7, 'One Hand Tricep Curl (Cable)', '1. Stand sideways to an upper cable pulley with a D-handle. 2. Grab the handle with a supinated (underhand) or pronated grip with your working arm. 3. Keep your elbow tucked securely at your side. 4. Exhale and pull the handle down until your arm is fully extended. 5. Hold the contraction for a moment. 6. Inhale and slowly return to the start position; repeat for reps and switch sides.'),
            (33, 7, 'Bench Dip', '1. Place two flat benches parallel to each other. 2. Sit on one bench, place your hands gripping the edge, and rest your heels on the other bench. 3. Slide your glutes off the edge so your arms support your weight. 4. Inhale and lower your body by bending your elbows until they reach a 90-degree angle. 5. Exhale and push through your palms to return to the starting position. 6. Repeat for reps.'),
            (34, 7, 'Triceps Pushdown (Cable - Straight Bar)', '1. Attach a straight bar to an upper cable pulley. 2. Grab the bar with an overhand grip, hands shoulder-width apart. 3. Stand upright with your elbows pinned to your sides. 4. Exhale and push the bar down until your arms are fully extended. 5. Squeeze the triceps at the bottom, then inhale as you slowly bring the bar back up to chest height. 6. Repeat for reps without letting your elbows move forward.'),
            (35, 3, 'Squat (Bodyweight)', '1. Stand with your feet shoulder-width apart, toes pointed slightly outward. 2. Engage your core, keep your chest up, and extend your arms straight in front of you. 3. Inhale and push your hips back, bending your knees to lower your body into a squat. 4. Descend until your thighs are parallel to the floor or lower. 5. Exhale and drive through your heels to return to the standing position. 6. Repeat for the desired number of reps.'),
            (36, 3, 'Squat (Smith Machine)', '1. Set the barbell on the Smith machine to shoulder height and step under it, resting it across your traps. 2. Place your feet slightly forward, shoulder-width apart. 3. Unrack the bar and inhale as you lower yourself by bending your knees and hips. 4. Squat down until your thighs are at least parallel to the floor. 5. Exhale and push forcefully through your heels back to a standing position. 6. Repeat for reps.'),
            (37, 3, 'Leg Press', '1. Sit in the leg press machine and place your feet on the sled at shoulder-width apart. 2. Release the safety handles and fully extend your legs without locking your knees. 3. Inhale and slowly lower the platform until your knees are at a 90-degree angle. 4. Ensure your knees track in line with your feet. 5. Exhale and push the sled back up using your quads and glutes. 6. Repeat for reps.'),
            (38, 3, 'Leg Extension (Machine)', '1. Sit on the leg extension machine, aligning your knees with the machine''s pivot point. 2. Tuck your ankles under the padded lever and grip the handles by the seat. 3. Exhale and extend your legs fully, lifting the weight upwards. 4. Squeeze your quadriceps hard at the top of the movement. 5. Inhale and slowly lower your legs back to the starting position. 6. Repeat for desired reps.'),
            (39, 3, 'Lying Leg Curl (Machine)', '1. Lie face down on the leg curl machine with your ankles secured under the padded lever. 2. Keep your torso flat on the bench and hold the handles lightly. 3. Exhale and curl your legs upward as far as possible, contracting your hamstrings. 4. Pause briefly at the top of the movement. 5. Inhale and lower your legs back to a straight position in a controlled manner. 6. Repeat for reps.'),
            (40, 3, 'Sumo Squat', '1. Stand with your feet significantly wider than shoulder-width, toes pointing outwards at a 45-degree angle. 2. Hold a dumbbell or kettlebell vertically between your legs with both hands. 3. Keep your chest up and inhale as you lower your hips into a squat. 4. Drop down until your thighs are parallel to the floor. 5. Exhale and drive through your heels to stand back up, squeezing your glutes. 6. Repeat for reps.'),
            (41, 3, 'Lunge (Dumbbell)', '1. Stand tall holding a dumbbell in each hand by your sides. 2. Take a large step forward with your right leg. 3. Inhale and lower your hips until both knees are bent at a 90-degree angle, keeping the back knee just above the ground. 4. Keep your front knee aligned with your ankle. 5. Exhale and push off your right foot to return to the starting position. 6. Alternate legs and repeat for reps.'),
            (42, 3, 'Seated Calf Raise (Plate Loaded)', '1. Sit on the calf raise machine and place the balls of your feet on the platform with your heels hanging off. 2. Position the padded lever snugly over your lower thighs. 3. Release the safety bar and lower your heels as far as comfortable for a deep stretch. 4. Exhale and raise your heels as high as possible, contracting your calves. 5. Hold for a brief pause at the top. 6. Repeat for reps.'),
            (43, 1, 'Bench Press (Barbell)', '1. Lie flat on a bench, gripping the barbell slightly wider than shoulder-width. 2. Unrack the bar and hold it straight above your chest. 3. Inhale and lower the bar slowly to your mid-chest, keeping your elbows at a 45-degree angle. 4. Pause briefly when the bar touches your chest. 5. Exhale and press the bar explosively upwards, extending your arms fully. 6. Repeat for reps.'),
            (44, 1, 'Incline Bench Press (Barbell)', '1. Set a bench to a 30-45 degree incline and lie back. 2. Grip the barbell slightly wider than shoulder-width. 3. Unrack the bar and hold it directly over your upper chest. 4. Inhale and lower the bar down to your collarbone in a controlled motion. 5. Exhale and press the bar back up to the top without locking out your elbows. 6. Repeat for reps.'),
            (45, 1, 'Chest Fly', '1. Lie on a flat bench holding a dumbbell in each hand directly above your chest, palms facing each other. 2. Keep a slight bend in your elbows. 3. Inhale and slowly lower your arms out to the sides in a wide arc until you feel a stretch in your chest. 4. Exhale and bring the dumbbells back to the starting position, hugging an imaginary barrel. 5. Squeeze your pecs together at the top. 6. Repeat for reps.'),
            (46, 1, 'Chest Press (Machine)', '1. Adjust the seat so the handles align with your mid-chest. 2. Sit down, place your feet flat on the floor, and grasp the handles with an overhand grip. 3. Keep your back pressed against the pad. 4. Exhale and push the handles forward until your arms are fully extended. 5. Squeeze your chest, then inhale and slowly return the handles back to the start. 6. Repeat for reps.'),
            (47, 1, 'Chest Press (Band)', '1. Secure a resistance band behind you at chest height (or loop it around your back). 2. Hold one end in each hand and step forward to create tension. 3. Start with your elbows bent and hands by your chest. 4. Exhale and press the bands straight out in front of you. 5. Squeeze your chest muscles together. 6. Inhale and bring your hands back to your chest; repeat.'),
            (48, 1, 'Incline Bench Press (Dumbbell)', '1. Lie back on an incline bench (30-45 degrees) holding a dumbbell in each hand. 2. Start with the weights at shoulder level, palms facing forward. 3. Exhale and press the dumbbells straight up until your arms are fully extended. 4. Bring the dumbbells close together at the top without clanking them. 5. Inhale and lower them under control back to the starting position. 6. Repeat for reps.'),
            (49, 1, 'Chest Fly (Dumbbell)', '1. Lie flat on a bench with a dumbbell in each hand, pressed up directly over your chest, palms facing inward. 2. Maintain a slight, soft bend in your elbows. 3. Inhale and open your arms wide to your sides until your elbows are parallel to the floor. 4. Feel a stretch in the chest, then exhale and pull the weights back up together. 5. Squeeze at the top. 6. Repeat for the designated reps.'),
            (50, 1, 'Decline Bench Press (Barbell)', '1. Set the bench to a decline angle of 15-30 degrees and secure your feet. 2. Unrack the barbell with a shoulder-width grip over your lower chest. 3. Inhale and lower the bar to your lower chest, keeping elbows tucked slightly. 4. Pause briefly at the bottom. 5. Exhale and press the bar powerfully back to the top position. 6. Repeat for reps.'),
            (51, 1, 'Bench Press - Close Grip (Barbell)', '1. Lie flat on a bench and grip the barbell with hands about shoulder-width apart (or slightly narrower). 2. Unrack the bar and hold it above your chest. 3. Inhale and lower the bar to your mid-chest, keeping your elbows tightly tucked against your sides. 4. Once it touches your chest, exhale and press the bar up using your chest and triceps. 5. Lock out your arms briefly. 6. Repeat for reps.'),
            (52, 1, 'Decline Bench Press (Dumbbell)', '1. Secure your legs in a decline bench and lie back with a dumbbell in each hand. 2. Hold the dumbbells at chest level, palms facing away from you. 3. Exhale and press the weights straight up over your lower chest. 4. Squeeze your pectorals at the peak contraction. 5. Inhale and slowly lower the dumbbells back down until you feel a good stretch. 6. Repeat for reps.'),
            (53, 1, 'Chest Fly (Band)', '1. Anchor a resistance band behind you at chest height, or use dual bands. 2. Stand facing away from the anchor, holding a handle in each hand, arms spread out laterally with slightly bent elbows. 3. Exhale and pull the bands together in front of your chest in a sweeping arc. 4. Squeeze your chest hard at the center. 5. Inhale and slowly return your arms to the starting wide position. 6. Repeat for reps.')
            """.trimIndent())

        db.execSQL("""
            INSERT INTO WORKOUT (workout_id, user_id, workout_name)
            VALUES
            (1, 'uid_john', 'Morning Workout'),
            (2, 'uid_sara', 'Evening Workout')
        """.trimIndent())

        db.execSQL("""
            INSERT INTO WORKOUT_SESSIONS (session_id, workout_id, workout_date, start_time, end_time)
            VALUES
            (1, 1, '2025-09-20', '08:00:00', '09:00:00'),
            (2, 2, '2025-09-21', '18:30:00', '19:15:00')
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
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE WORKOUT_SESSIONS ADD COLUMN user_id TEXT")
            db.execSQL("ALTER TABLE SETS ADD COLUMN user_id TEXT")
        }
        
        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS WORKOUT_EXERCISE")
        }
        
        if (oldVersion < 9) {
            db.execSQL("DROP TABLE IF EXISTS EXERCISES")
            db.execSQL("DROP TABLE IF EXISTS WORKOUT_SESSIONS")
            db.execSQL("DROP TABLE IF EXISTS WORKOUT")
            db.execSQL("DROP TABLE IF EXISTS SETS")
        }

        if (oldVersion >= 4) {
            // Recreate tables completely if not handled above
            db.execSQL("DROP TABLE IF EXISTS USERS")
            db.execSQL("DROP TABLE IF EXISTS MUSCLE_GROUPS")
            db.execSQL("DROP TABLE IF EXISTS EXERCISES")
            db.execSQL("DROP TABLE IF EXISTS WORKOUT")
            db.execSQL("DROP TABLE IF EXISTS WORKOUT_SESSIONS")
            db.execSQL("DROP TABLE IF EXISTS SETS")
            onCreate(db)
        }
    }

    fun addUser(userId: String, username: String, email: String, password_hash: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("user_id", userId)
        contentValues.put("username", username)
        contentValues.put("email", email)
        contentValues.put("password_hash", password_hash)
        return db.insert("USERS", null, contentValues)
    }


    fun getUserName(userId: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT username FROM USERS WHERE user_id = ?", arrayOf(userId))
        var username: String? = null
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
        }
        cursor.close()
        return username
    }

    fun getLatestWorkoutForUser(userId: String): com.example.gymworkout.data.model.Workout? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM WORKOUT WHERE user_id = ? ORDER BY workout_id DESC LIMIT 1", arrayOf(userId))
        var workout: com.example.gymworkout.data.model.Workout? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("workout_id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("workout_name"))
            workout = com.example.gymworkout.data.model.Workout(id, name)
        }
        cursor.close()
        return workout
    }

    fun getLatestWorkoutSession(userId: String): com.example.gymworkout.data.model.WorkoutSession? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
        SELECT ws.*, w.workout_name FROM WORKOUT_SESSIONS ws
        JOIN WORKOUT w ON ws.workout_id = w.workout_id
        WHERE w.user_id = ?
        ORDER BY ws.workout_date DESC, ws.start_time DESC
        LIMIT 1
    """, arrayOf(userId))

        var session: com.example.gymworkout.data.model.WorkoutSession? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
            val workoutId = cursor.getInt(cursor.getColumnIndexOrThrow("workout_id"))
            val workoutName = cursor.getString(cursor.getColumnIndexOrThrow("workout_name"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("workout_date"))
            val startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
            val endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"))
            session = com.example.gymworkout.data.model.WorkoutSession(id, workoutId, workoutName, date, startTime, endTime)
        }
        cursor.close()
        return session
    }

    fun getSessionStats(userId: String, sessionId: Int): com.example.gymworkout.data.model.SessionStats {
        val db = this.readableDatabase

        // Calculate duration
        val sessionCursor = db.rawQuery("SELECT start_time, end_time FROM WORKOUT_SESSIONS WHERE session_id = ? AND user_id = ?", arrayOf(sessionId.toString(), userId))
        var durationMinutes: Long = 0
        if (sessionCursor.moveToFirst()) {
            val startTimeStr = sessionCursor.getString(sessionCursor.getColumnIndexOrThrow("start_time"))
            val endTimeStr = sessionCursor.getString(sessionCursor.getColumnIndexOrThrow("end_time"))
            if (startTimeStr != null && endTimeStr != null) {
                val timeFormat = java.text.SimpleDateFormat("HH:mm:ss")
                val startTime = timeFormat.parse(startTimeStr)
                val endTime = timeFormat.parse(endTimeStr)
                val diff = endTime.time - startTime.time
                durationMinutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(diff)
            }
        }
        sessionCursor.close()

        // Calculate exercise count
        val exerciseCursor = db.rawQuery("SELECT COUNT(DISTINCT exercise_id) as count FROM SETS WHERE session_id = ? AND user_id = ?", arrayOf(sessionId.toString(), userId))
        var exerciseCount = 0
        if (exerciseCursor.moveToFirst()) {
            exerciseCount = exerciseCursor.getInt(exerciseCursor.getColumnIndexOrThrow("count"))
        }
        exerciseCursor.close()

        // Calculate total volume
        val volumeCursor = db.rawQuery("SELECT SUM(reps * weight_used) as volume FROM SETS WHERE session_id = ? AND user_id = ?", arrayOf(sessionId.toString(), userId))
        var totalVolume: Float = 0f
        if (volumeCursor.moveToFirst()) {
            totalVolume = volumeCursor.getFloat(volumeCursor.getColumnIndexOrThrow("volume"))
        }
        volumeCursor.close()

        return com.example.gymworkout.data.model.SessionStats(durationMinutes, exerciseCount, totalVolume)
    }

    fun calculateWorkoutStreak(userId: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
        SELECT DISTINCT workout_date FROM WORKOUT_SESSIONS ws
        JOIN WORKOUT w ON ws.workout_id = w.workout_id
        WHERE w.user_id = ?
        ORDER BY workout_date DESC
    """, arrayOf(userId))

        val dates = mutableListOf<java.util.Date>()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")

        if (cursor.moveToFirst()) {
            do {
                dates.add(dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("workout_date"))))
            } while (cursor.moveToNext())
        }
        cursor.close()

        if (dates.isEmpty()) return 0

        var streak = 0
        val today = java.util.Calendar.getInstance()
        val lastWorkoutDate = java.util.Calendar.getInstance()

        // Check if the most recent workout was today or yesterday
        lastWorkoutDate.time = dates[0]
        if (today.get(java.util.Calendar.YEAR) == lastWorkoutDate.get(java.util.Calendar.YEAR) &&
            today.get(java.util.Calendar.DAY_OF_YEAR) == lastWorkoutDate.get(java.util.Calendar.DAY_OF_YEAR)) {
            streak = 1
        } else {
            today.add(java.util.Calendar.DAY_OF_YEAR, -1)
            if (today.get(java.util.Calendar.YEAR) == lastWorkoutDate.get(java.util.Calendar.YEAR) &&
                today.get(java.util.Calendar.DAY_OF_YEAR) == lastWorkoutDate.get(java.util.Calendar.DAY_OF_YEAR)) {
                streak = 1
            } else {
                return 0 // No workout today or yesterday, so streak is broken
            }
        }

        val previousDate = java.util.Calendar.getInstance()
        previousDate.time = dates[0]

        for (i in 1 until dates.size) {
            val currentDate = java.util.Calendar.getInstance()
            currentDate.time = dates[i]

            previousDate.add(java.util.Calendar.DAY_OF_YEAR, -1)
            if (previousDate.get(java.util.Calendar.YEAR) == currentDate.get(java.util.Calendar.YEAR) &&
                previousDate.get(java.util.Calendar.DAY_OF_YEAR) == currentDate.get(java.util.Calendar.DAY_OF_YEAR)) {
                streak++
                previousDate.time = dates[i] // Continue the chain
            } else {
                break // Streak is broken
            }
        }

        return streak
    }

    fun getUserDetails(userId: String): Pair<String, String>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT username, email FROM USERS WHERE user_id = ?", arrayOf(userId))
        var userDetails: Pair<String, String>? = null
        if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            userDetails = Pair(username, email)
        }
        cursor.close()
        return userDetails
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

    fun getAllMuscleGroups(): List<com.example.gymworkout.data.model.MuscleGroup> {
        val muscleGroups = mutableListOf<com.example.gymworkout.data.model.MuscleGroup>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT muscle_group_id, name FROM MUSCLE_GROUPS", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("muscle_group_id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                muscleGroups.add(com.example.gymworkout.data.model.MuscleGroup(id, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return muscleGroups
    }

    fun addExercise(muscleGroupId: Int, name: String, instructions: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("muscle_group_id", muscleGroupId)
        contentValues.put("exercise_name", name)
        contentValues.put("instructions", instructions)
        return db.insert("EXERCISES", null, contentValues)
    }

    fun addWorkout(userId: String, workoutName: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("user_id", userId)
        contentValues.put("workout_name", workoutName)
        return db.insert("WORKOUT", null, contentValues)
    }

    fun addExerciseToWorkout(userId: String, workoutId: Int, exerciseId: Int): Long {
        val db = this.writableDatabase
        // Find the active session for this workout
        var sessionId = -1
        val sessionCursor = db.rawQuery("SELECT session_id FROM WORKOUT_SESSIONS WHERE workout_id = ? AND user_id = ? ORDER BY session_id DESC LIMIT 1", arrayOf(workoutId.toString(), userId))
        if (sessionCursor.moveToFirst()) {
            sessionId = sessionCursor.getInt(sessionCursor.getColumnIndexOrThrow("session_id"))
        }
        sessionCursor.close()
        
        if (sessionId != -1) {
            val contentValues = ContentValues()
            contentValues.put("session_id", sessionId)
            contentValues.put("exercise_id", exerciseId)
            contentValues.put("set_number", 1)
            contentValues.put("reps", 0)
            contentValues.put("weight_used", 0f)
            contentValues.put("user_id", userId)
            contentValues.put("is_completed", 0)
            return db.insert("SETS", null, contentValues)
        }
        return -1L
    }

    fun getExercisesForWorkout(userId: String, workoutId: Int): List<com.example.gymworkout.data.model.Exercise> {
        val exercises = mutableListOf<com.example.gymworkout.data.model.Exercise>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
            SELECT DISTINCT e.exercise_id, e.exercise_name, mg.name as muscle_group, ws.workout_id
            FROM EXERCISES e
            INNER JOIN MUSCLE_GROUPS mg ON e.muscle_group_id = mg.muscle_group_id
            INNER JOIN SETS s ON e.exercise_id = s.exercise_id
            INNER JOIN WORKOUT_SESSIONS ws ON s.session_id = ws.session_id
            WHERE ws.workout_id = ? AND s.user_id = ?
        """, arrayOf(workoutId.toString(), userId))
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

    fun getOrCreateWorkoutSession(userId: String, workoutId: Int): Int {
        val db = this.writableDatabase
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = java.text.SimpleDateFormat("HH:mm:ss")
        val date = dateFormat.format(java.util.Date())
        val cursor = db.rawQuery("SELECT session_id FROM WORKOUT_SESSIONS WHERE workout_id = ? AND workout_date = ? AND user_id = ?", arrayOf(workoutId.toString(), date, userId))
        if (cursor.moveToFirst()) {
            val sessionId = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
            cursor.close()
            return sessionId
        } else {
            val contentValues = ContentValues()
            contentValues.put("workout_id", workoutId)
            contentValues.put("workout_date", date)
            contentValues.put("user_id", userId)
            contentValues.put("start_time", timeFormat.format(java.util.Date()))
            return db.insert("WORKOUT_SESSIONS", null, contentValues).toInt()
        }
    }

    fun updateWorkoutName(userId: String, workoutId: Long, newName: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("workout_name", newName)
        db.update("WORKOUT", contentValues, "workout_id = ? AND user_id = ?", arrayOf(workoutId.toString(), userId))
    }

    fun getWorkoutName(userId: String, workoutId: Long): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT workout_name FROM WORKOUT WHERE workout_id = ? AND user_id = ?", arrayOf(workoutId.toString(), userId))
        var workoutName = ""
        if (cursor.moveToFirst()) {
            workoutName = cursor.getString(cursor.getColumnIndexOrThrow("workout_name"))
        }
        cursor.close()
        return workoutName
    }

    fun updateWorkoutSessionStartTime(userId: String, sessionId: Int, startTime: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("start_time", startTime)
        db.update("WORKOUT_SESSIONS", contentValues, "session_id = ? AND user_id = ?", arrayOf(sessionId.toString(), userId))
    }

    fun updateWorkoutSessionEndTime(userId: String, sessionId: Int, endTime: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("end_time", endTime)
        db.update("WORKOUT_SESSIONS", contentValues, "session_id = ? AND user_id = ?", arrayOf(sessionId.toString(), userId))
    }

    fun addSet(userId: String, workoutId: Int, exerciseId: Int, sets: Int, reps: Int, weight: Float) {
        val sessionId = getOrCreateWorkoutSession(userId, workoutId)
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("session_id", sessionId)
        contentValues.put("exercise_id", exerciseId)
        contentValues.put("set_number", sets)
        contentValues.put("reps", reps)
        contentValues.put("weight_used", weight)
        contentValues.put("user_id", userId)
        db.insert("SETS", null, contentValues)
    }

    fun getSetsForExercise(userId: String, sessionId: Int, exerciseId: Int): List<com.example.gymworkout.data.model.Set> {
        val sets = mutableListOf<com.example.gymworkout.data.model.Set>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SETS WHERE session_id = ? AND exercise_id = ? AND user_id = ? ORDER BY set_number ASC", arrayOf(sessionId.toString(), exerciseId.toString(), userId))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("set_id"))
                val sId = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
                val eId = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"))
                val setNumber = cursor.getInt(cursor.getColumnIndexOrThrow("set_number"))
                val reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"))
                val weightUsed = cursor.getFloat(cursor.getColumnIndexOrThrow("weight_used"))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1
                sets.add(com.example.gymworkout.data.model.Set(id, sId, eId, setNumber, weightUsed, reps, isCompleted))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sets
    }

    fun getAllWorkoutSessions(userId: String): List<com.example.gymworkout.data.model.WorkoutSession> {
        val sessions = mutableListOf<com.example.gymworkout.data.model.WorkoutSession>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
            SELECT ws.session_id, ws.workout_id, w.workout_name, ws.workout_date, ws.start_time, ws.end_time
            FROM WORKOUT_SESSIONS ws
            INNER JOIN WORKOUT w ON ws.workout_id = w.workout_id
            WHERE w.user_id = ?
            ORDER BY ws.workout_date DESC, ws.start_time DESC
        """, arrayOf(userId))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
                val workoutId = cursor.getInt(cursor.getColumnIndexOrThrow("workout_id"))
                val workoutName = cursor.getString(cursor.getColumnIndexOrThrow("workout_name"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("workout_date"))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"))
                sessions.add(com.example.gymworkout.data.model.WorkoutSession(id, workoutId, workoutName, date, startTime, endTime))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sessions
    }

    fun getExercisesForSession(userId: String, sessionId: Int): List<com.example.gymworkout.data.model.Exercise> {
        val exercises = mutableListOf<com.example.gymworkout.data.model.Exercise>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
            SELECT DISTINCT e.exercise_id, e.exercise_name, mg.name as muscle_group
            FROM EXERCISES e
            INNER JOIN MUSCLE_GROUPS mg ON e.muscle_group_id = mg.muscle_group_id
            INNER JOIN SETS s ON e.exercise_id = s.exercise_id
            WHERE s.session_id = ? AND s.user_id = ?
            ORDER BY e.exercise_name ASC
        """, arrayOf(sessionId.toString(), userId))

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

    fun getSetsForExerciseInSession(userId: String, sessionId: Int, exerciseId: Int): List<com.example.gymworkout.data.model.Set> {
        val sets = mutableListOf<com.example.gymworkout.data.model.Set>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SETS WHERE session_id = ? AND exercise_id = ? AND user_id = ? ORDER BY set_number ASC", arrayOf(sessionId.toString(), exerciseId.toString(), userId))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("set_id"))
                val sId = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
                val eId = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"))
                val setNumber = cursor.getInt(cursor.getColumnIndexOrThrow("set_number"))
                val reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"))
                val weightUsed = cursor.getFloat(cursor.getColumnIndexOrThrow("weight_used"))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1
                sets.add(com.example.gymworkout.data.model.Set(id, sId, eId, setNumber, weightUsed, reps, isCompleted))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sets
    }

    fun getSetsForExerciseInWorkout(userId: String, workoutId: Int, exerciseId: Int): List<com.example.gymworkout.data.model.Set> {
        val sets = mutableListOf<com.example.gymworkout.data.model.Set>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
            SELECT s.* FROM SETS s
            INNER JOIN WORKOUT_SESSIONS ws ON s.session_id = ws.session_id
            WHERE ws.workout_id = ? AND s.exercise_id = ? AND s.user_id = ?
            ORDER BY ws.session_id ASC, s.set_number ASC
        """, arrayOf(workoutId.toString(), exerciseId.toString(), userId))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("set_id"))
                val sId = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
                val eId = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"))
                val setNumber = cursor.getInt(cursor.getColumnIndexOrThrow("set_number"))
                val reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"))
                val weightUsed = cursor.getFloat(cursor.getColumnIndexOrThrow("weight_used"))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1
                sets.add(com.example.gymworkout.data.model.Set(id, sId, eId, setNumber, weightUsed, reps, isCompleted))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sets
    }

    fun deleteWorkoutAndSession(userId: String, workoutId: Long, sessionId: Int) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            db.delete("WORKOUT_SESSIONS", "session_id = ? AND user_id = ?", arrayOf(sessionId.toString(), userId))
            db.delete("WORKOUT", "workout_id = ? AND user_id = ?", arrayOf(workoutId.toString(), userId))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteWorkoutSession(userId: String, sessionId: Int) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            db.delete("SETS", "session_id = ? AND user_id = ?", arrayOf(sessionId.toString(), userId))
            db.delete("WORKOUT_SESSIONS", "session_id = ? AND user_id = ?", arrayOf(sessionId.toString(), userId))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateSet(userId: String, setId: Int, reps: Int, weight: Float) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("reps", reps)
        contentValues.put("weight_used", weight)
        db.update("SETS", contentValues, "set_id = ? AND user_id = ?", arrayOf(setId.toString(), userId))
    }

    fun deleteSet(userId: String, setId: Int) {
        val db = this.writableDatabase
        db.delete("SETS", "set_id = ? AND user_id = ?", arrayOf(setId.toString(), userId))
    }

    fun deleteExerciseFromWorkout(userId: String, workoutId: Int, exerciseId: Int) {
        val db = this.writableDatabase
        var sessionId = -1
        val cursor = db.rawQuery("SELECT session_id FROM WORKOUT_SESSIONS WHERE workout_id = ? AND user_id = ? ORDER BY session_id DESC LIMIT 1", arrayOf(workoutId.toString(), userId))
        if (cursor.moveToFirst()) {
            sessionId = cursor.getInt(cursor.getColumnIndexOrThrow("session_id"))
        }
        cursor.close()
        if (sessionId != -1) {
            db.delete("SETS", "session_id = ? AND exercise_id = ? AND user_id = ?", arrayOf(sessionId.toString(), exerciseId.toString(), userId))
        }
    }

    fun clearAllData(userId: String) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            db.delete("SETS", "user_id = ?", arrayOf(userId))
            db.delete("WORKOUT_SESSIONS", "user_id = ?", arrayOf(userId))
            db.delete("WORKOUT", "user_id = ?", arrayOf(userId))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun cleanUpOrphanedData(userId: String) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Delete sets linked to incomplete sessions
            db.execSQL("DELETE FROM SETS WHERE session_id IN (SELECT session_id FROM WORKOUT_SESSIONS WHERE end_time IS NULL AND user_id = ?)", arrayOf(userId))
            
            // Delete incomplete sessions
            db.delete("WORKOUT_SESSIONS", "end_time IS NULL AND user_id = ?", arrayOf(userId))
            
            // Delete workouts that have no sessions left
            db.execSQL("DELETE FROM WORKOUT WHERE workout_id NOT IN (SELECT DISTINCT workout_id FROM WORKOUT_SESSIONS WHERE user_id = ?) AND user_id = ?", arrayOf(userId, userId))
            
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun restoreData(userId: String, workoutData: List<com.example.gymworkout.data.sync.FirestoreSyncManager.WorkoutData>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            workoutData.forEach { wData ->
                val w = wData.workout
                val workoutValues = ContentValues()
                workoutValues.put("workout_id", w.id)
                workoutValues.put("user_id", userId)
                workoutValues.put("workout_name", w.name)
                db.insertWithOnConflict("WORKOUT", null, workoutValues, SQLiteDatabase.CONFLICT_REPLACE)

                wData.sessions.forEach { sData ->
                    val s = sData.session
                    val sessionValues = ContentValues()
                    sessionValues.put("session_id", s.id)
                    sessionValues.put("workout_id", s.workoutId)
                    sessionValues.put("workout_date", s.date)
                    sessionValues.put("start_time", s.startTime)
                    sessionValues.put("end_time", s.endTime)
                    sessionValues.put("user_id", userId)
                    db.insertWithOnConflict("WORKOUT_SESSIONS", null, sessionValues, SQLiteDatabase.CONFLICT_REPLACE)

                    sData.exercises.forEach { eData ->
                        val e = eData.exercise
                        // Ensure exercise exists in EXERCISES (it's shared, but good to check or at least assume it's there)
                        // If it's a custom exercise, it should be in EXERCISES table.

                        eData.sets.forEach { set ->
                            val setValues = ContentValues()
                            if (set.id != 0) setValues.put("set_id", set.id)
                            setValues.put("session_id", s.id)
                            setValues.put("exercise_id", e.id)
                            setValues.put("set_number", set.setNumber)
                            setValues.put("reps", set.reps)
                            setValues.put("weight_used", set.weightUsed)
                            setValues.put("is_completed", if (set.isCompleted) 1 else 0)
                            setValues.put("user_id", userId)
                            db.insertWithOnConflict("SETS", null, setValues, SQLiteDatabase.CONFLICT_REPLACE)
                        }
                    }
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getHistoryExercisesForSession(userId: String, sessionId: Int): List<com.example.gymworkout.data.model.HistoryExercise> {
        val exercises = mutableListOf<com.example.gymworkout.data.model.HistoryExercise>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("""
            SELECT e.exercise_name, COUNT(s.set_id) as set_count, MAX(s.weight_used) as max_weight
            FROM EXERCISES e
            INNER JOIN SETS s ON e.exercise_id = s.exercise_id
            WHERE s.session_id = ? AND s.user_id = ?
            GROUP BY e.exercise_name
        """, arrayOf(sessionId.toString(), userId))

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("exercise_name"))
                val setCount = cursor.getInt(cursor.getColumnIndexOrThrow("set_count"))
                val maxWeight = cursor.getFloat(cursor.getColumnIndexOrThrow("max_weight"))
                
                // Get reps for that max weight (simplification: first set matching max weight)
                val repsCursor = db.rawQuery("SELECT reps FROM SETS WHERE session_id = ? AND exercise_id = (SELECT exercise_id FROM EXERCISES WHERE exercise_name = ?) AND weight_used = ? LIMIT 1",
                    arrayOf(sessionId.toString(), name, maxWeight.toString()))
                var maxReps = 0
                if (repsCursor.moveToFirst()) {
                    maxReps = repsCursor.getInt(repsCursor.getColumnIndexOrThrow("reps"))
                }
                repsCursor.close()

                exercises.add(com.example.gymworkout.data.model.HistoryExercise(name, setCount, maxWeight, maxReps))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return exercises
    }

    fun updateSetCompletionStatus(userId: String, setId: Int, isCompleted: Boolean) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("is_completed", if (isCompleted) 1 else 0)
        db.update("SETS", contentValues, "set_id = ? AND user_id = ?", arrayOf(setId.toString(), userId))
    }
}