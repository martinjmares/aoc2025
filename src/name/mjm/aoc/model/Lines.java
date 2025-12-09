package name.mjm.aoc.model;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class Lines extends ArrayList<String> {

  public Lines(Reader reader) throws IOException {
    super(reader.readAllLines());
  }

  public Lines(int size) {
    super(size);
  }

  public Lines toNonEmpty() {
    Lines result = new Lines(this.size());
    for (String s : this) {
      if (!s.isBlank()) {
        result.add(s);
      }
    }
    return result;
  }
}
