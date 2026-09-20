package name.mjm.aoc.model.json;

import java.util.ArrayList;
import java.util.function.Function;

class JsonObjectFromCharsBuilder extends JsonElementFromCharsBuilder<JsonObject> {

  private final ArrayList<JsonObjectRecord> records = new ArrayList<>();

  JsonObjectFromCharsBuilder(Function<JsonElement, JsonElement> transformer, JsonElementFromCharsBuilder parentBuilder) {
    super(transformer, parentBuilder);
  }

  @Override
  protected JsonElementFromCharsBuilder add(char c, boolean escaped) {
    if (Character.isWhitespace(c)) {
      return this;
    }
    return switch (c) {
      case ',' -> {
        if (records.isEmpty()) {
          throw new InvalidJsonException("Comma cannot be a first character in JSON Object!");
        }
        yield this;
      }
      case '}' -> {
        iAmDone(new JsonObject(records));
        yield  parentBuilder;
      }
      case '"' -> {
        var recordBuilder = new JsonObjectRecordFromCharsBuilder(transformer, this);
        yield recordBuilder.add(c, escaped);
      }
      default -> throw new InvalidJsonException(c, "Invalid character in JSON Object!");
    };
  }

  @Override
  protected void endOfStream() {
    throw new InvalidJsonException("Unexpected end of input stream before object ends.");
  }

  @Override
  void myChildIsDone(JsonElement childElement) {
    if (childElement == null) {
      return;
    }
    if (childElement instanceof JsonObjectRecord record) {
      records.add(record);
    } else {
      throw new IllegalStateException("Child element of JsonObject must be JsonObjectRecord, but it is: " + childElement.getClass().getName());
    }
  }
}
