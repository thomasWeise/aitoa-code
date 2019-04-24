package aitoa.searchSpaces.trees.math;

import java.io.IOException;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/**
 * The atan2 operation. The integer versions return the result in
 * degrees.
 *
 * @param <C>
 *          the basic context
 */
public final class ATan2<C> extends BinaryFunction<C> {
  /**
   * Create a node
   *
   * @param type
   *          the node type record
   * @param _inner
   *          the inner function
   */
  public ATan2(final NodeType<ATan2<C>> type,
      final Node[] _inner) {
    super(type, _inner);
  }

  /** {@inheritDoc} */
  @Override
  public final double applyAsDouble(final C param) {
    return Math.atan2(this.inner0.applyAsDouble(param),
        this.inner1.applyAsDouble(param));
  }

  /** {@inheritDoc} */
  @Override
  public final long applyAsLong(final C param) {
    return Math.round(Math.toDegrees(//
        Math.atan2(this.inner0.applyAsLong(param),
            this.inner1.applyAsLong(param))));
  }

  /** {@inheritDoc} */
  @Override
  public final int applyAsInt(final C param) {
    return ((int) (Math.round(Math.toDegrees(//
        Math.atan2(this.inner0.applyAsInt(param),
            this.inner1.applyAsInt(param))))));
  }

  /** {@inheritDoc} */
  @Override
  public final void asText(final Appendable out)
      throws IOException {
    out.append("atan2("); //$NON-NLS-1$
    this.inner0.asText(out);
    out.append(',');
    this.inner1.asText(out);
    out.append(')');
  }
}
