package name.mjm.aoc;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

  public static String formatDuration(long startMillis, long endMillis) {
    return formatDuration(endMillis - startMillis);
  }

  public static String formatDuration(long durationMillis) {
    Duration sinceStart = Duration.of(durationMillis, ChronoUnit.MILLIS);

    StringBuilder sb = new StringBuilder(9);
    long minutes = sinceStart.toMinutes();
    if (minutes > 0) {
      sb.append(minutes).append(':');
      sinceStart = sinceStart.minusMinutes(minutes);
    }
    long sec = sinceStart.toSeconds();
    if (sec < 10) {
      sb.append('0');
    }
    sb.append(sec).append('.');
    sinceStart = sinceStart.minusSeconds(sec);
    long millis = sinceStart.toMillis();
    if (millis < 10) {
      sb.append("00");
    } else if (millis < 100) {
      sb.append('0');
    }
    sb.append(millis);

    return sb.toString();
  }
}