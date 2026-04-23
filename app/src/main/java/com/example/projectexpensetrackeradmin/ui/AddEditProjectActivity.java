package com.example.projectexpensetrackeradmin.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectexpensetrackeradmin.R;
import com.example.projectexpensetrackeradmin.database.DatabaseHelper;
import com.example.projectexpensetrackeradmin.model.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

public class AddEditProjectActivity extends AppCompatActivity {

    private EditText etProjectCode, etProjectName, etProjectDescription;
    private EditText etStartDate, etEndDate, etProjectManager, etProjectBudget;
    private EditText etSpecialRequirements, etClientInfo;
    private Spinner spinnerProjectStatus;
    private Button btnSaveProject, btnChooseProjectImage, btnRemoveProjectImage;
    private ImageView imgProjectFormPreview;

    private DatabaseHelper databaseHelper;
    private int projectId = -1;
    private boolean isEditMode = false;

    private String selectedImagePath = "";

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String savedPath = copyImageToInternalStorage(uri);
                    if (savedPath != null && !savedPath.isEmpty()) {
                        selectedImagePath = savedPath;
                        showImagePreview(selectedImagePath);
                    } else {
                        Toast.makeText(this, "Failed to save selected image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_project);

        initViews();
        databaseHelper = new DatabaseHelper(this);

        setupStatusSpinner();
        setupDatePickers();
        setupImageButtons();

        projectId = getIntent().getIntExtra("project_id", -1);
        if (projectId != -1) {
            isEditMode = true;
            loadProjectData();
        }

        btnSaveProject.setOnClickListener(v -> validateAndConfirmProject());
    }

    private void initViews() {
        etProjectCode = findViewById(R.id.etProjectCode);
        etProjectName = findViewById(R.id.etProjectName);
        etProjectDescription = findViewById(R.id.etProjectDescription);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etProjectManager = findViewById(R.id.etProjectManager);
        etProjectBudget = findViewById(R.id.etProjectBudget);
        etSpecialRequirements = findViewById(R.id.etSpecialRequirements);
        etClientInfo = findViewById(R.id.etClientInfo);
        spinnerProjectStatus = findViewById(R.id.spinnerProjectStatus);
        btnSaveProject = findViewById(R.id.btnSaveProject);
        btnChooseProjectImage = findViewById(R.id.btnChooseProjectImage);
        btnRemoveProjectImage = findViewById(R.id.btnRemoveProjectImage);
        imgProjectFormPreview = findViewById(R.id.imgProjectFormPreview);
    }

    private void setupStatusSpinner() {
        String[] statusOptions = {"Active", "Completed", "On Hold"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProjectStatus.setAdapter(adapter);
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));
    }

    private void setupImageButtons() {
        btnChooseProjectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnRemoveProjectImage.setOnClickListener(v -> {
            selectedImagePath = "";
            imgProjectFormPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        });
    }

