package aitoa.utils;

import java.io.File;
import java.io.IOException;
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
    final Path p = IOUtils.canonicalizePath(dir);
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
    return (IOUtils
        .pathArray(IOUtils.subDirectoriesStream(dir)));
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
    final Path p = IOUtils.canonicalizePath(dir);
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
}
