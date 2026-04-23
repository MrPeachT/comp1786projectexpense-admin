package com.example.projectexpensetrackeradmin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.example.projectexpensetrackeradmin.sync.SyncManager;
import com.example.projectexpensetrackeradmin.R;
import com.example.projectexpensetrackeradmin.adapter.ExpenseAdapter;
import com.example.projectexpensetrackeradmin.model.Expense;
import com.example.projectexpensetrackeradmin.model.Project;
import com.example.projectexpensetrackeradmin.repository.ExpenseRepository;
import com.example.projectexpensetrackeradmin.repository.ProjectRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseActionListener {

    private int projectId;
    private double currentProjectBudget = 0.0;

    private TextView tvExpenseTitle, tvEmptyExpenses;
    private TextView tvProjectBudget, tvTotalExpenses, tvRemainingBudget, tvBudgetStatus;
    private RecyclerView recyclerExpenses;
    private FloatingActionButton fabAddExpense;
    private View progressBar;

    private final List<Expense> expenseList = new ArrayList<>();
    private ExpenseAdapter adapter;

    private ExpenseRepository expenseRepository;
    private ProjectRepository projectRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        projectId = getIntent().getIntExtra("project_id", -1);

        if (projectId == -1) {
            Toast.makeText(this, "Invalid project ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        expenseRepository = new ExpenseRepository(this);
        projectRepository = new ProjectRepository(this);

        initViews();
        setupRecyclerView();
        loadProjectInfo();
        loadExpenses();

        fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditExpenseActivity.class);
            intent.putExtra("project_id", projectId);
            startActivity(intent);
        });
    }

    private void initViews() {
        tvExpenseTitle = findViewById(R.id.tvExpenseTitle);
        tvEmptyExpenses = findViewById(R.id.tvEmptyExpenses);

        tvProjectBudget = findViewById(R.id.tvProjectBudget);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus);

        recyclerExpenses = findViewById(R.id.recyclerExpenses);
        fabAddExpense = findViewById(R.id.fabAddExpense);
        progressBar = findViewById(R.id.progressBarExpenses);
    }

    private void setupRecyclerView() {
        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseAdapter(this, expenseList, this);
        recyclerExpenses.setAdapter(adapter);
    }

    private void loadProjectInfo() {
        projectRepository.getAllProjects(result -> runOnUiThread(() -> {
            for (Project project : result) {
                if (project.getId() == projectId) {
                    tvExpenseTitle.setText("Expenses - " + project.getProjectName());
                    currentProjectBudget = project.getProjectBudget();
                    updateBudgetSummary();
                    return;
                }
            }
        }));
    }

    private void loadExpenses() {
        showLoading(true);

        expenseRepository.getExpensesByProjectId(projectId, result -> runOnUiThread(() -> {
            showLoading(false);

            expenseList.clear();
            expenseList.addAll(result);
            adapter.notifyDataSetChanged();

            updateEmptyState();
            updateBudgetSummary();
        }));
    }

    private void updateBudgetSummary() {
        double totalExpenses = 0.0;

        for (Expense expense : expenseList) {
            totalExpenses += expense.getAmount();
        }

        double remainingBudget = currentProjectBudget - totalExpenses;

        String budgetStatus;
        if (remainingBudget < 0) {
            budgetStatus = "Over Budget";
        } else if (currentProjectBudget > 0 && totalExpenses >= currentProjectBudget * 0.8) {
            budgetStatus = "Near Budget Limit";
        } else {
            budgetStatus = "Within Budget";
        }

        tvProjectBudget.setText(
                "Project Budget: " + String.format(Locale.getDefault(), "%,.0f VND", currentProjectBudget)
        );

        tvTotalExpenses.setText(
                "Total Expenses: " + String.format(Locale.getDefault(), "%,.0f VND", totalExpenses)
        );

        tvRemainingBudget.setText(
                "Remaining Budget: " + String.format(Locale.getDefault(), "%,.0f VND", remainingBudget)
        );

        tvBudgetStatus.setText("Status: " + budgetStatus);
    }

    private void updateEmptyState() {
        if (expenseList.isEmpty()) {
            tvEmptyExpenses.setVisibility(View.VISIBLE);
            recyclerExpenses.setVisibility(View.GONE);
        } else {
            tvEmptyExpenses.setVisibility(View.GONE);
            recyclerExpenses.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerExpenses.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjectInfo();
        loadExpenses();
    }

    @Override
    public void onEditExpense(Expense expense) {
        Intent intent = new Intent(this, AddEditExpenseActivity.class);
        intent.putExtra("expense_id", expense.getId());
        intent.putExtra("project_id", expense.getProjectId());
        startActivity(intent);
    }

    @Override
    public void onDeleteExpense(Expense expense, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure? This will delete the expense locally. If internet is available, it will also be deleted from Firestore.")
                .setPositiveButton("Yes", (dialog, which) -> {

                    expenseRepository.deleteExpense(expense.getId(), success -> runOnUiThread(() -> {
                        if (!success) {
                            Toast.makeText(
                                    ExpenseActivity.this,
                                    "Failed to delete expense locally.",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        if (position >= 0 && position < expenseList.size()) {
                            expenseList.remove(position);
                            adapter.notifyItemRemoved(position);
                        }

                        updateEmptyState();
                        updateBudgetSummary();

                        if (!isNetworkAvailable()) {
                            Toast.makeText(
                                    ExpenseActivity.this,
                                    "Expense deleted locally. No internet connection, so Firestore was not updated.",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        SyncManager syncManager = new SyncManager(ExpenseActivity.this);
                        syncManager.deleteExpenseFromCloud(projectId, expense.getExpenseId(), new SyncManager.DeleteCallback() {
                            @Override
                            public void onSuccess(String message) {
                                runOnUiThread(() ->
                                        Toast.makeText(
                                                ExpenseActivity.this,
                                                "Expense deleted locally and from Firestore.",
                                                Toast.LENGTH_LONG
                                        ).show()
                                );
                            }

                            @Override
                            public void onFailure(String message) {
                                runOnUiThread(() ->
                                        Toast.makeText(
                                                ExpenseActivity.this,
                                                "Expense deleted locally, but Firestore deletion failed.",
                                                Toast.LENGTH_LONG
                                        ).show()
                                );
                            }
                        });
                    }));
                })
                .setNegativeButton("No", null)
                .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities =
                connectivityManager.getNetworkCapabilities(network);

        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }
}