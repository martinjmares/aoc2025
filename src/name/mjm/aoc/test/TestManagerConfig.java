package name.mjm.aoc.test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import name.mjm.aoc.model.Pair;

public class TestManagerConfig {

  private final Set<String> runTheseTestClasses;
  private final Set<Pair<String, String>> runTheseTestMethods;
  private final LogsConfig logsConfig;
  private final Duration timeout;

  public TestManagerConfig(
      Set<String> runTheseTestClasses,
      Set<Pair<String, String>> runTheseTestMethods,
      LogsConfig logsConfig,
      Duration timeout
  ) {
    this.runTheseTestClasses = runTheseTestClasses;
    this.runTheseTestMethods = runTheseTestMethods;
    this.logsConfig = logsConfig;
    this.timeout = timeout;
  }

  boolean runAllTests() {
    return runTheseTestClasses.isEmpty() && runTheseTestMethods.isEmpty();
  }

  public static Builder builder() {
    return new Builder();
  }

  public enum LogsConfig {
    FAILING, ALL;
  }

  public LogsConfig getLogsConfig() {
    return logsConfig;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public boolean canRunThisClass(Class clazz) {
    if (runAllTests()) {
      return true;
    }
    String simpleName = clazz.getSimpleName();
    String name = clazz.getName();
    if (runTheseTestClasses.contains(simpleName) || runTheseTestClasses.contains(name)) {
      return true;
    }
    for (Pair<String, String> pair : runTheseTestMethods) {
      if (simpleName.equals(pair.x()) || name.equals(pair.x())) {
        return true;
      }
    }
    return false;
  }

  public boolean canRunTestMethod(Class clazz, Method method) {
    if (runAllTests()) {
      return true;
    }
    String simpleName = clazz.getSimpleName();
    String name = clazz.getName();
    if (runTheseTestClasses.contains(simpleName) || runTheseTestClasses.contains(name)) {
      return true;
    }
    Pair<String, String> simplePair = new Pair<>(simpleName, method.getName());
    Pair<String, String> namePair = new Pair<>(name, method.getName());
    return runTheseTestMethods.contains(simplePair) || runTheseTestMethods.contains(namePair);
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    switch (logsConfig) {
      case FAILING -> result.append("Log when fail");
      case ALL -> result.append("Log all");
    }
    result.append("; ");
    if (runAllTests()) {
      result.append("Run all tests");
    } else {
      result.append("Run: ");
      result.append(
          runTheseTestClasses.stream()
            .collect(Collectors.joining(", "))
      );
      if (!runTheseTestClasses.isEmpty() && !runTheseTestMethods.isEmpty()) {
        result.append(", ");
      }
      result.append(
          runTheseTestMethods.stream()
              .map(pair -> pair.x() + "." + pair.y())
              .collect(Collectors.joining(", "))
      );
    }
    return result.toString();
  }

  public static class Builder {

    private boolean doNotAddMoreTests = false;
    private final Set<String> runTheseTestClasses = new HashSet<>();
    private final Set<Pair<String, String>> runTheseTestMethods = new HashSet<>();
    private LogsConfig logsConfig = LogsConfig.FAILING;
    private Duration timeout = Duration.ofSeconds(20);

    private  Builder() {
    }

    public Builder addTestToRun(String testName) {
      if (!doNotAddMoreTests) {
        if (testName != null && !testName.isBlank()) {
          testName = testName.trim();
          int index = testName.indexOf('.');
          if (index != -1) {
            runTheseTestMethods.add(new Pair<>(testName.substring(0, index), testName.substring(index + 1)));
          } else {
            runTheseTestClasses.add(testName);
          }
        }
      }
      return this;
    }

    public Builder showLogs(LogsConfig logsConfig) {
      this.logsConfig = logsConfig;
      return this;
    }

    public Builder runAllTests() {
      doNotAddMoreTests = true;
      runTheseTestClasses.clear();
      runTheseTestMethods.clear();
      return this;
    }

    public Builder timeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public Builder timeoutSeconds(int timeoutSeconds) {
      this.timeout = Duration.ofSeconds(timeoutSeconds);
      return this;
    }

    public TestManagerConfig build() {
      return new TestManagerConfig(runTheseTestClasses, runTheseTestMethods, logsConfig, timeout);
    }

  }

}
