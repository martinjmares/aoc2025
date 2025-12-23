package name.mjm.aoc.calendar;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

import java.util.*;

public class Day10 extends ParentDay {

  @TryResult("7")
  public long a(List<Machine> machines) {
    long sum = 0;
    for (Machine machine : machines) {
      int partial = lightUp(machine.lights, 0, machine.buttons, 0, 1);
      logger.debug(machine + " :: " + partial);
      sum += partial;
    }

    return sum;
  }

  int lightUp(int reqLights, int curLight, int[] buttons, int buttonPointer, int level) {
    int shortestRecursiveResult = -1;
    for (int i = buttonPointer; i < buttons.length; i++) {
      int nextLights = curLight ^ buttons[i];
      if (nextLights == reqLights) {
        return level;
      }
      int recResult = lightUp(reqLights, nextLights, buttons, i + 1, level + 1);
      if (recResult > 0 && (shortestRecursiveResult < 0 || recResult < shortestRecursiveResult)) {
        shortestRecursiveResult = recResult;
      }
    }

    return shortestRecursiveResult;
  }

  @TryResult("33")
  public long b(List<JMachine> machines) {
    long result = 0;
    for (int i = 0; i < machines.size(); i++) {
      JMachine machine = machines.get(i);
      Arrays.sort(machine.buttons, (a, b) -> (-1) * Integer.compare(a.length, b.length));
      int subResult = applyJoltage(machine, new int[machine.requstedJoltages.length], 0, 0, -1);
      result += subResult;
      logger.debug("machine[" + i + "] = " + subResult);
    }

    return result;
  }

  @TryResult("33")
  public long b2(List<JMachine> machines) {
    logger.info("Running B 0.2");
    long result = 0;
    for (int i = 0; i < machines.size(); i++) {
      JMachine machine = machines.get(i);
      Arrays.sort(machine.buttons, (a, b) -> (-1) * Integer.compare(a.length, b.length));
      ArrayList<ScoredButton> buttons = new ArrayList<>(machine.buttons.length);
      for (int[] b : machine.buttons) {
        buttons.add(new ScoredButton(b, -1, -1));
      }
      int subResult = applyJoltage2(machine, machine.requstedJoltages, buttons, 0, -1);
      result += subResult;
      logger.debug("machine[" + i + "] = " + subResult);
    }

    return result;
  }

  int applyJoltage2(JMachine machine, int[] goalFromParent, ArrayList<ScoredButton> remainingButtons, int clicksDone, int bestClicksSoFar) {
    // Protect the best result
    if (bestClicksSoFar > 0 && bestClicksSoFar <= (clicksDone + 1)) {
      //logger.debug("break 1");
      // Same or better result was already achieved. Don't continue.
      return bestClicksSoFar;
    }
    if (remainingButtons.isEmpty()) {
      return bestClicksSoFar;
    }

    int[] masterGoal = copy(goalFromParent);
    // Filter to applyable
    ArrayList<ScoredButton> buttons = new ArrayList<>(remainingButtons.size());
    for (ScoredButton button : remainingButtons) {
      int score = Integer.MAX_VALUE;
      int limitingPosition = 0;
      for (int goalIndex : button.button) {
        if (masterGoal[goalIndex] < score) {
          limitingPosition = goalIndex;
          score = masterGoal[goalIndex];
        }
      }
      if (score > 0) {
        buttons.add(new ScoredButton(button.button, score, limitingPosition));
      }
    }
    if (buttons.isEmpty()) {
      return bestClicksSoFar;
    }

    // Apply mandatory
    for (int i = 0; i < masterGoal.length; i++) {
      ScoredButton singleButton = null;
      boolean justSingleButton = true;
      int aggregatedScore = 0;
      int limitingPositionsMask = 0;
      for (ScoredButton button : buttons) {
        if (button.contains(i)) {
          int myMask = 1 << button.positionOfScore;
          if ((limitingPositionsMask & myMask) == 0) {
            limitingPositionsMask |= myMask;
            aggregatedScore += button.score;
          }
          if (singleButton == null) {
            singleButton = button;
          } else {
            justSingleButton = false;
            break;
          }
        }
      }
      if (singleButton == null) {
        return bestClicksSoFar; // Because there is no button to cover this position
      }
      if (aggregatedScore < masterGoal[i]) {
        return bestClicksSoFar; // Cannot apply enough
      }
      if (justSingleButton) {
        // Exactly one - so, apply
        applyButton(singleButton.button, singleButton.score, masterGoal);
        // and continue by recursion
        buttons.remove(singleButton);
        return applyJoltage2(machine, masterGoal, buttons, clicksDone + singleButton.score, bestClicksSoFar);
      }
    }

    while (!buttons.isEmpty()) {
      ScoredButton button = buttons.removeFirst();
      int counter = 0;
      int[] goal = copy(masterGoal);
      // apply max to prefer large max first strategy
      int score = Math.min(button.score, (bestClicksSoFar - clicksDone - 1));
      applyButton(button.button, score, goal);

      if (isZeros(goal)) {
        bestClicksSoFar = clicksDone + score;
      } else {
        // Lets do recursion
        for (int j = score; j > 0; j--) {
          int partial = applyJoltage2(machine, goal, buttons, clicksDone + j, bestClicksSoFar);
          if (partial >= 0 && (bestClicksSoFar < 0 || partial < bestClicksSoFar)) {
            bestClicksSoFar = partial;
          }
          applyButton(button.button, -1, goal);
        }
      }
    }

    return bestClicksSoFar;
  }


