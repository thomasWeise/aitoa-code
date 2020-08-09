package aitoa.searchSpaces.trees;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.searchSpaces.trees.math.TestFunctionNodeTypeSet;
import aitoa.structure.IBinarySearchOperatorTest;
import aitoa.structure.ISpace;

/** A test for the tree-based binary operator */
@Ignore
public class TestTreeBinaryOperator
    extends IBinarySearchOperatorTest<Node[]> {

  /** the type set */
  private final NodeTypeSet<?> mTypeSet;

  /** the space */
  private final TreeSpace mSpace;

  /** the nullary operator */
  private final TreeNullaryOperator mNullary;
  /** the unary operator */
  private final TreeBinaryOperator mBinary;

  /**
   * create the unary operator
   *
   * @param pMaxDepth
   *          the maximum depth
   */
  public TestTreeBinaryOperator(final int pMaxDepth) {

    super();

    this.mTypeSet =
        TestFunctionNodeTypeSet.makeMathNodeTypeSet();
    this.mSpace = new TreeSpace(pMaxDepth);
    this.mNullary =
        new TreeNullaryOperator(this.mTypeSet, pMaxDepth);
    this.mBinary = new TreeBinaryOperator(pMaxDepth);
  }

  /** {@inheritDoc} */
  @Override
  protected TreeSpace getSpace() {
    return this.mSpace;
  }

  /** {@inheritDoc} */
  @Override
  protected TreeBinaryOperator
      getOperator(final ISpace<Node[]> space) {
    return this.mBinary;
  }

  /** {@inheritDoc} */
  @Override
  protected Node[] createValid() {
    final Node[] res = new Node[1];
    this.mNullary.apply(res, ThreadLocalRandom.current());
    return res;
  }

  /**
   * Ensure that sufficient different nodes are created and that
   * the depth of the trees is correct
   */
  @Test(timeout = 3600000)
  public void testApplyValidAndDifferentAndDepthTest() {
    final TreeSpace space = this.getSpace();
    final TreeBinaryOperator op = this.getOperator(space);
    final Random random = ThreadLocalRandom.current();
    final int maxDepth = op.mMaxDepth;

    final Node[] copy1 = space.create();
    final Node[] copy2 = space.create();
    final Node[] dest = space.create();
    final int[] childDepths = new int[maxDepth];
    final int[] parentDepths = new int[maxDepth];

    final int max = Math.max(512, 100 * maxDepth);
    int count = 0;
    int different = 0;
    for (int i = 0; (++i) <= max;) {
      final Node[] src1 = this.createValid();
      final int pd1 = src1[0].depth();
      TestTools.assertInRange(pd1, 1, maxDepth);
      ++parentDepths[pd1 - 1];
      space.check(src1);
      space.copy(src1, copy1);

      final Node[] src2 = this.createValid();
      space.check(src2);
      space.copy(src2, copy2);
      final int pd2 = src2[0].depth();
      TestTools.assertInRange(pd2, 1, maxDepth);
      ++parentDepths[pd2 - 1];

      op.apply(src1, src2, dest, random);
      Assert.assertTrue(this.equals(src1, copy1));
      Assert.assertTrue(this.equals(src2, copy2));
      space.check(dest);

      if (!this.equals(src1, src2)) {
        count++;
        if ((!(this.equals(src1, dest)))
            && (!(this.equals(src2, dest)))) {
          different++;
        }
      }

      final int cd = dest[0].depth();
      TestTools.assertInRange(cd, 1,
          Math.min(maxDepth, (pd1 + pd2) - 1));
      ++childDepths[cd - 1];
    }

    TestTools.assertGreater(count, max / 16);
    TestTools.assertGreaterOrEqual(different, 1 + (count >>> 5));

    final int min = Math.max(2, (max / maxDepth) >>> 4);
    final int min2 = 2 * min;
    for (final int i : parentDepths) {
      TestTools.assertGreaterOrEqual(i, min2);
    }
    for (final int i : childDepths) {
      TestTools.assertGreaterOrEqual(i, min);
    }
  }
}
