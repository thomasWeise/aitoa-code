package aitoa.searchSpaces.trees.math;

import java.io.IOException;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/**
 * The if-greater-then-else operator.
 *
 * @param <C>
 *          the basic context
 */
public final class IfGreaterThenElse<C>
    extends QuaternaryFunction<C> {
  /**
   * Create a node
   *
   * @param type
   *          the node type record
   * @param _inner
   *          the inner function
   */
  public IfGreaterThenElse(
      final NodeType<IfGreaterThenElse<C>> type,
      final Node[] _inner) {
    super(type, _inner);
  }

  /** {@inheritDoc} */
  @Override
  public double applyAsDouble(final C param) {
    return (this.inner0.applyAsDouble(param) > this.inner1
        .applyAsDouble(param)) ? this.inner2.applyAsDouble(param)
            : this.inner3.applyAsDouble(param);
  }

  /** {@inheritDoc} */
  @Override
  public long applyAsLong(final C param) {
    return (this.inner0.applyAsLong(param) > this.inner1
        .applyAsLong(param)) ? this.inner2.applyAsLong(param)
            : this.inner3.applyAsLong(param);
  }

  /** {@inheritDoc} */
  @Override
  public int applyAsInt(final C param) {
    return (this.inner0.applyAsInt(param) > this.inner1
        .applyAsInt(param)) ? this.inner2.applyAsInt(param)
            : this.inner3.applyAsInt(param);
  }

  /** {@inheritDoc} */
  @Override
  public void asText(final Appendable out) throws IOException {
    out.append('(');
    out.append('(');
    this.inner0.asText(out);
    out.append('>');
    this.inner1.asText(out);
    out.append(')');
    out.append('?');
    this.inner2.asText(out);
    out.append(':');
    this.inner3.asText(out);
    out.append(')');
  }
}
