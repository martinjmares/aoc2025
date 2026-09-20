package name.mjm.aoc.model.json;

import name.mjm.aoc.test.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JsonParsingProcessingTest {

  public void testWithProcess() {
    String str = """
        {
          "arr": ["aaa", "removeMe", 123],
          "arr2": [
            "cx",
            {
              "one": "xyz",
              "remove2": "some",
              "twox": 2
            }
           ]
        }
        """;

    Function<JsonElement, JsonElement> processor = (input) -> {
      if (input instanceof JsonString jstr) {
        return new JsonString(jstr.get().replace('x', 'y'));
      } else if (input instanceof JsonArrayArray jarr) {
        List<JsonElement> elements = jarr.stream()
                                       .filter(element -> {
                                         if (element instanceof JsonString jstr) {
                                           return !jstr.get().startsWith("remove");
                                         } else {
                                           return true;
                                         }
                                       })
                                       .toList();
        return new JsonArrayArray(elements);
      } else if (input instanceof JsonObject jobj) {
        List<JsonObjectRecord> records = jobj.recordsStream()
                                            .filter(element -> !element.getKey().startsWith("remove"))
                                            .toList();
        return new JsonObject(records);
      } else {
        return input;
      }
    };

    JsonElement json = JsonParser.fromString(str, processor);
    String expected = """
        {
          "arr": ["aaa", 123],
          "arr2": [
            "cy",
            {
              "one": "yyz",
              "twoy": 2
            }
           ]
        }
        """;
    expected = JsonObjectParsingTest.removeWhitespaces(expected);
    Assert.that(json)
          .isNotNull()
          .isOfType(JsonObject.class);
    Assert.that(json.toJsonString())
          .is(expected);

  }
}
