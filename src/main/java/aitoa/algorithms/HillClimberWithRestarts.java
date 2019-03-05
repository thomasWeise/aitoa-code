package aitoa.algorithms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;

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
 */
// start relevant
public final class HillClimberWithRestarts
    implements IMetaheuristic {
// end relevant
  /** the initial steps before restarts */
  public final int initialFailsBeforeRestart;

  /** the strategy for the initial steps before restarts */
  public final String initialFailsBeforeRestartStrategy;

  /** the increase factor */
  public final double increaseFactor;

  /**
   * create
   *
   * @param _initialStepsBeforeRestart
   *          the initial number of steps before restarts
   * @param _initialStepsBeforeRestartStrategy
   *          the strategy for the initial steps before restarts
   * @param _increaseFactor
   *          the increase factor
   */
  public HillClimberWithRestarts(
      final int _initialStepsBeforeRestart,
      final String _initialStepsBeforeRestartStrategy,
      final double _increaseFactor) {
    super();

    if ((_initialStepsBeforeRestart < 1)
        || (_initialStepsBeforeRestart > 1_000_000)) {
      throw new IllegalArgumentException(
          "Invalid initialFailsBeforeRestart: " //$NON-NLS-1$
              + _initialStepsBeforeRestart);
    }
    this.initialFailsBeforeRestart = _initialStepsBeforeRestart;
    this.initialFailsBeforeRestartStrategy = Objects
        .requireNonNull(_initialStepsBeforeRestartStrategy);

    if ((_increaseFactor < 0d) || (_increaseFactor > 16d)
        || (!(Double.isFinite(_increaseFactor)))) {
      throw new IllegalArgumentException(
          "Invalid increaseFactor: " + _increaseFactor); //$NON-NLS-1$
    }
    this.increaseFactor = _increaseFactor;
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final BufferedWriter output)
      throws IOException {
    output.write("algorithm: hc_rs"); //$NON-NLS-1$
    output.newLine();
    output.write("algorithm_class: "); //$NON-NLS-1$
    output.write(this.getClass().getCanonicalName());
    output.newLine();
    output.write("initialStepsBeforeRestart: "); //$NON-NLS-1$
    output.write(//
        Integer.toString(this.initialFailsBeforeRestart));
    output.newLine();
    output.write("initialStepsBeforeRestartStrategy: ");//$NON-NLS-1$
    output.write(this.initialFailsBeforeRestartStrategy);
    output.newLine();
    output.write("increaseFactor: ");//$NON-NLS-1$
    output.write(Double.toString(this.increaseFactor));
    output.newLine();
    output.write("increaseFactor(hex): ");//$NON-NLS-1$
    output.write(Double.toHexString(this.increaseFactor));
    output.newLine();
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public final <X, Y> void
      solve(final IBlackBoxProcess<X, Y> process) {
// end relevant
    final X x_cur = process.getSearchSpace().create();
    final X x_best = process.getSearchSpace().create();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get nullary op
    final Random random = process.getRandom();// get random gen

    long failsBeforeRestart = this.initialFailsBeforeRestart;
    long failCounter = 0L; // initialize counters
// start relevant
// omitted: initialize local variables x_cur, x_best, nullary,
// unary,random, failsBeforeRestart, and failCounter=0
    while (!(process.shouldTerminate())) { // outer loop: restart
      nullary.apply(x_best, random); // sample random solution
      double f_best = process.evaluate(x_best); // evaluate it

      innerHC: do {// repeat until budget exhausted or got stock
        unary.apply(x_best, x_cur, random); // try to improve
        ++failCounter;// increase step counter
        final double f_cur = process.evaluate(x_cur); // evaluate
        if (f_cur < f_best) { // we found a better solution
          f_best = f_cur; // remember best quality
          process.getSearchSpace().copy(x_cur, x_best); // copy
          failCounter = 0L; // reset number of unsuccessful steps
        } else { // ok, we did not find an improvement
          if (failCounter >= failsBeforeRestart) {
            // increase steps before restart
            failsBeforeRestart = Math.max(failsBeforeRestart,
                Math.round(failsBeforeRestart
                    * (1d + this.increaseFactor)));
            failCounter = 0L;
            break innerHC; // jump back to outer loop for restart
          }
        }
      } while (!process.shouldTerminate()); // until time is up
    }
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((("hc_rs_" + //$NON-NLS-1$
        this.initialFailsBeforeRestartStrategy) + '_')
        + this.increaseFactor);
  }
// start relevant
}
// end relevant
