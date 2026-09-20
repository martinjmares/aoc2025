package name.mjm.aoc.model.json;

import java.util.Objects;

public class JsonDouble extends JsonNumber {

  private final double value;

  public JsonDouble(double value) {
    this.value = value;
  }

  public double get() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    JsonDouble that = (JsonDouble) o;
    return Double.compare(value, that.value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toJsonString() {
    return String.valueOf(value);
  }

  @Override
  public String toJsonStringIndented(String baseIndentation, String incrementIndentation) {
    return toJsonString();
  }

  @Override
  public String toString() {
    return toJsonString();
  }
}
