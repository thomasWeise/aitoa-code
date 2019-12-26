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
   * @param _m
   *          the value to return
   */
  public DiscreteConstant(final int _m) {
    super();
    this.m = _m;
  }

  /** {@inheritDoc} */
  @Override
  public final int nextInt(final Random random) {
    return this.m;
  }
}
