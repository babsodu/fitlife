package com.fitlife.app.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.fitlife.app.database.dao.EquipmentDao;
import com.fitlife.app.database.dao.ExerciseDao;
import com.fitlife.app.database.dao.WorkoutDao;
import com.fitlife.app.database.entities.Equipment;
import com.fitlife.app.database.entities.Exercise;
import com.fitlife.app.database.entities.Workout;

import java.util.Arrays;
import java.util.concurrent.Executors;

public class DatabaseCallback extends RoomDatabase.Callback {

    private static final String TAG = "DB_SEED";
    private final Context context;

    public DatabaseCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);
        Log.d(TAG, "onCreate fired — starting seed");
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AppDatabase database = AppDatabase.getInstance(context);
                seedDatabase(
                        database.workoutDao(),
                        database.exerciseDao(),
                        database.equipmentDao()
                );
                Log.d(TAG, "Seed completed successfully");
            } catch (Exception e) {
                Log.e(TAG, "Seed failed: " + e.getMessage(), e);
            }
        });
    }

    private void addEquipment(EquipmentDao equipmentDao, Equipment... items) {
        equipmentDao.insertAll(Arrays.asList(items));
    }

    private void seedDatabase(WorkoutDao workoutDao, ExerciseDao exerciseDao, EquipmentDao equipmentDao) {

        // ════════════════════════════════════════════════════════════════════════
        //  WORKOUT 1 — Full Body Strength
        // ════════════════════════════════════════════════════════════════════════
        long w1 = workoutDao.insert(new Workout(
                null, "Full Body Strength",
                "Classic compound movements targeting every major muscle group.", null));
        Log.d(TAG, "Inserted workout: Full Body Strength, id=" + w1);

        long e1 = exerciseDao.insert(new Exercise(
                (int) w1, "Barbell Squat", 4, 8,
                "Feet shoulder-width apart. Keep chest up and push knees out as you descend.", 1));
        addEquipment(equipmentDao,
                new Equipment((int) e1, "Barbell"),
                new Equipment((int) e1, "Squat Rack"),
                new Equipment((int) e1, "Weight Plates"));

        long e2 = exerciseDao.insert(new Exercise(
                (int) w1, "Bench Press", 4, 8,
                "Grip bar slightly wider than shoulders. Lower to chest, press up explosively.", 2));
        addEquipment(equipmentDao,
                new Equipment((int) e2, "Barbell"),
                new Equipment((int) e2, "Flat Bench"),
                new Equipment((int) e2, "Weight Plates"));

        long e3 = exerciseDao.insert(new Exercise(
                (int) w1, "Bent Over Row", 3, 10,
                "Hinge at hips, pull bar to lower chest. Keep back flat throughout.", 3));
        addEquipment(equipmentDao,
                new Equipment((int) e3, "Barbell"),
                new Equipment((int) e3, "Weight Plates"));

        long e4 = exerciseDao.insert(new Exercise(
                (int) w1, "Overhead Press", 3, 10,
                "Press bar from shoulder height to fully overhead. Keep core tight.", 4));
        addEquipment(equipmentDao,
                new Equipment((int) e4, "Barbell"),
                new Equipment((int) e4, "Weight Plates"));

        long e5 = exerciseDao.insert(new Exercise(
                (int) w1, "Romanian Deadlift", 3, 12,
                "Push hips back, lower bar along legs until you feel a hamstring stretch.", 5));
        addEquipment(equipmentDao,
                new Equipment((int) e5, "Barbell"),
                new Equipment((int) e5, "Weight Plates"));


        // ════════════════════════════════════════════════════════════════════════
        //  WORKOUT 2 — HIIT Cardio Blast
        // ════════════════════════════════════════════════════════════════════════
        long w2 = workoutDao.insert(new Workout(
                null, "HIIT Cardio Blast",
                "High-intensity intervals to torch calories and boost endurance.", null));
        Log.d(TAG, "Inserted workout: HIIT Cardio Blast, id=" + w2);

        exerciseDao.insert(new Exercise(
                (int) w2, "Burpees", 4, 15,
                "Drop to plank, do a push-up, jump feet in, explode up with arms overhead.", 1));
        exerciseDao.insert(new Exercise(
                (int) w2, "Jump Squats", 4, 20,
                "Squat down then explode upward. Land softly with bent knees.", 2));
        exerciseDao.insert(new Exercise(
                (int) w2, "Mountain Climbers", 3, 30,
                "In plank position, alternate driving knees to chest as fast as possible.", 3));
        exerciseDao.insert(new Exercise(
                (int) w2, "High Knees", 3, 40,
                "Run in place, driving knees up to hip height. Pump your arms.", 4));

        long e10 = exerciseDao.insert(new Exercise(
                (int) w2, "Box Jumps", 3, 12,
                "Jump onto box with both feet, landing softly. Step down carefully.", 5));
        addEquipment(equipmentDao, new Equipment((int) e10, "Plyo Box"));


        // ════════════════════════════════════════════════════════════════════════
        //  WORKOUT 3 — Upper Body Push
        // ════════════════════════════════════════════════════════════════════════
        long w3 = workoutDao.insert(new Workout(
                null, "Upper Body Push",
                "Chest, shoulders and triceps focused pressing movements.", null));
        Log.d(TAG, "Inserted workout: Upper Body Push, id=" + w3);

        long e11 = exerciseDao.insert(new Exercise(
                (int) w3, "Incline Dumbbell Press", 4, 10,
                "Set bench to 30-45 degrees. Press dumbbells from chest upward.", 1));
        addEquipment(equipmentDao,
                new Equipment((int) e11, "Dumbbells"),
                new Equipment((int) e11, "Incline Bench"));

        long e12 = exerciseDao.insert(new Exercise(
                (int) w3, "Lateral Raises", 3, 15,
                "Raise dumbbells to shoulder height with a slight bend in elbows.", 2));
        addEquipment(equipmentDao, new Equipment((int) e12, "Dumbbells"));

        long e13 = exerciseDao.insert(new Exercise(
                (int) w3, "Tricep Dips", 3, 12,
                "Lower body by bending elbows to 90 degrees, then press back up.", 3));
        addEquipment(equipmentDao, new Equipment((int) e13, "Dip Bars"));

        long e14 = exerciseDao.insert(new Exercise(
                (int) w3, "Cable Fly", 3, 15,
                "Bring handles together in an arc motion, squeezing chest at centre.", 4));
        addEquipment(equipmentDao,
                new Equipment((int) e14, "Cable Machine"),
                new Equipment((int) e14, "D-Handle Attachments"));

        long e15 = exerciseDao.insert(new Exercise(
                (int) w3, "Skull Crushers", 3, 12,
                "Lower bar to forehead by bending elbows only. Extend back up slowly.", 5));
        addEquipment(equipmentDao,
                new Equipment((int) e15, "EZ Bar"),
                new Equipment((int) e15, "Flat Bench"),
                new Equipment((int) e15, "Weight Plates"));


        // ════════════════════════════════════════════════════════════════════════
        //  WORKOUT 4 — Core & Abs
        // ════════════════════════════════════════════════════════════════════════
        long w4 = workoutDao.insert(new Workout(
                null, "Core & Abs",
                "Targeted core work to build stability and a strong midsection.", null));
        Log.d(TAG, "Inserted workout: Core & Abs, id=" + w4);

        exerciseDao.insert(new Exercise(
                (int) w4, "Plank", 3, 1,
                "Hold a straight line from head to heels. Brace your core. Hold 60 seconds.", 1));
        exerciseDao.insert(new Exercise(
                (int) w4, "Crunches", 3, 20,
                "Lie on back, curl shoulders toward knees. Control the movement.", 2));
        exerciseDao.insert(new Exercise(
                (int) w4, "Leg Raises", 3, 15,
                "Keep legs straight, raise until perpendicular to floor. Lower slowly.", 3));

        long e19 = exerciseDao.insert(new Exercise(
                (int) w4, "Russian Twists", 3, 20,
                "Lean back slightly, rotate torso side to side. Hold weight for extra difficulty.", 4));
        addEquipment(equipmentDao, new Equipment((int) e19, "Weight Plate"));

        exerciseDao.insert(new Exercise(
                (int) w4, "Dead Bug", 3, 10,
                "Extend opposite arm and leg while keeping lower back pressed to floor.", 5));


        // ════════════════════════════════════════════════════════════════════════
        //  WORKOUT 5 — Pull Day
        // ════════════════════════════════════════════════════════════════════════
        long w5 = workoutDao.insert(new Workout(
                null, "Pull Day",
                "Back and biceps focused pulling movements for a strong posterior chain.", null));
        Log.d(TAG, "Inserted workout: Pull Day, id=" + w5);

        long e21 = exerciseDao.insert(new Exercise(
                (int) w5, "Deadlift", 4, 5,
                "Keep bar close to body, drive hips forward. Maintain a neutral spine.", 1));
        addEquipment(equipmentDao,
                new Equipment((int) e21, "Barbell"),
                new Equipment((int) e21, "Weight Plates"));

        long e22 = exerciseDao.insert(new Exercise(
                (int) w5, "Pull-Ups", 4, 8,
                "Start from a full hang, pull chest to bar. Control the descent.", 2));
        addEquipment(equipmentDao, new Equipment((int) e22, "Pull-Up Bar"));

        long e23 = exerciseDao.insert(new Exercise(
                (int) w5, "Seated Cable Row", 3, 12,
                "Pull handle to lower chest. Squeeze shoulder blades together at the end.", 3));
        addEquipment(equipmentDao,
                new Equipment((int) e23, "Cable Machine"),
                new Equipment((int) e23, "Row Attachment"));

        long e24 = exerciseDao.insert(new Exercise(
                (int) w5, "Face Pulls", 3, 15,
                "Pull rope to face level, flaring elbows out. Excellent for rear delts.", 4));
        addEquipment(equipmentDao,
                new Equipment((int) e24, "Cable Machine"),
                new Equipment((int) e24, "Rope Attachment"));

        long e25 = exerciseDao.insert(new Exercise(
                (int) w5, "Barbell Curl", 3, 10,
                "Keep elbows pinned to sides. Curl bar to shoulders, lower slowly.", 5));
        addEquipment(equipmentDao,
                new Equipment((int) e25, "Barbell"),
                new Equipment((int) e25, "Weight Plates"));


        // ════════════════════════════════════════════════════════════════════════
        //  WORKOUT 6 — Lower Body Power
        // ════════════════════════════════════════════════════════════════════════
        long w6 = workoutDao.insert(new Workout(
                null, "Lower Body Power",
                "Explosive leg training to build size, strength and athleticism.", null));
        Log.d(TAG, "Inserted workout: Lower Body Power, id=" + w6);

        long e26 = exerciseDao.insert(new Exercise(
                (int) w6, "Front Squat", 4, 6,
                "Rest bar on front delts, elbows high. Squat deep keeping torso upright.", 1));
        addEquipment(equipmentDao,
                new Equipment((int) e26, "Barbell"),
                new Equipment((int) e26, "Squat Rack"),
                new Equipment((int) e26, "Weight Plates"));

        long e27 = exerciseDao.insert(new Exercise(
                (int) w6, "Leg Press", 4, 10,
                "Push platform away until legs are almost straight. Do not lock your knees.", 2));
        addEquipment(equipmentDao,
                new Equipment((int) e27, "Leg Press Machine"),
                new Equipment((int) e27, "Weight Plates"));

        long e28 = exerciseDao.insert(new Exercise(
                (int) w6, "Walking Lunges", 3, 12,
                "Step forward into a lunge, back knee almost touching floor. Alternate legs.", 3));
        addEquipment(equipmentDao, new Equipment((int) e28, "Dumbbells"));

        long e29 = exerciseDao.insert(new Exercise(
                (int) w6, "Leg Curl", 3, 12,
                "Curl weight toward glutes, squeezing hamstrings at the top.", 4));
        addEquipment(equipmentDao, new Equipment((int) e29, "Leg Curl Machine"));

        long e30 = exerciseDao.insert(new Exercise(
                (int) w6, "Calf Raises", 4, 20,
                "Rise up on toes as high as possible. Lower slowly for a full stretch.", 5));
        addEquipment(equipmentDao, new Equipment((int) e30, "Calf Raise Machine"));


        // ════════════════════════════════════════════════════════════════════════
        //  WORKOUT 7 — Mobility & Flexibility
        // ════════════════════════════════════════════════════════════════════════
        long w7 = workoutDao.insert(new Workout(
                null, "Mobility & Flexibility",
                "Low-intensity stretching and mobility work for recovery and injury prevention.", null));
        Log.d(TAG, "Inserted workout: Mobility & Flexibility, id=" + w7);

        exerciseDao.insert(new Exercise(
                (int) w7, "Hip Flexor Stretch", 3, 1,
                "Kneel on one knee, push hips forward gently. Hold 30 seconds each side.", 1));
        exerciseDao.insert(new Exercise(
                (int) w7, "Cat-Cow Stretch", 3, 10,
                "On all fours, alternate arching and rounding spine with each breath.", 2));
        exerciseDao.insert(new Exercise(
                (int) w7, "Pigeon Pose", 3, 1,
                "Front shin parallel to mat, hinge forward over leg. Hold 45 seconds each side.", 3));
        exerciseDao.insert(new Exercise(
                (int) w7, "Thoracic Rotation", 3, 10,
                "Sit cross-legged, rotate upper body each side. Keep hips still.", 4));
        exerciseDao.insert(new Exercise(
                (int) w7, "World's Greatest Stretch", 3, 5,
                "Lunge forward, rotate upper body reaching arm to sky. Alternate sides.", 5));

        Log.d(TAG, "All 7 workouts seeded successfully");
    }
}