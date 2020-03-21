// start relevant
package aitoa.structure;
// end relevant

import java.io.IOException;
import java.io.Writer;

/**
 * A metaheuristic optimization algorithm
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public interface IMetaheuristic<X, Y> {
  /**
   * Solve the given problem instance
   *
   * @param process
   *          the process with all instance information
   */
  public abstract void
      solve(final IBlackBoxProcess<X, Y> process);
// end relevant

  /**
   * Print the setup of this algorithm to the provided buffered
   * writer
   *
   * @param output
   *          the writer to write to
   * @throws IOException
   *           if i/o fails
   */
  public default void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry("algorithm", this)); //$NON-NLS-1$
    output.write(System.lineSeparator());
  }
// start relevant
}
// end relevant
