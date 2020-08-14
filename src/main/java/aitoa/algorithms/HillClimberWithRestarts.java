package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;

/**
 * The hill climbing algorithm remembers the current best
 * solution and tries to improve upon it in each step. It does so
 * by applying a modification to this solution and keeps the
 * modified solution if and only if it is better. If no improving
 * move could be made for some time, this algorithm restarts. Of
 * course, it remembers the overall best solution.
 * <p>
 * If is different from the
 * {@linkplain aitoa.algorithms.HillClimber2WithRestarts second
 * version} of the hill climbing algorithm with restarts in that
 * it uses single, randomized applications of the unary search
 * operator to discover neighboring points while the second
 * version scans the neighborhood by enumerating it.
 * <p>
 * The constructor allows to specify both the steps before the
 * restart as well as a factor for increasing them. If the steps
 * before the restarts are the result of a computation, say,
 * depend on the problem size, then this logic can provided as an
 * additional string parameter.
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class HillClimberWithRestarts<X, Y>
    implements IMetaheuristic<X, Y> {
// end relevant
  /** the number of non-improving steps before restarts */
  public final long failsBeforeRestart;

  /**
   * the strategy that determined the value of
   * {@link #failsBeforeRestart}
   */
  public final String failsBeforeRestartStrategy;

  /**
   * create
   *
   * @param pFailsBeforeRestart
   *          the number of non-improving steps before restarts
   */
  public HillClimberWithRestarts(
      final long pFailsBeforeRestart) {
    this(pFailsBeforeRestart,
        Long.toString(pFailsBeforeRestart));
  }

  /**
   * create
   *
   * @param pFailsBeforeRestart
   *          the number of non-improving steps before restarts
   * @param pFailsBeforeRestartStrategy
   *          the the strategy that determined the value of
   *          {@code _failsBeforeRestart}output.write(System.lineSeparator());
   */
  public HillClimberWithRestarts(final long pFailsBeforeRestart,
      final String pFailsBeforeRestartStrategy) {
    super();

    if ((pFailsBeforeRestart < 1L)
        || (pFailsBeforeRestart > 1_000_000_000L)) {
      throw new IllegalArgumentException(
          "failsBeforeRestart must be in 1...1_000_000_000, but is " //$NON-NLS-1$
              + pFailsBeforeRestart);
    }
    this.failsBeforeRestart = pFailsBeforeRestart;

    this.failsBeforeRestartStrategy =
        pFailsBeforeRestartStrategy.trim();
    if (this.failsBeforeRestartStrategy.isEmpty()) {
      throw new IllegalArgumentException(
          "Invalid failsBeforeRestartStrategy: '" //$NON-NLS-1$
              + pFailsBeforeRestartStrategy + "'."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    IMetaheuristic.super.printSetup(output);
    output.write(LogFormat.mapEntry("failsBeforeRestart", ///$NON-NLS-1$
        this.failsBeforeRestart));
    output.write(System.lineSeparator());
    output.write(LogFormat.mapEntry("failsBeforeRestartStrategy", ///$NON-NLS-1$
        this.failsBeforeRestartStrategy));
    output.write(System.lineSeparator());
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// end relevant
    final X xCur = process.getSearchSpace().create();
    final X xBest = process.getSearchSpace().create();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get unary op
    final Random random = process.getRandom();// get random gen

// start relevant
// omitted: initialize local variables xCur, xBest, nullary,
// unary,random, failsBeforeRestart, and failCounter=0
    while (!(process.shouldTerminate())) { // outer loop: restart
      nullary.apply(xBest, random); // sample random solution
      double fBest = process.evaluate(xBest); // evaluate it
      long failCounter = 0L; // initialize counters

      while (!(process.shouldTerminate())) { // inner loop
        unary.apply(xBest, xCur, random); // try to improve
        final double fCur = process.evaluate(xCur); // evaluate

        if (fCur < fBest) { // we found a better solution
          fBest = fCur; // remember best quality
          process.getSearchSpace().copy(xCur, xBest); // copy
          failCounter = 0L; // reset number of unsuccessful steps
        } else { // ok, we did not find an improvement
          if ((++failCounter) >= this.failsBeforeRestart) {
            break; // jump back to outer loop for restart
          } // increase fail counter
        } // failure
      } // inner loop
    } // outer loop
  } // process has stored best-so-far result
// end relevant

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "hc_rs_" + //$NON-NLS-1$
        this.failsBeforeRestartStrategy;
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
