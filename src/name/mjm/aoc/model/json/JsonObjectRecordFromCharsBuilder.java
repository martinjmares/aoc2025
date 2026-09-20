package name.mjm.aoc.model.json;

import java.util.function.Function;

class JsonObjectRecordFromCharsBuilder extends JsonElementFromCharsBuilder {

  private JsonString key;
  private JsonElement value;

  public JsonObjectRecordFromCharsBuilder(Function transformer, JsonElementFromCharsBuilder parentBuilder) {
    super(transformer, parentBuilder);
  }

  @Override
  protected JsonElementFromCharsBuilder add(char c, boolean escaped) {
    if (Character.isWhitespace(c)) {
      return this;
    }
    if (c == '}' || c == ',') {
      if (key == null) {
        throw new InvalidJsonException(c, "Unexpected end attribute in an object");
      }
      if (value == null) {
        throw new InvalidJsonException(c, "Unexpected end attribute '" + key.get() + "' in an object");
      }
      iAmDone(new JsonObjectRecord(key, value));
      return parentBuilder.add(c, escaped);
    }
    if (c == ':') {
      if (key == null) {
        throw new InvalidJsonException(c, "Object attribute value expected, but the key is not finished yet.");
      }
      return this;
    }
    if (key == null && c != '"') {
      throw new InvalidJsonException("Object attribute key must be string. Expecting '\"', but get '" + c + "'.");
    }
    return getBuilderFromTheFirstCharacter(c, escaped);
  }

  @Override
  protected void endOfStream() {
    throw new InvalidJsonException("Unexpected end of input stream before object ends.");
  }

  @Override
  void myChildIsDone(JsonElement childElement) {
    if (key == null) {
      if (childElement instanceof JsonString jselem) {
        key = jselem;
      } else {
        throw new InvalidJsonException("Attribute key must be string but get " + childElement.getClass().getSimpleName() + ".");
      }
    } else {
      value = childElement;
    }
  }
}
