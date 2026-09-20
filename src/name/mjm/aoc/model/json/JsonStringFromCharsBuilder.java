package name.mjm.aoc.model.json;

import java.util.function.Function;

class JsonStringFromCharsBuilder extends JsonElementFromCharsBuilder<JsonString> {

  static final char QUAT = '"';

  private final StringBuilder content = new StringBuilder();

  JsonStringFromCharsBuilder(Function<JsonElement, JsonElement> transformer, JsonElementFromCharsBuilder parentBuilder) {
    super(transformer, parentBuilder);
  }

  @Override
  protected JsonElementFromCharsBuilder add(char c, boolean escaped) {
    if (!escaped && c == QUAT) {
      iAmDone(new JsonString(content.toString()));
      return parentBuilder;
    }

    content.append(c);
    return this;
  }

  @Override
  protected void endOfStream() {
    throw new InvalidJsonException("Unexpected end of input stream before string ends.");
  }

  @Override
  void myChildIsDone(JsonElement childElement) {
    throw new IllegalStateException("JsonString cannot have child elements!");
  }
}
