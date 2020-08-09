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
  public static final String NAME_PREFIX = "LeadingOnes"; //$NON-NLS-1$

  /**
   * create
   *
   * @param pN
   *          the length of the bit string
   */
  public LeadingOnesObjectiveFunction(final int pN) {
    super(pN);
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public LeadingOnesObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        .parseN(LeadingOnesObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final boolean[] y) {
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
  public double lowerBound() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public double upperBound() {
    return this.n;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return BitStringObjectiveFunction.makeNameN(
        LeadingOnesObjectiveFunction.NAME_PREFIX, this.n);
  }
}
