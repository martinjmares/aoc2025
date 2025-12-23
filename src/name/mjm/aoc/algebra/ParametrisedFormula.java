package name.mjm.aoc.algebra;

public sealed interface ParametrisedFormula permits ConstantFormula, SimpleParametrisedFormula, SumFormula {

  int[] parametersIndexes();

  RationalNumber solve(int... parameters);

  ParametrisedFormula replaceParameter(int index, ParametrisedFormula replacement);

}
