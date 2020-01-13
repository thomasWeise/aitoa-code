package aitoa.examples.bitstrings;

/**
 * The well-known OneMax problem: The goal is to maximize the
 * number of {@code true} bits in a bit string, which we can
 * transform into a minimization problem by minimizing the number
 * of {@code false} bits.
 */
public final class OneMaxObjectiveFunction
    extends BitStringObjectiveFunction {

  /** the name prefix */
  public static final String NAME_PREFIX = "OneMax_"; //$NON-NLS-1$

  /**
   * create
   *
   * @param _n
   *          the length of the bit string
   */
  public OneMaxObjectiveFunction(final int _n) {
    super(_n);
  }

  /**
   * Create the objective function based on the instance name
   *
   * @param s
   *          the name
   */
  public OneMaxObjectiveFunction(final String s) {
    this(OneMaxObjectiveFunction.__n(s));
  }

  /**
   * get the {@link BitStringObjectiveFunction#n} from the
   * instance name
   *
   * @param name
   *          the name
   * @return the instance scale
   */
  private static final int __n(final String name) {
    if (!name.startsWith(OneMaxObjectiveFunction.NAME_PREFIX)) {
      throw new IllegalArgumentException("Invalid name " + name); //$NON-NLS-1$
    }
    return Integer.parseInt(//
        name.substring(
            OneMaxObjectiveFunction.NAME_PREFIX.length()));
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
    return this.n;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return OneMaxObjectiveFunction.NAME_PREFIX + this.n;
  }
}
