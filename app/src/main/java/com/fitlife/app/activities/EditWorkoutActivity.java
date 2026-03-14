package com.fitlife.app.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fitlife.app.R;
import com.fitlife.app.adapters.ExerciseAdapter;
import com.fitlife.app.database.AppDatabase;
import com.fitlife.app.database.dao.EquipmentDao;
import com.fitlife.app.database.dao.ExerciseDao;
import com.fitlife.app.database.dao.WorkoutDao;
import com.fitlife.app.database.entities.Equipment;
import com.fitlife.app.database.entities.Exercise;
import com.fitlife.app.database.entities.Workout;
import com.fitlife.app.interfaces.OnExerciseActionListener;
import com.fitlife.app.utils.Constants;
import com.fitlife.app.utils.ExerciseDialogHelper;
import com.fitlife.app.utils.ImageHelper;
import com.fitlife.app.utils.ValidationHelper;
import com.google.android.material.button.MaterialButton;
import com.fitlife.app.utils.PreferenceManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class EditWorkoutActivity extends AppCompatActivity {

    private ImageView ivWorkoutImage;
    private MaterialButton btnSelectImage;
    private TextInputLayout tilWorkoutName;
    private TextInputLayout tilWorkoutDescription;
    private TextInputEditText etWorkoutName;
    private TextInputEditText etWorkoutDescription;
    private RecyclerView rvExercises;
    private MaterialButton btnAddExercise;
    private MaterialButton btnSaveWorkout;

    private AppDatabase database;
    private WorkoutDao workoutDao;
    private ExerciseDao exerciseDao;
    private EquipmentDao equipmentDao;

    private Workout workout;
    private Bitmap selectedImage;
    private String imagePath;

    private List<Exercise> exerciseList;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exercisesToDelete;

    // Maps exercise position -> equipment names
    private Map<Integer, List<String>> equipmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);

        initializeDatabase();
        initializeViews();
        setupToolbar();
        loadWorkoutData();
        setupListeners();
    }

    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        workoutDao = database.workoutDao();
        exerciseDao = database.exerciseDao();
        equipmentDao = database.equipmentDao();
        exercisesToDelete = new ArrayList<>();
    }

    private void initializeViews() {
        ivWorkoutImage = findViewById(R.id.iv_workout_image);
        btnSelectImage = findViewById(R.id.btn_select_image);
        tilWorkoutName = findViewById(R.id.til_workout_name);
        tilWorkoutDescription = findViewById(R.id.til_workout_description);
        etWorkoutName = findViewById(R.id.et_workout_name);
        etWorkoutDescription = findViewById(R.id.et_workout_description);
        rvExercises = findViewById(R.id.rv_exercises);
        btnAddExercise = findViewById(R.id.btn_add_exercise);
        btnSaveWorkout = findViewById(R.id.btn_save_workout);

        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(this, exerciseList, new OnExerciseActionListener() {
            @Override
            public void onExerciseClick(Exercise exercise) {
                int position = exerciseList.indexOf(exercise);
                editExercise(exercise, position);
            }

            @Override
            public void onEditExercise(Exercise exercise, int position) {
                editExercise(exercise, position);
            }

            @Override
            public void onDeleteExercise(Exercise exercise, int position) {
                deleteExercise(exercise, position);
            }

            @Override
            public void onExerciseCompletionChanged(Exercise exercise, boolean isCompleted) {
                exercise.setCompleted(isCompleted);
            }
        });
        rvExercises.setAdapter(exerciseAdapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadWorkoutData() {
        int workoutId = getIntent().getIntExtra(Constants.EXTRA_WORKOUT_ID, -1);

        if (workoutId == -1) {
            Toast.makeText(this, "Error loading workout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            workout = workoutDao.getWorkoutById(workoutId);
            List<Exercise> dbExercises = exerciseDao.getExercisesByWorkoutId(workoutId);

            // Load existing equipment for each exercise into the map
            for (int i = 0; i < dbExercises.size(); i++) {
                Exercise exercise = dbExercises.get(i);
                List<Equipment> equipmentList = equipmentDao.getEquipmentByExerciseId(exercise.getExerciseId());
                if (!equipmentList.isEmpty()) {
                    List<String> names = new ArrayList<>();
                    for (Equipment e : equipmentList) {
                        names.add(e.getEquipmentName());
                    }
                    equipmentMap.put(i, names);
                }
            }

            runOnUiThread(() -> {
                if (workout != null) {
                    etWorkoutName.setText(workout.getWorkoutName());
                    etWorkoutDescription.setText(workout.getDescription());

                    if (workout.getImagePath() != null) {
                        Bitmap bitmap = ImageHelper.loadImageFromPath(workout.getImagePath());
                        if (bitmap != null) {
                            ivWorkoutImage.setImageBitmap(bitmap);
                        }
                    }

                    exerciseList.clear();
                    exerciseList.addAll(dbExercises);
                    exerciseAdapter.notifyDataSetChanged();
                }
            });
        });
    }

    private void setupListeners() {
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnAddExercise.setOnClickListener(v -> addExercise());
        btnSaveWorkout.setOnClickListener(v -> updateWorkout());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.REQUEST_IMAGE_PICK);
    }

    private void addExercise() {
        if (workout == null) return;

        ExerciseDialogHelper.showAddExerciseDialog(this, workout.getWorkoutId(), (exercise, equipmentNames) -> {
            int position = exerciseList.size();
            exerciseAdapter.addExercise(exercise);
            if (!equipmentNames.isEmpty()) {
                equipmentMap.put(position, equipmentNames);
            }
        });
    }

    private void editExercise(Exercise exercise, int position) {
        List<String> existing = equipmentMap.getOrDefault(position, new ArrayList<>());
        ExerciseDialogHelper.showEditExerciseDialog(this, exercise, existing, (updatedExercise, equipmentNames) -> {
            exerciseAdapter.updateExercise(position, updatedExercise);
            equipmentMap.put(position, equipmentNames);
        });
    }

    private void deleteExercise(Exercise exercise, int position) {
        if (exercise.getExerciseId() != 0) {
            exercisesToDelete.add(exercise);
        }
        exerciseAdapter.removeExercise(position);
        equipmentMap.remove(position);

        // Re-index equipment map after deletion
        Map<Integer, List<String>> updatedMap = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : equipmentMap.entrySet()) {
            int key = entry.getKey();
            updatedMap.put(key > position ? key - 1 : key, entry.getValue());
        }
        equipmentMap = updatedMap;
    }

    private void updateWorkout() {
        tilWorkoutName.setError(null);
        tilWorkoutDescription.setError(null);

        String workoutName = etWorkoutName.getText().toString().trim();
        String workoutDescription = etWorkoutDescription.getText().toString().trim();

        if (!ValidationHelper.isValidWorkoutName(workoutName)) {
            tilWorkoutName.setError(getString(R.string.error_empty_field));
            etWorkoutName.requestFocus();
            return;
        }

        if (!ValidationHelper.isValidDescription(workoutDescription)) {
            tilWorkoutDescription.setError(getString(R.string.error_description_too_long));
            etWorkoutDescription.requestFocus();
            return;
        }

        btnSaveWorkout.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            workout.setWorkoutName(workoutName);
            workout.setDescription(workoutDescription);
            workout.setUpdatedAt(System.currentTimeMillis());

            if (selectedImage != null) {
                if (workout.getImagePath() != null) {
                    ImageHelper.deleteImage(workout.getImagePath());
                }
                imagePath = ImageHelper.saveImageToInternalStorage(this, selectedImage, workout.getWorkoutId());
                workout.setImagePath(imagePath);
            }

            workoutDao.update(workout);

            // Delete removed exercises (cascades to their equipment)
            if (!exercisesToDelete.isEmpty()) {
                exerciseDao.delete(exercisesToDelete);
            }

            // Save/update exercises and their equipment
            for (int i = 0; i < exerciseList.size(); i++) {
                Exercise exercise = exerciseList.get(i);
                exercise.setWorkoutId(workout.getWorkoutId());
                exercise.setOrderIndex(i);

                long exerciseId;
                if (exercise.getExerciseId() == 0) {
                    exerciseId = exerciseDao.insert(exercise);
                } else {
                    exerciseDao.update(exercise);
                    exerciseId = exercise.getExerciseId();
                    // Clear old equipment before re-saving
                    equipmentDao.deleteEquipmentByExerciseId((int) exerciseId);
                }

                // Save updated equipment for this exercise
                List<String> equipmentNames = equipmentMap.get(i);
                if (equipmentNames != null && !equipmentNames.isEmpty()) {
                    List<Equipment> equipmentList = new ArrayList<>();
                    for (String name : equipmentNames) {
                        equipmentList.add(new Equipment((int) exerciseId, name));
                    }
                    equipmentDao.insertAll(equipmentList);
                }
            }

            runOnUiThread(() -> {
                btnSaveWorkout.setEnabled(true);
                Toast.makeText(this, getString(R.string.success_workout_updated), Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                selectedImage = ImageHelper.loadAndResizeBitmap(this, imageUri);
                if (selectedImage != null) {
                    ivWorkoutImage.setImageBitmap(selectedImage);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}