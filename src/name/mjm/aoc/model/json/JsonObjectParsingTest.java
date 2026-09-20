package name.mjm.aoc.model.json;

import name.mjm.aoc.test.Assert;

public class JsonObjectParsingTest {

  static String removeWhitespaces(String input) {
    StringBuilder result = new StringBuilder(input.length());
    for (char c : input.toCharArray()) {
      if (!Character.isWhitespace(c)) {
        result.append(c);
      }
    }
    return result.toString();
  }

  void testEmptyObject() throws Exception {
    String jsStr = "{}";

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonObject.class);
    Assert.that(element.toJsonString())
          .is("{}");
  }

  void testEmptyStrStr() throws Exception {
    String jsStr = """
        {
           "aaa": "hi",
           "bbb": "hello"
        }
        """;

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonObject.class);
    Assert.that(element.toJsonString())
          .is(removeWhitespaces(jsStr));
  }

  void testMultipleTypes() throws Exception {
    String jsStr = """
        {
           "aaa": "hi",
           "bbb": 1234,
           "ccc": false,
           "ddd": null,
           "eee": { },
           "fff": [ ]
        }
        """;

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonObject.class);
    Assert.that(element.toJsonString())
          .is(removeWhitespaces(jsStr));
  }

  void testInherited() throws Exception {
    String jsStr = """
        {
           "aaa": "hi",
           "sub": {
             "saaa": "ffff",
             "sbbb": false
           }
        }
        """;

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonObject.class);
    Assert.that(element.toJsonString())
          .is(removeWhitespaces(jsStr));
  }
}
