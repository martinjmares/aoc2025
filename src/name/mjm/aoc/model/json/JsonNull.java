package name.mjm.aoc.model.json;

public class JsonNull implements JsonElement {

  public static final JsonNull NULL = new JsonNull();

  private JsonNull() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return toJsonString();
  }

  @Override
  public String toJsonString() {
    return "null";
  }

  @Override
  public String toJsonStringIndented(String baseIndentation, String incrementIndentation) {
    return toJsonString();
  }
}
