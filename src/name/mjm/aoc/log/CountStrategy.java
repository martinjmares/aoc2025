package name.mjm.aoc.log;

class CountStrategy implements LimitedLoggerStrategy {

  private int count = 0;
  private int sinceLastValid = 0;
  private final int logEvery;
  private final long startTime;

  public CountStrategy(int logEvery) {
    this.logEvery = logEvery;
    this.startTime = System.currentTimeMillis();
  }

  @Override
  public LimitedLoggerDecision canLog(boolean force) {
    count++;
    if (force || sinceLastValid >= logEvery) {
      sinceLastValid = 0;
      return new LimitedLoggerDecision(true, count, System.currentTimeMillis() - startTime);
    } else {
      sinceLastValid++;
      return LimitedLoggerDecision.NO;
    }
  }
}
