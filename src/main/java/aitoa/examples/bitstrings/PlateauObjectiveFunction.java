package aitoa.examples.bitstrings;

/**
 * the plateau objective function as defined in Precise Runtime
 * Analysis for Plateaus, http://export.arxiv.org/pdf/1806.01331,
 * turned to a minimization problem.
 */
public final class PlateauObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "Plateau_"; //$NON-NLS-1$

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
  public PlateauObjectiveFunction(final int _n, final int _k) {
    super(_n);
    if ((_k > 0) && (_k >= (_n >>> 1))) {
      throw new IllegalArgumentException(
          "k must be greater than 0 and less than half of n, but we got k=" //$NON-NLS-1$
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
  private PlateauObjectiveFunction(final int[] args) {
    this(args[0], args[1]);
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public PlateauObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        ._parse_nk(PlateauObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public final double evaluate(final boolean[] y) {
    int res = 0;
    for (final boolean b : y) {
      if (b) {
        ++res;
      }
    }

    if ((res >= this.n) || (res <= this.nMinusk)) {
      return (this.n - res);
    }

    return this.k;
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
    return (((PlateauObjectiveFunction.NAME_PREFIX + this.n)
        + '_') + this.k);
  }
}
