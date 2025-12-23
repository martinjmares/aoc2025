package name.mjm.aoc.algebra;

import java.util.List;

public final class SimpleParametrisedFormula implements ParametrisedFormula {

  private final int parameterIndex;
  private final RationalNumber multiplicator;

  SimpleParametrisedFormula(int parameterIndex, RationalNumber multiplicator) {
    if (parameterIndex < 0) {
      throw new IllegalArgumentException("Parameter index cannot be negative");
    }
    this.parameterIndex = parameterIndex;
    this.multiplicator = multiplicator;
  }

  @Override
  public RationalNumber solve(int... parameters) {
    return multiplicator.multiply(parameters[parameterIndex]);
  }

  void addToComplexFormulaString(StringBuilder sb, boolean isFirst) {
    if (isFirst) {
      sb.append('(').append(multiplicator).append(" * p").append(parameterIndex).append(')');
    } else {
      if (multiplicator.numerator() < 0) {
        sb.append(" - (");
      } else  {
        sb.append(" + (");
      }
      if (multiplicator.denominator() == 1 || multiplicator.numerator() == 0) {
        sb.append(Math.abs(multiplicator.numerator()));
      } else {
        sb.append("(").append(Math.abs(multiplicator.numerator())).append('/').append(multiplicator.denominator()).append(')');
      }
      sb.append(" * p").append(parameterIndex).append(')');
    }
  }

  public String toString() {
    return multiplicator + " * p" + parameterIndex;
  }

  @Override
  public int[] parametersIndexes() {
    return new int[] {parameterIndex};
  }

  int parameterIndex() {
    return parameterIndex;
  }

  RationalNumber multiplicator() {
    return multiplicator;
  }

  @Override
  public ParametrisedFormula replaceParameter(int index, ParametrisedFormula replacement) {
    if (index == parameterIndex) {
      // Apply
      return multiply(replacement, multiplicator);
    } else {
      return this;
    }
  }

  private static ParametrisedFormula multiply(ParametrisedFormula replacement, RationalNumber num) {
    switch (replacement) {
      case ConstantFormula cs -> {
        return new ConstantFormula(cs.result().multiply(num));
      }
      case SimpleParametrisedFormula spf -> {
        return new SimpleParametrisedFormula(spf.parameterIndex, spf.multiplicator.multiply(num));
      }
      case SumFormula sf -> {
        List<ParametrisedFormula> newFormulaList = sf.formulas().stream()
                                           .map(formula -> multiply(formula, num))
                                           .toList();
        return new SumFormula(newFormulaList);
      }
    }
  }
}
