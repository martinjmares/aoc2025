package name.mjm.aoc.model;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class Lines extends ArrayList<String> {

  public Lines(Reader reader) throws IOException {
    super(reader.readAllLines());
  }
}
