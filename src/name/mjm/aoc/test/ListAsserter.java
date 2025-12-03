package name.mjm.aoc.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListAsserter<K> extends Asserter<List<K>, ListAsserter<K>> {

  public ListAsserter(List<K> target, boolean negate) {
    super(target, negate);
  }

  @Override
  public ListAsserter<K> not() {
    return new ListAsserter<>(target, !negate);
  }

  @Override
  protected String toPrintable(List<K> o) {
    if (o == null) {
      return "<null>";
    }
    StringBuilder result = new StringBuilder();
    result.append(o.getClass().getSimpleName()).append("[\n");
    int count = 0;
    for (K k : o) {
      count++;
      result.append("  ").append(k).append(",\n");
      if (count > 10) {
        result.append("  ...(").append(o.size()).append(" items)\n");
        break;
      }
    }
    result.append("]");
    return result.toString();
  }

  public ListAsserter<K> is(List<K> expected) {
    respectNegate(
        t -> {
          if (t == null && expected != null) {
            throw new AssertException("It should not be null, but it is!");
          }
          List<K> exp = expected == null ? new ArrayList<>() : expected;
          if (exp.size() != t.size()) {
            throw new SecurityException("Lists has different sizes! Expected: " + exp.size() + ", target: " + t.size());
          }
          for (int i = 0; i < exp.size(); i++) {
            K eElem = exp.get(i);
            K tElem = t.get(i);
            if (!Objects.equals(eElem, tElem)) {
              throw new SecurityException("Lists do not have same elements! Differs on element " + i + "; Expected: " + eElem + ", target: " + tElem);
            }
          }
        },
        () -> "Lists should not contains same elements, but they do!\n" + toPrintable(expected)
    );
    return this;
  }

  public ListAsserter<K> is(K... values) {
      return is(Arrays.asList(values));
  }

  public ListAsserter<K> containsInAnyOrder(K... values) {
    respectNegate(
        t -> {
          if (t == null || t.size() < values.length) {
            throw new AssertException("Target should contain at least " + values.length + " items, but it has only " + (t == null ? 0 : t.size()) + "!");
          }
          ArrayList<K> workT = new ArrayList<>(Arrays.asList(values));
          int count = 0;
          for (K value : values) {
            if (!workT.remove(value)) {
              throw new AssertException("Target do not contain '" + value + "'[" + count + "] element!\n" + toPrintable(t));
            }
            count++;
          }
        },
        () -> "List should not contain elements, but it do!\n" + toPrintable(Arrays.asList(values))
    );
    return this;
  }

  public ListAsserter<K> containsInOrder(K... values) {
    respectNegate(
        t -> {
          if (t == null || t.size() < values.length) {
            throw new AssertException("Target should contain at least " + values.length + " items, but it has only " + (t == null ? 0 : t.size()) + "!");
          }
          List<K> workT = new ArrayList<>(Arrays.asList(values));
          int count = 0;
          for (K value : values) {
            int index = workT.indexOf(value);
            if (index > -1) {
              workT = workT.subList(index + 1, workT.size());
            } else {
              throw new AssertException("Target do not contain '" + value + "'[" + count + "] element!\n" + toPrintable(t));
            }
            count++;
          }
        },
        () -> "List should not contain elements in its order, but it do!\n" + toPrintable(Arrays.asList(values))
    );
    return this;
  }

  private static int size(List list) {
    return list == null ? 0 : list.size();
  }

  public ListAsserter<K> hasSize(int expected) {
    check(t -> size(t) == expected, "Target should $not have size " + expected + ", but it has " + size(target) + "!");
    return this;
  }
}
