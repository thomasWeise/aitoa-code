package aitoa.examples.bitstrings;

/**
 * A linear function with harmonic weights, i.e., f(x) =
 * sum_i=0^n ix[i], as also used in the IOHprofiler, see
 * https://github.com/IOHprofiler/IOHexperimenter/blob/master/src/Problems/f_linear.hpp
 */
public final class LinearHarmonicObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "LinearHarmonic"; //$NON-NLS-1$

  /** the upper bound of the objective value */
  public final long upperBound;

  /**
   * create
   *
   * @param pN
   *          the length of the bit string
   */
  public LinearHarmonicObjectiveFunction(final int pN) {
    super(pN);

    this.upperBound = (((long) pN) * (pN + 1)) >>> 1;
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public LinearHarmonicObjectiveFunction(final String s) {
    this(BitStringObjectiveFunction
        .parseN(LinearHarmonicObjectiveFunction.NAME_PREFIX, s));
  }

  /** {@inheritDoc} */
  @Override
  public double evaluate(final boolean[] y) {
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
  public double lowerBound() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public double upperBound() {
    return this.upperBound;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return BitStringObjectiveFunction.makeNameN(
        LinearHarmonicObjectiveFunction.NAME_PREFIX, this.n);
  }
}
