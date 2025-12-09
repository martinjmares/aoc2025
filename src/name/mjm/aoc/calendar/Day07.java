package name.mjm.aoc.calendar;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.CharMatrix;
import name.mjm.aoc.model.Coordinate;
import name.mjm.aoc.model.CoordinatesQueue;
import name.mjm.aoc.model.CoordinatesQueue.LCoordinate;

import java.lang.classfile.Opcode;
import java.util.Arrays;
import java.util.Spliterator;

public class Day07 extends ParentDay {

  private final char SPACE = '.';
  private final char SPLIT = '^';
  private final char BEAM = '|';
  private final char START = 'S';
  private final char OUT = ' ';

  Coordinate findStart(CharMatrix matrix) {
    for (int i = 0; i < matrix.rowCount(); i++) {
      char[] row = matrix.getRow(i);
      for (int j = 0; j < row.length; j++) {
        if (row[j] == START) {
          return new Coordinate(i, j);
        }
      }
    }
    throw new IllegalArgumentException("No start found");
  }

  @TryResult("21")
  public long a(CharMatrix matrix) {
    Coordinate start = findStart(matrix);
    CoordinatesQueue coordinates = new CoordinatesQueue();
    coordinates.add(start.row(), start.column());

    long result = 0;
    LCoordinate coord;
    while ((coord = coordinates.pull()) != null) {
      char c = matrix.get(coord.row(), coord.column(), OUT);
      switch (c) {
        case START -> coordinates.add(coord.row() + 1, coord.column());
        case SPACE -> {
          matrix.set(coord.row(), coord.column(), BEAM);
          coordinates.add(coord.row() + 1, coord.column());
        }
        case SPLIT -> {
          result++;
          coordinates.add(coord.row(), coord.column() + 1);
          coordinates.add(coord.row(), coord.column() - 1);
        }
      }
    }
    logger.debug(matrix::toString);
    return result;
  }

  @TryResult("40")
  public long b(CharMatrix matrix) {
    Coordinate start = findStart(matrix);
    char[] matRow = matrix.getRow(0);
    long[] prevRow = new long[matRow.length];
    Arrays.fill(prevRow, 0L);
    long[] curRow = new long[matRow.length];
    Arrays.fill(curRow, 0L);
    curRow[start.column()] = 1;
    for (int rowIndex = start.row() + 1; rowIndex < matrix.rowCount(); rowIndex++) {
      // cur -> prev
      var tmp = prevRow;
      prevRow = curRow;
      curRow = tmp;
      Arrays.fill(curRow, 0);
      char[] chars = matrix.getRow(rowIndex);
      for (int i = 0; i < prevRow.length; i++) {
        if (prevRow[i] > 0) {
          char ch = chars[i];
          switch (ch) {
            case SPACE -> curRow[i] = curRow[i] + prevRow[i]; // Beam(s) flows
            case SPLIT -> {
              // Beam(s) splits
              curRow[i - 1] = curRow[i - 1] + prevRow[i];
              curRow[i + 1] = curRow[i + 1] + prevRow[i];
            }
          }
        }
      }
      logger.debug("Row: " + (rowIndex + 1) + " = " + countArray(curRow));
    }

    return countArray(curRow);
  }

  private long countArray(long[] array) {
    return Arrays.stream(array)
                     .sum();
  }
}
