package name.mjm.aoc.model.json;

import name.mjm.aoc.test.Assert;

public class JsonSimpleElementsParsingTest {

  void testParseLong() throws Exception {
    // Given
    String jsStr = "1234";

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonLong.class);
    Assert.that(element.toJsonString())
        .is("1234");
  }

  void testParseLongNegative() throws Exception {
    // Given
    String jsStr = "-1234";

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonLong.class);
    Assert.that(element.toJsonString())
          .is("-1234");
  }

  void testParseDouble() throws Exception {
    // Given
    String jsStr = "2.13";

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonDouble.class);
    Assert.that(element.toJsonString())
          .is("2.13");
  }

  void testParseDoubleNegative() throws Exception {
    // Given
    String jsStr = "-2.13";

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonDouble.class);
    Assert.that(element.toJsonString())
          .is("-2.13");
  }

  void testParseBoolean() throws Exception {
    // Given
    String jsStr = "true";

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonBoolean.class);
    Assert.that(element.toJsonString())
          .is("true");
  }

  void testParseNull() throws Exception {
    // Given
    String jsStr = "null";

    JsonElement element = JsonParser.fromString(jsStr);

    Assert.that(element)
          .isNotNull()
          .isOfType(JsonNull.class);
  }
}
