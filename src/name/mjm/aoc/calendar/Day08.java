package name.mjm.aoc.calendar;

import name.mjm.aoc.*;
import name.mjm.aoc.model.PropertiesProvider;

import java.util.*;

import static name.mjm.aoc.NumberUtils.sqr;

// Wrong approch !!!
public class Day08 extends ParentDay {


  @TryResult("40")
  public long a(List<Coord> coords, @Named("props") PropertiesProvider propsProvider) {
    int iterationsCount = Integer.parseInt(propsProvider.getProperties().getProperty("iterations"));
    logger.info("iterationsCount: " + iterationsCount);

    // Compute closest distances
    ArrayList<LinkedCoord> linkedCoords = toLinkedCoords(coords);
    Set<Group> groups = new HashSet<>();
    for (int i = 0; i < linkedCoords.size(); i++) {
      var lCoord = linkedCoords.get(i);

      // Find closest in following
      LinkedCoord closest = null;
      long closeDistance = Long.MAX_VALUE;
      for (int j = (i + 1); j < linkedCoords.size(); j++) {
        LinkedCoord nextCoord = linkedCoords.get(j);
        long d = distance(lCoord.coord, nextCoord.coord);
        if (d < closeDistance) {
          closest = nextCoord;
          closeDistance = d;
        }
      }

      if (closest != null) {
        // A
        if (lCoord.closest == null || lCoord.distance > closeDistance) {
          lCoord.closest = closest.coord;
          lCoord.distance = closeDistance;
        }
        // B
        if (closest.closest == null || closest.distance > closeDistance) {
          closest.closest = lCoord.coord;
          closest.distance = closeDistance;
        }
      }
    }
    logger.debug("Closest distances find DONE");

    // Find shortest UNGROUPED
    return -1;
  }

  long distance(IdCoord a, IdCoord b) {
    return sqr( a.x() - b.x()) + sqr( a.y() - b.y()) + sqr(a.z() - b.z());
  }

  ArrayList<LinkedCoord> toLinkedCoords(List<Coord> coords) {
    ArrayList<LinkedCoord> result = new ArrayList<>(coords.size());
    int id = 0;
    for (Coord coord : coords) {
      result.add(new LinkedCoord(new IdCoord(id++, coord)));
    }
    return result;
  }

  static class LinkedCoord {
    final IdCoord coord;
    IdCoord closest;
    long distance;
    Group group;

    LinkedCoord(IdCoord coord) {
      this.coord = coord;
    }

    void setClosest(IdCoord closest, long distance) {
      this.closest = closest;
      this.distance = distance;
    }
  }

  static class Group {
    final List<IdCoord> coords = new LinkedList<>();
  }

  record IdCoord(int id, Coord coord) {
    long x() {
      return coord.x();
    }

    long y() {
      return coord.y();
    }

    long z() {
      return coord.z();
    }
  }

  public record Coord(long x, long y, long z) {
    public  Coord(String line) {
      line = line.trim();
      StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
      long x = Long.parseLong(stringTokenizer.nextToken());
      long y = Long.parseLong(stringTokenizer.nextToken());
      long z = Long.parseLong(stringTokenizer.nextToken());
      this(x, y, z);
    }
  }
}
