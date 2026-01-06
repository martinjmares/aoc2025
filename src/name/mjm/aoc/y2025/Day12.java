package name.mjm.aoc.y2025;

import name.mjm.aoc.ParentDay;
import name.mjm.aoc.y2025.forday12.Present;
import name.mjm.aoc.y2025.forday12.TreeSpace;
import name.mjm.aoc.y2025.forday12.TreesAndPresents;

public class Day12 extends ParentDay {

  public long a(TreesAndPresents tps) {
    // Loaded
    for (Present present : tps.getPresents()) {
      logger.debug("Present: \n" + present);
    }
    logger.debug("Treespaces count: " + tps.getTrees().size());

    // Find candidates
    int cannotFit = 0;
    int mustFit = 0;
    for (TreeSpace treeSpace : tps.getTrees()) {
      int minAcceptable = 0;
      int maxAcceptable = 0;
      int[] presentsCounts = treeSpace.getPresentsCounts();
      for (int i = 0; i < presentsCounts.length; i++) {
        minAcceptable += presentsCounts[i] * tps.getPresent(i).getFillSize();
        maxAcceptable += presentsCounts[i] * 9;
      }
      int rangeSize = treeSpace.getRowsCount() * treeSpace.getColumnsCount();
      if (rangeSize < minAcceptable) {
        cannotFit++;
      } else if (rangeSize >= maxAcceptable) {
        mustFit++;
      }
    }
    long whoKnows = tps.getTrees().size() - cannotFit - mustFit;
    logger.info("CANNOT FIT: " + cannotFit);
    logger.info("MUST FIT: " + mustFit);
    logger.info("WHO KNOWS: " + whoKnows);

    return whoKnows == 0 ? mustFit : -1;
  }
}
