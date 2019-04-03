package com.vikingsen.inject.work;

import android.content.Context;

import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

public class WorkerInjectFactory extends WorkerFactory {

    @Override
    public ListenableWorker createWorker(Context appContext, String workerClassName, WorkerParameters workerParameters) {
        throw new RuntimeException("STUB!");
    }
}
