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

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public LeadingOnesObjectiveFunction(final int _n) {
    super(_n);
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
    return "LeadingOnes_" + this.n; //$NON-NLS-1$
  }
}
