package com.example.projectexpensetrackeradmin.repository;

import android.content.Context;

import com.example.projectexpensetrackeradmin.database.DatabaseHelper;
import com.example.projectexpensetrackeradmin.model.Project;
import com.example.projectexpensetrackeradmin.util.AppExecutors;
import com.example.projectexpensetrackeradmin.util.RepositoryCallback;

import java.util.List;

public class ProjectRepository {

    private final DatabaseHelper databaseHelper;

    public ProjectRepository(Context context) {
        this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public void getAllProjects(RepositoryCallback<List<Project>> callback) {
        AppExecutors.getDatabaseExecutor().execute(() -> {
            List<Project> result = databaseHelper.getAllProjects();
            callback.onComplete(result);
        });
    }

    public void searchProjects(String keyword, RepositoryCallback<List<Project>> callback) {
        AppExecutors.getDatabaseExecutor().execute(() -> {
            List<Project> result = databaseHelper.searchProjects(keyword);
            callback.onComplete(result);
        });
    }

    public void advancedSearchProjects(String keyword, String status, String manager, String startDate,
                                       RepositoryCallback<List<Project>> callback) {
        AppExecutors.getDatabaseExecutor().execute(() -> {
            List<Project> result = databaseHelper.advancedSearchProjects(keyword, status, manager, startDate);
            callback.onComplete(result);
        });
    }

    public void deleteProject(int projectId, RepositoryCallback<Boolean> callback) {
        AppExecutors.getDatabaseExecutor().execute(() -> {
            try {
                databaseHelper.deleteProject(projectId);
                callback.onComplete(true);
            } catch (Exception e) {
                callback.onComplete(false);
            }
        });
    }

    public void resetDatabase(RepositoryCallback<Boolean> callback) {
        AppExecutors.getDatabaseExecutor().execute(() -> {
            try {
                databaseHelper.resetDatabase();
                callback.onComplete(true);
            } catch (Exception e) {
                callback.onComplete(false);
            }
        });
    }
}