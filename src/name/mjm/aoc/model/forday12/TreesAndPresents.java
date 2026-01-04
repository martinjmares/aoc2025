package name.mjm.aoc.model.forday12;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class TreesAndPresents {
  private final List<Present> presents;
  private final List<TreeSpace> trees;

  public TreesAndPresents(Reader reader) throws IOException {
    List<String> allLines = reader.readAllLines();

    this.presents = new ArrayList<>();
    this.trees = new ArrayList<>();
    ArrayList<String> tmpLines = new ArrayList<>();
    boolean readShape = false;
    for (String line : allLines) {
      line = line.trim();
      if (readShape) {
        // SHAPE
        if (line.isEmpty()) {
          // End of shape
          this.presents.add(new Present(tmpLines));
          tmpLines.clear();
          readShape = false;
        } else {
          tmpLines.add(line);
        }
      } else if (line.endsWith(":")) {
        readShape = true;
        tmpLines.add(line);
      } else if (!line.isEmpty()) {
        this.trees.add(new TreeSpace(line));
      }
    }
  }

  public List<Present> getPresents() {
    return presents;
  }

  public Present getPresent(int index) {
    return presents.get(index);
  }

  public List<TreeSpace> getTrees() {
    return trees;
  }
}
