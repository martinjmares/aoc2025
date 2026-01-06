package name.mjm.aoc.y2025.forday12;

import com.sun.source.tree.Tree;

public class TreeSpace {

  private final int rowsCount;
  private final int columnsCount;
  private final int[] presentsCounts;

  TreeSpace(String line) {
    int xIndex = line.indexOf('x');
    int colIndex = line.indexOf(':');
    this.rowsCount = Integer.parseInt(line.substring(0, xIndex));
    this.columnsCount = Integer.parseInt(line.substring(xIndex + 1, colIndex));

    // Itinerary
    String itinerary = line.substring(colIndex + 1).trim();
    String[] split = itinerary.split(" ");
    presentsCounts = new int[split.length];
    for (int i = 0; i < split.length; i++) {
      presentsCounts[i] = Integer.parseInt(split[i]);
    }
  }

  public int getRowsCount() {
    return rowsCount;
  }

  public int getColumnsCount() {
    return columnsCount;
  }

  public int[] getPresentsCounts() {
    return presentsCounts;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(rowsCount).append("x").append(columnsCount).append(": ");
    for (int presentsCount : presentsCounts) {
      sb.append(presentsCount).append(", ");
    }
    return sb.toString();
  }
}
