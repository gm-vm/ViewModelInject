package com.vikingsen.inject.work.processor

import com.google.common.truth.Truth.assertAbout
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import com.google.testing.compile.JavaSourcesSubjectFactory.javaSources
import org.junit.Test

private const val GENERATED_TYPE = "javax.annotation.Generated" // TODO vary once JDK 9 works.
private const val GENERATED_ANNOTATION = """
@Generated(
        value = "com.vikingsen.inject.work.processor.WorkerInjectProcessor",
        comments = "https://github.com/hansenji/ViewModelInject"
)
"""

class WorkerInjectProcessorTest {
    @Test
    fun simpleTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, Long foo) {
                    super(context, workerParameters);
                }
            }
        """
        )
        val inputModule = JavaFileObjects.forSourceString(
            "test.TestModule", """
            package test;

            import com.vikingsen.inject.work.WorkerModule;
            import dagger.Module;

            @WorkerModule
            @Module(includes = WorkerInject_TestModule.class)
            abstract class TestModule {}
        """
        )

        val expectedFactory = JavaFileObjects.forSourceString(
            "test.TestWorker_AssistedFactory", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.vikingsen.inject.work.WorkFactory;
            import java.lang.Long;
            import java.lang.Override;
            import $GENERATED_TYPE;
            import javax.inject.Inject;
            import javax.inject.Provider;

            $GENERATED_ANNOTATION
            public final class TestWorker_AssistedFactory implements WorkFactory {
                private final Provider<Long> foo;

                @Inject public TestWorker_AssistedFactory(Provider<Long> foo) {
                    this.foo = foo;
                }

                @Override public ListenableWorker create(Context context, WorkerParameters workerParameters) {
                    return new TestWorker(context, workerParameters, foo.get());
                }
            }
        """
        )

        val expectedModule = JavaFileObjects.forSourceString(
            "test.WorkerModule_TestModule", """
            package test;

            import com.vikingsen.inject.work.WorkFactory;
            import dagger.Binds;
            import dagger.Module;
            import dagger.multibindings.IntoMap;
            import dagger.multibindings.StringKey;
            import $GENERATED_TYPE;

            @Module
            $GENERATED_ANNOTATION
            abstract class WorkerInject_TestModule {
                private WorkerInject_TestModule() {}

                @Binds
                @IntoMap
                @StringKey("test.TestWorker")
                abstract WorkFactory bind_test_TestWorker(TestWorker_AssistedFactory factory);
            }
        """
        )

        assertAbout(javaSources())
            .that(listOf(inputWorker, inputModule))
            .processedWith(WorkerInjectProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedFactory, expectedModule)
    }

    @Test
    fun publicTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, Long foo) {
                    super(context, workerParameters);
                }
            }
        """
        )
        val inputModule = JavaFileObjects.forSourceString(
            "test.TestModule", """
            package test;

            import com.vikingsen.inject.work.WorkerModule;
            import dagger.Module;

            @WorkerModule
            @Module(includes = WorkerInject_TestModule.class)
            public abstract class TestModule {}
        """
        )

        val expectedFactory = JavaFileObjects.forSourceString(
            "test.TestWorker_AssistedFactory", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.vikingsen.inject.work.WorkFactory;
            import java.lang.Long;
            import java.lang.Override;
            import $GENERATED_TYPE;
            import javax.inject.Inject;
            import javax.inject.Provider;

            $GENERATED_ANNOTATION
            public final class TestWorker_AssistedFactory implements WorkFactory {
                private final Provider<Long> foo;

                @Inject public TestWorker_AssistedFactory(Provider<Long> foo) {
                    this.foo = foo;
                }

                @Override public ListenableWorker create(Context context, WorkerParameters workerParameters) {
                    return new TestWorker(context, workerParameters, foo.get());
                }
            }
        """
        )

        val expectedModule = JavaFileObjects.forSourceString(
            "test.WorkerModule_TestModule", """
            package test;

            import com.vikingsen.inject.work.WorkFactory;
            import dagger.Binds;
            import dagger.Module;
            import dagger.multibindings.IntoMap;
            import dagger.multibindings.StringKey;
            import $GENERATED_TYPE;

            @Module
            $GENERATED_ANNOTATION
            public abstract class WorkerInject_TestModule {
                private WorkerInject_TestModule() {}

                @Binds
                @IntoMap
                @StringKey("test.TestWorker")
                abstract WorkFactory bind_test_TestWorker(TestWorker_AssistedFactory factory);
            }
        """
        )

        assertAbout(javaSources())
            .that(listOf(inputWorker, inputModule))
            .processedWith(WorkerInjectProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedFactory, expectedModule)
    }

    @Test
    fun nestedTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class Outer {
                static class TestWorker extends ListenableWorker {
                    @WorkerInject
                    TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, Long foo) {
                        super(context, workerParameters);
                    }
                }
            }
        """
        )
        val inputModule = JavaFileObjects.forSourceString(
            "test.TestModule", """
            package test;

            import com.vikingsen.inject.work.WorkerModule;
            import dagger.Module;

            @WorkerModule
            @Module(includes = WorkerInject_TestModule.class)
            abstract class TestModule {}
        """
        )

        val expectedFactory = JavaFileObjects.forSourceString(
            "test.TestWorker_AssistedFactory", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.vikingsen.inject.work.WorkFactory;
            import java.lang.Long;
            import java.lang.Override;
            import $GENERATED_TYPE;
            import javax.inject.Inject;
            import javax.inject.Provider;

            $GENERATED_ANNOTATION
            public final class Outer${'$'}TestWorker_AssistedFactory implements WorkFactory {
                private final Provider<Long> foo;

                @Inject public TestWorker_AssistedFactory(Provider<Long> foo) {
                    this.foo = foo;
                }

                @Override public ListenableWorker create(Context context, WorkerParameters workerParameters) {
                    return new Outer.TestWorker(context, workerParameters, foo.get());
                }
            }
        """
        )
        val expectedModule = JavaFileObjects.forSourceString(
            "test.WorkerModule_TestModule", """
            package test;

            import com.vikingsen.inject.work.WorkFactory;
            import dagger.Binds;
            import dagger.Module;
            import dagger.multibindings.IntoMap;
            import dagger.multibindings.StringKey;
            import $GENERATED_TYPE;

            @Module
            $GENERATED_ANNOTATION
            abstract class WorkerInject_TestModule {
                private WorkerInject_TestModule() {}

                @Binds
                @IntoMap
                @StringKey("test.Outer${'$'}TestWorker")
                abstract WorkFactory bind_test_Outer${'$'}TestWorker(Outer${'$'}TestWorker_AssistedFactory factory);
            }
        """
        )

        assertAbout(javaSources())
            .that(listOf(inputWorker, inputModule))
            .processedWith(WorkerInjectProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedFactory, expectedModule)
    }

    @Test
    fun assistedParametersLastTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(Long foo, @Assisted Context context, @Assisted WorkerParameters workerParameters) {
                    super(context, workerParameters);
                }
            }
        """
        )

        val expectedFactory = JavaFileObjects.forSourceString(
            "test.TestWorker_AssistedFactory", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.vikingsen.inject.work.WorkFactory;
            import java.lang.Long;
            import java.lang.Override;
            import $GENERATED_TYPE;
            import javax.inject.Inject;
            import javax.inject.Provider;

            $GENERATED_ANNOTATION
            public final class TestWorker_AssistedFactory implements WorkFactory {
                private final Provider<Long> foo;

                @Inject public TestWorker_AssistedFactory(Provider<Long> foo) {
                    this.foo = foo;
                }

                @Override public ListenableWorker create(Context context, WorkerParameters workerParameters) {
                    return new TestWorker(foo.get(), context, workerParameters);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedFactory)
    }

    @Test
    fun differentNameContextTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted Context c, @Assisted WorkerParameters workerParameters, Long foo) {
                    super(c, workerParameters);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining(
                """
          Worker injection requires Context and WorkerParameters @Assisted parameters.
              Found:
                [android.content.Context c, androidx.work.WorkerParameters workerParameters]
              Expected:
                [android.content.Context context, androidx.work.WorkerParameters workerParameters]
          """.trimIndent()
            )
            .`in`(inputWorker).onLine(12)
    }

    @Test
    fun differentNameWorkerParametersTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted Context context, @Assisted WorkerParameters params, Long foo) {
                    super(context, params);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining(
                """
          Worker injection requires Context and WorkerParameters @Assisted parameters.
              Found:
                [android.content.Context context, androidx.work.WorkerParameters params]
              Expected:
                [android.content.Context context, androidx.work.WorkerParameters workerParameters]
          """.trimIndent()
            )
            .`in`(inputWorker).onLine(12)
    }

    @Test
    fun contextAndWorkerParametersSwappedTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted WorkerParameters workerParameters, @Assisted Context context, Long foo) {
                    super(context, workerParameters);
                }
            }
        """
        )

        val expectedFactory = JavaFileObjects.forSourceString(
            "test.TestWorker_AssistedFactory", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.vikingsen.inject.work.WorkFactory;
            import java.lang.Long;
            import java.lang.Override;
            import $GENERATED_TYPE;
            import javax.inject.Inject;
            import javax.inject.Provider;

            $GENERATED_ANNOTATION
            public final class TestWorker_AssistedFactory implements WorkFactory {
                private final Provider<Long> foo;

                @Inject public TestWorker_AssistedFactory(Provider<Long> foo) {
                    this.foo = foo;
                }

                @Override public ListenableWorker create(Context context, WorkerParameters workerParameters) {
                    return new TestWorker(workerParameters, context, foo.get());
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedFactory)
    }

    @Test
    fun typeDoesNotExtendListenableWorkerTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker {
                @WorkerInject
                TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, Long foo) {
                    super(context, workerParameters);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining("@WorkerInject-using types must be subtypes of androidx.work.ListenableWorker")
            .`in`(inputWorker).onLine(9)
    }

    @Test
    fun typeExtendsListenableWorkerSubclass() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.Worker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends Worker {
                @WorkerInject
                TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, Long foo) {
                    super(context, workerParameters);
                }
            }
        """
        )

        val expectedFactory = JavaFileObjects.forSourceString(
            "test.TestWorker_AssistedFactory", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.vikingsen.inject.work.WorkFactory;
            import java.lang.Long;
            import java.lang.Override;
            import $GENERATED_TYPE;
            import javax.inject.Inject;
            import javax.inject.Provider;

            $GENERATED_ANNOTATION
            public final class TestWorker_AssistedFactory implements WorkFactory {
                private final Provider<Long> foo;

                @Inject public TestWorker_AssistedFactory(Provider<Long> foo) {
                    this.foo = foo;
                }

                @Override public ListenableWorker create(Context context, WorkerParameters workerParameters) {
                    return new TestWorker(context, workerParameters, foo.get());
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedFactory)
    }

    @Test
    fun baseAndSubtypeInjectionTest() {
        val inputLongWorker = JavaFileObjects.forSourceString(
            "test.LongWorker", """
            package test;

            import android.content.Context;
            import androidx.work.Worker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class LongWorker extends Worker {
                @WorkerInject
                LongWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, Long foo) {
                    super(context, workerParameters);
                }
            }
        """
        )
        val inputStringWorker = JavaFileObjects.forSourceString(
            "test.StringWorker", """
            package test;

            import android.content.Context;
            import androidx.work.Worker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class StringWorker extends LongWorker {
                @WorkerInject
                StringWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, String foo) {
                    super(context, workerParameters, Long.parseLong(foo));
                }
            }
        """
        )

        val expectedLongFactory = JavaFileObjects.forSourceString(
            "test.LongWorker_AssistedFactory", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.vikingsen.inject.work.WorkFactory;
            import java.lang.Long;
            import java.lang.Override;
            import $GENERATED_TYPE;
            import javax.inject.Inject;
            import javax.inject.Provider;

            $GENERATED_ANNOTATION
            public final class LongWorker_AssistedFactory implements WorkFactory {
                private final Provider<Long> foo;

                @Inject public LongWorker_AssistedFactory(Provider<Long> foo) {
                    this.foo = foo;
                }

                @Override public ListenableWorker create(Context context, WorkerParameters workerParameters) {
                    return new LongWorker(context, workerParameters, foo.get());
                }
            }
        """
        )
        val expectedStringFactory = JavaFileObjects.forSourceString(
            "test.StringWorker_AssistedFactory", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.vikingsen.inject.work.WorkFactory;
            import java.lang.Override;
            import java.lang.String;
            import $GENERATED_TYPE;
            import javax.inject.Inject;
            import javax.inject.Provider;

            $GENERATED_ANNOTATION
            public final class StringWorker_AssistedFactory implements WorkFactory {
                private final Provider<String> foo;

                @Inject public StringWorker_AssistedFactory(Provider<String> foo) {
                    this.foo = foo;
                }

                @Override public ListenableWorker create(Context context, WorkerParameters workerParameters) {
                    return new StringWorker(context, workerParameters, foo.get());
                }
            }
        """
        )

        assertAbout(javaSources())
            .that(listOf(inputLongWorker, inputStringWorker))
            .processedWith(WorkerInjectProcessor())
            .compilesWithoutError()
            .and()
            .generatesSources(expectedLongFactory, expectedStringFactory)
    }


    @Test
    fun constructorMissingAssistedParametersFailsTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import androidx.work.ListenableWorker;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(Long foo) {
                    super(null, null);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining(
                """
          Worker injection requires Context and WorkerParameters @Assisted parameters.
              Found:
                []
              Expected:
                [android.content.Context context, androidx.work.WorkerParameters workerParameters]
          """.trimIndent()
            )
            .`in`(inputWorker).onLine(10)
    }

    @Test
    fun constructorExtraAssistedParameterFailsTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, @Assisted String hey, Long foo) {
                    super(context, workerParameters);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining(
                """
          Worker injection requires Context and WorkerParameters @Assisted parameters.
              Found:
                [android.content.Context context, androidx.work.WorkerParameters workerParameters, java.lang.String hey]
              Expected:
                [android.content.Context context, androidx.work.WorkerParameters workerParameters]
          """.trimIndent()
            )
            .`in`(inputWorker).onLine(12)
    }

    @Test
    fun constructorMissingContextFailsTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted WorkerParameters workerParameters, Long foo) {
                    super(null, workerParameters);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining(
                """
          Worker injection requires Context and WorkerParameters @Assisted parameters.
              Found:
                [androidx.work.WorkerParameters workerParameters]
              Expected:
                [android.content.Context context, androidx.work.WorkerParameters workerParameters]
          """.trimIndent()
            )
            .`in`(inputWorker).onLine(11)
    }

    @Test
    fun constructorMissingWorkerParametersFailsTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted Context context, Long foo) {
                    super(context, workerParameters);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining(
                """
          Worker injection requires Context and WorkerParameters @Assisted parameters.
              Found:
                [android.content.Context context]
              Expected:
                [android.content.Context context, androidx.work.WorkerParameters workerParameters]
          """.trimIndent()
            )
            .`in`(inputWorker).onLine(11)
    }

    @Test
    fun constructorMissingProvidedParametersNoWarningTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters) {
                    super(context, workerParameters);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .compilesWithoutError()
            .withWarningContaining("Worker injection requires at least one non-@Assisted parameter.")
            .`in`(inputWorker).onLine(12)
        // .and().generatesNoFiles()
    }


    @Test
    fun privateConstructorFailsTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                private TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, Long foo) {
                    super(context, workerParameters);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining("@WorkerInject constructor must not be private.")
            .`in`(inputWorker).onLine(12)
    }

    @Test
    fun nestedPrivateTypeFailsTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class Outer {
                private static class TestWorker extends ListenableWorker {
                    @WorkerInject
                    TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, Long foo) {
                        super(context, workerParameters);
                    }
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining("@WorkerInject-using types must not be private")
            .`in`(inputWorker).onLine(11)
    }

    @Test
    fun nestedNonStaticFailsTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class Outer {
                class TestWorker extends ListenableWorker {
                    @WorkerInject
                    TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, Long foo) {
                        super(context, workerParameters);
                    }
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining("Nested @WorkerInject-using types must be static")
            .`in`(inputWorker).onLine(11)
    }

    @Test
    fun multipleInflationInjectConstructorsFailsTest() {
        val inputWorker = JavaFileObjects.forSourceString(
            "test.TestWorker", """
            package test;

            import android.content.Context;
            import androidx.work.ListenableWorker;
            import androidx.work.WorkerParameters;
            import com.squareup.inject.assisted.Assisted;
            import com.vikingsen.inject.work.WorkerInject;

            class TestWorker extends ListenableWorker {
                @WorkerInject
                TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, @Assisted String hey, Long foo) {
                    super(context, workerParameters);
                }

                @WorkerInject
                TestWorker(@Assisted Context context, @Assisted WorkerParameters workerParameters, @Assisted String hey, String foo) {
                    super(context, workerParameters);
                }
            }
        """
        )

        assertAbout(javaSource())
            .that(inputWorker)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining("Multiple @WorkerInject-annotated constructors found.")
            .`in`(inputWorker).onLine(10)
    }


    @Test
    fun moduleWithoutModuleAnnotationFailsTest() {
        val inputModule = JavaFileObjects.forSourceString(
            "test.TestModule", """
            package test;

            import com.vikingsen.inject.work.WorkerModule;

            @WorkerModule
            abstract class TestModule {}
        """
        )

        assertAbout(javaSource())
            .that(inputModule)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining("@WorkerModule must also be annotated as a Dagger @Module")
            .`in`(inputModule).onLine(7)
    }

    @Test
    fun moduleWithNoIncludesFailsTest() {
        val inputModule = JavaFileObjects.forSourceString(
            "test.TestModule", """
            package test;

            import com.vikingsen.inject.work.WorkerModule;
            import dagger.Module;

            @WorkerModule
            @Module
            abstract class TestModule {}
        """
        )

        assertAbout(javaSource())
            .that(inputModule)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining("@WorkerModule's @Module must include WorkerInject_TestModule")
            .`in`(inputModule).onLine(9)
    }

    @Test
    fun moduleWithoutIncludeFailsTest() {
        val inputModule = JavaFileObjects.forSourceString(
            "test.TestModule", """
            package test;

            import com.vikingsen.inject.work.WorkerModule;
            import dagger.Module;

            @WorkerModule
            @Module(includes = TwoModule.class)
            abstract class TestModule {}

            @Module
            abstract class TwoModule {}
        """
        )

        assertAbout(javaSource())
            .that(inputModule)
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining("@WorkerModule's @Module must include WorkerInject_TestModule")
            .`in`(inputModule).onLine(9)
    }

    @Test
    fun multipleModulesFails() {
        val inputModule1 = JavaFileObjects.forSourceString(
            "test.TestModule1", """
            package test;

            import com.vikingsen.inject.work.WorkerModule;
            import dagger.Module;

            @WorkerModule
            @Module(includes = WorkerInject_TestModule1.class)
            abstract class TestModule1 {}
        """
        )
        val inputModule2 = JavaFileObjects.forSourceString(
            "test.TestModule2", """
            package test;

            import com.vikingsen.inject.work.WorkerModule;
            import dagger.Module;

            @WorkerModule
            @Module(includes = WorkerInject_TestModule2.class)
            abstract class TestModule2 {}
        """
        )

        assertAbout(javaSources())
            .that(listOf(inputModule1, inputModule2))
            .processedWith(WorkerInjectProcessor())
            .failsToCompile()
            .withErrorContaining("Multiple @WorkerModule-annotated modules found.")
            .`in`(inputModule1).onLine(9)
            .and()
            .withErrorContaining("Multiple @WorkerModule-annotated modules found.")
            .`in`(inputModule2).onLine(9)
    }
}