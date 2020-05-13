package aitoa.utils;

import java.io.PrintStream;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import oshi.SystemInfo;

/**
 * With this class, we can write stuff to the console in a
 * thread-safe way. Using the {@link #stderr(Consumer)} or
 * {@link #stdout(Consumer)}-based methods will automatically
 * prepend a process-thread ID tuple(is
 * base-{@value java.lang.Character#MAX_RADIX}), which should
 * unique identify the calling thread on the local computer,
 * followed by the date to the output. Via
 * {@link #setIDSuffix(String)}, you can add more ID elements to
 * be prepended on every log output.
 */
public final class ConsoleIO {

  /**
   * Print to stdout and/or stderr
   *
   * @param print
   *          the stream consumer
   */
  public static void
      print(final BiConsumer<PrintStream, PrintStream> print) {
    synchronized (IOUtils._IO_SYNCH) {
      synchronized (System.out) {
        synchronized (System.err) {
          synchronized (System.in) {
            System.out.flush();
            System.err.flush();

            print.accept(System.out, System.err);

            System.out.flush();
            System.err.flush();
          }
        }
      }
    }
  }

  /**
   * Print to stdout
   *
   * @param out
   *          the output
   */
  public static void stdout(final Consumer<PrintStream> out) {
    ConsoleIO.print((u, v) -> {
      ConsoleIO.__printIDandDate(u);
      out.accept(u);
    });
  }

  /**
   * Print to stdout, by prepending the process and thread ID as
   * well as the current data.
   *
   * @param line
   *          the line
   */
  public static void stdout(final String line) {
    ConsoleIO.stdout(stdout -> stdout.println(line));
  }

  /**
   * print the date and process ID
   *
   * @param ps
   *          the stream
   */
  private static void __printIDandDate(final PrintStream ps) {
    ps.print(ConsoleIO.__IDS.get());
    ps.print(new Date());
    ps.print('\t');
  }

  /**
   * Print to stderr, by prepending the process and thread ID as
   * well as the current data.
   *
   * @param out
   *          the output
   */
  public static void stderr(final Consumer<PrintStream> out) {
    ConsoleIO.print((u, v) -> {
      ConsoleIO.__printIDandDate(v);
      out.accept(v);
    });
  }

  /**
   * Print to stderr, by prepending the process and thread ID as
   * well as the current data.
   *
   * @param out
   *          the output
   * @param error
   *          the error
   */
  public static void stderr(final Consumer<PrintStream> out,
      final Throwable error) {
    ConsoleIO.stderr(stderr -> {
      out.accept(stderr);
      if (error != null) {
        if (error.getClass() == RuntimeException.class) {
          final Throwable cause = error.getCause();
          if (cause != null) {
            cause.printStackTrace(stderr);
            return;
          }
        }
        error.printStackTrace(stderr);
      }
    });
  }

  /**
   * Print to stderr, by prepending the process and thread ID as
   * well as the current data.
   *
   * @param message
   *          the message
   * @param error
   *          the error
   */
  public static void stderr(final String message,
      final Throwable error) {
    ConsoleIO.stderr(stderr -> {
      if (message != null) {
        stderr.println(message);
      }
    }, error);
  }

  /**
   * Set the ID suffix. Normally, each log line is prepended with
   * the process ID and the thread ID. This should uniquely
   * identify the calling thread on the computer. The two IDs are
   * printed in base-{@value java.lang.Character#MAX_RADIX} for
   * brevity and separated by {@code :}. With this method, you
   * can set another element to be included into this prefix. It
   * will then be separated again by {@code :} and appended at
   * the end of the ID. This element will be included in all log
   * output until you call {@link #setIDSuffix} again.
   *
   * @param suffix
   *          the ID suffix to set
   * @see #clearIDSuffix()
   */
  public static void setIDSuffix(final String suffix) {
    ConsoleIO.__IDS.set(ConsoleIO.__computeID(suffix));
  }

  /**
   * Remove the ID suffix.
   *
   * @see #setIDSuffix(String)
   */
  public static void clearIDSuffix() {
    ConsoleIO.setIDSuffix("");//$NON-NLS-1$
  }

  /** get the ID of the current thread */
  private static final ThreadLocal<char[]> __IDS =
      ThreadLocal.withInitial(() -> ConsoleIO.__computeID("")); //$NON-NLS-1$

  /**
   * compute the ID based on a suffix
   *
   * @param suffix
   *          the suffix
   * @return the ID
   */
  private static char[] __computeID(final String suffix) {
    final String t = suffix.trim();
    String a =
        ((Integer.toUnsignedString(__ID.ID, Character.MAX_RADIX)
            + ':')
            + Long.toUnsignedString(
                Thread.currentThread().getId(),
                Character.MAX_RADIX));
    if (t.length() > 0) {
      a = a + ':' + t;
    }
    return (a + ' ').toCharArray();
  }

  /** the ID holder */
  private static final class __ID {

    /** the internal id */
    static final int ID = __ID.__getID();

    /**
     * get the prefix to be used for log entries
     *
     * @return the prefix
     */
    private static int __getID() {
      try {
        return new SystemInfo().getOperatingSystem()
            .getProcessId();
      } catch (@SuppressWarnings("unused") final Throwable error) {
        return ThreadLocalRandom.current().nextInt();
      }
    }
  }
}