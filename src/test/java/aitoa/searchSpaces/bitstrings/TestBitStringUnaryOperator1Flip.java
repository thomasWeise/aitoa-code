package aitoa.searchSpaces.bitstrings;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** Test the bit string 1-flip unary operator */
@Ignore
public class TestBitStringUnaryOperator1Flip
    extends TestBitStringUnaryOperator {
  /**
   * create the unary operator test
   *
   * @param length
   *          the dimension of the bit strings
   * @param unary
   *          the unary operator
   */
  public TestBitStringUnaryOperator1Flip(final int length,
      final BitStringUnaryOperator1Flip unary) {
    super(length, unary);
  }

  /** test the enumeration */
  @Test(timeout = 3600000)
  public void testEnumerateFlip1() {
    final boolean[] x = this.createValid();
    final boolean[] y = this.getSpace().create();
    final boolean[] has = new boolean[x.length];

    this.getInstance().enumerate(ThreadLocalRandom.current(), x,
        y, (z) -> {
          boolean done = false;
          for (int i = x.length; (--i) >= 0;) {
            if (x[i] != y[i]) {
              Assert.assertFalse(done);
              done = true;
              Assert.assertFalse(has[i]);
              has[i] = true;
            }
          }
          return false;
        });

    for (final boolean d : has) {
      Assert.assertTrue(d);
    }
  }
}
