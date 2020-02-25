package aitoa.algorithms;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;

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
    implements IMetaheuristic<X, Y> {
// end relevant
  /** create */
  public SingleRandomSample() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "1rs"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public final void solve(final IBlackBoxProcess<X, Y> process) {
// Allocate data structure for holding 1 point from search space.
    final X x = process.getSearchSpace().create();

// Apply the nullary operator: Fill data structure with a random
// but valid point from the search space.
    process.getNullarySearchOperator().apply(x,
        process.getRandom());

// Evaluate the point: process.evaluate automatically applies the
// representation mapping and calls objective function. The
// objective value is ignored here (not stored anywhere), but
// "process" will remember the best solution. Thus, whoever
// called this "solve" function can obtain the result.
    process.evaluate(x);
  }
}
// end relevant
