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
   * @param type
   *          the node type record
   * @param _inner
   *          the inner function
   */
  @SuppressWarnings("unchecked")
  protected UnaryFunction(
      final NodeType<? extends UnaryFunction<C>> type,
      final Node[] _inner) {
    super(type);
    this.inner =
        (MathFunction<C>) (Objects.requireNonNull(_inner[0]));
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
