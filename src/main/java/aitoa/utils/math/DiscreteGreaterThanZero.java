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
  private final DiscreteRandomDistribution m_inner;

  /**
   * Create the distribution
   *
   * @param inner
   *          the inner distribution
   */
  public DiscreteGreaterThanZero(
      final DiscreteRandomDistribution inner) {
    super();
    this.m_inner = Objects.requireNonNull(inner);
  }

  /** {@inheritDoc} */
  @Override
  public final int nextInt(final Random random) {
    for (;;) {
      final int res = this.m_inner.nextInt(random);
      if (res > 0) {
        return res;
      }
    }
  }
}
