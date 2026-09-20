package name.mjm.aoc.model.json;

import java.util.function.Function;

class JsonNumberFromCharsBuilder extends JsonElementFromCharsBuilder<JsonNumber> {

  private final StringBuilder value = new StringBuilder();
  private boolean isDecimal = false;

  JsonNumberFromCharsBuilder(Function<JsonElement, JsonElement> transformer, JsonElementFromCharsBuilder parentBuilder) {
    super(transformer, parentBuilder);
  }

  @Override
  protected JsonElementFromCharsBuilder add(char c, boolean escaped) {
    if (c == '-') {
      if (value.isEmpty()) {
        value.append(c);
      } else {
        throw new InvalidJsonException(c, "Negative sign can be only the first character of a number.");
      }
    } else if (c == '.') {
      if (value.isEmpty()) {
        throw new InvalidJsonException(c, "Period '.' character in a number cannot be the first numerical character.");
      } else if (isDecimal) {
        throw new InvalidJsonException(c, "Period '.' character can be only one in the number.");
      } else {
        value.append(c);
        isDecimal = true;
      }
    } else if (Character.isDigit(c)) {
      value.append(c);
    } else {
      // Unknown character, can be valid in parent.
      JsonNumber num = isDecimal
          ? new JsonDouble(Double.parseDouble(value.toString()))
          : new JsonLong(Long.parseLong(value.toString()));
      iAmDone(num);
      return parentBuilder.add(c, escaped);
    }

    return this;
  }

  @Override
  protected void endOfStream() {
    JsonNumber num = isDecimal
        ? new JsonDouble(Double.parseDouble(value.toString()))
        : new JsonLong(Long.parseLong(value.toString()));
    iAmDone(num);
    parentBuilder.endOfStream();
  }

  @Override
  void myChildIsDone(JsonElement childElement) {
    throw new IllegalStateException("JsonNumber cannot have child elements");
  }
}
