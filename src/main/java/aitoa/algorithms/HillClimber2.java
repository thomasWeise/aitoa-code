package aitoa.algorithms;

import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;

/**
 * The second version of the hill climbing algorithm remembers
 * the current best solution and tries to improve upon it in each
 * step by enumerating its neighborhood.
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
 */
// start relevant
public final class HillClimber2 implements IMetaheuristic {

// end relevant
  /** create */
  public HillClimber2() {
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
        process.getUnarySearchOperator(); // get nullary op
    final Random random = process.getRandom();// get random gen
    boolean improved = false;
// start relevant
// create starting point: a random point in the search space
    nullary.apply(x_best, random); // put random point in x_best
    final double[] f_best = { process.evaluate(x_best) }; // evaluate

    do {// repeat until budget exhausted or no improving move
// enumerate all neighboring solutions of x_best and receive them
// one-by-one in parameter x (for which x_cur is used)
      improved = unary.enumerate(x_best, x_cur, (x) -> {
// map x from X to Y and evaluate candidate solution
        final double f_cur = process.evaluate(x);
        if (f_cur < f_best[0]) { // we found a better solution
// remember best objective value and copy x to x_best
          f_best[0] = f_cur;
          process.getSearchSpace().copy(x, x_best);
          return true; // quit enumerating neighborhood
        }
// no improvement: continue enumeration unless time is up
        return process.shouldTerminate();
      });
// repeat until time is up or no further improvement possible
    } while (improved && !process.shouldTerminate());

  } // process will have remembered the best candidate solution

// end relevant
  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "hc2f"; //$NON-NLS-1$
  }
// start relevant
}
// end relevant
