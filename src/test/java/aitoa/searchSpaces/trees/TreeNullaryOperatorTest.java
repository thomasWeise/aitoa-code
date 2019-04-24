package aitoa.searchSpaces.trees;

import org.junit.Ignore;

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
}
