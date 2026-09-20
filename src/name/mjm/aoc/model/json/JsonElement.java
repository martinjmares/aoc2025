package name.mjm.aoc.model.json;

public interface JsonElement {

  String toJsonString();

  default String toJsonStringIndented() {
    return toJsonStringIndented("", "  ");
  }

  /**
   * Returns indented (human convenient) JSON format.
   *
   * @param baseIndentation defines an indentation of the second line, if there is such thing.
   * @param incrementIndentation add this to base for child indentation if on the new line
   * @return indented JSON
   */
  String toJsonStringIndented(String baseIndentation, String incrementIndentation);
}
