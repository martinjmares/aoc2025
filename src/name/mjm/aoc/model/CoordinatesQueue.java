package name.mjm.aoc.model;

/**
 * Just for my personal fun. No intention to implement full Queue API. But still enjoying implementation
 * of such element after so many years.
 */
public class CoordinatesQueue {

  LCoordinate first;
  LCoordinate last;
  int size = 0;

  public LCoordinate getFirst() {
    return first;
  }

  public LCoordinate pull() {
    if (first == null) {
      return null;
    }
    var result = first;
    first = first.next;
    if (first == null) {
      last = null;
    }
    size--;
    return result;
  }

  public int size() {
    return size;
  }

  public void add(int row, int col) {
    LCoordinate coordinate = new LCoordinate(row, col);
    size++;
    if (first == null) {
      first = coordinate;
      last = coordinate;
    } else {
      last.next = coordinate;
      last = coordinate;
    }
  }

  public static class LCoordinate {
    private final int row;
    private final int column;
    private LCoordinate next = null;

    public LCoordinate(int row, int column) {
      this.row = row;
      this.column = column;
    }

    public int row() {
      return row;
    }

    public int column() {
      return column;
    }
  }
}
