package name.mjm.aoc;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import name.mjm.aoc.log.Logger;

public class ResourceUtils {

  private static Logger logger = new Logger(ResourceUtils.class);

  private ResourceUtils() {
    // Utility class
  }

  public static InputStream resource2Stream(String resource) {
    return ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
  }

  public static String loadResourceAsString(String resource) {
    try (
        InputStream stream = resource2Stream(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    ) {
      return reader.readAllAsString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<String> loadResourceAsLines(String resource) {
    try (
        InputStream stream = resource2Stream(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    ) {
      return reader.readAllLines();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<String> listResources(String dir, Pattern pattern) {
    try (
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(dir);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    ) {
      return reader.lines()
          .filter(line -> pattern.matcher(line).matches())
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Set<Class> findAllClassesRecursively(String packageName) throws IOException {
    String pn = packageName == null ? ResourceUtils.class.getPackageName() : packageName;
    try (InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(pn.replaceAll(
              "[.]",
              "/"
          ));
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
      Set<String> subdirectories = new HashSet<String>();
      Set<Class> result = reader.lines()
                                .peek(line -> {
                                  if (!line.contains(".")) {
                                    subdirectories.add(pn + "." + line);
                                  }
                                })
                                .filter(line -> line.endsWith(".class"))
                                .map(line -> toClass(line, pn))
                                .collect(Collectors.toSet());
      for (String subdir : subdirectories) {
        result.addAll(findAllClassesRecursively(subdir));
      }
      return result;
    }
  }

  private static Class toClass(String className, String packageName) {
    try {
      return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}