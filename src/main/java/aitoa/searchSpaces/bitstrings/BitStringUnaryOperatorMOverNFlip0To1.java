package aitoa.searchSpaces.bitstrings;

import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * A unary operator for flipping each bit with a certain
 * probability which flips one bit if none was chosen. This
 * operator, plugged into a {@linkplain aitoa.algorithms.EA1p1
 * (1+1)-EA} will be equivalent to a {@code (1+1)-EA_0->1}, as
 * discussed in E. Carvalho Pinto and C. Doerr, "Towards a more
 * practice-aware runtime analysis of evolutionary algorithms,"
 * July 2017, arXiv:1812.00493v1 [cs.NE] 3 Dec 2018. [Online].
 * Available: http://arxiv.org/pdf/1812.00493.pdf.
 */
public final class BitStringUnaryOperatorMOverNFlip0To1
    implements IUnarySearchOperator<boolean[]> {

  /** the multiplier */
  private final int m_m;

  /**
   * create the unary operator
   *
   * @param _m
   *          the multiplier
   */
  public BitStringUnaryOperatorMOverNFlip0To1(final int _m) {
    super();
    if (_m <= 0) {
      throw new IllegalArgumentException(
          "bit flip multiplier must be at least 1, but is " //$NON-NLS-1$
              + _m + //
              ", which would mean a zero probability to flip bits"); //$NON-NLS-1$
    }
    this.m_m = _m;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return this.m_m + "/n-flip_01"; //$NON-NLS-1$
  }

  /**
   * Sample a point from the neighborhood of {@code x} by
   * flipping each bit inside of {@code x} with probability m/n.
   *
   * @param x
   *          {@inheritDoc}
   * @param dest
   *          {@inheritDoc}
   * @param random
   *          {@inheritDoc}
   */
  @Override
  public final void apply(final boolean[] x,
      final boolean[] dest, final Random random) {
    final int n = x.length;
    boolean neeedFlip = true;

    System.arraycopy(x, 0, dest, 0, n);

    for (int i = n; (--i) >= 0;) {
      if (random.nextInt(n) < this.m_m) {
        dest[i] ^= true;
        neeedFlip = false;
      }
    }
    if (neeedFlip) {
      dest[random.nextInt(n)] ^= true;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean canEnumerate() {
    return false;
  }
}
