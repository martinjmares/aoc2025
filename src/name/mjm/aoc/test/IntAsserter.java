package name.mjm.aoc.test;

public class IntAsserter extends Asserter<Integer, IntAsserter> {

  public IntAsserter(Integer target, boolean negate) {
    super(target, negate);
  }

  @Override
  public IntAsserter not() {
    return new IntAsserter(target, !negate);
  }

  public IntAsserter isGt(int num) {
    check(t -> t != null && t > num, "Expecting '$target $not > $param' but it is not true!", num);
    return this;
  }

  public IntAsserter isGtOrEq(int num) {
    check(t -> t != null && t >= num, "Expecting '$target $not >= $param' but it is not true!", num);
    return this;
  }

  public IntAsserter isLt(int num) {
    check(t -> t != null && t < num, "Expecting '$target $not < $param' but it is not true!", num);
    return this;
  }

  public IntAsserter isLtOrEq(int num) {
    check(t -> t != null && t <= num, "Expecting '$target $not <= $param' but it is not true!", num);
    return this;
  }

  public IntAsserter isNegative() {
    check(t -> t != null && t < 0, "Expecting '$target' is $not negative but it is not true!");
    return this;
  }

  public IntAsserter isPositive() {
    check(t -> t != null && t > 0, "Expecting '$target' is $not positive but it is not true!");
    return this;
  }

  public IntAsserter isDividable(int num) {
    check(t -> (t != null) && (t % num) == 0, "Expecting '$target' is dividable by $param, but it is not true!", num);
    return this;
  }
}
