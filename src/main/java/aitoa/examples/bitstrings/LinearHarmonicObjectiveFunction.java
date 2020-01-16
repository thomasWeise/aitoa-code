package aitoa.examples.bitstrings;

/**
 * A linear function with harmonic weights, i.e., f(x) =
 * sum_i=0^n ix[i], as also used in the IOHprofiler, see
 * https://github.com/IOHprofiler/IOHexperimenter/blob/master/src/Problems/f_linear.hpp
 */
public final class LinearHarmonicObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "LinearHarmonic_"; //$NON-NLS-1$

  /** the upper bound of the objective value */
  public final long upperBound;

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public LinearHarmonicObjectiveFunction(final int _n) {
    super(_n);

    this.upperBound = (((long) _n) * (_n + 1)) >>> 1;
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public LinearHarmonicObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction._parse_n(
        LinearHarmonicObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public final double evaluate(final boolean[] y) {
    long s = this.upperBound;
    int last = y.length;
    for (int i = last; (--i) >= 0;) {
      if (y[i]) {
        s -= last;
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
    return this.upperBound;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return LinearHarmonicObjectiveFunction.NAME_PREFIX + this.n;
  }
}
