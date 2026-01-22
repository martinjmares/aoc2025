package name.mjm.aoc.y2015.day11;

public class ForbiddenPCharsValidator implements PasswordValidator {

  final int[] invalidPChars;

  public ForbiddenPCharsValidator(int[] invalidPChars) {
    this.invalidPChars = invalidPChars;
  }

  @Override
  public PValidationResult validate(int[] password, PValidationResult lastResult, int modifiedPosition) {
    int checkFrom = 0;
    if (lastResult instanceof Result result) {
      if (result.firstInvalidCharPosition >= 0 && result.firstInvalidCharPosition < modifiedPosition) {
        return result; // Don't need to check, it must be still true
      } else {
        checkFrom = modifiedPosition;
      }
    }

    for (int i = checkFrom; i < password.length; i++) {
      if (!isValid(password[i])) {
          return new Result(i);
      }
    }
    return new Result(-1);
  }

  private boolean isValid(int pChar) {
    for (int invalidPChar : invalidPChars) {
      if (pChar == invalidPChar) {
        return false;
      }
    }
    return true;
  }

  public static class Result implements PValidationResult {

    final int firstInvalidCharPosition;

    public Result(int firstInvalidCharPosition) {
      this.firstInvalidCharPosition = firstInvalidCharPosition;
    }

    @Override
    public boolean isValid() {
      return firstInvalidCharPosition < 0;
    }

    @Override
    public int movePosition() {
      return firstInvalidCharPosition;
    }

    @Override
    public int moveIncrement() {
      return firstInvalidCharPosition >= 0 ? 1 : -1;
    }
  }
}
