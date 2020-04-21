// start relevant
package aitoa.structure;
// end relevant

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import aitoa.utils.Experiment;

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

  /**
   * Get a proper name for the given setup of the algorithm
   *
   * @param builder
   *          the process builder with the setup information
   * @return the setup name
   */
  public default String
      getSetupName(final BlackBoxProcessBuilder<X, Y> builder) {
    return Experiment.nameFromObjectPrepare(this);
  }

  /**
   * Create a setup name by concatenating the base name of an
   * algorithm with a unary operator name.
   *
   * @param <X>
   *          the search space
   * @param <Y>
   *          the solution space
   * @param algorithm
   *          the algorithm
   * @param builder
   *          the process builder
   * @return the setup name
   * @see #getSetupName(BlackBoxProcessBuilder)
   */
  public static <X, Y> String getSetupNameWithUnaryOperator(
      final IMetaheuristic<X, Y> algorithm,
      final BlackBoxProcessBuilder<X, Y> builder) {
    return Experiment.nameFromObjectsMerge(algorithm, //
        Objects.requireNonNull(//
            builder.getUnarySearchOperator()));
  }

  /**
   * Create a setup name by concatenating the base name of an
   * algorithm with a unary operator name.
   *
   * @param <X>
   *          the search space
   * @param <Y>
   *          the solution space
   * @param algorithm
   *          the algorithm
   * @param builder
   *          the process builder
   * @return the setup name
   * @see #getSetupName(BlackBoxProcessBuilder)
   */
  public static <X, Y> String
      getSetupNameWithUnaryAndBinaryOperator(
          final IMetaheuristic<X, Y> algorithm,
          final BlackBoxProcessBuilder<X, Y> builder) {
    return Experiment.nameFromObjectsMerge(algorithm, //
        Objects.requireNonNull(//
            builder.getUnarySearchOperator()), //
        Objects.requireNonNull(//
            builder.getBinarySearchOperator()));
  }
// start relevant
}
// end relevant
