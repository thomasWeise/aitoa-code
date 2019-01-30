package aitoa.algorithms;

import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;

/**
 * The random sampling algorithm keeps sampling points from the
 * search space and evaluates them until the termination
 * criterion is met.
 */
// start relevant
public final class RandomSampling implements IMetaheuristic {
// end relevant

  /** create */
  public RandomSampling() {
    super();
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public final <X, Y> void
      solve(final IBlackBoxProcess<X, Y> process) {
// allocate data structure for holding 1 point from search space
    final X x = process.getSearchSpace().create();
// get nullary search operation for creating random point of X
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator();
    final Random random = process.getRandom();// get random gen

    do {// repeat until budget exhausted
      nullary.apply(x, random); // create random point in X
// evaluate the point: process.evaluate applies representation
// mapping and calls objective function. "process" will remember
// the best solution, so whoever called "solve" can obtain it.
      process.evaluate(x);
    } while (!process.shouldTerminate()); // do until time is up
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "rs"; //$NON-NLS-1$
  }
// start relevant
}
// end relevant
