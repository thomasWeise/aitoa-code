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
   * @param pN
   *          the length of the bit string
   * @param pK
   *          the length of the deceptive bit
   */
  public JumpObjectiveFunction(final int pN, final int pK) {
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
        .parseNK(JumpObjectiveFunction.NAME_PREFIX, s));
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
    return BitStringObjectiveFunction.makeNameNK(
        JumpObjectiveFunction.NAME_PREFIX, this.n, this.k);
  }
}
