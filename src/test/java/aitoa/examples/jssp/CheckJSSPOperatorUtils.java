package aitoa.examples.jssp;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;

import aitoa.utils.RandomUtils;

/** A base class for testing JSSP operators */
@Ignore
public class CheckJSSPOperatorUtils {

  /** the instances */
  public static final JSSPInstance[] INSTANCS = { //
      new JSSPInstance("abz7"), //$NON-NLS-1$
      new JSSPInstance("la24"), //$NON-NLS-1$
      new JSSPInstance("yn4"), //$NON-NLS-1$
      new JSSPInstance("swv15") };//$NON-NLS-1$

  /**
   * assert a point in the search space
   *
   * @param x
   *          the point in the search space
   * @param inst
   *          the point in the solution space
   */
  public static final void assertX(final int[] x,
      final JSSPInstance inst) {
    final int[] count = new int[inst.n];
    for (final int i : x) {
      ++count[i];
    }
    for (final int c : count) {
      Assert.assertEquals(inst.m, c);
    }
  }

  /**
   * create a canonical point in the search space
   *
   * @param x
   *          the destination point in the search space
   * @param inst
   *          the point in the solution space
   */
  public static final void canonicalX(final int[] x,
      final JSSPInstance inst) {
    int i = 0;
    for (int m = 0; m < inst.m; m++) {
      for (int n = 0; n < inst.n; n++) {
        x[i++] = n;
      }
    }
    Assert.assertEquals(i, x.length);
  }

  /**
   * create a random point in the search space
   *
   * @param x
   *          the destination point in the search space
   * @param inst
   *          the point in the solution space
   */
  public static final void randomX(final int[] x,
      final JSSPInstance inst) {
    CheckJSSPOperatorUtils.canonicalX(x, inst);
    RandomUtils.shuffle(ThreadLocalRandom.current(), x, 0,
        x.length);
  }
}
