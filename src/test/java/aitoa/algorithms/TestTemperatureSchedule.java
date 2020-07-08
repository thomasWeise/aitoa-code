package aitoa.algorithms;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.ObjectTest;
import aitoa.TestTools;

/** Test a temperature schedule */
@Ignore
public abstract class TestTemperatureSchedule
    extends ObjectTest<TemperatureSchedule> {
  /** test the temperature schedule */
  @Test(timeout = 3600000)
  public final void testTemperature() {
    final TemperatureSchedule ts = this.getInstance();
    for (long l = 1L; l < 1000; l++) {
      final double t = ts.temperature(l);
      TestTools.assertFinite(t);
      TestTools.assertGreaterOrEqual(t, 0);
    }
    final double x = ts.temperature(Long.MAX_VALUE);
    TestTools.assertFinite(x);
    TestTools.assertGreaterOrEqual(x, 0);
  }
}
