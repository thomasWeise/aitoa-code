package aitoa.algorithms.jssp;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import aitoa.algorithms.SimulatedAnnealing;
import aitoa.algorithms.TemperatureSchedule;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.SimulatedAnnealing
 * simulated annealing} using an exponential temperature schedule
 * on the JSSP
 */
public class TestSimulatedAnnealingExpJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic
      getAlgorithm(final JSSPInstance instance) {
    final Random rand = ThreadLocalRandom.current();
    double st, ep;

    do {
      st = rand.nextDouble() * 1000;
    } while (st <= 0d);
    do {
      ep = rand.nextDouble();
    } while ((ep <= 0d) || (ep >= 1d));

    return new SimulatedAnnealing(
        new TemperatureSchedule.Exponential(st, ep));
  }
}
