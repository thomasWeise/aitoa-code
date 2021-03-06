package aitoa.structure;

import java.math.BigDecimal;
import java.math.BigInteger;

import aitoa.utils.ReflectionUtils;

/** The class with constants for logging information */
public final class LogFormat {

  /** the character used for separating items in a CSV format */
  public static final char CSV_SEPARATOR_CHAR = ';';

  /** the comment char */
  public static final char COMMENT_CHAR = '#';

  /** the map separator char */
  public static final char MAP_SEPARATOR_CHAR = ':';

  /** the suffix to be used for all log and data files */
  public static final String FILE_SUFFIX = ".txt";//$NON-NLS-1$

  /** the log begin */
  public static final String BEGIN_LOG = "BEGIN_LOG"; //$NON-NLS-1$

  /** the log end */
  public static final String END_OF_LOG = "END_OF_LOG"; //$NON-NLS-1$

  /** the state begin */
  public static final String BEGIN_STATE = "BEGIN_STATE"; //$NON-NLS-1$

  /** the state end */
  public static final String END_STATE = "END_STATE"; //$NON-NLS-1$

  /** the budget of fes */
  public static final String MAX_FES = "MAX_FES"; //$NON-NLS-1$

  /** the budget of time */
  public static final String MAX_TIME = "MAX_TIME"; //$NON-NLS-1$

  /** the goal objective value */
  public static final String GOAL_F = "GOAL_F"; //$NON-NLS-1$

  /** the random seed */
  public static final String RANDOM_SEED = "RANDOM_SEED"; //$NON-NLS-1$
  /** the random seed prefix */
  public static final String RANDOM_SEED_PREFIX = "0x"; //$NON-NLS-1$

  /** the consumed fes */
  public static final String CONSUMED_FES = "CONSUMED_FES"; //$NON-NLS-1$

  /** the last improvement fes */
  public static final String LAST_IMPROVEMENT_FE =
      "LAST_IMPROVEMENT_FE"; //$NON-NLS-1$

  /** the consumed time */
  public static final String CONSUMED_TIME = "CONSUMED_TIME"; //$NON-NLS-1$

  /** the last improvement time */
  public static final String LAST_IMPROVEMENT_TIME =
      "LAST_IMPROVEMENT_TIME"; //$NON-NLS-1$

  /** the best f */
  public static final String BEST_F = "BEST_F"; //$NON-NLS-1$

  /** the key BEGIN_SETUP */
  public static final String BEGIN_SETUP = "BEGIN_SETUP"; //$NON-NLS-1$
  /** the key SEARCH_SPACE */
  public static final String SEARCH_SPACE = "SEARCH_SPACE"; //$NON-NLS-1$
  /** the key SOLUTION_SPACE */
  public static final String SOLUTION_SPACE = "SOLUTION_SPACE"; //$NON-NLS-1$
  /** the key REPRESENTATION_MAPPING */
  public static final String REPRESENTATION_MAPPING =
      "REPRESENTATION_MAPPING"; //$NON-NLS-1$
  /** the key OBJECTIVE_FUNCTION */
  public static final String OBJECTIVE_FUNCTION =
      "OBJECTIVE_FUNCTION"; //$NON-NLS-1$
  /** the key END_SETUP */
  public static final String END_SETUP = "END_SETUP"; //$NON-NLS-1$
  /** the key BEGIN_SYSTEM */
  public static final String BEGIN_SYSTEM = "BEGIN_SYSTEM"; //$NON-NLS-1$
  /** the key JAVA_VERSION */
  public static final String SYSTEM_INFO_JAVA_VERSION =
      "JAVA_VERSION"; //$NON-NLS-1$
  /** the key JAVA_VENDOR */
  public static final String SYSTEM_INFO_JAVA_VENDOR =
      "JAVA_VENDOR"; //$NON-NLS-1$
  /** the key JAVA_VM_VERSION */
  public static final String SYSTEM_INFO_JAVA_VM_VERSION =
      "JAVA_VM_VERSION"; //$NON-NLS-1$
  /** the key JAVA_VM_VENDOR */
  public static final String SYSTEM_INFO_JAVA_VM_VENDOR =
      "JAVA_VM_VENDOR"; //$NON-NLS-1$
  /** the key JAVA_VM_NAME */
  public static final String SYSTEM_INFO_JAVA_VM_NAME =
      "JAVA_VM_NAME"; //$NON-NLS-1$
  /** the key SJAVA_SPECIFICATION_VERSION */
  public static final String SYSTEM_INFO_JAVA_SPECIFICATION_VERSION =
      "JAVA_SPECIFICATION_VERSION"; //$NON-NLS-1$
  /** the key JAVA_SPECIFICATION_VENDOR */
  public static final String SYSTEM_INFO_JAVA_SPECIFICATION_VENDOR =
      "JAVA_SPECIFICATION_VENDOR"; //$NON-NLS-1$
  /** the key JAVA_SPECIFICATION_NAME */
  public static final String SYSTEM_INFO_JAVA_SPECIFICATION_NAME =
      "JAVA_SPECIFICATION_NAME"; //$NON-NLS-1$
  /** the key JAVA_COMPILER */
  public static final String SYSTEM_INFO_JAVA_COMPILER =
      "JAVA_COMPILER"; //$NON-NLS-1$

