package name.mjm.aoc.algebra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public final class SumFormula implements ParametrisedFormula {

  private final ArrayList<ParametrisedFormula> formulas;

  SumFormula(List<ParametrisedFormula> formulas) {
    this.formulas = new ArrayList<>(formulas.size());
    for (ParametrisedFormula formula : formulas) {
      addFlatAndNotZero(formula, this.formulas);
    }
  }

  private static void addFlatAndNotZero(ParametrisedFormula formula, ArrayList<ParametrisedFormula> target) {
    switch (formula) {
      case ConstantFormula cf -> {
        if (cf.result().numerator() != 0) {
          target.add(cf);
        }
      }
      case SimpleParametrisedFormula spf -> {
        if (spf.multiplicator().numerator() != 0) {
          target.add(spf);
        }
      }
      case SumFormula sf -> {
        for (ParametrisedFormula f : sf.formulas) {
          addFlatAndNotZero(f, target);
        }
      }
    }
  }

  List<ParametrisedFormula> formulas() {
    return formulas;
  }

  @Override
  public RationalNumber solve(int... parameters) {
    RationalNumber result = RationalNumber.ZERO;
    for (ParametrisedFormula formula : formulas) {
      result = result.add(formula.solve(parameters));
    }
    return result;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (ParametrisedFormula formula : formulas) {
      if (formula instanceof ConstantFormula cf) {
        cf.addToComplexFormulaString(sb, first);
      } else if (formula instanceof SimpleParametrisedFormula spf) {
        spf.addToComplexFormulaString(sb, first);
      } else {
        sb.append(" <WHAT> ");
      }
      first = false;
    }
    return sb.toString();
  }

  @Override
  public int[] parametersIndexes() {
    HashSet<Integer> indexes = new HashSet<>();
    for (ParametrisedFormula formula : formulas) {
      int[] ints = formula.parametersIndexes();
      for (int index : ints) {
        indexes.add(index);
      }
    }
    return indexes.stream().mapToInt(Integer::intValue).toArray();
  }

  @Override
  public ParametrisedFormula replaceParameter(int index, ParametrisedFormula replacement) {
    ArrayList<ConstantFormula> constantFormulas = new ArrayList<>();
    ArrayList<SimpleParametrisedFormula> simpleFormulas = new ArrayList<>();
    for (ParametrisedFormula formula : formulas) {
      ParametrisedFormula newFormula = formula.replaceParameter(index, replacement);
      switch (newFormula) {
        case ConstantFormula cf -> constantFormulas.add(cf);
        case SimpleParametrisedFormula spf -> simpleFormulas.add(spf);
        case SumFormula sf -> {
          for (ParametrisedFormula subFormula : sf.formulas) {
            switch (subFormula) {
              case ConstantFormula cf -> constantFormulas.add(cf);
              case SimpleParametrisedFormula spf -> simpleFormulas.add(spf);
              case SumFormula subF -> throw new RuntimeException("Not implemented yet");
            }
          }
        }
      }
    }
    // Collect new sum formula
    ArrayList<ParametrisedFormula> newFormulas = new ArrayList<>(simpleFormulas.size() + 1);
    // Collect constants
    RationalNumber constant = RationalNumber.ZERO;
    for (ConstantFormula constantFormula : constantFormulas) {
      constant = constant.add(constantFormula.result());
    }
    newFormulas.add(new ConstantFormula(constant));
    // Connect simpleFormulas
    simpleFormulas.sort(Comparator.comparingInt(SimpleParametrisedFormula::parameterIndex));
    int lastIndex = -1;
    RationalNumber lastNum = null;
    for (SimpleParametrisedFormula sf : simpleFormulas) {
      if (lastIndex != sf.parameterIndex()) {
        // New index add it
        if (lastNum != null) {
          newFormulas.add(new SimpleParametrisedFormula(lastIndex, lastNum));
        }
        lastIndex = sf.parameterIndex();
        lastNum = sf.multiplicator();
      } else {
        if (lastNum != null) {
          lastNum = lastNum.add(sf.multiplicator());
        }
      }
    }
    if (lastNum != null) {
      newFormulas.add(new SimpleParametrisedFormula(lastIndex, lastNum));
    }

    return new SumFormula(newFormulas);
  }
}
