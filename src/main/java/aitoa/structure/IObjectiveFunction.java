package aitoa.structure;

/**
 * An interface for an objective function.
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
   * @return the objective value
   */
  public abstract double evaluate(final Y y);
}