  /** the time at which the session has started */
  public static final String SYSTEM_INFO_SESSION_START_DATE_TIME =
      "SESSION_START_DATE_TIME"; //$NON-NLS-1$

  /** the CPU name */
  public static final String SYSTEM_INFO_CPU_NAME = "CPU_NAME"; //$NON-NLS-1$
  /** the CPU family name */
  public static final String SYSTEM_INFO_CPU_FAMILY =
      "CPU_FAMILY"; //$NON-NLS-1$
  /** the CPU identifier name */
  public static final String SYSTEM_INFO_CPU_IDENTIFIER =
      "CPU_IDENTIFIER"; //$NON-NLS-1$
  /** the CPU model */
  public static final String SYSTEM_INFO_CPU_MODEL = "CPU_MODEL"; //$NON-NLS-1$
  /** the CPU ID */
  public static final String SYSTEM_INFO_CPU_ID = "CPU_ID"; //$NON-NLS-1$
  /** the CPU vendor */
  public static final String SYSTEM_INFO_CPU_VENDOR =
      "CPU_VENDOR"; //$NON-NLS-1$
  /** is this a 64 bit CPU? */
  public static final String SYSTEM_INFO_CPU_IS_64_BIT =
      "CPU_IS_64_BIT"; //$NON-NLS-1$
  /** the CPU frequency in hz */
  public static final String SYSTEM_INFO_CPU_FREQUENCY =
      "CPU_FREQUENCY_HZ"; //$NON-NLS-1$
  /** the number of physical cores */
  public static final String SYSTEM_INFO_CPU_PHYSICAL_CORES =
      "CPU_PHYSICAL_CORES"; //$NON-NLS-1$
  /** the number of logical cores */
  public static final String SYSTEM_INFO_CPU_LOGICAL_CORES =
      "CPU_LOGICAL_CORES"; //$NON-NLS-1$
  /** the physical slots into which CPUs can be packaged */
  public static final String SYSTEM_INFO_CPU_PHYSICAL_SLOTS =
      "CPU_PHYSICAL_SLOTS"; //$NON-NLS-1$
  /** the total size of the memory in bytes */
  public static final String SYSTEM_INFO_MEM_TOTAL =
      "MEMORY_TOTAL_BYTES"; //$NON-NLS-1$
  /** the page size of the memory in bytes */
  public static final String SYSTEM_INFO_MEM_PAGE_SIZE =
      "MEMORY_PAGE_SIZE"; //$NON-NLS-1$
  /** the computer model */
  public static final String SYSTEM_INFO_COMPUTER_MODEL =
      "COMPUTER_MODEL"; //$NON-NLS-1$
  /** the computer manufacturer */
  public static final String SYSTEM_INFO_COMPUTER_MANUFACTURER =
      "COMPUTER_MANUFACTURER"; //$NON-NLS-1$
  /** the mainboard manufacturer */
  public static final String SYSTEM_INFO_MAINBOARD_MANUFACTURER =
      "MAINBOARD_MANUFACTURER"; //$NON-NLS-1$
  /** the mainboard model */
  public static final String SYSTEM_INFO_MAINBOARD_MODEL =
      "MAINBOARD_MODEL"; //$NON-NLS-1$
  /** the mainboard version */
  public static final String SYSTEM_INFO_MAINBOARD_VERSION =
      "MAINBOARD_VERSION"; //$NON-NLS-1$
  /** the mainboard serial number */
  public static final String SYSTEM_INFO_MAINBOARD_SERIAL_NUMBER =
      "MAINBOARD_SERIAL_NUMBER"; //$NON-NLS-1$
  /** the os family */
  public static final String SYSTEM_INFO_OS_FAMILY = "OS_FAMILY"; //$NON-NLS-1$
  /** the os bits */
  public static final String SYSTEM_INFO_OS_BITS = "OS_BITS"; //$NON-NLS-1$
  /** the os manufacturer */
  public static final String SYSTEM_INFO_OS_MANUFACTURER =
      "OS_MANUFACTURER"; //$NON-NLS-1$
  /** the os version */
  public static final String SYSTEM_INFO_OS_VERSION =
      "OS_VERSION"; //$NON-NLS-1$
  /** the os build */
  public static final String SYSTEM_INFO_OS_BUILD = "OS_BUILD"; //$NON-NLS-1$
  /** the os code name */
  public static final String SYSTEM_INFO_OS_CODENAME =
      "OS_CODENAME"; //$NON-NLS-1$
  /** the domain name */
  public static final String SYSTEM_INFO_NET_DOMAIN_NAME =
      "NET_DOMAIN_NAME"; //$NON-NLS-1$
  /** the host name */
  public static final String SYSTEM_INFO_NET_HOST_NAME =
      "NET_HOST_NAME"; //$NON-NLS-1$
  /** the current process id */
  public static final String SYSTEM_INFO_PROCESS_ID =
      "PROCESS_ID"; //$NON-NLS-1$
  /** the command line of the current process */
  public static final String SYSTEM_INFO_PROCESS_COMMAND_LINE =
      "PROCESS_COMMAND_LINE"; //$NON-NLS-1$
  /** the GPU name */
  public static final String SYSTEM_INFO_GPU_NAME = "GPU_NAME"; //$NON-NLS-1$
  /** the GPU vendor id */
  public static final String SYSTEM_INFO_GPU_PCI_VENDOR_ID =
      "GPU_PCI_VENDOR_ID"; //$NON-NLS-1$
  /** the GPU vendor */
  public static final String SYSTEM_INFO_GPU_PCI_VENDOR =
      "GPU_PCI_VENDOR"; //$NON-NLS-1$
  /** the GPU PCI id */
  public static final String SYSTEM_INFO_GPU_PCI_DEVICE_ID =
      "GPU_PCI_DEVICE_ID"; //$NON-NLS-1$
  /** the GPU PCI device */
  public static final String SYSTEM_INFO_GPU_PCI_DEVICE =
      "GPU_PCI_DEVICE"; //$NON-NLS-1$

