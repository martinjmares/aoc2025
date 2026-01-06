package name.mjm.aoc.y2025;

import name.mjm.aoc.Named;
import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.PropertiesProvider;

import java.util.*;

import static name.mjm.aoc.NumberUtils.sqr;

public class Day08v2 extends ParentDay {

  @TryResult("40")
  public long a(List<GCoord> coords, @Named("props") PropertiesProvider propsProvider) {
    int maxLinksCount = Integer.parseInt(propsProvider.getProperties().getProperty("iterations"));

    Set<Group> groups = fillGroupsToCoords(coords);

    TreeSet<Link> links = constructShortestLinks(coords, maxLinksCount);

    groupLinks(links, groups);

    // Compute result
    logger.info("There are " + groups.size() + " groups. Compute result ...");
    ArrayList<Group> groupsList = new ArrayList<>(groups);
    groupsList.sort((o1, o2) -> (-1) * Integer.compare(o1.coords.size(), o2.coords.size()));
    long result = 1;
    for (int i = 0; i < 3; i++) {
      Group group = groupsList.get(i);
      logger.debug("Group size: " + group.coords.size());
      result *= group.coords.size();
    }
    return result;
  }

  @TryResult("25272")
  public long b(List<GCoord> coords) {
    Set<Group> groups = fillGroupsToCoords(coords);
    TreeSet<Link> links = constructShortestLinks(coords, -1);

    logger.info("We created " + links.size() + " links.");

    Link lastLink = groupLinks(links, groups);
    if (lastLink == null) {
      throw new RuntimeException("There is no last link to this group.");
    }
    return lastLink.a.x() * lastLink.b.x();
  }

  private static Set<Group> fillGroupsToCoords(List<GCoord> coords) {
    Set<Group> groups = new HashSet<>();
    for (GCoord coord : coords) {
      Group g = new Group();
      g.coords.add(coord);
      coord.group = g;
      groups.add(g);
    }
    return groups;
  }

  private Link groupLinks(TreeSet<Link> links, Set<Group> groups) {
    logger.info("Start to interconnect links ...");
    for (Link link : links) {
      logger.debug("Link: " +  link);
      GCoord a = link.a();
      GCoord b = link.b();
      // Join group
      Group toRemove = b.group;
      Group g = a.group;
      if (g != toRemove) {
        for (GCoord coord : toRemove.coords) {
          g.coords.add(coord);
          coord.group = g;
        }
        groups.remove(toRemove);
        if (groups.size() == 1) {
          return link;
        }
      }
    }
    return null;
  }

  private TreeSet<Link> constructShortestLinks(List<GCoord> coords, int maxLinksCount) {
    TreeSet<Link> links = new TreeSet<>();
    logger.info("Searching for " + (maxLinksCount > 0 ? String.valueOf(maxLinksCount) : "UNLIMITED") + " shortest links...");
    for (int i = 0; i < coords.size(); i++) {
      GCoord a =  coords.get(i);
      for (int j = i + 1; j < coords.size(); j++) {
        GCoord b =  coords.get(j);
        long distance = distance(a, b);

        // Add if it fits
        if (maxLinksCount <= 0 || links.size() < maxLinksCount || distance < links.getLast().distance) {
          links.add(new Link(a, b, distance));
          if (maxLinksCount > 0 && links.size() > maxLinksCount) {
            links.remove(links.last());
          }
        }
      }
    }
    return links;
  }

  long distance(GCoord a, GCoord b) {
    return sqr( a.x() - b.x()) + sqr( a.y() - b.y()) + sqr(a.z() - b.z());
  }

  record Link(GCoord a, GCoord b, long distance) implements Comparable<Link> {

    @Override
    public int compareTo(Link o) {
      return Long.compare(distance, o.distance);
    }

    public String toString() {
      return a.toString() + " -> " + b.toString();
    }
  }

  static class Group {
    final List<GCoord> coords = new LinkedList<>();
  }

  public static class GCoord {
    final Coord coord;
    Group group;

    public GCoord(Coord coord) {
      this.coord = coord;
    }

    public GCoord(String line) {
      this.coord = new Coord(line);
    }

    long x() {
      return coord.x;
    }

    long y() {
      return coord.y;
    }

    long z() {
      return coord.z;
    }

    public String toString() {
      return coord.toString();
    }
  }

  public record Coord(long x, long y, long z) {

    public Coord(String line) {
      line = line.trim();
      StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
      long x = Long.parseLong(stringTokenizer.nextToken());
      long y = Long.parseLong(stringTokenizer.nextToken());
      long z = Long.parseLong(stringTokenizer.nextToken());
      this(x, y, z);
    }

    public String toString() {
      return "[" + x + "," + y + "," + z + "]";
    }
  }
}
