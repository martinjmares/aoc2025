package name.mjm.aoc.algebra;

import name.mjm.aoc.model.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ParametrisedEquationSetResult {

  private final ArrayList<Pair<Integer, ParametrisedFormula>> formulas;
  private final int[] parametersIndexes;

  ParametrisedEquationSetResult(List<Pair<Integer, ParametrisedFormula>> formulas) {
    this.formulas = new ArrayList<>(formulas);
    this.formulas.sort(Comparator.comparingInt(Pair::x));
    this.parametersIndexes = formulas.stream()
                          .map(Pair::y)
                          .map(ParametrisedFormula::parametersIndexes)
                          .flatMapToInt(Arrays::stream)
                          .distinct()
                          .toArray();
  }
  
  public RationalNumber[] solve(int... parameters) {
    RationalNumber[] results = new RationalNumber[formulas.size() + parametersIndexes.length];
    for (int parametersIndex : parametersIndexes) {
      results[parametersIndex] = new RationalNumber(parameters[parametersIndex]);
    }
    for (int i = 0; i < formulas.size(); i++) {
      Pair<Integer, ParametrisedFormula> pair = formulas.get(i);
      results[pair.x()] = pair.y().solve(parameters);
    }
    return results;
  }

  public int[] getParametersIndexes() {
    return parametersIndexes;
  }

  public int[] formulasIndexes() {
    return formulas.stream().mapToInt(Pair::x).toArray();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Pair<Integer, ParametrisedFormula> formula : formulas) {
      sb.append('x').append(formula.x()).append(" = ");
      sb.append(formula.y().toString());
      sb.append('\n');
    }
    return sb.toString();
  }
}