  /** the key END_SYSTEM */
  public static final String END_SYSTEM = "END_SYSTEM"; //$NON-NLS-1$

  /**
   * a log section where you can dump in any algorithm setup
   * information
   */
  public static final String ALGORITHM_SETUP_LOG_SECTION =
      "ALGORITHM_SETUP";//$NON-NLS-1$
  /** the key algorithm ID key */
  public static final String SETUP_ALGORITHM = "algorithm"; //$NON-NLS-1$
  /** the key algorithm ID key */
  public static final String SETUP_BASE_ALGORITHM =
      "baseAlgorithm"; //$NON-NLS-1$
  /** the key NULLARY_OP */
  public static final String SETUP_NULLARY_OP =
      "nullaryOperator"; //$NON-NLS-1$
  /** the key UNARY_OP */
  public static final String SETUP_UNARY_OP = "unaryOperator"; //$NON-NLS-1$
  /** the key BINARY_OP */
  public static final String SETUP_BINARY_OP = "binaryOperator"; //$NON-NLS-1$
  /** the key TERNARY_OP */
  public static final String SETUP_TERNARY_OP =
      "ternaryOperator"; //$NON-NLS-1$

  /** the null value */
  public static final String NULL = "null"; //$NON-NLS-1$

  /** the class key extension */
  private static final String CLASS_KEY_EXT = "(class)";//$NON-NLS-1$

