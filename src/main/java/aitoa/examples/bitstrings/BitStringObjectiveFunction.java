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
   * Create a space fitting to this objective function
   *
   * @return the space instance
   */
  public final BitStringSpace createSpace() {
    return new BitStringSpace(this.n);
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
  static final int parseN(final String prefix,
      final String name) {
    if (!name.startsWith(prefix)) {
      throw new IllegalArgumentException("Invalid name '" //$NON-NLS-1$
          + name + "': must start with prefix '" + //$NON-NLS-1$
          prefix + "'.");//$NON-NLS-1$
    }
    if (name.charAt(prefix.length()) != '_') {
      throw new IllegalArgumentException("Invalid name '" //$NON-NLS-1$
          + name + "': must start with prefix '" + //$NON-NLS-1$
          prefix + "' followed by an underscore (_).");//$NON-NLS-1$
    }

    String nn = null;
    try {
      nn = name.substring(prefix.length() + 1);
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
  static final int[] parseNK(final String prefix,
      final String name) {

    if (!name.startsWith(prefix)) {
      throw new IllegalArgumentException("Invalid name: '" //$NON-NLS-1$
          + name + "', must start with prefix '" + //$NON-NLS-1$
          prefix + "'.");//$NON-NLS-1$
    }
    if (name.charAt(prefix.length()) != '_') {
      throw new IllegalArgumentException("Invalid name '" //$NON-NLS-1$
          + name + "': must start with prefix '" + //$NON-NLS-1$
          prefix + "' followed by an underscore (_).");//$NON-NLS-1$
    }

    final int li = name.lastIndexOf('_');
    if (li <= (prefix.length() + 1)) {
      throw new IllegalArgumentException("Invalid name: '" //$NON-NLS-1$
          + name + "', must start with prefix '" + //$NON-NLS-1$
          prefix + "' and contain exactly two underscores (_).");//$NON-NLS-1$
    }

    String n1 = null, n2 = null;
    try {
      n1 = name.substring(prefix.length() + 1, li);
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
   * make a name from a prefix and an n
   *
   * @param prefix
   *          the prefix
   * @param n
   *          the n
   * @return the name
   */
  static final String makeNameN(final String prefix,
      final int n) {
    return (prefix + '_') + n;
  }

  /**
   * make a name from a prefix and an n
   *
   * @param prefix
   *          the prefix
   * @param n
   *          the n
   * @param k
   *          the k
   * @return the name
   */
  static final String makeNameNK(final String prefix,
      final int n, final int k) {
    return (((prefix + '_') + n) + '_') + k;
  }

  /**
   * Check if a given {@code name} string matches to one of the
   * typical example objective functions for bit strings. If so,
   * try to parse the name and load the objective
   *
   * @param name
   *          the name
   * @return either the objective function {@code f}
   *         corresponding to the name (with
   *         {@code f.toString().equals(name)}) or {@code null}
   *         if the name is not known
   * @throws IllegalArgumentException
   *           if the name is not formatted correctly
   * @throws NumberFormatException
   *           if the name is not formatted correctly
   * @throws NullPointerException
   *           if {@code name==null}
   */
  @SuppressWarnings("incomplete-switch")
  public static final BitStringObjectiveFunction
      tryLoadExample(final String name) {
    final int index = name.indexOf('_');
    if ((index > 0) && (index < (name.length() - 1))) {
      switch (name.substring(0, index)) {
        case OneMaxObjectiveFunction.NAME_PREFIX:
          return new OneMaxObjectiveFunction(name);
        case LeadingOnesObjectiveFunction.NAME_PREFIX:
          return new LeadingOnesObjectiveFunction(name);
        case TrapObjectiveFunction.NAME_PREFIX:
          return new TrapObjectiveFunction(name);
        case TwoMaxObjectiveFunction.NAME_PREFIX:
          return new TwoMaxObjectiveFunction(name);
        case JumpObjectiveFunction.NAME_PREFIX:
          return new JumpObjectiveFunction(name);
        case PlateauObjectiveFunction.NAME_PREFIX:
          return new PlateauObjectiveFunction(name);
        case LinearHarmonicObjectiveFunction.NAME_PREFIX:
          return new LinearHarmonicObjectiveFunction(name);
        case Ising1DObjectiveFunction.NAME_PREFIX:
          return new Ising1DObjectiveFunction(name);
        case Ising2DObjectiveFunction.NAME_PREFIX:
          return new Ising2DObjectiveFunction(name);
        case NQueensObjectiveFunction.NAME_PREFIX:
          return new NQueensObjectiveFunction(name);
      }
    }
    return null;
  }
}
