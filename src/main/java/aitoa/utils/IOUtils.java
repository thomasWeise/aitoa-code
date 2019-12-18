package aitoa.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

/** Utils for I/O */
public final class IOUtils {

  /**
   * Obtain a canonical path
   *
   * @param first
   *          the first string
   * @param more
   *          the more strings
   * @return the canonical version
   */
  public static final Path canonicalizePath(final String first,
      final String... more) {
    if (first == null) {
      throw new IllegalArgumentException(
          "path string cannot be null.");//$NON-NLS-1$
    }
    return IOUtils.canonicalizePath(Paths.get(first, more));
  }

  /**
   * Obtain a canonical path
   *
   * @param p
   *          the path
   * @return the canonical version
   */
  public static final Path canonicalizePath(final Path p) {
    Path r = p.normalize();
    if ((r == null) || (Objects.equals(p, r))) {
      r = p;
    }

    Path z = r.toAbsolutePath();
    if (z != null) {
      if (Objects.equals(p, z)) {
        r = p;
      } else {
        if (!(Objects.equals(r, z))) {
          r = z;
        }
      }
    }

    z = r.normalize();
    if (z != null) {
      if (Objects.equals(p, z)) {
        r = p;
      } else {
        if (!(Objects.equals(r, z))) {
          r = z;
        }
      }
    }

    try {
      z = r.toRealPath();
      if (z != null) {
        if (Objects.equals(p, z)) {
          r = p;
        } else {
          if (!(Objects.equals(r, z))) {
            r = z;
          }
        }
      }
    } catch (@SuppressWarnings("unused") final IOException ioe) {
      // ignore
    }
    return r;
  }

  /**
   * Require that a given path identifies a file.
   *
   * @param file
   *          the file path
   * @return the canonicalized path
   * @throws IOException
   *           if {@code file} does not identify a, existing,
   *           readable, non-empty, regular file
   */
  public static final Path requireFile(final Path file)
      throws IOException {
    final Path ret = IOUtils.canonicalizePath(//
        Objects.requireNonNull(file));
    if (!Files.exists(ret)) {
      throw new IOException("File '" + ret + //$NON-NLS-1$
          "' does not exist.");//$NON-NLS-1$
    }
    if (!Files.isRegularFile(ret)) {
      throw new IOException("Path '" + ret + //$NON-NLS-1$
          "' does not identify a regular file.");//$NON-NLS-1$
    }
    if (!Files.isReadable(ret)) {
      throw new IOException("File '" + ret + //$NON-NLS-1$
          "' is not readable.");//$NON-NLS-1$
    }
    final long size = Files.size(ret);
    if (size <= 0L) {
      throw new IOException(("File '" + ret + //$NON-NLS-1$
          "' has size " + size)//$NON-NLS-1$
          + '.');
    }
    return ret;
  }

  /**
   * Require that a given path identifies a directory.
   *
   * @param dir
   *          the directory path
   * @return the canonicalized path
   * @throws IOException
   *           if {@code file} does not identify an existing
   *           directory
   */
  public static final Path requireDirectory(final Path dir)
      throws IOException {
    return IOUtils.requireDirectory(dir, false);
  }

  /**
   * Require that a given path identifies a directory.
   *
   * @param dir
   *          the directory path
   * @param createIfNotExists
   *          If {@code true}, the directory will be created if
   *          it does not exist. If {@code false}, an error is
   *          thrown if the directory does not exist.
   * @return the canonicalized path
   * @throws IOException
   *           if {@code file} does not identify an existing
   *           directory
   */
  public static final Path requireDirectory(final Path dir,
      final boolean createIfNotExists) throws IOException {
    final Path ret = IOUtils.canonicalizePath(//
        Objects.requireNonNull(dir));

    boolean dirExists = Files.exists(ret);
    if (!dirExists) {
      if (createIfNotExists) {
        Files.createDirectories(ret);
        dirExists = Files.exists(ret);
      }
    }
    if (!dirExists) {
      throw new IOException(//
          "Directory '" + ret + //$NON-NLS-1$
              "' does not exist.");//$NON-NLS-1$
    }
    if (!Files.isDirectory(ret)) {
      throw new IOException("Path '" + ret + //$NON-NLS-1$
          "' does not identify a directory.");//$NON-NLS-1$
    }
    return ret;
  }

  /**
   * Create an array of (sorted) paths from a given stream
   *
   * @param stream
   *          the stream
   * @return the path array
   */
  public static final Path[]
      pathArray(final Stream<Path> stream) {
    return stream.sorted().toArray((i) -> new Path[i]);
  }

  /**
   * A comprehensive check whether a path is contained inside a
   * given directory.
   *
   * @param dir
   *          the directory
   * @param inside
   *          the inside file
   * @return {@code true} if {@code inside} is inside
   *         {@code dir}, {@code false} otherwise
   */
  private static final boolean __inDir(final Path inside,
      final Path dir) {
    if (dir == inside) {
      return false;
    }
    if (dir.equals(inside)) {
      return false;
    }
    try {
      if (Files.isSameFile(dir, inside)) {
        return false;
      }
    } catch (@SuppressWarnings("unused") final Throwable error) {
      return false;
    }
    final String s = inside.getFileName().toString();
    if (".".equals(s) || //$NON-NLS-1$
        "..".equals(s)) {//$NON-NLS-1$
      return false;
    }
    return inside.startsWith(dir);
  }

