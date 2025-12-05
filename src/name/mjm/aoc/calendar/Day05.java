package name.mjm.aoc.calendar;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.Interval;
import name.mjm.aoc.model.Lines;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day05 extends ParentDay {

  @TryResult("3")
  public long a(Model model) {
    long counter = 0;
    // Primitive implementation
    for (long candidate : model.candidates) {
      for (Interval interval : model.intervals) {
        if (candidate >= interval.from() && candidate <= interval.to()) {
          counter++;
          break;
        }
      }
    }
    return counter;
  }

  SmashedInterval smashFirst(List<Interval> intervals) {
    if (intervals.isEmpty()) {
      return null;
    }

    List<Interval> otherIntervals = new ArrayList<>(intervals.size());
    Interval first = intervals.getFirst();
    for (Interval interval : intervals) {
      boolean joined = true;
      if (interval.from() <= first.from()) {
        // STARTS BEFORE
        if (interval.to() >= first.to()) {
          // AROUND
          first = interval;
        } else if (interval.to() >= first.from()) {
          // LEFT JOIN
          first = new Interval(interval.from(), first.to());
        } else  {
          joined = false;
        }
      } else if (interval.from() <= first.to()) {
        // STARTS IN
        if (interval.to() >= first.to()) {
          // RIGHT JOIN
          first = new Interval(first.from(), interval.to());
        }
        // ELSE -> INSIDE
      } else {
        joined = false;
      }
      if (!joined) {
        otherIntervals.add(interval);
      }
    }

    return new SmashedInterval(first, otherIntervals);
  }

  @TryResult("14")
  public long b(Model model) {
    List<Interval> intervals = model.intervals;

    // Smash intervals together
    long iterCounter = 0;
    List<Interval> disjunctive = new ArrayList<>();
    while (true) {
      SmashedInterval smashed = smashFirst(intervals);
      iterCounter++;
      if (smashed == null) {
        break;
      }
      if (smashed.rest.isEmpty()) {
        logger.debug("Smashed Iteration " + iterCounter + ", intervale: " + smashed.interval + " DONE");
        disjunctive.add(smashed.interval);
        break;
      }

      if (intervals.size() > (smashed.rest.size() + 1)) {
        // Something was removed, repeat
        intervals = smashed.rest;
        intervals.addFirst(smashed.interval);
        logger.debug("Smashed Iteration " + iterCounter + ", intervale: " + smashed.interval + " REPEAT");
      } else {
        disjunctive.add(smashed.interval);
        intervals = smashed.rest;
        logger.debug("Smashed Iteration " + iterCounter + "; MOVE ON; rest: " + smashed.rest.size());
      }
    }

    // just compute
    long counter = 0;
    for (Interval interval : disjunctive) {
      long size = interval.to() - interval.from() + 1;
      logger.debug("Subresult: " + interval + " ==> " + size);
      counter = Math.addExact(counter, size);
    }
    return counter;
  }

  record SmashedInterval(Interval interval, List<Interval> rest) {}

  public record Model(List<Interval> intervals, long[] candidates) {

    public Model(BufferedReader reader) throws IOException {
      boolean doCandidates = false;
      List<Long> candidates = new ArrayList<>();
      ArrayList<Interval> intervals = new ArrayList<>();

      String line = null;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) {
          doCandidates = true;
          continue;
        }
        if (doCandidates) {
          candidates.add(Long.parseLong(line.trim()));
        } else {
          Interval interval = Interval.fromString(line);
          intervals.add(interval);
        }
      }

      long[] candidateArray = candidates.stream().mapToLong(Long::longValue).toArray();
      this(intervals, candidateArray);
    }
  }
}
