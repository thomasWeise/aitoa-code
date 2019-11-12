package aitoa.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/** A class for processing and executing experiments */
public class Experiment {

  /**
   * Process a name part derived from an object's
   * {@link Object#toString()} method, meaning that all
   * non-acceptable characters are transformed or removed
   *
   * @param part
   *          the string to be processed
   * @return the name part as acceptable for file and directory
   *         names
   */
  public static final String
      nameFromObjectPrepare(final Object part) {
    return Experiment.nameStringPrepare(part.toString());
  }

  /**
   * Process a name part, meaning that all non-acceptable
   * characters are transformed or removed
   *
   * @param part
   *          the string to be processed
   * @return the name part as acceptable for file and directory
   *         names
   */
  public static final String
      nameStringPrepare(final String part) {
    final Object res = Experiment.__processNamePart(part);
    if (res == null) {
      return part;
    }
    if (res instanceof char[]) {
      return String.valueOf((char[]) res);
    }
    final Object[] k = ((Object[]) res);
    return String.valueOf(((char[]) (k[0])), 0,
        ((int[]) k[1])[0]);
  }

  /**
   * Process an array of name parts and merge them.
   *
   * @param parts
   *          the strings to be processed
   * @return the name part as acceptable for file and directory
   *         names
   */
  public static final String
      nameStringsMerge(final String... parts) {
    switch (parts.length) {
      case 0: {
        throw new IllegalArgumentException(
            "There must be at least one name part."); //$NON-NLS-1$
      }
      case 1: {
        return Experiment.nameStringPrepare(parts[0]);
      }
      default: {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String part : parts) {
          if (first) {
            first = false;
          } else {
            sb.append('_');
          }
          final Object res = Experiment.__processNamePart(part);
          if (res == null) {
            sb.append(part);
          } else {
            if (res instanceof char[]) {
              sb.append((char[]) res);
            } else {
              final Object[] k = ((Object[]) res);
              sb.append(((char[]) (k[0])), 0, ((int[]) k[1])[0]);
            }
          }
        }
        return (sb.toString());
      }
    }
  }

  /**
   * Process an array of objects and convert each non-null object
   * to a name part and then merge these parts.
   *
   * @param parts
   *          the string to be processed
   * @return the name part as acceptable for file and directory
   *         names
   */
  public static final String
      nameFromObjectsMerge(final Object... parts) {
    int length = 0;
    for (int i = parts.length; (--i) >= 0;) {
      if (parts[i] != null) {
        length++;
      }
    }
    if (length <= 0) {
      throw new IllegalArgumentException(
          "There must be at least one non-null name component."); //$NON-NLS-1$
    }
    final String[] names = new String[length];

    int i = 0;
    for (final Object part : parts) {
      if (part != null) {
        names[i++] = part.toString();
      }
    }

    return Experiment.nameStringsMerge(names);
  }

  /**
   * check if a character is a white space
   *
   * @param ch
   *          the character
   * @return {@code true} if it is, {@code false} if it isn't
   */
  private static final boolean __isWhiteSpace(final char ch) {
    return (ch <= 32) || (ch == '_')//
        || (ch == '\u00A0') || (ch == '\u2007')
        || (ch == '\u202F')//
        || Character.isWhitespace(ch) //
        || Character.isSpaceChar(ch);
  }

  /**
   * pseudo-delete a character from a character array
   *
   * @param data
   *          the data
   * @param index
   *          the index
   * @param lengthMinusOne
   *          the length - 1
   */
  private static final void __delete(final char[] data,
      final int index, final int lengthMinusOne) {
    if (index < lengthMinusOne) {
      System.arraycopy(data, index + 1, data, index,
          lengthMinusOne - index);
    }
  }

  /**
   * Append a given name part to the specified string builder
   *
   * @param s
   *          the name part
   * @return either null if the string can be used as-is, a
   *         full-length char array or a two-object array, where
   *         the first one is the character array and the second
   *         one an int array of length 1 with the array length
   *         of the first array
   */
  private static final Object __processNamePart(final String s) {
    final char[] chars = s.toCharArray();
    boolean unchanged = true;
    int length = chars.length;

    if (length <= 0) {
      throw new IllegalArgumentException(
          "name part cannot be empty."); //$NON-NLS-1$
    }

    trimRight: for (;;) { // trim right
      final int next = length - 1;
      if (Experiment.__isWhiteSpace(chars[next])) {
        if (next <= 0) {
          throw new IllegalArgumentException("name part '"//$NON-NLS-1$
              + s + "' only consists of white space!");//$NON-NLS-1$
        }
        length = next;
      } else {
        break trimRight;
      }
    }

    trimLeft: for (;;) { // trim left
      if (Experiment.__isWhiteSpace(chars[0])) {
        Experiment.__delete(chars, 0, --length);
      } else {
        break trimLeft;
      }
    }

    // cleanse
    boolean acceptSpace = false;
    looper: for (int i = 0; i < length; i++) {
      final char ch = chars[i];
      switch (ch) {
        case '!':
        case '"':
        case '#':
        case '$':
        case '%':
        case '&':
        case '\'':
        case '*':
        case '/':
        case ':':
        case ';':
        case '<':
        case '>':
        case '?':
        case '[':
        case '\\':
        case ']':
        case '^':
        case '`':
        case '{':
        case '|':
        case '}':
        case '‘': {
          chars[i] = '_';
          unchanged = false;
        } //$FALL-THROUGH$
        case '_': {
          if (!acceptSpace) {
            Experiment.__delete(chars, i, --length);
          }
          acceptSpace = false;
          continue looper;
        }
        case '.': {
          chars[i] = 'd';
          unchanged = false;
          acceptSpace = true;
          continue looper;
        }
        default: {
          if (Experiment.__isWhiteSpace(ch)) {
            if (acceptSpace) {
              chars[i] = '_';
              acceptSpace = false;
            } else {
              Experiment.__delete(chars, i, --length);
            }
            continue looper;
          }
          acceptSpace = true;
          continue looper;
        }
      }
    }

    trimRight: for (;;) { // trim right
      final int next = length - 1;
      if (Experiment.__isWhiteSpace(chars[next])) {
        if (next <= 0) {
          throw new IllegalArgumentException("name part '"//$NON-NLS-1$
              + s
              + "' only consists of characters that map to white space!");//$NON-NLS-1$
        }
        length = next;
      } else {
        break trimRight;
      }
    }

    // return
    if (length >= chars.length) {
      if (unchanged) {
        return null;
      }
      return chars;
    }
    return new Object[] { chars, new int[] { length } };
  }

  /**
   * Get the path to a suitable log file for the given
   * experimental run if that log file does not yet exist. This
   * method allows for both parallel execution and for restarting
   * of experiments.
   * <p>
   * The idea is that we allow for running several instances of
   * the JVM in parallel, each executing the same kind of
   * experiment. Before a new run is started, we create the
   * corresponding log file. File creation is an
   * {@linkplain java.nio.file.Files#createFile(Path, java.nio.file.attribute.FileAttribute...)
   * atomic} operation, meaning that it is impossible that two
   * threads/processes can successfully create the same file (for
   * one of them, it will always already exist). Thus, if we fail
   * to create the log file for a run anew, then this run is
   * already ongoing. We will then skip it. Via this mechanism,
   * we can very easily execute several experiments in parallel.
   * <p>
   * If an experiment was aborted, say, due to a crash of a
   * machine or power outage, then we can use the same mechanism
   * to resume experiments. We simply have to delete all
   * zero-sized files and then start the experiments. Since the
   * log data will be written to the log files only
   * <em>after</em> the runs, only log files of completed runs
   * will have a size larger than zero.
   * <p>
   * Overall, this mechanism allows us to do experiments in
   * parallel while not caring about threads or parallelism in
   * anyway. We just start the program as often as we have cores.
   *
   * @param root
   *          the root path
   * @param instance
   *          the instance name
   * @param algorithm
   *          the algorithm setup
   * @param randSeed
   *          the random seed
   * @return the path
   */
  public static final Path logFile(final Path root,
      final String algorithm, final String instance,
      final long randSeed) {
    final Path r = IOUtils.canonicalizePath(root);
    final String algo = Experiment.nameStringPrepare(algorithm);
    final Path algoPath =
        IOUtils.canonicalizePath(r.resolve(algo));

    final String inst = Experiment.nameStringPrepare(instance);
    final Path instPath =
        IOUtils.canonicalizePath(algoPath.resolve(inst));

    final Path filePath = IOUtils.canonicalizePath(
        instPath.resolve(Experiment.nameStringsMerge(algo, inst,
            RandomUtils.randSeedToString(randSeed)) + ".txt")); //$NON-NLS-1$

    try {
      Files.createDirectories(instPath);
    } catch (final IOException error) {
      throw new RuntimeException(
          "Could not create instance directory '" + //$NON-NLS-1$
              instPath + '\'',
          error);
    }

    if (Files.exists(filePath)) {
      return null;
    }

    try {
      Files.createFile(filePath);
    } catch (@SuppressWarnings("unused") final FileAlreadyExistsException error) {
      return null;
    } catch (final IOException error) {
      throw new RuntimeException("Could not create log file '" + //$NON-NLS-1$
          filePath + '\'', error);
    }

    return filePath;
  }

  /**
   * This is a utility method for converting {@code double}
   * values to strings
   *
   * @param d
   *          the {@code double}
   * @return the string
   */
  public static final String
      doubleToStringForName(final double d) {
    if ((d <= Double.NEGATIVE_INFINITY) || //
        (d >= Double.POSITIVE_INFINITY) || //
        Double.isNaN(d)) {
      return Double.toString(d);
    }

    if ((d >= Long.MIN_VALUE) && (d <= Long.MAX_VALUE)) {
      final long l = Math.round(d);
      if (l == d) {
        return Long.toString(l);
      }
    }

    final String s = Double.toString(d);
    if (s.indexOf('E') < 0) {
      return s;
    }

    try {
      final BigDecimal bd = BigDecimal.valueOf(d);
      try {
        final String bis = bd.toBigInteger().toString();
        if (Double.parseDouble(bis) == d) {
          return bis;
        }
      } catch (@SuppressWarnings("unused") final Throwable error1) {
        // ignore
      }
      final String bds = bd.toPlainString();
      if (Double.parseDouble(bds) == d) {
        String best = bds;
        inner: for (int i = bds.length(); (--i) > 0;) {
          try {
            final String test = bds.substring(0, i);
            if (Double.parseDouble(test) == d) {
              best = test;
            }
          } catch (@SuppressWarnings("unused") final Throwable error3) {
            break inner;
          }
        }
        return best;
      }
    } catch (@SuppressWarnings("unused") final Throwable error2) {
      // ignore
    }

    return s;
  }
}