  /**
   * Get a stream of canonicalized sub-directories in a given
   * path. This method is not recursive.
   *
   * @param dir
   *          the path to list
   * @return the stream of immediate sub-directories
   * @throws IOException
   *           if something fails
   */
  public static final Stream<Path>
      subDirectoriesStream(final Path dir) throws IOException {
    final Path p = IOUtils.requireDirectory(dir);
    return Files.list(p)//
        .filter(Files::exists)//
        .filter(Files::isDirectory)//
        .map(IOUtils::canonicalizePath)//
        .filter((pp) -> IOUtils.__inDir(pp, p));
  }

  /**
   * Get an array of sub-directories in a given path. This method
   * is not recursive.
   *
   * @param dir
   *          the path to list
   * @return the array of immediate sub-directories
   * @throws IOException
   *           if something fails
   */
  public static final Path[] subDirectories(final Path dir)
      throws IOException {
    return (IOUtils.pathArray(//
        IOUtils.subDirectoriesStream(dir)));
  }

  /**
   * A comprehensive check whether a given file is readable
   *
   * @param f
   *          the file
   * @return {@code true} if it is readable, {@code false}
   *         otherwise
   */
  private static final boolean __isReadableFile(final Path f) {
    if (!Files.isReadable(f)) {
      return false;
    }

    try {
      if (Files.size(f) <= 0L) {
        return false;
      }
    } catch (@SuppressWarnings("unused") final Throwable error) {
      return false;
    }

    return true;
  }

  /**
   * Get a stream of canonicalized files of size greater than 0
   * in a given directory. This method is not recursive.
   *
   * @param dir
   *          the path to list
   * @return the stream of immediately contained files
   * @throws IOException
   *           if something fails
   */
  public static final Stream<Path> filesStream(final Path dir)
      throws IOException {
    final Path p = IOUtils.requireDirectory(dir);
    return Files.list(p)//
        .filter(Files::exists)//
        .filter(Files::isRegularFile)//
        .map(IOUtils::canonicalizePath)//
        .filter((pp) -> IOUtils.__inDir(pp, p))//
        .filter(IOUtils::__isReadableFile);
  }

  /**
   * Get an array of canonicalized files of size greater than 0
   * in a given directory. This method is not recursive.
   *
   * @param dir
   *          the path to list
   * @return the array of immediately contained files
   * @throws IOException
   *           if something fails
   */
  public static final Path[] files(final Path dir)
      throws IOException {
    return IOUtils.pathArray(IOUtils.filesStream(dir));
  }

  /**
   * Delete a path. If the path is a file, the file will be
   * deleted. If the path points to a directory, the directory
   * will be deleted recursively with everything in it.
   *
   * @param path
   *          the path to delete
   * @throws IOException
   *           if i/o fails
   */
  public static final void delete(final Path path)
      throws IOException {
    final Path p = IOUtils.canonicalizePath(path);
    if (Files.exists(p)) {
      if (Files.isRegularFile(p)) {
        Files.delete(p);
      } else {
        Files.walk(p).map(Path::toFile)
            .sorted((o1, o2) -> -o1.compareTo(o2))
            .forEach(File::delete);
      }
    }

    if (Files.exists(p)) {
      throw new IOException("Path '" + p //$NON-NLS-1$
          + "' still exists after trying to delete it.");//$NON-NLS-1$
    }
  }

  /**
   * Copy the data from a text resource to the given output
   * writer
   *
   * @param clazz
   *          the class
   * @param name
   *          the resource name
   * @param out
   *          the output writer
   * @throws IOException
   *           if i/o fails
   */
  public static final void copyResource(final Class<?> clazz,
      final String name, final BufferedWriter out)
      throws IOException {
    final InputStream is = clazz.getResourceAsStream(name);
    if (is == null) {
      throw new IOException(//
          "Resource '" + name //$NON-NLS-1$
              + "' of class '" + //$NON-NLS-1$
              ReflectionUtils.className(clazz) + "' not found."); //$NON-NLS-1$
    }
    try (final InputStream q = is;
        final InputStreamReader isr = new InputStreamReader(q);
        final BufferedReader br = new BufferedReader(isr)) {
      IOUtils.copy(br, out);
    }
  }

  /**
   * Copy the contents of a buffered reader to a buffered writer
   *
   * @param in
   *          the input reader
   * @param out
   *          the output writer
   * @throws IOException
   *           if i/o fails
   */
  public static final void copy(final BufferedReader in,
      final BufferedWriter out) throws IOException {
    String line;

    while ((line = in.readLine()) != null) {
      out.write(line);
      out.newLine();
    }
  }

  /** An I/O runnable */
  public static interface IORunnable {
    /**
     * Run the I/O job.
     *
     * @throws IOException
     *           if I/O fails
     */
    public abstract void run() throws IOException;
  }

  /**
   * An I/O consumer
   *
   * @param <T>
   *          the object type
   */
  public static interface IOConsumer<T> {
    /**
     * Consume the object
     *
     * @param t
     *          the object
     * @throws IOException
     *           if I/O fails
     */
    public abstract void accept(final T t) throws IOException;
  }

  /** the I/O synchronizer */
  static final Object _IO_SYNCH = new Object();

  /**
   * Execute a synchronized I/O job
   *
   * @param runnable
   *          the I/O job
   * @throws IOException
   *           if I/O fails
   */
  public static final void synchronizedIO(
      final IORunnable runnable) throws IOException {
    synchronized (IOUtils._IO_SYNCH) {
      runnable.run();
    }
  }
}
