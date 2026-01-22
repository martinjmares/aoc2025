package name.mjm.aoc.y2015.day11;

public interface PasswordValidator {

  PValidationResult validate(int[] password, PValidationResult lastResult, int modifiedPosition);

}
