package com.example.projectexpensetrackeradmin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.projectexpensetrackeradmin.model.Expense;
import com.example.projectexpensetrackeradmin.model.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper
 * Handles all database operations (CRUD for Project and Expense).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "project_expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // ================= PROJECT TABLE =================
    public static final String TABLE_PROJECTS = "projects";
    public static final String COLUMN_PROJECT_ID = "id";
    public static final String COLUMN_PROJECT_CODE = "project_code";
    public static final String COLUMN_PROJECT_NAME = "project_name";
    public static final String COLUMN_PROJECT_DESCRIPTION = "project_description";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_PROJECT_MANAGER = "project_manager";
    public static final String COLUMN_PROJECT_STATUS = "project_status";
    public static final String COLUMN_PROJECT_BUDGET = "project_budget";
    public static final String COLUMN_SPECIAL_REQUIREMENTS = "special_requirements";
    public static final String COLUMN_CLIENT_INFO = "client_info";
    public static final String COLUMN_EXACT_LOCATION = "exact_location";
    public static final String COLUMN_PROJECT_IMAGE_PATH = "image_path";
    public static final String COLUMN_PROJECT_LAST_MODIFIED = "last_modified";
    public static final String COLUMN_PROJECT_SYNCED = "synced";

    // ================= EXPENSE TABLE =================
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSE_DB_ID = "id";
    public static final String COLUMN_EXPENSE_ID = "expense_id";
    public static final String COLUMN_EXPENSE_PROJECT_ID = "project_id";
    public static final String COLUMN_DATE_OF_EXPENSE = "date_of_expense";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_EXPENSE_TYPE = "expense_type";
    public static final String COLUMN_PAYMENT_METHOD = "payment_method";
    public static final String COLUMN_CLAIMANT = "claimant";
    public static final String COLUMN_PAYMENT_STATUS = "payment_status";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_EXPENSE_IMAGE_PATH = "image_path";
    public static final String COLUMN_EXPENSE_LAST_MODIFIED = "last_modified";
    public static final String COLUMN_EXPENSE_SYNCED = "synced";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PROJECTS + " (" +
                COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PROJECT_CODE + " TEXT, " +
                COLUMN_PROJECT_NAME + " TEXT, " +
                COLUMN_PROJECT_DESCRIPTION + " TEXT, " +
                COLUMN_START_DATE + " TEXT, " +
                COLUMN_END_DATE + " TEXT, " +
                COLUMN_PROJECT_MANAGER + " TEXT, " +
                COLUMN_PROJECT_STATUS + " TEXT, " +
                COLUMN_PROJECT_BUDGET + " REAL, " +
                COLUMN_SPECIAL_REQUIREMENTS + " TEXT, " +
                COLUMN_CLIENT_INFO + " TEXT, " +
                COLUMN_EXACT_LOCATION + " TEXT, " +
                COLUMN_PROJECT_IMAGE_PATH + " TEXT, " +
                COLUMN_PROJECT_LAST_MODIFIED + " INTEGER, " +
                COLUMN_PROJECT_SYNCED + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_EXPENSES + " (" +
                COLUMN_EXPENSE_DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXPENSE_ID + " TEXT, " +
                COLUMN_EXPENSE_PROJECT_ID + " INTEGER, " +
                COLUMN_DATE_OF_EXPENSE + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CURRENCY + " TEXT, " +
                COLUMN_EXPENSE_TYPE + " TEXT, " +
                COLUMN_PAYMENT_METHOD + " TEXT, " +
                COLUMN_CLAIMANT + " TEXT, " +
                COLUMN_PAYMENT_STATUS + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_EXPENSE_IMAGE_PATH + " TEXT, " +
                COLUMN_EXPENSE_LAST_MODIFIED + " INTEGER, " +
                COLUMN_EXPENSE_SYNCED + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_EXPENSE_PROJECT_ID + ") REFERENCES " +
                TABLE_PROJECTS + "(" + COLUMN_PROJECT_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        onCreate(db);
    }

    // ================= PROJECT CRUD =================

    public long insertProject(Project p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(COLUMN_PROJECT_CODE, p.getProjectCode());
        v.put(COLUMN_PROJECT_NAME, p.getProjectName());
        v.put(COLUMN_PROJECT_DESCRIPTION, p.getProjectDescription());
        v.put(COLUMN_START_DATE, p.getStartDate());
        v.put(COLUMN_END_DATE, p.getEndDate());
        v.put(COLUMN_PROJECT_MANAGER, p.getProjectManager());
        v.put(COLUMN_PROJECT_STATUS, p.getProjectStatus());
        v.put(COLUMN_PROJECT_BUDGET, p.getProjectBudget());
        v.put(COLUMN_SPECIAL_REQUIREMENTS, p.getSpecialRequirements());
        v.put(COLUMN_CLIENT_INFO, p.getClientInfo());
        v.put(COLUMN_EXACT_LOCATION, p.getExactLocation());
        v.put(COLUMN_PROJECT_IMAGE_PATH, p.getImagePath());
        v.put(COLUMN_PROJECT_LAST_MODIFIED, p.getLastModified());
        v.put(COLUMN_PROJECT_SYNCED, p.getSynced());

        return db.insert(TABLE_PROJECTS, null, v);
    }

    public int updateProject(Project p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(COLUMN_PROJECT_CODE, p.getProjectCode());
        v.put(COLUMN_PROJECT_NAME, p.getProjectName());
        v.put(COLUMN_PROJECT_DESCRIPTION, p.getProjectDescription());
        v.put(COLUMN_START_DATE, p.getStartDate());
        v.put(COLUMN_END_DATE, p.getEndDate());
        v.put(COLUMN_PROJECT_MANAGER, p.getProjectManager());
        v.put(COLUMN_PROJECT_STATUS, p.getProjectStatus());
        v.put(COLUMN_PROJECT_BUDGET, p.getProjectBudget());
        v.put(COLUMN_SPECIAL_REQUIREMENTS, p.getSpecialRequirements());
        v.put(COLUMN_CLIENT_INFO, p.getClientInfo());
        v.put(COLUMN_EXACT_LOCATION, p.getExactLocation());
        v.put(COLUMN_PROJECT_IMAGE_PATH, p.getImagePath());
        v.put(COLUMN_PROJECT_LAST_MODIFIED, p.getLastModified());
        v.put(COLUMN_PROJECT_SYNCED, p.getSynced());

        return db.update(TABLE_PROJECTS, v, COLUMN_PROJECT_ID + "=?",
                new String[]{String.valueOf(p.getId())});
    }

    public void deleteProject(int projectId) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_EXPENSES,
                COLUMN_EXPENSE_PROJECT_ID + "=?",
                new String[]{String.valueOf(projectId)});

        db.delete(TABLE_PROJECTS,
                COLUMN_PROJECT_ID + "=?",
                new String[]{String.valueOf(projectId)});
    }

    public List<Project> getAllProjects() {
        List<Project> list = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_PROJECTS + " ORDER BY " + COLUMN_PROJECT_ID + " DESC",
                null
        );

        while (c.moveToNext()) {
            Project p = new Project();
            p.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_ID)));
            p.setProjectCode(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_CODE)));
            p.setProjectName(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_NAME)));
            p.setProjectDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_DESCRIPTION)));
            p.setStartDate(c.getString(c.getColumnIndexOrThrow(COLUMN_START_DATE)));
            p.setEndDate(c.getString(c.getColumnIndexOrThrow(COLUMN_END_DATE)));
            p.setProjectManager(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_MANAGER)));
            p.setProjectStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_STATUS)));
            p.setProjectBudget(c.getDouble(c.getColumnIndexOrThrow(COLUMN_PROJECT_BUDGET)));
            p.setSpecialRequirements(c.getString(c.getColumnIndexOrThrow(COLUMN_SPECIAL_REQUIREMENTS)));
            p.setClientInfo(c.getString(c.getColumnIndexOrThrow(COLUMN_CLIENT_INFO)));
            p.setExactLocation(c.getString(c.getColumnIndexOrThrow(COLUMN_EXACT_LOCATION)));
            p.setImagePath(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH)));
            p.setLastModified(c.getLong(c.getColumnIndexOrThrow(COLUMN_PROJECT_LAST_MODIFIED)));
            p.setSynced(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_SYNCED)));
            list.add(p);
        }

        c.close();
        return list;
    }

    public Project getProjectById(int id) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_PROJECTS + " WHERE " + COLUMN_PROJECT_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        Project p = null;

        if (c.moveToFirst()) {
            p = new Project();
            p.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_ID)));
            p.setProjectCode(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_CODE)));
            p.setProjectName(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_NAME)));
            p.setProjectDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_DESCRIPTION)));
            p.setStartDate(c.getString(c.getColumnIndexOrThrow(COLUMN_START_DATE)));
            p.setEndDate(c.getString(c.getColumnIndexOrThrow(COLUMN_END_DATE)));
            p.setProjectManager(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_MANAGER)));
            p.setProjectStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_STATUS)));
            p.setProjectBudget(c.getDouble(c.getColumnIndexOrThrow(COLUMN_PROJECT_BUDGET)));
            p.setSpecialRequirements(c.getString(c.getColumnIndexOrThrow(COLUMN_SPECIAL_REQUIREMENTS)));
            p.setClientInfo(c.getString(c.getColumnIndexOrThrow(COLUMN_CLIENT_INFO)));
            p.setExactLocation(c.getString(c.getColumnIndexOrThrow(COLUMN_EXACT_LOCATION)));
            p.setImagePath(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH)));
            p.setLastModified(c.getLong(c.getColumnIndexOrThrow(COLUMN_PROJECT_LAST_MODIFIED)));
            p.setSynced(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_SYNCED)));
        }

        c.close();
        return p;
    }

    public List<Project> searchProjects(String keyword) {
        List<Project> list = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_PROJECTS +
                        " WHERE " + COLUMN_PROJECT_NAME + " LIKE ? OR " +
                        COLUMN_PROJECT_DESCRIPTION + " LIKE ? " +
                        "ORDER BY " + COLUMN_PROJECT_ID + " DESC",
                new String[]{"%" + keyword + "%", "%" + keyword + "%"}
        );

        while (c.moveToNext()) {
            Project p = new Project();
            p.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_ID)));
            p.setProjectCode(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_CODE)));
            p.setProjectName(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_NAME)));
            p.setProjectDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_DESCRIPTION)));
            p.setStartDate(c.getString(c.getColumnIndexOrThrow(COLUMN_START_DATE)));
            p.setEndDate(c.getString(c.getColumnIndexOrThrow(COLUMN_END_DATE)));
            p.setProjectManager(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_MANAGER)));
            p.setProjectStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_STATUS)));
            p.setProjectBudget(c.getDouble(c.getColumnIndexOrThrow(COLUMN_PROJECT_BUDGET)));
            p.setSpecialRequirements(c.getString(c.getColumnIndexOrThrow(COLUMN_SPECIAL_REQUIREMENTS)));
            p.setClientInfo(c.getString(c.getColumnIndexOrThrow(COLUMN_CLIENT_INFO)));
            p.setExactLocation(c.getString(c.getColumnIndexOrThrow(COLUMN_EXACT_LOCATION)));
            p.setImagePath(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH)));
            p.setLastModified(c.getLong(c.getColumnIndexOrThrow(COLUMN_PROJECT_LAST_MODIFIED)));
            p.setSynced(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_SYNCED)));
            list.add(p);
        }

        c.close();
        return list;
    }

    public List<Project> advancedSearchProjects(String keyword, String status, String manager, String startDate) {
        List<Project> list = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT * FROM " + TABLE_PROJECTS + " WHERE 1=1"
        );

        List<String> args = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append(" AND (")
                    .append(COLUMN_PROJECT_NAME).append(" LIKE ? OR ")
                    .append(COLUMN_PROJECT_DESCRIPTION).append(" LIKE ?)");
            args.add("%" + keyword + "%");
            args.add("%" + keyword + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            query.append(" AND ").append(COLUMN_PROJECT_STATUS).append(" = ?");
            args.add(status);
        }

        if (manager != null && !manager.trim().isEmpty()) {
            query.append(" AND ").append(COLUMN_PROJECT_MANAGER).append(" LIKE ?");
            args.add("%" + manager + "%");
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            query.append(" AND ").append(COLUMN_START_DATE).append(" = ?");
            args.add(startDate);
        }

        query.append(" ORDER BY ").append(COLUMN_PROJECT_ID).append(" DESC");

        Cursor c = getReadableDatabase().rawQuery(query.toString(), args.toArray(new String[0]));

        while (c.moveToNext()) {
            Project p = new Project();
            p.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_ID)));
            p.setProjectCode(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_CODE)));
            p.setProjectName(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_NAME)));
            p.setProjectDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_DESCRIPTION)));
            p.setStartDate(c.getString(c.getColumnIndexOrThrow(COLUMN_START_DATE)));
            p.setEndDate(c.getString(c.getColumnIndexOrThrow(COLUMN_END_DATE)));
            p.setProjectManager(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_MANAGER)));
            p.setProjectStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_STATUS)));
            p.setProjectBudget(c.getDouble(c.getColumnIndexOrThrow(COLUMN_PROJECT_BUDGET)));
            p.setSpecialRequirements(c.getString(c.getColumnIndexOrThrow(COLUMN_SPECIAL_REQUIREMENTS)));
            p.setClientInfo(c.getString(c.getColumnIndexOrThrow(COLUMN_CLIENT_INFO)));
            p.setExactLocation(c.getString(c.getColumnIndexOrThrow(COLUMN_EXACT_LOCATION)));
            p.setImagePath(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH)));
            p.setLastModified(c.getLong(c.getColumnIndexOrThrow(COLUMN_PROJECT_LAST_MODIFIED)));
            p.setSynced(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_SYNCED)));
            list.add(p);
        }

        c.close();
        return list;
    }

    public void resetDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_EXPENSES, null, null);
        db.delete(TABLE_PROJECTS, null, null);
    }

    public int markProjectAsSynced(int projectId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_SYNCED, 1);

        return db.update(
                TABLE_PROJECTS,
                values,
                COLUMN_PROJECT_ID + "=?",
                new String[]{String.valueOf(projectId)}
        );
    }

    // ================= EXPENSE CRUD =================

    public long insertExpense(Expense e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(COLUMN_EXPENSE_ID, e.getExpenseId());
        v.put(COLUMN_EXPENSE_PROJECT_ID, e.getProjectId());
        v.put(COLUMN_DATE_OF_EXPENSE, e.getDateOfExpense());
        v.put(COLUMN_AMOUNT, e.getAmount());
        v.put(COLUMN_CURRENCY, e.getCurrency());
        v.put(COLUMN_EXPENSE_TYPE, e.getExpenseType());
        v.put(COLUMN_PAYMENT_METHOD, e.getPaymentMethod());
        v.put(COLUMN_CLAIMANT, e.getClaimant());
        v.put(COLUMN_PAYMENT_STATUS, e.getPaymentStatus());
        v.put(COLUMN_DESCRIPTION, e.getDescription());
        v.put(COLUMN_LOCATION, e.getLocation());
        v.put(COLUMN_EXPENSE_IMAGE_PATH, e.getImagePath());
        v.put(COLUMN_EXPENSE_LAST_MODIFIED, e.getLastModified());
        v.put(COLUMN_EXPENSE_SYNCED, e.getSynced());

        return db.insert(TABLE_EXPENSES, null, v);
    }

    public int updateExpense(Expense e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put(COLUMN_EXPENSE_ID, e.getExpenseId());
        v.put(COLUMN_EXPENSE_PROJECT_ID, e.getProjectId());
        v.put(COLUMN_DATE_OF_EXPENSE, e.getDateOfExpense());
        v.put(COLUMN_AMOUNT, e.getAmount());
        v.put(COLUMN_CURRENCY, e.getCurrency());
        v.put(COLUMN_EXPENSE_TYPE, e.getExpenseType());
        v.put(COLUMN_PAYMENT_METHOD, e.getPaymentMethod());
        v.put(COLUMN_CLAIMANT, e.getClaimant());
        v.put(COLUMN_PAYMENT_STATUS, e.getPaymentStatus());
        v.put(COLUMN_DESCRIPTION, e.getDescription());
        v.put(COLUMN_LOCATION, e.getLocation());
        v.put(COLUMN_EXPENSE_IMAGE_PATH, e.getImagePath());
        v.put(COLUMN_EXPENSE_LAST_MODIFIED, e.getLastModified());
        v.put(COLUMN_EXPENSE_SYNCED, e.getSynced());

        return db.update(TABLE_EXPENSES, v, COLUMN_EXPENSE_DB_ID + "=?",
                new String[]{String.valueOf(e.getId())});
    }

    public List<Expense> getExpensesByProjectId(int projectId) {
        List<Expense> list = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_EXPENSES +
                        " WHERE " + COLUMN_EXPENSE_PROJECT_ID + "=? " +
                        "ORDER BY " + COLUMN_EXPENSE_DB_ID + " DESC",
                new String[]{String.valueOf(projectId)}
        );

        while (c.moveToNext()) {
            Expense e = new Expense();
            e.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_DB_ID)));
            e.setExpenseId(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)));
            e.setProjectId(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_PROJECT_ID)));
            e.setDateOfExpense(c.getString(c.getColumnIndexOrThrow(COLUMN_DATE_OF_EXPENSE)));
            e.setAmount(c.getDouble(c.getColumnIndexOrThrow(COLUMN_AMOUNT)));
            e.setCurrency(c.getString(c.getColumnIndexOrThrow(COLUMN_CURRENCY)));
            e.setExpenseType(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_TYPE)));
            e.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)));
            e.setClaimant(c.getString(c.getColumnIndexOrThrow(COLUMN_CLAIMANT)));
            e.setPaymentStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_PAYMENT_STATUS)));
            e.setDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            e.setLocation(c.getString(c.getColumnIndexOrThrow(COLUMN_LOCATION)));
            e.setImagePath(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_IMAGE_PATH)));
            e.setLastModified(c.getLong(c.getColumnIndexOrThrow(COLUMN_EXPENSE_LAST_MODIFIED)));
            e.setSynced(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_SYNCED)));
            list.add(e);
        }

        c.close();
        return list;
    }

    public Expense getExpenseById(int id) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_EXPENSE_DB_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        Expense e = null;

        if (c.moveToFirst()) {
            e = new Expense();
            e.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_DB_ID)));
            e.setExpenseId(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)));
            e.setProjectId(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_PROJECT_ID)));
            e.setDateOfExpense(c.getString(c.getColumnIndexOrThrow(COLUMN_DATE_OF_EXPENSE)));
            e.setAmount(c.getDouble(c.getColumnIndexOrThrow(COLUMN_AMOUNT)));
            e.setCurrency(c.getString(c.getColumnIndexOrThrow(COLUMN_CURRENCY)));
            e.setExpenseType(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_TYPE)));
            e.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)));
            e.setClaimant(c.getString(c.getColumnIndexOrThrow(COLUMN_CLAIMANT)));
            e.setPaymentStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_PAYMENT_STATUS)));
            e.setDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            e.setLocation(c.getString(c.getColumnIndexOrThrow(COLUMN_LOCATION)));
            e.setImagePath(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_IMAGE_PATH)));
            e.setLastModified(c.getLong(c.getColumnIndexOrThrow(COLUMN_EXPENSE_LAST_MODIFIED)));
            e.setSynced(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_SYNCED)));
        }

        c.close();
        return e;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_EXPENSES + " ORDER BY " + COLUMN_EXPENSE_DB_ID + " DESC",
                null
        );

        while (c.moveToNext()) {
            Expense e = new Expense();
            e.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_DB_ID)));
            e.setExpenseId(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)));
            e.setProjectId(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_PROJECT_ID)));
            e.setDateOfExpense(c.getString(c.getColumnIndexOrThrow(COLUMN_DATE_OF_EXPENSE)));
            e.setAmount(c.getDouble(c.getColumnIndexOrThrow(COLUMN_AMOUNT)));
            e.setCurrency(c.getString(c.getColumnIndexOrThrow(COLUMN_CURRENCY)));
            e.setExpenseType(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_TYPE)));
            e.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)));
            e.setClaimant(c.getString(c.getColumnIndexOrThrow(COLUMN_CLAIMANT)));
            e.setPaymentStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_PAYMENT_STATUS)));
            e.setDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            e.setLocation(c.getString(c.getColumnIndexOrThrow(COLUMN_LOCATION)));
            e.setImagePath(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_IMAGE_PATH)));
            e.setLastModified(c.getLong(c.getColumnIndexOrThrow(COLUMN_EXPENSE_LAST_MODIFIED)));
            e.setSynced(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_SYNCED)));
            list.add(e);
        }

        c.close();
        return list;
    }

    public void deleteExpense(int id) {
        getWritableDatabase().delete(TABLE_EXPENSES,
                COLUMN_EXPENSE_DB_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public int markExpenseAsSynced(int expenseId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPENSE_SYNCED, 1);

        return db.update(
                TABLE_EXPENSES,
                values,
                COLUMN_EXPENSE_DB_ID + "=?",
                new String[]{String.valueOf(expenseId)}
        );
    }

    public Project getProjectByCode(String projectCode) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_PROJECTS + " WHERE " + COLUMN_PROJECT_CODE + "=?",
                new String[]{projectCode}
        );

        Project p = null;

        if (c.moveToFirst()) {
            p = new Project();
            p.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_ID)));
            p.setProjectCode(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_CODE)));
            p.setProjectName(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_NAME)));
            p.setProjectDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_DESCRIPTION)));
            p.setStartDate(c.getString(c.getColumnIndexOrThrow(COLUMN_START_DATE)));
            p.setEndDate(c.getString(c.getColumnIndexOrThrow(COLUMN_END_DATE)));
            p.setProjectManager(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_MANAGER)));
            p.setProjectStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_STATUS)));
            p.setProjectBudget(c.getDouble(c.getColumnIndexOrThrow(COLUMN_PROJECT_BUDGET)));
            p.setSpecialRequirements(c.getString(c.getColumnIndexOrThrow(COLUMN_SPECIAL_REQUIREMENTS)));
            p.setClientInfo(c.getString(c.getColumnIndexOrThrow(COLUMN_CLIENT_INFO)));
            p.setExactLocation(c.getString(c.getColumnIndexOrThrow(COLUMN_EXACT_LOCATION)));
            p.setImagePath(c.getString(c.getColumnIndexOrThrow(COLUMN_PROJECT_IMAGE_PATH)));
            p.setLastModified(c.getLong(c.getColumnIndexOrThrow(COLUMN_PROJECT_LAST_MODIFIED)));
            p.setSynced(c.getInt(c.getColumnIndexOrThrow(COLUMN_PROJECT_SYNCED)));
        }

        c.close();
        return p;
    }

    public Expense getExpenseByBusinessIdAndProjectId(String expenseId, int projectId) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_EXPENSES +
                        " WHERE " + COLUMN_EXPENSE_ID + "=? AND " + COLUMN_EXPENSE_PROJECT_ID + "=?",
                new String[]{expenseId, String.valueOf(projectId)}
        );

        Expense e = null;

        if (c.moveToFirst()) {
            e = new Expense();
            e.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_DB_ID)));
            e.setExpenseId(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)));
            e.setProjectId(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_PROJECT_ID)));
            e.setDateOfExpense(c.getString(c.getColumnIndexOrThrow(COLUMN_DATE_OF_EXPENSE)));
            e.setAmount(c.getDouble(c.getColumnIndexOrThrow(COLUMN_AMOUNT)));
            e.setCurrency(c.getString(c.getColumnIndexOrThrow(COLUMN_CURRENCY)));
            e.setExpenseType(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_TYPE)));
            e.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)));
            e.setClaimant(c.getString(c.getColumnIndexOrThrow(COLUMN_CLAIMANT)));
            e.setPaymentStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_PAYMENT_STATUS)));
            e.setDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            e.setLocation(c.getString(c.getColumnIndexOrThrow(COLUMN_LOCATION)));
            e.setImagePath(c.getString(c.getColumnIndexOrThrow(COLUMN_EXPENSE_IMAGE_PATH)));
            e.setLastModified(c.getLong(c.getColumnIndexOrThrow(COLUMN_EXPENSE_LAST_MODIFIED)));
            e.setSynced(c.getInt(c.getColumnIndexOrThrow(COLUMN_EXPENSE_SYNCED)));
        }

        c.close();
        return e;
    }

    public long upsertProjectFromCloud(Project project) {
        Project existingProject = getProjectByCode(project.getProjectCode());

        project.setSynced(1);

        if (existingProject != null) {
            project.setId(existingProject.getId());
            updateProject(project);
            return existingProject.getId();
        } else {
            return insertProject(project);
        }
    }

    public long upsertExpenseFromCloud(Expense expense) {
        Expense existingExpense = getExpenseByBusinessIdAndProjectId(
                expense.getExpenseId(),
                expense.getProjectId()
        );

        expense.setSynced(1);

        if (existingExpense != null) {
            expense.setId(existingExpense.getId());
            updateExpense(expense);
            return existingExpense.getId();
        } else {
            return insertExpense(expense);
        }
    }

    public boolean isProjectCodeExists(String projectCode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + TABLE_PROJECTS + " WHERE " + COLUMN_PROJECT_CODE + "=? LIMIT 1",
                new String[]{projectCode}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public boolean isExpenseIdExists(String expenseId, int projectId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + TABLE_EXPENSES +
                        " WHERE " + COLUMN_EXPENSE_ID + "=? AND " + COLUMN_EXPENSE_PROJECT_ID + "=? LIMIT 1",
                new String[]{expenseId, String.valueOf(projectId)}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }
}