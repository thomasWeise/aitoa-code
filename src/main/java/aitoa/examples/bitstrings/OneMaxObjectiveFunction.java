package aitoa.examples.bitstrings;

/**
 * The well-known OneMax problem: The goal is to maximize the
 * number of {@code true} bits in a bit string, which we can
 * transform into a minimization problem by minimizing the number
 * of {@code false} bits.
 */
public final class OneMaxObjectiveFunction
    extends BitStringObjectiveFunction {

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public OneMaxObjectiveFunction(final int _n) {
    super(_n);
  }

  /** {@inheritDoc} */
  @Override
  public final double evaluate(final boolean[] y) {
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
    return "OneMax_" + this.n; //$NON-NLS-1$
  }
}
