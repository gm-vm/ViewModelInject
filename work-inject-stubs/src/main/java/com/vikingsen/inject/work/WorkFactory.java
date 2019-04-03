package com.vikingsen.inject.work;

import android.content.Context;

import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

public interface WorkFactory {
    ListenableWorker create(Context context, WorkerParameters workerParameters);
}
