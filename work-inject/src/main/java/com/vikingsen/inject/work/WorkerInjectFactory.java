package com.vikingsen.inject.work;

import android.content.Context;

import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

public class WorkerInjectFactory extends WorkerFactory {

    private final Map<String, WorkFactory> factories;

    @Inject
    public WorkerInjectFactory(@NonNull Map<String, WorkFactory> factories) {
        if (factories == null) throw new NullPointerException("factories == null");
        this.factories = factories;
    }


    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context appContext, @NonNull String workerClassName, @NonNull WorkerParameters workerParameters) {
        WorkFactory factory = factories.get(workerClassName);
        if (factory != null) {
            return factory.create(appContext, workerParameters);
        }
        return null;
        // TODO: 4/2/19 See if we can use this fallback.
//        return createWorkerWithDefaultFallback(appContext, workerClassName, workerParameters);
    }
}
