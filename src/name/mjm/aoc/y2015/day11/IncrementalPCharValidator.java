package name.mjm.aoc.y2015.day11;

public class IncrementalPCharValidator implements PasswordValidator {

  @Override
  public PValidationResult validate(int[] password, PValidationResult lastResult, int modifiedPosition) {
    if (lastResult instanceof Result result) {
      if (result.firstTriplet >= 0 && modifiedPosition > (result.firstTriplet + 2)) {
        return result; // Still valid
      }
    }
    int startFrom = modifiedPosition - 2;
    if (startFrom < 0) {
      startFrom = 0;
    }

    int prePre = -100;
    int pre = -10;
    for (int i = startFrom; i < password.length; i++) {
      int now = password[i];
      if (now == (pre + 1)) {
        if (pre == (prePre + 1)) {
          // Triplet
          return new Result(i - 2, -1, 0);
        }
      }
      prePre = pre;
      pre = now;
    }

    // Best potential has always last three elements. which one to move
    int aPos = password.length - 3;
    int bPos = password.length - 2;
    int cPos = password.length - 1;
    int a = password[aPos];
    int b = password[bPos];
    int c = password[cPos];
    if (a < b - 1) {
      // a is too low, must move
      return new Result(-1 , aPos, 1);
    } else if (a >= b) {
      return new Result(-1 , bPos, a - b + 1);
    } else if (b < c - 1) {
      return new Result(-1 , bPos, 1);
    } else {
      int cMove = b - c + 1;
      if (cMove > 0) {
        return new Result(-1 , cPos, cMove);
      } else {
        return new Result(-1 , -1, 0);
      }
    }
  }

  public static class Result implements PValidationResult {
    
    final int firstTriplet;
    final int moveIndex;
    final int moveCount;

    Result(int firstTriplet, int moveIndex, int moveCount) {
      this.firstTriplet = firstTriplet;
      this.moveIndex = moveIndex;
      this.moveCount = moveCount;
    }

    @Override
    public boolean isValid() {
      return firstTriplet >= 0;
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
