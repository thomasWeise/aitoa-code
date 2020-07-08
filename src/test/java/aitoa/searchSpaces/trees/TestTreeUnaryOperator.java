package aitoa.searchSpaces.trees;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.searchSpaces.trees.math.TestFunctionNodeTypeSet;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperatorTest;

/** A test for the tree-based unary operator */
@Ignore
public class TestTreeUnaryOperator
    extends IUnarySearchOperatorTest<Node[]> {

  /** the type set */
  private final NodeTypeSet<?> m_typeSet;

  /** the space */
  private final TreeSpace m_space;

  /** the nullary operator */
  private final TreeNullaryOperator m_nullary;
  /** the unary operator */
  private final TreeUnaryOperator m_unary;

  /**
   * create the unary operator
   *
   * @param maxDepth
   *          the maximum depth
   */
  public TestTreeUnaryOperator(final int maxDepth) {

    super();

    this.m_typeSet =
        TestFunctionNodeTypeSet.makeMathNodeTypeSet();
    this.m_space = new TreeSpace(maxDepth);
    this.m_nullary =
        new TreeNullaryOperator(this.m_typeSet, maxDepth);
    this.m_unary = new TreeUnaryOperator(maxDepth);
  }

  /** {@inheritDoc} */
  @Override
  protected TreeSpace getSpace() {
    return this.m_space;
  }

  /** {@inheritDoc} */
  @Override
  protected TreeUnaryOperator
      getOperator(final ISpace<Node[]> space) {
    return this.m_unary;
  }

  /** {@inheritDoc} */
  @Override
  protected Node[] createValid() {
    final Node[] res = new Node[1];
    this.m_nullary.apply(res, ThreadLocalRandom.current());
    return res;
  }

  /**
   * Ensure that sufficient different nodes are created and that
   * the depth of the trees is correct
   */
  @Test(timeout = 3600000)
  public void testApplyValidAndDifferentAndDepthTest() {
    final TreeSpace space = this.getSpace();
    final TreeUnaryOperator op = this.getOperator(space);
    final Random random = ThreadLocalRandom.current();

    final Node[] copy = space.create();
    final Node[] dest = space.create();

    final int maxDepth = op.m_maxDepth;
    int count = 0;
    int different = 0;
    final int[] childDepths = new int[maxDepth];
    final int[] parentDepths = new int[maxDepth];
    final int max = Math.max(512, 100 * maxDepth);

    for (; (++count) <= max;) {
      final Node[] src = this.createValid();
      final int pd = src[0].depth();
      TestTools.assertInRange(pd, 1, maxDepth);
      ++parentDepths[pd - 1];
      space.check(src);
      space.copy(src, copy);
      op.apply(src, dest, random);
      Assert.assertTrue(this.equals(src, copy));
      space.check(dest);
      if (!(this.equals(dest, src))) {
        different++;
      }

      final int cd = dest[0].depth();
      TestTools.assertInRange(cd, 1, maxDepth);
      ++childDepths[cd - 1];
    }

    TestTools.assertGreaterOrEqual(count, max);
    TestTools.assertGreaterOrEqual(different,
        (count - (count >>> 3)));

    final int min = Math.max(2, (max / maxDepth) >>> 4);
    for (final int i : parentDepths) {
      TestTools.assertGreaterOrEqual(i, min);
    }
    for (final int i : childDepths) {
      TestTools.assertGreaterOrEqual(i, min);
    }
  }
}
