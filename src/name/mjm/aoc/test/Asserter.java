package name.mjm.aoc.test;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

abstract class Asserter<T, K> {

  protected final boolean negate;
  protected final T target;

  protected Asserter(T target, boolean negate) {
    this.target = target;
    this.negate = negate;
  }

  protected String toPrintable(T o) {
    if (o  == null) {
      return "<null>";
    }
    return o.toString();
  }

  protected void check(Predicate<T> predicate, String message, Object param) {
    if (negate) {
      predicate = predicate.negate();
    }
    if (!predicate.test(target)) {
      message = message.replace("$not ", negate ? "not ": "");
      message = message.replace("$not-", negate ? "not-": "");
      message = message.replace("$not", negate ? "not": "");
      if (message.contains("$target")) {
        message = message.replace("$target", toPrintable(target));
      }
      if (message.contains("$param")) {
        message = message.replace("$param", param == null ? "<null>" : param.toString());
      }
      throw new AssertException(message);
    }
  }

  protected void respectNegate(Consumer<T> consumer, Callable<String> negateMessage) {
    if (negate) {
      try {
        consumer.accept(target);
      } catch (AssertException e) {
        return;
      }
      try {
        throw new AssertException(negateMessage.call());
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      consumer.accept(target);
    }
  }

  protected void check(Predicate<T> predicate, String message) {
    check(predicate, message, null);
  }

  public K isNull() {
    check(Objects::isNull, "Expecting $not <null> but is $target!");
    return (K) this;
  }

  public K isNotNull() {
    check(Objects::nonNull, "Expecting $not <not-null> but is <$not-null>!");
    return (K) this;
  }

  public K is(T expected) {
    if (expected == null) {
      isNull();
    } else {
      check(expected::equals, "Expecting $not equals to $param but it was $target!", expected);
    }
    return (K) this;
  }

  public abstract K not();

}
