package name.mjm.aoc.algebra;

public final class ConstantFormula implements ParametrisedFormula {

  private final RationalNumber result;

  ConstantFormula(RationalNumber result) {
    this.result = result;
  }

  RationalNumber result() {
    return result;
  }

  @Override
  public RationalNumber solve(int... parameters) {
    return result;
  }

  void addToComplexFormulaString(StringBuilder sb, boolean isFirst) {
    if (isFirst) {
      sb.append(result);
    } else {
      if (result.numerator() < 0) {
        sb.append(" - ");
      } else  {
        sb.append(" + ");
      }
      if (result.denominator() == 1 || result.numerator() == 0) {
        sb.append(Math.abs(result.numerator()));
      } else {
        sb.append("(").append(Math.abs(result.numerator())).append('/').append(result.denominator()).append(')');
      }
    }
  }

  public String toString() {
    return String.valueOf(result);
  }

  @Override
  public int[] parametersIndexes() {
    return new int[0];
  }

  @Override
  public ParametrisedFormula replaceParameter(int index, ParametrisedFormula replacement) {
    return this;
  }
}
