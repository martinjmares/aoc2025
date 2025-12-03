package name.mjm.aoc.model;

import java.util.Objects;

public class IntPair {

  private final int x;
  private final int y;
  private final int hashcode;

  public IntPair(int x, int y) {
    this.x = x;
    this.y = y;
    this.hashcode = Objects.hash(x, y);
  }

  public int x() {
    return x;
  }
  public int y() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    IntPair intPair = (IntPair) o;
    return x == intPair.x && y == intPair.y;
  }

  @Override
  public int hashCode() {
    return hashcode;
  }
}
