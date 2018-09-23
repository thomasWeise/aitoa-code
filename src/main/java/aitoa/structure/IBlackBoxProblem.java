// start relevant
package aitoa.structure;
// end relevant

import java.io.Closeable;
import java.nio.file.Path;
import java.util.Objects;

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
public interface IBlackBoxProblem<X, Y> extends
    IObjectiveFunction<X>, ITerminationCriterion, Closeable {

  /**
   * Get the best objective value encountered so far
   *
   * @return the best objective value encountered so far, or
   *         {@link Double#POSITIVE_INFINITY} if
   *         {@link #evaluate(Object)} was not invoked yet
   */
  public abstract double getBestF();

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
  public abstract void getBestX(final X dest);

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
  public abstract void getBestY(final Y dest);

  /**
   * Get the total number of times {@link #evaluate(Object)} was
   * invoked.
   *
   * @return the total number of times {@link #evaluate(Object)}
   *         was invoked.
   */
  public abstract long getConsumedFEs();

  /**
   * Get the last time a call to {@link #evaluate(Object)} has
   * led to an improvement of the best candidate solution so far.
   *
   * @return the last time an improvement was made, or {@code 0L}
   *         if {@link #evaluate(Object)} has not yet been called
   */
  public abstract long getLastImprovementFE();

  /**
   * Get the time in milliseconds that has elapsed since the
   * creation of this object.
   *
   * @return the time in milliseconds that has elapsed since the
   *         creation of this object.
   */
  public abstract long getConsumedTime();

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
  public abstract long getLastImprovementTime();

  /**
   * Free all resources allocated to this object. After a call to
   * this method, calls to all other methods of the object are no
   * longer allowed and have undefined behavior.
   */
  @Override
  public abstract void close();

// end relevant
  /**
   * Create a black box problem
   *
   * @param searchSpace
   *          the search space
   * @param solutionSpace
   *          the solution space: if and only if
   *          {@code mapping==null}, the solution space must be
   *          {@code null} or equal to {@code searchSpace}
   * @param mapping
   *          the representation mapping, or {@code null} if the
   *          search and solution space are the same
   * @param f
   *          the objective function
   * @param maxFEs
   *          the maximum permitted FEs, use
   *          {@link Long#MAX_VALUE} for unlimited
   * @param maxTime
   *          the maximum permitted runtime in milliseconds, use
   *          {@link Long#MAX_VALUE} for unlimited
   * @param goalF
   *          the goal objective value: the run will be
   *          terminated when we reach a better or equally good
   *          solution
   * @param logFile
   *          the log file. after the black box problem is
   *          "closed", log information will be written to the
   *          file. until then, it is kept in memory
   * @param expectedLogLength
   *          the expected maximum number of points to enter the
   *          log, set to {@code 0} if unknown
   * @return the black-box problem
   * @param <XX>
   *          the search space
   * @param <YY>
   *          the solution space
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <XX, YY> IBlackBoxProblem<XX, YY> create(
      final ISpace<XX> searchSpace,
      final ISpace<YY> solutionSpace,
      final IRepresentationMapping<XX, YY> mapping,
      final IObjectiveFunction<YY> f, final long maxFEs,
      final long maxTime, final double goalF, final Path logFile,
      final int expectedLogLength) {

    if (mapping == null) {
      if (Objects.equals(searchSpace, solutionSpace)
          || (solutionSpace == null)) {
        if (logFile == null) {
          // no logging and search and solution space are the
          // same
          return new _BlackBoxProblem1NoLog(searchSpace, f,
              maxFEs, maxTime, goalF);
        }
        // logging and search and solution space are the same
        return new _BlackBoxProblem1Log(searchSpace, f, maxFEs,
            maxTime, goalF, logFile, expectedLogLength);
      }
      throw new IllegalArgumentException(
          "If no representation mapping is provided for different spaces!"); //$NON-NLS-1$
    }

    if (logFile == null) {
      // no logging and search and solution space are different
      return new _BlackBoxProblem2NoLog(searchSpace,
          solutionSpace, mapping, f, maxFEs, maxTime, goalF);
    }
    // logging and search and solution space are different
    return new _BlackBoxProblem2Log(searchSpace, solutionSpace,
        mapping, f, maxFEs, maxTime, goalF, logFile,
        expectedLogLength);
  }

  /**
   * Create a black box problem where search and solution space
   * are the same.
   *
   * @param searchAndSolutionSpace
   *          the search- and solution space
   * @param f
   *          the objective function
   * @param maxFEs
   *          the maximum permitted FEs, use
   *          {@link Long#MAX_VALUE} for unlimited
   * @param maxTime
   *          the maximum permitted runtime in milliseconds, use
   *          {@link Long#MAX_VALUE} for unlimited
   * @param goalF
   *          the goal objective value: the run will be
   *          terminated when we reach a better or equally good
   *          solution
   * @param logFile
   *          the log file. after the black box problem is
   *          "closed", log information will be written to the
   *          file. until then, it is kept in memory
   * @param expectedLogLength
   *          the expected maximum number of points to enter the
   *          log, set to {@code 0} if unknown
   * @return the black-box problem
   * @param <XX>
   *          the search and solution space
   */
  public static <XX> IBlackBoxProblem<XX, XX> create(
      final ISpace<XX> searchAndSolutionSpace,
      final IObjectiveFunction<XX> f, final long maxFEs,
      final long maxTime, final double goalF, final Path logFile,
      final int expectedLogLength) {
    return (IBlackBoxProblem.create(searchAndSolutionSpace,
        searchAndSolutionSpace, null, f, maxFEs, maxTime, goalF,
        logFile, expectedLogLength));
  }
// start relevant
}
// end relevant
