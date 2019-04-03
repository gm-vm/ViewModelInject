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
            .withErrorContaining("""
          Worker injection requires Context and WorkerParameters @Assisted parameters.
              Found:
                [android.content.Context c, androidx.work.WorkerParameters workerParameters]
              Expected:
                [android.content.Context context, androidx.work.WorkerParameters workerParameters]
          """.trimIndent())
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
            .withErrorContaining("""
          Worker injection requires Context and WorkerParameters @Assisted parameters.
              Found:
                [android.content.Context context, androidx.work.WorkerParameters params]
              Expected:
                [android.content.Context context, androidx.work.WorkerParameters workerParameters]
          """.trimIndent())
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

// TODO: 4/3/19 IMPLEMENT EXTRA TESTS
//    @Test fun constructorMissingAssistedParametersFails() {
//        val inputView = JavaFileObjects.forSourceString("test.TestView", """
//      package test;
//
//      import android.view.View;
//      import com.squareup.inject.inflation.InflationInject;
//
//      class TestView extends View {
//        @InflationInject
//        TestView(Long foo) {
//          super(null);
//        }
//      }
//    """)
//
//        assertAbout(javaSource())
//            .that(inputView)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("""
//          Inflation injection requires Context and AttributeSet @Assisted parameters.
//              Found:
//                []
//              Expected:
//                [android.content.Context context, android.util.AttributeSet attrs]
//          """.trimIndent())
//            .`in`(inputView).onLine(9)
//    }
//
//    @Test fun constructorExtraAssistedParameterFails() {
//        val inputView = JavaFileObjects.forSourceString("test.TestView", """
//      package test;
//
//      import android.content.Context;
//      import android.util.AttributeSet;
//      import android.view.View;
//      import com.squareup.inject.assisted.Assisted;
//      import com.squareup.inject.inflation.InflationInject;
//
//      class TestView extends View {
//        @InflationInject
//        TestView(@Assisted Context context, @Assisted AttributeSet attrs, @Assisted String hey, Long foo) {
//          super(context, attrs);
//        }
//      }
//    """)
//
//        assertAbout(javaSource())
//            .that(inputView)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("""
//          Inflation injection requires Context and AttributeSet @Assisted parameters.
//              Found:
//                [android.content.Context context, android.util.AttributeSet attrs, java.lang.String hey]
//              Expected:
//                [android.content.Context context, android.util.AttributeSet attrs]
//          """.trimIndent())
//            .`in`(inputView).onLine(12)
//    }
//
//    @Test fun constructorMissingContextFails() {
//        val inputView = JavaFileObjects.forSourceString("test.TestView", """
//      package test;
//
//      import android.util.AttributeSet;
//      import android.view.View;
//      import com.squareup.inject.assisted.Assisted;
//      import com.squareup.inject.inflation.InflationInject;
//
//      class TestView extends View {
//        @InflationInject
//        TestView(@Assisted AttributeSet attrs, Long foo) {
//          super(null, attrs);
//        }
//      }
//    """)
//
//        assertAbout(javaSource())
//            .that(inputView)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("""
//          Inflation injection requires Context and AttributeSet @Assisted parameters.
//              Found:
//                [android.util.AttributeSet attrs]
//              Expected:
//                [android.content.Context context, android.util.AttributeSet attrs]
//          """.trimIndent())
//            .`in`(inputView).onLine(11)
//    }
//
//    @Test fun constructorMissingAttributeSetFails() {
//        val inputView = JavaFileObjects.forSourceString("test.TestView", """
//      package test;
//
//      import android.content.Context;
//      import android.view.View;
//      import com.squareup.inject.assisted.Assisted;
//      import com.squareup.inject.inflation.InflationInject;
//
//      class TestView extends View {
//        @InflationInject
//        TestView(@Assisted Context context, Long foo) {
//          super(context, null);
//        }
//      }
//    """)
//
//        assertAbout(javaSource())
//            .that(inputView)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("""
//          Inflation injection requires Context and AttributeSet @Assisted parameters.
//              Found:
//                [android.content.Context context]
//              Expected:
//                [android.content.Context context, android.util.AttributeSet attrs]
//          """.trimIndent())
//            .`in`(inputView).onLine(11)
//    }
//
//    @Test fun constructorMissingProvidedParametersWarns() {
//        val inputView = JavaFileObjects.forSourceString("test.TestView", """
//      package test;
//
//      import android.content.Context;
//      import android.util.AttributeSet;
//      import android.view.View;
//      import com.squareup.inject.assisted.Assisted;
//      import com.squareup.inject.inflation.InflationInject;
//
//      class TestView extends View {
//        @InflationInject
//        TestView(@Assisted Context context, @Assisted AttributeSet attrs) {
//          super(context, attrs);
//        }
//      }
//    """)
//
//        assertAbout(javaSource())
//            .that(inputView)
//            .processedWith(InflationInjectProcessor())
//            .compilesWithoutError()
//            .withWarningContaining("Inflation injection requires at least one non-@Assisted parameter.")
//            .`in`(inputView).onLine(12)
//        // .and().generatesNoFiles()
//    }
//
//    @Test fun privateConstructorFails() {
//        val inputView = JavaFileObjects.forSourceString("test.TestView", """
//      package test;
//
//      import android.view.View;
//      import com.squareup.inject.assisted.Assisted;
//      import com.squareup.inject.inflation.InflationInject;
//
//      class TestView extends View {
//        @InflationInject
//        private TestView(@Assisted Context context, @Assisted AttributeSet attrs, Long foo) {
//          super(context, attrs);
//        }
//      }
//    """)
//
//        assertAbout(javaSource())
//            .that(inputView)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("@InflationInject constructor must not be private.")
//            .`in`(inputView).onLine(10)
//    }
//
//    @Test fun nestedPrivateTypeFails() {
//        val inputView = JavaFileObjects.forSourceString("test.TestView", """
//      package test;
//
//      import android.view.View;
//      import com.squareup.inject.assisted.Assisted;
//      import com.squareup.inject.inflation.InflationInject;
//
//      class Outer {
//        private static class TestView extends View {
//          @InflationInject
//          TestView(@Assisted Context context, @Assisted AttributeSet attrs, Long foo) {
//            super(context, attrs);
//          }
//        }
//      }
//    """)
//
//        assertAbout(javaSource())
//            .that(inputView)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("@InflationInject-using types must not be private")
//            .`in`(inputView).onLine(9)
//    }
//
//    @Test fun nestedNonStaticFails() {
//        val inputView = JavaFileObjects.forSourceString("test.TestView", """
//      package test;
//
//      import android.view.View;
//      import com.squareup.inject.assisted.Assisted;
//      import com.squareup.inject.inflation.InflationInject;
//
//      class Outer {
//        class TestView extends View {
//          @InflationInject
//          TestView(@Assisted Context context, @Assisted AttributeSet attrs, Long foo) {
//            super(context, attrs);
//          }
//        }
//      }
//    """)
//
//        assertAbout(javaSource())
//            .that(inputView)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("Nested @InflationInject-using types must be static")
//            .`in`(inputView).onLine(9)
//    }
//
//    @Test fun multipleInflationInjectConstructorsFails() {
//        val inputView = JavaFileObjects.forSourceString("test.TestView", """
//      package test;
//
//      import android.view.View;
//      import com.squareup.inject.assisted.Assisted;
//      import com.squareup.inject.inflation.InflationInject;
//
//      class TestView extends View {
//        @InflationInject
//        TestView(@Assisted Context context, @Assisted AttributeSet attrs, Long foo) {
//          super(context, attrs);
//        }
//
//        @InflationInject
//        TestView(@Assisted Context context, @Assisted AttributeSet attrs, String foo) {
//          super(context, attrs);
//        }
//      }
//    """)
//
//        assertAbout(javaSource())
//            .that(inputView)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("Multiple @InflationInject-annotated constructors found.")
//            .`in`(inputView).onLine(8)
//    }
//
//    @Test fun moduleWithoutModuleAnnotationFails() {
//        val moduleOne = JavaFileObjects.forSourceString("test.OneModule", """
//      package test;
//
//      import com.squareup.inject.inflation.InflationModule;
//
//      @InflationModule
//      abstract class OneModule {}
//    """)
//
//        assertAbout(javaSource())
//            .that(moduleOne)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("@InflationModule must also be annotated as a Dagger @Module")
//            .`in`(moduleOne).onLine(7)
//    }
//
//    @Test fun moduleWithNoIncludesFails() {
//        val moduleOne = JavaFileObjects.forSourceString("test.OneModule", """
//      package test;
//
//      import com.squareup.inject.inflation.InflationModule;
//      import dagger.Module;
//
//      @InflationModule
//      @Module
//      abstract class OneModule {}
//    """)
//
//        assertAbout(javaSource())
//            .that(moduleOne)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("@InflationModule's @Module must include InflationInject_OneModule")
//            .`in`(moduleOne).onLine(9)
//    }
//
//    @Test fun moduleWithoutIncludeFails() {
//        val moduleOne = JavaFileObjects.forSourceString("test.OneModule", """
//      package test;
//
//      import com.squareup.inject.inflation.InflationModule;
//      import dagger.Module;
//
//      @InflationModule
//      @Module(includes = TwoModule.class)
//      abstract class OneModule {}
//
//      @Module
//      abstract class TwoModule {}
//    """)
//
//        assertAbout(javaSource())
//            .that(moduleOne)
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("@InflationModule's @Module must include InflationInject_OneModule")
//            .`in`(moduleOne).onLine(9)
//    }
//
//    @Test fun multipleModulesFails() {
//        val moduleOne = JavaFileObjects.forSourceString("test.OneModule", """
//      package test;
//
//      import com.squareup.inject.inflation.InflationModule;
//      import dagger.Module;
//
//      @InflationModule
//      @Module(includes = AssistedInject_OneModule.class)
//      abstract class OneModule {}
//    """)
//        val moduleTwo = JavaFileObjects.forSourceString("test.TwoModule", """
//      package test;
//
//      import com.squareup.inject.inflation.InflationModule;
//      import dagger.Module;
//
//      @InflationModule
//      @Module(includes = AssistedInject_TwoModule.class)
//      abstract class TwoModule {}
//    """)
//
//        assertAbout(javaSources())
//            .that(listOf(moduleOne, moduleTwo))
//            .processedWith(InflationInjectProcessor())
//            .failsToCompile()
//            .withErrorContaining("Multiple @InflationModule-annotated modules found.")
//            .`in`(moduleOne).onLine(9)
//            .and()
//            .withErrorContaining("Multiple @InflationModule-annotated modules found.")
//            .`in`(moduleTwo).onLine(9)
//    }
}