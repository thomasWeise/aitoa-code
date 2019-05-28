package aitoa.searchSpaces.trees;

import java.util.Objects;
import java.util.Random;

import aitoa.structure.IUnarySearchOperator;

/**
 * A simple mutation operation for a given tree which implants a
 * randomly created subtree into parent, thereby replacing a
 * randomly picked node in the parent. This operation basically
 * proceeds according to the ideas discussed in Section 31.3.4.1
 * with the extension that it also respects the type system of
 * the strongly-typed GP system.
 */
public final class TreeUnaryOperator extends _TreePathOperator
    implements IUnarySearchOperator<Node[]> {
  /**
   * Create a new tree mutation operation
   *
   * @param md
   *          the maximum tree depth
   */
  public TreeUnaryOperator(final int md) {
    super(md);
  }

  /** {@inheritDoc} */
  @Override
  public final void apply(final Node[] x, final Node[] dest,
      final Random random) {
    final Node xx = x[0];

    for (int trials = _TreeOperator.MAX_TRIALS;
        (--trials) >= 0;) {
      final int length = this._randomPath(xx, random);
      final Node yy = random.nextBoolean()//
          ? this._tryModifyEnd(random)//
          : this._replaceEnd(
              _TreeOperator._createTree(this._getEndChoices(),
                  (this.m_maxDepth - length) + 1, random));
      if ((yy != null) && (!Objects.equals(xx, yy))) {
        dest[0] = yy;
        return;
      }
    }

    throw new IllegalArgumentException(//
        "failed to modify tree " + //$NON-NLS-1$
            xx.toString() + " after trying "//$NON-NLS-1$
            + _TreeOperator.MAX_TRIALS + " times!"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "rep" + this.m_maxDepth;//$NON-NLS-1$
  }
}