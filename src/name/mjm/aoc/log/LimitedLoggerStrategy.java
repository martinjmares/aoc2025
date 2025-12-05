package name.mjm.aoc.log;

interface LimitedLoggerStrategy {

  LimitedLoggerDecision canLog(boolean force);
}
