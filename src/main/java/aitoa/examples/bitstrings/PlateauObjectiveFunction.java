package aitoa.examples.bitstrings;

/**
 * the plateau objective function as defined in Precise Runtime
 * Analysis for Plateaus, http://export.arxiv.org/pdf/1806.01331,
 * turned to a minimization problem.
 */
public final class PlateauObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "Plateau"; //$NON-NLS-1$

  /** the k */
  public final int k;
  /** the n-minus-k */
  public final int nMinusk;

  /**
   * create
   *
   * @param pN
   *          the length of the bit string
   * @param pK
   *          the length of the deceptive bit
   */
  public PlateauObjectiveFunction(final int pN, final int pK) {
    super(pN);
    if ((pK <= 1) || (pK >= (pN >>> 1))) {
      throw new IllegalArgumentException(
          "k must be greater than 1 and less than half of n, but we got k=" //$NON-NLS-1$
              + pK + " and n=" + pN);//$NON-NLS-1$
    }
    this.k = pK;
    this.nMinusk = (pN - pK);
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
        .parseNK(PlateauObjectiveFunction.NAME_PREFIX, s));
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

    return this.k;
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
    return BitStringObjectiveFunction.makeNameNK(
        PlateauObjectiveFunction.NAME_PREFIX, this.n, this.k);
  }
}
