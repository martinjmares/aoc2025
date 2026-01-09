package name.mjm.testdays.y2025;

import name.mjm.aoc.Data;
import name.mjm.aoc.Datas;
import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

@Datas (
{
    @Data("abcd"),
    @Data(tryId = 1, value = "test1")}
)
public class Day50 extends ParentDay {

  @TryResult("test1")
  public String a(String val) {
    if (val == null) {
      return "null";
    }
    if (val.startsWith("test") || val.equals("abcd")) {
      return val;
    }
    throw new RuntimeException("Unexpected val: " + val);
  }

  @Data(tryId = 1, value = "test2")
  @TryResult("test2")
  public String b(String val) {
    return a(val);
  }
}
