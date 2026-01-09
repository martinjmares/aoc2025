package name.mjm.aoc.y2015;

import name.mjm.aoc.ParentDay;

import java.util.Arrays;
import java.util.List;

public class Day06 extends ParentDay {

  public int a(List<Instruction> instructions) {
    boolean[] area = new boolean[1_000_000];
    Arrays.fill(area, false);

    for (Instruction instruction : instructions) {
      for (int row = instruction.rowA; row <= instruction.rowB; row++) {
        for (int col = instruction.colA; col <= instruction.colB; col++) {
          switch (instruction.action) {
            case ON -> area[row * 1000 + col] = true;
            case OFF -> area[row * 1000 + col] = false;
            case SWAP -> area[row * 1000 + col] = !area[row * 1000 + col];
          }
        }
      }
    }

    int result = 0;
    for (boolean b : area) {
      if (b) {
        result++;
      }
    }
    return result;
  }

  public long b(List<Instruction> instructions) {
    int[] area = new int[1_000_000];
    Arrays.fill(area, 0);

    for (Instruction instruction : instructions) {
      for (int row = instruction.rowA; row <= instruction.rowB; row++) {
        for (int col = instruction.colA; col <= instruction.colB; col++) {
          int index = row * 1000 + col;
          switch (instruction.action) {
            case ON -> area[index]++;
            case OFF -> {
              if (area[index] > 0) {
                area[index]--;
              }
            }
            case SWAP -> area[index] += 2;
          }
        }
      }
    }

    long result = 0;
    for (int i : area) {
      result += i;
    }
    return result;
  }

  public record Instruction(int rowA, int colA, int rowB, int colB, Action action) {
    public Instruction(String s) {
      Action act;
      if (s.startsWith(Action.ON.prefix)) {
        act = Action.ON;
        s = s.substring(Action.ON.prefix.length()).trim();
      } else if (s.startsWith(Action.OFF.prefix)) {
        act = Action.OFF;
        s = s.substring(Action.OFF.prefix.length()).trim();
      } else if (s.startsWith(Action.SWAP.prefix)) {
        act = Action.SWAP;
        s = s.substring(Action.SWAP.prefix.length()).trim();
      } else {
        throw new IllegalArgumentException("Prefix is not a valid action!");
      }

      String[] words = s.split(" ");
      String[] numsA = words[0].split(",");
      String[] numsB = words[2].split(",");
      this(Integer.parseInt(numsA[0]), Integer.parseInt(numsA[1]),
           Integer.parseInt(numsB[0]), Integer.parseInt(numsB[1]),
           act);
    }
  }

  enum Action {
    ON("turn on"),
    OFF("turn off"),
    SWAP("toggle");

    String prefix;

    Action(String prefix) {
      this.prefix = prefix;
    }
  }

}