  boolean isZeros(int[] array) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] < 0) {
        throw new RuntimeException("Negative values are not allowed");
      } else if (array[i] > 0) {
        return false;
      }
    }
    return true;
  }

  int[] copy(int[] array) {
    int[] result = new int[array.length];
    System.arraycopy(array, 0, result, 0, array.length);
    return result;
  }

  record ScoredButton(int[] button, int score, int positionOfScore) {

    boolean contains(int i) {
      for (int i1 : button) {
        if (i1 == i) {
          return true;
        }
      }
      return false;
    }
  }

  int applyJoltage(JMachine machine, int[] curJoltages, int buttonPointer, int clicksDone, int bestClicksSoFar) {
    if (bestClicksSoFar > 0 && bestClicksSoFar <= (clicksDone + 1)) {
      //logger.debug("break 1");
      // Same or better result was already achieved. Don't continue.
      return bestClicksSoFar;
    }

    for (int i = buttonPointer; i < machine.buttons.length; i++) {
      int[] button =  machine.buttons[i];
      int counter = 0;
      int[] myCurJoltage = new int[curJoltages.length];
      System.arraycopy(curJoltages, 0, myCurJoltage, 0, curJoltages.length);
      int maxRepeat = buttonMaxUse(curJoltages, machine.requstedJoltages, button);
      //logger.debug("button<" + i + ">" + Arrays.toString(button) + "{rep: " + maxRepeat + ": cur = " + Arrays.toString(myCurJoltage) + " ;; req = " + Arrays.toString(machine.requstedJoltages));
      for (int j = 0; j < maxRepeat; j++) {
        if (bestClicksSoFar > 0 && bestClicksSoFar <= (clicksDone + j + 1)) {
          break;
        }
        applyButton(myCurJoltage, button);
        if (Arrays.equals(myCurJoltage, machine.requstedJoltages)) {
          //logger.debug("find - break");
          bestClicksSoFar = clicksDone + j + 1;
          break;
        }

        // Recursion
        int partial = applyJoltage(machine, myCurJoltage, i + 1, clicksDone + j + 1, bestClicksSoFar);
        if (partial >= 0 && (bestClicksSoFar < 0 || partial < bestClicksSoFar)) {
          bestClicksSoFar = partial;
        }
      }
    }

    return bestClicksSoFar;
  }

  void applyButton(int[] curJoltages, int[] button) {
    for (int b : button) {
      curJoltages[b]++;
    }
  }

  int buttonMaxUse(int[] curJoltages, int[] requestedJoltages, int[] button) {
    int maxUse = -1;
    for (int b : button) {
      int diff = requestedJoltages[b] - curJoltages[b];
      if (diff <= 0) {
        return 0;
      }
      if (maxUse < 0 || diff < maxUse) {
        maxUse = diff;
      }
    }
    return maxUse;
  }

  int fillLargestStrategy(JMachine machine, int loggingIndex) {
    // ----- PREPARE MODEL -----
    // Count of use of individual buttons
    int[] bUseCount = new int[machine.buttons.length];
    Arrays.fill(bUseCount, 0);
    // Sorted buttons based on size
    int[][] buttons = new int[machine.buttons.length][];
    System.arraycopy(machine.buttons, 0, buttons, 0, machine.buttons.length);
    Arrays.sort(buttons, (a, b) -> (-1) * Integer.compare(a.length, b.length));
    // Goal is requested jolatge, we will sub from it to zero
    int[] goal = new int[machine.requstedJoltages.length];
    System.arraycopy(machine.requstedJoltages, 0, goal, 0, machine.requstedJoltages.length);
    // Button pointers, that can change joltage for individual positions
    int[][] buttonsForPosition = new int[goal.length][];
    for (int i = 0; i < goal.length; i++) {
      ArrayList<Integer> pointers = new ArrayList<>();
      for (int b = 0; b < buttons.length; b++) {
        int[] button = buttons[b];
        for (int j = 0; j < button.length; j++) {
          if (button[j] == i) {
            pointers.add(b);
            break;
          }
        }
      }
      buttonsForPosition[i] = pointers.stream().mapToInt(Integer::intValue).toArray();
    }

    // ----- FIRST IF SOMETHING HAS JUST ONE BUTTON, USE IT ------
    for (int i = 0; i < buttonsForPosition.length; i++) {
      if (buttonsForPosition[i].length == 1) {
        int[] button = buttons[buttonsForPosition[i][0]];
        applyButton(button, goal[i], goal);
        bUseCount[buttonsForPosition[i][0]] += goal[i];
      }
    }

    // TODO:
    throw new RuntimeException("Not implemented yet");
  }

  void applyButton(int[] button, int count, int[] goal) {
    for (int position : button) {
      goal[position] -= count;
      if (goal[position] < 0) {
        throw new RuntimeException("Subzero!");
      }
    }
  }

  public record Machine(int lights, int[] buttons, List<Integer> joltages) {
    public Machine(String line) {
      int lights = 0;
      ArrayList<Integer> buttons = new ArrayList<>();
      ArrayList<Integer> joltages = new ArrayList<>();
      StringTokenizer stok = new StringTokenizer(line, " ");
      while (stok.hasMoreTokens()) {
        String token = stok.nextToken().trim();
        String inToken = token.substring(1, token.length() - 1);
        switch (token.charAt(0)) {
          case '[' -> {
            for (int i = 0; i < inToken.length(); i++) {
              char c = inToken.charAt(i);
              if (c == '#') {
                lights = lights | (1 << i);
              }
            }
          }
          case '(' -> {
            String[] split = inToken.split(",");
            int button = 0;
            for (String s : split) {
              button = button | (1 << Integer.parseInt(s));
            }
            buttons.add(button);
          }
          case '{' -> {
            String[] split = inToken.split(",");
            for (String s : split) {
              joltages.add(Integer.parseInt(s));
            }
          }
        }
      }

      int[] buttonArray = buttons.stream().mapToInt(Integer::intValue).toArray();
      this(lights,
           buttonArray,
           joltages);
    }

    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Machine: [").append(Integer.toBinaryString(lights)).append("]");
      for (int button : buttons) {
        sb.append(" (").append(Integer.toBinaryString(button)).append(")");
      }
      return sb.toString();
    }
  }

  public record JMachine(String line, int[][] buttons, int[] requstedJoltages) {
    public JMachine(String line) {
      ArrayList<int[]> buttons = new ArrayList<>();
      int[] joltages = null;
      StringTokenizer stok = new StringTokenizer(line, " ");
      while (stok.hasMoreTokens()) {
        String token = stok.nextToken();
        String inToken = token.substring(1, token.length() - 1);
        switch (token.charAt(0)) {
          case '[' -> {}
          case '(' -> {
            String[] split = inToken.split(",");
            int[] buttonConfig = new int[split.length];
            for (int i = 0; i < split.length; i++) {
              int button = Integer.parseInt(split[i]);
              buttonConfig[i] = button;
            }
            buttons.add(buttonConfig);
          }
          case '{' -> {
            String[] split = inToken.split(",");
            joltages = new int[split.length];
            for (int i = 0; i < split.length; i++) {
              int joltage = Integer.parseInt(split[i]);
              joltages[i] = joltage;
            }
          }
        }
      }

      int[][] aButtons = new int[buttons.size()][];
      for (int i = 0; i < buttons.size(); i++) {
        aButtons[i] = buttons.get(i);
      }
      this(line, aButtons, joltages);
    }

    public String toString() {
      return line;
    }
  }

}
