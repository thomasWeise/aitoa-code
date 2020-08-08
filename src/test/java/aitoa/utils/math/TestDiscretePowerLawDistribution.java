package aitoa.utils.math;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;

/** Test the power law distributed unary operator flip */
public class TestDiscretePowerLawDistribution {

  /** Test the power law distribution */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testPowerLawDistribution() {
    final Random random = ThreadLocalRandom.current();
    for (final double alpha : new double[] { 1.5d, 2d, 3d }) {
      for (int n = 2; n <= 10; n++) {
        final DiscretePowerLawDistribution test =
            new DiscretePowerLawDistribution(1, n, alpha);

        final int count = (2 + ((1000 * n * n * n)));
        final long[] occurences = new long[n];

        for (int k = count; (--k) >= 0;) {
          ++occurences[test.nextInt(random)];
        }

        Assert.assertEquals(0, occurences[0]);

        // We try to test whether the points roughly are a linear
        // function in the log-log scale. If so, we accept that
        // they are power-law distributed.
        if (n > 2) {
          double minSlope = Double.POSITIVE_INFINITY;
          double maxSlope = Double.NEGATIVE_INFINITY;
          double minIntercept = Double.POSITIVE_INFINITY;
          double maxIntercept = Double.NEGATIVE_INFINITY;
          for (int i = (n - 1); (--i) > 0;) {
            final long oc0 = occurences[i];
            final long oc1 = occurences[i + 1];
            TestTools.assertGreater(oc0, oc1);
            if (oc1 <= 10) {
              continue;
            }
            final double x0 = Math.log(i);
            final double x1 = Math.log(i + 1);
            final double y0 = Math.log(oc0);
            final double y1 = Math.log(oc1);
            final double slope = (y1 - y0) / (x1 - x0);
            minSlope = Math.min(minSlope, slope);
            maxSlope = Math.max(maxSlope, slope);
            final double intercept = y1 - (slope * x1);
            minIntercept = Math.min(minIntercept, intercept);
            maxIntercept = Math.max(maxIntercept, intercept);
          }

          TestTools.assertLessOrEqual(minSlope, maxSlope);
          TestTools.assertLess(maxSlope, 0d);
          TestTools.assertGreaterOrEqual(minSlope,
              maxSlope / 0.7);
          TestTools.assertLessOrEqual(maxSlope, minSlope * 0.7);

          TestTools.assertLessOrEqual(minIntercept,
              maxIntercept);
          TestTools.assertGreater(minIntercept, 0d);
          TestTools.assertLessOrEqual(maxIntercept * 0.7,
              minIntercept);
          TestTools.assertGreaterOrEqual(minIntercept / 0.7,
              maxIntercept);
        }
      }
    }
  }

}
