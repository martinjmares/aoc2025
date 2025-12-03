package name.mjm.aoc.log;

public enum LogLevel {
  DEBUG(0, 'D'), INFO(1, 'I'), ERROR(2, 'E');

  private final int level;
  private final char shortcut;

  LogLevel(int level, char shortcut) {
    this.level = level;
    this.shortcut = shortcut;
  }

  public int getLevel() {
    return level;
  }

  public char getShortcut() {
    return shortcut;
  }
}
