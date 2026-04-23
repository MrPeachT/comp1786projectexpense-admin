package com.example.projectexpensetrackeradmin.repository;

import android.content.Context;

import com.example.projectexpensetrackeradmin.database.DatabaseHelper;
import com.example.projectexpensetrackeradmin.model.Expense;
import com.example.projectexpensetrackeradmin.util.AppExecutors;
import com.example.projectexpensetrackeradmin.util.RepositoryCallback;

import java.util.List;

public class ExpenseRepository {

    private final DatabaseHelper databaseHelper;

    public ExpenseRepository(Context context) {
        this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public void getAllExpenses(RepositoryCallback<List<Expense>> callback) {
        AppExecutors.getDatabaseExecutor().execute(() -> {
            List<Expense> result = databaseHelper.getAllExpenses();
            callback.onComplete(result);
        });
    }

    public void getExpensesByProjectId(int projectId, RepositoryCallback<List<Expense>> callback) {
        AppExecutors.getDatabaseExecutor().execute(() -> {
            List<Expense> result = databaseHelper.getExpensesByProjectId(projectId);
            callback.onComplete(result);
        });
    }

    public void deleteExpense(int expenseId, RepositoryCallback<Boolean> callback) {
        AppExecutors.getDatabaseExecutor().execute(() -> {
            try {
                databaseHelper.deleteExpense(expenseId);
                callback.onComplete(true);
            } catch (Exception e) {
                callback.onComplete(false);
            }
        });
    }
}