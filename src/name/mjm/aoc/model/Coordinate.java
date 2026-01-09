package name.mjm.aoc.model;

public record Coordinate(int row, int column) {
  public Coordinate(String line) {
    int index = line.indexOf(',');
    int x = Integer.parseInt(line.substring(0, index));
    int y = Integer.parseInt(line.substring(index + 1));
    this(x, y);
  }

  public Coordinate plus(int plusRow, int plusColumn) {
    return new Coordinate(this.row + plusRow, this.column + plusColumn);
  }
}