    private void showDatePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                    );
                    targetEditText.setText(date);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void validateAndConfirmProject() {
        String projectCode = etProjectCode.getText().toString().trim();
        String projectName = etProjectName.getText().toString().trim();
        String projectDescription = etProjectDescription.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String projectManager = etProjectManager.getText().toString().trim();
        String projectStatus = spinnerProjectStatus.getSelectedItem().toString();
        String budgetText = etProjectBudget.getText().toString().trim();
        String specialRequirements = etSpecialRequirements.getText().toString().trim();
        String clientInfo = etClientInfo.getText().toString().trim();

        if (projectCode.isEmpty()) {
            etProjectCode.setError("Project code is required");
            etProjectCode.requestFocus();
            return;
        }

        if (projectName.isEmpty()) {
            etProjectName.setError("Project name is required");
            etProjectName.requestFocus();
            return;
        }

        if (projectDescription.isEmpty()) {
            etProjectDescription.setError("Project description is required");
            etProjectDescription.requestFocus();
            return;
        }

        if (startDate.isEmpty()) {
            etStartDate.setError("Start date is required");
            etStartDate.requestFocus();
            return;
        }

        if (endDate.isEmpty()) {
            etEndDate.setError("End date is required");
            etEndDate.requestFocus();
            return;
        }

        if (projectManager.isEmpty()) {
            etProjectManager.setError("Project manager is required");
            etProjectManager.requestFocus();
            return;
        }

        if (budgetText.isEmpty()) {
            etProjectBudget.setError("Project budget is required");
            etProjectBudget.requestFocus();
            return;
        }

        if (!isEditMode && databaseHelper.isProjectCodeExists(projectCode)) {
            etProjectCode.setError("Project code already exists");
            etProjectCode.requestFocus();
            return;
        }

        double projectBudget;
        try {
            projectBudget = Double.parseDouble(budgetText);
        } catch (NumberFormatException e) {
            etProjectBudget.setError("Invalid budget value");
            etProjectBudget.requestFocus();
            return;
        }

        if (projectBudget <= 0) {
            etProjectBudget.setError("Budget must be greater than 0");
            etProjectBudget.requestFocus();
            return;
        }

        if (endDate.compareTo(startDate) < 0) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            return;
        }

        showConfirmationDialog(
                projectCode,
                projectName,
                projectDescription,
                startDate,
                endDate,
                projectManager,
                projectStatus,
                projectBudget,
                specialRequirements,
                clientInfo
        );
    }

    private void showConfirmationDialog(
            String projectCode,
            String projectName,
            String projectDescription,
            String startDate,
            String endDate,
            String projectManager,
            String projectStatus,
            double projectBudget,
            String specialRequirements,
            String clientInfo
    ) {
        String message =
                "Please review the project details:\n\n" +
                        "Code: " + projectCode + "\n" +
                        "Name: " + projectName + "\n" +
                        "Description: " + projectDescription + "\n" +
                        "Start Date: " + startDate + "\n" +
                        "End Date: " + endDate + "\n" +
                        "Manager: " + projectManager + "\n" +
                        "Status: " + projectStatus + "\n" +
                        "Budget: " + String.format(Locale.getDefault(), "%.0f", projectBudget) + "\n" +
                        "Special Requirements: " + (specialRequirements.isEmpty() ? "N/A" : specialRequirements) + "\n" +
                        "Client Info: " + (clientInfo.isEmpty() ? "N/A" : clientInfo) + "\n" +
                        "Image: " + (selectedImagePath.isEmpty() ? "No image selected" : "Image selected");

        new AlertDialog.Builder(this)
                .setTitle(isEditMode ? "Confirm Project Update" : "Confirm Project Details")
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> saveProject(
                        projectCode,
                        projectName,
                        projectDescription,
                        startDate,
                        endDate,
                        projectManager,
                        projectStatus,
                        projectBudget,
                        specialRequirements,
                        clientInfo
                ))
                .setNegativeButton("Edit Again", null)
                .show();
    }

    private void saveProject(
            String projectCode,
            String projectName,
            String projectDescription,
            String startDate,
            String endDate,
            String projectManager,
            String projectStatus,
            double projectBudget,
            String specialRequirements,
            String clientInfo
    ) {
        Project project = new Project();
        project.setProjectCode(projectCode);
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setProjectManager(projectManager);
        project.setProjectStatus(projectStatus);
        project.setProjectBudget(projectBudget);
        project.setSpecialRequirements(specialRequirements);
        project.setClientInfo(clientInfo);
        project.setExactLocation("");
        project.setImagePath(selectedImagePath);
        project.setLastModified(System.currentTimeMillis());
        project.setSynced(0);

        int result;

        if (isEditMode) {
            project.setId(projectId);
            result = databaseHelper.updateProject(project);
        } else {
            result = (int) databaseHelper.insertProject(project);
        }

        if (result > 0) {
            Toast.makeText(
                    this,
                    isEditMode ? "Project updated successfully" : "Project saved successfully",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save project", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProjectData() {
        Project project = databaseHelper.getProjectById(projectId);

        if (project != null) {
            etProjectCode.setText(project.getProjectCode());
            etProjectName.setText(project.getProjectName());
            etProjectDescription.setText(project.getProjectDescription());
            etStartDate.setText(project.getStartDate());
            etEndDate.setText(project.getEndDate());
            etProjectManager.setText(project.getProjectManager());
            etProjectBudget.setText(String.format(Locale.getDefault(), "%.0f", project.getProjectBudget()));
            etSpecialRequirements.setText(project.getSpecialRequirements());
            etClientInfo.setText(project.getClientInfo());

            selectedImagePath = project.getImagePath();
            showImagePreview(selectedImagePath);

            setSpinnerSelection(spinnerProjectStatus, project.getProjectStatus());

            etProjectCode.setEnabled(false);
            btnSaveProject.setText("Update Project");
        }
    }

    private void showImagePreview(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            imgProjectFormPreview.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            imgProjectFormPreview.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        if (bitmap != null) {
            imgProjectFormPreview.setImageBitmap(bitmap);
        } else {
            imgProjectFormPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private String copyImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return "";
            }

            File imagesDir = new File(getFilesDir(), "project_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            String fileName = "project_" + System.currentTimeMillis() + ".jpg";
            File outputFile = new File(imagesDir, fileName);

            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null && item.toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}