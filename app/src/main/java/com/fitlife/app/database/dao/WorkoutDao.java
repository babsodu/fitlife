package com.fitlife.app.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fitlife.app.database.entities.Workout;

import java.util.List;

/**
 * Data Access Object for Workout entity
 */
@Dao
public interface WorkoutDao {

    /**
     * Insert a new workout
     * @param workout Workout to insert
     * @return Row ID of inserted workout
     */
    @Insert
    long insert(Workout workout);

    /**
     * Update an existing workout
     * @param workout Workout to update
     */
    @Update
    void update(Workout workout);

    /**
     * Delete a workout (cascades to exercises and equipment)
     * @param workout Workout to delete
     */
    @Delete
    void delete(Workout workout);

    /**
     * Get all workouts for a user (their own + all templates)
     * @param userId User ID
     * @return List of workouts
     */
    @Query("SELECT * FROM workouts WHERE user_id IS NULL OR user_id = :userId ORDER BY user_id IS NULL ASC, created_at DESC")
    List<Workout> getAllWorkoutsForUser(int userId);

    /**
     * Get only user-created workouts (excludes templates)
     * @param userId User ID
     * @return List of workouts
     */
    @Query("SELECT * FROM workouts WHERE user_id = :userId ORDER BY created_at DESC")
    List<Workout> getWorkoutsByUserId(int userId);

    /**
     * Get all template workouts (pre-seeded, no owner)
     * @return List of template workouts
     */
    @Query("SELECT * FROM workouts WHERE user_id IS NULL ORDER BY workout_name ASC")
    List<Workout> getTemplateWorkouts();

    /**
     * Get workout by ID
     * @param workoutId Workout ID
     * @return Workout object or null
     */
    @Query("SELECT * FROM workouts WHERE workout_id = :workoutId LIMIT 1")
    Workout getWorkoutById(int workoutId);

    /**
     * Get completed workouts for a user (includes templates)
     * @param userId User ID
     * @return List of completed workouts
     */
    @Query("SELECT * FROM workouts WHERE (user_id IS NULL OR user_id = :userId) AND is_completed = 1 ORDER BY updated_at DESC")
    List<Workout> getCompletedWorkouts(int userId);

    /**
     * Get incomplete workouts for a user (includes templates)
     * @param userId User ID
     * @return List of incomplete workouts
     */
    @Query("SELECT * FROM workouts WHERE (user_id IS NULL OR user_id = :userId) AND is_completed = 0 ORDER BY created_at DESC")
    List<Workout> getIncompleteWorkouts(int userId);

    /**
     * Mark workout as completed
     * @param workoutId Workout ID
     * @param isCompleted Completion status
     * @param timestamp Updated at timestamp
     */
    @Query("UPDATE workouts SET is_completed = :isCompleted, updated_at = :timestamp WHERE workout_id = :workoutId")
    void updateCompletionStatus(int workoutId, boolean isCompleted, long timestamp);

    /**
     * Get total workout count for a user (includes templates)
     * @param userId User ID
     * @return Total number of workouts
     */
    @Query("SELECT COUNT(*) FROM workouts WHERE user_id IS NULL OR user_id = :userId")
    int getTotalWorkoutCountForUser(int userId);

    /**
     * Get completed workout count for a user (includes templates)
     * @param userId User ID
     * @return Number of completed workouts
     */
    @Query("SELECT COUNT(*) FROM workouts WHERE (user_id IS NULL OR user_id = :userId) AND is_completed = 1")
    int getCompletedWorkoutCountForUser(int userId);

    /**
     * Reset all workouts to incomplete for a user (only resets user's own workouts, not templates)
     * @param userId User ID
     */
    @Query("UPDATE workouts SET is_completed = 0 WHERE user_id = :userId")
    void resetAllWorkouts(int userId);

    /**
     * Clone a template into a user's personal workout
     * @param templateId Template workout ID
     * @param userId User ID
     * @param timestamp Created/updated timestamp
     * @return Row ID of new workout
     */
    @Query("INSERT INTO workouts (user_id, workout_name, description, image_path, is_completed, created_at, updated_at) " +
            "SELECT :userId, workout_name, description, image_path, 0, :timestamp, :timestamp " +
            "FROM workouts WHERE workout_id = :templateId")
    long cloneTemplateForUser(int templateId, int userId, long timestamp);
}