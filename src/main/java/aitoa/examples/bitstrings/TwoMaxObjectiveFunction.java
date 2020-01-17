package aitoa.examples.bitstrings;

/**
 * The TwoMax problem as defined in "Escaping large deceptive
 * basins of attraction with heavy-tailed mutation operators,"
 * July 2018, DOI: 10.1145/3205455.3205515, just inverted to a
 * minimization problems.
 */
public final class TwoMaxObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "TwoMax"; //$NON-NLS-1$

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public TwoMaxObjectiveFunction(final int _n) {
    super(_n);
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public TwoMaxObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        ._parse_n(TwoMaxObjectiveFunction.NAME_PREFIX, s));
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
    return (1 + this.n) - Math.max(om, this.n - om);
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
    return BitStringObjectiveFunction._make_name_n(
        TwoMaxObjectiveFunction.NAME_PREFIX, this.n);
  }
}
