package aitoa.algorithms;

import java.util.concurrent.ThreadLocalRandom;

/** Test an exponential temperature schedule */
public class TestExponentialTemperatureSchedule
    extends TestTemperatureSchedule {

  /** {@inheritDoc} */
  @Override
  protected final TemperatureSchedule getInstance() {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    return new TemperatureSchedule.Exponential(
        Math.max(1e-17, r.nextDouble() * 1e3), Math.max(1e-17,
            Math.min(0.999999999999, r.nextDouble())));
  }
}
