package aitoa.searchSpaces.trees.math;

import java.util.Objects;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/**
 * A binary function computes its result by combining the output
 * of two inner functions.
 *
 * @param <C>
 *          the basic context
 */
public abstract class BinaryFunction<C> extends MathFunction<C> {

  /** the first inner function */
  public final MathFunction<C> inner0;
  /** the second inner function */
  public final MathFunction<C> inner1;

  /**
   * Create a node
   *
   * @param pType
   *          the node type record
   * @param pInner
   *          the inner function
   */
  @SuppressWarnings("unchecked")
  protected BinaryFunction(
      final NodeType<? extends BinaryFunction<C>> pType,
      final Node[] pInner) {
    super(pType);
    this.inner0 =
        (MathFunction<C>) (Objects.requireNonNull(pInner[0]));
    this.inner1 =
        (MathFunction<C>) (Objects.requireNonNull(pInner[1]));
  }

  /** {@inheritDoc} */
  @Override
  public final int getChildCount() {
    return 2;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public final MathFunction<C> getChild(final int index) {
    switch (index) {
      case 0: {
        return this.inner0;
      }
      case 1: {
        return this.inner1;
      }
      default: {
        return (MathFunction<C>) (super.getChild(index));
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isTerminal() {
    return false;
  }
}
