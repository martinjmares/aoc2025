package name.mjm.aoc;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayResourcesProvider {

  public static String DEFAULT_RESOURCES_DIRECTORY = "resources";
  // Day(dayNum)(AB)(name)(tryId)(suffix)
  // Day12a-jmeno_try1.txt
  static final Pattern PATTERN = Pattern.compile("[dD]ay(\\d+)([aAbB]{0,2})(-[a-zA-Z0-9]+)?(_try\\d+)?(\\.[a-zA-Z0-9]{1,50})?");

  private final Map<DayAndPhase, ResourcePack> resources = new HashMap<>();

  public DayResourcesProvider(String resourceDirectory) {
    final String resourceDirName = resourceDirectory == null ? DEFAULT_RESOURCES_DIRECTORY : resourceDirectory;
    List<String> ress = ResourceUtils.listResources(resourceDirName, PATTERN);

    for (String listResource : ress) {
      Matcher matcher = PATTERN.matcher(listResource);
      if (matcher.matches()) {
        // Parse identifiers
        int day = NumberUtils.parseInt(matcher.group(1), -1);
        String phaseStr = StringUtils.orElse(matcher.group(2), "ab").toLowerCase();
        boolean isA = phaseStr.contains("a");
        boolean isB = phaseStr.contains("b");
        String name = StringUtils.orElse(matcher.group(3), "-").substring(1);
        String tryStr = StringUtils.orElse(matcher.group(4), "_try-1").substring(4);
        int tryId = NumberUtils.parseInt(tryStr, -1);

        // Construct packs
        if (isA) {
          resources.computeIfAbsent(new DayAndPhase(day, CalendarManager.PhaseDef.A), ResourcePack::new)
              .addResource(tryId, name, resourceDirName, listResource);
        }
        if (isB) {
          resources.computeIfAbsent(new DayAndPhase(day, CalendarManager.PhaseDef.B), ResourcePack::new)
                   .addResource(tryId, name, resourceDirName, listResource);
        }
      }
    }
  }

  public List<Integer> getTryIds(int day, CalendarManager.PhaseDef phase) {
    ResourcePack resourcePack = resources.get(new DayAndPhase(day, phase));
    if (resourcePack == null) {
      return Collections.emptyList();
    }
    ArrayList<Integer> result = new ArrayList<>(resourcePack.tryResources.keySet());
    result.sort(null);
    return result;
  }

  public String findResource(int day, CalendarManager.PhaseDef phase, int tryId, String name) {
    if (name == null) {
      name = "";
    }
    ResourcePack resourcePack = resources.get(new DayAndPhase(day, phase));
    if (resourcePack == null) {
      return null;
    }
    if (tryId >= 0) {
      Map<String, TryResource> nameTryResource = resourcePack.tryResources.get(tryId);
      if (nameTryResource == null) {
        return null;
      }
      TryResource tryResource = nameTryResource.get(name);
      if (tryResource == null) {
        return null;
      }
      return tryResource.resourceDir + "/" + tryResource.resourceName;
    } else {
      NamedResource namedResource = resourcePack.mainResources.get(name);
      if (namedResource == null) {
        return null;
      }
      return namedResource.resourceDir + "/" + namedResource.resourceName;
    }
  }

  private static class ResourcePack {
    final DayAndPhase dayAndPhase;
    final Map<String, NamedResource> mainResources = new HashMap<>();
    final Map<Integer, Map<String, TryResource>> tryResources = new HashMap<>();

    ResourcePack(DayAndPhase dayAndPhase) {
      this.dayAndPhase = dayAndPhase;
    }

    void addResource(int tryId, String name, String dirName, String resourceName) {
      if (tryId >= 0) {
        TryResource tryResource = new TryResource(name, dirName, resourceName, tryId);
        tryResources.computeIfAbsent(tryId, k -> new HashMap<>())
            .put(name, tryResource);
      } else {
        NamedResource namedResource = new NamedResource(name, dirName, resourceName);
        mainResources.put(name, namedResource);
      }
    }
  }

  private static class TryResource extends NamedResource{
    final int tryId;

    public TryResource(String name, String resourceDir, String resourceName, int tryId) {
      super(name, resourceDir, resourceName);
      this.tryId = tryId;
    }

    public String toString() {
      return "TryResource: try:" + tryId + ", name:" + name + " (" + resourceName + ")";
    }
  }

  private static class NamedResource {
    final String name;
    final String resourceDir;
    final String resourceName;

    public NamedResource(String name, String resourceDir, String resourceName) {
      this.resourceDir = resourceDir;
      this.resourceName = resourceName;
      this.name = name;
    }

    public String toString() {
      return "NamedResource: " + name + " (" + resourceName + ")";
    }
  }

  private static class DayAndPhase {
    final int day;
    final CalendarManager.PhaseDef phaseDef;

    public DayAndPhase(int day, CalendarManager.PhaseDef phaseDef) {
      this.day = day;
      this.phaseDef = phaseDef;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      DayAndPhase that = (DayAndPhase) o;
      return day == that.day && phaseDef == that.phaseDef;
    }

    @Override
    public int hashCode() {
      return Objects.hash(day, phaseDef);
    }

    @Override
    public String toString() {
      return day + "-" + phaseDef;
    }
  }
}
