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
 *
 * @param <X>
 *          the search space
 * @param <Y>
 *          the solution space
 */
// start relevant
public final class SimulatedAnnealing<X, Y>
    implements IMetaheuristic<X, Y> {
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
  public final void solve(final IBlackBoxProcess<X, Y> process) {
// init local variables x_new, x_cur, nullary, unary, random
// end relevant
    final X x_new = process.getSearchSpace().create();
    final X x_cur = process.getSearchSpace().create();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get unary op
    final Random random = process.getRandom();// get random gen
// start relevant
// create starting point: a random point in the search space
    nullary.apply(x_cur, random); // put random point in x_cur
    double f_cur = process.evaluate(x_cur); // map & evaluate
    long tau = 1L; // initialize step counter to 1

    do {// repeat until budget exhausted
// create a slightly modified copy of x_cur and store in x_new
      unary.apply(x_cur, x_new, random);
      ++tau; // increase step counter
// map x_new from X to Y and evaluate candidate solution
      final double f_new = process.evaluate(x_new);
      if ((f_new <= f_cur) || // accept if better solution OR
          (random.nextDouble() < // probability is e^(-dE/T)
          Math.exp((f_cur - f_new) / // -dE == -(f_new-f_cur)
              this.schedule.temperature(tau)))) {
// accepted: remember objective value and copy x_new to x_cur
        f_cur = f_new;
        process.getSearchSpace().copy(x_new, x_cur);
      } // otherwise, i.e., f_new >= f_cur: just forget x_new
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
