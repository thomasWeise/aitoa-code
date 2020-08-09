package aitoa.utils.math;

import java.util.Random;

/**
 * A constant distribution always yields the same number
 */
public final class DiscreteConstant
    extends DiscreteRandomDistribution {
  /** the value to return */
  public final int m;

  /**
   * create the distribution
   *
   * @param pM
   *          the value to return
   */
  public DiscreteConstant(final int pM) {
    super();
    this.m = pM;
  }

  /** {@inheritDoc} */
  @Override
  public int nextInt(final Random random) {
    return this.m;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ("C=" + this.m); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return this.m;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o instanceof DiscreteConstant) {
      return this.m == ((DiscreteConstant) o).m;
    }
    return false;
  }
}
