package com.example.projectexpensetrackeradmin.ui;

import android.app.DatePickerDialog;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectexpensetrackeradmin.R;
import com.example.projectexpensetrackeradmin.database.DatabaseHelper;
import com.example.projectexpensetrackeradmin.model.Expense;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

/**
 * AddEditExpenseActivity
 * This activity allows the user to add a new expense
 * or edit an existing expense record.
 */
public class AddEditExpenseActivity extends AppCompatActivity {

    private EditText etExpenseId, etExpenseDate, etAmount, etCurrency,
            etClaimant, etDescription;
    private Spinner spinnerExpenseType, spinnerPaymentMethod,
            spinnerPaymentStatus;
    private Button btnSaveExpense, btnChooseExpenseImage, btnRemoveExpenseImage;
    private ImageView imgExpenseFormPreview;

    private DatabaseHelper databaseHelper;
    private int projectId = -1;
    private int expenseDbId = -1;
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
        setContentView(R.layout.activity_add_edit_expense);

        projectId = getIntent().getIntExtra("project_id", -1);
        expenseDbId = getIntent().getIntExtra("expense_id", -1);

        if (projectId == -1) {
            Toast.makeText(this, "Invalid project ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (expenseDbId != -1) {
            isEditMode = true;
        }

        databaseHelper = new DatabaseHelper(this);

        initViews();
        setupSpinners();
        setupDatePicker();
        setupImageButtons();

        if (isEditMode) {
            loadExpenseData();
        }

        btnSaveExpense.setOnClickListener(v -> saveExpense());
    }

    private void initViews() {
        etExpenseId = findViewById(R.id.etExpenseId);
        etExpenseDate = findViewById(R.id.etExpenseDate);
        etAmount = findViewById(R.id.etAmount);
        etCurrency = findViewById(R.id.etCurrency);
        etClaimant = findViewById(R.id.etClaimant);
        etDescription = findViewById(R.id.etDescription);

        spinnerExpenseType = findViewById(R.id.spinnerExpenseType);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        spinnerPaymentStatus = findViewById(R.id.spinnerPaymentStatus);

        btnSaveExpense = findViewById(R.id.btnSaveExpense);
        btnChooseExpenseImage = findViewById(R.id.btnChooseExpenseImage);
        btnRemoveExpenseImage = findViewById(R.id.btnRemoveExpenseImage);
        imgExpenseFormPreview = findViewById(R.id.imgExpenseFormPreview);
    }

    private void setupSpinners() {
        String[] expenseTypes = {
                "Travel", "Equipment", "Materials", "Services",
                "Software/Licenses", "Labour costs", "Utilities", "Miscellaneous"
        };

        String[] paymentMethods = {
                "Cash", "Credit Card", "Bank Transfer", "Cheque"
        };

        String[] paymentStatuses = {
                "Paid", "Pending", "Reimbursed"
        };

        ArrayAdapter<String> expenseTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, expenseTypes);
        expenseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpenseType.setAdapter(expenseTypeAdapter);

        ArrayAdapter<String> paymentMethodAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, paymentMethods);
        paymentMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(paymentMethodAdapter);

        ArrayAdapter<String> paymentStatusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, paymentStatuses);
        paymentStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentStatus.setAdapter(paymentStatusAdapter);
    }

    private void setupDatePicker() {
        etExpenseDate.setOnClickListener(v -> showDatePicker());
    }

    private void setupImageButtons() {
        btnChooseExpenseImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnRemoveExpenseImage.setOnClickListener(v -> {
            selectedImagePath = "";
            imgExpenseFormPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        });
    }

    private void showDatePicker() {
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
                    etExpenseDate.setText(date);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void saveExpense() {
        String expenseId = etExpenseId.getText().toString().trim();
        String date = etExpenseDate.getText().toString().trim();
        String amountText = etAmount.getText().toString().trim();
        String currency = etCurrency.getText().toString().trim();
        String expenseType = spinnerExpenseType.getSelectedItem().toString();
        String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();
        String claimant = etClaimant.getText().toString().trim();
        String paymentStatus = spinnerPaymentStatus.getSelectedItem().toString();
        String description = etDescription.getText().toString().trim();

        if (expenseId.isEmpty()) {
            etExpenseId.setError("Expense ID is required");
            etExpenseId.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            etExpenseDate.setError("Expense date is required");
            etExpenseDate.requestFocus();
            return;
        }

        if (amountText.isEmpty()) {
            etAmount.setError("Amount is required");
            etAmount.requestFocus();
            return;
        }

        if (currency.isEmpty()) {
            etCurrency.setError("Currency is required");
            etCurrency.requestFocus();
            return;
        }

        if (claimant.isEmpty()) {
            etClaimant.setError("Claimant is required");
            etClaimant.requestFocus();
            return;
        }

        if (!isEditMode && databaseHelper.isExpenseIdExists(expenseId, projectId)) {
            etExpenseId.setError("Expense ID already exists in this project");
            etExpenseId.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount");
            etAmount.requestFocus();
            return;
        }

        if (amount <= 0) {
            etAmount.setError("Amount must be greater than 0");
            etAmount.requestFocus();
            return;
        }

        Expense expense = new Expense();
        expense.setExpenseId(expenseId);
        expense.setProjectId(projectId);
        expense.setDateOfExpense(date);
        expense.setAmount(amount);
        expense.setCurrency(currency);
        expense.setExpenseType(expenseType);
        expense.setPaymentMethod(paymentMethod);
        expense.setClaimant(claimant);
        expense.setPaymentStatus(paymentStatus);
        expense.setDescription(description);
        expense.setLocation("");
        expense.setImagePath(selectedImagePath);
        expense.setLastModified(System.currentTimeMillis());
        expense.setSynced(0);

        int result;

        if (isEditMode) {
            expense.setId(expenseDbId);
            result = databaseHelper.updateExpense(expense);
        } else {
            result = (int) databaseHelper.insertExpense(expense);
        }

        if (result > 0) {
            Toast.makeText(
                    this,
                    isEditMode ? "Expense updated successfully" : "Expense saved successfully",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExpenseData() {
        Expense expense = databaseHelper.getExpenseById(expenseDbId);

        if (expense != null) {
            etExpenseId.setText(expense.getExpenseId());
            etExpenseDate.setText(expense.getDateOfExpense());
            etAmount.setText(String.valueOf(expense.getAmount()));
            etCurrency.setText(expense.getCurrency());
            etClaimant.setText(expense.getClaimant());
            etDescription.setText(expense.getDescription());

            selectedImagePath = expense.getImagePath();
            showImagePreview(selectedImagePath);

            setSpinnerSelection(spinnerExpenseType, expense.getExpenseType());
            setSpinnerSelection(spinnerPaymentMethod, expense.getPaymentMethod());
            setSpinnerSelection(spinnerPaymentStatus, expense.getPaymentStatus());

            btnSaveExpense.setText("Update Expense");
        }
    }

    private void showImagePreview(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            imgExpenseFormPreview.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            imgExpenseFormPreview.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        if (bitmap != null) {
            imgExpenseFormPreview.setImageBitmap(bitmap);
        } else {
            imgExpenseFormPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private String copyImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return "";
            }

            File imagesDir = new File(getFilesDir(), "expense_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            String fileName = "expense_" + System.currentTimeMillis() + ".jpg";
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