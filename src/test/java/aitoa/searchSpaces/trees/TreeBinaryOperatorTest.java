package aitoa.searchSpaces.trees;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;

import aitoa.searchSpaces.trees.math.TestFunctionNodeTypeSet;
import aitoa.structure.IBinarySearchOperatorTest;
import aitoa.structure.ISpace;

/** A test for the tree-based binary operator */
@Ignore
public class TreeBinaryOperatorTest
    extends IBinarySearchOperatorTest<Node[]> {

  /** the type set */
  private final NodeTypeSet<?> m_typeSet;

  /** the space */
  private final TreeSpace m_space;

  /** the nullary operator */
  private final TreeNullaryOperator m_nullary;
  /** the unary operator */
  private final TreeBinaryOperator m_binary;

  /**
   * create the unary operator
   *
   * @param maxDepth
   *          the maximum depth
   */
  public TreeBinaryOperatorTest(final int maxDepth) {

    super();

    this.m_typeSet =
        TestFunctionNodeTypeSet.makeMathNodeTypeSet();
    this.m_space = new TreeSpace(maxDepth);
    this.m_nullary =
        new TreeNullaryOperator(this.m_typeSet, maxDepth);
    this.m_binary = new TreeBinaryOperator(maxDepth);
  }

  /** {@inheritDoc} */
  @Override
  protected TreeSpace getSpace() {
    return this.m_space;
  }

  /** {@inheritDoc} */
  @Override
  protected TreeBinaryOperator
      getOperator(final ISpace<Node[]> space) {
    return this.m_binary;
  }

  /** {@inheritDoc} */
  @Override
  protected Node[] createValid() {
    final Node[] res = new Node[1];
    this.m_nullary.apply(res, ThreadLocalRandom.current());
    return res;
  }
}
