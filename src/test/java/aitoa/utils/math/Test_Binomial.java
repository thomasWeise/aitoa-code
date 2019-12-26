package aitoa.utils.math;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import aitoa.TestTools;

/** Test the binomial distributed unary operator flip */
public class Test_Binomial {

  /** Test the binomial distribution */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testBinomialDistribution() {
    final Random random = ThreadLocalRandom.current();
    for (int n = 1; n < 10; n++) {
      for (int m = n; (--m) > 1;) {
        final double p = (m / ((double) n));

        final BinomialDistribution test =
            new BinomialDistribution(n, p);
        final int count = (int) (2 + ((100 * n * n) / p));
        final long[] all = new long[count];

        for (int k = count; (--k) >= 0;) {
          final int res = test.nextInt(random);
          all[k] = res;
        }

        final double meanExp = n * p;
        final double stdExp = Math.sqrt(n * p * (1d - p));
        final Number[] ms =
            Statistics.sampleMeanAndStandardDeviation(all);
        final double meanFound = ms[0].doubleValue();
        final double stdFound = ms[1].doubleValue();

        TestTools.assertGreaterOrEqual(meanFound,
            meanExp - (0.3d * stdExp));
        TestTools.assertLessOrEqual(meanFound,
            meanExp + (0.3d * stdExp));
        TestTools.assertGreaterOrEqual(stdFound, 0.25d * stdExp);
        TestTools.assertLessOrEqual(stdFound, 1.25d * stdExp);
      }
    }
  }

}
