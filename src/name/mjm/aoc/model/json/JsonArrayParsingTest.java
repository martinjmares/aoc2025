package name.mjm.aoc.model.json;

import name.mjm.aoc.test.Assert;

public class JsonArrayParsingTest {

  void testEmpty() throws Exception {
    String jsStr = """
        []
        """;

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonArrayArray.class);
    Assert.that(element.toJsonString())
          .is(JsonObjectParsingTest.removeWhitespaces(jsStr));
  }

  void testMultipleSimpleValues() throws Exception {
    String jsStr = """
        ["aaa", 123, null, "bbb", "ccc"]
        """;

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonArrayArray.class);
    Assert.that(element.toJsonString())
          .is(JsonObjectParsingTest.removeWhitespaces(jsStr));
  }

  void testInherited() throws Exception {
    String jsStr = """
        [
          "aaa",
          [
            123,
            {
               "foo": "bar",
               "baz": {
                  "zebra": false
               }
            }
          ]
        ]
        """;

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonArrayArray.class);
    Assert.that(element.toJsonString())
          .is(JsonObjectParsingTest.removeWhitespaces(jsStr));
  }
}
