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
    if ((_k > 0) && (_k >= (_n >>> 1))) {
      throw new IllegalArgumentException(
          "k must be greater than 0 and less than half of n, but we got k=" //$NON-NLS-1$
              + _k + " and n=" + _n);//$NON-NLS-1$
    }
    this.k = _k;
    this.nMinusk = (_n - _k);
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

    return (this.k + res);
  }

  /** {@inheritDoc} */
  @Override
  public final double lowerBound() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public final double upperBound() {
    return (this.n + this.k);
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ((("Jump_" + this.n) + '_') + this.k); //$NON-NLS-1$
  }
}