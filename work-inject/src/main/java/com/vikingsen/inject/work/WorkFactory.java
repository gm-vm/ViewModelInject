package com.vikingsen.inject.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

public interface WorkFactory {
    @NonNull
    ListenableWorker create(@NonNull Context context, @NonNull WorkerParameters workerParameters);
}
