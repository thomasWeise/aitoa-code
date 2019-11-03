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
}
