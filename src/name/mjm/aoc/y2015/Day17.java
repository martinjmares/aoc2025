package name.mjm.aoc.y2015;

import name.mjm.aoc.Data;
import name.mjm.aoc.Datas;
import name.mjm.aoc.Named;
import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.IntStack;
import name.mjm.aoc.model.Size;

import java.util.ArrayList;

@Datas({
    @Data(tryId = 1, name = "size", value = "25"),
    @Data(name = "size", value = "150")
})
public class Day17 extends ParentDay {


  @TryResult(value = "4")
  public int a(ArrayList<Size> sizes, @Named("size") int size) {
    return withoutRecursion(sizes, size, false);
  }

  @TryResult(value = "3")
  public int b(ArrayList<Size> sizes, @Named("size") int size) {
    return withoutRecursion(sizes, size, true);
  }

  /**
   * Just for fun implementation without recursion, with local stack instead if it.
   * @param sizes
   * @return the count of combinations
   */
  private int withoutRecursion(ArrayList<Size> sizes, int expectedValue, boolean justMinimumCounts) {
    sizes.sort(Size::compareTo);
    IntStack configStact = new IntStack(sizes.size());

    int minContainersNeeded = Integer.MAX_VALUE;
    int result = 0;
    configStact.push(0);
    int value = sizes.get(0).intValue();
    while (true) {
      boolean removeLastAndMoveOneBefore = false;
      if (value == expectedValue) {
        // We are there, write it and try the next one. It can be the same size.
        if (justMinimumCounts) {
          if (configStact.size() < minContainersNeeded) {
            result = 0;
            minContainersNeeded = configStact.size();
          }
          if (configStact.size() == minContainersNeeded) {
            result++;
          }
        } else {
          result++;
        }
        int index = configStact.pop();
        value -= sizes.get(index).intValue();
        index++;
        if (index < sizes.size()) {
          configStact.push(index);
        } else {
          // Put it back to be imadiatly removed again
          configStact.push(--index);
          removeLastAndMoveOneBefore = true;
        }
        value += sizes.get(index).intValue();
      } else if (value < expectedValue) {
        // We need to add next element or move previous
        int index = configStact.peek();
        index++;
        if (index < sizes.size()) {
          configStact.push(index);
          value += sizes.get(index).intValue();
        } else {
          // the last in config was the last available, remove it and move one before
          removeLastAndMoveOneBefore = true;
        }
      } else { // if (value > expectedValue)
        // The last element adds too much, do not continue
        removeLastAndMoveOneBefore = true;
      }
      if (removeLastAndMoveOneBefore) {
        // Remove the last element
        int index = configStact.pop();
        value -= sizes.get(index).intValue();
        // Move previous element
        if (configStact.size() == 0) {
          break; // we are done
        }
        index = configStact.pop();
        value -= sizes.get(index).intValue();
        index++; // It must be ok, because we removed the one element after this one
        configStact.push(index);
        value += sizes.get(index).intValue();
      }
    }
    return result;
  }



}
