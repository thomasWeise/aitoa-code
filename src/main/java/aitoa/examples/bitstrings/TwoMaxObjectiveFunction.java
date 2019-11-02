package aitoa.examples.bitstrings;

import aitoa.structure.IObjectiveFunction;

/**
 * The TwoMax problem as defined in "Escaping large deceptive
 * basins of attraction with heavy-tailed mutation operators,"
 * July 2018, DOI: 10.1145/3205455.3205515, just inverted to a
 * minimization problems.
 */
public final class TwoMaxObjectiveFunction
    implements IObjectiveFunction<boolean[]> {

  /** the length of the bit string */
  public final int n;

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public TwoMaxObjectiveFunction(final int _n) {
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
    int om = 0;
    for (final boolean b : y) {
      if (b) {
        ++om;
      }
    }
    if (om == this.n) {
      return 0;
    }
    return 1 + this.n - Math.max(om, this.n - om);
  }

  /** {@inheritDoc} */
  @Override
  public final double lowerBound() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public final double upperBound() {
    return this.n + 1;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "TwoMax_" + this.n; //$NON-NLS-1$
  }
}
