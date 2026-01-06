package name.mjm.aoc;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import name.mjm.aoc.log.Logger;
import name.mjm.aoc.log.LoggingConfig;

public class CalendarManager implements Runnable{

  private final Logger logger = new Logger(this);
  private final CalendarManagerConfig config;
  private final Set<Class> dayClasses;
  private final Map<Integer, DayResourcesProvider> yearResourcesProvider;
  private final DayResourcesProvider baseResourcesProvider;

  public CalendarManager(CalendarManagerConfig config, String classesPackage, String resourceDirectory) throws Exception {
    this.config = config;
    Set<Class> classes = ResourceUtils.findAllClassesRecursively(classesPackage);
    this.dayClasses = classes.stream()
        .filter(c -> !Modifier.isAbstract(c.getModifiers()))
        .filter(ParentDay.class::isAssignableFrom)
        .collect(Collectors.toSet());
    this.yearResourcesProvider = DayResourcesProvider.createForYears(resourceDirectory);
    this.baseResourcesProvider = new DayResourcesProvider(resourceDirectory);
  }

  private Stats executeDayPhase(MinVersion minVersion, Parameter[] parameters, Type[] paramTypes, int tryId, DayResourcesProvider resourcesProvider, String expectedResult) {
    Method method = minVersion.method;
    PhaseDef phaseDef = minVersion.phaseDef;
    int dayId = minVersion.target.getDay();
    int yearId = minVersion.target.getYear();
    long startTime = System.currentTimeMillis();

    try {
      // Heder
      logger.println();
      logger.println(Logger.F_SINGLE_SEPARATOR);
      StringBuilder caption = new StringBuilder();
      caption.append("Day ").append(yearId).append("..").append(dayId);
      caption.append(" - ").append(phaseDef);
      if (minVersion.target.getVersion() > 0 || minVersion.minVersion > 0) {
        caption.append(" (version ")
               .append(minVersion.target.getVersion())
               .append('.')
               .append(minVersion.minVersion)
               .append(')');
      }
      if (tryId >= 0) {
        caption.append(" - try ").append(tryId);
      }
      logger.println(Logger.f_CAPTION(caption.toString()));
      logger.println(Logger.F_SINGLE_SEPARATOR);

      // Load parameters
      List paramValues = new ArrayList(parameters.length);
      for (int i = 0; i < parameters.length; i++) {
        Named named = parameters[i].getAnnotation(Named.class);
        String name = named != null ? (named.value() == null ? "" : named.value()) : "";
        String resourceName = resourcesProvider.findResource(dayId, phaseDef, tryId, name);
        if (resourceName == null) {
          if (name.isEmpty()) {
            throw new RuntimeException("Cannot data (resource) for method parameter '" + method.getName() + "'.");
          } else {
            throw new RuntimeException("Cannot data (resource) for named method parameter '" + method.getName() + "', name: " + name);
          }
        }
        Object value = ResourceUtils.loadResource(resourceName, parameters[i].getType(), paramTypes[i]);
        if (value == null) {
          throw new RuntimeException("Cannot construct value for method parameter '" + method.getName() + "', Class: " + parameters[i].getType().getName());
        }
        paramValues.add(value);
      }

      // Create new instance for each rum
      Constructor<? extends ParentDay> constructor = minVersion.target.getClass().getConstructor();
      constructor.setAccessible(true);
      ParentDay target = constructor.newInstance();

      // Run in separated thread
      LoggingConfig loggingConfig = LoggingConfig.builder()
                                         .minLevel(config.logLevel)
                                         .build();
      AtomicReference<String> result = new AtomicReference<>();
      AtomicReference<Throwable> error = new AtomicReference<>();
      Thread thread = Thread.ofVirtual()
                           .name("Day " + dayId + " " + phaseDef)
                           .start(() ->
                             ScopedValue.where(Logger.LOGGING_CONFIG, loggingConfig).run(() -> {
                               try {
                                 Object r = method.invoke(target, paramValues.toArray(new Object[0]));
                                 if (r != null) {
                                   result.set(String.valueOf(r));
                                 }
                               } catch (InvocationTargetException ite) {
                                 if (ite.getCause() != null) {
                                   error.set(ite.getCause());
                                 } else {
                                   error.set(ite);
                                 }
                               } catch (Throwable throwable) {
                                 error.set(throwable);
                               }
                             })
                           );
      thread.join();

      logger.println();
      Throwable throwable = error.get();
      if (throwable != null) {
        logger.println(">>> ERROR!");
        logger.printThrowable(throwable);
        return new Stats(tryId, Result.CANNOT_EXECUTE, null, expectedResult, System.currentTimeMillis() - startTime, throwable);
      }

      String sRes = result.get();
      if (sRes == null) {
        logger.println(">>> MISSING RESULT!");
        return new Stats(tryId, Result.UNEXPECTED_RESULT, null, expectedResult, System.currentTimeMillis() - startTime, null);
      }

      if (expectedResult != null && !sRes.equals(expectedResult)) {
        logger.println(">>> UNEXPECTED RESULT!  result '" + sRes + "' != '" + expectedResult + "'");
        return new Stats(tryId, Result.UNEXPECTED_RESULT, sRes, expectedResult, System.currentTimeMillis() - startTime, null);
      }

      logger.println(">>> RESULT: " + sRes);
      return new Stats(tryId, Result.OK, sRes, expectedResult, System.currentTimeMillis() - startTime, null);

    } catch (Throwable t) {
      logger.println();
      logger.println(">>> EXECUTION FAILED!");
      logger.printThrowable(t);

      return new Stats(tryId, Result.CANNOT_EXECUTE, null, expectedResult, System.currentTimeMillis() - startTime, t);
    }
  }

