package name.mjm.aoc.model.json;

import java.util.Objects;

/**
 * Represents a key-value pair in JSON Object.
 */
public class JsonObjectRecord implements JsonElement {

  private final JsonString key;
  private final JsonElement value;

  public JsonObjectRecord(JsonString key, JsonElement value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key.get();
  }

  public JsonElement get() {
    return value;
  }

  @Override
  public String toString() {
    return "\"" + key + "\":" + value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    JsonObjectRecord that = (JsonObjectRecord) o;
    return Objects.equals(key, that.key) && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toJsonString() {
    return key.toJsonString() + ":" + value.toJsonString();
  }

  @Override
  public String toJsonStringIndented(String baseIndentation, String incrementIndentation) {
    return key.toJsonStringIndented(baseIndentation, incrementIndentation)
        + ": "
        + value.toJsonStringIndented(baseIndentation, incrementIndentation);
  }
}
