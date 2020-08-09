package aitoa.utils.math;

import java.util.Objects;
import java.util.Random;

/**
 * A random distribution drawing numbers from another
 * distribution and re-sampling the numbers if they are less than
 * one.
 */
public final class DiscreteGreaterThanZero
    extends DiscreteRandomDistribution {

  /** the inner distribution */
  private final DiscreteRandomDistribution mInner;

  /**
   * Create the distribution
   *
   * @param pInner
   *          the inner distribution
   */
  public DiscreteGreaterThanZero(
      final DiscreteRandomDistribution pInner) {
    super();
    this.mInner = Objects.requireNonNull(pInner);
  }

  /** {@inheritDoc} */
  @Override
  public int nextInt(final Random random) {
    for (;;) {
      final int res = this.mInner.nextInt(random);
      if (res > 0) {
        return res;
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.mInner.toString() + "_gt0"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return this.mInner.hashCode() ^ 0x93245878;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o instanceof DiscreteGreaterThanZero) {
      return this.mInner
          .equals(((DiscreteGreaterThanZero) o).mInner);
    }
    return false;
  }
}
