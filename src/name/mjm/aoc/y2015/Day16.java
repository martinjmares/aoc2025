package name.mjm.aoc.y2015;

import name.mjm.aoc.ParentDay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class Day16 extends ParentDay {

  private static final String MFCSAM_RES = """
      children: 3
      cats: 7
      samoyeds: 2
      pomeranians: 3
      akitas: 0
      vizslas: 0
      goldfish: 5
      trees: 3
      cars: 2
      perfumes: 1
      """;
  private PropertyValueMap createMfcsamMap() {
    String input = MFCSAM_RES;
    StringTokenizer rows = new StringTokenizer(input, "\n");
    PropertyValueMap result = new PropertyValueMap();
    while (rows.hasMoreTokens()) {
      String row = rows.nextToken().trim();
      if (row.isEmpty()) {
        continue;
      }
      int index = row.indexOf(':');
      result.set(Properties.valueOf(row.substring(0, index).trim()),
                 Integer.parseInt(row.substring(index + 1).trim()));
    }
    return result;
  }

  public String a(ArrayList<Sue> sues) {
    PropertyValueMap mfcsamMap = createMfcsamMap();
    List<Sue> valiedSues = sues.stream()
                         .filter(sue -> isValidSueForA(sue, mfcsamMap))
                         .peek(sue -> logger.debug("Valied sue: " + sue.id))
                         .toList();
    if (valiedSues.isEmpty()) {
      throw new IllegalArgumentException("No valid sues found");
    }
    if (valiedSues.size() > 1) {
      throw new IllegalArgumentException("Too many valid sues found");
    }
    return valiedSues.get(0).id;
  }

  private boolean isValidSueForA(Sue sue, PropertyValueMap mfcsamMap) {
    int[] suePopsValues = sue.props.values;
    for (int i = 0; i < suePopsValues.length; i++) {
      if (suePopsValues[i] > -1 && suePopsValues[i] != mfcsamMap.values[i]) {
        return false;
      }
    }
    return true;
  }

  public String b(ArrayList<Sue> sues) {
    PropertyValueMap mfcsamMap = createMfcsamMap();
    List<Sue> valiedSues = sues.stream()
                               .filter(sue -> isValidSueForB(sue, mfcsamMap))
                               .peek(sue -> logger.debug("Valied sue: " + sue.id))
                               .toList();
    if (valiedSues.isEmpty()) {
      throw new IllegalArgumentException("No valid sues found");
    }
    if (valiedSues.size() > 1) {
      throw new IllegalArgumentException("Too many valid sues found");
    }
    return valiedSues.get(0).id;
  }

  private boolean isValidSueForB(Sue sue, PropertyValueMap mfcsamMap) {
    int[] suePopsValues = sue.props.values;
    for (int i = 0; i < suePopsValues.length; i++) {
      if (suePopsValues[i] <= -1) {
        continue;
      }
      if (!Properties.values()[i].test(suePopsValues[i],  mfcsamMap.values[i])) {
        return false;
      }
    }
    return true;
  }

  public enum Properties {
    children(TwoIntsPredicate.EQ),
    cats(TwoIntsPredicate.GT),
    samoyeds(TwoIntsPredicate.EQ),
    pomeranians(TwoIntsPredicate.FT),
    akitas(TwoIntsPredicate.EQ),
    vizslas(TwoIntsPredicate.EQ),
    goldfish(TwoIntsPredicate.FT),
    trees(TwoIntsPredicate.GT),
    cars(TwoIntsPredicate.EQ),
    perfumes(TwoIntsPredicate.EQ);

    final TwoIntsPredicate bPredicate;

    Properties(TwoIntsPredicate bPredicate) {
      this.bPredicate = bPredicate;
    }

    boolean test(int tested, int expected) {
      return bPredicate.test(tested, expected);
    }
  }

  @FunctionalInterface
  public static interface TwoIntsPredicate {
    public static final TwoIntsPredicate EQ = ((tested, expected) -> tested == expected);
    public static final TwoIntsPredicate GT = ((tested, expected) -> tested > expected);
    public static final TwoIntsPredicate FT = ((tested, expected) -> tested < expected);
    boolean test(int tested, int expected);
  }

  /**
   * Just for fun, I am making own, fast specialised map between enum and ints.
   */
  public static class PropertyValueMap {
    private final int[] values = new int[Properties.values().length];

    public PropertyValueMap() {
      Arrays.fill(values, -1);
    }

    public int get(Properties prop) {
      return values[prop.ordinal()];
    }

    public void set(Properties prop, int value) {
      values[prop.ordinal()] = value;
    }
  }

  public record Sue(String id, PropertyValueMap props) {
    public Sue(String line) {
      int index = line.indexOf(':');
      String name = line.substring(0, index);
      if (!name.startsWith("Sue ")) {
        throw new IllegalArgumentException("Unexpected line: " + line);
      }
      name = name.substring("Sue ".length()).trim();

      // Parse properties
      PropertyValueMap props = new PropertyValueMap();
      StringTokenizer tokenizer = new StringTokenizer(line.substring(index + 1), ",");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        int inIndex = token.indexOf(':');
        String key = token.substring(0, inIndex).trim();
        String value = token.substring(inIndex + 1).trim();
        props.set(Properties.valueOf(key), Integer.parseInt(value));
      }
      this(name, props);
    }
  }
}
