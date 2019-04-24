package aitoa.searchSpaces.trees.math;

import java.io.IOException;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/**
 * The multiplication operation. The integer versions of this
 * operation are saturating, meaning that overflows will not wrap
 * the sign but return the corresponding upper limits.
 *
 * @param <C>
 *          the basic context
 */
public final class Multiply<C> extends BinaryFunction<C> {
  /**
   * Create a node
   *
   * @param type
   *          the node type record
   * @param _inner
   *          the inner function
   */
  public Multiply(final NodeType<Multiply<C>> type,
      final Node[] _inner) {
    super(type, _inner);
  }

  /** {@inheritDoc} */
  @Override
  public final double applyAsDouble(final C param) {
    final double a = this.inner0.applyAsDouble(param);
    if (a == 0d) {
      return 0d;
    }
    final double b = this.inner1.applyAsDouble(param);
    if (b == 0d) {
      return 0d;
    }
    return a * b;
  }

  /** {@inheritDoc} */
  @Override
  public final long applyAsLong(final C param) {
    final long l1 = this.inner0.applyAsLong(param);
    if (l1 == 0L) {
      return 0L;
    }
    final long l2 = this.inner1.applyAsLong(param);
    final long r = l1 * l2;
    final long ax = Math.abs(l1);
    final long ay = Math.abs(l2);
    if ((((ax | ay) >>> 31) != 0L)) {
      // Some bits greater than 2^31 that might cause overflow
      // Check the result using the divide operator
      // and check for the special case of Long.MIN_VALUE * -1
      if (((l2 != 0) && ((r / l2) != l1))
          || ((l1 == Long.MIN_VALUE) && (l2 == -1L))) {
        return ((l1 ^ l2) < 0L) // l1 and l2 have different signs
            ? Long.MIN_VALUE// result must be negative infinity
            : Long.MAX_VALUE; // result is positive infinity
      }
    }
    return r;
  }

  /** {@inheritDoc} */
  @Override
  public final int applyAsInt(final C param) {
    final long a = this.inner0.applyAsInt(param);
    if (a == 0L) {
      return 0;
    }
    final long r = a * this.inner1.applyAsInt(param);
    final int rr = ((int) r);
    if (rr != r) {
      if (r < 0L) {
        return Integer.MIN_VALUE;
      }
      return Integer.MAX_VALUE;
    }
    return rr;
  }

  /** {@inheritDoc} */
  @Override
  public final void asText(final Appendable out)
      throws IOException {
    out.append('(');
    this.inner0.asText(out);
    out.append('*');
    this.inner1.asText(out);
    out.append(')');
  }
}
