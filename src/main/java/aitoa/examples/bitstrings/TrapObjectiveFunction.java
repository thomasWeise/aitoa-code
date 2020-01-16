package aitoa.examples.bitstrings;

/**
 * The trap function basically is similar to the
 * {@linkplain OneMaxObjectiveFunction OneMax problem}, except
 * that it puts the global optimum at the all-0 string. However,
 * following a trail of increasing fitness will still lead you to
 * the all-1 string.
 * <p>
 * Droste, S., Jansen, T., and Wegener, I. (2002). On the
 * analysis of the (1+1) evolutionary algo-rithm.Theoretical
 * Computer Science, 276(1-2):51–81.
 * https://doi.org/10.1016/S0304-3975(01)00182-7
 */
public final class TrapObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "Trap_"; //$NON-NLS-1$

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public TrapObjectiveFunction(final int _n) {
    super(_n);
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public TrapObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        ._parse_n(TrapObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public final double evaluate(final boolean[] y) {
    int s = 0;
    for (final boolean b : y) {
      if (b) {
        continue;
      }
      ++s;
    }
    return (s >= y.length) ? 0 : (s + 1);
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
    return TrapObjectiveFunction.NAME_PREFIX + this.n;
  }
}
