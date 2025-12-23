package name.mjm.aoc.algebra;

import name.mjm.aoc.test.Assert;

public class RationalNumberTest {

  void testAdd() {
    RationalNumber a = new RationalNumber(5, 8);
    RationalNumber b = new RationalNumber(4, 12);
    RationalNumber target = a.add(b);

    RationalNumber expected = new RationalNumber(23, 24);
    Assert.that(target).is(expected);
  }

  void testAddNegToZero() {
    RationalNumber a = RationalNumber.ZERO;
    RationalNumber b = new RationalNumber(-1, 1);
    RationalNumber target = a.add(b);

    RationalNumber expected = new RationalNumber(-1, 1);
    Assert.that(target).is(expected);
  }

  void testAddInt() {
    RationalNumber a = new RationalNumber(5, 8);
    int b = 4;
    RationalNumber target = a.add(b);

    RationalNumber expected = new RationalNumber(37, 8);
    Assert.that(target).is(expected);
  }

  void testDivideByInt() {
    RationalNumber a = new RationalNumber(5, 8);
    RationalNumber target = a.divide(2);

    RationalNumber expected = new RationalNumber(5, 16);
    Assert.that(target).is(expected);
  }

  void testSimplify() {
    RationalNumber a = new RationalNumber(11 * 5, 11 * 8);
    RationalNumber target = a.simplify();

    RationalNumber expected = new RationalNumber(5, 8);
    Assert.that(target).is(expected);
  }

  void testSimplifyNegOne() {
    RationalNumber a = new RationalNumber(-1, 1);
    RationalNumber target = a.simplify();

    RationalNumber expected = new RationalNumber(-1, 1);
    Assert.that(target).is(expected);
  }
}
