package name.mjm.aoc.test;

public class CharSequenceAsserter extends Asserter<CharSequence, CharSequenceAsserter> {

  CharSequenceAsserter(CharSequence target, boolean negate) {
    super(target, negate);
  }

  protected String toPrintable(CharSequence seq) {
    if (seq == null) {
      return "<null>";
    }
    if (seq.length() > 38) {
      int len = seq.length();
      return "\"" + seq.subSequence(0, 30) + "...\"(" + len + " chars)";
    } else {
      return "\"" + seq + "\"";
    }
  }

  public  CharSequenceAsserter not() {
    return new CharSequenceAsserter(target, !negate);
  }

  public CharSequenceAsserter contains(CharSequence expected) {
    check(t -> t != null && t.toString().contains(expected), "Expecting $not contains $param but it was not true for $target!");
    return this;
  }

  public CharSequenceAsserter isEmpty() {
    check(t -> t != null && t.isEmpty(), "Expecting $not empty string but it was $target!");
    return this;
  }

  public CharSequenceAsserter isNullOrEmpty() {
    check(t -> t == null || t.isEmpty(), "Expecting $not null or $not empty string but it was $target!");
    return this;
  }

  public CharSequenceAsserter isNotEmpty() {
    check(t -> t != null && !t.isEmpty(), "Expecting $not not empty string but it was $target!");
    return this;
  }

  public CharSequenceAsserter isEffectivelyEmpty() {
    check(t -> t == null || t.toString().trim().isEmpty(), "Expecting $not effectively empty (null or empty or whitespaces) string but it was $target!");
    return this;
  }
}
