// start relevant
package aitoa.examples.jssp;

// end relevant
import java.io.IOException;
// start relevant
import java.util.Arrays;
import java.util.Objects;

import aitoa.structure.ISpace;

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
  private final int length;

  /**
   * create
   *
   * @param _instance
   *          the problem instance
   */
  public JSSPSearchSpace(final JSSPInstance _instance) {
    super();
    this.instance = Objects.requireNonNull(_instance);
    this.length = (_instance.m * _instance.n);
  }

// start relevant
  /**
   * create an empty instance
   *
   * @return the empty instance
   */
  @Override
  public final int[] create() {
    return new int[this.length];
  }

  /** {@inheritDoc} */
  @Override
  public final void copy(final int[] from, final int[] to) {
    System.arraycopy(from, 0, to, 0, this.length);
  }
// end relevant

  /** {@inheritDoc} */
  @Override
  public final void print(final int[] z, final Appendable out) {
    try {
      out.append("new int[]"); //$NON-NLS-1$
      char ch = '{';
      for (final int i : z) {
        out.append(ch);
        ch = ',';
        out.append(' ');
        out.append(Integer.toString(i));
      }
      out.append('}');
    } catch (final IOException error) {
      throw new RuntimeException(//
          "Error when writing int array.", //$NON-NLS-1$
          error);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void check(final int[] z) {
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

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return (("jssp:int[" + //$NON-NLS-1$
        this.length + "]:" //$NON-NLS-1$
        + this.instance.toString()) + ':'
        + this.getClass().getCanonicalName());
  }
// start relevant
}
// end relevant
