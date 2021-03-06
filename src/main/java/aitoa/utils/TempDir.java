package aitoa.utils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A temporary directory which can be wrapped into a
 * {@code try-with-resources} statement. The directory is created
 * in the constructor and deleted once the {@link #close()}
 * method is called. Within the {@code try-with-resources}
 * statement, you can get the path to the temporary directory via
 * {@link #getPath()}. You can then create as many files and
 * sub-directories inside the temporary directory as you wish,
 * they will all automatically be deleted at the end.
 */
public final class TempDir implements Closeable {

  /** the directory */
  private final Path mDir;

  /**
   * create the temporary directory
   *
   * @throws IOException
   *           if io fails
   */
  public TempDir() throws IOException {
    super();
    final Path p1 = IOUtils.canonicalizePath(//
        Files.createTempDirectory(null));
    if (p1 == null) {
      throw new IOException(
          "Failed to create temporary directory."); //$NON-NLS-1$
    }
    final Path p2 = p1.normalize();
    this.mDir = ((p2 != null) ? p2 : p1);
  }

  /**
   * Get the path of the temporary directory.
   *
   * @return the path to the directory
   */
  public Path getPath() {
    return this.mDir;
  }

  /** {@inheritDoc} */
  @Override
  public void close() throws IOException {
    try {
      IOUtils.delete(this.mDir);
    } catch (@SuppressWarnings("unused") final Throwable error) {
      // ignore
    }
  }
}
