package name.mjm.aoc.y2015;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 extends ParentDay {

  @TryResult("330")
  public int a(ArrayList<Naighbors> input) {
    Map<String, Integer> name2Id = name2Id(input);
    return processSittingArrangement(input, name2Id);
  }

  public int b(ArrayList<Naighbors> input) {
    Map<String, Integer> name2Id = name2Id(input);
    name2Id.put("me", name2Id.size());
    return processSittingArrangement(input, name2Id);
  }

  int processSittingArrangement(ArrayList<Naighbors> input, Map<String, Integer> name2Id) {
    EdgeValueFinder edges = new EdgeValueFinder(input, name2Id);

    BestTable bestTable = new BestTable(name2Id.size());
    boolean[] atTheTable = new boolean[name2Id.size()];
    Arrays.fill(atTheTable, false);
    int[] table = new int[name2Id.size()];
    table[0] = 0;
    atTheTable[0] = true;

    solveRecursive(edges, table, 1, 0, atTheTable, bestTable);

    logger.info("Winner table size: " + bestTable.value + " table: " + tableToString(table, name2Id));

    return bestTable.value;
  }

  String tableToString(int[] table, Map<String, Integer> name2Id) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < table.length; i++) {
      int personId = table[i];
      String name = name2Id.entrySet().stream()
                        .filter(entry -> entry.getValue() == personId)
                        .map(Map.Entry::getKey)
                        .findAny()
                        .orElse("Missing[" + i + "]");
      if (!sb.isEmpty()) {
        sb.append(" - ");
      }
      sb.append(name);
    }
    return sb.toString();
  }

  void solveRecursive(EdgeValueFinder edges, int[] table, int tablePointer, int currentValue, boolean[] atTheTable, BestTable bestTable) {
    for (int candidate = 0; candidate < atTheTable.length; candidate++) {
      if (atTheTable[candidate]) {
        continue; // Is already at the table, continue to the next candidate
      }
      // Sit guest
      table[tablePointer] = candidate;
      int addToValue = edges.get(table[tablePointer - 1], candidate);
      int nextValue = currentValue + addToValue;
      atTheTable[candidate] = true;

      // Call or end recursion
      if (tablePointer == table.length - 1) {
        // The last one
        // Also sits next to the first one
        nextValue += edges.get(table[0], candidate);
        if (nextValue > bestTable.value) {
          bestTable.value = nextValue;
          System.arraycopy(table, 0, bestTable.table, 0, table.length);
        }
      } else {
        // continue recursion
        solveRecursive(edges, table, tablePointer + 1, nextValue, atTheTable, bestTable);
      }

      // Get back for next iteration
      atTheTable[candidate] = false;
    }
  }




  Map<String, Integer> name2Id(List<Naighbors>  neighbors) {
    Map<String, Integer> name2Id = new HashMap<>();
    int index = 0;
    for (Naighbors neighbor : neighbors) {
      if (!name2Id.containsKey(neighbor.nameA))  {
        name2Id.put(neighbor.nameA, index++);
      }
    }
    return name2Id;
  }

  static class BestTable {
    int[] table;
    int value = Integer.MIN_VALUE;

    public BestTable(int tableSize) {
      this.table = new int[tableSize];
    }
  }

  static class EdgeValueFinder {
    private final int[][] values;

    EdgeValueFinder(List<Naighbors> neighbors, Map<String, Integer> name2Id) {
      values = new int[name2Id.size()][name2Id.size()];
      // Null it
      for (int i = 0; i < values.length; i++) {
        Arrays.fill(values[i], 0);
      }
      // Compute it
      for (Naighbors neighbor : neighbors) {
        values[name2Id.get(neighbor.nameA)][name2Id.get(neighbor.nameB)] += neighbor.value;
        values[name2Id.get(neighbor.nameB)][name2Id.get(neighbor.nameA)] += neighbor.value;
      }
    }

    int get(int a, int b) {
      return values[a][b];
    }
  }

  public record Naighbors(String nameA, String nameB, int value) {

    private static final Pattern REGEXP
        = Pattern.compile("([A-Za-z]+) would (lose|gain) (\\d+) happiness units by sitting next to ([A-Za-z]+)\\.");

    public Naighbors(String line) {
      Matcher matcher = REGEXP.matcher(line);
      String nA;
      String nB;
      int amount;
      if (matcher.matches()) {
        nA = matcher.group(1);
        String lg = matcher.group(2);
        String samount = matcher.group(3);
        amount = Integer.parseInt(samount);
        if (lg.equals("lose")) {
          amount *= -1;
        }
        nB = matcher.group(4);
      } else  {
        throw new IllegalArgumentException("Invalid line format: " + line);
      }
      this(nA, nB, amount);
    }
  }
}
