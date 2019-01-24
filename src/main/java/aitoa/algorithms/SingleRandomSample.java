// start relevant
package aitoa.algorithms;

// end relevant
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;

/**
 * The single random sample algorithm samples one point from the
 * search space and evaluates it.
 */
// start relevant
public final class SingleRandomSample implements IMetaheuristic {
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
  public final <X, Y> void
      solve(final IBlackBoxProcess<X, Y> process) {
// allocate data structure for holding 1 point from search space
    final X x = process.getSearchSpace().create();

// apply nullary operator: fill data structure with a random but
// valid point from the search space
    process.getNullarySearchOperator().apply(x,
        process.getRandom());

// evaluate the point: process.evaluate automatically applies
// representation mapping and calls objective function. the
// objective value is ignored here (not stored anywhere), but
// "process" will remember the best solution, so whoever called
// this "solve" function can obtain the result.
    process.evaluate(x);
  }
}
// end relevant