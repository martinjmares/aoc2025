package name.mjm.aoc.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Csv {
  private final List<String> values;

  public Csv(BufferedReader reader) throws IOException {
    String s = reader.readAllAsString();
    StringTokenizer tokenizer = new StringTokenizer(s, ",");
    this.values = new ArrayList<>();
    while (tokenizer.hasMoreTokens()) {
      this.values.add(tokenizer.nextToken());
    }
  }

  public List<String> values() {
    return values;
  }
}
