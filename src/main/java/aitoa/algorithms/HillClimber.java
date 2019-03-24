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
 * <p>
 * If is different from the
 * {@linkplain aitoa.algorithms.HillClimber2 second version} of
 * the hill climbing algorithm in that it uses single, randomized
 * applications of the unary search operator to discover
 * neighboring points while the second version scans the
 * neighborhood by enumerating it.
 */
// start relevant
public final class HillClimber implements IMetaheuristic {
// end relevant
  /** create */
  public HillClimber() {
    super();
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public final <X, Y> void
      solve(final IBlackBoxProcess<X, Y> process) {
// init local variables x_cur, x_best, nullary, unary, random
// end relevant
    final X x_cur = process.getSearchSpace().create();
    final X x_best = process.getSearchSpace().create();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get unary op
    final Random random = process.getRandom();// get random gen
// start relevant
// create starting point: a random point in the search space
    nullary.apply(x_best, random); // put random point in x_best
    double f_best = process.evaluate(x_best); // map & evaluate

    do {// repeat until budget exhausted
// create a slightly modified copy of x_best and store in x_cur
      unary.apply(x_best, x_cur, random);
// map x_cur from X to Y and evaluate candidate solution
      final double f_cur = process.evaluate(x_cur);
      if (f_cur < f_best) { // we found a better solution
// remember best objective value and copy x_cur to x_best
        f_best = f_cur;
        process.getSearchSpace().copy(x_cur, x_best);
      } // otherwise, i.e., f_cur >= f_best: just forget x_cur
    } while (!process.shouldTerminate()); // until time is up
  } // process will have remembered the best candidate solution

// end relevant
  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "hc"; //$NON-NLS-1$
  }
// start relevant
}
// end relevant
