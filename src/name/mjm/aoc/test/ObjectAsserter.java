package name.mjm.aoc.test;

public class ObjectAsserter extends Asserter<Object, ObjectAsserter> {

  public ObjectAsserter(Object target, boolean negate) {
    super(target, negate);
  }

  @Override
  public ObjectAsserter not() {
    return new ObjectAsserter(target, !negate);
  }

  public ObjectAsserter isOfType(Class<?> type) {
    check(o -> o != null && o.getClass().isAssignableFrom(type), "Expecting to $not be of type " + type + " but it is not true!");
    return this;
  }
}
