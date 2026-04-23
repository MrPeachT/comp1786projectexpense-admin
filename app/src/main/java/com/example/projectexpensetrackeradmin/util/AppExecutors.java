package com.example.projectexpensetrackeradmin.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private AppExecutors() {
    }

    public static ExecutorService getDatabaseExecutor() {
        return databaseExecutor;
    }
}