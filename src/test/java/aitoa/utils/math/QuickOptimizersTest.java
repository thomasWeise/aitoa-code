package aitoa.utils.math;

import org.junit.Assert;
import org.junit.Test;

/** Test the internal optimization utilizies */
public class QuickOptimizersTest {
  /**
   * test the
   * {@link QuickOptimizers#unimodal1Dminimization(java.util.function.DoubleUnaryOperator, double, double, double[], boolean)}
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 10000000)
  public void testUnimodal1DMinimize() {
    final double[] d = new double[2];

    QuickOptimizers.unimodal1Dminimization(
        x -> (x - 2) * (x - 2), -100d, 100d, d, true);
    Assert.assertEquals(0d, d[1], 0d);
    Assert.assertEquals(2d, d[0], 0d);

    QuickOptimizers.unimodal1Dminimization(
        x -> (x - 2) * (x - 2), -1e20d, 1e20d, d, true);
    Assert.assertEquals(0d, d[1], 0d);
    Assert.assertEquals(2d, d[0], 4 * Math.ulp(2d));

    QuickOptimizers.unimodal1Dminimization(
        x -> ((x - 2) * (x - 2)) + 1d, -1e20d, 1e20d, d, true);
    Assert.assertEquals(0d, d[1], 1d);
    Assert.assertEquals(2d, d[0], 1e-8d);

    QuickOptimizers.unimodal1Dminimization(
        x -> ((x - 2) * (x - 2)) + 231d, -1e20d, 1e20d, d, true);
    Assert.assertEquals(231d, d[1], 0d);
    Assert.assertEquals(2d, d[0], 1e-7d);

    QuickOptimizers.unimodal1Dminimization(Math::exp, -100d,
        100d, d, true);
    Assert.assertEquals(Math.exp(-100d), d[1], 0d);
    Assert.assertEquals(-100d, d[0], 4 * Math.ulp(100d));

    QuickOptimizers.unimodal1Dminimization(
        x -> Math.exp((x - 3) * (x - 3)), -10d, 10d, d, true);
    Assert.assertEquals(1d, d[1], 0d);
    Assert.assertEquals(3d, d[0], 1e-8d);
  }
}
