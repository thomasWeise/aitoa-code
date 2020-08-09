package aitoa.searchSpaces.bitstrings;

import java.util.Objects;
import java.util.Random;

import aitoa.structure.IUnarySearchOperator;
import aitoa.utils.math.DiscreteRandomDistribution;

/**
 * A base class for operators based on the thoughts of E.
 * Carvalho Pinto and C. Doerr given in "Towards a more
 * practice-aware runtime analysis of evolutionary algorithms,"
 * July 2017, arXiv:1812.00493v1 [cs.NE] 3 Dec 2018. [Online].
 * Available: http://arxiv.org/pdf/1812.00493.pdf.
 */
public abstract class BitStringUnaryOperatorFlipWithDist
    implements IUnarySearchOperator<boolean[]> {

  /** the index list */
  private final int[] mIndexes;

  /** the number of bits in the representation */
  protected final int mN;

  /** the distribution to sample from */
  private final DiscreteRandomDistribution mDistribution;

  /**
   * create the unary operator
   *
   * @param pN
   *          the number of bits that could be flipped
   * @param pDistribution
   *          the discrete random distribution to use
   */
  protected BitStringUnaryOperatorFlipWithDist(final int pN,
      final DiscreteRandomDistribution pDistribution) {
    super();
    if (pN <= 0) {
      throw new IllegalArgumentException(
          "there must be at least 1 bit to flip, but you specified " //$NON-NLS-1$
              + pN);
    }
    this.mDistribution = Objects.requireNonNull(pDistribution);
    this.mN = pN;
    this.mIndexes = new int[pN];
    for (int i = pN; (--i) >= 0;) {
      this.mIndexes[i] = i;
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.mDistribution + "_flip"; //$NON-NLS-1$
  }

  /**
   * Flip the bits
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
    final int[] indexes = this.mIndexes;
    final int _n = this.mN;
    System.arraycopy(x, 0, dest, 0, _n);

    final int flip = this.mDistribution.nextInt(random);

    // shuffle the first flip elements in a Fisher-Yates style
    for (int i = 0; i < flip; i++) {
      final int j = i + random.nextInt(_n - i);
      final int t = indexes[j];
      indexes[j] = indexes[i];
      indexes[i] = t;
    }

    // perform the flips
    for (int i = flip; (--i) >= 0;) {
      dest[indexes[i]] ^= true;
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean canEnumerate() {
    return false;
  }
}
