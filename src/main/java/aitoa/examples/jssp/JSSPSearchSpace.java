package aitoa.examples.jssp;

import aitoa.structure.ISpace;

/**
 * An implementation of the space interface for the search space
 * of our JSSP
 */
public final class JSSPSearchSpace implements ISpace<int[]> {

  /** the length of the strings */
  private final int m_length;

  /**
   * create
   *
   * @param inst
   *          the problem instance
   */
  public JSSPSearchSpace(final JSSPInstance inst) {
    super();
    this.m_length = inst.m * inst.n;
  }

  /**
   * create an empty instance
   *
   * @return the empty instance
   */
  @Override
  public final int[] create() {
    return new int[this.m_length];
  }

  /** {@inheritDoc} */
  @Override
  public final void copy(final int[] from, final int[] to) {
    System.arraycopy(from, 0, to, 0, this.m_length);
  }

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
    } catch (final Throwable error) {
      throw new RuntimeException(//
          "Error when writing int array.", //$NON-NLS-1$
          error);
    }
  }
}
