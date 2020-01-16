package aitoa.examples.bitstrings;

import aitoa.searchSpaces.bitstrings.BitStringSpace;
import aitoa.structure.IObjectiveFunction;

/**
 * An objective function for bit strings
 */
public abstract class BitStringObjectiveFunction
    implements IObjectiveFunction<boolean[]> {

  /** the length of the bit string */
  public final int n;

  /**
   * Create the bit string objective function / space
   *
   * @param _n
   *          the length of the bit string
   */
  public BitStringObjectiveFunction(final int _n) {
    super();
    if (_n <= 0) {
      throw new IllegalArgumentException(
          "n must be at least one, but is " //$NON-NLS-1$
              + _n);
    }
    this.n = _n;
  }

  /**
   * Compute the value of {@link BitStringObjectiveFunction#n}
   * from the instance name, where it should start after a given
   * prefix
   *
   * @param prefix
   *          the name prefix
   * @param name
   *          the name
   * @return the instance scale
   */
  static final int _parse_n(final String prefix,
      final String name) {
    if (!name.startsWith(prefix)) {
      throw new IllegalArgumentException("Invalid name '" //$NON-NLS-1$
          + name + "': must start with prefix '" + //$NON-NLS-1$
          prefix + "'.");//$NON-NLS-1$
    }
    String nn = null;
    try {
      nn = name.substring(prefix.length());
      return Integer.parseInt(nn);
    } catch (final Throwable error) {
      throw new IllegalArgumentException("Invalid name '" //$NON-NLS-1$
          + name + "': must start with prefix '" + //$NON-NLS-1$
          prefix + "' and the rest must be a valid number, but '"//$NON-NLS-1$
          + nn + "' is not.", error);//$NON-NLS-1$
    }
  }

  /**
   * get the {@link BitStringObjectiveFunction#n} and the
   * {@code k} value from the instance name
   *
   * @param prefix
   *          the name prefix
   * @param name
   *          the name
   * @return the instance scale
   */
  static final int[] _parse_nk(final String prefix,
      final String name) {

    if (!name.startsWith(prefix)) {
      throw new IllegalArgumentException("Invalid name: '" //$NON-NLS-1$
          + name + "', must start with prefix '" + //$NON-NLS-1$
          prefix + "'.");//$NON-NLS-1$
    }

    final int li = name.lastIndexOf('_');
    if (li <= prefix.length()) {
      throw new IllegalArgumentException("Invalid name: '" //$NON-NLS-1$
          + name + "', must start with prefix '" + //$NON-NLS-1$
          prefix + "' and contain exactly two underscores (_).");//$NON-NLS-1$
    }

    String n1 = null, n2 = null;
    try {
      n1 = name.substring(prefix.length(), li);
      n2 = name.substring(li + 1);

      return new int[] { Integer.parseInt(n1),
          Integer.parseInt(n2) };
    } catch (final Throwable error) {
      throw new IllegalArgumentException("Invalid name '" //$NON-NLS-1$
          + name + "': must start with prefix '" + //$NON-NLS-1$
          prefix
          + "' and then contain two integers separated by an underscore (_), but at least one of '"//$NON-NLS-1$
          + n1 + "' and '" + n2 + //$NON-NLS-1$
          "' is not a valid integer.", error);//$NON-NLS-1$
    }
  }

  /**
   * Create a space fitting to this objective function
   *
   * @return the space instance
   */
  public final BitStringSpace createSpace() {
    return new BitStringSpace(this.n);
  }
}
