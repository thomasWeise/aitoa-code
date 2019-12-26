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
  private final int[] m_indexes;

  /** the number of bits in the representation */
  protected final int n;

  /** the distribution to sample from */
  private final DiscreteRandomDistribution m_dist;

  /**
   * create the unary operator
   *
   * @param _n
   *          the number of bits that could be flipped
   * @param _dist
   *          the discrete random distribution to use
   */
  protected BitStringUnaryOperatorFlipWithDist(final int _n,
      final DiscreteRandomDistribution _dist) {
    super();
    if (_n <= 0) {
      throw new IllegalArgumentException(
          "there must be at least 1 bit to flip, but you specified " //$NON-NLS-1$
              + _n);
    }
    this.m_dist = Objects.requireNonNull(_dist);
    this.n = _n;
    this.m_indexes = new int[_n];
    for (int i = _n; (--i) >= 0;) {
      this.m_indexes[i] = i;
    }
  }

  /** {@inheritDoc} */
  @Override
  public abstract String toString();

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
    final int[] indexes = this.m_indexes;
    final int _n = this.n;
    System.arraycopy(x, 0, dest, 0, _n);

    final int flip = this.m_dist.nextInt(random);

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
