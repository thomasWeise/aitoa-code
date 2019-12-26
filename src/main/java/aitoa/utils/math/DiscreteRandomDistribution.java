package aitoa.utils.math;

import java.util.Random;

/** A random distribution that can be sampled */
public abstract class DiscreteRandomDistribution {

  /**
   * Sample the next integer from the distribution
   *
   * @param random
   *          the random number generator
   * @return the next integer
   */
  public abstract int nextInt(final Random random);

}
