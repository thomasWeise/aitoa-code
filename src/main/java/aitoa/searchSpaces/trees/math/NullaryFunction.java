package aitoa.searchSpaces.trees.math;

import aitoa.searchSpaces.trees.NodeType;

/**
 * A nullary function
 *
 * @param <C>
 *          the basic context
 */
public abstract class NullaryFunction<C>
    extends MathFunction<C> {

  /**
   * Create a node
   *
   * @param type
   *          the node type record
   */
  protected NullaryFunction(
      final NodeType<? extends NullaryFunction<C>> type) {
    super(type);
  }

  /** {@inheritDoc} */
  @Override
  public final int getChildCount() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isTerminal() {
    return true;
  }
}
