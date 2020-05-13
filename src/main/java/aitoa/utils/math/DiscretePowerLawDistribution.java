package aitoa.utils.math;

import java.util.Random;

/**
 * A class for sampling the discrete power law distribution, a
 * discretized version of the code found at
 * https://stackoverflow.com/questions/918736
 */
public final class DiscretePowerLawDistribution
    extends DiscreteRandomDistribution {

  /** the minimal value (inclusive) */
  public final int minInclusive;

  /** the maximal value (exclusive) */
  public final int maxExclusive;

  /** the alpha parameter */
  public final double alpha;

  /** the minimum power */
  private final double m_powMin;

  /** the difference between the maximum and minimum power */
  private final double m_powMaxMinDiv;

  /** the fraction */
  private final double m_frac;

  /**
   * create the distribution
   *
   * @param _minInclusive
   *          the inclusive minimum
   * @param _maxExclusive
   *          the inclusive maximum
   * @param _alpha
   *          the alpha parameter
   */
  public DiscretePowerLawDistribution(final int _minInclusive,
      final int _maxExclusive, final double _alpha) {
    super();

    if (_minInclusive <= 0) {
      throw new IllegalArgumentException(
          "Minimum must be > 0, but is " //$NON-NLS-1$
              + _minInclusive);
    }
    if (_minInclusive >= _maxExclusive) {
      throw new IllegalArgumentException(("min (" + //$NON-NLS-1$
          _minInclusive + ") must be less than max (" //$NON-NLS-1$
          + _maxExclusive) + ')');// $NON-NLS-1$
    }
    if ((_alpha < 0d) || (!Double.isFinite(_alpha))) {
      throw new IllegalArgumentException(
          "alpha must be positive, but is " //$NON-NLS-1$
              + _alpha);
    }

    this.minInclusive = _minInclusive;
    this.maxExclusive = _maxExclusive;
    this.alpha = _alpha;

    this.m_powMin = Math.pow(_minInclusive, ((-_alpha) + 1d));
    this.m_powMaxMinDiv =
        Math.pow(_maxExclusive, ((-_alpha) + 1d))
            - this.m_powMin;
    this.m_frac = 1d / ((-_alpha) + 1d);

    if (!(Double.isFinite(this.m_powMin)
        && Double.isFinite(this.m_powMaxMinDiv)
        && Double.isFinite(this.m_frac))) {
      throw new IllegalArgumentException(((((("The combination [" //$NON-NLS-1$
          + _minInclusive) + ',') + _maxExclusive) + ',')
          + _alpha) + "] does not work.");//$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public int nextInt(final Random random) {
    return ((int) (Math.pow(//
        ((this.m_powMaxMinDiv * random.nextDouble())
            + this.m_powMin),
        this.m_frac)));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return (((((("Pow(" + this.minInclusive) + ',') //$NON-NLS-1$
        + this.maxExclusive) + ',') + this.alpha) + ')');
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return ((31 * (31 * Integer.hashCode(this.minInclusive)))
        + Integer.hashCode(this.maxExclusive))
        + Double.hashCode(this.alpha);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o instanceof DiscretePowerLawDistribution) {
      final DiscretePowerLawDistribution b =
          ((DiscretePowerLawDistribution) o);
      return (this.minInclusive == b.minInclusive)
          && (this.maxExclusive == b.maxExclusive)
          && (Double.compare(this.alpha, b.alpha) == 0);
    }
    return false;
  }
}
