package name.mjm.aoc.algebra;

import name.mjm.aoc.test.Assert;

public class AlgebraTest {

  void testNsd1() {
    int nsd = Algebra.nsd(5 * 3, 5 * 5);

    Assert.that(nsd).is(5);
  }

  void testNsdWithNegative() {
    int nsd = Algebra.nsd((-5) * 3, 5 * 5);

    Assert.that(nsd).is(-5);
  }

  void testNsdWithNegatives() {
    int nsd = Algebra.nsd((-5) * 3, (-5) * 5);

    Assert.that(nsd).is(-5);
  }

  void testNsn() {
    int nsn = Algebra.nsn(15, 10);

    Assert.that(nsn).is(30);
  }

  void testNsnNegative() {
    int nsn = Algebra.nsn(-15, 10);

    Assert.that(nsn).is(-30);
  }
}
