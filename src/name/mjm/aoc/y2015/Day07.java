package name.mjm.aoc.y2015;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.StringUtils;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.TryResults;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day07 extends ParentDay {


  @TryResults({
      @TryResult(tryId = 1, value = "72"),
      @TryResult(tryId = 2, value = "492"),
      @TryResult(tryId = 3, value = "456")
  })
  public int a(List<Instruction> instructions) {
    Map<String, Instruction> map = instructions.stream()
                                               .collect(Collectors.toMap(inst -> inst.id, inst -> inst));
    Instruction a = map.get("a");
    int result = a.compute(map);
    if (result < 0) {
      return (-1) * result - 1;
    }
    return result;
  }

  public int b(List<Instruction> instructions) {
    Map<String, Instruction> map = instructions.stream()
                                               .collect(Collectors.toMap(inst -> inst.id, inst -> inst));
    Instruction b = new Instruction("3176 -> b");
    map.put("b", b);
    Instruction a = map.get("a");
    int result = a.compute(map);
    if (result < 0) {
      return (-1) * result - 1;
    }
    return result;
  }


  public static class Instruction {
    final String id;
    int a;
    int b;
    String aSource;
    String bSource;
    final Operation operation;
    int result;
    boolean computed = false;

    public Instruction(String line) {
      int index = line.indexOf("->");
      this.id = line.substring(index + 2).trim();
      // Parts
      String[] formulaParts = line.substring(0, index).split(" ");
      if (formulaParts.length == 1) {
        String s = formulaParts[0].trim();
        if (StringUtils.isNumber(s)) {
          a = Integer.parseInt(s);
          aSource = null;
        } else {
          a = -1;
          aSource = s;
        }
        operation = (x, y) -> x;
      } else if (formulaParts[0].trim().equals("NOT")) {
        String la = formulaParts[1].trim();
        if (StringUtils.isNumber(la)) {
          a = Integer.parseInt(la);
          aSource = null;
        } else {
          a = -1;
          aSource = la;
        }
        b = -1;
        bSource = null;
        this.operation = (x, y) -> ~x;
      } else {
        // A
        String s = formulaParts[0].trim();
        if (StringUtils.isNumber(s)) {
          a = Integer.parseInt(s);
          aSource = null;
        } else {
          a = -1;
          aSource = s;
        }
        // B
        s = formulaParts[2].trim();
        if (StringUtils.isNumber(s)) {
          b = Integer.parseInt(s);
          bSource = null;
        } else {
          b = -1;
          bSource = s;
        }
        // Operation
        this.operation = switch (formulaParts[1].trim()) {
          case "AND" -> (a, b) -> a & b;
          case "OR" -> (a, b) -> a | b;
          case "LSHIFT" -> (a, b) -> a << b;
          case "RSHIFT" -> (a, b) -> a >> b;
          default -> throw new RuntimeException("Unknown operation: " + formulaParts[1].trim());
        };
      }
    }

    public int compute(Map<String, Instruction> instructionMap) {
      if (computed) {
        return result;
      }
      if (aSource != null) {
        a = instructionMap.get(aSource).compute(instructionMap);
      }
      if (bSource != null) {
        b = instructionMap.get(bSource).compute(instructionMap);
      }
      result = operation.process(a, b);
      computed = true;
      return result;
    }
  }

  @FunctionalInterface
  interface Operation {
    int process(int a, int b);
  }
}
