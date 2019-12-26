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
}
