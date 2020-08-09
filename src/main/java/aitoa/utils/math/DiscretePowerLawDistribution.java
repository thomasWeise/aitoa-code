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
  private final double mPowMin;

  /** the difference between the maximum and minimum power */
  private final double mPowMaxMinDiv;

  /** the fraction */
  private final double mFrac;

  /**
   * create the distribution
   *
   * @param pMinInclusive
   *          the inclusive minimum
   * @param pMaxExclusive
   *          the inclusive maximum
   * @param pAlpha
   *          the alpha parameter
   */
  public DiscretePowerLawDistribution(final int pMinInclusive,
      final int pMaxExclusive, final double pAlpha) {
    super();

    if (pMinInclusive <= 0) {
      throw new IllegalArgumentException(
          "Minimum must be > 0, but is " //$NON-NLS-1$
              + pMinInclusive);
    }
    if (pMinInclusive >= pMaxExclusive) {
      throw new IllegalArgumentException(("min (" + //$NON-NLS-1$
          pMinInclusive + ") must be less than max (" //$NON-NLS-1$
          + pMaxExclusive) + ')');// $NON-NLS-1$
    }
    if ((pAlpha < 0d) || (!Double.isFinite(pAlpha))) {
      throw new IllegalArgumentException(
          "alpha must be positive, but is " //$NON-NLS-1$
              + pAlpha);
    }

    this.minInclusive = pMinInclusive;
    this.maxExclusive = pMaxExclusive;
    this.alpha = pAlpha;

    this.mPowMin = Math.pow(pMinInclusive, ((-pAlpha) + 1d));
    this.mPowMaxMinDiv =
        Math.pow(pMaxExclusive, ((-pAlpha) + 1d)) - this.mPowMin;
    this.mFrac = 1d / ((-pAlpha) + 1d);

    if (!(Double.isFinite(this.mPowMin)
        && Double.isFinite(this.mPowMaxMinDiv)
        && Double.isFinite(this.mFrac))) {
      throw new IllegalArgumentException(((((("The combination [" //$NON-NLS-1$
          + pMinInclusive) + ',') + pMaxExclusive) + ',')
          + pAlpha) + "] does not work.");//$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public int nextInt(final Random random) {
    return ((int) (Math.pow(//
        ((this.mPowMaxMinDiv * random.nextDouble())
            + this.mPowMin),
        this.mFrac)));
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
