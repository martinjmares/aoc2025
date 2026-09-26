package name.mjm.aoc.y2015;

import name.mjm.aoc.Data;
import name.mjm.aoc.Datas;
import name.mjm.aoc.Named;
import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Datas({
    @Data(tryId = 1, name = "ssec", value = "1000"),
    @Data(name = "ssec", value = "2503")
})
public class Day14 extends ParentDay {

  @TryResult(value = "1120")
  public int a(ArrayList<DeerSpeed> config, @Named("ssec") String ssec) {
    int sec = Integer.parseInt(ssec);
    logger.debug("Numeber of deers: " + config.size() + ", race for " + sec + " sec.");
    int maxDistance = -1;
    DeerSpeed maxDistanceDeer = null;
    for (DeerSpeed deerSpeed : config) {
      int distance = computeDistance(deerSpeed, sec);
      if (distance > maxDistance) {
        maxDistance = distance;
        maxDistanceDeer = deerSpeed;
      }
    }
    logger.info("max distance: " + maxDistance + ", by " + maxDistanceDeer.name());
    return maxDistance;
  }

  private int computeDistance(DeerSpeed deer, int sec) {
    int sequenceDuration = deer.secActive + deer.secPause;
    int sequences = sec / sequenceDuration;
    int remainingSec = sec % sequenceDuration;
    if (remainingSec > deer.secActive) {
      remainingSec = deer.secActive;
    }

    return ((sequences * deer.secActive) + remainingSec) * deer.speed;
  }

  public int b(ArrayList<DeerTracker> trackers, @Named("ssec") String ssec) {
    int sec = Integer.parseInt(ssec);
    ArrayList<DeerTracker> winnerTrackers = new ArrayList<>(trackers.size());
    for (int i = 0; i < sec; i++) {
      int maxDistance = -1;
      for (DeerTracker tracker : trackers) {
        int distance = tracker.addSecond();
        if (distance > maxDistance) { 
          winnerTrackers.clear();
        }
        if (distance >= maxDistance) {
          winnerTrackers.add(tracker);
          maxDistance = distance;
        }
      }
      for (DeerTracker winnerTracker : winnerTrackers) {
        winnerTracker.addPoint();
      }
    }
    // Find final winner
    DeerTracker realWinner  = trackers.get(0);
    for (DeerTracker tracker : trackers) {
      if (tracker.points > realWinner.points) {
        realWinner = tracker;
      }
    }
    logger.info("Real winner based on points: " + realWinner.deerSpeed.name + ", points: " + realWinner.points);
    return realWinner.points;
  }
  
  public static class DeerTracker {
    private enum State {
      RUNNING, RESTING;
    }
    private final DeerSpeed deerSpeed;
    private int points;
    private State state;
    private int secsInState;
    private int distance = 0;

    public DeerTracker(String line) {
      this.deerSpeed = new DeerSpeed(line);
      this.state = State.RUNNING;
      this.secsInState = deerSpeed.secActive;
      this.points = 0;
    }
    
    int addSecond() {
      switch (state) {
        case RUNNING -> {
          distance += deerSpeed.speed;
          if ((--secsInState) <= 0) {
            state = State.RESTING;
            secsInState = deerSpeed.secPause;
          }
        }
        case RESTING -> {
          if ((--secsInState) <= 0) {
            state = State.RUNNING;
            secsInState = deerSpeed.secActive;
          }
        }
      }
      return distance;
    }
    
    void addPoint() {
      points++;
    }
  }

  public record DeerSpeed(String name, int secActive, int speed, int secPause) {
    private static final Pattern REGEXP
        = Pattern.compile("([A-Za-z]+) can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds\\.");

    public DeerSpeed(String line) {
      Matcher matcher = REGEXP.matcher(line);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid line format: " + line);
      }
      this(matcher.group(1), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(4)));
    }
  }
}
