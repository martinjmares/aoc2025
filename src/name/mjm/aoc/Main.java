package name.mjm.aoc;

import java.util.Arrays;
import java.util.Set;

import name.mjm.aoc.log.LogLevel;
import name.mjm.aoc.test.TestManager;
import name.mjm.aoc.test.TestManagerConfig;
import name.mjm.aoc.test.TestManagerConfig.Builder;
import name.mjm.aoc.test.TestManagerConfig.LogsConfig;

public class Main {

  private static void printHelp(String cmd) {
    boolean unknown = false;
    if (!"run".equals(cmd) && !"test".equals(cmd)) {
      unknown = true;
      System.out.println("USAGE: <command> [params]");
      System.out.println("Commands:");
    }

    if ("run".equals(cmd) || unknown) {
      System.out.println("  run: executes advent of code");
      System.out.println("     day related: all | last | <day>; (default: all)");
      System.out.println("     version related: vAll | vLast | v<version.minVersion>; (default: vLast)");
      System.out.println("     phase related: A | B | AB | ABLast; (default: ABLast)");
      System.out.println("     logs relates: logDebug | logInfo | logError; (default: logInfo)");
    }

    if ("test".equals(cmd) || unknown) {
      System.out.println("  test: executes tests and print logs only of failing");
      System.out.println("     test related: all | <testCalssname> | <testClassname.method>; (default: all)");
      System.out.println("     logs relates: logAll | logFailed; (default: logFailed)");
    }
  }

  void runTests(String... params) throws Exception {
    // Create configuration
    Builder builder = TestManagerConfig.builder();
    for (String param : params) {
      switch (param.trim().toLowerCase()) {
        case "all" -> builder.runAllTests();
        case "logall" -> builder.showLogs(LogsConfig.ALL);
        case "logfailed" -> builder.showLogs(LogsConfig.FAILING);
        default -> builder.addTestToRun(param);
      }
    }

    // List classes
    Set<Class> classes = ResourceUtils.findAllClassesRecursively(null);
    TestManager testManager = new TestManager(builder.build(), classes);
    testManager.run();
  }

  void runDays(String... params) throws Exception {
    // Create configuration
    CalendarManagerConfig.Builder builder = CalendarManagerConfig.builder();
    for (String param : params) {
      switch (param.trim().toLowerCase()) {
        case "logdebug" -> builder.logLevel(LogLevel.DEBUG);
        case "loginfo" -> builder.logLevel(LogLevel.INFO);
        case "logerror" -> builder.logLevel(LogLevel.ERROR);
        case "all" -> builder.execDays(CalendarManagerConfig.ExecDays.ALL);
        case "last" -> builder.execDays(CalendarManagerConfig.ExecDays.LAST);
        case "vall" -> builder.execVersions(CalendarManagerConfig.ExecVersions.ALL);
        case "vlast" -> builder.execVersions(CalendarManagerConfig.ExecVersions.LAST);
        case "a" -> builder.execPhases(CalendarManagerConfig.ExecPhases.A);
        case "b" -> builder.execPhases(CalendarManagerConfig.ExecPhases.B);
        case "ab" -> builder.execPhases(CalendarManagerConfig.ExecPhases.AB);
        case "ablast" -> builder.execPhases(CalendarManagerConfig.ExecPhases.LAST);
        case "trybefore" -> builder.execTries(CalendarManagerConfig.ExecTries.BEFORE);
        case "tryonly" -> builder.execTries(CalendarManagerConfig.ExecTries.ONLY);
        case "tryno" -> builder.execTries(CalendarManagerConfig.ExecTries.NO);
        default -> {
          if (param.startsWith("v")) {
            builder.selectedVersion(param.substring(1));
          } else {
            builder.selectedDay(Integer.parseInt(param));
          }
        }
      }
    }

    CalendarManager manager = new CalendarManager(builder.build(), Main.class.getPackageName(), "resources");
    manager.run();
  }

  public static void main(String[] args){

    if (args == null || args.length == 0) {
      printHelp(null);
      System.exit(1);
    }

    Main main = new Main();
    String command = args[0];
    String[] params = Arrays.copyOfRange(args, 1, args.length);
    try {
      switch (command) {
        case "test" -> main.runTests(params);
        case "run" -> main.runDays(params);
        default -> {
          System.out.println("Unknown command: " + command);
          printHelp(null);
        }
      }
    } catch (Throwable th) {
      System.err.println();
      System.err.println("FATAL ERROR: " + th.getMessage());
      th.printStackTrace(System.err);
      System.exit(2);
    }
  }
}