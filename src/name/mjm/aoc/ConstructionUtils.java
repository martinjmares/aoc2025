package name.mjm.aoc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstructionUtils {

  private ConstructionUtils() {
    // Util class, never construct
    throw new IllegalAccessError("Utility class");
  }

  public static <T> Function<String, T> createBuilderFromString(Class<T> clazz) {
    // String
    if (String.class.isAssignableFrom(clazz)) {
      return (s) -> (T) s;
    }

    // Primitive types
    if (clazz.isPrimitive()) {
      if (Integer.TYPE.isAssignableFrom(clazz)) {
        return (strVal) -> (T) Integer.valueOf(strVal);
      } else if (Long.TYPE.isAssignableFrom(clazz)) {
        return (strVal) -> (T) Long.valueOf(strVal);
      } else if (Double.TYPE.isAssignableFrom(clazz)) {
        return (strVal) -> (T) Double.valueOf(strVal);
      } else if (Float.TYPE.isAssignableFrom(clazz)) {
        return (strVal) -> (T) Float.valueOf(strVal);
      } else if (Boolean.TYPE.isAssignableFrom(clazz)) {
        return (strVal) -> (T) Boolean.valueOf(strVal);
      } else if (Character.TYPE.isAssignableFrom(clazz)) {
        return (strVal) -> {
          if (strVal.length() != 1) {
            throw new IllegalArgumentException("Cannot cast " + strVal + " to char because it has not exactly one character!");
          }
          return (T) Character.valueOf(strVal.charAt(0));
        };
      }
    }

    // FromRegexp annotation on constructor
    Constructor<?>[] allConstructors = clazz.getConstructors();
    for (Constructor<?> constructor : allConstructors) {
      FromRegexpGroups regexpAnnotation = constructor.getAnnotation(FromRegexpGroups.class);
      if (regexpAnnotation != null) {
        Parameter[] parameters = constructor.getParameters();
        List<ParamInfo> paramInfos = toParamInfo(parameters);
        Pattern pattern = Pattern.compile(regexpAnnotation.value());
        return (s) -> {
          Object[] paramValues = createParamsUsingRegexp(s, pattern, paramInfos);
          try {
            return (T) constructor.newInstance(paramValues);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        };
      }
    }

    // FromRegexp annotation on class in case, there is exactly one constructor
    FromRegexpGroups fromRegexpGroups = clazz.getAnnotation(FromRegexpGroups.class);
    if (fromRegexpGroups != null) {
      if (allConstructors.length != 1) {
        throw new IllegalArgumentException("Cannot construct class with class level FromRegexpGroups annotation that do not have exactly single constructor! Class: "
                                               + clazz.getSimpleName() + "; Number of constructors: " + allConstructors.length);
      }
      Constructor<?> constructor = allConstructors[0];
      List<ParamInfo> paramInfos = toParamInfo(constructor.getParameters());
      Pattern pattern = Pattern.compile(fromRegexpGroups.value());
      return (s) -> {
        Object[] paramValues = createParamsUsingRegexp(s, pattern, paramInfos);
        try {
          return (T) constructor.newInstance(paramValues);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      };
    }

    // Static method with FromRegexpGroup annotation
    Method[] methods = clazz.getMethods();
    for (Method m : methods) {
      FromRegexpGroups fromRegexp = m.getAnnotation(FromRegexpGroups.class);
      if (fromRegexp != null) {
        if (Modifier.isStatic(m.getModifiers()) && clazz.isAssignableFrom(m.getReturnType())) {
          List<ParamInfo> paramInfos = toParamInfo(m.getParameters());
          Pattern pattern = Pattern.compile(fromRegexp.value());
          return (s) -> {
            Object[] paramValues = createParamsUsingRegexp(s, pattern, paramInfos);
            try {
              return (T) m.invoke(null, paramValues);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          };
        } else {
          throw new IllegalStateException("Find method with @FromRegexpGroups annotated but is not sutable for construction. Type: " + clazz.getSimpleName() + "; Method: " + m.getName());
        }
      }
    }

    // valueOf static method from String
    try {
      Method m = clazz.getMethod("valueOf", String.class);
      if (Modifier.isStatic(m.getModifiers()) && clazz.isAssignableFrom(m.getReturnType())) {
        return (s) -> {
          try {
            return (T) m.invoke(null, s);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        };
      }
    } catch (NoSuchMethodException e) {
      // Never mind, try the next possibility
    }

    // Public Constructor
    try {
      Constructor<T> constructor = clazz.getConstructor(String.class);
      return (s) -> {
        try {
          return (T) constructor.newInstance(s);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      };
    } catch (NoSuchMethodException e) {
      // Never mind, try the next possibility
    }

    // Declared non-public constructor
    try {
      Constructor<T> constructor = clazz.getDeclaredConstructor(String.class);
      constructor.setAccessible(true);
      return (s) -> {
        try {
          return (T) constructor.newInstance(s);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      };
    } catch (NoSuchMethodException e) {
      // Never mind, try the next possibility
    }

    throw new IllegalStateException("Cannot find String based building method for class: " + clazz);
  }

  private static Object[] createParamsUsingRegexp(String s, Pattern pattern, List<ParamInfo> paramInfos) {
    Matcher matcher = pattern.matcher(s);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Input is not matching expected regexp. Input: '"
                                             + s + "'; regexp: '"
                                             + pattern.pattern() + "'");
    }
    Object[] paramValues = paramInfos.stream()
                                     .map(pi -> {
                                 String val;
                                 if (pi.isNamed()) {
                                   val = matcher.group(pi.name());
                                 } else {
                                   val = matcher.group(pi.index + 1);
                                 }
                                 return pi.paramBuilder.apply(val);
                               })
                                     .toArray();
    return paramValues;
  }

  private static List<ParamInfo> toParamInfo(Parameter[] parameters) {
    List<ParamInfo> paramInfos = new ArrayList<>(parameters.length);
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      Named named = parameter.getAnnotation(Named.class);
      String name = named == null ? null : named.value();
      ParamInfo paramInfo = new ParamInfo(name,
                                          i,
                                          createBuilderFromString(parameter.getType()));
      paramInfos.add(paramInfo);
    }
    return paramInfos;
  }

  private record ParamInfo<T>(String name, int index, Function<String, T> paramBuilder) {
    boolean isNamed() {
      return name != null;
    }
  }
}
