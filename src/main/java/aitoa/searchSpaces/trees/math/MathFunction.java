package aitoa.searchSpaces.trees.math;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeType;

/**
 * A tree node representing a mathematical function.
 *
 * @param <C>
 *          the basic context
 */
public abstract class MathFunction<C> extends Node implements
    ToDoubleFunction<C>, ToLongFunction<C>, ToIntFunction<C> {

  /**
   * Create a node
   *
   * @param type
   *          the node type record
   */
  protected MathFunction(
      final NodeType<? extends MathFunction<C>> type) {
    super(type);
  }

  /**
   * Perform the computation of this mathematical function in the
   * continuous domain.
   *
   * @param param
   *          the parameter
   */
  @Override
  public abstract double applyAsDouble(final C param);

  /**
   * Perform the computation of this mathematical function in the
   * domain of 64bit integers.
   *
   * @param param
   *          the parameter
   */
  @Override
  public abstract long applyAsLong(final C param);

  /**
   * Perform the computation of this mathematical function in the
   * domain of 32bit integers.
   *
   * @param param
   *          the parameter
   */
  @Override
  public abstract int applyAsInt(final C param);
}
