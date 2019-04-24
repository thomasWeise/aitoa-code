package aitoa.searchSpaces.trees.math;

import java.io.IOException;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/**
 * The division operation. Division by zero will result in
 * negative or positive infinity for negative and positive
 * divideds, respectively. {@code 0/0} will become 1.
 *
 * @param <C>
 *          the basic context
 */
public final class Divide<C> extends BinaryFunction<C> {
  /**
   * Create a node
   *
   * @param type
   *          the node type record
   * @param _inner
   *          the inner function
   */
  public Divide(final NodeType<Divide<C>> type,
      final Node[] _inner) {
    super(type, _inner);
  }

  /** {@inheritDoc} */
  @Override
  public final double applyAsDouble(final C param) {
    final double r = this.inner0.applyAsDouble(param)
        / this.inner1.applyAsDouble(param);
    if (r != r) {
      return 1d;
    }
    return r;
  }

  /** {@inheritDoc} */
  @Override
  public final long applyAsLong(final C param) {
    final long l1 = this.inner0.applyAsLong(param);
    final long l2 = this.inner1.applyAsLong(param);
    if (l2 == 0L) {
      if (l1 < 0L) {
        return Long.MIN_VALUE;
      }
      if (l1 > 0L) {
        return Long.MAX_VALUE;
      }
      return 1L;
    }
    return l1 / l2;
  }

  /** {@inheritDoc} */
  @Override
  public final int applyAsInt(final C param) {
    final int i1 = this.inner0.applyAsInt(param);
    final int i2 = this.inner1.applyAsInt(param);
    if (i2 == 0) {
      if (i1 < 0) {
        return Integer.MIN_VALUE;
      }
      if (i1 > 0) {
        return Integer.MAX_VALUE;
      }
      return 1;
    }
    return i1 / i2;
  }

  /** {@inheritDoc} */
  @Override
  public final void asText(final Appendable out)
      throws IOException {
    out.append('(');
    this.inner0.asText(out);
    out.append('/');
    this.inner1.asText(out);
    out.append(')');
  }
}
