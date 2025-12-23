package name.mjm.aoc.algebra;

/*
 * coefficient[0] * x + coefficient[1] * y + ... = constant
 */
public record LinearEquation(int constant, int... coefficients) {

  public String toString() {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (int coefficient : coefficients) {
      if (first) {
        sb.append(coefficient);
        first = false;
      } else if (coefficient < 0) {
        sb.append(" - ").append((-1) * coefficient);
      } else {
        sb.append(" + ").append(coefficient);
      }
    }
    sb.append(" = ").append(constant);

    return sb.toString();
  }
}
