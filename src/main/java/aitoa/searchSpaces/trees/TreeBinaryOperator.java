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
public final class TreeBinaryOperator extends TreePathOperator
    implements IBinarySearchOperator<Node[]> {

  /** the collector for child nodes */
  private Node[] mCuts;
  /** the number of cuts collected */
  private int mCutsSize;

  /**
   * Create a new tree recombination operation
   *
   * @param pMd
   *          the maximum tree depth
   */
  public TreeBinaryOperator(final int pMd) {
    super(pMd);
    this.mCuts =
        new Node[Math.max(16, Math.min(168384, (2 << pMd)))];
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
  private void collectCuts(final Node root,
      final NodeTypeSet<?> allowed, final int maxDepth) {
    if ((root.depth() <= maxDepth)
        && (allowed.containsNode(root))) {
      if (this.mCutsSize >= this.mCuts.length) {
        this.mCuts = Arrays.copyOf(this.mCuts,
            Math.addExact(this.mCutsSize, this.mCutsSize));
      }
      this.mCuts[this.mCutsSize++] = root;
    }
    for (int i = root.getChildCount(); (--i) >= 0;) {
      this.collectCuts(root.getChild(i), allowed, maxDepth);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void apply(final Node[] x0, final Node[] x1,
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

      for (int trials = TreeOperator.MAX_TRIALS;
          (--trials) >= 0;) {
        if (canSwap) {
          final Node t = p0;
          p0 = p1;
          p1 = t;
        }

        final int length = this.randomPath(p0, random);
        final NodeTypeSet<?> allowed = this.getEndChoices();
        this.mCutsSize = 0;
        this.collectCuts(p1, allowed,
            (this.mMaxDepth - length) + 1);
        if (this.mCutsSize > 0) {
          final Node yy = this.replaceEnd(
              this.mCuts[random.nextInt(this.mCutsSize)]);
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
  public String toString() {
    return "rec" + this.mMaxDepth;//$NON-NLS-1$
  }
}
