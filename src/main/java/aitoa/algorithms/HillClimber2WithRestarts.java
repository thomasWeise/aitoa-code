package aitoa.algorithms;

import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.Metaheuristic1;
import aitoa.utils.Experiment;

/**
 * The second version of the hill climbing algorithm with
 * restarts remembers the current best solution and tries to
 * improve upon it in each step by enumerating its neighborhood.
 * If no improvement could be found in the whole neighborhood, it
 * restarts.
 * <p>
 * Different from the first version of
 * {@linkplain aitoa.algorithms.HillClimber hill climber}, this
 * first-improvement algorithm does so by enumerating the
 * neighborhood of the current-best solution until it finds an
 * improving move.
 * <p>
 * This version here performs restarts. Different from the
 * {@linkplain aitoa.algorithms.HillClimber stochastic hill
 * climber with restarts}, we do not need any parameter to decide
 * when we should restart. We can simply do this when no
 * improving move was found.
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
public final class HillClimber2WithRestarts<X, Y>
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
  public HillClimber2WithRestarts(
      final INullarySearchOperator<X> pNullary,
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
// initialization of local variables xCur, xBest, random omitted
// for brevety
// end relevant
    final X xCur = process.getSearchSpace().create();
    final X xBest = process.getSearchSpace().create();
    final Random random = process.getRandom();// get random gen
    boolean improved = false;
    final double[] fBest = new double[1]; // needs to be array
// start relevant
    while (!process.shouldTerminate()) { // main loop
// create starting point: a random point in the search space
// put random point in xBest
      this.nullary.apply(xBest, random);
      fBest[0] = process.evaluate(xBest); // evaluate

      do { // repeat until budget exhausted or no improving move
// enumerate all neighboring solutions of xBest and receive them
// one-by-one in parameter x (for which xCur is used)
        improved = this.unary.enumerate(random, xBest, xCur, //
            x -> {
// map x from X to Y and evaluate candidate solution
              final double fCur = process.evaluate(x);
              if (fCur < fBest[0]) { // found better solution
// remember best objective value and copy x to xBest
                fBest[0] = fCur;
                process.getSearchSpace().copy(x, xBest);
                return true; // quit enumerating neighborhood
              }
// no improvement: continue enumeration unless time is up
              return process.shouldTerminate();
            });
// repeat until time is up or no further improvement possible
        if (process.shouldTerminate()) {
          return; // ok, we should exit
        } // otherwise: continue inner loop as long as we
      } while (improved); // can find improvements
    } // outer loop: if we get here, we need to restart
  } // process will have remembered the best candidate solution
// end relevant

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge("hc2f_rs", //$NON-NLS-1$
        this.unary);
  }
// start relevant
}
// end relevant
