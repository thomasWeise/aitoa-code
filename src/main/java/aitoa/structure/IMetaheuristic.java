package aitoa.structure;

/** A metaheuristic optimization algorithm */
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
}
