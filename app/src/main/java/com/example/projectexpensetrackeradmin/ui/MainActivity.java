package com.example.projectexpensetrackeradmin.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectexpensetrackeradmin.R;
import com.example.projectexpensetrackeradmin.adapter.ProjectAdapter;
import com.example.projectexpensetrackeradmin.model.Project;
import com.example.projectexpensetrackeradmin.repository.ProjectRepository;
import com.example.projectexpensetrackeradmin.sync.SyncManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProjectAdapter.OnProjectActionListener {

    private RecyclerView recyclerProjects;
    private EditText etSearch;
    private Button btnSearch, btnReset, btnSync, btnAdvancedSearch;
    private FloatingActionButton fabAddProject;
    private TextView tvEmptyProjects;
    private View progressBar;

    private ProjectRepository projectRepository;
    private ProjectAdapter projectAdapter;
    private final List<Project> projectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        projectRepository = new ProjectRepository(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadAllProjects();
    }

    private void initViews() {
        recyclerProjects = findViewById(R.id.recyclerProjects);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnReset = findViewById(R.id.btnReset);
        btnSync = findViewById(R.id.btnSync);
        btnAdvancedSearch = findViewById(R.id.btnAdvancedSearch);
        fabAddProject = findViewById(R.id.fabAddProject);
        tvEmptyProjects = findViewById(R.id.tvEmptyProjects);
        progressBar = findViewById(R.id.progressBarProjects);
    }

    private void setupRecyclerView() {
        recyclerProjects.setLayoutManager(new LinearLayoutManager(this));
        projectAdapter = new ProjectAdapter(this, projectList, this);
        recyclerProjects.setAdapter(projectAdapter);
    }

    private void setupListeners() {
        fabAddProject.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditProjectActivity.class))
        );

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                loadAllProjects();
            } else {
                searchProjects(keyword);
            }
        });

        btnAdvancedSearch.setOnClickListener(v -> showAdvancedSearchDialog());

        btnReset.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Reset Database")
                    .setMessage("Are you sure? This will delete all data.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        showLoading(true);
                        projectRepository.resetDatabase(success ->
                                runOnUiThread(() -> {
                                    showLoading(false);
                                    if (success) {
                                        loadAllProjects();
                                        Toast.makeText(MainActivity.this, "Database reset successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to reset database", Toast.LENGTH_SHORT).show();
                                    }
                                }));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnSync.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                Toast.makeText(MainActivity.this, "No internet connection. Please connect and try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSync.setEnabled(false);
            Toast.makeText(MainActivity.this, "Sync started...", Toast.LENGTH_SHORT).show();

            SyncManager syncManager = new SyncManager(MainActivity.this);
            syncManager.syncAllData(new SyncManager.SyncCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        btnSync.setEnabled(true);
                        loadAllProjects();
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onFailure(String message) {
                    runOnUiThread(() -> {
                        btnSync.setEnabled(true);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllProjects();
    }

    private void loadAllProjects() {
        showLoading(true);
        projectRepository.getAllProjects(result -> runOnUiThread(() -> {
            showLoading(false);
            projectList.clear();
            projectList.addAll(result);
            projectAdapter.notifyDataSetChanged();
            updateEmptyState();
        }));
    }

    private void searchProjects(String keyword) {
        showLoading(true);
        projectRepository.searchProjects(keyword, result -> runOnUiThread(() -> {
            showLoading(false);
            projectList.clear();
            projectList.addAll(result);
            projectAdapter.notifyDataSetChanged();
            updateEmptyState();
        }));
    }

    private void advancedSearchProjects(String keyword, String status, String manager, String startDate) {
        showLoading(true);
        projectRepository.advancedSearchProjects(keyword, status, manager, startDate, result -> runOnUiThread(() -> {
            showLoading(false);
            projectList.clear();
            projectList.addAll(result);
            projectAdapter.notifyDataSetChanged();
            updateEmptyState();
        }));
    }

    private void showAdvancedSearchDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_advanced_search, null);

        EditText etKeyword = dialogView.findViewById(R.id.etAdvancedKeyword);
        EditText etManager = dialogView.findViewById(R.id.etAdvancedManager);
        EditText etStartDate = dialogView.findViewById(R.id.etAdvancedStartDate);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerAdvancedStatus);

        String[] statusOptions = {"Any", "Active", "Completed", "On Hold"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        etKeyword.setText(etSearch.getText().toString().trim());

        new AlertDialog.Builder(this)
                .setTitle("Advanced Search")
                .setView(dialogView)
                .setPositiveButton("Search", (dialog, which) -> {
                    String keyword = etKeyword.getText().toString().trim();
                    String manager = etManager.getText().toString().trim();
                    String startDate = etStartDate.getText().toString().trim();
                    String status = spinnerStatus.getSelectedItem().toString();

                    if ("Any".equals(status)) {
                        status = "";
                    }

                    advancedSearchProjects(keyword, status, manager, startDate);
                })
                .setNeutralButton("Clear Filters", (dialog, which) -> {
                    etSearch.setText("");
                    loadAllProjects();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateEmptyState() {
        if (projectList.isEmpty()) {
            tvEmptyProjects.setVisibility(View.VISIBLE);
            recyclerProjects.setVisibility(View.GONE);
        } else {
            tvEmptyProjects.setVisibility(View.GONE);
            recyclerProjects.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerProjects.setVisibility(isLoading ? View.GONE : View.VISIBLE);
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

    @Override
    public void onEditProject(Project project) {
        Intent intent = new Intent(MainActivity.this, AddEditProjectActivity.class);
        intent.putExtra("project_id", project.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteProject(Project project, int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete Project")
                .setMessage("Are you sure? This will delete the project locally. If internet is available, it will also be deleted from Firestore.")
                .setPositiveButton("Yes", (dialog, which) -> {

                    projectRepository.deleteProject(project.getId(), success -> runOnUiThread(() -> {
                        if (!success) {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Failed to delete project locally.",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        if (position >= 0 && position < projectList.size()) {
                            projectList.remove(position);
                            projectAdapter.notifyItemRemoved(position);
                        }
                        updateEmptyState();

                        if (!isNetworkAvailable()) {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Project deleted locally. No internet connection, so Firestore was not updated.",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        SyncManager syncManager = new SyncManager(MainActivity.this);
                        syncManager.deleteProjectFromCloud(project.getProjectCode(), new SyncManager.DeleteCallback() {
                            @Override
                            public void onSuccess(String message) {
                                runOnUiThread(() ->
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Project deleted locally and from Firestore.",
                                                Toast.LENGTH_LONG
                                        ).show()
                                );
                            }

                            @Override
                            public void onFailure(String message) {
                                runOnUiThread(() ->
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Project deleted locally, but Firestore deletion failed.",
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

    @Override
    public void onOpenProject(Project project) {
        Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
        intent.putExtra("project_id", project.getId());
        startActivity(intent);
    }
}