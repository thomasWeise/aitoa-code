package aitoa.searchSpaces.trees.math;

import java.io.IOException;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/**
 * The addition operation. The integer versions of this operation
 * are saturating, meaning that overflows will not wrap the sign
 * but return the corresponding upper limits.
 *
 * @param <C>
 *          the basic context
 */
public final class Add<C> extends BinaryFunction<C> {
  /**
   * Create a node
   *
   * @param type
   *          the node type record
   * @param _inner
   *          the inner function
   */
  public Add(final NodeType<Add<C>> type, final Node[] _inner) {
    super(type, _inner);
  }

  /** {@inheritDoc} */
  @Override
  public double applyAsDouble(final C param) {
    return this.inner0.applyAsDouble(param)
        + this.inner1.applyAsDouble(param);
  }

  /** {@inheritDoc} */
  @Override
  public long applyAsLong(final C param) {
    final long l1 = this.inner0.applyAsLong(param);
    final long l2 = this.inner1.applyAsLong(param);
    final long r = l1 + l2;
    if (((l1 ^ r) & (l2 ^ r)) < 0L) {
      // l1 and l2 have opposite sign of result
      if (l1 > 0L) {
        return Long.MAX_VALUE;
      }
      return Long.MIN_VALUE;
    }
    return r;
  }

  /** {@inheritDoc} */
  @Override
  public int applyAsInt(final C param) {
    final long r = ((long) (this.inner0.applyAsInt(param))
        + ((long) (this.inner1.applyAsInt(param))));
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
  public void asText(final Appendable out) throws IOException {
    out.append('(');
    this.inner0.asText(out);
    out.append('+');
    this.inner1.asText(out);
    out.append(')');
  }
}
