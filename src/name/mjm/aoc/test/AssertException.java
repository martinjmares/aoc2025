package name.mjm.aoc.test;

public class AssertException extends RuntimeException {

  private final String detail;

  public AssertException(String message, String detail) {
    super(message);
    this.detail = detail;
  }

  public AssertException(String message) {
    this(message, null);
  }

  public String getDetail() {
    return detail;
  }
}
