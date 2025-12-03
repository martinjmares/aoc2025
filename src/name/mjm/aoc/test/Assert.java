package name.mjm.aoc.test;

import java.util.List;

public class Assert {

  public static LongAsserter that(long target) {
    return new LongAsserter(target, false);
  }

  public static IntAsserter that(int target) {
    return new IntAsserter(target, false);
  }

  public static DoubleAsserter that(double target) {
    return new DoubleAsserter(target, false);
  }

  public static BooleanAsserter that(boolean target) {
    return new BooleanAsserter(target, false);
  }

  public static CharSequenceAsserter that(CharSequence target) {
    return new CharSequenceAsserter(target, false);
  }

  public static ListAsserter that(List target) {
    return new ListAsserter(target, false);
  }

  public static ObjectAsserter that(Object target) {
    return new ObjectAsserter(target, false);
  }

}
