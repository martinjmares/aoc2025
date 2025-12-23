package name.mjm.aoc.algebra;

import java.util.Objects;

public class RationalNumber {

  public static RationalNumber ZERO = new RationalNumber(0, 1);

  private final int numerator;
  private final int denominator;

  public RationalNumber(int numerator, int denominator) {
    if (denominator == 0) {
      throw new IllegalArgumentException("denominator cannot be 0");
    } else if (denominator < 0) {
      this.numerator = (-1) * numerator;
      this.denominator = (-1) * denominator;
    } else {
      this.numerator = numerator;
      this.denominator = denominator;
    }
  }

  public RationalNumber(int numerator) {
    this.numerator = numerator;
    this.denominator = 1;
  }

  public int numerator() {
    return numerator;
  }

  public int denominator() {
    return denominator;
  }

  public RationalNumber add(int num) {
    return new RationalNumber(numerator + num * denominator, denominator);
  }

  public RationalNumber add(RationalNumber num) {
    if (denominator == num.denominator) {
      return new RationalNumber(numerator + num.numerator, denominator);
    }
    int nsn = Algebra.nsn(denominator, num.denominator);
    int myFactor = nsn / denominator ;
    int otherFactor = nsn / num.denominator;
    return new RationalNumber((numerator * myFactor) + (num.numerator * otherFactor), nsn);
  }

  public RationalNumber minus(int num) {
    return new RationalNumber(numerator - num * denominator, denominator);
  }

  public RationalNumber multiply(int num) {
    if (num == 0) {
      return ZERO;
    }
    return new RationalNumber(numerator * num, denominator);
  }

  public RationalNumber multiply(RationalNumber num) {
    return new RationalNumber(numerator * num.numerator, denominator * num.denominator);
  }

  public RationalNumber divide(int num) {
    if (num == 0) {
      throw new RuntimeException("divide by zero");
    }
    return new RationalNumber(numerator, denominator * num);
  }

  public RationalNumber simplify() {
    if (numerator == 0) {
      return ZERO;
    }

    int nsd = Algebra.nsd(numerator, denominator);
    if (nsd == 1) {
      return this;
    } else {
      return new RationalNumber(numerator / nsd, denominator / nsd);
    }
  }

  public boolean isWhole() {
    return (numerator % denominator) == 0;
  }

  public int toWHole() {
    return numerator / denominator;
  }

  public String toString() {
    if (denominator == 1 || numerator == 0) {
      return String.valueOf(numerator);
    }
    return "(" + numerator + "/" + denominator + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    RationalNumber that = (RationalNumber) o;
    return numerator == that.numerator && denominator == that.denominator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(numerator, denominator);
  }
}
