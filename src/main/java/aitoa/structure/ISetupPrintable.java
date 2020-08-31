package aitoa.structure;

import java.io.IOException;
import java.io.Writer;

/**
 * A metaheuristic optimization algorithm
 */
public interface ISetupPrintable {

  /**
   * Print the setup of this algorithm to the provided writer
   *
   * @param output
   *          the writer to write to
   * @throws IOException
   *           if i/o fails
   */
  default void printSetup(final Writer output)
      throws IOException {
    // do nothing
  }
}