  private void runIncludingTries(MinVersion minVersion) {
    int dayId = minVersion.target.getDay();
    PhaseDef phaseDef = minVersion.phaseDef;
    Method method = minVersion.method;
    method.setAccessible(true);
    Parameter[] parameters = method.getParameters();
    Type[] genericParameterTypes = method.getGenericParameterTypes();
    DayResourcesProvider resourcesProvider = yearResourcesProvider.get(minVersion.target.getYear());
    if  (resourcesProvider == null) {
      resourcesProvider = baseResourcesProvider;
    }


    // Tries
    boolean triesOk = true;
    if (config.execTries == CalendarManagerConfig.ExecTries.BEFORE || config.execTries == CalendarManagerConfig.ExecTries.ONLY) {
      // Find results for tries
      Map<Integer, String> expectedTryResults = new HashMap<>();
      TryResult tres = method.getAnnotation(TryResult.class);
      if (tres != null) {
        expectedTryResults.put(tres.tryId(), tres.value());
      }
      TryResults tress = method.getAnnotation(TryResults.class);
      if (tress != null) {
        for (TryResult tryResult : tress.value()) {
          expectedTryResults.put(tryResult.tryId(), tryResult.value());
        }
      }

      // Execute tries
      List<Integer> tryIds = resourcesProvider.getTryIds(dayId, phaseDef);
      for (Integer tryId : tryIds) {
        Stats stats = executeDayPhase(minVersion, parameters, genericParameterTypes, tryId, resourcesProvider, expectedTryResults.get(tryId));
        if (stats.result != Result.OK) {
          triesOk = false;
        }
        minVersion.stats.add(stats);
      }
    }

    if (triesOk && config.execTries != CalendarManagerConfig.ExecTries.ONLY) {
      Stats stats = executeDayPhase(minVersion, parameters, genericParameterTypes, -1, resourcesProvider, null);
      minVersion.stats.add(stats);
    }
  }

