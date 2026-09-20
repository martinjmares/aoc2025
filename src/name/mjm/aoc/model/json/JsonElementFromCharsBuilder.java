package name.mjm.aoc.model.json;

import java.util.function.Function;

/**
 * Supports incremental building based on the incoming stream of characters.
 * @param <T> the element it builds
 */
abstract class JsonElementFromCharsBuilder<T extends JsonElement> {

  protected final Function<JsonElement, JsonElement> transformer;
  protected final JsonElementFromCharsBuilder parentBuilder;

  protected JsonElementFromCharsBuilder(Function<JsonElement, JsonElement> transformer, JsonElementFromCharsBuilder parentBuilder) {
    this.transformer = transformer;
    this.parentBuilder = parentBuilder;
  }

  /**
   * Called by char iterator building process. It represents one more read character from the stream.
   *
   * @param c character
   * @param escaped is {@code true} when the character is escaped
   *
   * @return builder of the element to continue with the building process
   */
  protected abstract JsonElementFromCharsBuilder add(char c, boolean escaped);

  /**
   * Signals, that this data stream ended. Some types, like numbers, needs it, for others it is a signal os unfinished data.
   */
  protected abstract void endOfStream();

  protected JsonElementFromCharsBuilder getBuilderFromTheFirstCharacter(char c, boolean escaped) {
    return switch (c) {
      case '"' -> new JsonStringFromCharsBuilder(transformer, this);
      case '[' -> new JsonArrayFromCharsBuilder(transformer, this);
      case 't', 'T', 'f', 'F', 'n', 'N' -> {
        var bb = new JsonBooleanAndNullFromCharsBuilder(transformer, this);
        yield bb.add(c, escaped);
      }
      case '{' -> new JsonObjectFromCharsBuilder(transformer, this);
      default -> {
        if (c == '-' || Character.isDigit(c)) {
          var nb = new JsonNumberFromCharsBuilder(transformer, this);
          yield nb.add(c, escaped);
        } else {
          throw new InvalidJsonException(c, "Unexpected character, not a beginning of any element");
        }
      }
    };
  }

  /**
   * Called by child element builder when finished.
   * @param childElement build child element
   */
  abstract void myChildIsDone(JsonElement childElement);

  /**
   * Call it on {@code this} when you finished building of yourself. It will propagate it to the parent.
   * @param childElement
   */
  protected void iAmDone(JsonElement childElement) {
    if (childElement == null) {
      return;
    }
    if (this.transformer != null) {
      childElement = transformer.apply(childElement);
    }
    parentBuilder.myChildIsDone(childElement);
  }
}
