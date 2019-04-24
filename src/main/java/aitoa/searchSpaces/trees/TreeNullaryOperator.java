package aitoa.searchSpaces.trees;

import java.util.Random;

import aitoa.structure.INullarySearchOperator;

/**
 * An operation creating trees.
 */
public final class TreeNullaryOperator extends _TreeOperator
    implements INullarySearchOperator<Node[]> {

  /** the types to choose from */
  private final NodeTypeSet<?> m_types;

  /**
   * Create a new ramped-half-and-half
   *
   * @param md
   *          the maximum tree depth
   * @param ptypes
   *          the types
   */
  public TreeNullaryOperator(final NodeTypeSet<?> ptypes,
      final int md) {
    super(md);
    this.m_types = ptypes;
  }

  /** {@inheritDoc} */
  @Override
  public void apply(final Node[] dest, final Random random) {
    for (int trials = _TreeOperator.MAX_TRIALS;
        (--trials) >= 0;) {
      if ((dest[0] = _TreeOperator._createTree(this.m_types,
          (1 + random.nextInt(this.m_maxDepth)),
          random)) != null) {
        return;
      }
    }
    throw new IllegalArgumentException(//
        "failed to create tree " + //$NON-NLS-1$
            _TreeOperator.MAX_TRIALS + " times!"); //$NON-NLS-1$
  }
}