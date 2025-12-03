package name.mjm.aoc;

public class NumberUtils {

  private NumberUtils() {
  }

  public static int parseInt(String string, int defaultValue) {
    if (string != null) {
      string = string.trim();
    }
    try {
      return Integer.parseInt(string);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public static long parseLong(String string, long defaultValue) {
    if (string != null) {
      string = string.trim();
    }
    try {
      return Long.parseLong(string);
    }  catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
