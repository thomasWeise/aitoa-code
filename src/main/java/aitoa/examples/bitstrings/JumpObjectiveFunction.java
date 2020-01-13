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
  public static final String NAME_PREFIX = "Jump_"; //$NON-NLS-1$

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
    this(JumpObjectiveFunction.__nk(s));
  }

  /**
   * get the {@link BitStringObjectiveFunction#n} and the
   * {@link #k} from the instance name
   *
   * @param name
   *          the name
   * @return the instance scale
   */
  private static final int[] __nk(final String name) {
    if (!name.startsWith(JumpObjectiveFunction.NAME_PREFIX)) {
      throw new IllegalArgumentException("Invalid name " + name); //$NON-NLS-1$
    }
    final int li = name.lastIndexOf('_');
    if (li <= JumpObjectiveFunction.NAME_PREFIX.length()) {
      throw new IllegalArgumentException("Invalid name " + name); //$NON-NLS-1$
    }
    return new int[] { Integer.parseInt(//
        name.substring(
            JumpObjectiveFunction.NAME_PREFIX.length(), li)), //
        Integer.parseInt(//
            name.substring(li + 1)) };
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
    return (((JumpObjectiveFunction.NAME_PREFIX + this.n) + '_')
        + this.k);
  }
}
