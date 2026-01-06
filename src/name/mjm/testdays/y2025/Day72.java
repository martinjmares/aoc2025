package name.mjm.testdays.y2025;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

public class Day72 extends ParentDay {

  @TryResult(tryId = 3, value = "333A")
  public String a(String s) {
    return s.trim() + "A";
  }

  @TryResult(tryId = 3, value = "333spatne")
  public String b(String s) {
    return s.trim() + "B";
  }
}
