package aitoa.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  public static void putString(final String key,
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
  public static void putBoolean(final String key,
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
  public static void putInteger(final String key,
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
  public static void putInteger(final String key,
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
  public static void putCommandLine(final String s) {
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
  public static void delete(final String key) {
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
  public static String getString(final String key) {
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
  public static boolean getBoolean(final String key) {
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
  public static Path getPath(final String key) {
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
  public static Path getPath(final String key,
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
   * Get an integer value from the configuration
   *
   * @param key
   *          the key
   * @return the value
   */
  public static Integer getInteger(final String key) {
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
   * Get a double value from the configuration
   *
   * @param key
   *          the key
   * @return the value
   */
  public static Double getDouble(final String key) {
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
   * parse an enum string
   *
   * @param <E>
   *          the enum type
   * @param s
   *          the string
   * @param clazz
   *          the enum class
   * @return the enum constant
   */
  private static <E extends Enum<E>> E
      __parseEnum(final String s, final Class<E> clazz) {
    try {
      return Enum.valueOf(clazz, s);
    } catch (final IllegalArgumentException eee) {

      try {
        return Enum.valueOf(clazz, s.toUpperCase());
      } catch (@SuppressWarnings("unused") final IllegalArgumentException ee) {
        try {
          return Enum.valueOf(clazz, s.toLowerCase());
        } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
          for (final E tt : clazz.getEnumConstants()) {
            if (tt.name().equalsIgnoreCase(s)) {
              return tt;
            }
            if (tt.toString().equalsIgnoreCase(s)) {
              return tt;
            }
          }
          throw eee;
        }
      }
    }
  }

  /**
   * Get an integer value from the configuration
   *
   * @param key
   *          the key
   * @param clazz
   *          the enum class
   * @return the enum value
   * @param <E>
   *          the enumeration type
   */
  public static <E extends Enum<E>> E getEnum(final String key,
      final Class<E> clazz) {
    final String k = Objects.requireNonNull(key);
    final Object res;
    synchronized (Configuration.CONFIGURATION) {
      res = Configuration.CONFIGURATION.get(k);
      if (res == null) {
        return null;
      }
      if (clazz.isInstance(res)) {
        return clazz.cast(res);
      }

      if (res instanceof String) {
        final E x = Configuration
            .__parseEnum(((String) res).trim(), clazz);
        Configuration.CONFIGURATION.put(k, x);
        return x;
      }
    }
    throw new IllegalStateException("config key '"//$NON-NLS-1$
        + key + "' is not an enum constance of type " //$NON-NLS-1$
        + ReflectionUtils.className(clazz)
        + ", but an incompatible instance of "//$NON-NLS-1$
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
  public static void putPath(final String key,
      final Path value) {
    final String k = Objects.requireNonNull(key);
    final Path p = IOUtils.canonicalizePath(value);
    synchronized (Configuration.CONFIGURATION) {
      Configuration.CONFIGURATION.put(k, p);
    }
  }

  /**
   * Get the path to the specified executable.
   *
   * @param name
   *          the executable name
   * @return the path
   */
  public static Path getExecutable(final String name) {
    String stdout = null;
    String stderr = null;
    Path path = null;
    Object res;

    synch: synchronized (Configuration.CONFIGURATION) {
      res = Configuration.CONFIGURATION.get(name);
      if (res != null) {
        if (res instanceof Path) {
          path = ((Path) res);
          if (Files.isExecutable(path)) {
            break synch;
          }
          throw new IllegalStateException("config key '"//$NON-NLS-1$
              + name + "' is path '" + path + //$NON-NLS-1$
              "', but it is not executable");//$NON-NLS-1$
        }
        if (res instanceof String) {
          path = IOUtils.canonicalizePath((String) res);
          if (Files.isExecutable(path)) {
            stdout = (name + " executable configured as "//$NON-NLS-1$
                + path);
            Configuration.CONFIGURATION.put(name, path);
            break synch;
          }
          path = null;
          stderr = ("Configured file '" + //$NON-NLS-1$
              res + "' is not executable.");//$NON-NLS-1$
        }
        throw new IllegalStateException("config key '"//$NON-NLS-1$
            + name
            + "' does not reference an executable path, but is an instance of"//$NON-NLS-1$
            + res.getClass());
      }

      for (final String dirname : System.getenv("PATH") //$NON-NLS-1$
          .split(File.pathSeparator)) {
        for (final String ext : new String[] { "", //$NON-NLS-1$
            ".exe" }) { //$NON-NLS-1$
          path = IOUtils.canonicalizePath(dirname, name + ext);
          if (Files.isExecutable(path)) {
            stdout = (name + " executable detected in PATH as " //$NON-NLS-1$
                + path);
            Configuration.CONFIGURATION.put(name, path);
            break synch;
          }
        }
      }
      path = null;

      try {
        final Process process =
            Runtime.getRuntime().exec("which " + name); //$NON-NLS-1$
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {
          path =
              IOUtils.canonicalizePath(Paths.get(in.readLine()));
          if (Files.isExecutable(path)) {
            stdout = (name + " executable found via which as " //$NON-NLS-1$
                + path);
            Configuration.CONFIGURATION.put(name, path);
            break synch;
          }
        }
      } catch (@SuppressWarnings("unused") final Throwable ignore) {
        // ignored
      }
      path = null;
    }

    if (stderr != null) {
      ConsoleIO.stderr(stderr, null);
    }
    if (stdout != null) {
      ConsoleIO.stdout(stdout);
    }
    if (path == null) {
      ConsoleIO.stdout("no " + name + //$NON-NLS-1$
          " executable detected."); //$NON-NLS-1$
    }
    return path;
  }

  /** Print the whole configuration to stdout */
  public static void print() {
    ConsoleIO.stdout(stdout -> {
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
