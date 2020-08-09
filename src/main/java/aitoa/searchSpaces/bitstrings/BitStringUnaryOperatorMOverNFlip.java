package aitoa.searchSpaces.bitstrings;

import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * A unary operator for flipping each bit with a certain
 * probability (and repeat this until at least one bit was
 * flipped). This operator, plugged into a
 * {@linkplain aitoa.algorithms.EA1p1 (1+1)-EA} will be
 * equivalent to a {@code (1+1)-EA>0}, as discussed in E.
 * Carvalho Pinto and C. Doerr, "Towards a more practice-aware
 * runtime analysis of evolutionary algorithms," July 2017,
 * arXiv:1812.00493v1 [cs.NE] 3 Dec 2018. [Online]. Available:
 * http://arxiv.org/pdf/1812.00493.pdf.
 */
public final class BitStringUnaryOperatorMOverNFlip
    implements IUnarySearchOperator<boolean[]> {

  /** the multiplier */
  private final int mM;

  /**
   * create the unary operator
   *
   * @param pM
   *          the multiplier
   */
  public BitStringUnaryOperatorMOverNFlip(final int pM) {
    super();
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
    return this.mM + "/n-flip"; //$NON-NLS-1$
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
  public void apply(final boolean[] x, final boolean[] dest,
      final Random random) {
    final int n = x.length;
    boolean done = false;

    System.arraycopy(x, 0, dest, 0, n);
    do {
      for (int i = n; (--i) >= 0;) {
        if (random.nextInt(n) < this.mM) {
          dest[i] ^= true;
          done = true;
        }
      }
    } while (!done);
  }

  /** {@inheritDoc} */
  @Override
  public boolean canEnumerate() {
    return false;
  }
}
