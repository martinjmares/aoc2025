package name.mjm.aoc.y2025;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.Coordinate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class Day09 extends ParentDay {

  @TryResult("50")
  public long a(ArrayList<Coordinate> coordinates) {
    // Bruteforce approach
    long bestArea = 0;
    for (int i = 0; i < coordinates.size(); i++) {
      Coordinate a = coordinates.get(i);
      for (int j = i + 1; j < coordinates.size(); j++) {
        Coordinate b = coordinates.get(j);
        long area = area(a, b);
        if (area > bestArea) {
          bestArea = area;
        }
      }
    }
    return bestArea;
  }

  @TryResult("24")
  public long b(ArrayList<Coordinate> coordinates) {
    List<Horizontal> horizontals = toHorizontals(coordinates);
    //List<Vertical> verticals = toVerticals(coordinates);
    List<FilledColumn> filledColumns = toFilledColumns(horizontals);

    // Search rectangles
    long bestArea = 0;
    for (int i = 0; i < coordinates.size(); i++) {
      Coordinate a = coordinates.get(i);
      for (int j = i + 1; j < coordinates.size(); j++) {
        Coordinate b = coordinates.get(j);
        Rectangle rectangle = new Rectangle(a, b);
        long area = rectangle.area();
        if (area > bestArea && isFilled(rectangle, filledColumns)) {
          bestArea = area;
        }
      }
    }
    return bestArea;
  }

  boolean isFilled(Rectangle rectangle, List<FilledColumn> filledColumns) {
    int fromCol = rectangle.leftColumn;
    int toCol = rectangle.rightColumn;
    for (FilledColumn filledColumn : filledColumns) {
      if (filledColumn.left > fromCol) {
        // Missing the column at all
        return false;
      }
      if (filledColumn.right == fromCol) {
        // Special case, if not cover, we will continue by next
        boolean covered = filledColumn.intervals
            .stream()
            .anyMatch(inter -> inter.from <= rectangle.topRow && inter.to >= rectangle.bottomRow);
        if (covered) {
          fromCol++;
        }
      } else if (filledColumn.right >= fromCol) {
        boolean covered = filledColumn.intervals
            .stream()
            .anyMatch(inter -> inter.from <= rectangle.topRow && inter.to >= rectangle.bottomRow);
        if (covered) {
          fromCol = filledColumn.right + 1;
        } else {
          return false;
        }
      }
      // If all covered => true
      if (fromCol > toCol) {
        return true;
      }
    }
    return false;
  }

  List<FilledColumn> toFilledColumns(List<Horizontal> horizontalsList) {
    HashSet<Horizontal> horizontals = new HashSet<>(horizontalsList);
    List<FilledColumn> result = new ArrayList<>();

    var nextLefts = extractLefts(horizontals);
    while (!nextLefts.isEmpty()) {
      var left = nextLefts;
      nextLefts = extractLefts(horizontals);

      while (!left.isEmpty()) {
        if ((left.size() % 2) != 0) {
          throw new RuntimeException("We have an odd number of horizontals!");
        }
        // Bounderies
        int leftCol = left.getFirst().left;
        int rightCol = nextLefts.isEmpty() ? Integer.MAX_VALUE : nextLefts.getFirst().left;
        for (Horizontal horizontal : left) {
          if (horizontal.right < rightCol) {
            rightCol = horizontal.right;
          }
        }

        FilledColumn col = new FilledColumn(leftCol, rightCol);
        ArrayList<Horizontal> remainingLefts = new ArrayList<>();
        for (int i = 1; i < left.size(); i += 2) {
          Horizontal a = left.get(i - 1);
          Horizontal b = left.get(i);
          if (!col.intervals.isEmpty() && col.intervals.getLast().to == (a.row - 1)) {
            // Append
            Interval original = col.intervals.removeLast();
            col.intervals.add(new Interval(original.from, b.row));
          } else {
            col.intervals.add(new Interval(a.row, b.row));
          }
          if (rightCol < a.right) {
            remainingLefts.add(new Horizontal(a.row, rightCol, a.right));
          }
          if (rightCol < b.right) {
            remainingLefts.add(new Horizontal(b.row, rightCol, b.right));
          }
        }
        result.add(col);
        if (!remainingLefts.isEmpty()) {
          // Maybe we reach point of the next set
          if (!nextLefts.isEmpty() && nextLefts.getFirst().left == rightCol) {
            nextLefts.addAll(remainingLefts);
            nextLefts.sort(Comparator.comparingInt(Horizontal::row));
            left.clear();
          } else {
            left = remainingLefts;
          }
        } else {
          left.clear();
        }
      }
    }
    return result;
  }

  private static ArrayList<Horizontal> extractLefts(HashSet<Horizontal> horizontals) {
    ArrayList<Horizontal> lefts = new ArrayList<>();
    int left = Integer.MAX_VALUE;
    for (Horizontal horizontal : horizontals) {
      if (horizontal.left < left) {
        left = horizontal.left;
        lefts.clear();
        lefts.add(horizontal);
      } else if (horizontal.left == left) {
        lefts.add(horizontal);
      }
    }
    lefts.forEach(horizontals::remove);
    lefts.sort(Comparator.comparingInt(Horizontal::row));
    return lefts;
  }

  List<Horizontal> toHorizontals(ArrayList<Coordinate> coordinates) {
    List<Horizontal> horizontals = new ArrayList<>(coordinates.size() / 2 + 2);
    for (int i = 0; i < coordinates.size(); i++) {
      Coordinate a = i == 0 ? coordinates.getLast() : coordinates.get(i - 1);
      Coordinate b = coordinates.get(i);
      if (a.row() == b.row()) {
        Horizontal h;
        if (a.column() < b.column()) {
          h = new Horizontal(a.row(), a.column(), b.column());
        } else {
          h = new Horizontal(a.row(), b.column(), a.column());
        }
        horizontals.add(h);
      }
    }
    return horizontals;
  }

  List<Vertical> toVerticals(ArrayList<Coordinate> coordinates) {
    List<Vertical> verticals = new ArrayList<>(coordinates.size() / 2 + 2);
    for (int i = 0; i < coordinates.size(); i++) {
      Coordinate a = i == 0 ? coordinates.getLast() : coordinates.get(i - 1);
      Coordinate b = coordinates.get(i);
      if (a.column() == b.column()) {
        Vertical v;
        if (a.row() < b.row()) {
          v = new Vertical(a.column(), a.row(), b.row());
        } else {
          v = new Vertical(a.column(), b.row(), a.row());
        }
        verticals.add(v);
      }
    }
    return verticals;
  }

  long area(Coordinate a, Coordinate b) {
    return (((long) Math.abs(a.row() - b.row())) + 1) * (((long) Math.abs(a.column() - b.column())) + 1);
  }

  record Rectangle(int topRow, int leftColumn, int bottomRow, int rightColumn) {
    Rectangle(Coordinate a, Coordinate b) {
      int topRow;
      int leftColumn;
      int bottomRow;
      int rightColumn;
      if (a.row() < b.row()) {
        topRow = a.row();
        bottomRow = b.row();
      } else {
        topRow = b.row();
        bottomRow = a.row();
      }
      if (a.column() < b.column()) {
        leftColumn = a.column();
        rightColumn = b.column();
      } else  {
        leftColumn = b.column();
        rightColumn = a.column();
      }
      this(topRow, leftColumn, bottomRow, rightColumn);
    }

    long area() {
      return (((long) (bottomRow - topRow)) + 1) * (((long) (rightColumn - leftColumn)) + 1);
    }
  }

  static class FilledColumn {
    final int left;
    final int right;
    List<Interval> intervals = new ArrayList<>();

    FilledColumn(int left, int right) {
      this.left = left;
      this.right = right;
    }
  }

  record Interval(int from, int to) {}

  record Horizontal(int row, int left, int right) {
    public String toString() {
      return "-: " + row + "/" + left + "-" + right;
    }
  }

  record Vertical(int column, int top, int bottom) {
    public String toString() {
      return "|: " + column + "/" + top + "-" + bottom;
    }
  }
}
