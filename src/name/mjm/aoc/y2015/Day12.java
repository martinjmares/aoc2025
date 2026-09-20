package name.mjm.aoc.y2015;

import name.mjm.aoc.Data;
import name.mjm.aoc.Datas;
import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.model.json.JsonArrayArray;
import name.mjm.aoc.model.json.JsonElement;
import name.mjm.aoc.model.json.JsonLong;
import name.mjm.aoc.model.json.JsonObject;
import name.mjm.aoc.model.json.JsonParser;
import name.mjm.aoc.model.json.JsonString;

import java.util.function.Function;

public class Day12 extends ParentDay {

  @Data(tryId = 1, value = "[[[[3]]],{\"a\":{\"b\":4},\"c\":-1},[1,2,3],{\"a\":[-1,1]},[-1,{\"a\":1}]]")
  @TryResult("12")
  public long a(String input) {
    return sumNonQuotedNumbers(input);
  }

  long sumNonQuotedNumbers(String input) {
    long sum = 0;
    boolean inQuot = false;
    StringBuilder numberCollector = new StringBuilder();
    for (char c : input.toCharArray()) {
      if (c == '"') {
        inQuot = !inQuot;
      } else if (!inQuot) {
        if (c == '-' || Character.isDigit(c)) {
          numberCollector.append(c);
        } else  {
          if (!numberCollector.isEmpty()) {
            sum += Long.parseLong(numberCollector.toString());
            numberCollector.setLength(0);
          }
        }
      }
    }
    return sum;
  }

  @Data(tryId = 1, value = "[1,{\"c\":\"red\",\"b\":2},3,[1,\"red\",5]]")
  @TryResult("10")
  public long b(String input) {
    Function<JsonElement, JsonElement> transformer = (element) -> {
      if (element instanceof JsonObject jobj) {
        boolean hasRed = jobj.stream()
                        .filter(JsonString.class::isInstance)
                        .map(JsonString.class::cast)
                        .map(JsonString::get)
                        .anyMatch("red"::equals);
        if (hasRed) {
          return new JsonLong(0L);
        }
        // Not red, count numbers
        long sum = jobj.stream()
                       .filter(JsonLong.class::isInstance)
                       .map(JsonLong.class::cast)
                       .mapToLong(JsonLong::get)
                       .sum();
        return new JsonLong(sum);
      } else if (element instanceof JsonArrayArray jarr) {
        long sum = jarr.stream()
                       .filter(JsonLong.class::isInstance)
                       .map(JsonLong.class::cast)
                       .mapToLong(JsonLong::get)
                       .sum();
        return new JsonLong(sum);
      } else {
        return element;
      }
    };

    JsonElement result = JsonParser.fromString(input, transformer);
    if (result instanceof JsonLong jlong) {
      return jlong.get();
    } else {
      throw new RuntimeException("It should be JsonLong and not " + result.getClass());
    }
  }


}
