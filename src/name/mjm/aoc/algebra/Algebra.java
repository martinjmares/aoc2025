package name.mjm.aoc.algebra;

public class Algebra {

  private  Algebra() {
  }

  /**
   * Largest shared dividor.
   */
  public static int nsd(int a, int b) {
    while (b != 0) {
      int temp = b;
      b = a % b;
      a = temp;
    }
    return a;
  }

  public static int nsn(int a, int b) {
    return Math.abs(a * b) / nsd(a, b);
  }
}
