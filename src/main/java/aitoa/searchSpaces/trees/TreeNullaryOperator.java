package aitoa.searchSpaces.trees;

import java.util.Random;

import aitoa.structure.INullarySearchOperator;

/**
 * An operation creating trees.
 */
public final class TreeNullaryOperator extends TreeOperator
    implements INullarySearchOperator<Node[]> {

  /** the types to choose from */
  private final NodeTypeSet<?> mTypes;

  /**
   * Create a new ramped-half-and-half
   *
   * @param pMd
   *          the maximum tree depth
   * @param pTypes
   *          the types
   */
  public TreeNullaryOperator(final NodeTypeSet<?> pTypes,
      final int pMd) {
    super(pMd);
    this.mTypes = pTypes;
  }

  /** {@inheritDoc} */
  @Override
  public void apply(final Node[] dest, final Random random) {
    for (int trials = TreeOperator.MAX_TRIALS;
        (--trials) >= 0;) {
      if ((dest[0] = TreeOperator.createTree(this.mTypes,
          (1 + random.nextInt(this.mMaxDepth)),
          random)) != null) {
        return;
      }
    }
    throw new IllegalArgumentException(//
        "failed to create tree " + //$NON-NLS-1$
            TreeOperator.MAX_TRIALS + " times!"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "create" + this.mMaxDepth;//$NON-NLS-1$
  }
}
