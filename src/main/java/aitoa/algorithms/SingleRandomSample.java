package aitoa.algorithms;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.Metaheuristic0;

/**
 * The single random sample algorithm samples one point from the
 * search space and evaluates it.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class SingleRandomSample<X, Y>
    extends Metaheuristic0<X, Y> {
// end relevant
  /**
   * Create the single random sample algorithm
   *
   * @param pNullary
   *          the nullary search operator.
   */
  public SingleRandomSample(
      final INullarySearchOperator<X> pNullary) {
    super(pNullary);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "1rs"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// Allocate data structure for holding 1 point from search space.
    final X x = process.getSearchSpace().create();

// Apply the nullary operator: Fill data structure with a random
// but valid point from the search space.
    this.nullary.apply(x, process.getRandom());

// Evaluate the point: process.evaluate automatically applies the
// representation mapping and calls objective function. The
// objective value is ignored here (not stored anywhere), but
// "process" will remember the best solution. Thus, whoever
// called this "solve" function can obtain the result.
    process.evaluate(x);
  }
}
// end relevant
