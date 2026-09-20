package name.mjm.aoc.model.json;

import java.util.Objects;

public class JsonString implements JsonElement {

  static final String ESCAPABLES = "\\\n\r\b\t\"";

  private final String value;

  public JsonString(String value) {
    this.value = value;
  }

  public String get() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    JsonString that = (JsonString) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "\"" + value + "\"";
  }

  @Override
  public String toJsonString() {
    StringBuilder result = new StringBuilder(value.length() + 12);
    result.append('"');
    for (char c : value.toCharArray()) {
      if (ESCAPABLES.indexOf(c) >= 0) {
        result.append('\\');
      }
      result.append(c);
    }
    result.append('"');
    return result.toString();
  }

  @Override
  public String toJsonStringIndented(String baseIndentation, String incrementIndentation) {
    return toJsonString();
  }
}
