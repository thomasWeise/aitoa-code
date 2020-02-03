package aitoa.utils.math;

import org.junit.Assert;
import org.junit.Test;

/** Test the internal optimization utilizies */
public class QuickOptimizersTest {
  /**
   * test the
   * {@link QuickOptimizers#unimoal1Dminimization(java.util.function.DoubleUnaryOperator, double, double, double[])}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 10000000)
  public void testUnimodal1DMinimize() {
    final double[] d = new double[2];

    QuickOptimizers.unimoal1Dminimization(
        (x) -> (x - 2) * (x - 2), -100d, 100d, d);
    Assert.assertEquals(0d, d[1], 0d);
    Assert.assertEquals(2d, d[0], 0d);

    QuickOptimizers.unimoal1Dminimization(
        (x) -> (x - 2) * (x - 2), -1e20d, 1e20d, d);
    Assert.assertEquals(0d, d[1], 0d);
    Assert.assertEquals(2d, d[0], 4 * Math.ulp(2d));

    QuickOptimizers.unimoal1Dminimization(
        (x) -> ((x - 2) * (x - 2)) + 1d, -1e20d, 1e20d, d);
    Assert.assertEquals(0d, d[1], 1d);
    Assert.assertEquals(2d, d[0], 1e-8d);

    QuickOptimizers.unimoal1Dminimization(
        (x) -> ((x - 2) * (x - 2)) + 231d, -1e20d, 1e20d, d);
    Assert.assertEquals(231d, d[1], 0d);
    Assert.assertEquals(2d, d[0], 1e-7d);

    QuickOptimizers.unimoal1Dminimization(Math::exp, -100d, 100d,
        d);
    Assert.assertEquals(Math.exp(-100d), d[1], 0d);
    Assert.assertEquals(-100d, d[0], 4 * Math.ulp(100d));

    QuickOptimizers.unimoal1Dminimization(
        (x) -> Math.exp((x - 3) * (x - 3)), -10d, 10d, d);
    Assert.assertEquals(1d, d[1], 0d);
    Assert.assertEquals(3d, d[0], 1e-8d);
  }
}
