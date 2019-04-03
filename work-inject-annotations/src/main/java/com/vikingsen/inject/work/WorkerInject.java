package com.vikingsen.inject.work;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@Target(CONSTRUCTOR)
public @interface WorkerInject {
}
