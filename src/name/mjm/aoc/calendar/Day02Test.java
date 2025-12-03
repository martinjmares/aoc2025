package name.mjm.aoc.calendar;

import name.mjm.aoc.test.Assert;

public class Day02Test {

  final Day02 target = new Day02();

  void testIncrement1_3() {
    long increment = target.createIncrement(1, 3);
    Assert.that(increment).is(111L);
  }

  void testIncrement2_3() {
    long increment = target.createIncrement(2, 3);
    Assert.that(increment).is(10101L);
  }

  void testIncrement3_2() {
    long increment = target.createIncrement(3, 2);
    Assert.that(increment).is(1001L);
  }
}
