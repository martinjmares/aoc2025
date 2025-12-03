package name.mjm.aoc.test;

public class BooleanAsserter extends Asserter<Boolean, BooleanAsserter> {

  public BooleanAsserter(Boolean target, boolean negate) {
    super(target, negate);
  }

  @Override
  public BooleanAsserter not() {
    return new BooleanAsserter(target, !negate);
  }

  public BooleanAsserter isTrue() {
    check(Boolean.TRUE::equals, "Expecting that '$target' is $not true!", target);
    return this;
  }

  public BooleanAsserter isFalse() {
    check(Boolean.FALSE::equals, "Expecting that '$target' is $not true!", target);
    return this;
  }

}
