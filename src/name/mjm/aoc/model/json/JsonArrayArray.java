package name.mjm.aoc.model.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonArrayArray implements JsonArray {

  private final List<JsonElement> values;

  public JsonArrayArray(Collection<JsonElement> values) {
    this.values = values == null ? List.of() : new ArrayList<>(values);
  }

  public int size() {
    return values.size();
  }

  public JsonElement get(int index) {
    return values.get(index);
  }

  @Override
  public Iterator<JsonElement> iterator() {
    return values.iterator();
  }

  @Override
  public void forEach(Consumer<? super JsonElement> action) {
    values.forEach(action);
  }

  @Override
  public Spliterator<JsonElement> spliterator() {
    return values.spliterator();
  }

  @Override
  public Stream<JsonElement> stream() {
    return values.stream();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    JsonArrayArray that = (JsonArrayArray) o;
    return Objects.equals(values, that.values);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(values);
  }

  @Override
  public String toString() {
    return toJsonString();
  }

  @Override
  public String toJsonString() {
    return values.stream()
                 .map(JsonElement::toJsonString)
                 .collect(Collectors.joining(",", "[", "]"));
  }

  @Override
  public String toJsonStringIndented(String baseIndentation, String incrementIndentation) {
    if (values.isEmpty()) {
      return "[]";
    }
    StringBuilder builder = new StringBuilder();
    builder.append("[\n");
    String indentation = baseIndentation + incrementIndentation;
    boolean first = true;
    for (JsonElement value : values) {
      if (first) {
        first = false;
      } else {
        builder.append(",\n");
      }
      builder.append(indentation)
             .append(value.toJsonStringIndented(indentation, incrementIndentation));
    }
    builder.append(baseIndentation)
           .append("]");
    return builder.toString();
  }
}
