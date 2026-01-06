package name.mjm.aoc.y2025.forday12;

import java.util.*;

public class Present {

  /**
   * For simplicity and few optimisations, we are supporting only 3x3 shapes.
   * Why? Because it is like that in our adventofcode task
   */
  public static final int SIZE = 3;

  private final int id;
  private final boolean[][] baseShape;
  private final ArrayList<Variant> variants;

  private final int fillSize;

  public Present(List<String> lines) {
    // ID
    if (lines.size() != (SIZE + 1)) {
      throw new IllegalArgumentException("Accepting only 3x3 shapes!");
    }
    String line = lines.getFirst().trim();
    if (line.charAt(line.length() - 1) != ':') {
      throw new IllegalArgumentException("Expected as a last character ':' but got " + line);
    }
    this.id = Integer.parseInt(line.substring(0, line.length() - 1));

    // Shape
    this.baseShape = new boolean[SIZE][];
    for (int i = 1; i < lines.size(); i++) {
      line = lines.get(i).trim();
      if (line.length() != SIZE) {
        throw new IllegalArgumentException("Expecting uniform size of all lines 3 but got " + line.length());
      }
      boolean[] pattern = new boolean[SIZE];
      for (int j = 0; j < SIZE; j++) {
        pattern[j] = line.charAt(j) == '#';
      }
      this.baseShape[i-1] = pattern;
    }

    // Compute fillsize
    int fs = 0;
    for (boolean[] booleans : baseShape) {
      for (boolean b : booleans) {
        if (b) {
          fs++;
        }
      }
    }
    this.fillSize = fs;

    // Compute variants
    var rvariants = new HashSet<Variant>();
    var lastShape = baseShape;
    for (int rotation = 0; rotation < 4; rotation++) {
      // rotate
      boolean[][] rotShape = new boolean[3][3];
      for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
          rotShape[col][SIZE - 1 - row] = lastShape[row][col];
        }
      }
      rvariants.add(new Variant(rotShape));
      lastShape = rotShape;
      // flip
      boolean[][] flipShape = new boolean[3][3];
      for (int row = 0; row < SIZE; row++) {
        for (int col = 0; col < SIZE; col++) {
          flipShape[row][SIZE - 1 - col] = rotShape[row][col];
        }
      }
      rvariants.add(new Variant(flipShape));
    }
    this.variants = new ArrayList<>(rvariants);
  }

  public int getId() {
    return id;
  }

  public ArrayList<Variant> getVariants() {
    return variants;
  }

  public int getFillSize() {
    return fillSize;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(id).append(":\n");
    for (int rowIndex = 0; rowIndex < SIZE; rowIndex++) {
      for (Variant variant : variants) {
        boolean[] row = variant.shape[rowIndex];
        for (boolean b : row) {
          sb.append(b ? '#' : '.');
        }
        sb.append(" | ");
      }
      sb.append('\n');
    }
    return sb.toString();
  }

  public static class Variant {
    private final boolean[][] shape;
    private final int mask;

    private Variant(boolean[][] shape) {
      this.shape = shape;
      int tmask = 0;
      for (int rowIndex = 0; rowIndex < shape.length; rowIndex++) {
        boolean[] row = shape[rowIndex];
        for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
          if (row[columnIndex]) {
            tmask += 1 << (row.length * rowIndex + columnIndex);
          }
        }
      }
      this.mask = tmask;
    }

    public boolean[][] getShape() {
      return shape;
    }

    public int getMask() {
      return mask;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Variant variant = (Variant) o;
      return mask == variant.mask;
    }

    @Override
    public int hashCode() {
      return mask;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder(12);
      for (boolean[] row : shape) {
        for (boolean b : row) {
          sb.append(b ? '#' : '.');
        }
        sb.append('\n');
      }
      return sb.toString();
    }
  }
}
