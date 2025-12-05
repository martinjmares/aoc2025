package name.mjm.aoc.log;

import name.mjm.aoc.Provider;
import static name.mjm.aoc.log.Logger.config;

public class LimitedLogger {

  private final LimitedLoggerStrategy strategy;
  private final String name;
  private LimitedLoggerDecision transaction;

  LimitedLogger(LimitedLoggerStrategy strategy, String name) {
    this.strategy = strategy;
    this.name = name;
  }

  public static LimitedLogger newCountBasedLogger(int countInterval, String name) {
    return new LimitedLogger(new CountStrategy(countInterval), name);
  }

  private StringBuilder prefix(LogLevel level, LimitedLoggerDecision decision) {
    StringBuilder sb = Logger.genericPrefix(name, level);
    sb.append(" <#").append(decision.iteration()).append(">: ");
    return sb;
  }

  private void log(Object message, LogLevel level, boolean force) {
    LoggingConfig config = config();
    if (config.canLog(level)) {
      // Decide
      LimitedLoggerDecision decision = null;
      if (transaction != null) {
        if (force && !decision.canLog()) {
          transaction = null;
        } else {
          decision = transaction;
        }
      }
      if (decision == null) {
        decision = strategy.canLog(force);
      }

      // Potentially log
      if (decision != null && decision.canLog()) {
        StringBuilder sb = prefix(level, decision);
        if (message instanceof Provider provider) {
          sb.append(provider.provide());
        } else {
          sb.append(message);
        }
        config.getOut().println(sb);
      }
    }
  }

  public void info(Object message) {
    log(message, LogLevel.INFO, false);
  }

  public void info(Provider<String> message) {
    log(message, LogLevel.INFO, false);
  }

  public void forceInfo(Object message) {
    log(message, LogLevel.INFO, true);
  }

  public void forceInfo(Provider<String> message) {
    log(message, LogLevel.INFO, true);
  }

  public void debug(Object message) {
    log(message, LogLevel.DEBUG, false);
  }

  public void debug(Provider<String> message) {
    log(message, LogLevel.DEBUG, false);
  }

  public void forceDebug(Object message) {
    log(message, LogLevel.DEBUG, true);
  }

  public void forceDebug(Provider<String> message) {
    log(message, LogLevel.DEBUG, true);
  }
}
