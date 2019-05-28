package aitoa.searchSpaces.trees;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test a node type
 *
 * @param <T>
 *          the node type
 */
@Ignore
public class TestNodeType<T extends Node>
    extends _StructureTest<NodeType<T>> implements Cloneable {

  /** the node type */
  private NodeType<T> m_nodeType;

  /**
   * test a node type
   *
   * @param owner
   *          the owner
   * @param type
   *          the node type
   */
  private TestNodeType(final _StructureTest<?> owner,
      final NodeType<T> type) {
    super(owner);
    this.m_nodeType = Objects.requireNonNull(type);
  }

  /**
   * test a node type
   *
   * @param type
   *          the node type
   */
  protected TestNodeType(final NodeType<T> type) {
    this(null, type);
  }

  /** test the child types of this node type recursively */
  @Test(timeout = 3600000)
  @SuppressWarnings("rawtypes")
  public void testChildTypesRecursively() {
    final NodeType type = this.getInstance();
    Assert.assertNotNull(type);
    final int size = type.getChildCount();
    if (size > 0) {
      Assert.assertFalse(type.isTerminal());
      final Random random = ThreadLocalRandom.current();
      final NodeTypeSet[] ch = new NodeTypeSet[size];
      for (int i = size; (--i) >= 0;) {
        Assert.assertNotNull(ch[i] = type.getChildTypes(i));
      }
      for (int i = ch.length; i > 0;) {
        final int choice = random.nextInt(i);
        final NodeTypeSet sel = ch[choice];
        ch[choice] = ch[--i];
        TestNodeTypeSet._testNodeTypeSet(this, sel);
      }
    } else {
      Assert.assertTrue(type.isTerminal());
    }
  }

  /**
   * instantiate the node type
   *
   * @param type
   *          the node type
   * @param r
   *          the randomizer
   * @param d
   *          the current depth
   * @return the node
   */
  @SuppressWarnings("rawtypes")
  static final Node _instantiate(final NodeType type,
      final Random r, final int d) {
    final Node n;

    if (type.isTerminal()) {
      n = type.instantiate(null, r);
    } else {
      final Node[] ns = new Node[type.getChildCount()];
      for (int i = ns.length; (--i) >= 0;) {
        final NodeTypeSet nts = type.getChildTypes(i);
        if ((nts.getTerminalTypeCount() > 0) && (d > 4)) {
          ns[i] = TestNodeType._instantiate(
              nts.getRandomTerminalType(r), r, d + 1);
        } else {
          ns[i] = TestNodeType._instantiate(nts.getRandomType(r),
              r, d + 1);
        }
      }

      n = type.instantiate(ns, r);
    }

    Assert.assertNotNull(n);
    Assert.assertSame(n.getType(), type);
    return n;
  }

  /** test whether the type can be instantiated properly */
  @Test(timeout = 3600000)
  public void testInstantiate() {
    final Node n = TestNodeType._instantiate(this.getInstance(),
        ThreadLocalRandom.current(), 0);
    Assert.assertNotNull(n);
    TestNode._testNode(this, n);
  }

  /**
   * Check whether a node type is a dummy type
   *
   * @param type
   *          the type
   * @return {@code true} if the type is a dummy type,
   *         {@code false} otherwise
   */
  public static final boolean
      isDummyNodeType(final NodeType<?> type) {
    return (NodeType.dummy().getClass()
        .isAssignableFrom(type.getClass()));
  }

  /** {@inheritDoc} */
  @Override
  protected NodeType<T> getInstance() {
    return this.m_nodeType;
  }

  /**
   * Apply the default tests to a given node
   *
   * @param owner
   *          the owning test
   * @param nodeType
   *          the node type
   */
  static void _testNodeType(final _StructureTest<?> owner,
      final NodeType<?> nodeType) {
    owner._test(nodeType,
        () -> new TestNodeType<>(owner, nodeType).runAllTests());
  }

  /**
   * Apply the default tests to a given node
   *
   * @param nodeType
   *          the node type
   */
  public static void testNodeType(final NodeType<?> nodeType) {
    new TestNodeType<>(nodeType).runAllTests();
  }
}
