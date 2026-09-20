package name.mjm.aoc.model.json;

import java.util.ArrayList;
import java.util.function.Function;

class JsonArrayFromCharsBuilder extends JsonElementFromCharsBuilder<JsonArrayArray> {

  private final ArrayList<JsonElement> elements = new ArrayList<>();

  JsonArrayFromCharsBuilder(Function<JsonElement, JsonElement> transformer, JsonElementFromCharsBuilder parentBuilder) {
    super(transformer, parentBuilder);
  }

  @Override
  protected JsonElementFromCharsBuilder add(char c, boolean escaped) {
    if (Character.isWhitespace(c) || c == ',') {
      return this;
    }
    if (']' == c) {
      iAmDone(new JsonArrayArray(elements));
      return parentBuilder;
    }
    return getBuilderFromTheFirstCharacter(c, escaped);
  }

  @Override
  protected void endOfStream() {
    throw new InvalidJsonException("Unexpected end of input stream before array ends.");
  }

  @Override
  void myChildIsDone(JsonElement childElement) {
    elements.add(childElement);
  }
}
