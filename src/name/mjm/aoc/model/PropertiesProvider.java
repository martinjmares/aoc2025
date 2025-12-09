package name.mjm.aoc.model;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class PropertiesProvider {

  final Properties properties;

  public PropertiesProvider(Reader reader) throws IOException {
    this.properties = new Properties();
    this.properties.load(reader);
  }

  public Properties getProperties() {
    return properties;
  }
}
