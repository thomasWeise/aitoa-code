// start relevant
package aitoa.examples.jssp;

import java.util.Random;

import aitoa.structure.INullarySearchOperator;
import aitoa.utils.RandomUtils;

/**
 * An implementation of the nullary search operator for the JSSP
 * representation.
 */
public final class JSSPNullaryOperator
    implements INullarySearchOperator<int[]> {
// end relevant

  /** the number of jobs */
  private final int m_n;

  /**
   * create the representation
   *
   * @param instance
   *          the problem instance
   */
  public JSSPNullaryOperator(final JSSPInstance instance) {
    super();
    this.m_n = instance.n;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ("jssp:int[]:nullary:" + //$NON-NLS-1$
        this.getClass().getCanonicalName());
  }

  /** {@inheritDoc} */
  @Override
  public final void apply(final int[] dest,
      final Random random) {
    // create first sequence of nodes
    for (int i = this.m_n; (--i) >= 0;) {
      dest[i] = i;
    }
    // copy this m-1 times
    for (int i = dest.length; (i -= this.m_n) > 0;) {
      System.arraycopy(dest, 0, dest, i, this.m_n);
    }
    RandomUtils.shuffle(random, dest, 0, dest.length);
  }
}