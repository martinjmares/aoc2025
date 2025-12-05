package name.mjm.aoc.calendar;

import name.mjm.aoc.LongUtils;
import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.Csv;

import java.util.*;

public class Day02 extends ParentDay {

  long createIncrement(int partSize, int repeats) {
    long increment = 0L;
    for (int i = 0; i < repeats; i++) {
      increment += LongUtils.pow(LongUtils.pow(10L, partSize), i);
    }
    return increment;
  }

  void computeRepeatations(int partSize, int repeats, long minPart, long minValue, long maxValue, Set<Long> collector) {
    // Compute
    if (minPart < 0) {
      minPart = LongUtils.pow(10L, partSize - 1);
    }
    if (maxValue < 0) {
      maxValue = LongUtils.pow(10L, ((long) partSize * repeats)) - 1L;
    }

    // Initial value
    long initialValue = 0;
    for (long i = 0; i < repeats; i++) {
      initialValue += minPart * LongUtils.pow(LongUtils.pow(10L, partSize), i);
    }

    // Increment
    long increment = createIncrement(partSize, repeats);

    // Check variants
    logger.debug("computeRepeatations(partSize: "  + partSize + ", repeats: " + repeats + ", minPart: " + minPart + ", maxValue: " + maxValue + "), increment: " + increment + ", initValue: " + initialValue );
    long value = initialValue;
    while (value <= maxValue) {
      if (minValue <= value) {
        logger.debug("computeRepeatations(): find: " + value);
        collector.add(value);
      }
      value += increment;
    }
  }

  private long sumSaftely(Collection<Long> segments) {
    long sum = 0;
    for (long segment : segments) {
      sum = Math.addExact(sum, segment);
    }
    return sum;
  }

  @TryResult(value = "1227775554")
  public long a(Csv csv) {
    List<MyInterval> intervals = loadIntervals(csv);
    Set<Long> results = new HashSet<>();
    for (MyInterval interval : intervals) {
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

  @TryResult(value = "4174379265")
  public long b(Csv csv) {
    List<MyInterval> intervals = loadIntervals(csv);
    Set<Long> results = new HashSet<>();
    for (MyInterval interval : intervals) {
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

  private List<MyInterval> loadIntervals(Csv csv) {
    return csv.values()
        .stream()
        .map(String::trim)
        .map(s -> {
          int index = s.indexOf('-');
          if (index == -1) {
            throw new RuntimeException("Interval without dash! It is an invalid input.");
          }
          return new MyInterval(s.substring(0, index), s.substring(index + 1));
        })
        .toList();
  }

  static class MyInterval {
    final long start;
    final long end;
    final String sStart;
    final String sEnd;

    MyInterval(String sStart, String sEnd) {
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
