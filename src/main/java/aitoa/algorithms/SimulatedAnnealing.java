package aitoa.algorithms;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.INullarySearchOperator;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;

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

  /** the temperature schedule */
  public final TemperatureSchedule schedule;

// end relevant

  /**
   * create
   *
   * @param pSchedule
   *          the temperature schedule
   */
  public SimulatedAnnealing(
      final TemperatureSchedule pSchedule) {
    super();

    this.schedule = Objects.requireNonNull(pSchedule);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ("sa_" + //$NON-NLS-1$
        this.schedule.toString());
  }

  /** {@inheritDoc} */
  @Override
  public void printSetup(final Writer output)
      throws IOException {
    output.write(LogFormat.mapEntry("base_algorithm", //$NON-NLS-1$
        "sa")); //$NON-NLS-1$
    output.write(System.lineSeparator());
    IMetaheuristic.super.printSetup(output);
    this.schedule.printSetup(output);
  }

  /** {@inheritDoc} */
  @Override
  public String
      getSetupName(final BlackBoxProcessBuilder<X, Y> builder) {
    return IMetaheuristic.getSetupNameWithUnaryOperator(this,
        builder);
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public void solve(final IBlackBoxProcess<X, Y> process) {
// init local variables xNew, xCur, nullary, unary, random
// end relevant
    final X xNew = process.getSearchSpace().create();
    final X xCur = process.getSearchSpace().create();
    final INullarySearchOperator<X> nullary =
        process.getNullarySearchOperator(); // get nullary op
    final IUnarySearchOperator<X> unary =
        process.getUnarySearchOperator(); // get unary op
    final Random random = process.getRandom();// get random gen
// start relevant
// create starting point: a random point in the search space
    nullary.apply(xCur, random); // put random point in xCur
    double fCur = process.evaluate(xCur); // map & evaluate
    long tau = 1L; // initialize step counter to 1

    do { // repeat until budget exhausted
// create a slightly modified copy of xCur and store in xNew
      unary.apply(xCur, xNew, random);
      ++tau; // increase step counter
// map xNew from X to Y and evaluate candidate solution
      final double fNew = process.evaluate(xNew);
      if ((fNew <= fCur) || // accept if better solution OR
          (random.nextDouble() < // probability is e^(-dE/T)
              Math.exp((fCur - fNew) / // -dE == -(fNew-fCur)
                  this.schedule.temperature(tau)))) {
// accepted: remember objective value and copy xNew to xCur
        fCur = fNew;
        process.getSearchSpace().copy(xNew, xCur);
      } // otherwise fNew > fCur and not accepted
    } while (!process.shouldTerminate()); // until time is up
  } // process will have remembered the best candidate solution
}
// end relevant
