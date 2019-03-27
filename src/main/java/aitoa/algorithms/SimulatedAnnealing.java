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
 * A simulated annealing (SA) algorithm is much similar to a hill
 * climber, but it will also move to worse states with a non-zero
 * probability. Here we implement a simple SA approach with
 * exponential temperature schedule.
 * <p>
 * The temperature at algorithm iteration index {@code t} is
 * computed based on a
 * {@linkplain aitoa.algorithms.TemperatureSchedule temperature
 * schedule} and usually either decreases
 * {@linkplain aitoa.algorithms.TemperatureSchedule.Exponential
 * exponentially} or
 * {@linkplain aitoa.algorithms.TemperatureSchedule.Logarithmic
 * logarithmically} with the iteration index from a start
 * temperature.
 * <p>
 * In each step, the algorithm will accept a new solution if it
 * is better than the current best solution. If it is worse, it
 * might still be accepted, but only with a certain probability.
 */
// start relevant
public final class SimulatedAnnealing implements IMetaheuristic {
// end relevant

  /** the temperature scheduke */
  public final TemperatureSchedule schedule;

  /**
   * create
   *
   * @param _schedule
   *          the temperature schedule
   */
  public SimulatedAnnealing(
      final TemperatureSchedule _schedule) {
    super();

    this.schedule = Objects.requireNonNull(_schedule);
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
        process.getUnarySearchOperator(); // get unary op
    final Random random = process.getRandom();// get random gen
// start relevant
// create starting point: a random point in the search space
    nullary.apply(x_best, random); // put random point in x_best
    double f_best = process.evaluate(x_best); // map & evaluate
    final long t = 1L;

    do {// repeat until budget exhausted
// create a slightly modified copy of x_best and store in x_cur
      unary.apply(x_best, x_cur, random);
// map x_cur from X to Y and evaluate candidate solution
      final double f_cur = process.evaluate(x_cur);
      if ((f_cur <= f_best) || // accept if better solution OR
          (random.nextDouble() < // probability is e^(-dE/T)
          Math.exp((f_best - f_cur) / // -dE == -(f_cur-f_best)
              this.schedule.temperature(t)))) {
// accepted: remember objective value and copy x_cur to x_best
        f_best = f_cur;
        process.getSearchSpace().copy(x_cur, x_best);
      } // otherwise, i.e., f_cur >= f_best: just forget x_cur
    } while (!process.shouldTerminate()); // until time is up
  } // process will have remembered the best candidate solution

// end relevant
  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ("sa_" + //$NON-NLS-1$
        this.schedule.toString());
  }

  /** {@inheritDoc} */
  @Override
  public final void printSetup(final BufferedWriter output)
      throws IOException {
    output.write("base_algorithm: sa"); //$NON-NLS-1$
    output.newLine();
    IMetaheuristic.super.printSetup(output);
    this.schedule.printSetup(output);
  }
// start relevant
}
// end relevant
