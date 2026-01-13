package name.mjm.aoc.y2015;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

import java.util.*;

public class Day09 extends ParentDay {

  @TryResult("605")
  public int a(List<Distance> distances) {
    ArrayList<String> places = distinctPlaces(distances);
    int[][] placeToPlaces = computeDistancesMatrix(distances, places);
    // Backtrack
    boolean[] visited = new boolean[places.size()];
    Arrays.fill(visited, false);
    int[] path = new int[places.size()];
    Result result = new Result(new int[0], Integer.MAX_VALUE);
    for (int i = 0; i < places.size(); i++) {
      Result r = gotoRecursive(i, visited, path, 0, placeToPlaces, 0, result, true);
      if (r.distance < result.distance) {
        result = r;
      }
    }

    return result.distance;
  }

  @TryResult("982")
  public int b(List<Distance> distances) {
    ArrayList<String> places = distinctPlaces(distances);
    int[][] placeToPlaces = computeDistancesMatrix(distances, places);
    // Backtrack
    boolean[] visited = new boolean[places.size()];
    Arrays.fill(visited, false);
    int[] path = new int[places.size()];
    Result result = new Result(new int[0], 0);
    for (int i = 0; i < places.size(); i++) {
      Result r = gotoRecursive(i, visited, path, 0, placeToPlaces, 0, result, false);
      if (r.distance > result.distance) {
        result = r;
      }
    }

    return result.distance;
  }

  private int[][] computeDistancesMatrix(List<Distance> distances, ArrayList<String> places) {
    int[][] placeToPlaces = new int[places.size()][places.size()];
    for (int i = 0; i < places.size(); i++) {
      for (int j = 0; j < places.size(); j++) {
        int distance = i == j ? 0 : findDistance(distances, places.get(i), places.get(j));
        if (distance < 0) {
          throw new IllegalStateException("Missing distance for " + places.get(i) + " -> " + places.get(j));
        }
        placeToPlaces[i][j] = distance;
      }
    }
    return placeToPlaces;
  }

  private static ArrayList<String> distinctPlaces(List<Distance> distances) {
    Set<String> placesSet = new HashSet<>();
    for (Distance distance : distances) {
      placesSet.add(distance.aName);
      placesSet.add(distance.bName);
    }
    return new ArrayList<>(placesSet);
  }

  Result gotoRecursive(int destination, boolean[] visited, int[] path, int pathPointer, int[][] placesToPlaces, int distanceSoFar, Result bestResultSoFar, boolean shortest) {
    if (pathPointer > 0) {
      distanceSoFar += placesToPlaces[path[pathPointer - 1]][destination];
      // End soon if reach the limit
      if (shortest && bestResultSoFar.distance <= distanceSoFar) {
        return bestResultSoFar;
      }
    }
    path[pathPointer] = destination;
    // Am I last
    if (pathPointer == visited.length - 1) {
      return new Result(path, distanceSoFar);
    }
    // Iterate
    visited[destination] = true;
    for (int i = 0; i < visited.length; i++) {
      if (!visited[i]) {
        Result r = gotoRecursive(i, visited, path, pathPointer + 1, placesToPlaces, distanceSoFar, bestResultSoFar, shortest);
        if ((shortest && r.distance < bestResultSoFar.distance) || (!shortest && r.distance > bestResultSoFar.distance)) {
          bestResultSoFar = r;
        }
      }
    }
    visited[destination] = false;
    // return
    return bestResultSoFar;
  }

  record Result(int[] path, int distance) {
    public Result {
      path = path.clone();
    }
  }

  int findDistance(List<Distance> distances, String from, String to) {
    return distances.stream()
        .filter(d -> d.amI(from, to))
        .mapToInt(Distance::distance)
        .findAny()
        .orElse(-1);

  }

  public record Distance(String aName, String bName, int distance) {
    public Distance(String line) {
      String[] parts = line.split(" ");
      if (parts.length != 5 || !"to".equals(parts[1]) || !"=".equals(parts[3])) {
        throw new IllegalArgumentException("Unexpected format: " + line);
      }
      this(parts[0].trim(), parts[2], Integer.parseInt(parts[4].trim()));
    }

    boolean amI(String a, String b) {
      return (aName.equals(a) && bName.equals(b)) || (bName.equals(a) && aName.equals(b));
    }
  }
}
