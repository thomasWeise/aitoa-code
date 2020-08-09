package aitoa.searchSpaces.trees;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;

import aitoa.searchSpaces.trees.math.TestFunctionNodeTypeSet;
import aitoa.structure.ISpaceTest;

/** test a tree space */
public class TestTreeSpace extends ISpaceTest<Node[]> {

  /** the type set */
  private final NodeTypeSet<?> mTypeSet;

  /** the space */
  private final TreeSpace mSpace;

  /** test a tree space */
  public TestTreeSpace() {
    super();

    this.mTypeSet =
        TestFunctionNodeTypeSet.makeMathNodeTypeSet();
    this.mSpace = new TreeSpace();
  }

  /** {@inheritDoc} */
  @Override
  public TreeSpace getInstance() {
    return this.mSpace;
  }

  /** {@inheritDoc} */
  @Override
  protected void fillWithRandomData(final Node[] dest) {
    final Random r = ThreadLocalRandom.current();
    dest[0] = TestNodeType
        .instantiate(this.mTypeSet.getRandomType(r), r, 0);
  }

  /** {@inheritDoc} */
  @Override
  protected int testCheckValidityTimes() {
    return 2;
  }

  /** {@inheritDoc} */
  @Override
  protected Node[] createValid() {
    final Node[] n = new Node[1];
    this.fillWithRandomData(n);
    return n;
  }

  /** {@inheritDoc} */
  @Override
  protected Node[] createInvalid() {
    switch (ThreadLocalRandom.current().nextInt(3)) {
      case 0:
        return null;
      case 1:
        return new Node[0];
      case 2:
        return new Node[2];
      default:
        return new Node[1];
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void assertValid(final Node[] a) {
    Assert.assertNotNull(a);
    Assert.assertEquals(1, a.length);
    TestNode.testNode(a[0]);
  }

  /** {@inheritDoc} */
  @Override
  protected void assertEquals(final Node[] a, final Node[] b) {
    Assert.assertTrue(Objects.deepEquals(a, b));
  }
}
