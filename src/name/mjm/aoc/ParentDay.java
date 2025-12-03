package name.mjm.aoc;

import name.mjm.aoc.log.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ParentDay {

  private static final Pattern CLASS_NAME_PATTER = Pattern.compile("Day(\\d+)(v\\d+)?");

  private final int day;
  private final int version;
  protected final Logger logger = new Logger(this);

  protected ParentDay(int day, int version) {
    this.day = day;
    this.version = version;
  }

  protected ParentDay() {
    String simpleName = this.getClass().getSimpleName();
    Matcher matcher = CLASS_NAME_PATTER.matcher(simpleName);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid class name for a Day class: " + simpleName);
    }
    int dayNum = Integer.parseInt(matcher.group(1));
    String vGroup = matcher.group(2);
    int versionNum = vGroup == null ? 0 : Integer.parseInt(vGroup.substring(1));
    this.day = dayNum;
    this.version = versionNum;
  }

  public int getDay() {
    return day;
  }

  public int getVersion() {
    return version;
  }
}
