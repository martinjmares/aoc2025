package name.mjm.aoc;

import name.mjm.aoc.log.LogLevel;

public class CalendarManagerConfig {

  final LogLevel logLevel;
  final ExecYears execYears;
  final int selectedYear;
  final ExecDays execDays;
  final int selectedDay;
  final ExecVersions execVersions;
  final int selectedVersion;
  final int selectedMinVersion;
  final ExecPhases execPhases;
  final ExecTries execTries;

  private CalendarManagerConfig(Builder builder) {
    this.logLevel = builder.logLevel == null ? LogLevel.INFO : builder.logLevel;
    // Year
    this.execYears = builder.execYears == null ? ExecYears.LAST : builder.execYears;
    this.selectedYear = builder.selectedYear;
    // Day
    this.execDays = builder.execDays == null ? ExecDays.ALL : builder.execDays;
    this.selectedDay = builder.selectedDay;
    // Version
    this.execVersions = builder.execVersions == null ? ExecVersions.LAST : builder.execVersions;
    this.selectedVersion = builder.selectedVersion;
    this.selectedMinVersion = builder.selectedMinVersion;
    // Phase
    this.execPhases = builder.execPhases ==  null ? ExecPhases.LAST : builder.execPhases;
    // Tries
    this.execTries = builder.execTries == null ? ExecTries.BEFORE : builder.execTries;
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    // Day
    switch (execDays) {
      case ALL -> result.append("All implemented days, cililink\n");
      case LAST ->  result.append("Last implemented day only\n");
      case SELECTED -> result.append("Day ").append(selectedDay).append("\n");
    }
    // version
    switch (execVersions) {
      case ALL -> result.append("All versions\n");
      case LAST -> result.append("Last version\n");
      case SELECTED -> result.append("Version ").append(selectedVersion).append("\n");
    }
    // Phase
    switch (execPhases) {
      case A -> result.append("Phase A\n");
      case B -> result.append("Phase B\n");
      case AB ->  result.append("Both phases (A & B)\n");
      case LAST ->  result.append("Last implemented phase\n");
    }
    // Tries
    switch (execTries) {
      case BEFORE -> result.append("Try with test data before execute\n");
      case ONLY -> result.append("Just try with test data and don't execute\n");
      case NO -> result.append("Don't execute with test data, just run for real\n");
    }

    return result.toString();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private LogLevel logLevel;
    private ExecYears execYears;
    private int selectedYear = -1;
    private ExecDays execDays;
    private int selectedDay = -1;
    private ExecVersions execVersions;
    private int selectedVersion = -1;
    private int selectedMinVersion = -2;
    private ExecPhases execPhases;
    private ExecTries execTries;

    private Builder() {}

    public Builder logLevel(LogLevel logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    public Builder execYears(ExecYears execYears) {
      this.execYears = execYears;
      if (execYears != ExecYears.SELECTED) {
        this.selectedYear = -1;
      }
      return this;
    }

    public Builder selectedYear(int selectedYear) {
      this.selectedYear = selectedYear;
      this.execYears = ExecYears.SELECTED;
      return this;
    }

    public Builder execDays(ExecDays execDays) {
      this.execDays = execDays;
      if (execDays != ExecDays.SELECTED) {
        this.selectedDay = -1;
      }
      return this;
    }

    public Builder selectedDay(int selectedDay) {
      this.selectedDay = selectedDay;
      this.execDays = ExecDays.SELECTED;
      return this;
    }

    public Builder execVersions(ExecVersions execVersions) {
      this.execVersions = execVersions;
      if (execVersions != ExecVersions.SELECTED) {
        this.selectedVersion = -1;
        this.selectedMinVersion = -1;
      }
      return this;
    }

    public Builder selectedVersion(String selectedVersion) {
      int index = selectedVersion.indexOf('.');
      if (index == -1) {
        this.selectedVersion = Integer.parseInt(selectedVersion);
      } else {
        this.selectedVersion = Integer.parseInt(selectedVersion.substring(0, index));
        this.selectedMinVersion = Integer.parseInt(selectedVersion.substring(index + 1));
      }
      this.execVersions = ExecVersions.SELECTED;
      return this;
    }

    public Builder execPhases(ExecPhases execPhases) {
      this.execPhases = execPhases;
      return this;
    }

    public Builder execTries(ExecTries execTries) {
      this.execTries = execTries;
      return this;
    }

    public CalendarManagerConfig build() {
      return new CalendarManagerConfig(this);
    }
  }

  public enum ExecYears {
    ALL,
    LAST,
    SELECTED
  }

  public enum ExecDays {
    ALL,
    LAST,
    SELECTED
  }

  public enum ExecVersions {
    ALL,
    LAST,
    SELECTED
  }

  public enum ExecPhases {
    A,
    B,
    AB,
    LAST
  }

  public enum ExecTries {
    BEFORE,
    ONLY,
    NO
  }

}
