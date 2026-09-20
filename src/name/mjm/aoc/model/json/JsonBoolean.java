package name.mjm.aoc.model.json;

import java.util.Objects;

public class JsonBoolean implements JsonElement {
  private final boolean value;

  public JsonBoolean(boolean value) {
    this.value = value;
  }

  public boolean get() {
    return value;
  }

  @Override
  public String toString() {
    return toJsonString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    JsonBoolean that = (JsonBoolean) o;
    return value == that.value;
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
}
