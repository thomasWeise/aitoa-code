package aitoa.utils.math;

import java.util.Objects;
import java.util.Random;

/**
 * A random distribution drawing numbers from another
 * distribution and returning 1 for all values not greater than
 * zero
 */
public final class DiscreteZeroToOne
    extends DiscreteRandomDistribution {

  /** the inner distribution */
  private final DiscreteRandomDistribution m_inner;

  /**
   * Create the distribution
   *
   * @param inner
   *          the inner distribution
   */
  public DiscreteZeroToOne(
      final DiscreteRandomDistribution inner) {
    super();
    this.m_inner = Objects.requireNonNull(inner);
  }

  /** {@inheritDoc} */
  @Override
  public final int nextInt(final Random random) {
    final int res = this.m_inner.nextInt(random);
    return (res > 0) ? res : 1;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return this.m_inner.toString() + "_0to1"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return this.m_inner.hashCode() ^ 0xdf24a157;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o instanceof DiscreteZeroToOne) {
      return this.m_inner
          .equals(((DiscreteZeroToOne) o).m_inner);
    }
    return false;
  }
}
