package aitoa.examples.jssp;

import org.junit.Assert;
import org.junit.Test;

/** A Test for the JSSP candidate solution data structure */
public class TestJSSPCandidateSolution {

  /** test the creation */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public void test_create() {
    JSSPCandidateSolution x;

    x = new JSSPCandidateSolution(0, 0);
    Assert.assertEquals(0, x.schedule.length);

    for (int m = 1; m < 10; m++) {
      for (int n = 1; n < 10; n++) {
        x = new JSSPCandidateSolution(m, n);
        Assert.assertEquals(m, x.schedule.length);
        for (final int[] mm : x.schedule) {
          Assert.assertEquals(3 * n, mm.length);
        }
      }
    }
  }
}
