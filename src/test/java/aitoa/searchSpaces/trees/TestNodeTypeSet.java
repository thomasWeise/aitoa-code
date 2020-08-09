package aitoa.searchSpaces.trees;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;

/**
 * Test a node type set
 *
 * @param <T>
 *          the node type
 */
@Ignore
public class TestNodeTypeSet<T extends Node>
    extends StructureTest<NodeTypeSet<T>> implements Cloneable {

  /** the node type */
  private NodeTypeSet<T> mNodeTypeSet;

  /**
   * test a node type
   *
   * @param pOwner
   *          the owner
   * @param pType
   *          the node type
   */
  private TestNodeTypeSet(final StructureTest<?> pOwner,
      final NodeTypeSet<T> pType) {
    super(pOwner);
    this.mNodeTypeSet = Objects.requireNonNull(pType);
  }

  /**
   * test a node type
   *
   * @param pType
   *          the node type
   */
  protected TestNodeTypeSet(final NodeTypeSet<T> pType) {
    this(null, pType);
  }

  /** test the node types in this node recursively */
  @Test(timeout = 3600000)
  public void testNodeTypes() {
    final NodeTypeSet<T> set = this.getInstance();
    Assert.assertNotNull(set);
    final int size = set.getTypeCount();
    TestTools.assertGreater(size, 0);

    final int terminals = set.getTerminalTypeCount();
    TestTools.assertInRange(terminals, 0, size);
    TestTools.assertInRange(set.getNonTerminalTypeCount(), 0,
        size);
    Assert.assertEquals(size, set.getTerminalTypeCount()
        + set.getNonTerminalTypeCount());

    final int[] range = new int[size];
    for (int i = size; (--i) >= 0;) {
      range[i] = i;
    }

    final Random r = ThreadLocalRandom.current();

    this.test(null, () -> {
      for (int i = size; i > 0;) {
        final int k = r.nextInt(i);
        final int idx = range[k];
        range[k] = range[--i];

        final NodeType<? extends T> t = set.getType(idx);
        Assert.assertNotNull(t);

        if (idx < terminals) {
          Assert.assertTrue(t.isTerminal());
          Assert.assertSame(t, set.getTerminalType(idx));
          Assert.assertEquals(0, t.getChildCount());
        } else {
          Assert.assertFalse(t.isTerminal());
          Assert.assertSame(t,
              set.getNonTerminalType(idx - terminals));
          TestTools.assertGreater(t.getChildCount(), 0);
        }

        TestNodeType.testNodeType(this, t);
      }
    });
  }

  /** {@inheritDoc} */
  @Override
  protected NodeTypeSet<T> getInstance() {
    return this.mNodeTypeSet;
  }

  /**
   * Apply the default tests to a given node
   *
   * @param owner
   *          the owner
   * @param nodeTypeSet
   *          the node type set
   */
  static void testNodeTypeSet(final StructureTest<?> owner,
      final NodeTypeSet<?> nodeTypeSet) {
    owner.test(nodeTypeSet,
        () -> new TestNodeTypeSet<>(owner, nodeTypeSet)
            .runAllTests());
  }

  /**
   * Apply the default tests to a given node
   *
   * @param nodeTypeSet
   *          the node type set
   */
  public static void
      testNodeTypeSet(final NodeTypeSet<?> nodeTypeSet) {
    new TestNodeTypeSet<>(nodeTypeSet).runAllTests();
  }
}
