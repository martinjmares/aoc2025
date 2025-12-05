package name.mjm.aoc.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CharMatrix {

  public static final Coordinate UP = new Coordinate(-1, 0);
  public static final Coordinate DOWN = new Coordinate(1, 0);
  public static final Coordinate LEFT = new Coordinate(0, -1);
  public static final Coordinate RIGHT = new Coordinate(0, 1);
  public static final Coordinate UPRIGHT = new Coordinate(-1, 1);
  public static final Coordinate UPLEFT = new Coordinate(-1, -1);
  public static final Coordinate DOWNRIGHT = new Coordinate(1, 1);
  public static final Coordinate DOWNLEFT = new Coordinate(1, -1);

  public static final Coordinate[] AROUND = new Coordinate[]{UP, DOWN, LEFT, RIGHT, UPRIGHT, UPLEFT, DOWNRIGHT, DOWNLEFT};


  private final List<char[]> rows;
  private final boolean uniformLength;

  public CharMatrix(BufferedReader reader) throws IOException {
    List<char[]> lRows = new ArrayList<>();
    String line;
    int lastLength = -1;
    boolean uniformLength = true;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) {
        continue;
      }
      char[] row = line.toCharArray();
      if (uniformLength && lastLength != -1 && row.length != lastLength) {
        uniformLength = false;
      }
      lastLength = row.length;
      lRows.add(row);
    }

    this.uniformLength = uniformLength;
    this.rows = lRows;
  }

  public boolean isUniformLength() {
    return uniformLength;
  }

  public char get(int row, int column) {
    return rows.get(row)[column];
  }

  public char get(int row, int column, char defaultValue) {
    if (row < 0 || row >= rows.size()) {
      return defaultValue;
    }
    char[] chars = rows.get(row);
    if (column < 0 || column >= chars.length) {
      return defaultValue;
    }
    return chars[column];
  }

  public char getAfterTransformation(int row, int column, Coordinate transformation, char defaultValue) {
    return get(row + transformation.row(), column + transformation.column(), defaultValue);
  }

  public int rowCount() {
    return rows.size();
  }

  public int columnCount() {
    if (uniformLength) {
      return rows.getFirst().length;
    }  else {
      throw new RuntimeException("Cannot get column count for uniform length");
    }
  }

  public void set(int row, int column, char ch) {
    rows.get(row)[column] = ch;
  }
}
