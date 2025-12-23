package name.mjm.aoc.algebra;

import name.mjm.aoc.test.Assert;

import java.util.Arrays;

public class EquationSetTest {

  void testToMatrix() {
    EquationSet equationSet = new EquationSet();
    equationSet.addEquation(new LinearEquation(5, 3, 2, 1));
    equationSet.addEquation(new LinearEquation(18, 0, 1, 0));
    equationSet.addEquation(new LinearEquation(22, 5, 6, 7));

    int[][] matrix = equationSet.toMatrix();
    int[][] expected = new int[][] {{3, 2, 1, 5}, {0, 1, 0, 18}, {5, 6, 7, 22}};
    boolean deepEquals = Arrays.deepEquals(expected, matrix);

    Assert.that(deepEquals).isTrue();
  }

  void testSolve() {
    String str = """
        2  1  0  | 44]
        [ 0  0  0  0  0  2  0 -1  | 22]
        [ 0  0  0  0  0  1  0 -1  |  4]
        """;
    EquationSet equationSet = new EquationSet();
    equationSet.addEquation(new LinearEquation(44, 2, 1, 0));
    equationSet.addEquation(new LinearEquation(4, 1, 0, -1));

    int[][] matrix = equationSet.toMatrix();
    equationSet.toStaircase(matrix);

    int[][] expected = new int[][] {{2, 1, 0, 44}, {0, -1, -2, -36}};
    boolean deepEquals = Arrays.deepEquals(expected, matrix);
    Assert.that(deepEquals).isTrue();
  }


}
