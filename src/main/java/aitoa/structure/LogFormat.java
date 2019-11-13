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
  /** the key NULLARY_OP */
  public static final String NULLARY_OP = "NULLARY_OP"; //$NON-NLS-1$
  /** the key UNARY_OP */
  public static final String UNARY_OP = "UNARY_OP"; //$NON-NLS-1$
  /** the key BINARY_OP */
  public static final String BINARY_OP = "BINARY_OP"; //$NON-NLS-1$
  /** the key TERNARY_OP */
  public static final String TERNARY_OP = "TERNARY_OP"; //$NON-NLS-1$
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
  public static final String JAVA_VERSION = "JAVA_VERSION"; //$NON-NLS-1$
  /** the key JAVA_VENDOR */
  public static final String JAVA_VENDOR = "JAVA_VENDOR"; //$NON-NLS-1$
  /** the key JAVA_VM_VERSION */
  public static final String JAVA_VM_VERSION = "JAVA_VM_VERSION"; //$NON-NLS-1$
  /** the key JAVA_VM_VENDOR */
  public static final String JAVA_VM_VENDOR = "JAVA_VM_VENDOR"; //$NON-NLS-1$
  /** the key JAVA_VM_NAME */
  public static final String JAVA_VM_NAME = "JAVA_VM_NAME"; //$NON-NLS-1$
  /** the key JAVA_SPECIFICATION_VERSION */
  public static final String JAVA_SPECIFICATION_VERSION =
      "JAVA_SPECIFICATION_VERSION"; //$NON-NLS-1$
  /** the key JAVA_SPECIFICATION_VENDOR */
  public static final String JAVA_SPECIFICATION_VENDOR =
      "JAVA_SPECIFICATION_VENDOR"; //$NON-NLS-1$
  /** the key JAVA_SPECIFICATION_NAME */
  public static final String JAVA_SPECIFICATION_NAME =
      "JAVA_SPECIFICATION_NAME"; //$NON-NLS-1$
  /** the key JAVA_COMPILER */
  public static final String JAVA_COMPILER = "JAVA_COMPILER"; //$NON-NLS-1$
  /** the key END_SYSTEM */
  public static final String END_SYSTEM = "END_SYSTEM"; //$NON-NLS-1$
  /** the null value */
  public static final String NULL = "null"; //$NON-NLS-1$

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
  public static final String asComment(final String str) {
    return (LogFormat.COMMENT_CHAR + (' ' + str));
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
  public static final String mapEntry(final String key,
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
        + LogFormat.mapEntry(key + "(class)", //$NON-NLS-1$
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
  public static final String mapEntry(final String key,
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
  public static final String mapEntry(final String key,
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
  public static final String mapEntry(final String key,
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
  public static final String mapEntry(final String key,
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
  public static final String mapEntry(final String key,
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
  public static final String
      doubleToStringForLog(final double d) {
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
  public static final String
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
}
