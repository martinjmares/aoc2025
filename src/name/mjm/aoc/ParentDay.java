package name.mjm.aoc;

import name.mjm.aoc.log.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ParentDay {

  private static final Pattern CLASS_NAME_PATTER = Pattern.compile("Day(\\d+)(v\\d+)?");
  private static final Pattern PACKAGE_NAME_PATTER = Pattern.compile("y(ear)?(\\d{2,4})");

  private final int year;
  private final int day;
  private final int version;
  protected final Logger logger = new Logger(this);

  protected ParentDay(int year, int day, int version) {
    this.year = year;
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
    // Year
    String pck = this.getClass().getPackageName();
    String[] split = pck.split("\\.");
    String sYear = null;
    for (String s : split) {
      Matcher ym = PACKAGE_NAME_PATTER.matcher(s);
      if (ym.matches()) {
        sYear = ym.group(2);
      }
    }
    if (sYear == null) {
      throw new IllegalArgumentException("Invalid package name for a Day class. There should be 'yearXXXX', but it is not: " + simpleName);
    }
    if (sYear.length() <= 3) {
      this.year = 2000 + Integer.parseInt(sYear);
    } else {
      this.year = Integer.parseInt(sYear);
    }
  }

  public int getDay() {
    return day;
  }

  public int getVersion() {
    return version;
  }

  public int getYear() {
    return year;
  }
}
