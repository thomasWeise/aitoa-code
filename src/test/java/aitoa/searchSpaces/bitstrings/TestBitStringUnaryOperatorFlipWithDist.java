package aitoa.searchSpaces.bitstrings;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.utils.math.DiscreteConstant;

/** Test the binomial distributed unary operator flip */
public class TestBitStringUnaryOperatorFlipWithDist {

  /** Test the binomial distribution */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void testBinomialDistribution() {
    final Random random = ThreadLocalRandom.current();
    for (int n = 1; n < 10; n++) {
      final boolean[] bits = new boolean[n];
      final boolean[] dest = new boolean[n];
      final long[] count = new long[n];

      for (int m = n; m > 1; m--) {

        final TheTest test = new TheTest(n, m);
        Arrays.fill(count, 0);

        final int steps = 20 * n * n * n;
        for (int i = steps; (--i) >= 0;) {
          test.apply(bits, dest, random);
          int c = 0;
          for (int j = n; (--j) >= 0;) {
            if (dest[j]) {
              ++c;
              ++count[j];
            }
          }
          Assert.assertEquals(c, m);
        }

        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long sum = 0L;
        for (final long i : count) {
          if (i < min) {
            min = i;
          }
          if (i > max) {
            max = i;
          }
          sum += i;
        }

        Assert.assertEquals(sum, m * steps);
        TestTools.assertGreaterOrEqual(min, max * 0.8d);
        TestTools.assertLessOrEqual(max, min / 0.8d);
      }
    }
  }

  /** the test */
  private static final class TheTest
      extends BitStringUnaryOperatorFlipWithDist {

    /**
     * create the unary operator
     *
     * @param pN
     *          the number of bits to flip
     * @param pM
     *          the number of bits to flip
     */
    TheTest(final int pN, final int pM) {
      super(pN, new DiscreteConstant(pM));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
      return "Test"; //$NON-NLS-1$
    }
  }
}
