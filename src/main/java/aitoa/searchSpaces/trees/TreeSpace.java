package aitoa.searchSpaces.trees;

import java.io.IOException;
import java.util.Objects;

import aitoa.structure.ISpace;

/** The space for trees */
public final class TreeSpace implements ISpace<Node[]> {

  /** the maximum permitted node depth */
  private final int m_maxDepth;

  /**
   * create the tree space
   *
   * @param maxDepth
   *          the maximum depth parameter
   */
  public TreeSpace(final int maxDepth) {
    super();
    this.m_maxDepth = TreeSpace._checkMaxDepth(maxDepth);
  }

  /**
   * check the maximum permitted depth
   *
   * @param md
   *          the maximum depth
   * @return the maximum depth to use
   */
  static int _checkMaxDepth(final int md) {
    if (md < 2) {
      throw new IllegalArgumentException(
          "maximum depth must be at least 2, but is " //$NON-NLS-1$
              + md);
    }
    return md;
  }

  /** create the tree space */
  public TreeSpace() {
    this(Integer.MAX_VALUE);
  }

  /** {@inheritDoc} */
  @Override
  public Node[] create() {
    return new Node[1];
  }

  /** {@inheritDoc} */
  @Override
  public void copy(final Node[] from, final Node[] to) {
    to[0] = from[0];
  }

  /** {@inheritDoc} */
  @Override
  public void print(final Node[] z, final Appendable out)
      throws IOException {
    final Node n = z[0];
    if (n != null) {
      n.asText(out);
      out.append(System.lineSeparator());
      out.append(System.lineSeparator());
      n.asJavaPrintParameters(out);
    } else {
      out.append("null"); //$NON-NLS-1$
    }
  }

  /**
   * check a node
   *
   * @param n
   *          the node to check
   */
  static void _checkNode(final Node n) {
    if (n == null) {
      throw new IllegalArgumentException(
          "Node array must contain a node.");//$NON-NLS-1$
    }

    final NodeType<?> t = n.getType();

    if (t == null) {
      throw new IllegalArgumentException(
          "Node must have node type set."); //$NON-NLS-1$
    }

    final NodeTypeSet<?> ts = t.getTypeSet();
    if (ts == null) {
      throw new IllegalArgumentException(
          "Node type set cannot be null."); //$NON-NLS-1$
    }
    if (!(ts.containsType(t))) {
      throw new IllegalArgumentException(
          "Node type set does not contain node type?"); //$NON-NLS-1$
    }

    if (Objects.equals(t, NodeType.dummy())) {
      throw new IllegalArgumentException(
          "Node type of a node in optimization code cannot be dummy type."); //$NON-NLS-1$
    }
    final int type_childCount = t.getChildCount();
    final int node_childCount = n.getChildCount();
    if (type_childCount != node_childCount) {
      throw new IllegalArgumentException(
          "Child-count disagreement: node says " + //$NON-NLS-1$
              node_childCount + ", while type says " + //$NON-NLS-1$
              type_childCount);
    }

    int d = 0;
    int w = 1;

    for (int i = node_childCount; (--i) >= 0;) {
      final Node c = n.getChild(i);

      final NodeTypeSet<?> cts = c.getType().getTypeSet();
      final NodeTypeSet<?> tts = t.getChildTypes(i);
      if (cts != tts) {
        throw new IllegalArgumentException(
            "Child node not permitted at index "//$NON-NLS-1$
                + i + " due to type conflict.");//$NON-NLS-1$
      }

      d = Math.max(d, c.depth());
      w += c.weight();
      TreeSpace._checkNode(c);
    }

    ++d;
    final int nd = n.depth();
    if (d != nd) {
      throw new IllegalArgumentException(
          "Depth disagreement: node says " + //$NON-NLS-1$
              nd + ", while computation yields " + //$NON-NLS-1$
              d);
    }

    final int nw = n.weight();
    if (w != nw) {
      throw new IllegalArgumentException(
          "Weight disagreement: node says " + //$NON-NLS-1$
              nw + ", while computation yields " + //$NON-NLS-1$
              w);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void check(final Node[] z) {
    if ((z == null) || (z.length != 1)) {
      throw new IllegalArgumentException(
          "Node array must not be null and must be of length 1.");//$NON-NLS-1$
    }

    final Node n = z[0];
    if (n == null) {
      throw new IllegalArgumentException(
          "Root node cannot be null.");//$NON-NLS-1$
    }
    final int d = n.depth();
    if ((d <= 0) || (d > this.m_maxDepth)) {
      throw new IllegalArgumentException(
          "Invalid root node depth " //$NON-NLS-1$
              + d + ", must be in 1.." + //$NON-NLS-1$
              this.m_maxDepth);
    }
    TreeSpace._checkNode(n);
  }

  /** {@inheritDoc} */
  @Override
  public double getScale() {
    return this.m_maxDepth;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "trees:" + this.m_maxDepth;//$NON-NLS-1$
  }
}
