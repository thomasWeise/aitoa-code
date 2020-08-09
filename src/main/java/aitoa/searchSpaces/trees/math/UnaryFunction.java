package aitoa.searchSpaces.trees.math;

import java.util.Objects;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/**
 * A unary function
 *
 * @param <C>
 *          the basic context
 */
public abstract class UnaryFunction<C> extends MathFunction<C> {

  /** the inner function */
  public final MathFunction<C> inner;

  /**
   * Create a node
   *
   * @param pType
   *          the node type record
   * @param pInner
   *          the inner function
   */
  @SuppressWarnings("unchecked")
  protected UnaryFunction(
      final NodeType<? extends UnaryFunction<C>> pType,
      final Node[] pInner) {
    super(pType);
    this.inner =
        (MathFunction<C>) (Objects.requireNonNull(pInner[0]));
  }

  /** {@inheritDoc} */
  @Override
  public final int getChildCount() {
    return 1;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public final MathFunction<C> getChild(final int index) {
    if (index == 0) {
      return this.inner;
    }
    return ((MathFunction<C>) (super.getChild(index)));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isTerminal() {
    return false;
  }
}
