package aitoa.algorithms;

import java.util.Random;

import aitoa.structure.BlackBoxProcessBuilder;
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
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class HillClimber<X, Y>
    implements IMetaheuristic<X, Y> {
// end relevant
  /** create */
  public HillClimber() {
    super();
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// init local variables xCur, xBest, nullary, unary, random
// end relevant
    final X xCur = process.getSearchSpace().create();
    final X xBest = process.getSearchSpace().create();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get unary op
    final Random random = process.getRandom();// get random gen
// start relevant
// Create starting point: a random point in the search space.
    nullary.apply(xBest, random); // Put random point in xBest.
    double fBest = process.evaluate(xBest); // map & evaluate

    while (!process.shouldTerminate()) {
// Create a slightly modified copy of xBest and store in xCur.
      unary.apply(xBest, xCur, random);
// Map xCur from X to Y and evaluate candidate solution.
      final double fCur = process.evaluate(xCur);
      if (fCur < fBest) { // we found a better solution
// Remember best objective value and copy xCur to xBest.
        fBest = fCur;
        process.getSearchSpace().copy(xCur, xBest);
      } // Otherwise, i.e., fCur >= fBest: Just forget xCur.
    } // Repeat until computational budget is exhausted.
  } // `process` has remembered the best candidate solution.
// end relevant

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "hc"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public String
      getSetupName(final BlackBoxProcessBuilder<X, Y> builder) {
    return IMetaheuristic.getSetupNameWithUnaryOperator(this,
        builder);
  }
// start relevant
}
// end relevant
