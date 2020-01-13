package aitoa.examples.bitstrings;

/**
 * The well-known LeadingOnes problem: The goal is to maximize
 * the number of {@code true} bits at the beginning of the bit
 * string, which we can transform into a minimization problem by
 * subtracting the value of leadings ones from the total number
 * of bits.
 */
public final class LeadingOnesObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "LeadingOnes_"; //$NON-NLS-1$

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public LeadingOnesObjectiveFunction(final int _n) {
    super(_n);
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public LeadingOnesObjectiveFunction(final String s) {
    this(LeadingOnesObjectiveFunction.__n(s));
  }

  /**
   * get the {@link BitStringObjectiveFunction#n} from the
   * instance name
   *
   * @param name
   *          the name
   * @return the instance scale
   */
  private static final int __n(final String name) {
    if (!name
        .startsWith(LeadingOnesObjectiveFunction.NAME_PREFIX)) {
      throw new IllegalArgumentException("Invalid name " + name); //$NON-NLS-1$
    }
    return Integer.parseInt(//
        name.substring(
            LeadingOnesObjectiveFunction.NAME_PREFIX.length()));
  }

  /** {@inheritDoc} */
  @Override
  public final double evaluate(final boolean[] y) {
    int s = 0;
    for (final boolean b : y) {
      if (!b) {
        break;
      }
      ++s;
    }
    return (this.n - s);
  }

  /** {@inheritDoc} */
  @Override
  public final double lowerBound() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public final double upperBound() {
    return this.n;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return LeadingOnesObjectiveFunction.NAME_PREFIX + this.n;
  }
}
