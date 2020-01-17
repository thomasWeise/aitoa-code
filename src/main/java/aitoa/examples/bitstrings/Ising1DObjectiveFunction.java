package aitoa.examples.bitstrings;

/**
 * The Ising model on the 1-dimensional Torus.
 */
public final class Ising1DObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "Ising1d"; //$NON-NLS-1$

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public Ising1DObjectiveFunction(final int _n) {
    super(_n);
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public Ising1DObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        ._parse_n(Ising1DObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public final double evaluate(final boolean[] y) {
    int last = 0;
    int s = y.length;
    for (int i = s; (--i) >= 0;) {
      if (y[i] == y[last]) {
        --s;
      }
      last = i;
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
    return BitStringObjectiveFunction._make_name_n(
        Ising1DObjectiveFunction.NAME_PREFIX, this.n);
  }
}