  private void runMinVersions(Phase phase) {
    logger.debug("runMinVersions(" + phase + ")" );

    if (phase.minVersions.isEmpty()) {
      return;
    }

    List<MinVersion> minVersionsToRun = switch(config.execVersions) {
      case ALL -> phase.minVersions.values().stream().sorted().toList();
      case LAST -> List.of(phase.minVersions.values().stream().sorted().toList().getLast());
      case SELECTED -> {
        if (config.selectedMinVersion < 0) {
          yield List.of(phase.minVersions.values().stream().sorted().toList().getLast());
        } else {
          MinVersion minVersion = phase.minVersions.get(config.selectedMinVersion);
          if (minVersion == null) {
            yield List.of();
          } else {
            yield List.of(minVersion);
          }
        }
      }
    };

    // Run It
    for (MinVersion minVersion : minVersionsToRun) {
      runIncludingTries(minVersion);
    }
  }

  private void runPhases(Version version) {
    logger.debug("runPhases(" + version + ")");

    // Find phase relevant methods
    ParentDay target = version.target;
    Method[] declaredMethods = target.getClass().getDeclaredMethods();
    for (Method method : declaredMethods) {
      for (PhaseDef phaseDef : PhaseDef.values()) {
        Matcher matcher = phaseDef.methodPattern.matcher(method.getName());
        if (matcher.matches()) {
          String strMinVer = matcher.group(1);
          int minVer = strMinVer.isEmpty() ? 0 : Integer.parseInt(strMinVer);
          MinVersion minVersion = new MinVersion(minVer, version.target, method, phaseDef);
          version.phases
              .computeIfAbsent(phaseDef, Phase::new)
              .minVersions
              .put(minVer, minVersion);
        }
      }
    }

    // Run relevant
    switch (config.execPhases) {
      case AB -> {
        Phase a = version.phases.get(PhaseDef.A);
        if (a != null && !a.minVersions.isEmpty()) {
          runMinVersions(a);
        }
        Phase b = version.phases.get(PhaseDef.B);
        if (b != null && !b.minVersions.isEmpty()) {
          runMinVersions(b);
        }
      }
      case A -> {
        Phase a = version.phases.get(PhaseDef.A);
        if (a != null && !a.minVersions.isEmpty()) {
          runMinVersions(a);
        }
      }
      case B -> {
        Phase b = version.phases.get(PhaseDef.B);
        if (b != null && !b.minVersions.isEmpty()) {
          runMinVersions(b);
        }
      }
      case LAST -> {
        Phase b = version.phases.get(PhaseDef.B);
        if (b != null && !b.minVersions.isEmpty()) {
          runMinVersions(b);
        } else {
          Phase a = version.phases.get(PhaseDef.A);
          if (a != null && !a.minVersions.isEmpty()) {
            runMinVersions(a);
          }
        }
      }
    }
  }

  private void runVersions(Day day) {
    logger.debug("runVersions(" + day + ")");
    Map<Integer, Version> versions = day.versions;
    if (versions.isEmpty()) {
      logger.error("There are no versions for this day " + day.day + "! It should never happen!");
      return;
    }
    List<Version> verToRun = switch (config.execVersions) {
          case LAST -> List.of(versions.values()
                                       .stream()
                                       .sorted()
                                       .toList()
                                       .getLast());
          case SELECTED -> {
            Version version = versions.get(config.selectedVersion);
            if (version == null) {
              logger.error("There is no selected version (" + config.selectedVersion + ") for day " + day.day + "! ");
              yield List.of();
            } else {
              yield List.of(version);
            }
          }
          case ALL -> {
            ArrayList<Version> res = new ArrayList<>(versions.values());
            res.sort(Version::compareTo);
            yield res;
          }
        };

    // Going on
    for (Version version : verToRun) {
      runPhases(version);
    }
  }

