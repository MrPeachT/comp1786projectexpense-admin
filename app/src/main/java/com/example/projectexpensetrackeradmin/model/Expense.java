package com.example.projectexpensetrackeradmin.model;

/**
 * Expense model class.
 * Represents an expense record associated with a specific project.
 * Contains financial details, payment information, and metadata.
 */
public class Expense {

    // Primary key (auto-increment in database)
    private int id;

    // Business identifier for the expense (user-defined)
    private String expenseId;

    // Foreign key linking to Project
    private int projectId;

    // Basic expense information
    private String dateOfExpense;
    private double amount;
    private String currency;

    // Classification and payment details
    private String expenseType;
    private String paymentMethod;
    private String claimant;
    private String paymentStatus;

    // Additional optional information
    private String description;
    private String location;
    private String imagePath;

    // Metadata for syncing and tracking updates
    private long lastModified;
    private int synced;

    // Default constructor (required for object creation and database operations)
    public Expense() {
    }

    // Full constructor for initializing all fields
    public Expense(int id, String expenseId, int projectId, String dateOfExpense,
                   double amount, String currency, String expenseType, String paymentMethod,
                   String claimant, String paymentStatus, String description, String location,
                   String imagePath, long lastModified, int synced) {
        this.id = id;
        this.expenseId = expenseId;
        this.projectId = projectId;
        this.dateOfExpense = dateOfExpense;
        this.amount = amount;
        this.currency = currency;
        this.expenseType = expenseType;
        this.paymentMethod = paymentMethod;
        this.claimant = claimant;
        this.paymentStatus = paymentStatus;
        this.description = description;
        this.location = location;
        this.imagePath = imagePath;
        this.lastModified = lastModified;
        this.synced = synced;
    }

    // =========================
    // Getters & Setters
    // =========================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getDateOfExpense() {
        return dateOfExpense;
    }

    public void setDateOfExpense(String dateOfExpense) {
        this.dateOfExpense = dateOfExpense;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getClaimant() {
        return claimant;
    }

    public void setClaimant(String claimant) {
        this.claimant = claimant;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }
}