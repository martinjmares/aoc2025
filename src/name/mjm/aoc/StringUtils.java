package name.mjm.aoc;

public class StringUtils {

  private StringUtils() {
  }

  public static boolean isNullOrEmpty(String string) {
    return string == null || string.isEmpty();
  }

  public static boolean isNullOrBlank(String string) {
    return string == null || string.isBlank();
  }

  public static String orElse(String string, String defaultValue) {
    return isNullOrBlank(string) ? defaultValue : string;
  }
}