  List<Day> runDays() {
    // Construct relevant Days from
    Map<Integer, Map<Integer, Day>> yearDayMap = new HashMap<>();
    dayClasses.stream()
        .map(c -> {
              try {
                return c.getDeclaredConstructor().newInstance();
              } catch (Exception e) {
                logger.error("Cannot instantiate day class " + c.getName(), e); return null;
              }
            })
        .filter(Objects::nonNull)
        .map(o -> (ParentDay) o)
        .forEach(day -> {
          Map<Integer, Day> year = yearDayMap.computeIfAbsent(day.getYear(), _ -> new HashMap<>());
          Day d = year.computeIfAbsent(day.getDay(), _ -> new Day(day.getYear(), day.getDay()));
          d.versions.put(day.getVersion(), new Version(day));
        });

    if (yearDayMap.isEmpty()) {
      logger.error("There are no days.");
      return List.of();
    }

    // Select year(s) to execute and put days to a Liat
    ArrayList<Day> daysToExecute = switch (config.execYears) {
      case ALL -> yearDayMap.values()
                         .stream()
                         .flatMap(map -> map.values().stream())
                         .collect(Collectors.toCollection(ArrayList::new));
      case SELECTED -> {
        var years = yearDayMap.get(config.selectedYear);
        if (years == null) {
          yield new ArrayList<>();
        } else {
          yield new ArrayList<>(years.values());
        }
      }
      case LAST -> {
        Integer lastYear = yearDayMap.keySet()
                                    .stream()
                                    .max(Comparator.naturalOrder())
                                    .orElseThrow(() -> new IllegalStateException("There is no year. Impossible"));
        yield new ArrayList<>(yearDayMap.get(lastYear).values());
      }
    };

    // Select what days to actually execute
    daysToExecute.sort(Day::compareTo);
    daysToExecute = switch (config.execDays) {
      case ALL -> daysToExecute;
      case SELECTED -> daysToExecute.stream()
                                    .filter(d -> d.day == config.selectedDay)
                                    .collect(Collectors.toCollection(ArrayList::new));
      case LAST -> new ArrayList<>(List.of(daysToExecute.getLast()));
    };

    // Execute
    for (Day day : daysToExecute) {
      runVersions(day);
    }

    return daysToExecute;
  }

