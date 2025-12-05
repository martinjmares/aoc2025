package name.mjm.aoc.calendar;

import name.mjm.aoc.model.Interval;
import name.mjm.aoc.test.Assert;

import java.util.List;

public class Day05Test {

  private final Day05 target = new Day05();

  void testSmashLeftDis() {
    var a = new Interval(10, 20);
    var b = new Interval(3, 5);

    Day05.SmashedInterval result = target.smashFirst(List.of(a, b));

    Assert.that(result.interval()).is(a);
    Assert.that(result.rest()).hasSize(1);
    Assert.that(result.rest()).containsInAnyOrder(b);
  }

  void testSmashLeftJoin() {
    var a = new Interval(10, 20);
    var b = new Interval(3, 15);

    Day05.SmashedInterval result = target.smashFirst(List.of(a, b));

    Assert.that(result.interval()).is(new Interval(3, 20));
    Assert.that(result.rest()).hasSize(0);
  }

  void testSmashInside() {
    var a = new Interval(10, 20);
    var b = new Interval(13, 15);

    Day05.SmashedInterval result = target.smashFirst(List.of(a, b));

    Assert.that(result.interval()).is(a);
    Assert.that(result.rest()).hasSize(0);
  }

  void testSmashRightJoin() {
    var a = new Interval(10, 20);
    var b = new Interval(15, 25);

    Day05.SmashedInterval result = target.smashFirst(List.of(a, b));

    Assert.that(result.interval()).is(new Interval(10, 25));
    Assert.that(result.rest()).hasSize(0);
  }

  void testSmashRightDist() {
    var a = new Interval(10, 20);
    var b = new Interval(22, 25);

    Day05.SmashedInterval result = target.smashFirst(List.of(a, b));

    Assert.that(result.interval()).is(a);
    Assert.that(result.rest()).hasSize(1);
    Assert.that(result.rest()).containsInAnyOrder(b);
  }

  void testSmashAround() {
    var a = new Interval(10, 20);
    var b = new Interval(5, 25);

    Day05.SmashedInterval result = target.smashFirst(List.of(a, b));

    Assert.that(result.interval()).is(b);
    Assert.that(result.rest()).hasSize(0);
  }

  void testSmashExact() {
    var a = new Interval(10, 20);
    var b = new Interval(10, 20);

    Day05.SmashedInterval result = target.smashFirst(List.of(a, b));

    Assert.that(result.interval()).is(a);
    Assert.that(result.rest()).hasSize(0);
  }

  void testSmashLeftTouch() {
    var a = new Interval(10, 20);
    var b = new Interval(5, 10);

    Day05.SmashedInterval result = target.smashFirst(List.of(a, b));

    Assert.that(result.interval()).is(new Interval(5, 20));
    Assert.that(result.rest()).hasSize(0);
  }

  void testSmashRightTouch() {
    var a = new Interval(10, 20);
    var b = new Interval(20, 30);

    Day05.SmashedInterval result = target.smashFirst(List.of(a, b));

    Assert.that(result.interval()).is(new Interval(10, 30));
    Assert.that(result.rest()).hasSize(0);
  }
}
