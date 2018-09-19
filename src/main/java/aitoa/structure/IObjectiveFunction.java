package aitoa.structure;

/**
 * An interface for an objective function.
 *
 * @param <X>
 *          the solution space data structure
 */
public interface IObjectiveFunction<X> {

  /**
   * Evaluate the candidate solution {@code x} and return the
   * corresponding objective value.
   *
   * @param x
   *          the candidate solution
   * @return the objective value
   */
  public abstract double evaluate(final X x);
}
