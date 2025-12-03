package name.mjm.aoc.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import name.mjm.aoc.Counter;
import name.mjm.aoc.Disabled;
import name.mjm.aoc.log.LogLevel;
import name.mjm.aoc.log.Logger;
import name.mjm.aoc.log.LoggingConfig;
import name.mjm.aoc.log.LoggingConfig.Builder;
import name.mjm.aoc.test.TestManagerConfig.LogsConfig;

public class TestManager implements Runnable {

  private final Logger logger = new Logger(this);

  private final TestManagerConfig config;
  private final List<Class> testClasses;

  public TestManager(TestManagerConfig config, Set<Class> allClasses) {
    this.config = config;
    this.testClasses = allClasses.stream()
        .filter(clazz -> clazz.getSimpleName().matches(".+Test") || clazz.isAnnotationPresent(Test.class))
        .sorted(Comparator.comparing(Class::getName))
        .toList();
  }


  private TestStatistics executeTest(TestClassStatistics classStatistics, Method testMethod) {
    long startTime = System.currentTimeMillis();
    if (config.getLogsConfig() == LogsConfig.ALL) {
      // More verbose
      logger.println("");
      logger.println(Logger.F_SINGLE_SEPARATOR);
      logger.println("TEST: " + classStatistics.testClass.getName() + "." + testMethod.getName() + "()");
      logger.println("");
    } else {
      logger.print(Logger.F_INDENT + testMethod.getName() + "()");
    }
    try {
      testMethod.setAccessible(true);
      Constructor constructor = classStatistics.testClass.getConstructor();
      constructor.setAccessible(true);
      Object testObject = constructor.newInstance();

      boolean disabled = testMethod.isAnnotationPresent(Disabled.class);
      if (disabled) {
        if (config.getLogsConfig() == LogsConfig.ALL) {
          logger.println("");
          logger.println(">>>>> Test result: DISABLED");
        } else {
          logger.println(" DISABLED");
        }
        return new TestStatistics(testMethod, TestResult.DISABLED, 0L);
      }
      if (testMethod.getParameterCount() > 0) {
        throw new Exception("We don't support test methods with parameters");
      }

      // Prepare logging configuration
      Builder loggingConfigBuilder = LoggingConfig.builder();
      loggingConfigBuilder.minLevel(LogLevel.DEBUG);
      ByteArrayOutputStream outputStream = null;
      if (config.getLogsConfig() == LogsConfig.FAILING) {
        outputStream = new ByteArrayOutputStream();
        loggingConfigBuilder.out(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
      }

      // Run the test
      AtomicReference<Throwable> throwableRef = new AtomicReference<>();
      Thread t = Thread.ofVirtual()
          .name("TEST-"  + testMethod.getName())
          .start(() -> {
            ScopedValue.where(Logger.LOGGING_CONFIG, loggingConfigBuilder.build())
                .run(() -> {
                  try {
                    testMethod.invoke(testObject);
                  } catch (InvocationTargetException ite) {
                    if (ite.getCause() != null) {
                      throwableRef.set(ite.getCause());
                    } else {
                      throwableRef.set(ite);
                    }
                  } catch (Throwable throwable) {
                    throwableRef.set(throwable);
                  }
                });
          });

      // Join and timeout
      if (!t.join(config.getTimeout())) {
        if (config.getLogsConfig() == LogsConfig.ALL) {
          logger.println("");
          logger.println(">>>>> Test result: TIMEOUT");
        } else {
          logger.println(" TIMEOUT");
        }
        t.interrupt();
        return new TestStatistics(testMethod, TestResult.TIMEOUT, System.currentTimeMillis() - startTime);
      }

      // FAILED or ERROR
      if (throwableRef.get() != null) {
        if (config.getLogsConfig() == LogsConfig.ALL) {
          logger.println("");
          logger.println(">>>>> Test result: FAILED");
        } else {
          logger.println(" FAILED");
          if (outputStream != null) {
            logger.println("Logs:");
            logger.println(outputStream.toString(StandardCharsets.UTF_8));
          }
          logger.println("^^^^^ " + classStatistics.testClass.getName() + "." + testMethod.getName() + " FAILED");
        }
        logger.printThrowable(throwableRef.get());
        logger.println("");
        return new TestStatistics(testMethod, TestResult.FAILURE, System.currentTimeMillis() - startTime, throwableRef.get());
      }

      // OK
      if (config.getLogsConfig() == LogsConfig.ALL) {
        logger.println("");
        logger.println(">>>>> Test result: OK");
      } else {
        logger.println(" OK");
      }
      return new TestStatistics(testMethod, TestResult.SUCCESS, System.currentTimeMillis() - startTime);

    } catch (Exception e) {
      if (config.getLogsConfig() == LogsConfig.ALL) {
        logger.println("");
        logger.println(">>>>> Test result: ERROR");
      } else {
        logger.println(" ERROR: " + e.getMessage());
      }
      logger.printThrowable(e);
      return new TestStatistics(testMethod, TestResult.ERROR, System.currentTimeMillis() - startTime, e);
    }
  }

  private TestClassStatistics executeTestsOnClass(Class clazz) {
    TestClassStatistics stats = new TestClassStatistics(clazz);
    stats.disabled = clazz.isAnnotationPresent(Disabled.class);
    logger.println(clazz.getName() + (stats.disabled ? " (disabled)" : ""));

    Method[] methods = clazz.getDeclaredMethods();
    for (Method method : methods) {
      if (method.getName().startsWith("test") || method.isAnnotationPresent(Test.class)) {
        if (config.canRunTestMethod(clazz, method)) {
          TestStatistics testStatistics = executeTest(stats, method);
          stats.testStatistics.add(testStatistics);
        }
      }
    }
    return stats;
  }

  @Override
  public void run() {
    // Print headers
    logger.println("");
    logger.println(Logger.F_DOUBLE_SEPARATOR);
    logger.println(Logger.f_CAPTION("TESTING"));
    logger.println(Logger.f_CAPTION(config.toString()));
    logger.println(Logger.F_DOUBLE_SEPARATOR);
    logger.println("");

    // Execute
    List<TestClassStatistics> statistics = new ArrayList<>();
    for (Class testClass : testClasses) {
      if (config.canRunThisClass(testClass)) {
        TestClassStatistics stats = executeTestsOnClass(testClass);
        statistics.add(stats);
      }
    }

    // Print results
    logger.println("");
    logger.println(Logger.F_SINGLE_SEPARATOR);
    logger.println(Logger.f_CAPTION("TEST RESULTS:"));
    logger.println(Logger.f_CAPTION("([OK]; [F]ail; [E]rror; [T]ime[O]out; [D]isabled)", Logger.F_INDENT));
    logger.println(Logger.F_SINGLE_SEPARATOR);
    logger.println("");
    for (TestClassStatistics stat : statistics) {
      logger.println(stat);
      for (TestStatistics tStat : stat.testStatistics) {
        switch (tStat.result) {
          case FAILURE -> logger.println(Logger.F_INDENT + "failed: " + tStat.method.getName() + "()");
          case ERROR -> logger.println(Logger.F_INDENT + "error: " + tStat.method.getName() + "()");
          case TIMEOUT ->  logger.println(Logger.F_INDENT + "timeout: " + tStat.method.getName() + "()");
          default -> {}
        }
      }
    }
  }

  private static class TestClassStatistics {
    Class testClass;
    boolean disabled = false;
    List<TestStatistics> testStatistics = new ArrayList<>();

    public TestClassStatistics(Class testClass) {
      this.testClass = testClass;
    }

    public String toString() {
      StringBuffer result = new StringBuffer();
      Map<TestResult, Counter> counters = new EnumMap<>(TestResult.class);
      for (TestStatistics stats : testStatistics) {
        counters.computeIfAbsent(stats.result, k -> new Counter()).inc();
      }
      result.append(testClass.getName()).append(' ');
      String statStr = Arrays
          .stream(TestResult.values())
          .map(tr -> tr.getShortcut() + ": " + counters.getOrDefault(
              tr,
              new Counter()
          ))
          .collect(Collectors.joining(", ", "[", "]"));
      result.append(statStr);

      return result.toString();
    }
  }

  private static class TestStatistics {
    Method method;
    TestResult result;
    long duration;
    Throwable error;

    TestStatistics(Method method, TestResult result, long duration, Throwable error) {
      this.method = method;
      this.result = result;
      this.duration = duration;
      this.error = error;
    }

    public TestStatistics(Method method, TestResult result, long duration) {
      this(method, result, duration, null);
    }
  }

  enum TestResult {
    SUCCESS("OK"),
    FAILURE("F"),
    ERROR("E"),
    TIMEOUT("TO"),
    DISABLED("D");

    private final String shortcut;

    TestResult(String shortcut) {
      this.shortcut = shortcut;
    }

    public String getShortcut() {
      return shortcut;
    }
  }
}
