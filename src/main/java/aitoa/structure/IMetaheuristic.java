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
public interface IMetaheuristic<X, Y> extends ISetupPrintable {
  /**
   * Solve the given problem instance
   *
   * @param process
   *          the process with all instance information
   */
  void solve(IBlackBoxProcess<X, Y> process);
// end relevant

  /**
   * Print the setup of this algorithm to the provided writer
   *
   * @param output
   *          the writer to write to
   * @throws IOException
   *           if i/o fails
   */
  @Override
  default void printSetup(final Writer output)
      throws IOException {
    output.write(
        LogFormat.mapEntry(LogFormat.SETUP_ALGORITHM, this));
    output.write(System.lineSeparator());
  }
// start relevant
}
// end relevant
