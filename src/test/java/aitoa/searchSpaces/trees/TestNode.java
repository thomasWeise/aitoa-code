package aitoa.searchSpaces.trees;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.TestTools;
import aitoa.utils.ReflectionUtils;

/**
 * Test a node
 */
@Ignore
public class TestNode extends StructureTest<Node> {

  /** the node */
  private final Node m_node;

  /**
   * test a node
   *
   * @param node
   *          the node
   * @param owner
   *          the owner
   */
  private TestNode(final StructureTest<?> owner,
      final Node node) {
    super(owner);
    this.m_node = Objects.requireNonNull(node);
  }

  /**
   * test a node
   *
   * @param node
   *          the node
   */
  protected TestNode(final Node node) {
    this(null, node);
  }

  /** test the children of this node recursively */
  @Test(timeout = 3600000)
  public void testChildrenRecursively() {
    final Node node = this.getInstance();
    Assert.assertNotNull(node);
    final int size = node.getChildCount();
    if (size > 0) {
      final Random random = ThreadLocalRandom.current();
      final Node[] ch = new Node[size];
      for (int i = size; (--i) >= 0;) {
        Assert.assertNotNull(ch[i] = node.getChild(i));
      }
      for (int i = ch.length; i > 0;) {
        final int choice = random.nextInt(i);
        final Node sel = ch[choice];
        ch[choice] = ch[--i];
        TestNode._testNode(this, sel);
      }
    }
  }

  /** test the type of this node */
  @Test(timeout = 3600000)
  public void testNodeType() {
    final Node n = this.getInstance();
    final NodeType<?> t = n.getType();

    Assert.assertNotNull(t);
    if (!(TestNodeType.isDummyNodeType(t))) {
      TestNodeType._testNodeType(this, t);

      final int size = t.getChildCount();
      Assert.assertEquals(size, n.getChildCount());
      for (int i = size; (--i) >= 0;) {
        Assert.assertTrue(
            t.getChildTypes(i).containsNode(n.getChild(i)));
      }
    }
  }

  /** test the this node via the space-check */
  @Test(timeout = 3600000)
  public void testViaSpaceCheck() {
    final Node n = this.getInstance();
    final NodeType<?> t = n.getType();
    if ((t != null) && (t != NodeType.dummy())) {
      TreeSpace._checkNode(n);
    }
  }

  /** test the weight and size of this node */
  @Test(timeout = 3600000)
  public void testNodeStats() {
    final Node n = this.getInstance();
    final int depth = n.depth();
    final int weight = n.weight();
    final int size = n.getChildCount();

    TestTools.assertGreaterOrEqual(depth, 1);
    TestTools.assertGreaterOrEqual(size, 0);
    TestTools.assertGreaterOrEqual(weight, depth);
    TestTools.assertGreaterOrEqual(weight, size + 1);
    Assert.assertTrue((size == 0) == n.isTerminal());
    Assert.assertTrue((size > 0) == (depth > 1));

    int d = 0, w = 1;
    for (int i = size; (--i) >= 0;) {
      w += n.getChild(i).weight();
      d = Math.max(d, n.getChild(i).depth());
    }
    Assert.assertEquals(d + 1, depth);
    Assert.assertEquals(w, weight);
  }

  /** {@inheritDoc} */
  @Override
  protected Node getInstance() {
    return this.m_node;
  }

  /**
   * test the as-text method works
   *
   * @throws IOException
   *           if i/o fails
   */
  @Test(timeout = 3600000)
  public void testText() throws IOException {
    final StringBuilder sb = new StringBuilder();
    this.getInstance().asText(sb);
    TestTools.assertGreater(sb.length(), 0);
  }

  /**
   * test the as-text modify method of the node type
   *
   * @throws IOException
   *           if i/o fails
   */
  @Test(timeout = 3600000)
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void testNodeTypeModify() throws IOException {
    final Node n = this.getInstance();
    final NodeType<Node> t = ((NodeType) (n.getType()));
    if (!(t == NodeType.dummy())) {
      final Node nn =
          t.createModifiedCopy(n, ThreadLocalRandom.current());
      Assert.assertNotNull(nn);
      Assert.assertEquals(n.getClass(), nn.getClass());
      Assert.assertEquals(t, nn.getType());
      if (Objects.equals(nn, n)) {
        Assert.assertSame(nn, n);
      }
    }
  }

  /**
   * test the to-Java method of the node works and produces an
   * exact copy of the given node
   *
   * @throws IOException
   *           if i/o fails
   * @throws ClassNotFoundException
   *           if compilation fails
   * @throws SecurityException
   *           if compilation fails
   * @throws NoSuchMethodException
   *           if compilation fails
   * @throws InvocationTargetException
   *           if compilation fails
   * @throws IllegalArgumentException
   *           if compilation fails
   * @throws IllegalAccessException
   *           if compilation fails
   */
  @Test(timeout = 3600000)
  public void testAsJava() throws IOException,
      ClassNotFoundException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
    final String clazz = "_TEST";//$NON-NLS-1$
    final String method = "get";//$NON-NLS-1$
    final StringBuilder sb = new StringBuilder();
    sb.append("public final class ");//$NON-NLS-1$
    sb.append(clazz);
    sb.append(" { public static ");//$NON-NLS-1$
    sb.append(ReflectionUtils.className(Node.class));
    sb.append(' ');
    sb.append(method);
    sb.append("() {return "); //$NON-NLS-1$
    final int l = sb.length();
    final Node n = this.getInstance();
    n.asJava(sb);
    TestTools.assertGreater(sb.length(), l);
    sb.append(";}}");//$NON-NLS-1$

    final Path tempDir = Files.createTempDirectory(null);
    try {
      final Path java =
          Files.createFile(tempDir.resolve(clazz + ".java")); //$NON-NLS-1$
      try {
        Files.write(java, sb.toString().getBytes());
        final JavaCompiler compiler =
            ToolProvider.getSystemJavaCompiler();
        Assert.assertEquals(0, compiler.run(null, null, null,
            java.toFile().getAbsolutePath()));
        try {
          final URL classUrl = tempDir.toUri().toURL();
          try (URLClassLoader classLoader = URLClassLoader
              .newInstance(new URL[] { classUrl })) {
            final Class<?> clazzx =
                Class.forName(clazz, true, classLoader);
            Assert.assertEquals(n,
                clazzx.getMethod(method).invoke(null));
          }
        } finally {
          Files.delete(tempDir.resolve(clazz + ".class")); //$NON-NLS-1$
        }
      } finally {
        Files.delete(java);
      }
    } finally {
      Files.delete(tempDir);
    }
  }

  /**
   * Apply the default tests to a given node
   *
   * @param owner
   *          the owning test
   * @param node
   *          the node
   */
  static void _testNode(final StructureTest<?> owner,
      final Node node) {
    owner._test(node,
        () -> new TestNode(owner, node).runAllTests());
  }

  /**
   * Apply the default tests to a given node
   *
   * @param node
   *          the node
   */
  public static void testNode(final Node node) {
    new TestNode(node).runAllTests();
  }
}
