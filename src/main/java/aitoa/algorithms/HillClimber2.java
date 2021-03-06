package aitoa.algorithms;

import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.Metaheuristic1;
import aitoa.utils.Experiment;

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
// start relevant
public final class HillClimber2<X, Y>
    extends Metaheuristic1<X, Y> {
// end relevant
  /**
   * Create the hill climber
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pUnary
   *          the unary search operator
   */
  public HillClimber2(final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary) {
    super(pNullary, pUnary);
    if (!pUnary.canEnumerate()) {
      throw new IllegalArgumentException(//
          "Unary operator cannot enumerate neighborhood."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// init local variables xCur, xBest, n random, fBest, improved
    final X xCur = process.getSearchSpace().create();
    final X xBest = process.getSearchSpace().create();
    final Random random = process.getRandom();// get random gen
    boolean improved = true;

// create starting point: a random point in the search space
    this.nullary.apply(xBest, random); // xBest= random point
    final double[] fBest = { process.evaluate(xBest) }; // evaluate

    while (improved && !process.shouldTerminate()) {
// repeat until budget exhausted or no improving move
// enumerate all neighboring solutions of xBest and receive them
// one-by-one in parameter x (for which xCur is used)
      improved = this.unary.enumerate(random, xBest, xCur, x -> {
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
// end relevant

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge("hc2f", //$NON-NLS-1$
        this.unary);
  }
// start relevant
}
// end relevant
