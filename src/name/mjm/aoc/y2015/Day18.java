package name.mjm.aoc.y2015;

import name.mjm.aoc.Data;
import name.mjm.aoc.Datas;
import name.mjm.aoc.Named;
import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

import java.util.ArrayList;

public class Day18 extends ParentDay {

  private static final byte ON = 1;
  private static final byte OFF = 0;

  @Datas({
      @Data(tryId = 1, name = "steps", value = "4"),
      @Data(name = "steps", value = "100")
  })
  @TryResult(value = "4")
  public int a(ArrayList<ByteRow> input, @Named("steps") int steps) {
    return doTheLiveAlgorithm(input, steps, false);
  }

  @Datas({
      @Data(tryId = 1, name = "steps", value = "5"),
      @Data(name = "steps", value = "100")
  })
  @TryResult(value = "17")
  public int b(ArrayList<ByteRow> input, @Named("steps") int steps) {
    return doTheLiveAlgorithm(input, steps, true);
  }

  public int doTheLiveAlgorithm(ArrayList<ByteRow> input, int steps, boolean cornersAlwaysOn) {
    byte[][] data = simplify(input);
    int rows = data.length;
    int cols = data[0].length;
    byte[][] nextData = new byte[rows][cols];

    if (cornersAlwaysOn) {
      data[0][0] = ON;
      data[rows - 1][0] = ON;
      data[rows - 1][cols - 1] = ON;
      data[0][cols - 1] = ON;
    }
    logger.debug("DATA [0]:\n" + toString(data));

    for (int step = 0; step < steps; step++) {
      for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
          byte around = countOnLightsAround(data, row, col);
          if (data[row][col] == ON) {
            // is on
            nextData[row][col] = (around == 2 || around == 3) ? ON : OFF;
          } else {
            nextData[row][col] = around == 3 ? ON : OFF;
          }
        }
      }
      var tmp = data;
      data = nextData;
      nextData = tmp;
      if (cornersAlwaysOn) {
        data[0][0] = ON;
        data[rows - 1][0] = ON;
        data[rows - 1][cols - 1] = ON;
        data[0][cols - 1] = ON;
      }
      logger.debug("DATA [" + step + "]:\n" + toString(data));
    }

    // count on
    int result = 0;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        result += data[row][col];
      }
    }
    return result;
  }

  byte countOnLightsAround(byte[][] input, int row, int col) {
    byte result = 0;
    if (row > 0) {
      result = countLeftRight(input[row - 1], col, true);
    }
    result += countLeftRight(input[row], col, false);
    if (row < input.length - 1) {
      result += countLeftRight(input[row + 1], col, true);
    }
    return result;
  }

  byte countLeftRight(byte[] row, int col, boolean includeMiddle) {
    byte result = 0;
    if (col > 0) {
      result = row[col - 1];
    }
    if (col < row.length - 1) {
      result += row[col + 1];
    }
    if (includeMiddle) {
      result += row[col];
    }
    return result;
  }

  private byte[][] simplify(ArrayList<ByteRow> input) {
    int lastSize = -1;
    byte[][] result = new byte[input.size()][];
    for (int i = 0; i < input.size(); i++) {
      ByteRow row = input.get(i);
      if (lastSize < 0) {
        lastSize = row.bytes.length;
      } else if (lastSize != row.bytes.length) {
        throw new  RuntimeException("Input is not a rectangle!");
      }
      result[i] = row.bytes;
    }
    return result;
  }

  private String toString(byte[][] data) {
    StringBuilder sb = new StringBuilder(data.length * data[0].length + data.length + 10);
    for (byte[] row : data) {
      for (int i = 0; i < row.length; i++) {
        sb.append(row[i] == ON ? '#' : '.');
      }
      sb.append('\n');
    }
    return sb.toString();
  }

  public record ByteRow(byte[] bytes) {
    public ByteRow(String line) {
      line = line.trim();
      char[] charArray = line.toCharArray();
      byte[] bytes = new byte[charArray.length];
      for (int i = 0; i < bytes.length; i++) {
        switch (charArray[i]) {
          case '.':
            bytes[i] = OFF;
            break;
          case '#':
            bytes[i] = ON;
            break;
          default:
            throw new IllegalArgumentException("Illegal character: " + charArray[i] + " in line '" + line + "'");
        }
      }
      this(bytes);
    }
  }
}
