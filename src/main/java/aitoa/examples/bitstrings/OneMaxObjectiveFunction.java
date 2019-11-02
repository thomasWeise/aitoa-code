package aitoa.examples.bitstrings;

import aitoa.structure.IObjectiveFunction;

/**
 * The well-known OneMax problem: The goal is to maximize the
 * number of {@code true} bits in a bit string, which we can
 * transform into a minimization problem by minimizing the number
 * of {@code false} bits.
 */
public final class OneMaxObjectiveFunction
    implements IObjectiveFunction<boolean[]> {

  /** the length of the bit string */
  public final int n;

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public OneMaxObjectiveFunction(final int _n) {
    super();
    if (_n <= 0) {
      throw new IllegalArgumentException(
          "n must be at least one, but is " //$NON-NLS-1$
              + _n);
    }
    this.n = _n;
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
