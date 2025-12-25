package name.mjm.aoc.calendar;

import name.mjm.aoc.Counter;
import name.mjm.aoc.ParentDay;
import name.mjm.aoc.TryResult;
import name.mjm.aoc.log.LimitedLogger;
import name.mjm.aoc.model.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Day11 extends ParentDay {

  private static final String OUT_ID = "out";
  private static final String YOU_ID = "you";

  private Map<String, Device> deviceMap;
  private Device out;

  private static void enrichDevicesByOutlets(List<Device> deviceList, Map<String, Device> deviceMap) {
    for (Device device : deviceList) {
      ArrayList<Device> devices = new ArrayList<>(device.outletIds.length);
      for (String outletId : device.outletIds) {
        Device outDev = deviceMap.get(outletId);
        if (outDev == null) {
          throw new IllegalArgumentException("No such device " + outletId);
        }
        devices.add(outDev);
      }
      device.outlets = devices;
    }
  }

  private void initFields(List<Device> deviceList) {
    this.out = new Device(OUT_ID, new String[0]);
    this.out.outlets = List.of();
    this.deviceMap = deviceList.stream().collect(Collectors.toMap(d -> d.id, d -> d));
    this.deviceMap.put(OUT_ID, out);
    enrichDevicesByOutlets(deviceList, deviceMap);
  }

  @TryResult("5")
  public long a(List<Device> deviceList) {
    initFields(deviceList);

    HashMap<String, Tracer> tracersMap = new HashMap<>();
    tracersMap.put(YOU_ID, new Tracer(deviceMap.get(YOU_ID)));
    LinkedList<Tracer> tracers = new LinkedList<>();
    tracers.add(tracersMap.get(YOU_ID));
    long counter = 0L;
    while (!tracers.isEmpty()) {
      Tracer tracer = tracers.removeFirst();
      for (Device outlet : tracer.head.outlets) {
        if (outlet.id.equals(OUT_ID)) {
          counter += tracer.incomeCount;
          continue;
        }
        // Chack, if it is in map
        Tracer findT = tracersMap.get(outlet.id);
        if (findT == null) {
          Tracer branch = tracer.branch(outlet);
          tracersMap.put(outlet.id, branch);
          tracers.add(branch);
        } else {
          findT.inc(tracer.incomeCount);
        }
      }
    }

    return counter;
  }

  @TryResult("2")
  public long b(List<Device> deviceList) {
    logger.debug("Count of devices: " + deviceList.size());
    initFields(deviceList);

    HashMap<Tracer2, Counter> tracersMap = new HashMap<>();
    LinkedList<Pair<Tracer2, Counter>> tracers = new LinkedList<>();
    Tracer2 t = new Tracer2(deviceMap.get("svr"), false, false);
    Pair<Tracer2, Counter> pairToProcess = new Pair<>(t, new Counter(1));
    tracersMap.put(pairToProcess.x(), pairToProcess.y());
    tracers.add(pairToProcess);
    long counter = 0L;
    while (!tracers.isEmpty()) {
      pairToProcess = tracers.removeFirst();
      tracersMap.remove(pairToProcess.x());
      Tracer2 tracerToProcess = pairToProcess.x();
      for (Device followingDevice : tracerToProcess.head.outlets) {
        // Process known situations
        if (followingDevice.id.equals(OUT_ID)) {
          if (tracerToProcess.dacVisited && tracerToProcess.fftVisited) {
            counter += pairToProcess.y().get();
            continue;
          }
        }

        // Branch to following
        Tracer2 nextTracer = new Tracer2(followingDevice,
                                      "dac".equals(followingDevice.id) ? true : tracerToProcess.dacVisited,
                                      "fft".equals(followingDevice.id) ? true : tracerToProcess.fftVisited);
        // Chack, if it is in map
        var nextCounter = tracersMap.get(nextTracer);
        if (nextCounter == null) {
          nextCounter = new Counter(pairToProcess.y().get());
          var nextPair = new Pair<>(nextTracer, nextCounter);
          tracersMap.put(nextTracer, nextCounter);
          tracers.add(nextPair);
        } else {
          nextCounter.inc(pairToProcess.y().get());
        }
      }
    }

    return counter;
  }

  record Tracer2(Device head, boolean dacVisited, boolean fftVisited) {}

  static class Tracer {
    Device head;
    HashSet<Device> meetDevices;
    int incomeCount = 1;

    private Tracer(Device head, Collection<Device> meetDevices, int incomeCount) {
      this.head = head;
      this.meetDevices = new HashSet<>(meetDevices);
      this.meetDevices.add(head);
      this.incomeCount = incomeCount;
    }

    private Tracer(Device head) {
      this(head, List.of(), 1);
    }

    Tracer branch(Device newHead) {
      return new Tracer(newHead, meetDevices, incomeCount);
    }

    void inc(int value) {
      this.incomeCount += value;
    }
  }

  public static class Device {
    final String id;
    final String[] outletIds;

    List<Device> outlets;
    Set<Device> inletsFromYouPaths = new HashSet<>();

    public Device(String id, String[] outletIds) {
      if (id == null) {
        throw new IllegalArgumentException("id is null");
      }
      this.id = id;
      this.outletIds = outletIds;
    }

    public Device(String line) {
      int index = line.indexOf(':');
      if (index == -1) {
        throw new IllegalArgumentException();
      }
      id = line.substring(0, index).trim();

      line = line.substring(index + 1).trim();
      outletIds = line.split(" ");
      for (int i = 0; i < outletIds.length; i++) {
        outletIds[i] = outletIds[i].trim();
      }
    }

    @Override
    public String toString() {
      return id + ": " + Arrays.toString(outletIds);
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Device device = (Device) o;
      return id.equals(device.id);
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }
  }
}
