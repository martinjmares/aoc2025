package name.mjm.aoc.y2015;

import name.mjm.aoc.test.Assert;

public class Day13Test {

  public void testNeighborsParsing1() {
    Day13.Naighbors naighbors = new Day13.Naighbors("Alice would gain 54 happiness units by sitting next to Bob.");

    Assert.that(naighbors.nameA())
        .is("Alice");
    Assert.that(naighbors.nameB())
          .is("Bob");
    Assert.that(naighbors.value())
          .is(54);
  }

  public void testNeighborsParsing2() {
    Day13.Naighbors naighbors = new Day13.Naighbors("Alice would lose 79 happiness units by sitting next to Carol.");

    Assert.that(naighbors.nameA())
          .is("Alice");
    Assert.that(naighbors.nameB())
          .is("Carol");
    Assert.that(naighbors.value())
          .is(-79);
  }

}
