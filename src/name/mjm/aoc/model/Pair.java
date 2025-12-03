package name.mjm.aoc.model;

import java.util.Objects;

public class Pair<X, Y> {

  private final X x;
  private final Y y;

  public Pair(X x, Y y) {
    this.x = x;
    this.y = y;
  }

  public X x() {
    return x;
  }

  public Y y() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equals(x, pair.x) && Objects.equals(y, pair.y);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}
