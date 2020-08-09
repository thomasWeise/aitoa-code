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
   * @param pType
   *          the node type record
   */
  protected NullaryFunction(
      final NodeType<? extends NullaryFunction<C>> pType) {
    super(pType);
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
