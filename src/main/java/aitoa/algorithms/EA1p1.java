package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;

/**
 * The (1+1)-EA is a hill climbing algorithm remembers the
 * current best solution and tries to improve upon it in each
 * step. It does so by applying a modification to this solution
 * and keeps the modified solution if and only if it is better or
 * equal. It is very similar to the {@link HillClimber}. It is
 * slightly different from our {@link EA} implementation with mu
 * and lambda set to 1: In the case that the new solution is
 * equally good as the old one, the (1+1)-EA takes it always,
 * whereas our other EA implementation takes it with 50%
 * probability.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class EA1p1<X, Y> implements IMetaheuristic<X, Y> {
// end relevant
  /** create */
  public EA1p1() {
    super();
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public final void solve(final IBlackBoxProcess<X, Y> process) {
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

    while (!process.shouldTerminate()) {// repeat until budget
                                        // exhausted
// create a slightly modified copy of x_best and store in x_cur
      unary.apply(x_best, x_cur, random);
// map x_cur from X to Y and evaluate candidate solution
      final double f_cur = process.evaluate(x_cur);
      if (f_cur <= f_best) { // we found a better solution
// remember best objective value and copy x_cur to x_best
        f_best = f_cur;
        process.getSearchSpace().copy(x_cur, x_best);
      } // otherwise, i.e., f_cur > f_best: just forget x_cur
    } // until time is up
  } // process will have remembered the best candidate solution
// end relevant

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "(1+1)-EA"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry("base_algorithm", //$NON-NLS-1$
        "1+1_ea")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("mu", 1));///$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("lambda", 1));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("cr", 0));//$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("clearing", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("restarts", false)); //$NON-NLS-1$
    output.write(System.lineSeparator());
  }
// start relevant
}
// end relevant
