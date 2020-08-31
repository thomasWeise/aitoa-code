package aitoa.structure;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import aitoa.utils.IOUtils.IOConsumer;

/**
 * A black-box single-objective optimization problem encapsulates
 * the objective function, the representation mapping, and the
 * termination criterion. Here, we can blur the distinction
 * between search space and solution space: The black box problem
 * acts as an objective function on the <em>search space</em> by
 * performing the representation mapping and then internally
 * calling an original objective function defined on the solution
 * space.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public interface IBlackBoxProcess<X, Y> extends
    IObjectiveFunction<X>, ITerminationCriterion, Closeable {

  /**
   * Get the random number generator to be used by this process.
   *
   * @return the random number generator to be used by this
   *         process
   */
  Random getRandom();

  /**
   * Get the search space
   *
   * @return the search space
   */
  ISpace<X> getSearchSpace();

  /**
   * Get the best objective value encountered so far
   *
   * @return the best objective value encountered so far, or
   *         {@link Double#POSITIVE_INFINITY} if
   *         {@link #evaluate(Object)} was not invoked yet
   */
  double getBestF();

  /**
   * Get the goal objective value,
   * {@link Double#NEGATIVE_INFINITY} if no goal is specified
   *
   * @return the goal objective value,
   *         {@link Double#NEGATIVE_INFINITY} if no goal is
   *         specified
   */
  double getGoalF();

  /**
   * Get the best point in the search space encountered so far.
   *
   * @param dest
   *          the destination point in the search space, will be
   *          overwritten with the best point
   * @throws IllegalStateException
   *           if {@link #evaluate(Object)} has not yet been
   *           called
   */
  void getBestX(final X dest);

  /**
   * Get the best candidate solution encountered so far.
   *
   * @param dest
   *          the destination candidate solution, will be
   *          overwritten with the best point
   * @throws IllegalStateException
   *           if {@link #evaluate(Object)} has not yet been
   *           called
   */
  void getBestY(final Y dest);

  /**
   * Get the total number of times {@link #evaluate(Object)} was
   * invoked.
   *
   * @return the total number of times {@link #evaluate(Object)}
   *         was invoked.
   */
  long getConsumedFEs();

  /**
   * Get the last time a call to {@link #evaluate(Object)} has
   * led to an improvement of the best candidate solution so far.
   *
   * @return the last time an improvement was made, or {@code 0L}
   *         if {@link #evaluate(Object)} has not yet been called
   */
  long getLastImprovementFE();

  /**
   * Get the maximum allowed FEs, {@link Long#MAX_VALUE} for
   * unlimited
   *
   * @return the maximum allowed FEs, {@link Long#MAX_VALUE} for
   *         unlimited
   */
  long getMaxFEs();

  /**
   * Get the time in milliseconds that has elapsed since the
   * creation of this object.
   *
   * @return the time in milliseconds that has elapsed since the
   *         creation of this object.
   */
  long getConsumedTime();

  /**
   * Get the time in milliseconds that has elapsed since the last
   * time a call to {@link #evaluate(Object)} has led to an
   * improvement of the best candidate solution so far.
   *
   * @return the time in milliseconds that has elapsed since the
   *         last time a call to {@link #evaluate(Object)} has
   *         led to an improvement of the best candidate solution
   *         so far, {@code 0L} if {@link #evaluate(Object)} has
   *         not yet been called
   */
  long getLastImprovementTime();

  /**
   * Get the maximum allowed runtime in milliseconds,
   * {@link Long#MAX_VALUE} for unlimited
   *
   * @return the maximum allowed runtime in milliseconds,
   *         {@link Long#MAX_VALUE} for unlimited
   */
  long getMaxTime();
// end relevant

  /**
   * Free all resources allocated to this object. After a call to
   * this method, calls to all other methods of the object are no
   * longer allowed and have undefined behavior.
   *
   * @throws IOException
   *           if some I/O fails
   */
  @Override
  void close() throws IOException;

  /**
   * Print a section into the log, if logging is supported
   *
   * @param sectionName
   *          the name of the section
   * @param printer
   *          the consumer for the log writer
   * @throws IOException
   *           if I/O fails
   */
  default void printLogSection(final String sectionName,
      final IOConsumer<Writer> printer) throws IOException {
    // does nothing
  }
// start relevant
}
// end relevant
