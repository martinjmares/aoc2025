package name.mjm.aoc.model;

public record Interval(long from, long to) {

  public String toString() {
    return from + "-" + to;
  }

  public static Interval fromString(String intervalString) {
    int index = intervalString.indexOf('-');
    if (index < 0) {
      throw new IllegalArgumentException("Invalid interval: " + intervalString);
    }
    long from = Long.parseLong(intervalString.substring(0, index).trim());
    long to = Long.parseLong(intervalString.substring(index + 1).trim());
    return new Interval(from, to);
  }
}
