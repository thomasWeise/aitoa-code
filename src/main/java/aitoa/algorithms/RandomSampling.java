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
public final class RandomSampling implements IMetaheuristic {

  /** create */
  public RandomSampling() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final <X, Y> void
      solve(final IBlackBoxProcess<X, Y> process) {
    final X x = process.getSearchSpace().create(); // allocate x
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final Random random = process.getRandom();// get random gen

    do {// repeat until budget exhausted
      nullary.apply(x, random); // sample random solution
      process.evaluate(x); // evaluate it (remembered in process)
    } while (!process.shouldTerminate()); // until time is up
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "rs"; //$NON-NLS-1$
  }
}
