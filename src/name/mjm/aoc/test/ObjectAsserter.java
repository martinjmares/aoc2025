package name.mjm.aoc.test;

public class ObjectAsserter extends Asserter<Object, ObjectAsserter> {

  public ObjectAsserter(Object target, boolean negate) {
    super(target, negate);
  }

  @Override
  public ObjectAsserter not() {
    return new ObjectAsserter(target, !negate);
  }
}
