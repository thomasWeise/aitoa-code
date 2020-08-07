package aitoa.searchSpaces.trees;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * The node type of a node has two main functions: 1) It
 * describes which types of children are allowed to occur at
 * which position (if any), and 2) it is used to instantiate
 * nodes of the type. Notice that 1) is the basic requirement for
 * strongly-typed GP: We can specify which type of node can occur
 * as child of which other node and thus, build a complex type
 * system and pre-define node structures precisely. Point 2)
 * somehow reproduces the capabilities of Java's reflection
 * system with the extension of allowing us to perform some
 * additional, randomized actions. Matter of fact, with the class
 * ReflectionNodeType, we defer the node creation to Java's
 * reflection mechanisms in cases where no randomized
 * instantiation actions are required.
 *
 * @param <T>
 *          the node type
 */
public abstract class NodeType<T extends Node>
    implements Comparable<NodeType<?>> {

  /** a list of possible node types for each child */
  final NodeTypeSet<?>[] m_childTypes;

  /** the type set */
  NodeTypeSet<? super T> m_typeSet;

  /** the node type id */
  int m_id;

  /**
   * Create a new node type record
   *
   * @param childTypes
   *          the child types
   */
  protected NodeType(final NodeTypeSet<?>[] childTypes) {
    super();
    this.m_childTypes = Objects.requireNonNull(childTypes);
  }

  /**
   * Get the owning node type set
   *
   * @return the owning node type set
   */
  public final NodeTypeSet<? super T> getTypeSet() {
    return this.m_typeSet;
  }

  /**
   * Instantiate a node
   *
   * @param children
   *          a given set of children
   * @param random
   *          the randomizer
   * @return the new node
   */
  public abstract T instantiate(final Node[] children,
      final Random random);

  /**
   * Create a modified copy of the node, or the node itself if no
   * modification different from child changes is possible
   *
   * @param node
   *          the node
   * @param random
   *          the random number generator
   * @return a modified copy of the node, or the node itself if
   *         no modification different from child changes is
   *         possible
   */
  public T createModifiedCopy(final T node,
      final Random random) {
    return Objects.requireNonNull(node);
  }

  /**
   * Replace the child at the given index. This default
   * implementation will <em>only</em> work if the node is
   * completely defined by its children and no randomness is
   * needed <em>OR</em> the node cannot have any children, i.e.,
   * if this method must never be invoked.
   *
   * @param original
   *          the original node
   * @param child
   *          the child
   * @param index
   *          the index
   * @return the new node
   */
  public T replaceChild(final T original, final Node child,
      final int index) {
    int i = this.m_childTypes.length;
    if (i <= index) {
      throw new IndexOutOfBoundsException(//
          "index " + index //$NON-NLS-1$
              + " out of valid range 0.." //$NON-NLS-1$
              + (i - 1));
    }
    if (child == original.getChild(index)) {
      return original;
    }
    final Node[] nodes = new Node[i];
    for (; (--i) >= 0;) {
      nodes[i] = ((i == index) ? child : original.getChild(i));
    }
    return this.instantiate(nodes, null);
  }

  /**
   * Get the number of chTypes of this node type
   *
   * @return the number of chTypes of this node type
   */
  public final int getChildCount() {
    return this.m_childTypes.length;
  }

  /**
   * Get the possible types for the chTypes at the specific
   * index.
   *
   * @param index
   *          the child index
   * @return the possible types of that child
   */
  public final NodeTypeSet<?> getChildTypes(final int index) {
    return this.m_childTypes[index];
  }

  /**
   * Does this node type describe a terminal node?
   *
   * @return true if this node type describes a terminal node,
   *         false otherwise
   */
  public final boolean isTerminal() {
    return (this.m_childTypes.length <= 0);
  }

  /**
   * compare two node types
   *
   * @param o
   *          the other node type
   * @return {@inheritDoc}
   */
  @Override
  public final int compareTo(final NodeType<?> o) {
    if (o == this) {
      return 0;
    }
    if (o == null) {
      return (-1);
    }
    final int r = Integer.compare(this.m_id, o.m_id);
    if (r != 0) {
      return r;
    }

    return Integer.compare(this.m_childTypes.length,
        o.m_childTypes.length);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("rawtypes")
  public final boolean equals(final Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (o instanceof NodeType) {
      if (this.hashCode() == o.hashCode()) {
        final NodeType t = ((NodeType) o);
        if (t.m_id == this.m_id) {
          return Arrays.equals(this.m_childTypes,
              t.m_childTypes);
        }
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /** get the instance */
  @SuppressWarnings("rawtypes")
  private static final NodeType DUMMY_TYPE = new DummyType();

  /**
   * Obtain a dummy node type. This node type cannot be used in
   * any search operation or other context within the procedure
   * of synthesizing trees. It can, however, be used as parameter
   * for the constructors of tree nodes in cases where we only
   * need the tree structures as is, without any intention to
   * execute an optimization procedure. This is useful when
   * re-generating tree structures from log files as done in
   * {@link Node#asJava(Appendable)}
   *
   * @return a dummy node type
   * @param <T>
   *          the node type
   * @see Node#asJava(Appendable)
   */
  public static final <T extends Node> NodeType<T> dummy() {
    return NodeType.DUMMY_TYPE;
  }

  /** a dummy node type */
  private static final class DummyType extends NodeType<Node> {
    /** create */
    DummyType() {
      super(new NodeTypeSet[0]);
    }

    /** {@inheritDoc} */
    @Override
    public Node instantiate(final Node[] children,
        final Random random) {
      throw new UnsupportedOperationException(//
          "this is a dummy node type. it cannot be used for instantiating nodes!"); //$NON-NLS-1$
    }
  }
}
