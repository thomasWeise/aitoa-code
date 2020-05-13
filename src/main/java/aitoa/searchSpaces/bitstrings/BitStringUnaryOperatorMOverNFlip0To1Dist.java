package aitoa.searchSpaces.bitstrings;

import aitoa.utils.math.BinomialDistribution;
import aitoa.utils.math.DiscreteZeroToOne;

/**
 * A unary operator for flipping each bit with a certain
 * probability and flip one bit if no bit was chosen. This
 * operator is equivalent to the
 * {@link BitStringUnaryOperatorMOverNFlip} but should be faster
 * for larger bit strings. This operator, plugged into a
 * {@linkplain aitoa.algorithms.EA1p1 (1+1)-EA} will be
 * equivalent to a {@code (1+1)-EA 0->1}, as discussed in E.
 * Carvalho Pinto and C. Doerr, "Towards a more practice-aware
 * runtime analysis of evolutionary algorithms," July 2017,
 * arXiv:1812.00493v1 [cs.NE] 3 Dec 2018. [Online]. Available:
 * http://arxiv.org/pdf/1812.00493.pdf.
 */
public final class BitStringUnaryOperatorMOverNFlip0To1Dist
    extends BitStringUnaryOperatorFlipWithDist {

  /** the multiplier */
  private final int m_m;

  /**
   * create the unary operator
   *
   * @param _n
   *          the number of bits that could be flipped
   * @param _m
   *          the multiplier
   */
  public BitStringUnaryOperatorMOverNFlip0To1Dist(final int _n,
      final int _m) {
    super(_n, new DiscreteZeroToOne(
        new BinomialDistribution(_n, ((double) _m) / _n)));
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
  public String toString() {
    return this.m_m + "/n-flip01D"; //$NON-NLS-1$
  }
}
