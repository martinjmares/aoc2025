package name.mjm.aoc;

import name.mjm.aoc.log.LogLevel;
import name.mjm.aoc.test.Assert;

import java.util.List;

public class CalendarManagerTest {

  private CalendarManager.Stats findStat(List<CalendarManager.Day> days, int dayId, int version, int minVersion, CalendarManager.PhaseDef phase, int tryId) {
    if (days == null) {
      return null;
    }
    CalendarManager.Day d = days.stream().filter(day -> day.day == dayId).findAny().orElse(null);
    if (d == null) {
      return null;
    }
    CalendarManager.Version v = d.versions.get(version);
    if (v == null) {
      return null;
    }
    CalendarManager.Phase p = v.phases.get(phase);
    if (p == null) {
      return null;
    }
    CalendarManager.MinVersion mv = p.minVersions.get(minVersion);
    if (mv == null) {
      return null;
    }
    return mv.stats.stream().filter(s -> s.tryId == tryId).findAny().orElse(null);
  }

  private CalendarManager createTarget(CalendarManagerConfig config) throws Exception {
    return new CalendarManager(config, "name.mjm.testdays", "testresources");
  }

  void testDoAllDays() throws Exception {
    CalendarManagerConfig config = CalendarManagerConfig.builder()
                                                       .logLevel(LogLevel.DEBUG)
                                                       .execDays(CalendarManagerConfig.ExecDays.ALL)
                                                       .execVersions(CalendarManagerConfig.ExecVersions.ALL)
                                                       .execPhases(CalendarManagerConfig.ExecPhases.AB)
                                                       .execTries(CalendarManagerConfig.ExecTries.BEFORE)
                                                       .build();
    CalendarManager target = createTarget(config);
    List<CalendarManager.Day> days = target.runDays();

    CalendarManager.Stats s = findStat(days, 70, 0, 0, CalendarManager.PhaseDef.A, -1);
    Assert.that(s).isNotNull();
    Assert.that(s.result).is(CalendarManager.Result.OK);
    Assert.that(s.value).is("70_v0_A");

    s = findStat(days, 70, 2, 0, CalendarManager.PhaseDef.B, -1);
    Assert.that(s).isNotNull();
    Assert.that(s.result).is(CalendarManager.Result.OK);
    Assert.that(s.value).is("70_v2_B");

    s = findStat(days, 70, 2, 2, CalendarManager.PhaseDef.B, -1);
    Assert.that(s).isNotNull();
    Assert.that(s.result).is(CalendarManager.Result.OK);
    Assert.that(s.value).is("70_v2.2_B");

    s = findStat(days, 71, 0, 0, CalendarManager.PhaseDef.A, 1);
    Assert.that(s).isNotNull();
    Assert.that(s.result).is(CalendarManager.Result.OK);
    Assert.that(s.value).is("71try1");

    s = findStat(days, 71, 0, 0, CalendarManager.PhaseDef.A, -1);
    Assert.that(s).isNotNull();
    Assert.that(s.result).is(CalendarManager.Result.OK);
    Assert.that(s.value).is("ahoj71");
  }

  void testTryFail() throws Exception {
    CalendarManagerConfig config = CalendarManagerConfig.builder()
                                                        .logLevel(LogLevel.DEBUG)
                                                        .execDays(CalendarManagerConfig.ExecDays.ALL)
                                                        .execVersions(CalendarManagerConfig.ExecVersions.ALL)
                                                        .execPhases(CalendarManagerConfig.ExecPhases.AB)
                                                        .execTries(CalendarManagerConfig.ExecTries.BEFORE)
                                                        .build();
    CalendarManager target = createTarget(config);
    List<CalendarManager.Day> days = target.runDays();

    CalendarManager.Stats s = findStat(days, 72, 0, 0, CalendarManager.PhaseDef.A, -1);
    Assert.that(s).isNotNull();
    Assert.that(s.result).is(CalendarManager.Result.OK);

    s = findStat(days, 72, 0, 0, CalendarManager.PhaseDef.B, -1);
    Assert.that(s).isNull();

    s = findStat(days, 72, 0, 0, CalendarManager.PhaseDef.B, 1);
    Assert.that(s).isNotNull();
    Assert.that(s.result).is(CalendarManager.Result.OK);

    s = findStat(days, 72, 0, 0, CalendarManager.PhaseDef.B, 3);
    Assert.that(s).isNotNull();
    Assert.that(s.result).is(CalendarManager.Result.UNEXPECTED_RESULT);
  }
}
