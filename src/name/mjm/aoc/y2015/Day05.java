package name.mjm.aoc.y2015;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Day05 extends ParentDay {

  @TryResult("2")
  public int a(List<String> lines) {
    int counter = 0;
    for (String line : lines) {
      Validator[] validators = new Validator[]{
          new VowelsCounter(3),
          new RepeatValidator(),
          new ForbiddenPairsValidator()
      };
      if (isNice(line, validators)) {
        counter++;
      }
    }
    return counter;
  }

  @TryResult("2")
  public int b(List<String> lines) {
    int counter = 0;
    for (String line : lines) {
      Validator[] validators = new Validator[]{
          new RepeatPairsValidator(),
          new SkipRepeatValidator()
      };
      if (isNice(line, validators)) {
        counter++;
      }
    }
    return counter;
  }

  boolean isNice(String s, Validator[] validators) {
    char[] charArray = s.toCharArray();
    for (char c : charArray) {
      for (Validator validator : validators) {
        validator.consume(c);
      }
    }

    // Result
    for (Validator validator : validators) {
      if (!validator.isOk()) {
        return false;
      }
    }
    logger.debug("NICE: " + s);
    return true;
  }

  interface Validator {
    boolean isOk();
    void consume(char c);
  }

  static class VowelsCounter implements Validator {

    final int minVowels;
    int counter = 0;

    VowelsCounter(int minVowels) {
      this.minVowels = minVowels;
    }

    @Override
    public boolean isOk() {
      return counter >= minVowels;
    }

    @Override
    public void consume(char c) {
      switch (c) {
        case 'a', 'e', 'i', 'o', 'u' -> counter++;
      }
    }
  }

  static class RepeatValidator implements Validator {
    private char lastChar = '\n';
    private boolean findRepeat = false;

    @Override
    public boolean isOk() {
      return findRepeat;
    }

    @Override
    public void consume(char c) {
      if (c == lastChar) {
        findRepeat = true;
      }
      lastChar = c;
    }
  }

  static class ForbiddenPairsValidator implements Validator {
    private static final char[][] FORBIDDEN = new char[][] {
        {'a', 'b'},
        {'c', 'd'},
        {'p', 'q'},
        {'x', 'y'}
    };
    private char lastChar = '\n';
    boolean hasForbiddenPairs = false;

    @Override
    public boolean isOk() {
      return !hasForbiddenPairs;
    }

    @Override
    public void consume(char c) {
      if (!hasForbiddenPairs) {
        for (char[] pair : FORBIDDEN) {
          if (pair[0] == lastChar && pair[1] == c) {
            hasForbiddenPairs = true;
            break;
          }
        }
        lastChar = c;
      }
    }
  }

  static class RepeatPairsValidator implements Validator {
    private char lastChar = '\n';
    String lastPair = "";
    private boolean findRepeat = false;
    private final Set<String> pairs = new HashSet<>();

    @Override
    public boolean isOk() {
      return findRepeat;
    }

    @Override
    public void consume(char c) {
      if (!findRepeat) {
        if (lastChar != '\n') {
          String pair = String.valueOf(new char[]{lastChar, c});
          if (pairs.contains(pair)) {
            findRepeat = true;
          }
          pairs.add(lastPair);
          lastPair = pair;
        }
        lastChar = c;
      }
    }
  }

  static class SkipRepeatValidator implements Validator {
    private char lastLastChar = '\n';
    private char lastChar = '\n';
    private boolean findRepeat = false;

    @Override
    public boolean isOk() {
      return findRepeat;
    }

    @Override
    public void consume(char c) {
      if (!findRepeat) {
        if (lastLastChar == c) {
          findRepeat = true;
        } else {
          lastLastChar = lastChar;
          lastChar = c;
        }
      }
    }
  }
}
