package name.mjm.aoc;

import java.util.Objects;

public class Counter implements Comparable<Counter> {

  private long value;

  public Counter(long value) {
    this.value = value;
  }

  public Counter() {
    this(0);
  }

  public long get() {
    return value;
  }

  public void set(long value) {
    this.value = value;
  }

  public void inc() {
    value++;
  }

  public void inc(long value) {
    this.value += value;
  }

  public void dec() {
    value--;
  }

  public void dec(long value) {
    this.value -= value;
  }

  @Override
  public int compareTo(Counter o) {
    if (o == null) {
      return 1;
    }
    return Long.compare(value, o.value);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Counter counter = (Counter) o;
    return value == counter.value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  public String toString() {
    return String.valueOf(value);
  }
}
