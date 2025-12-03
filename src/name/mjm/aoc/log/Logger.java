package name.mjm.aoc.log;

import name.mjm.aoc.Provider;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.StringTokenizer;

public class Logger {

  public static final ScopedValue<LoggingConfig> LOGGING_CONFIG = ScopedValue.newInstance();

  public static final String F_SINGLE_SEPARATOR = "---------------------------------------------------------------------";
  public static final String F_DOUBLE_SEPARATOR = "=====================================================================";
  public static final String F_INDENT = "  ";

  private static final long START_TIME = System.currentTimeMillis();

  private final String name;

  public Logger(String name) {
    this.name = name;
  }

  public Logger(Class<?> clazz) {
    this(clazz.getSimpleName());
  }

  public Logger(Object o) {
    this(o.getClass());
  }

  public static String f_CAPTION(String s) {
    return f_CAPTION(s, F_INDENT);
  }

  public static String f_CAPTION(String s, String rowPrefix) {
    if (s == null) {
      s = "";
    }
    if (rowPrefix == null) {
      rowPrefix = "";
    }
    // Count lines
    StringTokenizer tokenizer = new StringTokenizer(s, "\n");
    int rows = tokenizer.countTokens();

    StringBuilder result = new StringBuilder((F_SINGLE_SEPARATOR.length() + 1) * rows);
    boolean first = true;
    while (tokenizer.hasMoreTokens()) {
      if (first) {
        first = false;
      } else {
        result.append('\n');
      }
      String row = tokenizer.nextToken().trim();
      result.append('|').append(rowPrefix).append(row);

      int contentLength = 1 + rowPrefix.length() + row.length();
      while (contentLength < (F_SINGLE_SEPARATOR.length() - 1)) {
        result.append(' ');
        contentLength++;
      }
      if (contentLength == (F_SINGLE_SEPARATOR.length() - 1)) {
        result.append("|");
      }
    }

    return result.toString();
  }

  private LoggingConfig config() {
    return LOGGING_CONFIG.orElse(LoggingConfig.DEFAULT);
  }

  private StringBuffer prefix(LoggingConfig config, LogLevel level) {
    StringBuffer sb = new StringBuffer();
    // Timestamp
    Duration sinceStart = Duration.of(System.currentTimeMillis() - START_TIME, ChronoUnit.MILLIS);
    long minutes = sinceStart.toMinutes();
    if (minutes > 0) {
      sb.append(minutes).append(':');
      sinceStart = sinceStart.minusMinutes(minutes);
    }
    long sec = sinceStart.toSeconds();
    if (sec < 10) {
      sb.append('0');
    }
    sb.append(sec).append('.');
    sinceStart = sinceStart.minusSeconds(sec);
    long millis = sinceStart.toMillis();
    if (millis < 10) {
      sb.append("00");
    }  else if (millis < 100) {
      sb.append('0');
    }
    sb.append(millis).append(' ');

    // Level
    sb.append('[').append(level.getShortcut()).append("] ");

    // Logger name
    sb.append(name);

    sb.append(": ");
    return sb;
  }

  private void log(Object message, Throwable throwable, LogLevel level) {
    LoggingConfig config = config();
    if (config.canLog(level)) {
      StringBuffer m = prefix(config, level).append(message);
      config.getOut().println(m);
      if (throwable != null) {
        throwable.printStackTrace(config.getOut());
      }
    }
  }

  private void log(Provider<String> messageProvider, LogLevel level) {
    LoggingConfig config = config();
    if (config.canLog(level)) {
      StringBuffer m = prefix(config, level).append(messageProvider.provide());
      config.getOut().println(m);
    }
  }

  public void debug(Object message, Throwable throwable) {
    log(message, throwable, LogLevel.DEBUG);
  }

  public void debug(Object message) {
    log(message, null, LogLevel.DEBUG);
  }

  public void debug(Provider<String> provider) {
    log(provider, LogLevel.DEBUG);
  }

  public void info(Object message, Throwable throwable) {
    log(message, throwable, LogLevel.INFO);
  }

  public void info(Object message) {
    log(message, null, LogLevel.INFO);
  }

  public void info(Provider<String> provider) {
    log(provider, LogLevel.INFO);
  }

  public void error(Object message, Throwable throwable) {
    log(message, throwable, LogLevel.ERROR);
  }

  public void error(Object message) {
    log(message, null, LogLevel.ERROR);
  }

  public void error(Provider<String> provider) {
    log(provider, LogLevel.ERROR);
  }

  public void println(Object message) {
    config().getOut().println(message);
  }

  public void println() {
    config().getOut().println();
  }

  public void print(Object message) {
    config().getOut().print(message);
  }

  public void printThrowable(Throwable throwable) {
    if (throwable != null) {
      throwable.printStackTrace(config().getOut());
    }
  }

}
