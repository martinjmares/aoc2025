package name.mjm.aoc.y2015;

import name.mjm.aoc.Data;
import name.mjm.aoc.ParentDay;

@Data("1321131112")
public class Day10 extends ParentDay {

  public int a(String input) {
    return iterativeLookAndSay(input, 40).length();
  }

  public int b(String input) {
    return iterativeLookAndSay(input, 50).length();
  }

  String iterativeLookAndSay(String input, int iterations) {
    String output = input;
    for (int i = 0; i < iterations; i++) {
      output = lookAndSay(output);
    }
    return output;
  }

  public String lookAndSay(String s) {
    StringBuilder output = new StringBuilder(s.length() * 2);
    char lastChar = '\n';
    int lastCharCount = 0;
    for (char c : s.toCharArray()) {
      if (c == lastChar) {
        lastCharCount++;
      } else {
        if (lastCharCount > 0) {
          output.append(lastCharCount).append(lastChar);
        }
        lastChar = c;
        lastCharCount = 1;
      }
    }
    if (lastCharCount > 0) {
      output.append(lastCharCount).append(lastChar);
    }
    return output.toString();
  }
}
