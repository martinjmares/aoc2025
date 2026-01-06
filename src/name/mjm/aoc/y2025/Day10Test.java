package name.mjm.aoc.y2025;

import name.mjm.aoc.test.Assert;

import java.util.Arrays;

public class Day10Test {

  void testShift() {
    int one = 1;
    int two = one << 1;
    int four = one << 2;
    int four2 = two << 1;

    Assert.that(two).is(2);
    Assert.that(four).is(4);
    Assert.that(four2).is(4);
  }

  void testOr() {
    int zero = 0;
    int one = 1;
    int two = 2;
    int four = 4;

    Assert.that(one | four).is(5);
    Assert.that(zero | one).is(1);
    Assert.that(one | two | four).is(7);
  }

  void testArrayEq() {
    int[] a = new int[] {1, 2, 3, 4};
    int[] b = new int[] {1, 2, 3, 4};

    Assert.that(Arrays.equals(a, b)).is(true);
  }
}
