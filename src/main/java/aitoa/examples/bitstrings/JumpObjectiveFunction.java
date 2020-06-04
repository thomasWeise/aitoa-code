package aitoa.examples.bitstrings;

/**
 * the jump objective function as defined in "Escaping large
 * deceptive basins of attraction with heavy-tailed mutation
 * operators," July 2018, DOI: 10.1145/3205455.3205515, turned to
 * a minimization problem.
 * <p>
 * In this definition, points near the global optimum are
 * deceptive. In other definitions, they are neutral.
 */
public final class JumpObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "Jump"; //$NON-NLS-1$

  /** the k */
  public final int k;
  /** the n-minus-k */
  public final int nMinusk;

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   * @param _k
   *          the length of the deceptive bit
   */
  public JumpObjectiveFunction(final int _n, final int _k) {
    super(_n);
    if ((_k <= 1) || (_k >= (_n >>> 1))) {
      throw new IllegalArgumentException(
          "k must be greater than 1 and less than half of n, but we got k=" //$NON-NLS-1$
              + _k + " and n=" + _n);//$NON-NLS-1$
    }
    this.k = _k;
    this.nMinusk = (_n - _k);
  }

  /**
   * create
   *
   * @param args
   *          the arguments
   */
  private JumpObjectiveFunction(final int[] args) {
    this(args[0], args[1]);
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public JumpObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        ._parse_nk(JumpObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final boolean[] y) {
    int res = 0;
    for (final boolean b : y) {
      if (b) {
        ++res;
      }
    }

    if ((res >= this.n) || (res <= this.nMinusk)) {
      return (this.n - res);
    }

    return (this.k + res);
  }

  /** {@inheritDoc} */
  @Override
  public double lowerBound() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public double upperBound() {
    return (this.n + this.k);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return BitStringObjectiveFunction._make_name_nk(
        JumpObjectiveFunction.NAME_PREFIX, this.n, this.k);
  }
}