  /**
   * Join some strings to create a single log line and return it
   *
   * @param items
   *          the items
   * @return the log line
   */
  public static final String joinLogLine(final String... items) {
    final StringBuilder sb = new StringBuilder();

    boolean add = false;
    for (final String s : items) {
      if (add) {
        sb.append(LogFormat.CSV_SEPARATOR_CHAR);
      } else {
        add = true;
      }
      sb.append(s);
    }

    return sb.toString();
  }

  /**
   * create a commented string as character array
   *
   * @param str
   *          the string
   * @return the character array
   */
  public static String asComment(final String str) {
    return (LogFormat.COMMENT_CHAR + (' ' + str));
  }

  /**
   * Get the class version of a key
   *
   * @param key
   *          the key
   * @return the class version of a keyy
   */
  public static String classKey(final String key) {
    return key + LogFormat.CLASS_KEY_EXT;
  }

  /**
   * Create a map entry string
   *
   * @param key
   *          the key
   * @param value
   *          the value
   * @return the map entry string
   */
  public static String mapEntry(final String key,
      final Object value) {
    if (value == null) {
      return LogFormat.mapEntry(key, LogFormat.NULL);
    }
    if (value instanceof Number) {
      return LogFormat.mapEntry(key,
          LogFormat.numberToStringForLog((Number) value));
    }
    return LogFormat.mapEntry(key, value.toString())
        + System.lineSeparator()
        + LogFormat.mapEntry(LogFormat.classKey(key),
            ReflectionUtils.className(value));
  }

  /**
   * Create a map entry string
   *
   * @param key
   *          the key
   * @param value
   *          the value
   * @return the map entry string
   */
  public static String mapEntry(final String key,
      final String value) {
    return (((((Character.toString(LogFormat.COMMENT_CHAR) + ' ')
        + key) + LogFormat.MAP_SEPARATOR_CHAR) + ' ') + value);
  }

  /**
   * Create a map entry string
   *
   * @param key
   *          the key
   * @param value
   *          the value
   * @return the map entry string
   */
  public static String mapEntry(final String key,
      final int value) {
    return LogFormat.mapEntry(key, Integer.toString(value));
  }

  /**
   * Create a map entry string
   *
   * @param key
   *          the key
   * @param value
   *          the value
   * @return the map entry string
   */
  public static String mapEntry(final String key,
      final long value) {
    return LogFormat.mapEntry(key, Long.toString(value));
  }

  /**
   * Create a map entry string
   *
   * @param key
   *          the key
   * @param value
   *          the value
   * @return the map entry string
   */
  public static String mapEntry(final String key,
      final double value) {
    return LogFormat.mapEntry(key,
        LogFormat.doubleToStringForLog(value))
        + System.lineSeparator()
        + LogFormat.mapEntry(key + "(inhex)", //$NON-NLS-1$
            Double.toHexString(value));
  }

  /**
   * Create a map entry string
   *
   * @param key
   *          the key
   * @param value
   *          the value
   * @return the map entry string
   */
  public static String mapEntry(final String key,
      final boolean value) {
    return LogFormat.mapEntry(key, Boolean.toString(value));
  }

  /**
   * Convert a double to a string for the log
   *
   * @param d
   *          the double value
   * @return the string representation
   */
  public static String doubleToStringForLog(final double d) {
    if (Double.isFinite(d)) {
      if ((d >= Long.MIN_VALUE) && (d <= Long.MAX_VALUE)) {
        final long l = Math.round(d);
        if (d == l) {
          return Long.toString(l);
        }
      }
    }
    return Double.toString(d);
  }

  /**
   * Convert a number object to a string for use in a log
   *
   * @param number
   *          the number object
   * @return the string
   */
  public static String
      numberToStringForLog(final Number number) {
    if ((number instanceof Long) || //
        (number instanceof Integer) || //
        (number instanceof Byte) || //
        (number instanceof Short) || //
        (number instanceof BigInteger)) {
      return number.toString();
    }
    if ((number instanceof Float) || //
        (number instanceof Double)) {
      return LogFormat
          .doubleToStringForLog(number.doubleValue());
    }
    if (number instanceof BigDecimal) {
      try {
        return Long.toString(//
            ((BigDecimal) number).longValueExact());
      } catch (@SuppressWarnings("unused") //
      final ArithmeticException ignore) {
        //
      }
    }

    return number.toString();
  }

  /** forbidden */
  private LogFormat() {
    throw new UnsupportedOperationException();
  }
}
