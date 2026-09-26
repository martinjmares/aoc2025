package name.mjm.aoc.model;

import java.util.Objects;

/**
 * Basically Integer but with construction from String.
 */
public class Size extends Number implements Comparable<Number> {

  private final int value;

  public Size(int value) {
    this.value = value;
  }

  public Size(String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public int intValue() {
    return value;
  }

  @Override
  public long longValue() {
    return value;
  }

  @Override
  public float floatValue() {
    return value;
  }

  @Override
  public double doubleValue() {
    return value;
  }

  @Override
  public int compareTo(Number o) {
    return value - o.intValue();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Size size = (Size) o;
    return value == size.value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
