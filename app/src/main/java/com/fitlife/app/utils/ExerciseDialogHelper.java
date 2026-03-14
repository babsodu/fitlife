package com.fitlife.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fitlife.app.R;
import com.fitlife.app.database.entities.Equipment;
import com.fitlife.app.database.entities.Exercise;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog helper for adding/editing exercises with equipment
 */
public class ExerciseDialogHelper {

    public interface OnExerciseDialogListener {
        void onExerciseSaved(Exercise exercise, List<String> equipmentNames);
    }

    public static void showAddExerciseDialog(Context context, int workoutId, OnExerciseDialogListener listener) {
        showExerciseDialog(context, null, null, workoutId, listener);
    }

    public static void showEditExerciseDialog(Context context, Exercise exercise, List<String> existingEquipment, OnExerciseDialogListener listener) {
        showExerciseDialog(context, exercise, existingEquipment, exercise.getWorkoutId(), listener);
    }

    private static void showExerciseDialog(Context context, Exercise existingExercise,
                                           List<String> existingEquipment, int workoutId,
                                           OnExerciseDialogListener listener) {

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_exercise, null);

        // Exercise fields
        TextInputLayout tilExerciseName = dialogView.findViewById(R.id.til_exercise_name);
        TextInputLayout tilSets = dialogView.findViewById(R.id.til_sets);
        TextInputLayout tilReps = dialogView.findViewById(R.id.til_reps);
        TextInputEditText etExerciseName = dialogView.findViewById(R.id.et_exercise_name);
        TextInputEditText etSets = dialogView.findViewById(R.id.et_sets);
        TextInputEditText etReps = dialogView.findViewById(R.id.et_reps);
        TextInputEditText etInstructions = dialogView.findViewById(R.id.et_instructions);

        // Equipment fields
        LinearLayout layoutEquipmentList = dialogView.findViewById(R.id.layout_equipment_list);
        LinearLayout layoutEquipmentInput = dialogView.findViewById(R.id.layout_equipment_input);
        TextInputEditText etEquipment = dialogView.findViewById(R.id.et_equipment);
        MaterialButton btnAddEquipment = dialogView.findViewById(R.id.btn_add_equipment);
        MaterialButton btnConfirmEquipment = dialogView.findViewById(R.id.btn_confirm_equipment);

        // Track equipment names
        List<String> equipmentNames = new ArrayList<>();

        // Pre-fill if editing
        if (existingExercise != null) {
            etExerciseName.setText(existingExercise.getExerciseName());
            etSets.setText(String.valueOf(existingExercise.getSets()));
            etReps.setText(String.valueOf(existingExercise.getReps()));
            etInstructions.setText(existingExercise.getInstructions());
        }

        // Pre-fill existing equipment if editing
        if (existingEquipment != null) {
            for (String name : existingEquipment) {
                equipmentNames.add(name);
                addEquipmentChip(context, layoutEquipmentList, name, equipmentNames);
            }
        }

        // Show input row when + is tapped
        btnAddEquipment.setOnClickListener(v -> {
            layoutEquipmentInput.setVisibility(View.VISIBLE);
            etEquipment.requestFocus();
        });

        // Confirm adding equipment item
        btnConfirmEquipment.setOnClickListener(v -> {
            String name = etEquipment.getText().toString().trim();
            if (!name.isEmpty() && !equipmentNames.contains(name)) {
                equipmentNames.add(name);
                addEquipmentChip(context, layoutEquipmentList, name, equipmentNames);
                etEquipment.setText("");
                layoutEquipmentInput.setVisibility(View.GONE);
            } else {
                etEquipment.setError(name.isEmpty() ? "Enter equipment name" : "Already added");
            }
        });

        // Build dialog
        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(context)
                .setTitle(existingExercise == null ? R.string.add_exercise : R.string.edit_exercise)
                .setView(dialogView)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            tilExerciseName.setError(null);
            tilSets.setError(null);
            tilReps.setError(null);

            String exerciseName = etExerciseName.getText().toString().trim();
            String setsStr = etSets.getText().toString().trim();
            String repsStr = etReps.getText().toString().trim();
            String instructions = etInstructions.getText().toString().trim();

            if (!ValidationHelper.isValidExerciseName(exerciseName)) {
                tilExerciseName.setError(context.getString(R.string.error_empty_field));
                return;
            }
            if (!ValidationHelper.isValidSetsReps(setsStr)) {
                tilSets.setError(context.getString(R.string.error_invalid_number));
                return;
            }
            if (!ValidationHelper.isValidSetsReps(repsStr)) {
                tilReps.setError(context.getString(R.string.error_invalid_number));
                return;
            }

            int sets = Integer.parseInt(setsStr);
            int reps = Integer.parseInt(repsStr);

            Exercise exercise;
            if (existingExercise != null) {
                exercise = existingExercise;
                exercise.setExerciseName(exerciseName);
                exercise.setSets(sets);
                exercise.setReps(reps);
                exercise.setInstructions(instructions);
            } else {
                exercise = new Exercise(workoutId, exerciseName, sets, reps);
                exercise.setInstructions(instructions);
            }

            if (listener != null) {
                listener.onExerciseSaved(exercise, equipmentNames);
            }

            dialog.dismiss();
        });
    }

    // Adds a removable chip-style row for each equipment item
    private static void addEquipmentChip(Context context, LinearLayout container,
                                         String name, List<String> equipmentNames) {
        View chip = LayoutInflater.from(context).inflate(R.layout.item_equipment_chip, container, false);
        TextView tvName = chip.findViewById(R.id.tv_equipment_name);
        MaterialButton btnRemove = chip.findViewById(R.id.btn_remove_equipment);

        tvName.setText(name);
        btnRemove.setOnClickListener(v -> {
            equipmentNames.remove(name);
            container.removeView(chip);
        });

        container.addView(chip);
    }
}