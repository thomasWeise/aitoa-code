package aitoa.structure;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystem.OSVersionInfo;

/**
 * An internal class for creating and holding the system data.
 * The system data is queried only exactly once and then stored
 * for the rest of the session.
 */
final class _SystemData {

  /**
   * get the system data
   *
   * @return the system data
   */
  static final String _getSystemData() {
    return __Holder.SYSTEM_DATA;
  }

  /** the internal holder class */
  private static final class __Holder {

    /** make the system data */
    static final String SYSTEM_DATA =
        __Holder.__makeSystemData();

    /**
     * add a value to the map
     *
     * @param map
     *          the map
     * @param key
     *          the key
     * @param rawvalue
     *          the value
     */
    private static final void __add(
        final TreeMap<String, String> map, final String key,
        final String rawvalue) {
      if (rawvalue == null) {
        return;
      }
      final String value = rawvalue.trim();
      if (value.isEmpty()//
          || "null".equalsIgnoreCase(value) //$NON-NLS-1$
          || "unknown".equalsIgnoreCase(value)) {//$NON-NLS-1$
        return;
      }
      if (map.put(Objects.requireNonNull(key), value) != null) {
        throw new IllegalStateException("duplicate key: "//$NON-NLS-1$
            + key);
      }
    }

    /**
     * add a value to the map
     *
     * @param map
     *          the map
     * @param key
     *          the key
     * @param value
     *          the value
     * @param threshold
     *          the threshold
     */
    private static final void __addgt(
        final TreeMap<String, String> map, final String key,
        final LongSupplier value, final long threshold) {
      final long l;
      try {
        l = value.getAsLong();
      } catch (@SuppressWarnings("unused") final Throwable ignore) {
        return;
      }
      if (l > threshold) {
        __Holder.__add(map, key, Long.toString(l));
      }
    }

    /**
     * add a value to the map
     *
     * @param map
     *          the map
     * @param key
     *          the key
     * @param value
     *          the value
     */
    private static final void __addgt0(
        final TreeMap<String, String> map, final String key,
        final LongSupplier value) {
      __Holder.__addgt(map, key, value, 0L);
    }

    /**
     * add a value to the map
     *
     * @param map
     *          the map
     * @param key
     *          the key
     * @param value
     *          the value
     */
    private static final void __add(
        final TreeMap<String, String> map, final String key,
        final Supplier<String> value) {
      final String s;
      try {
        s = value.get();
      } catch (@SuppressWarnings("unused") final Throwable ignore) {
        return;
      }
      if (s != null) {
        __Holder.__add(map, key, s);
      }
    }

