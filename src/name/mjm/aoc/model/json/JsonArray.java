package name.mjm.aoc.model.json;

import java.util.stream.Stream;

/**
 * Basic API to access JSON array style data. These are ARRAY and OBJECT.
 *
 * @see JsonArray
 * @see JsonObject
 */
public interface JsonArray extends JsonElement, Iterable<JsonElement> {

  JsonElement get(int index);

  int size();

  Stream<JsonElement> stream();

}
