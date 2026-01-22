package name.mjm.aoc.y2015;

import name.mjm.aoc.*;
import name.mjm.aoc.y2015.day11.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@Datas({
    @Data("cqjxjnds"),
    @Data(tryId = 1, value = "abcdefgh"),
    @Data(tryId = 2, value = "ghijklmn")
})
public class Day11 extends ParentDay {

  private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

  @TryResults({
      @TryResult(tryId = 1, value = "abcdffaa"),
      @TryResult(tryId = 2, value = "ghjaabcc")
  })
  public String a(String input) {
    int[] password = toIntPassword(input);

    PasswordValidator[] validators = new PasswordValidator[] {
        new IncrementalPCharValidator(),
        new ForbiddenPCharsValidator(new int[] {ALPHABET.indexOf('i'), ALPHABET.indexOf('o'), ALPHABET.indexOf('l')}),
        new DoubleDoublesValidator()
    };
    PValidationResult[] results = new PValidationResult[validators.length];

    // Continue until find a solution
    int movePosition = 0;
    int counter = 0;
    while (true) {
      counter++;
      // Validate
      for (int i = 0; i < validators.length; i++) {
        results[i] = validators[i].validate(password, results[i], movePosition);
      }
      // Process results
      boolean valid = true;
      movePosition = password.length - 1;
      int moveIncrement = 1;
      for (PValidationResult result : results) {
        if (!result.isValid()) {
          valid = false;
          int pos = result.movePosition();
          int inc = result.moveIncrement();
          if (pos >= 0 && inc > 0) {
            if (pos < movePosition) {
              movePosition = pos;
              moveIncrement = inc;
            } else if (pos == movePosition && inc > moveIncrement) {
              moveIncrement = inc;
            }
          }
        }
      }
      logger.debug(toLogString(counter, password, results, movePosition, moveIncrement));
      if (valid) {
        break;
      } else {
        movePosition = inc(password, movePosition, moveIncrement, true);
      }
    }

    String result = toStringPassword(password);
    logger.info("Done after " + counter + " iterations: " + result);
    return result;
  }

  public String b(String input) {
    logger.debug("FIRST ITERATION");
    String first = a(input);
    logger.debug("SECOND ITERATION");
    int[] firstPass = toIntPassword(first);
    inc(firstPass);
    String firstMoved = toStringPassword(firstPass);
    String second = a(firstMoved);
    return second;
  }

  private String toLogString(int iteration, int[] password, PValidationResult[] results, int movePosition, int moveIncrement) {
    StringBuilder sb = new StringBuilder();
    sb.append("iter-").append(iteration);
    sb.append(", valid=");
    sb.append(
        Arrays.stream(results)
        .map(PValidationResult::isValid)
        .map(String::valueOf)
        .collect(Collectors.joining(",", "[", "]"))
    );
    sb.append(". Move: '");
    for (int i = 0; i < password.length; i++) {
      if (i == movePosition) {
        sb.append("(").append(ALPHABET.charAt(password[i])).append(")");
      } else {
        sb.append(ALPHABET.charAt(password[i]));
      }
    }
    sb.append("' | ").append(moveIncrement);
    return sb.toString();
  }

  int[] toIntPassword(String password) {
    int[] result = new int[password.length()];
    for (int i = 0; i < password.length(); i++) {
      char c = password.charAt(i);
      int index = ALPHABET.indexOf(c);
      if (index == -1) {
        throw new IllegalArgumentException("Password must contain only valid characters and not '" + c + "'!");
      }
      result[i] = index;
    }
    return result;
  }

  String toStringPassword(int[] password) {
    StringBuilder result = new StringBuilder(password.length);
    for (int i : password) {
      result.append(ALPHABET.charAt(i));
    }
    return result.toString();
  }

  int inc(int[] password) {
    return inc(password, password.length - 1, 1, false);
  }

  int inc(int[] password, int place, int inc, boolean clearAfter) {
    if (clearAfter) {
      for (int i = place + 1; i < password.length; i++) {
        password[i] = 0;
      }
    }
    password[place] += inc;
    int result = place;
    while (password[place] >= ALPHABET.length()) {
      password[place] -= ALPHABET.length();
      int candidate = inc(password, place - 1, 1, false);
      if (candidate < result) {
        result = candidate;
      }
    }
    return result;
  }
}
