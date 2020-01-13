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
  public static final String NAME_PREFIX = "TwoMax_"; //$NON-NLS-1$

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
    this(TwoMaxObjectiveFunction.__n(s));
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
    if (!name.startsWith(TwoMaxObjectiveFunction.NAME_PREFIX)) {
      throw new IllegalArgumentException("Invalid name " + name); //$NON-NLS-1$
    }
    return Integer.parseInt(//
        name.substring(
            TwoMaxObjectiveFunction.NAME_PREFIX.length()));
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
    return TwoMaxObjectiveFunction.NAME_PREFIX + this.n;
  }
}
