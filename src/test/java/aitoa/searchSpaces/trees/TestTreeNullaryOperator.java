package aitoa.searchSpaces.trees;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.searchSpaces.trees.math.TestFunctionNodeTypeSet;
import aitoa.structure.INullarySearchOperatorTest;
import aitoa.structure.ISpace;

/** A test for the tree-based nullary operator */
@Ignore
public class TestTreeNullaryOperator
    extends INullarySearchOperatorTest<Node[]> {

  /** the type set */
  private final NodeTypeSet<?> mTypeSet;

  /** the space */
  private final TreeSpace mSpace;

  /** the nullary operator */
  private final TreeNullaryOperator mNullary;

  /**
   * create the nullary operator
   *
   * @param pMaxDepth
   *          the maximum depth
   */
  public TestTreeNullaryOperator(final int pMaxDepth) {

    super();

    this.mTypeSet =
        TestFunctionNodeTypeSet.makeMathNodeTypeSet();
    this.mSpace = new TreeSpace(pMaxDepth);
    this.mNullary =
        new TreeNullaryOperator(this.mTypeSet, pMaxDepth);
  }

  /** {@inheritDoc} */
  @Override
  protected TreeSpace getSpace() {
    return this.mSpace;
  }

  /** {@inheritDoc} */
  @Override
  protected TreeNullaryOperator
      getOperator(final ISpace<Node[]> space) {
    return this.mNullary;
  }

  /**
   * Ensure that sufficient different nodes are created and that
   * the depth of the trees is correct
   */
  @Test(timeout = 3600000)
  public final void testApplyValidAndDifferentAndDepthTest() {
    final TreeSpace space = this.getSpace();
    final TreeNullaryOperator op = this.getOperator(space);
    final Random random = ThreadLocalRandom.current();
    final ArrayList<Node> list = new ArrayList<>();

    final int maxDepth = op.mMaxDepth;
    final int[] depths = new int[maxDepth];
    final int max = Math.max(512, 100 * maxDepth);

    outer: for (int i = max; (--i) >= 0;) {
      final Node[] dest = space.create();
      op.apply(dest, random);
      final Node node = dest[0];
      space.check(dest);
      for (final Node x : list) {
        if (Objects.equals(x, node)) {
          continue outer;
        }
      }
      list.add(node);
      ++depths[node.depth() - 1];
    }

    TestTools.assertInRange(list.size(), max / 50, max);

    final int min = Math.max(2, (max / maxDepth) >>> 4);
    for (final int i : depths) {
      TestTools.assertGreaterOrEqual(i, min);
    }
  }
}
