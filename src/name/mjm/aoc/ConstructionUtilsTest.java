package name.mjm.aoc;

import jdk.jfr.Name;
import name.mjm.aoc.test.Assert;

import javax.smartcardio.ATR;
import java.util.function.Function;

public class ConstructionUtilsTest {

  public void testString() {
    Function<String, String> f = ConstructionUtils.createBuilderFromString(String.class);
    String result = f.apply("foo");
    Assert.that(result).is("foo");
  }

  public void testInt() {
    Function<String, Integer> f = ConstructionUtils.createBuilderFromString(int.class);
    int result = f.apply("123");
    Assert.that(result).is(123);
  }

  public void testFloat() {
    Function<String, Float> f = ConstructionUtils.createBuilderFromString(float.class);
    float result = f.apply("1.1");
    Assert.that(result).isNotNull();
  }

  public void testConstructor() {
    Function<String, A> f = ConstructionUtils.createBuilderFromString(A.class);
    A result = f.apply("foo");
    Assert.that(result)
          .isNotNull();
    Assert.that(result.line).is("foo");
  }

  public void testValueOf() {
    Function<String, C> f = ConstructionUtils.createBuilderFromString(C.class);
    C result = f.apply("foo");
    Assert.that(result)
          .isNotNull();
    Assert.that(result.a).is("fooa");
    Assert.that(result.b).is("foob");
  }

  public void testOneOfConstructors() {
    Function<String, B> f = ConstructionUtils.createBuilderFromString(B.class);
    B result = f.apply("foo");
    Assert.that(result)
          .isNotNull();
    Assert.that(result.a).is("fooa");
    Assert.that(result.b).is("foob");
  }

  public void testRegexpSingleConstructor() {
    Function<String, D> f = ConstructionUtils.createBuilderFromString(D.class);
    D result = f.apply("foo bar baz");
    Assert.that(result)
          .isNotNull();
    Assert.that(result.a).is("foo");
    Assert.that(result.b).is("baz");
  }

  public void testRegexpValueOf() {
    Function<String, E> f = ConstructionUtils.createBuilderFromString(E.class);
    E result = f.apply("foo bar 123");
    Assert.that(result)
          .isNotNull();
    Assert.that(result.a).is("foo");
    Assert.that(result.b).is(123);
  }

  public record A(String line) {}

  public record B(String a, String b) {
    public B(String line) {
      this(line + "a", line + "b");
    }
  }

  public record C(String a, String b) {
    public static C valueOf(String line) {
      return new C(line + "a", line + "b");
    }
  }

  public static class E {
    final String a;
    final int b;

    private E(String a, int b) {
      this.a = a;
      this.b = b;
    }

    @FromRegexpGroups("(?<first>[a-zA-Z]+) (?<sec>[a-zA-Z]+) (?<last>\\d+)")
    public static E valueOf(String a, @Named("last") int b) {
      return new E(a, b);
    }
  }

  @FromRegexpGroups("(?<first>[a-zA-Z]+) (?<sec>[a-zA-Z]+) (?<last>[a-zA-Z]+)")
  public record D(String a, @Named("last") String b) {
  }
}
