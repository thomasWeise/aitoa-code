package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;
import aitoa.structure.Metaheuristic1;
import aitoa.utils.Experiment;

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
public final class EA1p1<X, Y> extends Metaheuristic1<X, Y> {

  /**
   * Create the (1+1) EA
   *
   * @param pNullary
   *          the nullary search operator.
   * @param pUnary
   *          the unary search operator
   */
  public EA1p1(final INullarySearchOperator<X> pNullary,
      final IUnarySearchOperator<X> pUnary) {
    super(pNullary, pUnary);
  }

  /** {@inheritDoc} */
  @Override
  public void solve(final IBlackBoxProcess<X, Y> process) {
// init local variables xCur, xBest, nullary, unary, random
    final X xCur = process.getSearchSpace().create();
    final X xBest = process.getSearchSpace().create();
    final Random random = process.getRandom();// get random gen

// create starting point: a random point in the search space
    this.nullary.apply(xBest, random); // xBest=random point
    double fBest = process.evaluate(xBest); // map & evaluate

    while (!process.shouldTerminate()) {
// create a slightly modified copy of xBest and store in xCur
      this.unary.apply(xBest, xCur, random);
// map xCur from X to Y and evaluate candidate solution
      final double fCur = process.evaluate(xCur);
      if (fCur <= fBest) { // we found a better solution
// remember best objective value and copy xCur to xBest
        fBest = fCur;
        process.getSearchSpace().copy(xCur, xBest);
      } // otherwise, i.e., fCur > fBest: just forget xCur
    } // until time is up
  } // process will have remembered the best candidate solution

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Experiment.nameFromObjectsMerge(//
        "(1+1)-EA", //$NON-NLS-1$
        this.unary);
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry(//
        LogFormat.SETUP_BASE_ALGORITHM, "1+1_ea")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    super.printSetup(output);
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
}
