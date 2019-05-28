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
    extends _StructureTest<NodeTypeSet<T>> implements Cloneable {

  /** the node type */
  private NodeTypeSet<T> m_nodeTypeSet;

  /**
   * test a node type
   *
   * @param owner
   *          the owner
   * @param type
   *          the node type
   */
  private TestNodeTypeSet(final _StructureTest<?> owner,
      final NodeTypeSet<T> type) {
    super(owner);
    this.m_nodeTypeSet = Objects.requireNonNull(type);
  }

  /**
   * test a node type
   *
   * @param type
   *          the node type
   */
  protected TestNodeTypeSet(final NodeTypeSet<T> type) {
    this(null, type);
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

    this._test(null, () -> {
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

        TestNodeType._testNodeType(this, t);
      }
    });
  }

  /** {@inheritDoc} */
  @Override
  protected NodeTypeSet<T> getInstance() {
    return this.m_nodeTypeSet;
  }

  /**
   * Apply the default tests to a given node
   *
   * @param owner
   *          the owner
   * @param nodeTypeSet
   *          the node type set
   */
  static void _testNodeTypeSet(final _StructureTest<?> owner,
      final NodeTypeSet<?> nodeTypeSet) {
    owner._test(nodeTypeSet,
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
