package aitoa.algorithms;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;

/** Test a logarithmic temperature schedule */
@Ignore
public class TestLogarithmicTemperatureSchedule
    extends TestTemperatureSchedule {

  /** {@inheritDoc} */
  @Override
  protected final TemperatureSchedule getInstance() {
    return new TemperatureSchedule.Logarithmic(Math.max(1e-17,
        ThreadLocalRandom.current().nextDouble() * 1e3));
  }
}
