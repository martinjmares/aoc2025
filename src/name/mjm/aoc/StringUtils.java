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

  public static char charAt(String string, int index, char defaultValue) {
    if (string == null || index >= string.length() || index < 0) {
      return defaultValue;
    }
    return string.charAt(index);
  }

  public static boolean isNumber(String str) {
    char[] charArray = str.toCharArray();
    if (str.isBlank()) {
      return false;
    }
    for (char c : charArray) {
      if (!Character.isDigit(c) && !Character.isWhitespace(c) && c != '-') {
        return false;
      }
    }
    return true;
  }
}
