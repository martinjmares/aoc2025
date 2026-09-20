package name.mjm.aoc.model.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonObject implements JsonArray {

  private final List<JsonObjectRecord> records;

  public JsonObject(Collection<JsonObjectRecord> records) {
    this.records = records == null ? List.of() : new ArrayList<>(records);
  }

  @Override
  public int size() {
    return records.size();
  }

  @Override
  public JsonElement get(int index) {
    return records.get(index).get();
  }

  public String getKey(int index) {
    return records.get(index).getKey();
  }

  public JsonElement get(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name is null");
    }
    return records.stream()
                  .filter(rec -> name.equals(rec.getKey()))
                  .findAny()
                  .map(JsonObjectRecord::get)
                  .orElse(null);
  }

  @Override
  public Stream<JsonElement> stream() {
    return records.stream()
        .map(JsonObjectRecord::get);
  }

  public Stream<JsonObjectRecord> recordsStream() {
    return records.stream();
  }

  @Override
  public Iterator<JsonElement> iterator() {
    return records.stream().map(JsonObjectRecord::get).iterator();
  }

  @Override
  public void forEach(Consumer<? super JsonElement> action) {
    records.stream().map(JsonObjectRecord::get).forEach(action);
  }

  @Override
  public Spliterator<JsonElement> spliterator() {
    return records.stream().map(JsonObjectRecord::get).spliterator();
  }

  @Override
  public String toString() {
    return toJsonString();
  }

  @Override
  public String toJsonString() {
    return records.stream()
        .map(JsonObjectRecord::toJsonString)
        .collect(Collectors.joining(",", "{", "}"));
  }

  @Override
  public String toJsonStringIndented(String baseIndentation, String incrementIndentation) {
    if (records.isEmpty()) {
      return "{}";
    }
    StringBuilder result = new StringBuilder();
    result.append("{\n");
    String indentation = baseIndentation + incrementIndentation;
    boolean first = true;
    for (JsonObjectRecord record : records) {
      if (first) {
        first = false;
      } else {
        result.append(",\n");
      }
      result.append(indentation)
            .append(record.toJsonStringIndented(indentation, incrementIndentation));
    }
    result.append(baseIndentation)
        .append("}");
    return result.toString();
  }
}
