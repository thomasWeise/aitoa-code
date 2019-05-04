package aitoa.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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
}
