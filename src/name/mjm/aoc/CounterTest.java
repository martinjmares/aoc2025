package name.mjm.aoc;

import name.mjm.aoc.log.Logger;
import name.mjm.aoc.test.Assert;

public class CounterTest {

  private static Logger logger = new Logger(CounterTest.class);

  void testInc() {
    logger.debug("testing inc");
    Counter target = new Counter();
    target.inc();
    Assert.that(target.get()).is(1L);
  }

  void testDec() {
    logger.debug("testing dec");
    Counter target = new Counter(2);
    target.dec(5);
    Assert.that(target.get()).is(-3L);
  }

}
