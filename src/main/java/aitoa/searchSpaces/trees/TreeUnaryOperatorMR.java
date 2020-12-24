package aitoa.searchSpaces.trees;

import java.util.Objects;
import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * A simple mutation operation for a given tree which implants a
 * randomly created subtree into parent, thereby replacing a
 * randomly picked node in the parent. It either tries to apply
 * the {@linkplain NodeType#createModifiedCopy(Node, Random)
 * modification operator} or to replace the node with an entirely
 * new sub-tree.
 */
public final class TreeUnaryOperatorMR extends TreePathOperator
    implements IUnarySearchOperator<Node[]> {
  /**
   * Create a new tree mutation operation
   *
   * @param md
   *          the maximum tree depth
   */
  public TreeUnaryOperatorMR(final int md) {
    super(md);
  }

  /** {@inheritDoc} */
  @Override
  public void apply(final Node[] x, final Node[] dest,
      final Random random) {
    final Node xx = x[0];

    for (int trials = TreeOperator.MAX_TRIALS;
        (--trials) >= 0;) {
      final int length = this.randomPath(xx, random);
      final Node yy = random.nextBoolean()//
          ? this.tryModifyEnd(random)//
          : this.replaceEnd(
              TreeOperator.createTree(this.getEndChoices(),
                  (this.mMaxDepth - length) + 1, random));
      if ((yy != null) && (!Objects.equals(xx, yy))) {
        dest[0] = yy;
        return;
      }
    }

    throw new IllegalArgumentException(//
        "failed to modify tree " + //$NON-NLS-1$
            xx.toString() + " after trying "//$NON-NLS-1$
            + TreeOperator.MAX_TRIALS + " times!"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "mr" + this.mMaxDepth;//$NON-NLS-1$
  }
}
