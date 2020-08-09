package aitoa.examples.bitstrings;

/**
 * The well-known OneMax problem: The goal is to maximize the
 * number of {@code true} bits in a bit string, which we can
 * transform into a minimization problem by minimizing the number
 * of {@code false} bits.
 */
public final class OneMaxObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "OneMax"; //$NON-NLS-1$

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public OneMaxObjectiveFunction(final int _n) {
    super(_n);
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public OneMaxObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        .parseN(OneMaxObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final boolean[] y) {
    int s = 0;
    for (final boolean b : y) {
      if (b) {
        continue;
      }
      ++s;
    }
    return s;
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
    return BitStringObjectiveFunction
        .makeNameN(OneMaxObjectiveFunction.NAME_PREFIX, this.n);
  }
}
