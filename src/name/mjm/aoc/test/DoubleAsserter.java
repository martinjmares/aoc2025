package name.mjm.aoc.test;

public class DoubleAsserter  extends Asserter<Double, DoubleAsserter> {

  public DoubleAsserter(Double target, boolean negate) {
    super(target, negate);
  }

  @Override
  public DoubleAsserter not() {
    return new DoubleAsserter(target, !negate);
  }

  public DoubleAsserter isGt(double num) {
    check(t -> t != null && t > num, "Expecting '$target $not > $param' but it is not true!", num);
    return this;
  }

  public DoubleAsserter isGtOrEq(double num) {
    check(t -> t != null && t >= num, "Expecting '$target $not >= $param' but it is not true!", num);
    return this;
  }

  public DoubleAsserter isLt(double num) {
    check(t -> t != null && t < num, "Expecting '$target $not < $param' but it is not true!", num);
    return this;
  }

  public DoubleAsserter isLtOrEq(double num) {
    check(t -> t != null && t <= num, "Expecting '$target $not <= $param' but it is not true!", num);
    return this;
  }

  public DoubleAsserter isNegative() {
    check(t -> t != null && t < 0, "Expecting '$target' is $not negative but it is not true!");
    return this;
  }

  public DoubleAsserter isPositive() {
    check(t -> t != null && t > 0, "Expecting '$target' is $not positive but it is not true!");
    return this;
  }

}
