package aitoa.examples.jssp;

import java.util.Random;

import aitoa.structure.INullarySearchOperator;
import aitoa.utils.RandomUtils;

/**
 * An implementation of the nullary search operator for the JSSP
 * representation.
 */
// start relevant
public final class JSSPNullaryOperator
    implements INullarySearchOperator<int[]> {
// end relevant

  /** the number of jobs */
  private final int n;

  /**
   * create the representation
   *
   * @param pInstance
   *          the problem instance
   */
  public JSSPNullaryOperator(final JSSPInstance pInstance) {
    super();
    this.n = pInstance.n;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "uniform"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
// start relevant
  public void apply(final int[] dest, final Random random) {
// create first sequence of jobs: n-1, n-2, ..., 0
    for (int i = this.n; (--i) >= 0;) {
      dest[i] = i;
    }
// copy this m-1 times: n-1, n-2, ..., 0, n-1, ... 0, n-1, ...
    for (int i = dest.length; (i -= this.n) > 0;) {
      System.arraycopy(dest, 0, dest, i, this.n);
    }
// now randomly shuffle the array: create a random sequence
    RandomUtils.shuffle(random, dest, 0, dest.length);
  }
}
// end relevant
