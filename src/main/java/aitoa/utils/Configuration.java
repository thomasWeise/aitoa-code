package aitoa.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The configuration map is an easy way to encapsulate, access,
 * and process command line parameters.
 */
public final class Configuration {

  /** the internal configuration map */
  private static final HashMap<String, Object> CONFIGURATION =
      new HashMap<>();

  /**
   * Put a value
   *
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public static final void putString(final String key,
      final String value) {
    final String k = Objects.requireNonNull(key);
    final String v = Objects.requireNonNull(value);
    synchronized (Configuration.CONFIGURATION) {
      Configuration.CONFIGURATION.put(k, v);
    }
  }

  /**
   * Put a value
   *
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public static final void putBoolean(final String key,
      final boolean value) {
    final String k = Objects.requireNonNull(key);
    final Boolean b = Boolean.valueOf(value);
    synchronized (Configuration.CONFIGURATION) {
      Configuration.CONFIGURATION.put(k, b);
    }
  }

  /**
   * Put a value
   *
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public static final void putInteger(final String key,
      final Integer value) {
    final String k = Objects.requireNonNull(key);
    final Integer b = Objects.requireNonNull(value);
    synchronized (Configuration.CONFIGURATION) {
      Configuration.CONFIGURATION.put(k, b);
    }
  }

  /**
   * Put a value
   *
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public static final void putInteger(final String key,
      final int value) {
    Configuration.putInteger(key, Integer.valueOf(value));
  }

  /**
   * Put a string to the map. The string is considered to be in
   * the form {@code key=value} or {@code key:value} and may be
   * preceded by any number of {@code -} or {@code /}-es. If the
   * value part is missing {@code "true"} is used as value.
   *
   * @param s
   *          the string
   */
  public static final void putCommandLine(final String s) {
    String t;
    int i, j;
    final int len;
    char ch;
    boolean canUseSlash;

    if (s == null) {
      return;
    }

    t = s.trim();
    len = t.length();
    if (len <= 0) {
      return;
    }

    canUseSlash = (File.separatorChar != '/');

    for (i = 0; i < len; i++) {
      ch = t.charAt(i);
      if ((ch == '-') || (canUseSlash && (ch == '/'))
          || (ch <= 32)) {
        continue;
      }

      for (j = i + 1; j < len; j++) {
        ch = t.charAt(j);
        if ((ch == ':') || (ch == '=')) {
          Configuration.putString(t.substring(i, j),
              t.substring(j + 1).trim());
          return;
        }
      }

      Configuration.putBoolean(t.substring(i), true);

      return;
    }
  }

  /**
   * Load command line arguments into a map
   *
   * @param args
   *          the arguments
   */
  public static final void putCommandLine(final String... args) {
    if (args != null) {
      for (final String s : args) {
        Configuration.putCommandLine(s);
      }
    }
  }

  /**
   * Delete a key from the configuration
   *
   * @param key
   *          the key to delete
   */
  public static final void delete(final String key) {
    final String k = Objects.requireNonNull(key);
    synchronized (Configuration.CONFIGURATION) {
      Configuration.CONFIGURATION.remove(k);
    }
  }

  /**
   * Get a value from the configuration
   *
   * @param key
   *          the key
   * @return the value
   */
  public static final String getString(final String key) {
    final String k = Objects.requireNonNull(key);
    final Object res;
    synchronized (Configuration.CONFIGURATION) {
      res = Configuration.CONFIGURATION.get(k);
    }
    return ((res != null) ? res.toString() : null);
  }

  /**
   * Get a value from the configuration
   *
   * @param key
   *          the key
   * @return the value
   */
  public static final boolean getBoolean(final String key) {
    final String k = Objects.requireNonNull(key);
    final Object res;
    synchronized (Configuration.CONFIGURATION) {
      res = Configuration.CONFIGURATION.get(k);
      if (res instanceof Boolean) {
        return ((Boolean) res).booleanValue();
      }
      if (res == null) {
        Configuration.CONFIGURATION.put(key, Boolean.FALSE);
        return false;
      }
      if (res instanceof String) {
        final boolean resb = Boolean.parseBoolean((String) res);
        Configuration.CONFIGURATION.put(key,
            Boolean.valueOf(resb));
        return resb;
      }
    }
    throw new IllegalStateException("config key '"//$NON-NLS-1$
        + k + "' is not a boolean but a " + //$NON-NLS-1$
        res.getClass());
  }

