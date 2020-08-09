package aitoa.searchSpaces.trees.math;

import java.io.IOException;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/**
 * The maximum operator.
 *
 * @param <C>
 *          the basic context
 */
public final class Max<C> extends BinaryFunction<C> {
  /**
   * Create a node
   *
   * @param pType
   *          the node type record
   * @param pInner
   *          the inner function
   */
  public Max(final NodeType<Max<C>> pType, final Node[] pInner) {
    super(pType, pInner);
  }

  /** {@inheritDoc} */
  @Override
  public double applyAsDouble(final C param) {
    return Math.max(this.inner0.applyAsDouble(param),
        this.inner1.applyAsDouble(param));
  }

  /** {@inheritDoc} */
  @Override
  public long applyAsLong(final C param) {
    return Math.max(this.inner0.applyAsLong(param),
        this.inner1.applyAsLong(param));
  }

  /** {@inheritDoc} */
  @Override
  public int applyAsInt(final C param) {
    return Math.max(this.inner0.applyAsInt(param),
        this.inner1.applyAsInt(param));
  }

  /** {@inheritDoc} */
  @Override
  public void asText(final Appendable out) throws IOException {
    out.append("max("); //$NON-NLS-1$
    this.inner0.asText(out);
    out.append(',');
    this.inner1.asText(out);
    out.append(')');
  }
}
