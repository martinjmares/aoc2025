package name.mjm.aoc.model.json;

import java.util.function.Function;

class JsonBooleanAndNullFromCharsBuilder extends JsonElementFromCharsBuilder {

  private static final String VALID_CHARACTERS = "trueTRUEfalsFALSnN";
  private final StringBuilder content = new StringBuilder();

  JsonBooleanAndNullFromCharsBuilder(Function<JsonElement, JsonElement> transformer, JsonElementFromCharsBuilder parentBuilder) {
    super(transformer, parentBuilder);
  }

  @Override
  protected JsonElementFromCharsBuilder add(char c, boolean escaped) {
    if (VALID_CHARACTERS.indexOf(c) == -1) {
      String value = content.toString().toLowerCase();
      JsonElement element = switch (value) {
        case "true" -> new JsonBoolean(true);
        case "false" -> new JsonBoolean(false);
        case "null" -> JsonNull.NULL;
        default -> throw new InvalidJsonException("Expected true/false/null. But it was '" + content + "'");
      };
      iAmDone(element);
      return parentBuilder.add(c, escaped);
    } else {
      content.append(c);
      return this;
    }
  }

  @Override
  protected void endOfStream() {
    String value = content.toString().toLowerCase();
    JsonElement element = switch (value) {
      case "true" -> new JsonBoolean(true);
      case "false" -> new JsonBoolean(false);
      case "null" -> JsonNull.NULL;
      default -> throw new InvalidJsonException("Expected true/false/null. But it was '" + content + "' and data stream ends.");
    };
    iAmDone(element);
    parentBuilder.endOfStream();
  }

  @Override
  void myChildIsDone(JsonElement childElement) {
    throw new IllegalStateException("JsonBoolean or JsonNull cannot have child elements.");
  }
}
