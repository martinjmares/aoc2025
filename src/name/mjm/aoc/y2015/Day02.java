package name.mjm.aoc.y2015;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

import java.util.List;

public class Day02 extends ParentDay {

  @TryResult("101")
  public long a(List<Dimensions> allDimensions) {
    return allDimensions.stream()
        .mapToLong(this::wrapping)
        .sum();
  }

  long wrapping(Dimensions d) {
    long a = d.x * d.y;
    long b = d.y * d.z;
    long c = d.z * d.x;
    long smallest = Math.min(a, Math.min(b, c));
    return 2 * (a + b + c) + smallest;
  }

  @TryResult("48")
  public long b(List<Dimensions> allDimensions) {
    return allDimensions.stream()
                        .mapToLong(this::ribbon)
                        .sum();
  }

  long ribbon(Dimensions d) {
    long a = 2L * (d.x + d.y);
    long b = 2L * (d.y + d.z);
    long c = 2L * (d.x + d.z);
    long cub = d.x * d.y * d.z;
    long shartest = Math.min(a, Math.min(b, c));
    return cub + shartest;
  }

  public record Dimensions(int x, int y, int z) {
    public Dimensions(String line) {
      String[] split = line.split("x");
      this(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }
  }
}
