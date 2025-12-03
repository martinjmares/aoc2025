package name.mjm.aoc.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListOfDigitArrays extends ArrayList<int[]> {

  private final boolean uniformLength;

  public ListOfDigitArrays(BufferedReader reader) throws IOException {
    List<int[]> digitsList = new ArrayList<>();
    String line;
    int lastLength = -1;
    boolean uniformLength = true;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) {
        continue;
      }
      if (lastLength != -1 && line.length() != lastLength) {
        uniformLength = false;
      }
      lastLength = line.length();
      int[] digits = new int[line.length()];
      for (int i = 0; i < line.length(); i++) {
        digits[i] = Integer.parseInt(line, i, i + 1, 10);
      }
      digitsList.add(digits);
    }

    this.uniformLength = uniformLength;
    super(digitsList);
  }

  public boolean isUniformLength() {
    return uniformLength;
  }

  public static String toString(int[] digits) {
    StringBuilder builder = new StringBuilder(digits.length * 2);
    for (int digit : digits) {
      builder.append(digit);
    }
    return builder.toString();
  }

  public static String toString(int[] digits, int[] markPositions) {
    if (markPositions == null || markPositions.length == 0) {
      return toString(digits);
    }

    int[] sortedMarkedPositions = new int[markPositions.length];
    System.arraycopy(markPositions, 0, sortedMarkedPositions, 0, markPositions.length);
    Arrays.sort(sortedMarkedPositions);
    int markIndex = 0;

    StringBuilder builder = new StringBuilder(digits.length * 3);
    for (int i = 0; i < digits.length; i++) {
      if (markIndex < sortedMarkedPositions.length && i == sortedMarkedPositions[markIndex]) {
        builder.append('[').append(digits[i]).append(']');
        markIndex++;
      } else {
        builder.append(digits[i]);
      }
    }
    return builder.toString();
  }

  public static String toString(int[] digits, Collection<Integer> markPositions) {
    if (markPositions == null || markPositions.isEmpty()) {
      return toString(digits);
    } else {
      int[] markPosArray = markPositions.stream()
                                 .mapToInt(Integer::intValue)
                                 .toArray();
      return toString(digits, markPosArray);
    }
  }
}