  /**
   * Get a path value from the configuration
   *
   * @param key
   *          the key
   * @return the value
   */
  public static final Path getPath(final String key) {
    return Configuration.getPath(key, null);
  }

  /**
   * Get a path value from the configuration
   *
   * @param key
   *          the key
   * @param ifNotSet
   *          the supplier if no path was found
   * @return the value
   */
  public static final Path getPath(final String key,
      final Supplier<Path> ifNotSet) {
    final String k = Objects.requireNonNull(key);
    final Object res;

    synchronized (Configuration.CONFIGURATION) {
      res = Configuration.CONFIGURATION.get(k);
      if (res == null) {
        if (ifNotSet != null) {
          final Path p =
              IOUtils.canonicalizePath(ifNotSet.get());
          if (p != null) {
            Configuration.CONFIGURATION.put(k, p);
          }
          return p;
        }
        return null;
      }
      if (res instanceof Path) {
        return ((Path) res);
      }
      if (res instanceof String) {
        final Path p = IOUtils.canonicalizePath((String) res);
        Configuration.CONFIGURATION.put(k, p);
        return p;
      }
    }
    throw new IllegalStateException("config key '"//$NON-NLS-1$
        + key + "' is not a path but an instance of "//$NON-NLS-1$
        + res.getClass());
  }

  /**
   * Get a value from the configuration
   *
   * @param key
   *          the key
   * @return the value
   */
  public static final Integer getInteger(final String key) {
    final String k = Objects.requireNonNull(key);
    final Object res;
    synchronized (Configuration.CONFIGURATION) {
      res = Configuration.CONFIGURATION.get(k);
      if (res == null) {
        return null;
      }
      if (res instanceof Number) {
        if (res instanceof Integer) {
          return ((Integer) res);
        }
        final Number n = ((Number) res);
        final int v = n.intValue();
        if (n.doubleValue() == v) {
          final Integer i = Integer.valueOf(v);
          Configuration.CONFIGURATION.put(k, i);
          return i;
        }
      } else {
        if (res instanceof String) {
          final Integer p = Integer.valueOf((String) res);
          Configuration.CONFIGURATION.put(k, p);
          return p;
        }
      }
    }
    throw new IllegalStateException("config key '"//$NON-NLS-1$
        + key
        + "' is not an integer but an incompatible instance of "//$NON-NLS-1$
        + res.getClass());
  }

  /**
   * Get a value from the configuration
   *
   * @param key
   *          the key
   * @return the value
   */
  public static final Double getDouble(final String key) {
    final String k = Objects.requireNonNull(key);
    final Object res;
    synchronized (Configuration.CONFIGURATION) {
      res = Configuration.CONFIGURATION.get(k);
      if (res == null) {
        return null;
      }
      if (res instanceof Number) {
        if (res instanceof Double) {
          return ((Double) res);
        }
        final Number n = ((Number) res);
        final double v = n.doubleValue();
        final Double i = Double.valueOf(v);
        Configuration.CONFIGURATION.put(k, i);
        return i;
      }
      if (res instanceof String) {
        final Double p = Double.valueOf((String) res);
        Configuration.CONFIGURATION.put(k, p);
        return p;
      }
    }
    throw new IllegalStateException("config key '"//$NON-NLS-1$
        + key
        + "' is not a double but an incompatible instance of "//$NON-NLS-1$
        + res.getClass());
  }

  /**
   * Put a path value from the configuration
   *
   * @param key
   *          the key
   * @param value
   *          the path
   */
  public static final void putPath(final String key,
      final Path value) {
    final String k = Objects.requireNonNull(key);
    final Path p = value.normalize();
    synchronized (Configuration.CONFIGURATION) {
      Configuration.CONFIGURATION.put(k, p);
    }
  }

  /** Print the whole configuration to stdout */
  public static final void print() {
    ConsoleIO.stdout((stdout) -> {
      stdout.println("The current full configuration is:"); //$NON-NLS-1$
      synchronized (Configuration.CONFIGURATION) {
        for (final Entry<String,
            Object> e : Configuration.CONFIGURATION.entrySet()) {
          stdout.print('\t');
          stdout.print(e.getKey());
          stdout.print("\t->\t"); //$NON-NLS-1$
          stdout.println(e.getValue());
        }
      }
    });
  }
}
