package name.mjm.aoc.y2015;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day16 extends ParentDay {

  @TryResult("62842880")
  public int a(ArrayList<Ingredient> ingredients) {
    Leader leader = brutForce(ingredients, false);
    logger.info("The leading configuration is " + Arrays.toString(leader.configuration) + "; value: " + leader.value);
    return leader.value;
  }

  @TryResult("57600000")
  public int b(ArrayList<Ingredient> ingredients) {
    Leader leader = brutForce(ingredients, true);
    logger.info("The leading configuration is " + Arrays.toString(leader.configuration) + "; value: " + leader.value);
    return leader.value;
  }

  Leader brutForce(ArrayList<Ingredient> ingredientsList, boolean caloriesTest) {
    Ingredient[] ingredients = ingredientsList.toArray(new Ingredient[0]);
    int[] configuration = new int[ingredients.length];
    Leader leader = new Leader();
    brutForceIterative(ingredients, 0, configuration, 0, 100, caloriesTest, leader);
    return leader;
  }

  void brutForceIterative(Ingredient[] ingredients, int pointer, int[] configuration, int currentSize, int sizeLimit, boolean caloriesTest, Leader leader) {
    if (pointer == (configuration.length - 1)) {
      // I am last one
      configuration[pointer] = sizeLimit - currentSize;
      int value = computeNutrientsOfTheProduct(ingredients, configuration, caloriesTest);
      if (value > leader.value) {
        leader.value = value;
        leader.configuration = Arrays.copyOf(configuration, configuration.length);
      }
      return;
    }

    // Iterations
    for (int i = 0; i <= (sizeLimit - currentSize); i++) {
      configuration[pointer] = i;
      brutForceIterative(ingredients, pointer + 1, configuration, currentSize + i, sizeLimit, caloriesTest, leader);
    }
  }

  int computeNutrientsOfTheProduct(Ingredient[] ingredients, int[] configuration, boolean caloriesTest) {
    int capacity = 0;
    int durability = 0;
    int flavor = 0;
    int texture = 0;
    int calories = 0;
    for (int i = 0; i < ingredients.length; i++) {
      capacity += configuration[i] * ingredients[i].capacity;
      durability += configuration[i] * ingredients[i].durability;
      flavor += configuration[i] * ingredients[i].flavor;
      texture += configuration[i] * ingredients[i].texture;
      calories += configuration[i] * ingredients[i].calories;
    }
    if (caloriesTest && calories != 500) {
      return -1;
    }
    if (capacity <= 0 || durability <= 0 || flavor <= 0 || texture <= 0) {
      return 0;
    }
    return capacity * durability * flavor * texture;
  }

  static class Leader {
    int[] configuration;
    int value = -1;
  }

  public record Ingredient(String name, int capacity, int durability, int flavor, int texture, int calories) {

    private static final Pattern PATTERN =
        Pattern.compile("([a-zA-Z]+): capacity (-?\\d+), durability (-?\\d+), flavor (-?\\d+), texture (-?\\d+), calories (-?\\d+)");

    public Ingredient(String line) {
      Matcher matcher = PATTERN.matcher(line);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Line is invalid: " + line);
      }
      this(matcher.group(1),
           Integer.parseInt(matcher.group(2)),
           Integer.parseInt(matcher.group(3)),
           Integer.parseInt(matcher.group(4)),
           Integer.parseInt(matcher.group(5)),
           Integer.parseInt(matcher.group(6)));
    }
  }
}
