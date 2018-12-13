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
 * modified solution if and only if it is better.
 */
public final class HillClimberWithRestarts
    implements IMetaheuristic {

  /** the initial steps before restarts */
  public final int initialStepsBeforeRestart;

  /** the strategy for the initial steps before restarts */
  public final String initialStepsBeforeRestartStrategy;

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
          "Invalid initialStepsBeforeRestart: " //$NON-NLS-1$
              + _initialStepsBeforeRestart);
    }
    this.initialStepsBeforeRestart = _initialStepsBeforeRestart;
    this.initialStepsBeforeRestartStrategy = Objects
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
        Integer.toString(this.initialStepsBeforeRestart));
    output.newLine();
    output.write("initialStepsBeforeRestartStrategy: ");//$NON-NLS-1$
    output.write(this.initialStepsBeforeRestartStrategy);
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
  public final <X, Y> void
      solve(final IBlackBoxProcess<X, Y> process) {
    final X x_cur = process.getSearchSpace().create();
    final X x_best = process.getSearchSpace().create();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get nullary op
    final Random random = process.getRandom();// get random gen

    long stepsBeforeRestart = this.initialStepsBeforeRestart; // restart
                                                              // after
                                                              // 256
                                                              // failures
    long currentStep = 0L; // initialize counters

    while (!(process.shouldTerminate())) {
      nullary.apply(x_best, random); // sample random solution
      double f_best = process.evaluate(x_best); // evaluate it

      innerHC: do {// repeat until budget exhausted
        unary.apply(x_best, x_cur, random); // try to improve
        ++currentStep;// increase step counter
        final double f_cur = process.evaluate(x_cur); // evaluate
        if (f_cur < f_best) { // we found a better solution
          f_best = f_cur; // remember best quality
          process.getSearchSpace().copy(x_cur, x_best); // update
          currentStep = 0L;
        } else {
          if (currentStep >= stepsBeforeRestart) {
            // increase steps before restart
            stepsBeforeRestart = Math.max(stepsBeforeRestart,
                Math.round(stepsBeforeRestart
                    * (1d + this.increaseFactor)));
            currentStep = 0L;
            break innerHC; // jump back to outer loop for restart
          }
        }
      } while (!process.shouldTerminate()); // until time is up
    }
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((("hc_rs_" + //$NON-NLS-1$
        this.initialStepsBeforeRestartStrategy) + '_')
        + this.increaseFactor);
  }
}
