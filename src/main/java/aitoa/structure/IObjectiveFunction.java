package aitoa.structure;

/**
 * An interface for an objective function, subject to
 * minimization.
 *
 * @param <Y>
 *          the solution space data structure
 */
@FunctionalInterface
// start relevant
public interface IObjectiveFunction<Y> {

  /**
   * Evaluate the candidate solution {@code y} and return the
   * corresponding objective value.
   *
   * @param y
   *          the candidate solution
   * @return the objective value: smaller values are better
   */
  double evaluate(Y y);
// end relevant

  /**
   * Compute a lower bound, if possible. The default
   * implementation of this method returns
   * {@link Double#NEGATIVE_INFINITY}.
   *
   * @return the lower bound of the objective value
   */
// start lowerBound
  default double lowerBound() {
    return Double.NEGATIVE_INFINITY;
  }
// end lowerBound

  /**
   * Compute an upper bound, if possible. The default
   * implementation of this method returns
   * {@link Double#POSITIVE_INFINITY}. This method is mainly used
   * in our unit tests to ensure that the computed objective
   * values are valid.
   *
   * @return the upper bound of the objective value
   */
  default double upperBound() {
    return Double.POSITIVE_INFINITY;
  }

// start relevant
}
// end relevant
