package name.mjm.aoc.y2025;

import name.mjm.aoc.LongUtils;
import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.ListOfDigitArrays;

import java.util.ArrayList;
import java.util.Collection;

public class Day03 extends ParentDay {

  long constructHighest(int[] digits, int rezSize, int first, Collection<Integer> collectPositions) {
    if (rezSize == 0) {
      return 0L;
    }

    long highDigit = -1;
    int position = -1;
    for (int i = first; i <= (digits.length - rezSize); i++) {
      if (digits[i] > highDigit) {
        highDigit = digits[i];
        position = i;
        if (highDigit == 9) {
          break;
        }
      }
    }
    collectPositions.add(position);

    long rest = constructHighest(digits, rezSize - 1, position + 1, collectPositions);
    long my = highDigit * LongUtils.pow(10L, rezSize - 1);
    return rest + my;
  }

  private long compute(ListOfDigitArrays multiDigits, int sizeOfResult) {
    long sum = 0;
    ArrayList<Integer> indexes = new ArrayList<>(multiDigits.getFirst().length);
    for (int[] digits : multiDigits) {
      indexes.clear();
      long subresult = constructHighest(digits, sizeOfResult, 0, indexes);
      logger.debug(() -> ListOfDigitArrays.toString(digits, indexes));
      logger.debug("subresult: " + subresult);
      sum = Math.addExact(sum, subresult);
    }
    return sum;
  }

  @TryResult(value = "357")
  public long a(ListOfDigitArrays multiDigits) {
    return compute(multiDigits, 2);
  }

  @TryResult(value = "3121910778619")
  public long b(ListOfDigitArrays multiDigits) {
    return compute(multiDigits, 12);
  }
}
