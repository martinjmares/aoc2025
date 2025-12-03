package name.mjm.aoc.calendar;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.Csv;
import name.mjm.aoc.model.IntPair;

import java.util.*;

public class Day02 extends ParentDay {

  private final Map<IntPair, Long> fullSegmentsCache = new HashMap<>();

  long pow(long x, long y) {
    long result = 1;
    for (int i = 0; i < y; i++) {
      result *= x;
    }
    return result;
  }

  long createIncrement(int partSize, int repeats) {
    long increment = 0L;
    for (int i = 0; i < repeats; i++) {
      increment += pow(pow(10L, partSize), i);
    }
    return increment;
  }

  void computeRepeatations(int partSize, int repeats, long minPart, long minValue, long maxValue, Set<Long> collector) {
//    boolean wholeSegment = minPart < 0 && maxValue < 0;
//    if (wholeSegment) {
//      // Try cache first
//      Long l = fullSegmentsCache.get(new IntPair(partSize, repeats));
//      if (l != null) {
//        logger.debug("computeRepeatations(partSize: "  + partSize + ", repeats: " + repeats + ", 'unbounded') - from cache");
//        return l;
//      }
//    }

    // Compute
    if (minPart < 0) {
      minPart = pow(10L, partSize - 1);
    }
    if (maxValue < 0) {
      maxValue = pow(10L, (partSize * repeats)) - 1L;
    }

    // Initial value
    long initialValue = 0;
    for (long i = 0; i < repeats; i++) {
      initialValue += minPart * pow(pow(10L, partSize), i);
    }

    // Increment
    long increment = createIncrement(partSize, repeats);

    // Check variants
    logger.debug("computeRepeatations(partSize: "  + partSize + ", repeats: " + repeats + ", minPart: " + minPart + ", maxValue: " + maxValue + "), increment: " + increment + ", initValue: " + initialValue );
    long counter = 0;
    long value = initialValue;
    while (value <= maxValue) {
      if (minValue <= value) {
        logger.debug("computeRepeatations(): find: " + value);
        counter++;
        collector.add(value);
      }
      value += increment;
    }
    //logger.debug("computeRepeatations(): find count: " + counter);

//    if (wholeSegment) {
//      fullSegmentsCache.put(new IntPair(partSize, repeats), result);
//    }
  }

  private long sumSaftely(Collection<Long> segments) {
    long sum = 0;
    for (long segment : segments) {
      sum = Math.addExact(sum, segment);
    }
    return sum;
  }

  @TryResult(tryId = 1, value = "1227775554")
  public long a(Csv csv) {
    List<Interval> intervals = loadIntervals(csv);
    Set<Long> results = new HashSet<>();
    for (Interval interval : intervals) {
      logger.info("Interval: " + interval);
      int lenStart = interval.sStart.length();
      int lenEnd = interval.sEnd.length();
      for (int len = lenStart; len <= lenEnd; len++) {
        if ((len % 2) == 0) {
          // je to sudy
          int repeats = 2;
          int partSize = len / 2;
          long minPart = len == lenStart ? Long.parseLong(interval.sStart.substring(0, partSize)) : -1L;
          long maxValue = len == lenEnd ? interval.end : -1L;
          computeRepeatations(partSize, repeats, minPart, interval.start, maxValue, results);
        }
      }
    }

    return sumSaftely(results);
  }

  @TryResult(tryId = 1, value = "4174379265")
  public long b(Csv csv) {
    List<Interval> intervals = loadIntervals(csv);
    Set<Long> results = new HashSet<>();
    for (Interval interval : intervals) {
      logger.info("Interval: " + interval);
      int lenStart = interval.sStart.length();
      int lenEnd = interval.sEnd.length();
      for (int len = lenStart; len <= lenEnd; len++) {
        for (int partSize = 1; partSize < len; partSize++) {
          if ((len % partSize) == 0) {
            // Valid part
            int repeats = len / partSize;
            long minPat = len == lenStart ? Long.parseLong(interval.sStart.substring(0, partSize)) : -1;
            long maxValue = len == lenEnd ? interval.end : -1;
            computeRepeatations(partSize, repeats, minPat, interval.start, maxValue, results);
          }
        }
      }
    }

    return sumSaftely(results);
  }

  private List<Interval> loadIntervals(Csv csv) {
    return csv.values()
        .stream()
        .map(String::trim)
        .map(s -> {
          int index = s.indexOf('-');
          if (index == -1) {
            throw new RuntimeException("Interval without dash! It is an invalid input.");
          }
          return new Interval(s.substring(0, index), s.substring(index + 1));
        })
        .toList();
  }

  static class Interval {
    final long start;
    final long end;
    final String sStart;
    final String sEnd;

    Interval(String sStart, String sEnd) {
      this.sStart = sStart;
      this.sEnd = sEnd;
      this.start = Long.parseLong(sStart);
      this.end = Long.parseLong(sEnd);
    }

    @Override
    public String toString() {
      return String.format("[%d-%d]", start, end);
    }
  }
}