    /**
     * the internal system data maker
     *
     * @return the system data
     */
    private static final String __makeSystemData() {
      final StringBuilder out = new StringBuilder();

      // print the system information
      out.append(LogFormat.asComment(LogFormat.BEGIN_SYSTEM));
      out.append(System.lineSeparator());

      final TreeMap<String, String> data = new TreeMap<>();
      __Holder.__add(data, LogFormat.SYSTEM_INFO_JAVA_VERSION,
          System.getProperty("java.version"));//$NON-NLS-1$
      __Holder.__add(data, LogFormat.SYSTEM_INFO_JAVA_VENDOR,
          System.getProperty("java.vendor"));//$NON-NLS-1$
      __Holder.__add(data, LogFormat.SYSTEM_INFO_JAVA_VM_VERSION,
          System.getProperty("java.vm.version"));//$NON-NLS-1$
      __Holder.__add(data, LogFormat.SYSTEM_INFO_JAVA_VM_VENDOR,
          System.getProperty("java.vm.vendor"));//$NON-NLS-1$
      __Holder.__add(data, LogFormat.SYSTEM_INFO_JAVA_VM_NAME,
          System.getProperty("java.vm.name"));//$NON-NLS-1$
      __Holder.__add(data,
          LogFormat.SYSTEM_INFO_JAVA_SPECIFICATION_VERSION,
          System.getProperty("java.specification.version"));//$NON-NLS-1$
      __Holder.__add(data,
          LogFormat.SYSTEM_INFO_JAVA_SPECIFICATION_VENDOR,
          System.getProperty("java.specification.vendor"));//$NON-NLS-1$
      __Holder.__add(data,
          LogFormat.SYSTEM_INFO_JAVA_SPECIFICATION_NAME,
          System.getProperty("java.specification.name"));//$NON-NLS-1$
      __Holder.__add(data, LogFormat.SYSTEM_INFO_JAVA_COMPILER,
          System.getProperty("java.compiler"));//$NON-NLS-1$

      __Holder.__add(data,
          LogFormat.SYSTEM_INFO_COMPLETION_DATE_TIME,
          Instant.now().toString());

      try {
        final SystemInfo sys = new SystemInfo();

        try {
          final HardwareAbstractionLayer hal = sys.getHardware();
          if (hal != null) {
            try {
              final CentralProcessor cpu = hal.getProcessor();
              __Holder.__addgt0(data,
                  LogFormat.SYSTEM_INFO_CPU_LOGICAL_CORES,
                  () -> cpu.getLogicalProcessorCount());
              __Holder.__addgt0(data,
                  LogFormat.SYSTEM_INFO_CPU_PHYSICAL_CORES,
                  () -> cpu.getPhysicalProcessorCount());
              __Holder.__addgt0(data,
                  LogFormat.SYSTEM_INFO_CPU_PHYSICAL_SLOTS,
                  () -> cpu.getPhysicalPackageCount());

              try {
                final ProcessorIdentifier pi =
                    cpu.getProcessorIdentifier();
                if (pi != null) {
                  __Holder.__add(data,
                      LogFormat.SYSTEM_INFO_CPU_NAME,
                      () -> pi.getName());
                  __Holder.__add(data,
                      LogFormat.SYSTEM_INFO_CPU_FAMILY,
                      () -> pi.getFamily());
                  __Holder.__add(data,
                      LogFormat.SYSTEM_INFO_CPU_IDENTIFIER,
                      () -> pi.getIdentifier());
                  __Holder.__add(data,
                      LogFormat.SYSTEM_INFO_CPU_MODEL,
                      () -> pi.getModel());
                  __Holder.__add(data,
                      LogFormat.SYSTEM_INFO_CPU_ID,
                      () -> pi.getProcessorID());
                  __Holder.__add(data,
                      LogFormat.SYSTEM_INFO_CPU_VENDOR,
                      () -> pi.getVendor());
                  __Holder.__addgt0(data,
                      LogFormat.SYSTEM_INFO_CPU_FREQUENCY,
                      () -> pi.getVendorFreq());
                  __Holder.__add(data,
                      LogFormat.SYSTEM_INFO_CPU_IS_64_BIT,
                      () -> Boolean.toString(pi.isCpu64bit()));
                }
              } catch (@SuppressWarnings("unused") final Throwable ignore) {
                // ignore
              } // end cpu info
            } catch (@SuppressWarnings("unused") final Throwable ignore) {
              // ignore
            } // end cpu

            try {
              final GlobalMemory mem = hal.getMemory();
              if (mem != null) {
                __Holder.__addgt(data,
                    LogFormat.SYSTEM_INFO_MEM_PAGE_SIZE,
                    () -> mem.getPageSize(), 1L);
                __Holder.__addgt0(data,
                    LogFormat.SYSTEM_INFO_MEM_TOTAL,
                    () -> mem.getTotal());
              }
            } catch (@SuppressWarnings("unused") final Throwable ignore) {
              // ignore
            } // end memory

            try {
              final ComputerSystem cs = hal.getComputerSystem();
              if (cs != null) {
                __Holder.__add(data,
                    LogFormat.SYSTEM_INFO_COMPUTER_MANUFACTURER,
                    () -> cs.getManufacturer());
                __Holder.__add(data,
                    LogFormat.SYSTEM_INFO_COMPUTER_MODEL,
                    () -> cs.getModel());

                try {
                  final Baseboard bb = cs.getBaseboard();
                  if (bb != null) {
                    __Holder.__add(data,
                        LogFormat.SYSTEM_INFO_MAINBOARD_MANUFACTURER,
                        () -> bb.getManufacturer());
                    __Holder.__add(data,
                        LogFormat.SYSTEM_INFO_MAINBOARD_MODEL,
                        () -> bb.getModel());
                    __Holder.__add(data,
                        LogFormat.SYSTEM_INFO_MAINBOARD_SERIAL_NUMBER,
                        () -> bb.getSerialNumber());
                    __Holder.__add(data,
                        LogFormat.SYSTEM_INFO_MAINBOARD_VERSION,
                        () -> bb.getVersion());
                  }
                } catch (@SuppressWarnings("unused") final Throwable ignore) {
                  // ignore
                } // base board
              }
            } catch (@SuppressWarnings("unused") final Throwable ignore) {
              // ignore
            } // end computer system
          }
        } catch (@SuppressWarnings("unused") final Throwable ignore) {
          // ignore
        } // end hal

        try {
          final OperatingSystem os = sys.getOperatingSystem();
          if (os != null) {
            __Holder.__add(data, LogFormat.SYSTEM_INFO_OS_FAMILY,
                () -> os.getFamily());
            __Holder.__addgt0(data,
                LogFormat.SYSTEM_INFO_OS_BITS,
                () -> os.getBitness());
            __Holder.__add(data,
                LogFormat.SYSTEM_INFO_OS_MANUFACTURER,
                () -> os.getManufacturer());
            __Holder.__addgt(data,
                LogFormat.SYSTEM_INFO_PROCESS_ID,
                () -> os.getProcessId(), Long.MIN_VALUE);

            try {
              final OSProcess po =
                  os.getProcess(os.getProcessId());
              if (po != null) {
                final String cmd = po.getCommandLine();
                if (cmd != null) {
                  final String usecmd =
                      cmd.replaceAll("\\p{Cntrl}", " ") //$NON-NLS-1$ //$NON-NLS-2$
                          .replaceAll("[^\\p{Print}]", " ")//$NON-NLS-1$ //$NON-NLS-2$
                          .replaceAll("\\p{C}", " ")//$NON-NLS-1$ //$NON-NLS-2$
                          .replaceAll("[\\p{C}\\p{Z}]", " ") //$NON-NLS-1$ //$NON-NLS-2$
                          .trim();
                  __Holder.__add(data,
                      LogFormat.SYSTEM_INFO_PROCESS_COMMAND_LINE,
                      usecmd);
                }
              }
            } catch (@SuppressWarnings("unused") final Throwable ignore) {
              // ignore
            } // end os version

            try {
              final OSVersionInfo ovi = os.getVersionInfo();
              if (ovi != null) {
                __Holder.__add(data,
                    LogFormat.SYSTEM_INFO_OS_BUILD,
                    () -> ovi.getBuildNumber());
                __Holder.__add(data,
                    LogFormat.SYSTEM_INFO_OS_CODENAME,
                    () -> ovi.getCodeName());
                __Holder.__add(data,
                    LogFormat.SYSTEM_INFO_OS_VERSION,
                    () -> ovi.getVersion());
              }
            } catch (@SuppressWarnings("unused") final Throwable ignore) {
              // ignore
            } // end os version

            try {
              final NetworkParams net = os.getNetworkParams();
              if (net != null) {
                __Holder.__add(data,
                    LogFormat.SYSTEM_INFO_NET_DOMAIN_NAME,
                    () -> net.getDomainName());
                __Holder.__add(data,
                    LogFormat.SYSTEM_INFO_NET_HOST_NAME,
                    () -> net.getHostName());
              }
            } catch (@SuppressWarnings("unused") final Throwable ignore) {
              // ignore
            } // end net version
          }
        } catch (@SuppressWarnings("unused") final Throwable ignore) {
          // ignore
        } // end os
      } catch (@SuppressWarnings("unused") final Throwable ignore) {
        // ignore
      } // end system

      for (final Map.Entry<String, String> entry : data
          .entrySet()) {
        out.append(LogFormat.mapEntry(entry.getKey(),
            entry.getValue()));
        out.append(System.lineSeparator());
      }

      out.append(LogFormat.asComment(LogFormat.END_SYSTEM));
      out.append(System.lineSeparator());

      return out.toString();
    }
  }
}