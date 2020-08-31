package aitoa.algorithms;

import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.Metaheuristic0;

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
    extends Metaheuristic0<X, Y> {
// end relevant
  /**
   * Create the single random sampling algorithm
   *
   * @param pNullary
   *          the nullary search operator.
   */
  public RandomSampling(
      final INullarySearchOperator<X> pNullary) {
    super(pNullary);
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// Allocate data structure for holding 1 point from search space.
    final X x = process.getSearchSpace().create();
    final Random random = process.getRandom();// get random gen

    do { // Repeat until budget is exhausted.
      this.nullary.apply(x, random); // Create random point in X.
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
