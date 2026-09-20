package name.mjm.aoc.model;

import java.util.Arrays;

/**
 * Represent a path in the tree.
 * Empty is root.
 */
public class TreePath {
  private final int[] path;

  public TreePath(int[] path) {
    if (path == null) {
      this.path = new int[0];
    } else {
      this.path = Arrays.copyOf(path, path.length);
    }
  }

  public TreePath(TreePath parent, int child) {
    this.path = Arrays.copyOf(parent.path, parent.path.length + 1);
    this.path[this.path.length - 1] = child;
  }

  public int size() {
    return path.length;
  }

  public int get(int index) {
    return path[index];
  }

  @Override
  public String toString() {
    return "TreePath" + Arrays.toString(path);
  }
}
