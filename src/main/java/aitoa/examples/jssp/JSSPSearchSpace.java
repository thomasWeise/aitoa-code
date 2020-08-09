package aitoa.examples.jssp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

import aitoa.structure.ISpace;
import aitoa.utils.math.BigMath;

/**
 * An implementation of the space interface for the search space
 * of our JSSP
 */
// start relevant
public final class JSSPSearchSpace implements ISpace<int[]> {
// end relevant
  /** the problem instance */
  public final JSSPInstance instance;
  /** the length */
  private final int mLength;

  /**
   * create
   *
   * @param pInstance
   *          the problem instance
   */
  public JSSPSearchSpace(final JSSPInstance pInstance) {
    super();
    this.instance = Objects.requireNonNull(pInstance);
    this.mLength = (pInstance.m * pInstance.n);
  }

  /**
   * Load the space from a given instance name
   *
   * @param pInstance
   *          the instance name
   */
  public JSSPSearchSpace(final String pInstance) {
    this(new JSSPInstance(pInstance));
  }

// start relevant
  /**
   * create an empty instance
   *
   * @return the empty instance
   */
  @Override
  public int[] create() {
    return new int[this.mLength];
  }

  /** {@inheritDoc} */
  @Override
  public void copy(final int[] from, final int[] to) {
    System.arraycopy(from, 0, to, 0, this.mLength);
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public void print(final int[] z, final Appendable out)
      throws IOException {
    out.append("new int[] "); //$NON-NLS-1$
    char ch = '{';
    for (final int i : z) {
      out.append(ch);
      ch = ',';
      out.append(Integer.toString(i));
    }
    out.append('}');
  }

  /** {@inheritDoc} */
  @Override
  public void check(final int[] z) {
    final int[] times = new int[this.instance.n];
    for (final int i : z) {
      times[i]++;
    }
    for (final int i : times) {
      if (i != this.instance.m) {
        throw new IllegalArgumentException(//
            "Some elements in " + //$NON-NLS-1$
                Arrays.toString(z) + //
                " do not occur " + //$NON-NLS-1$
                this.instance.m + //
                " times."); //$NON-NLS-1$
      }
    }
  }

  /**
   * Compute the scale of the search space. This is base-2
   * logarithm of the size of the search space.
   *
   * @return the solution space scale
   */
  @Override
  public double getScale() {
    final BigInteger mm = BigInteger.valueOf(this.instance.m);
    final BigInteger nn = BigInteger.valueOf(this.instance.n);

    final BigInteger upper = BigMath.factorial(mm.multiply(nn));
    final BigInteger lower =
        BigMath.factorial(mm).pow(this.instance.n);

    return BigMath.ld(upper.divide(lower));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ((("jssp:int[" + //$NON-NLS-1$
        this.mLength) + "]:") //$NON-NLS-1$
        + this.instance.toString());
  }
// start relevant
}
// end relevant
