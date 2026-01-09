package name.mjm.aoc.y2015;

import name.mjm.aoc.*;

@Datas(
    {
        @Data(tryId = 1, value = "(()(()("),
        @Data(tryId = 2, value = ")())())")
    }
)
public class Day01 extends ParentDay {

  @TryResults(
      {
          @TryResult(tryId = 1, value = "3"),
          @TryResult(tryId = 2, value = "-3")
      }
  )
  public long a(String input) {
    char[] charArray = input.toCharArray();
    long counter = 0;
    for (char c : charArray) {
      switch (c) {
        case '(' -> counter++;
        case ')' -> counter--;
      }
    }
    return counter;
  }

  @TryResults(
      {
          @TryResult(tryId = 1, value = "-1"),
          @TryResult(tryId = 2, value = "1")
      }
  )
  public long b(String input) {
    char[] charArray = input.toCharArray();
    long floor = 0;
    long counter = 0;
    for (char c : charArray) {
      counter++;
      switch (c) {
        case '(' -> floor++;
        case ')' -> floor--;
      }
      if (floor == -1) {
        return counter;
      }
    }
    return -1;
  }
}
