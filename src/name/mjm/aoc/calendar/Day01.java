package name.mjm.aoc.calendar;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class Day01 extends ParentDay {

  @TryResult(tryId = 1, value = "3")
  public Integer a(Moves moves) {
    int position = 50;
    int result = 0;
    for (Move move : moves.moves) {
      int size = move.size % 100;
      if (move.direction == Direction.RIGHT) {
        // RIGHT
        position += size;
        if (position >= 100) {
          position -= 100;
        }
      } else {
        // LEFT
        position -= size;
        if (position < 0) {
          position += 100;
        }
      }
      // Counting
      if (position == 0) {
        result++;
      }
    }

    return result;
  }

  @TryResult(tryId = 1, value = "6")
  public Integer b(Moves moves) {
    int position = 50;
    int result = 0;
    for (Move move : moves.moves) {
      result += move.size / 100;
      int size = move.size % 100;
      if (move.direction == Direction.RIGHT) {
        // RIGHT
        position += size;
        if (position >= 100) {
          position -= 100;
          result++;
        }
      } else {
        // LEFT
        if (position == 0) {
          result--;
        }
        position -= size;
        if (position < 0) {
          position += 100;
          result++;
        } else if (position == 0) {
          result++;
        }
      }
      logger.debug(move + " -> " + position + " (" + result + ")");
    }

    return result;
  }


  public static class Moves {
    final List<Move> moves;

    public Moves(BufferedReader reader) throws IOException {
      this.moves = reader.lines()
          .filter(line -> !line.isEmpty())
          .map(String::trim)
          .map(line -> {
            char ch = line.charAt(0);
            Direction direction = switch (ch) {
              case 'L' -> Direction.LEFT;
              case 'R' -> Direction.RIGHT;
              default -> throw new RuntimeException("Character " + ch + " is not a valid move.");
            };
            return new Move(direction, Integer.parseInt(line.substring(1)));
          })
          .toList();
    }
  }

  static class Move {
    final Direction direction;
    final int size;

    public Move(Direction direction, int size) {
      this.direction = direction;
      this.size = size;
    }

    @Override
    public String toString() {
      return direction.toString() + " " + size;
    }
  }

  enum Direction {
    LEFT,
    RIGHT
  }
}
