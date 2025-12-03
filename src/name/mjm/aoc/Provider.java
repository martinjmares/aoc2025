package name.mjm.aoc;

@FunctionalInterface
public interface Provider<T> {

  T provide();
}