  public void run() {
    logger.debug("run()");
    logger.println(Logger.F_DOUBLE_SEPARATOR);
    logger.println(Logger.f_CAPTION("CILILINK, CILILINK - RUN IT"));
    logger.println(Logger.f_CAPTION(config.toString(), Logger.F_INDENT + Logger.F_INDENT));
    logger.println(Logger.F_DOUBLE_SEPARATOR);
    logger.println();

    // Execute it
    List<Day> days = new ArrayList<>(runDays());

    // Print results
    logger.println();
    logger.println(Logger.F_SINGLE_SEPARATOR);
    logger.println(Logger.f_CAPTION("RESULTS:"));
    logger.println(Logger.F_SINGLE_SEPARATOR);
    days.sort(Day::compareTo);
    for (Day day : days) {
      if (day.hasStats()) {
        ArrayList<Version> versions = new ArrayList<>(day.versions.values());
        versions.sort(Version::compareTo);
        for (Version version : versions) {
          if (version.hasStats()) {
            logger.print("Day " + day.year + ".." + day.day);
            if (version.version > 0) {
              logger.print("  v" + version.version);
            }
            logger.println(":");
            ArrayList<Phase> phases = new ArrayList<>(2);
            if (version.phases.get(PhaseDef.A) != null) {
              phases.add(version.phases.get(PhaseDef.A));
            }
            if (version.phases.get(PhaseDef.B) != null) {
              phases.add(version.phases.get(PhaseDef.B));
            }
            for (Phase phase : phases) {
              if (phase.hasStats()) {
                ArrayList<MinVersion> minVersions = new ArrayList<>(phase.minVersions.values());
                minVersions.sort(MinVersion::compareTo);
                for (MinVersion minVersion : minVersions) {
                  if (minVersion.hasStats()) {
                    logger.print("  " + phase.phaseDef);
                    if (minVersion.minVersion > 0) {
                      logger.print(version.version + "." + minVersion.minVersion);
                    }
                    logger.println(":");
                    ArrayList<Stats> stats = new ArrayList<>(minVersion.stats);
                    stats.sort(Comparator.comparingInt(s -> s.tryId < 0 ? Integer.MAX_VALUE : s.tryId));
                    for (Stats stat : stats) {
                      logger.print("    ");
                      if (stat.tryId > 0) {
                        logger.print("try " + stat.tryId + " ");
                      } else {
                        logger.print("RESULT ");
                      }
                      logger.print("[" + stat.result.printValue + " in " + TimeUtils.formatDuration(stat.duration) + " sec]: ");
                      logger.print(stat.value);
                      if (stat.expectedValue != null) {
                        logger.print(" (expected: '" + stat.expectedValue + "')");
                      }
                      // Duration
                      logger.println();
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // ---- Internal classes

  static class Day implements Comparable<Day> {
    final int year;
    final int day;
    final Map<Integer, Version>  versions = new HashMap<>();

    public Day(int year, int day) {
      this.year = year;
      this.day = day;
    }

    @Override
    public int compareTo(Day o) {
      int yearComp = Integer.compare(year, o.year);
      if (yearComp == 0) {
        return Integer.compare(day, o.day);
      }
      return yearComp;
    }

    public String toString() {
      return "Day{" + year + "." + day + ", versions.size: " +  versions.size() + "}";
    }

    boolean hasStats() {
      return versions.values().stream().anyMatch(Version::hasStats);
    }
  }

  static class Version implements Comparable<Version> {
    final int version;
    final ParentDay target;
    final Map<PhaseDef, Phase> phases = new EnumMap<>(PhaseDef.class);

    public Version(ParentDay target) {
      this.target = target;
      this.version = target.getVersion();
    }

    @Override
    public int compareTo(Version o) {
      return Integer.compare(version, o.version);
    }

    @Override
    public String toString() {
      return "Version{" + version + ", target: " + target.getClass().getSimpleName() + "}";
    }

    boolean hasStats() {
      return phases.values().stream().anyMatch(Phase::hasStats);
    }
  }

  static class Phase {
    final PhaseDef phaseDef;
    final Map<Integer, MinVersion> minVersions = new HashMap<>();

    public Phase(PhaseDef phaseDef) {
      this.phaseDef = phaseDef;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Phase phase = (Phase) o;
      return phaseDef == phase.phaseDef;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(phaseDef);
    }

    @Override
    public String toString() {
      return "Phase{" + phaseDef + ", minVersions.size: " +  minVersions.size() + "}";
    }

    boolean hasStats() {
      return minVersions.values().stream().anyMatch(MinVersion::hasStats);
    }
  }

  static class MinVersion implements Comparable<MinVersion> {
    final int minVersion;
    final ParentDay target;
    final Method method;
    final PhaseDef phaseDef;
    final List<Stats> stats = new ArrayList<>();

    private MinVersion(int minVersion, ParentDay target, Method method, PhaseDef phaseDef) {
      this.minVersion = minVersion;
      this.target = target;
      this.method = method;
      this.phaseDef = phaseDef;
    }

    @Override
    public int compareTo(MinVersion o) {
      return Integer.compare(minVersion, o.minVersion);
    }

    @Override
    public String toString() {
      return "MinVersion{" + minVersion + ", target: " + target.getClass().getSimpleName() + "." + method.getName() + "(???)}";
    }

    boolean hasStats() {
      return !stats.isEmpty();
    }
  }

  public record Stats(int tryId, Result result, String value, String expectedValue, long duration, Throwable throwable) {
  }

  public enum Result {
    OK("OK"),
    FAILED("FAILED!"),
    CANNOT_EXECUTE("CANNOT EXECUTE!"),
    UNEXPECTED_RESULT("UNEXPECTED RESULT!"),;

    final String printValue;
    Result(String printValue) {
      this.printValue = printValue;
    }
  }

  public enum PhaseDef {
    A("[Aa](\\d*)"),
    B("[Bb](\\d*)");

    final Pattern methodPattern;

    PhaseDef(String methodPattern) {
      this.methodPattern = Pattern.compile(methodPattern);
    }
  }


}
