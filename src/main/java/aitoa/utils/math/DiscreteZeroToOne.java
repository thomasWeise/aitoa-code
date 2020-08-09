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
  private final DiscreteRandomDistribution mInner;

  /**
   * Create the distribution
   *
   * @param pInner
   *          the inner distribution
   */
  public DiscreteZeroToOne(
      final DiscreteRandomDistribution pInner) {
    super();
    this.mInner = Objects.requireNonNull(pInner);
  }

  /** {@inheritDoc} */
  @Override
  public int nextInt(final Random random) {
    final int res = this.mInner.nextInt(random);
    return (res > 0) ? res : 1;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.mInner.toString() + "_0to1"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return this.mInner.hashCode() ^ 0xdf24a157;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o instanceof DiscreteZeroToOne) {
      return this.mInner.equals(((DiscreteZeroToOne) o).mInner);
    }
    return false;
  }
}
