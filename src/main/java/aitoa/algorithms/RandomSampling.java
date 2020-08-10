package aitoa.algorithms;

import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;

/**
 * The random sampling algorithm keeps sampling points from the
 * search space and evaluates them until the termination
 * criterion is met.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class RandomSampling<X, Y>
    implements IMetaheuristic<X, Y> {
// end relevant

  /** create */
  public RandomSampling() {
    super();
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// Allocate data structure for holding 1 point from search space.
    final X x = process.getSearchSpace().create();
// Get nullary search operation for creating random point of X.
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator();
    final Random random = process.getRandom();// get random gen

    do { // Repeat until budget is exhausted.
      nullary.apply(x, random); // Create random point in X.
// Evaluate the point: process.evaluate applies the
// representation mapping and calls objective function. It
// remembers the best solution, so the caller can obtain it.
      process.evaluate(x);
    } while (!process.shouldTerminate()); // do until time is up
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "rs"; //$NON-NLS-1$
  }
// start relevant
}
// end relevant
