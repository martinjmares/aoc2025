package name.mjm.aoc.y2025;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.CharMatrix;
import name.mjm.aoc.model.Coordinate;

public class Day04 extends ParentDay {

  private static final char PAPER = '@';
  private static final char SPACE = '.';

  @TryResult("13")
  public long a(CharMatrix matrix) {
    if (!matrix.isUniformLength()) {
      throw new RuntimeException("This matrix is not uniform");
    }

    long result = 0L;
    int rows = matrix.rowCount();
    int columns = matrix.columnCount();
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if  (matrix.get(row, column) == PAPER) {
          int paperCounter = countPapersAround(matrix, row, column);
          if (paperCounter < 4) {
            result++;
          }
        }
      }
    }

    return result;
  }

  private static int countPapersAround(CharMatrix matrix, int row, int column) {
    int paperCounter = 0;
    for (Coordinate transformation : CharMatrix.AROUND) {
      if (matrix.getAfterTransformation(row, column, transformation, SPACE) == PAPER) {
        paperCounter++;
      }
    }
    return paperCounter;
  }

  @TryResult("43")
  public long b(CharMatrix matrix) {
    if (!matrix.isUniformLength()) {
      throw new RuntimeException("This matrix is not uniform");
    }
    int rows = matrix.rowCount();
    int columns = matrix.columnCount();

    long iterationCounter = 0;
    long result = 0L;
    long circleResult = 1L;
    while (circleResult > 0) {
      iterationCounter++;
      logger.debug("Iteration: " + iterationCounter);
      circleResult = 0L;
      for (int row = 0; row < rows; row++) {
        for (int column = 0; column < columns; column++) {
          if (matrix.get(row, column) == PAPER) {
            int paperCounter = countPapersAround(matrix, row, column);
            if (paperCounter < 4) {
              circleResult++;
              matrix.set(row, column, SPACE);
            }
          }
        }
      }
      result += circleResult;
    }

    return result;
  }

}
