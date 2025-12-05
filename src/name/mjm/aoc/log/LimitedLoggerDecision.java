package name.mjm.aoc.log;

record LimitedLoggerDecision(boolean canLog, int iteration, long sinceStart) {

  final static LimitedLoggerDecision NO = new LimitedLoggerDecision(false, 0, 0);
}
