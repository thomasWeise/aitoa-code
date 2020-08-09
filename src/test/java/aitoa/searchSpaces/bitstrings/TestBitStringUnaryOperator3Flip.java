package aitoa.searchSpaces.bitstrings;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;

/** Test the bit string 3-flip unary operator */
@Ignore
public class TestBitStringUnaryOperator3Flip
    extends TestBitStringUnaryOperator {
  /**
   * create the unary operator test
   *
   * @param length
   *          the dimension of the bit strings
   * @param unary
   *          the unary operator
   */
  public TestBitStringUnaryOperator3Flip(final int length,
      final BitStringUnaryOperator3Flip unary) {
    super(length, unary);
  }

  /** test the enumeration */
  @Test(timeout = 3600000)
  public void testEnumerateFlip3() {
    final boolean[] x = this.createValid();
    final boolean[] y = this.getSpace().create();
    final int[] has = new int[x.length];
    final boolean[] once = new boolean[x.length];
    final int[] twice = new int[x.length];
    final int[] thrice = new int[x.length];

    this.getInstance().enumerate(ThreadLocalRandom.current(), x,
        y, z -> {
          int done = 0;
          int first = -1;
          int second = -1;
          int third = -1;
          for (int i = x.length; (--i) >= 0;) {
            if (x[i] != y[i]) {
              if (first >= 0) {
                if (second >= 0) {
                  third = i;
                } else {
                  second = i;
                }
              } else {
                first = i;
              }
              ++done;
              ++has[i];
            }
          }
          TestTools.assertInRange(done, 1, 3);
          if (done == 1) {
            Assert.assertFalse(once[first]);
            once[first] = true;
          } else {
            if (done == 2) {
              ++twice[first];
              ++twice[second];
            } else {
              Assert.assertEquals(3, done);
              ++thrice[first];
              ++thrice[second];
              ++thrice[third];
            }
          }
          return false;
        });

    for (final int d : has) {
      Assert.assertEquals(1 + (x.length - 1)
          + (((x.length - 1) * (x.length - 2)) / 2), d);
    }
    for (final int d : twice) {
      Assert.assertEquals(x.length - 1, d);
    }
    for (final int d : thrice) {
      Assert.assertEquals(((x.length - 1) * (x.length - 2)) / 2,
          d);
    }
  }
}
