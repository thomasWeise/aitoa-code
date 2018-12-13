package aitoa.algorithms;

import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;

/**
 * The hill climbing algorithm remembers the current best
 * solution and tries to improve upon it in each step. It does so
 * by applying a modification to this solution and keeps the
 * modified solution if and only if it is better.
 */
public final class HillClimber implements IMetaheuristic {

  /** create */
  public HillClimber() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final <X, Y> void
      solve(final IBlackBoxProcess<X, Y> process) {
    final X x_cur = process.getSearchSpace().create();
    final X x_best = process.getSearchSpace().create();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get nullary op
    final Random random = process.getRandom();// get random gen

    nullary.apply(x_best, random); // sample random solution
    double f_best = process.evaluate(x_best); // evaluate it

    do {// repeat until budget exhausted
      unary.apply(x_best, x_cur, random); // try to improve best
      final double f_cur = process.evaluate(x_cur); // evaluate
      if (f_cur < f_best) { // we found a better solution
        f_best = f_cur; // remember best quality
        process.getSearchSpace().copy(x_cur, x_best); // update
      }
    } while (!process.shouldTerminate()); // until time is up
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "hc"; //$NON-NLS-1$
  }
}
