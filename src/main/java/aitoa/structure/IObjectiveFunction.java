package aitoa.structure;

/**
 * An interface for an objective function, subject to
 * minimization.
 *
 * @param <Y>
 *          the solution space data structure
 */
@FunctionalInterface
public interface IObjectiveFunction<Y> {

  /**
   * Evaluate the candidate solution {@code y} and return the
   * corresponding objective value.
   *
   * @param y
   *          the candidate solution
   * @return the objective value: smaller values are better
   */
  public abstract double evaluate(final Y y);
}
