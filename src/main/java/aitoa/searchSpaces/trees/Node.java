package aitoa.searchSpaces.trees;

import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.Objects;

import aitoa.utils.ReflectionUtils;

/**
 * A generic class for representing tree nodes. Its child node
 * type parameter {@code CT} should later be replaced with the
 * base class of the nodes that can be children to it.
 * <p>
 * Trees are the search spaces of Standard Genetic Programming
 * (SGP). The node class here could, however, also be used as
 * solution space. Actually, in SGP, search and solution space
 * are often the same. Our node class here supports
 * strongly-typed GP. In other words, we can define which type of
 * node is allowed as child for which other node.
 */
public class Node {

  /** the dummy node type string */
  private static final String DUMMY_STRING =
      ('(' + ReflectionUtils.className(NodeType.class)//
          + ".dummy()"); //$NON-NLS-1$

  /** the new node array string */
  private static final String NEW_NODE = ", new " + //$NON-NLS-1$
      ReflectionUtils.className(Node.class) + "[] {";//$NON-NLS-1$

  /** the node type record */
  final NodeType<?> m_type;

  /** the depth of the node */
  private transient int m_depth;

  /**
   * Create a node
   *
   * @param _type
   *          the node type record
   */
  public Node(final NodeType<?> _type) {
    super();
    this.m_type = Objects.requireNonNull(_type);
  }

  /**
   * Get the number of children
   *
   * @return the number of children
   */
  @SuppressWarnings("static-method")
  public int getChildCount() {
    return 0;
  }

  /**
   * Get a specific child
   *
   * @param index
   *          the child index
   * @return the child at that index
   */
  public Node getChild(final int index) {
    throw new IndexOutOfBoundsException(//
        "cannot access element " //$NON-NLS-1$
            + index + ", tree only has " //$NON-NLS-1$
            + this.getChildCount() + " elements.");//$NON-NLS-1$
  }

  /**
   * Is this a terminal node?
   *
   * @return true if this node is terminal, i.e., a leaf, false
   *         otherwise
   */
  public boolean isTerminal() {
    return (this.getChildCount() <= 0);
  }

  /**
   * Get the node type
   *
   * @return the node type
   */
  public final NodeType<?> getType() {
    return this.m_type;
  }

  /**
   * Write a Java representation of the given node to the
   * destination. Invoking this method must produce a textual
   * representation of Java code which, if copy-pasted into a
   * function or something, would allow us to instantiate the
   * object with its current content. The only exception is that
   * this method may rely on using a {@linkplain NodeType#dummy()
   * dummy node type}, i.e., it does not need to allow for a full
   * compatibility with search operations.
   *
   * @param out
   *          the writer
   * @throws IOException
   *           if i/o fails
   * @see NodeType#dummy()
   */
  @SuppressWarnings("rawtypes")
  public void asJava(final Appendable out) throws IOException {
    final Class clazz = this.getClass();
    out.append("new ")//$NON-NLS-1$
        .append(ReflectionUtils.className(clazz));
    final TypeVariable[] gt = clazz.getTypeParameters();
    if ((gt != null) && (gt.length > 0)) {
      out.append('<').append('>');
    }
    out.append(Node.DUMMY_STRING);
    this.asJavaPrintParameters(out);
    out.append(')');
  }

  /**
   * Write the java code for the constructor parameters except
   * the initial node type. If the constructor of this node class
   * takes any parameter except for the node type, then this
   * method must print them in a way that allows for
   * instantiating the node with its current configuration. If
   * there are no parameters, the method must exit without doing
   * anything. If parameters need to be written, this method must
   * first write a comma ("{@code ,}"). This comma is then
   * followed by all necessary parameters to construct the
   * object.
   *
   * @param out
   *          the destination appendable
   * @throws IOException
   *           if i/o fails
   */
  protected void asJavaPrintParameters(final Appendable out)
      throws IOException {

    final int size = this.getChildCount();
    if (size > 0) {

      final Node[] children = new Node[size];
      for (int i = size; (--i) >= 0;) {
        children[i] = this.getChild(i);
      }

      out.append(Node.NEW_NODE);

      boolean first = true;

      for (final Node child : children) {
        if (first) {
          first = false;
        } else {
          out.append(',');
        }
        out.append(System.lineSeparator());
        child.asJava(out);
      }
      out.append(' ').append('}');
    }
  }

  /**
   * Write a textual representation of the given node to the
   * output writer
   *
   * @param out
   *          the writer
   * @throws IOException
   *           if i/o fails
   */
  public void asText(final Appendable out) throws IOException {
    //
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    final StringBuilder sb = new StringBuilder();
    try {
      this.asText(sb);
    } catch (final IOException ioe) {
      throw new RuntimeException(ioe);
    }
    return sb.toString();
  }

  /**
   * Compare with another object
   *
   * @param o
   *          the other object
   * @return true if the objects are equal
   */
  @Override
  public boolean equals(final Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (o.getClass() == this.getClass()) {
      final Node ot = ((Node) o);
      int i = this.getChildCount();
      final int bs = ot.getChildCount();
      if (i == bs) {
        for (; (--i) >= 0;) {
          if (!(Objects.equals(this.getChild(i),
              ot.getChild(i)))) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Get the depth of this tree
   *
   * @return the depth of this tree
   */
  public final int depth() {
    int d = this.m_depth;
    if (d <= 0) {
      for (int i = this.getChildCount(); (--i) >= 0;) {
        final int dd = this.getChild(i).depth();
        if (dd > d) {
          d = dd;
        }
      }
      d = this.m_depth = (d + 1);
    }
    return d;
  }

  /**
   * Get the weight of this tree
   *
   * @return the weight of this tree
   */
  public final int weight() {
    int w = 1;
    for (int i = this.getChildCount(); (--i) >= 0;) {
      w += this.getChild(i).weight();
    }
    return w;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
