package aitoa.structure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
  static char[] _getSystemData() {
    return __Holder.SYSTEM_DATA;
  }

  /** the internal holder class */
  private static final class __Holder {

    /** make the system data */
    static final char[] SYSTEM_DATA =
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
    private static void __add(final TreeMap<String, String> map,
        final String key, final String rawvalue) {
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
    private static void __addgt(
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
    private static void __addgt0(
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
    private static void __add(final TreeMap<String, String> map,
        final String key, final Supplier<String> value) {
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
    private static char[] __makeSystemData() {
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
          LogFormat.SYSTEM_INFO_SESSION_START_DATE_TIME,
          _BlackBoxProcessData._getSessionStart().toString());

      final String osInfo[] = new String[2];

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
                () -> {
                  final String s = os.getFamily();
                  osInfo[0] = s;
                  return s;
                });
            __Holder.__addgt0(data,
                LogFormat.SYSTEM_INFO_OS_BITS,
                () -> os.getBitness());
            __Holder.__add(data,
                LogFormat.SYSTEM_INFO_OS_MANUFACTURER, () -> {
                  final String s = os.getManufacturer();
                  osInfo[1] = s;
                  return s;
                });
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

      try (
          final InputStream is = _SystemData.class
              .getResourceAsStream("versions.txt"); //$NON-NLS-1$
          final InputStreamReader isr =
              new InputStreamReader(is);
          final BufferedReader br = new BufferedReader(isr)) {

        String line = null;
        while ((line = br.readLine()) != null) {
          line = line.trim();
          if (line.isEmpty()) {
            continue;
          }
          final int i = line.indexOf('=');
          if ((i < 1) || (i >= (line.length() - 1))) {
            continue;
          }
          final String a = line.substring(0, i).trim();
          if (a.isEmpty()) {
            continue;
          }
          final String b = line.substring(i + 1).trim();
          if (b.isEmpty()) {
            continue;
          }
          __Holder.__add(data, a, b);
        }

      } catch (@SuppressWarnings("unused") final Throwable ignore) {
        // ignore
      }

      try { // detect GPU
        final String os =
            (((osInfo[0] != null) ? osInfo[0] : "") + //$NON-NLS-1$
                ((osInfo[1] != null) ? osInfo[1] : ""))//$NON-NLS-1$
                    .toLowerCase();
        boolean detected = false;
        if (os.isEmpty() || //
            os.contains("linux") || //$NON-NLS-1$
            os.contains("unbuntu")) {//$NON-NLS-1$
          detected = __Holder.__tryDetectGPULinux(data);
        }
        if ((!detected) && (os.isEmpty() || //
            os.contains("win"))) {//$NON-NLS-1$
          detected = __Holder.__tryDetectGPUWindows(data);
        }

      } catch (@SuppressWarnings("unused") final Throwable ignore) {
        // ignore
      } // end detect GPU

      for (final Map.Entry<String, String> entry : data
          .entrySet()) {
        out.append(LogFormat.mapEntry(entry.getKey(),
            entry.getValue()));
        out.append(System.lineSeparator());
      }

      out.append(LogFormat.asComment(LogFormat.END_SYSTEM));
      out.append(System.lineSeparator());

      final int length = out.length();
      final char[] res = new char[length];
      out.getChars(0, length, res, 0);

      return res;
    }

    /**
     * split a pci string into name, vendor id, and device id
     *
     * @param s
     *          the string
     * @return the split string
     */
    private static String[] __pciSplit(final String s) {
      int firstDots = s.indexOf(':');

      if (firstDots > 0) {
        firstDots = s.indexOf(':', firstDots + 1);
        if (firstDots > 0) {
          final int firstBracket = s.lastIndexOf('[');
          if (firstBracket > firstDots) {
            final int secondDots =
                s.indexOf(':', firstBracket + 1);
            if (secondDots > firstBracket) {
              final int lastBracket =
                  s.indexOf(']', secondDots + 1);
              if (lastBracket > secondDots) {
                final String name =
                    s.substring(firstDots + 1, firstBracket)
                        .trim();
                if (!name.isEmpty()) {
                  final String vendorId =
                      s.substring(firstBracket + 1, secondDots)
                          .trim();
                  try {
                    Integer.parseUnsignedInt(vendorId, 16);
                  } catch (@SuppressWarnings("unused") final Throwable error) {
                    return new String[] { name };
                  }

                  if (!vendorId.isEmpty()) {
                    final String pciId =
                        s.substring(secondDots + 1, lastBracket)
                            .trim();
                    if (!pciId.isEmpty()) {
                      try {
                        Integer.parseUnsignedInt(pciId, 16);
                      } catch (@SuppressWarnings("unused") final Throwable error) {
                        return new String[] { name };
                      }

// register name, vendor id, and pci ID
                      return new String[] { name, vendorId,
                          pciId };
                    }
                  }
                }
              }
            }
          } else {
            // no vendor/pci ID
            final String t = s.substring(firstDots + 1).trim();
            if (!t.isEmpty()) {
              return new String[] { t };
            }
          }
        }
      }

      return null;
    }

    /**
     * Look up one device or vendor in the PCI device repository.
     *
     * @param id
     *          the device or vendor id
     * @return the string
     */
    private static String __pciLookup(final String id) {
      for (final String base : new String[] {
          "http://pci-ids.ucw.cz/read/PC/", //$NON-NLS-1$
          "https://pci-ids.ucw.cz/read/PC/" //$NON-NLS-1$
      }) {
        try {
          try (
              final InputStream is =
                  new URL(base + id).openStream();
              final InputStreamReader isr =
                  new InputStreamReader(is);
              final BufferedReader br =
                  new BufferedReader(isr)) {
            String line = null;
            while ((line = br.readLine()) != null) {
              line = line.trim();
              if (line.isEmpty()) {
                continue;
              }
              final String linelc = line.toLowerCase();
              final int index = linelc.indexOf("<p>name:");//$NON-NLS-1$
              if (index >= 0) {
                final int end = linelc.indexOf("</", //$NON-NLS-1$
                    index + 8);
                if (end > index) {
                  final String res =
                      line.substring(index + 8, end).trim();
                  if (!res.isEmpty()) {
                    return res;
                  }
                }
              }
            }
          }
          return null;
        } catch (@SuppressWarnings("unused") final Throwable error) {
          // ignore
        }
      }

      return null;
    }

    /**
     * Add a GPU information
     *
     * @param vendor
     *          the vendor
     * @param device
     *          the device
     * @param map
     *          the destination map
     */
    private static void __addGPU(final String vendor,
        final String device, final TreeMap<String, String> map) {

      String vendorId = null;
      String deviceId = null;
      if (vendor != null) {
        vendorId = Integer.toUnsignedString(//
            Integer.parseUnsignedInt(vendor, 16), 16)
            .toLowerCase();
        while (vendorId.length() < 4) {
          vendorId = '0' + vendorId;
        }
        __Holder.__add(map,
            LogFormat.SYSTEM_INFO_GPU_PCI_VENDOR_ID, vendorId);
      }

      if (device != null) {
        deviceId = Integer.toUnsignedString(//
            Integer.parseUnsignedInt(device, 16), 16)
            .toLowerCase();
        while (deviceId.length() < 4) {
          deviceId = '0' + deviceId;
        }
        __Holder.__add(map,
            LogFormat.SYSTEM_INFO_GPU_PCI_DEVICE_ID, deviceId);
      }

      if (vendorId != null) {
        final String vn = __Holder.__pciLookup(vendorId);
        if (vn != null) {
          __Holder.__add(map,
              LogFormat.SYSTEM_INFO_GPU_PCI_VENDOR, vn);
        }
        if (deviceId != null) {
          final String dc =
              __Holder.__pciLookup(vendorId + '/' + deviceId);
          if (dc != null) {
            __Holder.__add(map,
                LogFormat.SYSTEM_INFO_GPU_PCI_DEVICE, dc);
          }
        }
      }
    }

    /**
     * Try to detect the GPU under Linux
     *
     * @param map
     *          the destination map
     * @return {@code true} if a graphics card was detected,
     *         {@code false} otherwise
     */
    private static boolean __tryDetectGPULinux(//
        final TreeMap<String, String> map) {
      try {
        final Process p = new ProcessBuilder()//
            .command("lspci", //$NON-NLS-1$
                "-nn") //$NON-NLS-1$
            .redirectErrorStream(true)//
            .start();

        try {
          try {
            String[] vga = null;
            String[] displayController = null;

            try (final InputStream is = p.getInputStream();
                final InputStreamReader isr =
                    new InputStreamReader(is);
                final BufferedReader br =
                    new BufferedReader(isr)) {
              String s = null;

              while ((s = br.readLine()) != null) {
                s = s.trim();
                final String t = s.toLowerCase();
                if (t.contains("vga compatible controller")) { //$NON-NLS-1$
                  final String[] q = __Holder.__pciSplit(s);
                  if ((q != null) && ((vga == null)
                      || (vga.length < q.length))) {
                    vga = q;
                    if ((displayController != null)
                        && (displayController.length >= 3)
                        && (q.length >= 3)) {
                      break;
                    }
                  }
                } else {
                  if (t.contains("display controller")) { //$NON-NLS-1$
                    final String[] q = __Holder.__pciSplit(s);
                    if ((q != null)
                        && ((displayController == null)
                            || (displayController.length < q.length))) {
                      displayController = q;
                      if ((vga != null) && (vga.length >= 3)
                          && (q.length >= 3)) {
                        break;
                      }
                    }
                  }
                }
              }
            }

            if (displayController != null) {
              vga = displayController;
            }
            if (vga != null) {
              __Holder.__add(map, LogFormat.SYSTEM_INFO_GPU_NAME,
                  vga[0]);
              if (vga.length > 1) {
                __Holder.__addGPU(vga[1],
                    (vga.length > 2) ? vga[2] : null, map);
              }
              return true;
            }

          } finally {
            p.waitFor();
          }
        } finally {
          p.destroy();
        }

      } catch (@SuppressWarnings("unused") final Throwable error) {
        // ignore
      }
      return false;
    }

    /**
     * Try to detect the GPU under Windows
     *
     * @param map
     *          the destination map
     * @return {@code true} if a graphics card was detected,
     *         {@code false} otherwise
     */
    private static boolean __tryDetectGPUWindows(
        final TreeMap<String, String> map) {
      try {
        final Path tempFile = Files.createTempFile("gd", //$NON-NLS-1$
            ".txt"); //$NON-NLS-1$
        try {
// use dxdiag to get the graphics card info
          final Process p = new ProcessBuilder()//
              .command("dxdiag", //$NON-NLS-1$
                  "/whql:off", //$NON-NLS-1$
                  "/t", //$NON-NLS-1$
                  tempFile.toFile().getCanonicalPath())
              .redirectErrorStream(true)//
              .start();

          try {
            p.waitFor();
            if (p.exitValue() != 0) {
              return false;
            }

// Wait until the text file has fully been written.
// When doing dxdiag via command line, I saw some slightly
// strange effects where it seemed as if the file was still
// written while the process had already been returned. Maybe
// there was some spawning of a sub-processes or something. So we
// better be on the safe side and wait until we can expect that
// the file won't change anymore.
            long lastSize = 0L;
            int waitNonIncreased = 5;
            for (int i = 300; (--i) >= 0;) {
              final long size = Files.size(tempFile);
              if (size > lastSize) {
                waitNonIncreased = 5;
                lastSize = size;
              } else {
                if (size > 0L) {
                  if ((--waitNonIncreased) <= 0) {
                    break;
                  }
                }
              }
              try {
                Thread.sleep(100L);
              } catch (@SuppressWarnings("unused") final Throwable ignore) {
                // ignore
              }
            }

            String name = null;
            String vendorId = null;
            String deviceId = null;

// OK, if we get here, the file has probably been written fully
            try (final BufferedReader br =
                Files.newBufferedReader(tempFile)) {
              String s = null;
              boolean inDashes = false;
              boolean inDisplay = false;

              readFile: while ((s = br.readLine()) != null) {
                s = s.trim();
                if (s.isEmpty()) {
                  continue readFile;
                }

                boolean isAllDashes = true;
                inner: for (int i = s.length(); (--i) >= 0;) {
                  if (s.charAt(i) != '-') {
                    isAllDashes = false;
                    break inner;
                  }
                }

                if (isAllDashes) {
                  inDashes = !inDashes;
                  continue readFile;
                }

                if (inDashes) {
                  inDisplay = s.toLowerCase().contains(//
                      "display devices"); //$NON-NLS-1$
                  continue readFile;
                }

                if (!inDisplay) {
                  continue readFile;
                }

                final int dots = s.indexOf(':');

                if (dots <= 0) {
                  continue readFile;
                }
                final String key =
                    s.substring(0, dots).trim().toLowerCase();

                if (key.isEmpty()) {
                  continue readFile;
                }

                if (key.contains("card name")) { //$NON-NLS-1$
                  if (name == null) {
                    name = s.substring(dots + 1).trim();
                    if (!name.isEmpty()) {
                      if ((vendorId != null)
                          && (deviceId != null)) {
                        break readFile;
                      }
                      continue readFile;
                    }
                    name = null;
                  }
                  continue readFile;
                }

                if (key.contains("vendor id")) {//$NON-NLS-1$
                  if (vendorId == null) {
                    vendorId = s.substring(dots + 1).trim()
                        .toLowerCase();
                    if ((vendorId.length() > 2)
                        && vendorId.startsWith("0x")) {//$NON-NLS-1$
                      vendorId = vendorId.substring(2);
                      try {
                        Integer.parseUnsignedInt(vendorId, 16);
                        if ((name != null)
                            && (deviceId != null)) {
                          break readFile;
                        }
                        continue readFile;
                      } catch (@SuppressWarnings("unused") final Throwable error2) {
                        // ignore
                      }
                    }
                    vendorId = null;
                  }
                  continue readFile;
                }

                if (key.contains("device id")) {//$NON-NLS-1$
                  if (deviceId == null) {
                    deviceId = s.substring(dots + 1).trim()
                        .toLowerCase();
                    if ((deviceId.length() > 2)
                        && deviceId.startsWith("0x")) {//$NON-NLS-1$
                      deviceId = deviceId.substring(2);
                      try {
                        Integer.parseUnsignedInt(deviceId, 16);
                        if ((name != null)
                            && (vendorId != null)) {
                          break readFile;
                        }
                        continue readFile;
                      } catch (@SuppressWarnings("unused") final Throwable error2) {
                        // ignore
                      }
                    }
                    deviceId = null;
                  }
                  continue readFile;
                }
              } // end read file
            } // close writer

            // write back the infos
            if (name != null) {
              __Holder.__add(map, LogFormat.SYSTEM_INFO_GPU_NAME,
                  name);
              __Holder.__addGPU(vendorId, deviceId, map);
              return true;
            }

          } finally {
            p.destroy();
          }
        } finally {
          Files.delete(tempFile);
        }
      } catch (@SuppressWarnings("unused") final Throwable error) {
        // ignore
      }
      return false;
    }
  }
}