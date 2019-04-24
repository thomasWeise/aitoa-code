package aitoa.searchSpaces.trees;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;

import aitoa.searchSpaces.trees.math.TestFunctionNodeTypeSet;
import aitoa.structure.ISpace;
import aitoa.structure.IUnarySearchOperatorTest;

/** A test for the tree-based unary operator */
@Ignore
public class TreeUnaryOperatorTest
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
  public TreeUnaryOperatorTest(final int maxDepth) {

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
}
