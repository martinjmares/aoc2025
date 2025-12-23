package name.mjm.aoc.calendar;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.TryResults;
import name.mjm.aoc.algebra.EquationSet;
import name.mjm.aoc.algebra.LinearEquation;
import name.mjm.aoc.algebra.ParametrisedEquationSetResult;
import name.mjm.aoc.algebra.RationalNumber;
import name.mjm.aoc.calendar.Day10.JMachine;

import java.util.Arrays;
import java.util.List;

public class Day10v2 extends ParentDay {

  /**
   * Use original for a(). We need this version just for b().
   */
  @TryResult("7")
  public long a(List<Day10.Machine> machines) {
    Day10 day10 = new Day10();
    return day10.a(machines);
  }

  @TryResults(
      {@TryResult(tryId = 1, value = "33"),
      @TryResult(tryId = 2, value = "49")}
  )
  public long b(List<JMachine> machines) {
    long result = 0;
    for (int i = 0; i < machines.size(); i++) {
      JMachine machine = machines.get(i);
      logger.debug("------------------------------------");
      logger.debug("PROCESS machine " + i + ": " + machine);
      EquationSet equationSet = toEquationSet(machine);
      ParametrisedEquationSetResult eqResult = equationSet.solveLinearEquations();
      int[] parametersIndexes = eqResult.getParametersIndexes();
      logger.debug("Indexes of parameters: " + Arrays.toString(parametersIndexes));
      int[] indexesOfResults = eqResult.formulasIndexes();
      logger.debug("Indexes of formulas: " + Arrays.toString(indexesOfResults));
      logger.debug("Resulting formulas: \n" + eqResult + "\n" );

      // Iterate
      int size = parametersIndexes.length + indexesOfResults.length;
      int[] limitForParameters = new int[parametersIndexes.length];
      for (int j = 0; j < parametersIndexes.length; j++) {
        limitForParameters[j] = limitForButton(machine.buttons()[parametersIndexes[j]], machine.requstedJoltages());
      }
      logger.debug("Limits for params: " + Arrays.toString(limitForParameters));
      logger.debug("[Machine-" + i + "] Varieting " + parametersIndexes.length + " params");
      int subResult = findMinSolutionInMinutesIterative(eqResult, parametersIndexes, limitForParameters, new int[size], 0);
      logger.debug("[Machine-" + i + "] Subresult: " + subResult);
      System.out.println(subResult);
      result += subResult;
    }

    return result;
  }

  int findMinSolutionInMinutesIterative(ParametrisedEquationSetResult equationSetResult, int[] parameterIndexes, int[] limitsForParameters, int[] configuration, int index) {
    int smallestValid = -1;
    if (index < parameterIndexes.length) {
      int limit = limitsForParameters[index];
      for (int i = 0; i <= limit; i++) {
        configuration[parameterIndexes[index]] = i;
        int curSolution = findMinSolutionInMinutesIterative(equationSetResult, parameterIndexes, limitsForParameters, configuration, index + 1);
        if (curSolution >= 0 && (smallestValid < 0 || curSolution < smallestValid)) {
          smallestValid = curSolution;
        }
      }
    } else {
      RationalNumber[] solution = equationSetResult.solve(configuration);
      int count = 0;
      boolean valid = true;
      for (int i = 0; i < solution.length; i++) {
        if (solution[i].isWhole()) {
          int curSolution = solution[i].toWHole();
          if (curSolution >= 0) {
            count += curSolution;
          } else {
            valid = false;
            break;
          }
        } else {
          valid = false;
          break;
        }
      }
      return valid ? count : -1;
    }
    return smallestValid;
  }

  int limitForButton(int[] button, int[] leds) {
    int min = Integer.MAX_VALUE;
    for (int i = 0; i < button.length; i++) {
      if (leds[button[i]] < min) {
        min = leds[button[i]];
      }
    }
    return min;
  }

  EquationSet toEquationSet(JMachine machine) {
    EquationSet result = new EquationSet();

    for (int ledId = 0; ledId < machine.requstedJoltages().length; ledId++) {
      int[] equationParams =  new int[machine.buttons().length];
      Arrays.fill(equationParams, 0);
      for (int buttonId = 0; buttonId < machine.buttons().length; buttonId++) {
        if (contains(machine.buttons()[buttonId], ledId)) {
          equationParams[buttonId] = 1;
        }
      }
      result.addEquation(new LinearEquation(machine.requstedJoltages()[ledId], equationParams));
    }
    return result;
  }

  static boolean contains(int[] array, int value) {
    if (array == null) {
      return false;
    }
    for (int expected : array) {
      if (expected == value) {
        return true;
      }
    }
    return false;
  }
}
