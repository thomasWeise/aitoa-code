// start relevant
package aitoa.structure;
// end relevant

import java.io.BufferedWriter;
import java.io.IOException;

/** A metaheuristic optimization algorithm */
// start relevant
public interface IMetaheuristic {
  /**
   * Solve the given problem instance
   *
   * @param process
   *          the process with all instance information
   * @param <X>
   *          the search space
   * @param <Y>
   *          the solution space
   */
  public abstract <X, Y> void
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
  public default void printSetup(final BufferedWriter output)
      throws IOException {
    output.write("algorithm: "); //$NON-NLS-1$
    output.write(this.toString());
    output.newLine();
    output.write("algorithm_class: "); //$NON-NLS-1$
    output.write(this.getClass().getCanonicalName());
    output.newLine();
  }
// start relevant
}
// end relevant
