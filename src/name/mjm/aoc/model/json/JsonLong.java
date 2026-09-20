package name.mjm.aoc.model.json;

import java.util.Objects;

public class JsonLong extends JsonNumber {

  private final long value;

  public JsonLong(long value) {
    this.value = value;
  }

  public long get() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    JsonLong jsonLong = (JsonLong) o;
    return value == jsonLong.value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return toJsonString();
  }

  @Override
  public String toJsonString() {
    return String.valueOf(value);
  }

  @Override
  public String toJsonStringIndented(String baseIndentation, String incrementIndentation) {
    return toJsonString();
  }
}
