package aitoa.searchSpaces.bitstrings;

import aitoa.utils.math.BinomialDistribution;
import aitoa.utils.math.DiscreteGreaterThanZero;

/**
 * A unary operator for flipping each bit with a certain
 * probability (and repeat this until at least one bit was
 * flipped). This operator is equivalent to the
 * {@link BitStringUnaryOperatorMOverNFlip} but should be faster
 * for larger bit strings. This operator, plugged into a
 * {@linkplain aitoa.algorithms.EA1p1 (1+1)-EA} will be
 * equivalent to a {@code (1+1)-EA>0}, as discussed in E.
 * Carvalho Pinto and C. Doerr, "Towards a more practice-aware
 * runtime analysis of evolutionary algorithms," July 2017,
 * arXiv:1812.00493v1 [cs.NE] 3 Dec 2018. [Online]. Available:
 * http://arxiv.org/pdf/1812.00493.pdf.
 */
public final class BitStringUnaryOperatorMOverNFlipDist
    extends BitStringUnaryOperatorFlipWithDist {

  /** the multiplier */
  private final int mM;

  /**
   * create the unary operator
   *
   * @param pN
   *          the number of bits that could be flipped
   * @param pM
   *          the multiplier
   */
  public BitStringUnaryOperatorMOverNFlipDist(final int pN,
      final int pM) {
    super(pN, new DiscreteGreaterThanZero(
        new BinomialDistribution(pN, ((double) pM) / pN)));
    if (pM <= 0) {
      throw new IllegalArgumentException(
          "bit flip multiplier must be at least 1, but is " //$NON-NLS-1$
              + pM + //
              ", which would mean a zero probability to flip bits"); //$NON-NLS-1$
    }
    this.mM = pM;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.mM + "/n-flipD"; //$NON-NLS-1$
  }
}
