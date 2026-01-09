package name.mjm.aoc;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

class ParametersProvider {

  private final int dayId;
  private final CalendarManager.PhaseDef phase;
  private final DayResourcesProvider resourcesProvider;
  private final Class dayClass;
  private final Method method;
  private final ArrayList<Data> allData = new ArrayList<>();

  public ParametersProvider(int dayId, CalendarManager.PhaseDef phase, DayResourcesProvider resourcesProvider, Class dayClass, Method method) {
    this.dayId = dayId;
    this.phase = phase;
    this.resourcesProvider = resourcesProvider;
    this.dayClass = dayClass;
    this.method = method;

    // Class level annotation first
    Datas datas = (Datas) dayClass.getAnnotation(Datas.class);
    if (datas != null && datas.value() != null && datas.value().length > 0) {
      allData.addAll(Arrays.asList(datas.value()));
    }
    Data data = (Data) dayClass.getAnnotation(Data.class);
    addOrReplace(allData, data);
    // Method level annotation replaces the class level
    datas = method.getAnnotation(Datas.class);
    if (datas != null && datas.value() != null && datas.value().length > 0) {
      for (Data d : datas.value()) {
        addOrReplace(allData, d);
      }
    }
    data = method.getAnnotation(Data.class);
    addOrReplace(allData, data);
  }

  private static void addOrReplace(ArrayList<Data> dts, Data d) {
    if (d == null || d.value() == null) {
      return;
    }
    dts.stream()
        .filter(dt -> Objects.equals(dt.name(), d.name()) && d.tryId() == dt.tryId())
        .findAny()
        .ifPresent(dts::remove);
    dts.add(d);
  }

  <T> T createValueForParameter(int tryId, String name, Class<T> clazz, Type genericType) {
    if (tryId < 0) {
      tryId = -1;
    }
    if (name == null) {
      name = "";
    }

    // Data annotation first
    Data data = null;
    for (Data d : allData) {
      if (d.tryId() == tryId && name.equals(d.name())) {
        data = d;
        break;
      }
    }

    // Resource file next
    String resourceName = data == null
        ? resourcesProvider.findResource(dayId, phase, tryId, name)
        : null;
    if (resourceName == null && data == null) {
      throw new IllegalStateException("Could not find data for parameter.  Day: " + dayId + ", Phase: " + phase);
    }

    // String
    if (clazz.isAssignableFrom(String.class)) {
      return data == null ? (T) ResourceUtils.loadResourceAsString(resourceName) : (T) data.value();
    }

    // Lists
    if (clazz.isAssignableFrom(ArrayList.class)) {
      if (genericType instanceof ParameterizedType pt) {
        Type[] actualTypeArguments = pt.getActualTypeArguments();
        if (actualTypeArguments.length != 1) {
          throw new IllegalArgumentException("Expected a single single generic argument of type list, but it has " + actualTypeArguments.length);
        }
        Class actualTypeArgument = (Class) actualTypeArguments[0];

        // Convert input to a list of strings
        List<String> lines = data == null
            ? ResourceUtils.loadResourceAsLines(resourceName)
            : Arrays.asList(data.value().split("\\n"));
        if (actualTypeArgument.isAssignableFrom(String.class)) {
          return (T) new ArrayList<>(lines);
        }

        Constructor<T> elementConstructor;
        try {
          elementConstructor = actualTypeArgument.getConstructor(String.class);
          elementConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
          throw new RuntimeException("List parameter must be constructable from string!", e);
        }

        try {
          ArrayList<T> result = new ArrayList<>(lines.size());
          String line;
          for (String l : lines) {
            l = l.trim();
            if (!l.isEmpty()) {
              result.add(elementConstructor.newInstance(l));
            }
          }
          return (T) result;
        } catch (RuntimeException re) {
          throw re;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      } else {
        throw new IllegalArgumentException("List parameter should have generic, but it do not: " + genericType);
      }
    }

    // From reader
    try {
      Constructor<T> constructor = clazz.getConstructor(BufferedReader.class);
      constructor.setAccessible(true);
      if (data == null) {
        try (
            InputStream stream = ResourceUtils.resource2Stream(resourceName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        ) {
          return constructor.newInstance(reader);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      } else {
        try (
            Reader inputString = new StringReader(data.value());
            BufferedReader reader = new BufferedReader(inputString);
        ) {
          return constructor.newInstance(reader);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    } catch (NoSuchMethodException e) {
    }

    // From String
    try {
      Constructor<T> constructor = clazz.getConstructor(String.class);
      constructor.setAccessible(true);
      String str = data == null
          ? ResourceUtils.loadResourceAsString(resourceName)
          : data.value();
      return constructor.newInstance(str);
    } catch (NoSuchMethodException e) {
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    throw new RuntimeException("Could not load class parameter for day " + dayId + ", phase " + phase);
  }

  List<Integer> getTryIds() {
    if (allData.isEmpty()) {
      return resourcesProvider.getTryIds(dayId, phase);
    }
    Set<Integer> tryIds = new TreeSet<>();
    for (Data data : allData) {
      if (data.tryId() >= 0) {
        tryIds.add(data.tryId());
      }
    }
    tryIds.addAll(resourcesProvider.getTryIds(dayId, phase));
    return new ArrayList<>(tryIds);
  }


}
