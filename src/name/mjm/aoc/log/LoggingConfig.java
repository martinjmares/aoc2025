package name.mjm.aoc.log;

import java.io.PrintStream;

public class LoggingConfig {

  public static final LoggingConfig DEFAULT = new LoggingConfig(LogLevel.INFO, System.out);

  private final LogLevel minLevel;
  private final PrintStream out;

  public LoggingConfig(LogLevel minLevel, PrintStream out) {
    this.minLevel = minLevel == null ? LogLevel.INFO : minLevel;
    this.out = out;
  }

  LogLevel getMinLevel() {
    return minLevel;
  }

  PrintStream getOut() {
    return out;
  }

  boolean canLog(LogLevel level) {
    return level != null && level.getLevel() >= minLevel.getLevel();
  }

  public static Builder builder() {
    return new Builder(Logger.LOGGING_CONFIG.orElse(LoggingConfig.DEFAULT));
  }

  public static class Builder {
    private LogLevel minLevel = LogLevel.INFO;
    private PrintStream out = System.out;

    private Builder(LoggingConfig loggingConfig) {
      if (loggingConfig.minLevel == null) {
        this.minLevel = loggingConfig.minLevel;
        this.out = loggingConfig.out;
      }
    }

    private Builder() {
      this(null);
    }

    public Builder minLevel(LogLevel minLevel) {
      this.minLevel = minLevel;
      return this;
    }

    public Builder out(PrintStream out) {
      this.out = out;
      return this;
    }

    public LoggingConfig build() {
      return new LoggingConfig(minLevel, out);
    }

  }

}
