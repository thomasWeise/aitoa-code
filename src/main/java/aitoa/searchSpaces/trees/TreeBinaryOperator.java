package aitoa.searchSpaces.trees;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import aitoa.structure.IBinarySearchOperator;

/**
 * A simple recombination operation for a given tree which
 * implants a randomly chosen subtree from one parent into the
 * other parent, thereby replacing a randomly picked node in the
 * second parent. This operation basically proceeds according to
 * the ideas discussed in Section 31.3.5 with the extension that
 * it also respects the type system of the strongly-typed GP
 * system.
 */
public final class TreeBinaryOperator extends _TreePathOperator
    implements IBinarySearchOperator<Node[]> {

  /** the collector for child nodes */
  private Node[] m_cuts;
  /** the number of cuts collected */
  private int m_cutsSize;

  /**
   * Create a new tree recombination operation
   *
   * @param md
   *          the maximum tree depth
   */
  public TreeBinaryOperator(final int md) {
    super(md);
    this.m_cuts =
        new Node[Math.max(16, Math.min(168384, (2 << md)))];
  }

  /**
   * collect all cuts that are allowed
   *
   * @param root
   *          the root
   * @param allowed
   *          the allowed nodes
   * @param maxDepth
   *          the maximum depth
   */
  private final void __collectCuts(final Node root,
      final NodeTypeSet<?> allowed, final int maxDepth) {
    if ((root.depth() <= maxDepth)
        && (allowed.containsNode(root))) {
      if (this.m_cutsSize >= this.m_cuts.length) {
        this.m_cuts = Arrays.copyOf(this.m_cuts,
            Math.addExact(this.m_cutsSize, this.m_cutsSize));
      }
      this.m_cuts[this.m_cutsSize++] = root;
    }
    for (int i = root.getChildCount(); (--i) >= 0;) {
      this.__collectCuts(root.getChild(i), allowed, maxDepth);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void apply(final Node[] x0, final Node[] x1,
      final Node[] dest, final Random random) {

    Node p0 = x0[0];
    Node p1 = x1[0];

    final boolean p0IsTerminal = p0.isTerminal();
    final boolean p1IsTerminal = p1.isTerminal();

    if (!(p0IsTerminal && p1IsTerminal)) {

      if (p0IsTerminal) {
        final Node t = p0;
        p0 = p1;
        p1 = t;
      }
      final boolean canSwap = !(p0IsTerminal || p1IsTerminal);

      for (int trials = _TreeOperator.MAX_TRIALS;
          (--trials) >= 0;) {
        if (canSwap) {
          final Node t = p0;
          p0 = p1;
          p1 = t;
        }

        final int length = this._randomPath(p0, random);
        final NodeTypeSet<?> allowed = this._getEndChoices();
        this.m_cutsSize = 0;
        this.__collectCuts(p1, allowed,
            (this.m_maxDepth - length) + 1);
        if (this.m_cutsSize > 0) {
          final Node yy = this._replaceEnd(
              this.m_cuts[random.nextInt(this.m_cutsSize)]);
          if ((yy != null) && (!(Objects.equals(yy, p0)
              || Objects.equals(yy, p1)))) {
            dest[0] = yy;
            return;
          }
        }
      }
    }

    // give up: if we cannot combine the trees
    dest[0] = random.nextBoolean() ? p0 : p1;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "rec" + this.m_maxDepth;//$NON-NLS-1$
  }
}