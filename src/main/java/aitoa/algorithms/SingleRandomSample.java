package aitoa.algorithms;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;

/**
 * The single random sample algorithm samples one point from the
 * search space and evaluates it.
 */
public final class SingleRandomSample implements IMetaheuristic {

  /** create */
  public SingleRandomSample() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final <X, Y> void
      solve(final IBlackBoxProcess<X, Y> process) {
    // allocate data structure for point from search space
    final X x = process.getSearchSpace().create();

    // apply nullary operator: fill data structure with random
    // but valid point
    process.getNullarySearchOperator().apply(x,
        process.getRandom());

    // evaluate point (process will remember the result)
    process.evaluate(x);
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "1rs"; //$NON-NLS-1$
  }
}
