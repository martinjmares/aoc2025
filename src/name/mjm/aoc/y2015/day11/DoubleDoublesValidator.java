package name.mjm.aoc.y2015.day11;

public class DoubleDoublesValidator implements PasswordValidator {
  @Override
  public PValidationResult validate(int[] password, PValidationResult lastResult, int modifiedPosition) {
    int firstCouple = -1;
    int secondCouple = -1;
    int fromIndex = 0;

    if (lastResult instanceof Result result) {
      if (result.secondCouple >= 0 && modifiedPosition > (result.secondCouple + 1)) {
          return result;
      }
      if (result.firstCouple >= 0 && modifiedPosition > (result.firstCouple + 1)) {
        firstCouple = result.firstCouple;
      }
      fromIndex = modifiedPosition - 1;
      if (fromIndex < 0) {
        fromIndex = 0;
      } else if (firstCouple >= 0 && fromIndex <= firstCouple + 1) {
        fromIndex = firstCouple + 2;
      }
    }

    // Find pairs
    int prev = -1;
    for (int i = fromIndex; i < password.length; i++) {
      int now = password[i];
      if (prev == now) {
        if (firstCouple < 0) {
          firstCouple = i - 1;
        } else if (secondCouple < 0 && i > firstCouple + 2) {
          secondCouple = i - 1;
          break;
        }
      }
      prev = now;
    }
    if (secondCouple >= 0) {
      return new Result(firstCouple, secondCouple, -1, 0);
    }

    // Now find what to change
    if (firstCouple >= 0) {
      if (password[password.length - 2] <= password[password.length - 1]) {
        return new Result(firstCouple, secondCouple, password.length - 2, 1);
      } else {
        return new Result(firstCouple, secondCouple, password.length - 1, password[password.length - 2] - password[password.length - 1]);
      }
    } else {
      if (password[password.length - 4] <= password[password.length - 3]) {
        return new Result(firstCouple, secondCouple, password.length - 4, 1);
      } else {
        return new Result(firstCouple, secondCouple, password.length - 3, password[password.length - 4] - password[password.length - 3]);
      }
    }
  }

  public static class Result implements PValidationResult {

    final int firstCouple;
    final int secondCouple;
    final int moveIndex;
    final int moveCount;

    Result(int firstCouple, int secondCouple, int moveIndex, int moveCount) {
      this.firstCouple = firstCouple;
      this.secondCouple = secondCouple;
      this.moveIndex = moveIndex;
      this.moveCount = moveCount;
    }

    @Override
    public boolean isValid() {
      return secondCouple >= 0;
    }

    @Override
    public int movePosition() {
      return moveIndex;
    }

    @Override
    public int moveIncrement() {
      return moveCount;
    }
  }
}
