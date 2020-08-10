package aitoa.algorithms;

import java.util.Random;

import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;

/**
 * The second version of the
 * {@linkplain aitoa.algorithms.HillClimber hill climbing}
 * algorithm remembers the current best solution and tries to
 * improve upon it in each step by enumerating its neighborhood.
 * <p>
 * Different from the first version of the
 * {@linkplain aitoa.algorithms.HillClimber hill climber}, this
 * first-improvement algorithm does so by enumerating the
 * neighborhood of the current-best solution until it finds an
 * improving move.
 * <p>
 * It should be noted that only the search iterations are
 * deterministic, the starting points are still randomly chosen.
 * Hence, the results of this algorithm are still randomized.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
public final class HillClimber2<X, Y>
    implements IMetaheuristic<X, Y> {

  /** create */
  public HillClimber2() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public void solve(final IBlackBoxProcess<X, Y> process) {
// init local variables xCur, xBest, nullary, unary, random,
// fBest, improved: omitted here for brevity
    final X xCur = process.getSearchSpace().create();
    final X xBest = process.getSearchSpace().create();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get unary op
    final Random random = process.getRandom();// get random gen
    boolean improved = true;

// create starting point: a random point in the search space
    nullary.apply(xBest, random); // put random point in xBest
    final double[] fBest = { process.evaluate(xBest) }; // evaluate

    while (improved && !process.shouldTerminate()) {
// repeat until budget exhausted or no improving move
// enumerate all neighboring solutions of xBest and receive them
// one-by-one in parameter x (for which xCur is used)
      improved = unary.enumerate(random, xBest, xCur, x -> {
// map x from X to Y and evaluate candidate solution
        final double fCur = process.evaluate(x);
        if (fCur < fBest[0]) { // we found a better solution
// remember best objective value and copy x to xBest
          fBest[0] = fCur;
          process.getSearchSpace().copy(x, xBest);
          return true; // quit enumerating neighborhood
        }
// no improvement: continue enumeration unless time is up
        return process.shouldTerminate();
      });
// repeat until time is up or no further improvement possible
    }

  } // process will have remembered the best candidate solution

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "hc2f"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public String
      getSetupName(final BlackBoxProcessBuilder<X, Y> builder) {
    return IMetaheuristic.getSetupNameWithUnaryOperator(this,
        builder);
  }
}
