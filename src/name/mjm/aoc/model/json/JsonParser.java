package name.mjm.aoc.model.json;

import java.util.function.Function;

/**
 * Start here to build your JSONs.
 */
public class JsonParser {

  private final char[] diagBuffer = new char[16];
  private int bufferAppendPointer = 0;
  private boolean bufferRotated = false;

  private boolean willEscape = false;
  private long appendedChars = 0;

  private final JsonParserFromCharsBuilder rootBuilder;
  private JsonElementFromCharsBuilder nextBuilder;

  private boolean isInExceptionState = false;

  public JsonParser(Function<JsonElement, JsonElement> transformer) {
    rootBuilder = new JsonParserFromCharsBuilder(transformer);
    nextBuilder = rootBuilder;
  }

  public void append(char c) {
    if (isInExceptionState) {
      throw new IllegalStateException("Parser is in exception state. Cannot be used to parse more characters.");
    }

    // Add to diagnostic buffer
    if (bufferAppendPointer >= diagBuffer.length) {
      bufferRotated = true;
      bufferAppendPointer = 0;
    }
    diagBuffer[bufferAppendPointer] = c;
    bufferAppendPointer++;

    // Char counter
    appendedChars++;

    // Check escaping
    if (!willEscape && c == '\\') {
      willEscape = true;
      return;
    }

    // Append character
    try {
      nextBuilder = nextBuilder.add(c, willEscape);
    } catch (Exception e) {
      isInExceptionState = true;
      throw new InvalidJsonException("JSON parsing error on character '" + c + "' @" + appendedChars + ". Last few characters: \"" + getDiagBufferString() + "\"", e);
    } finally {
      willEscape = false;
    }
  }

  public JsonElement endOfStream() {
    if (isInExceptionState) {
      throw new IllegalStateException("Parser is in exception state. Cannot be used to finish this JSON parsing.");
    }

    nextBuilder.endOfStream();
    return rootBuilder.root;
  }

  public JsonElement get() {
    return rootBuilder.root;
  }

  public static JsonElement fromString(String json) {
    return fromString(json, null);
  }

  public static JsonElement fromString(String json, Function<JsonElement, JsonElement> transformer) {
    JsonParser parser = new JsonParser(transformer);
    for (char c : json.toCharArray()) {
      parser.append(c);
    }
    return parser.endOfStream();
  }

  private String getDiagBufferString() {
    StringBuilder result = new StringBuilder(diagBuffer.length + 3);
    if (bufferRotated) {
      result.append("...");
      for (int i = bufferAppendPointer; i < diagBuffer.length; i++) {
        result.append(diagBuffer[i]);
      }
    }
    for (int i = 0; i < bufferAppendPointer; i++) {
      result.append(diagBuffer[i]);
    }
    return result.toString();
  }

  private static class JsonParserFromCharsBuilder extends JsonElementFromCharsBuilder {

    private JsonElement root;
    private boolean started = false;

    public JsonParserFromCharsBuilder(Function transformer) {
      super(transformer, null);
    }

    @Override
    protected JsonElementFromCharsBuilder add(char c, boolean escaped) {
      if (Character.isWhitespace(c)) {
        return this;
      }
      if (started) {
        throw new InvalidJsonException("Second root JSON element in the stream!");
      } else {
        started = true;
        return getBuilderFromTheFirstCharacter(c, escaped);
      }
    }

    @Override
    protected void endOfStream() {
      if (root == null) {
        throw new InvalidJsonException("Stream ended unexpectedly unfinished JSON!");
      }
    }

    @Override
    void myChildIsDone(JsonElement childElement) {
      if (root != null) {
        throw new IllegalStateException("Second root element of the JSON");
      }
      this.root = childElement;
    }
  }
}
