package name.mjm.aoc.y2015;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.Lines;

import java.util.List;

public class Day08 extends ParentDay {

  @TryResult("12")
  public int a(List<String> lines) {
    int result = 0;
    for (String line : lines) {
      line = line.trim();
      String unescaped = unescape(line);
      result += line.length() - unescaped.length();
    }
    return result;
  }

  String unescape(String input) {
    StringBuilder result = new StringBuilder(input.length());
    boolean inEscape = false;
    int hexadecSeqIndex = -1;
    char[] hexadec = new char[2];
    for (char c : input.toCharArray()) {
      if (hexadecSeqIndex >= 0) {
        hexadec[hexadecSeqIndex] = c;
        hexadecSeqIndex++;
        if (hexadecSeqIndex > 1) {
          hexadecSeqIndex = -1;
          result.append(hex2char(hexadec));
        }
      } else if (inEscape) {
        inEscape = false;
        if (c == 'x') {
          hexadecSeqIndex = 0;
        } else {
          result.append(c);
        }
      } else if (c == '\\') {
        inEscape = true;
      } else if (c != '"') {
        result.append(c);
      }
    }
    return result.toString();
  }

  @TryResult("19")
  public int b(List<String> lines) {
    int result = 0;
    for (String line : lines) {
      line = line.trim();
      result += encodedSize(line) - line.length();
    }
    return result;
  }

  int encodedSize(String str) {
    int result = 2;
    for (char c : str.toCharArray()) {
      result += switch (c) {
        case '"', '\\' -> 2;
        default -> 1;
      };
    }
    return result;
  }

  char hex2char(char[] hex) {
    return 'x'; // Don't care for now
  }
}
