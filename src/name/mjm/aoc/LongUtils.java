package name.mjm.aoc;

public class LongUtils {
  public static long pow(long x, long y) {
    long result = 1;
    for (int i = 0; i < y; i++) {
      result *= x;
    }
    return result;
  }
}