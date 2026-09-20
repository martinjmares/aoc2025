package name.mjm.aoc.model.json;

public class InvalidJsonException extends RuntimeException {
  public InvalidJsonException(String message) {
    super(message);
  }

  public InvalidJsonException(String message, Throwable cause) {
    super(message, cause);
  }

  InvalidJsonException(char c, String message) {
    super(message + "; Unexpected character '" + c + "'");
  }
}
