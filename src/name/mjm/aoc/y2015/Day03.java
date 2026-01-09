package name.mjm.aoc.y2015;

import name.mjm.aoc.*;
import name.mjm.aoc.model.Coordinate;

import java.util.HashSet;
import java.util.Set;

@Datas({
    @Data(tryId = 1, value = "^>v<"),
    @Data(tryId = 1, value = "^v^v^v^v^v")
})
public class Day03 extends ParentDay {

  @TryResults(
      {
          @TryResult(tryId = 1, value = "4"),
          @TryResult(tryId = 2, value = "2")
      }
  )
  public int a(String input) {
    char[] charArray = input.trim().toCharArray();
    Set<Coordinate> coordinates = new HashSet<>();
    Coordinate coordinate = new Coordinate(0, 0);
    coordinates.add(coordinate);
    for (char c : charArray) {
      coordinate = move(coordinate, c);
      coordinates.add(coordinate);
    }
    return coordinates.size();
  }

  private Coordinate move(Coordinate coordinate, char move) {
    return switch (move) {
      case '<' -> coordinate.plus(0, -1);
      case '>' -> coordinate.plus(0, 1);
      case '^' -> coordinate.plus(-1, 0);
      case 'v' -> coordinate.plus(1, 0);
      default -> throw new IllegalArgumentException("unknown character: " + move);
    };
  }

  @TryResults(
      {
          @TryResult(tryId = 1, value = "3"),
          @TryResult(tryId = 2, value = "11")
      }
  )
  public int b(String input) {
    char[] charArray = input.trim().toCharArray();
    Set<Coordinate> coordinates = new HashSet<>();
    Coordinate scoord = new Coordinate(0, 0);
    Coordinate rcoord = scoord;
    coordinates.add(scoord);

    boolean sMove = true;
    for (char c : charArray) {
      if (sMove) {
        scoord = move(scoord, c);
        coordinates.add(scoord);
      } else {
        rcoord = move(rcoord, c);
        coordinates.add(rcoord);
      }
      sMove = !sMove;
    }

    return coordinates.size();
  }
}
