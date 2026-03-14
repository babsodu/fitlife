package com.fitlife.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fitlife.app.R;
import com.fitlife.app.database.AppDatabase;
import com.fitlife.app.database.dao.EquipmentDao;
import com.fitlife.app.database.dao.WorkoutDao;
import com.fitlife.app.utils.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * Profile Activity - Display user profile and statistics
 */
public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvMemberSince;
    private TextView tvTotalWorkouts;
    private TextView tvCompletedWorkouts;
    private TextView tvEquipmentCount;
    private MaterialButton btnLogout;

    private AppDatabase database;
    private WorkoutDao workoutDao;
    private EquipmentDao equipmentDao;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeDatabase();
        initializeViews();
        setupToolbar();
        loadProfileData();
        setupListeners();
    }

    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        workoutDao = database.workoutDao();
        equipmentDao = database.equipmentDao();
        preferenceManager = new PreferenceManager(this);
    }

    private void initializeViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvMemberSince = findViewById(R.id.tv_member_since);
        btnLogout = findViewById(R.id.btn_logout);

        // Drill into each included stat card layout by its ID
        View layoutTotal = findViewById(R.id.layout_total_workouts);
        View layoutCompleted = findViewById(R.id.layout_completed_workouts);
        View layoutEquipment = findViewById(R.id.layout_equipment_count);

        tvTotalWorkouts = layoutTotal.findViewById(R.id.tv_stat_value);
        TextView tvLabelTotal = layoutTotal.findViewById(R.id.tv_stat_label);
        tvLabelTotal.setText(R.string.total_workouts);

        tvCompletedWorkouts = layoutCompleted.findViewById(R.id.tv_stat_value);
        TextView tvLabelCompleted = layoutCompleted.findViewById(R.id.tv_stat_label);
        tvLabelCompleted.setText(R.string.completed_this_week);

        tvEquipmentCount = layoutEquipment.findViewById(R.id.tv_stat_value);
        TextView tvLabelEquipment = layoutEquipment.findViewById(R.id.tv_stat_label);
        tvLabelEquipment.setText(R.string.equipment_items);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadProfileData() {
        tvUserName.setText(preferenceManager.getUserName());
        tvUserEmail.setText(preferenceManager.getUserEmail());

        int userId = preferenceManager.getUserId();

        Executors.newSingleThreadExecutor().execute(() -> {
            int totalWorkouts = workoutDao.getTotalWorkoutCountForUser(userId);
            int completedWorkouts = workoutDao.getCompletedWorkoutCountForUser(userId);
            int equipmentCount = equipmentDao.getTotalEquipmentCountForUser(userId);

            runOnUiThread(() -> {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                tvMemberSince.setText(sdf.format(new Date()));

                tvTotalWorkouts.setText(String.valueOf(totalWorkouts));
                tvCompletedWorkouts.setText(String.valueOf(completedWorkouts));
                tvEquipmentCount.setText(String.valueOf(equipmentCount));
            });
        });
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_logout_title)
                .setMessage(R.string.confirm_logout_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> logout())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void logout() {
        preferenceManager.clearSession();
        android.content.Intent intent = new android.content.Intent(this, WelcomeActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

