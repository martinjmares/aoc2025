package name.mjm.aoc.algebra;

import name.mjm.aoc.log.Logger;
import name.mjm.aoc.model.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class EquationSet {

  private final static Logger logger = new Logger(EquationSet.class);
  List<LinearEquation> equations;

  public EquationSet(List<LinearEquation> equations) {
    this.equations = new ArrayList<>(equations);
  }

  public EquationSet() {
    this.equations = new ArrayList<>();
  }

  public void addEquation(LinearEquation equation) {
    equations.add(equation);
  }

  public ParametrisedEquationSetResult solveLinearEquations() {
    // Build Matrix in staircase format
    int[][] matrix = toMatrix();
    toStaircase(matrix);
    // Create parametrised solution
    List<Pair<Integer, ParametrisedFormula>> assignedFormulas = new ArrayList<>(matrix.length);
    for (int i = (matrix.length - 1); i >= 0; i--) {
      int[] row = matrix[i];
      int firstNonZero = findFirstNonZero(row);
      if (firstNonZero == -1) {
        continue; // row full of zeros
      }
      // Build expression
      int divider = row[firstNonZero];
      ArrayList<ParametrisedFormula> sumFormulas = new ArrayList<>(row.length);
      for (int j = (firstNonZero + 1); j < row.length; j++) {
        if (j == row.length - 1) {
          // The last element is a constant
          ConstantFormula formula = new ConstantFormula(new RationalNumber(row[j], divider).simplify());
          sumFormulas.add(formula);
        } else {
          SimpleParametrisedFormula formula = new SimpleParametrisedFormula(j, new RationalNumber(row[j] * (-1), divider).simplify());
          sumFormulas.add(formula);
        }
      }
      ParametrisedFormula pf = new SumFormula(sumFormulas);
      logger.debug("Unresoved: x" + firstNonZero + " = " + pf);
      // Replace known variables
      for (Pair<Integer, ParametrisedFormula> assignedFormula : assignedFormulas) {
        pf = pf.replaceParameter(assignedFormula.x(), assignedFormula.y());
      }
      Pair<Integer, ParametrisedFormula> pair = new Pair<>(firstNonZero, pf);
      assignedFormulas.add(pair);
      logger.debug(() -> "Resolved:  x" + pair.x() + " = " + pair.y());
    }
    return new ParametrisedEquationSetResult(assignedFormulas);
  }

  int findFirstNonZero(int[] row) {
    int firstNonZero = -1;
    for (int j = 0; j < row.length; j++) {
      if (row[j] != 0) {
        firstNonZero = j;
        break;
      }
    }
    if (firstNonZero == row.length - 1) {
      throw new RuntimeException("Cannot solve equations because 0 == !0");
    }
    return firstNonZero;
  }

  void toStaircase(int[][] matrix) {
    int rowsSize = matrix.length;
    int colsSize = matrix[0].length;

    // To staircase form
    int leadCol = 0;
    int leadRow = 0;
    while (leadCol < (colsSize - 1) && leadRow < (rowsSize - 1)) {
      logger.debug(() -> "Solving Matrix (to staircase):\n" + matrixToString(matrix));
      // Swap if needed
      if (!swapToLeadOnRow(matrix, leadCol, leadRow)) {
        leadCol++; // Skip this, there is nothing free
        continue;
      }
      // Remove lead from following rows
      for (int i = leadRow + 1; i < rowsSize ; i++) {
        subtractToMakeLead(matrix[leadRow], matrix[i], leadCol);
      }
      // Mve both leads
      leadRow++;
      leadCol++;
    }
    logger.debug(() -> "Solving Matrix (in staircase):\n" + matrixToString(matrix));
  }

  void subtractToMakeLead(int[] upRow, int[] downRow, int leadCol) {
    if (downRow[leadCol] == 0) {
      return; // done
    }
    // I don't wont to care negative values
    if (upRow[leadCol] < 0) {
      multiplyAll(upRow, -1);
    }
    if (downRow[leadCol] < 0) {
      multiplyAll(downRow, -1);
    }
    // Find the smallest shared product
    int nsn = Algebra.nsn(upRow[leadCol], downRow[leadCol]);
    int upFactor = nsn / upRow[leadCol];
    int downFactor = nsn / downRow[leadCol];
    for (int i = leadCol; i < upRow.length; i++) {
      downRow[i] = (downRow[i] * downFactor) - (upRow[i] * upFactor);
    }
  }

  void multiplyAll(int[] row, int factor) {
    for (int i = 0; i < row.length; i++) {
      row[i] *= factor;
    }
  }


  boolean swapToLeadOnRow(int[][] matrix, int leadCol, int leadRow) {
    int[] row = matrix[leadRow];
    if (row[leadCol] != 0) {
      return true;
    }
    // We need to find
    for (int i = leadRow + 1; i < matrix.length; i++) {
      row = matrix[i];
      if (row[leadCol] != 0) {
        int[] temp = matrix[leadRow];
        matrix[leadRow] = row;
        matrix[i] = temp;
        return true;
      }
    }
    return false;
  }

  int[][] toMatrix() {
    int[][] matrix = new int[equations.size()][];
    // Find largest equation
    int size = 0;
    for (LinearEquation equation : equations) {
      if (equation.coefficients().length > size) {
        size = equation.coefficients().length;
      }
    }
    // Construct matrix
    for (int i = 0; i < equations.size(); i++) {
      LinearEquation equation = equations.get(i);
      int[] row = new int[size + 1];
      Arrays.fill(row, 0);
      System.arraycopy(equation.coefficients(), 0, row, 0, equation.coefficients().length);
      row[size] = equation.constant();
      matrix[i] = row;
    }
    return matrix;
  }

  String matrixToString(int[][] matrix) {
    int largestNumber = 0;
    boolean isNegative = false;
    for (int[] ints : matrix) {
      for (int anInt : ints) {
        int abs = Math.abs(anInt);
        if (abs >= largestNumber) {
          largestNumber = abs;
          if (anInt < 0) {
            isNegative = true;
          }
        }
      }
    }
    int numCharacters = String.valueOf(largestNumber).length() + (isNegative ? 1 : 0);

    StringBuilder sb = new StringBuilder();
    for (int[] row : matrix) {
      sb.append('[');
      for (int j = 0; j < row.length; j++) {
        if (j > 0) {
          sb.append(' ');
        }
        if (j == row.length - 1) {
          sb.append(" | ");
        }
        String str = String.valueOf(row[j]);
        sb.append(" ".repeat(Math.max(0, (numCharacters - str.length()))));
        sb.append(str);
      }
      sb.append("]\n");
    }
    return sb.toString();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (LinearEquation equation : equations) {
      sb.append(equation.toString()).append('\n');
    }
    return sb.toString();
  }

}
