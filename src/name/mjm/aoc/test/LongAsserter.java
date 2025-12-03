package name.mjm.aoc.test;

public class LongAsserter extends Asserter<Long, LongAsserter> {

  LongAsserter(Long target, boolean negate) {
    super(target, negate);
  }

  @Override
  public LongAsserter not() {
    return new LongAsserter(target, !negate);
  }

  public LongAsserter isGt(long num) {
    check(t -> t != null && t > num, "Expecting '$target $not > $param' but it is not true!", num);
    return this;
  }

  public LongAsserter isGtOrEq(long num) {
    check(t -> t != null && t >= num, "Expecting '$target $not >= $param' but it is not true!", num);
    return this;
  }

  public LongAsserter isLt(long num) {
    check(t -> t != null && t < num, "Expecting '$target $not < $param' but it is not true!", num);
    return this;
  }

  public LongAsserter isLtOrEq(long num) {
    check(t -> t != null && t <= num, "Expecting '$target $not <= $param' but it is not true!", num);
    return this;
  }

  public LongAsserter isNegative() {
    check(t -> t != null && t < 0, "Expecting '$target' is $not negative but it is not true!");
    return this;
  }

  public LongAsserter isPositive() {
    check(t -> t != null && t > 0, "Expecting '$target' is $not positive but it is not true!");
    return this;
  }

  public LongAsserter isDividable(long num) {
    check(t -> (t != null) && (t % num) == 0, "Expecting '$target' is dividable by $param, but it is not true!", num);
    return this;
  }
}
