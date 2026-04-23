package com.example.projectexpensetrackeradmin.model;

/**
 * Project model class.
 * Represents a project entity stored in the local SQLite database
 * and optionally synchronised with a cloud service.
 */
public class Project {

    // Primary key (auto-generated in SQLite)
    private int id;

    // Unique identifier used by users/admin
    private String projectCode;

    // Basic project information
    private String projectName;
    private String projectDescription;

    // Timeline of the project
    private String startDate;
    private String endDate;

    // Management and status
    private String projectManager;
    private String projectStatus; // e.g., Active, Completed, On Hold

    // Financial data
    private double projectBudget;

    // Optional details
    private String specialRequirements;
    private String clientInfo;
    private String exactLocation;
    private String imagePath;

    // Metadata for tracking updates and sync state
    private long lastModified;
    private int synced; // 0 = not synced, 1 = synced

    // Default constructor (required for database operations)
    public Project() {
    }

    // Full constructor to initialise all fields
    public Project(int id, String projectCode, String projectName, String projectDescription,
                   String startDate, String endDate, String projectManager, String projectStatus,
                   double projectBudget, String specialRequirements, String clientInfo,
                   String exactLocation, String imagePath, long lastModified, int synced) {
        this.id = id;
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectManager = projectManager;
        this.projectStatus = projectStatus;
        this.projectBudget = projectBudget;
        this.specialRequirements = specialRequirements;
        this.clientInfo = clientInfo;
        this.exactLocation = exactLocation;
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

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public double getProjectBudget() {
        return projectBudget;
    }

    public void setProjectBudget(double projectBudget) {
        this.projectBudget = projectBudget;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getExactLocation() {
        return exactLocation;
    }

    public void setExactLocation(String exactLocation) {
        this.exactLocation = exactLocation;
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