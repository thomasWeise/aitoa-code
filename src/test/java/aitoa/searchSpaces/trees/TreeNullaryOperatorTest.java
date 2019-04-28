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
public class TreeNullaryOperatorTest
    extends INullarySearchOperatorTest<Node[]> {

  /** the type set */
  private final NodeTypeSet<?> m_typeSet;

  /** the space */
  private final TreeSpace m_space;

  /** the nullary operator */
  private final TreeNullaryOperator m_nullary;

  /**
   * create the nullary operator
   *
   * @param maxDepth
   *          the maximum depth
   */
  public TreeNullaryOperatorTest(final int maxDepth) {

    super();

    this.m_typeSet =
        TestFunctionNodeTypeSet.makeMathNodeTypeSet();
    this.m_space = new TreeSpace(maxDepth);
    this.m_nullary =
        new TreeNullaryOperator(this.m_typeSet, maxDepth);
  }

  /** {@inheritDoc} */
  @Override
  protected TreeSpace getSpace() {
    return this.m_space;
  }

  /** {@inheritDoc} */
  @Override
  protected TreeNullaryOperator
      getOperator(final ISpace<Node[]> space) {
    return this.m_nullary;
  }

  /**
   * Ensure that sufficient different nodes are created and that
   * the depth of the trees is correct
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 3600000)
  public final void testApplyValidAndDifferentAndDepthTest() {
    final TreeSpace space = this.getSpace();
    final TreeNullaryOperator op = this.getOperator(space);
    final Random random = ThreadLocalRandom.current();
    final ArrayList<Node> list = new ArrayList<>();

    final int maxDepth = op.m_maxDepth;
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
