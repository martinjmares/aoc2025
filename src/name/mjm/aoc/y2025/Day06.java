package name.mjm.aoc.y2025;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.StringUtils;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.Lines;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day06 extends ParentDay {

  static final char SUM = '+';
  static final char MULT = '*';

  long zeroElement(char operator) {
    return switch (operator) {
      case SUM -> 0L;
      case MULT -> 1L;
      default -> throw new IllegalArgumentException("Invalid operator: '" + operator + "'");
    };
  }

  long operateExact(char operator, long leftOperator, long rightOperator) {
    return switch (operator) {
      case SUM -> Math.addExact(leftOperator, rightOperator);
      case MULT -> Math.multiplyExact(leftOperator, rightOperator);
      default -> throw new IllegalArgumentException("Invalid operator: " + operator);
    };
  }

  @TryResult("4277556")
  public long a(Model model) {
    long result = 0L;
    for (int j = 0; j < model.operators.length; j++) {
      final int columnIndex = j;
      char operator = model.operators[j];
      long subresult = zeroElement(operator);
      for (int i = 0; i < model.numbers.length; i++) {
        subresult = operateExact(operator, subresult, model.numbers[i][columnIndex]);
      }
      final long finalSubresult = subresult;
      logger.debug(() -> {
        int[] nums = new int[model.numbers.length];
        for (int i = 0; i < model.numbers.length; i++) {
          nums[i] = model.numbers[i][columnIndex];
        }
        return toString(nums, operator) + " = " + finalSubresult;
      });
      result += subresult;
    }
    return result;
  }

  @TryResult("3263827")
  public long b(Lines allLines) {
    Lines lines = allLines.toNonEmpty();
    int cursor = 0;

    boolean allDone = false;
    long result = 0L;

    while (!allDone) {
      char operator = ' ';
      List<String> columns = new ArrayList<>();
      StringBuilder sb = new StringBuilder(lines.size());
      //Eat chars
      while (true) {
        sb.setLength(0);
        for (String line : lines) {
          char ch = StringUtils.charAt(line, cursor, ' ');
          if (ch == SUM || ch == MULT) {
            if (operator != ' ') {
              throw new IllegalArgumentException("Repeated operator on cursor " + cursor);
            } else {
              operator = ch;
            }
          } else {
            sb.append(ch);
          }
        }
        // Add columns
        String column = sb.toString().trim();
        if (!column.isBlank()) {
          columns.add(column);
        } else {
          break;
        }
        cursor++;
      }

      // Eat whitespaces or end
      boolean hasNonWhitespave = false;
      while (!hasNonWhitespave) {
        cursor++;
        for (String line : lines) {
          if (line.length() <= cursor) {
            allDone = true;
            break;
          }
          if (!Character.isWhitespace(StringUtils.charAt(line, cursor, ' '))) {
            hasNonWhitespave = true;
          }
        }
      }

      // Compute
      long subResult = computeSpecMath(columns, operator);
      result = Math.addExact(result, subResult);
    }

    return result;
  }

  private long computeSpecMath(List<String> numbers, char operator) {
    logger.debug(() -> numbers.stream().collect(Collectors.joining(" " + operator + " ")));
    long result = zeroElement(operator);
    for (String number : numbers) {
      result = operateExact(operator, result, Long.parseLong(number));
    }
    logger.debug("         = " + result);
    return result;
  }

  String toString(int[] numbers, char operator) {
    return Arrays.stream(numbers)
        .mapToObj(Integer::toString)
        .collect(Collectors.joining(" " + operator + " "));
  }

  public record Model(int[][] numbers, char[] operators) {
    public Model(BufferedReader reader) throws IOException {
      String line = null;
      List<int[]> numbers = new ArrayList<>(10);
      char[] operators = null;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) {
          continue;
        }
        List<String> items = new LinkedList<>();
        StringTokenizer stok = new StringTokenizer(line, " ");
        while (stok.hasMoreTokens()) {
          String item = stok.nextToken().trim();
          if (!item.isEmpty()) {
            items.add(item);
          }
        }
        if (Character.isDigit(items.getFirst().charAt(0))) {
          int[] array = items.stream()
                             .mapToInt(Integer::parseInt)
                             .toArray();
          numbers.add(array);
        } else {
          operators = new char[items.size()];
          int index = 0;
          for (String item : items) {
            if (item.length() != 1) {
              throw new IllegalArgumentException("Operator is longer than 1 character");
            }
            operators[index++] = item.charAt(0);
          }
        }
      }

      // Numbers to array
      int[][] numbersArray = new int[numbers.size()][];
      int index = 0;
      for (int[] array : numbers) {
        numbersArray[index++] = array;
      }

      if (operators == null) {
        throw new IllegalArgumentException("There are no operators.");
      }

      this(numbersArray, operators);
    }
  }
}
