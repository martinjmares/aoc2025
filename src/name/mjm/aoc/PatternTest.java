package name.mjm.aoc;

import name.mjm.aoc.test.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {

  void testRegexpForDayClass() {
    Pattern pattern = Pattern.compile("[Aa](\\d*)");

    Matcher aMatcher = pattern.matcher("a");
    Matcher a1Matcher = pattern.matcher("a1");
    Matcher a23Matcher = pattern.matcher("a23");

    Assert.that(aMatcher.matches()).isTrue();
    Assert.that(pattern.matcher("b").matches()).isFalse();
    Assert.that(pattern.matcher("A").matches()).isTrue();
    Assert.that(a1Matcher.matches()).isTrue();
    Assert.that(pattern.matcher("ab").matches()).isFalse();
    Assert.that(a23Matcher.matches()).isTrue();
    Assert.that(pattern.matcher("aa22").matches()).isFalse();
    Assert.that(pattern.matcher("a22a").matches()).isFalse();

    Assert.that(a1Matcher.group(1)).is("1");
    Assert.that(a23Matcher.group(1)).is("23");
    Assert.that(aMatcher.group(1)).isEmpty();
  }

  void testResourceName1() {
    Matcher matcher = DayResourcesProvider.PATTERN.matcher("day10");

    Assert.that(matcher.matches()).isTrue();
    Assert.that(matcher.group(1)).is("10");
    Assert.that(matcher.group(2)).isEmpty();
  }

  void testResourceName2() {
    Matcher matcher = DayResourcesProvider.PATTERN.matcher("day10.txt");

    Assert.that(matcher.matches()).isTrue();
    Assert.that(matcher.group(1)).is("10");
    Assert.that(matcher.group(2)).isEmpty();
    Assert.that(matcher.group(5)).is(".txt");
  }

  void testResourceName3() {
    Matcher matcher = DayResourcesProvider.PATTERN.matcher("day12a-vole.txt");

    Assert.that(matcher.matches()).isTrue();
    Assert.that(matcher.group(1)).is("12");
    Assert.that(matcher.group(2)).is("a");
    Assert.that(matcher.group(3)).is("-vole");
    Assert.that(matcher.group(5)).is(".txt");
  }

  void testResourceName4() {
    Matcher matcher = DayResourcesProvider.PATTERN.matcher("day12a-vole_try34.txt");

    Assert.that(matcher.matches()).isTrue();
    Assert.that(matcher.group(1)).is("12");
    Assert.that(matcher.group(2)).is("a");
    Assert.that(matcher.group(3)).is("-vole");
    Assert.that(matcher.group(4)).is("_try34");
    Assert.that(matcher.group(5)).is(".txt");
  }

  void testResourceName5() {
    Matcher matcher = DayResourcesProvider.PATTERN.matcher("day12_try34.txt");

    Assert.that(matcher.matches()).isTrue();
    Assert.that(matcher.group(1)).is("12");
    Assert.that(matcher.group(2)).isEmpty();
    Assert.that(matcher.group(3)).isNullOrEmpty();
    Assert.that(matcher.group(4)).is("_try34");
    Assert.that(matcher.group(5)).is(".txt");
  }

  void testSubstring() {
    String test = "_try-1".substring(4);
    Assert.that(test).is("-1");
  }
}
