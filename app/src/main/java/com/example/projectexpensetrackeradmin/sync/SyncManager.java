package com.example.projectexpensetrackeradmin.sync;

import android.content.Context;
import android.util.Log;

import com.example.projectexpensetrackeradmin.database.DatabaseHelper;
import com.example.projectexpensetrackeradmin.model.Expense;
import com.example.projectexpensetrackeradmin.model.Project;
import com.example.projectexpensetrackeradmin.repository.ExpenseRepository;
import com.example.projectexpensetrackeradmin.repository.ProjectRepository;
import com.example.projectexpensetrackeradmin.util.RepositoryCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncManager {

    public interface SyncCallback {
        void onSuccess(String message);
        void onFailure(String message);
    }

    public interface DeleteCallback {
        void onSuccess(String message);
        void onFailure(String message);
    }

    private final ProjectRepository projectRepository;
    private final ExpenseRepository expenseRepository;
    private final FirebaseFirestore firestore;
    private final DatabaseHelper databaseHelper;

    public SyncManager(Context context) {
        projectRepository = new ProjectRepository(context);
        expenseRepository = new ExpenseRepository(context);
        firestore = FirebaseFirestore.getInstance();
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public void syncAllData(SyncCallback callback) {
        syncProjectsToCloud(callback);
    }

    private void syncProjectsToCloud(SyncCallback callback) {
        projectRepository.getAllProjects(new RepositoryCallback<List<Project>>() {
            @Override
            public void onComplete(List<Project> projects) {
                if (projects == null || projects.isEmpty()) {
                    downloadAllDataFromCloud(callback);
                    return;
                }

                final int totalProjects = projects.size();
                final int[] completedProjects = {0};
                final boolean[] hasFailed = {false};

                for (Project p : projects) {
                    String firestoreProjectId = p.getProjectCode();

                    Map<String, Object> projectMap = new HashMap<>();
                    projectMap.put("id", p.getId());
                    projectMap.put("projectCode", p.getProjectCode());
                    projectMap.put("projectName", p.getProjectName());
                    projectMap.put("projectDescription", p.getProjectDescription());
                    projectMap.put("startDate", p.getStartDate());
                    projectMap.put("endDate", p.getEndDate());
                    projectMap.put("projectManager", p.getProjectManager());
                    projectMap.put("projectStatus", p.getProjectStatus());
                    projectMap.put("projectBudget", p.getProjectBudget());
                    projectMap.put("specialRequirements", p.getSpecialRequirements());
                    projectMap.put("clientInfo", p.getClientInfo());
                    projectMap.put("exactLocation", p.getExactLocation());
                    projectMap.put("imagePath", p.getImagePath());
                    projectMap.put("lastModified", p.getLastModified());
                    projectMap.put("synced", 1);

                    firestore.collection("projects")
                            .document(firestoreProjectId)
                            .set(projectMap)
                            .addOnSuccessListener(unused -> {
                                databaseHelper.markProjectAsSynced(p.getId());
                                Log.d("SYNC", "Project synced: " + p.getProjectName());

                                syncExpensesToCloud(p.getId(), firestoreProjectId, new StepCallback() {
                                    @Override
                                    public void onSuccess() {
                                        completedProjects[0]++;
                                        if (!hasFailed[0] && completedProjects[0] == totalProjects) {
                                            downloadAllDataFromCloud(callback);
                                        }
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        if (!hasFailed[0]) {
                                            hasFailed[0] = true;
                                            callback.onFailure(message);
                                        }
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                if (!hasFailed[0]) {
                                    hasFailed[0] = true;
                                    callback.onFailure("Failed to sync project: " + p.getProjectName());
                                }
                                Log.e("SYNC", "Project sync failed", e);
                            });
                }
            }
        });
    }

    private void syncExpensesToCloud(int projectDbId, String firestoreProjectId, StepCallback callback) {
        expenseRepository.getExpensesByProjectId(projectDbId, new RepositoryCallback<List<Expense>>() {
            @Override
            public void onComplete(List<Expense> expenses) {
                if (expenses == null || expenses.isEmpty()) {
                    callback.onSuccess();
                    return;
                }

                final int totalExpenses = expenses.size();
                final int[] completedExpenses = {0};
                final boolean[] hasFailed = {false};

                for (Expense e : expenses) {
                    Map<String, Object> expenseMap = new HashMap<>();
                    expenseMap.put("id", e.getId());
                    expenseMap.put("expenseId", e.getExpenseId());
                    expenseMap.put("projectId", e.getProjectId());
                    expenseMap.put("dateOfExpense", e.getDateOfExpense());
                    expenseMap.put("amount", e.getAmount());
                    expenseMap.put("currency", e.getCurrency());
                    expenseMap.put("expenseType", e.getExpenseType());
                    expenseMap.put("paymentMethod", e.getPaymentMethod());
                    expenseMap.put("claimant", e.getClaimant());
                    expenseMap.put("paymentStatus", e.getPaymentStatus());
                    expenseMap.put("description", e.getDescription());
                    expenseMap.put("location", e.getLocation());
                    expenseMap.put("imagePath", e.getImagePath());
                    expenseMap.put("lastModified", e.getLastModified());
                    expenseMap.put("synced", 1);

                    firestore.collection("projects")
                            .document(firestoreProjectId)
                            .collection("expenses")
                            .document(e.getExpenseId())
                            .set(expenseMap)
                            .addOnSuccessListener(unused -> {
                                databaseHelper.markExpenseAsSynced(e.getId());
                                Log.d("SYNC", "Expense synced: " + e.getExpenseId());

                                completedExpenses[0]++;
                                if (!hasFailed[0] && completedExpenses[0] == totalExpenses) {
                                    callback.onSuccess();
                                }
                            })
                            .addOnFailureListener(err -> {
                                if (!hasFailed[0]) {
                                    hasFailed[0] = true;
                                    callback.onFailure("Failed to sync expense: " + e.getExpenseId());
                                }
                                Log.e("SYNC", "Expense sync failed", err);
                            });
                }
            }
        });
    }

    private void downloadAllDataFromCloud(SyncCallback callback) {
        firestore.collection("projects")
                .get()
                .addOnSuccessListener(projectDocuments -> {
                    if (projectDocuments.isEmpty()) {
                        callback.onSuccess("Sync completed. No cloud data found.");
                        return;
                    }

                    final int totalProjects = projectDocuments.size();
                    final int[] completedProjects = {0};
                    final boolean[] hasFailed = {false};

                    for (QueryDocumentSnapshot document : projectDocuments) {
                        Project project = new Project();

                        String projectCode = getStringValue(document, "projectCode");
                        if (projectCode.isEmpty()) {
                            projectCode = document.getId();
                        }

                        project.setProjectCode(projectCode);
                        project.setProjectName(getStringValue(document, "projectName"));
                        project.setProjectDescription(getStringValue(document, "projectDescription"));
                        project.setStartDate(getStringValue(document, "startDate"));
                        project.setEndDate(getStringValue(document, "endDate"));
                        project.setProjectManager(getStringValue(document, "projectManager"));
                        project.setProjectStatus(getStringValue(document, "projectStatus"));
                        project.setProjectBudget(getDoubleValue(document, "projectBudget"));
                        project.setSpecialRequirements(getStringValue(document, "specialRequirements"));
                        project.setClientInfo(getStringValue(document, "clientInfo"));
                        project.setExactLocation(getStringValue(document, "exactLocation"));
                        project.setImagePath(getStringValue(document, "imagePath"));
                        project.setLastModified(getLongValue(document, "lastModified"));
                        project.setSynced(1);

                        long localProjectIdLong = databaseHelper.upsertProjectFromCloud(project);
                        int localProjectId = (int) localProjectIdLong;

                        document.getReference()
                                .collection("expenses")
                                .get()
                                .addOnSuccessListener(expenseDocuments -> {
                                    for (QueryDocumentSnapshot expenseDocument : expenseDocuments) {
                                        Expense expense = new Expense();
                                        expense.setExpenseId(getStringValue(expenseDocument, "expenseId"));
                                        expense.setProjectId(localProjectId);
                                        expense.setDateOfExpense(getStringValue(expenseDocument, "dateOfExpense"));
                                        expense.setAmount(getDoubleValue(expenseDocument, "amount"));
                                        expense.setCurrency(getStringValue(expenseDocument, "currency"));
                                        expense.setExpenseType(getStringValue(expenseDocument, "expenseType"));
                                        expense.setPaymentMethod(getStringValue(expenseDocument, "paymentMethod"));
                                        expense.setClaimant(getStringValue(expenseDocument, "claimant"));
                                        expense.setPaymentStatus(getStringValue(expenseDocument, "paymentStatus"));
                                        expense.setDescription(getStringValue(expenseDocument, "description"));
                                        expense.setLocation(getStringValue(expenseDocument, "location"));
                                        expense.setImagePath(getStringValue(expenseDocument, "imagePath"));
                                        expense.setLastModified(getLongValue(expenseDocument, "lastModified"));
                                        expense.setSynced(1);

                                        databaseHelper.upsertExpenseFromCloud(expense);
                                    }

                                    completedProjects[0]++;
                                    if (!hasFailed[0] && completedProjects[0] == totalProjects) {
                                        callback.onSuccess("Sync completed successfully.");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (!hasFailed[0]) {
                                        hasFailed[0] = true;
                                        callback.onFailure("Failed to download expenses from Firestore.");
                                    }
                                    Log.e("SYNC", "Expense download failed", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Failed to download projects from Firestore.");
                    Log.e("SYNC", "Project download failed", e);
                });
    }

    public void deleteProjectFromCloud(String projectCode, DeleteCallback callback) {
        firestore.collection("projects")
                .document(projectCode)
                .collection("expenses")
                .get()
                .addOnSuccessListener(expenseDocuments -> {
                    if (expenseDocuments.isEmpty()) {
                        deleteProjectDocument(projectCode, callback);
                        return;
                    }

                    final int totalExpenses = expenseDocuments.size();
                    final int[] completedExpenses = {0};
                    final boolean[] hasFailed = {false};

                    for (QueryDocumentSnapshot expenseDoc : expenseDocuments) {
                        expenseDoc.getReference()
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    completedExpenses[0]++;
                                    if (!hasFailed[0] && completedExpenses[0] == totalExpenses) {
                                        deleteProjectDocument(projectCode, callback);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (!hasFailed[0]) {
                                        hasFailed[0] = true;
                                        callback.onFailure("Failed to delete project expenses from Firestore.");
                                    }
                                    Log.e("SYNC_DELETE", "Expense subcollection delete failed", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Failed to load project expenses from Firestore.");
                    Log.e("SYNC_DELETE", "Load expense subcollection failed", e);
                });
    }

    private void deleteProjectDocument(String projectCode, DeleteCallback callback) {
        firestore.collection("projects")
                .document(projectCode)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess("Project deleted from Firestore successfully."))
                .addOnFailureListener(e -> {
                    callback.onFailure("Failed to delete project from Firestore.");
                    Log.e("SYNC_DELETE", "Project delete failed", e);
                });
    }

    public void deleteExpenseFromCloud(int localProjectId, String expenseId, DeleteCallback callback) {
        Project project = databaseHelper.getProjectById(localProjectId);

        if (project == null || project.getProjectCode() == null || project.getProjectCode().trim().isEmpty()) {
            callback.onFailure("Project not found for Firestore expense deletion.");
            return;
        }

        firestore.collection("projects")
                .document(project.getProjectCode())
                .collection("expenses")
                .document(expenseId)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess("Expense deleted from Firestore successfully."))
                .addOnFailureListener(e -> {
                    callback.onFailure("Failed to delete expense from Firestore.");
                    Log.e("SYNC_DELETE", "Expense delete failed", e);
                });
    }

    private String getStringValue(QueryDocumentSnapshot document, String field) {
        String value = document.getString(field);
        return value != null ? value : "";
    }

    private long getLongValue(QueryDocumentSnapshot document, String field) {
        Number value = (Number) document.get(field);
        return value != null ? value.longValue() : System.currentTimeMillis();
    }

    private double getDoubleValue(QueryDocumentSnapshot document, String field) {
        Number value = (Number) document.get(field);
        return value != null ? value.doubleValue() : 0.0;
    }

    private interface StepCallback {
        void onSuccess();
        void onFailure(String message);
    }
}