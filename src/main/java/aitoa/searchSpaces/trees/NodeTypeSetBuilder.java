package aitoa.searchSpaces.trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Build a node type system
 */
public class NodeTypeSetBuilder {

  /** the node type set builders */
  private ArrayList<Builder> m_builders;

  /** do we have the root builder? */
  private boolean m_hasRoot;

  /** Create the node type system builder */
  public NodeTypeSetBuilder() {
    super();
    this.m_builders = new ArrayList<>();
  }

  /**
   * create a new node type set
   *
   * @return the node type set builder
   */
  public final Builder newNodeTypeSet() {
    final Builder n = new Builder(false);
    this.m_builders.add(n);
    return n;
  }

  /**
   * create the root node type set
   *
   * @return the node type set
   */
  public final Builder rootNodeTypeSet() {
    if (this.m_hasRoot) {
      throw new IllegalStateException(
          "only one root node type set can be constructed."); //$NON-NLS-1$
    }
    final Builder n = new Builder(true);
    this.m_builders.add(0, n);
    return n;
  }

  /**
   * build the root node type set
   *
   * @return the root node type set
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public final NodeTypeSet build() {
    final ArrayList<Builder> builders = this.m_builders;
    this.m_builders = null;

    final int size = builders.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "there must be builders."); //$NON-NLS-1$
    }
    if (!(builders.get(0).m_isRoot)) {
      throw new IllegalStateException(//
          "you must create a root node type.");//$NON-NLS-1$
    }

    // create the node type sets, but leave them empty for now
    final NodeTypeSet[] result = new NodeTypeSet[size];
    for (int i = size; (--i) >= 0;) {
      final Builder ntsb = builders.get(i);
      ntsb.m_index = i;
      final NodeType[] types = new NodeType[ntsb.m_types.size()];
      result[i] = new NodeTypeSet<>(types);
    }

    final NodeTypeSet[] empty = new NodeTypeSet[0];

    // create the node types
    int id = 0;
    for (int i = size; (--i) >= 0;) {
      // prepare each node type set

      final NodeTypeSet current = result[i];
      final Builder ntsb = builders.get(i);
      final ArrayList<Object[]> types = ntsb.m_types;

      for (int j = types.size(); (--j) >= 0;) {
        // prepare each node type
        final Object[] ab = types.get(j);
        final Function<NodeTypeSet[], NodeType> factory =
            ((Function) (ab[0]));
        final Builder[] childTypes = ((Builder[]) (ab[1]));
        final NodeTypeSet[] children;

        int k = childTypes.length;
        if (k > 0) {
          children = new NodeTypeSet[k];
          for (; (--k) >= 0;) {
            children[k] = result[childTypes[k].m_index];
          }
        } else {
          children = empty;
        }

        final NodeType type =
            Objects.requireNonNull(factory.apply(children));
        current.m_types[j] = type;
        type.m_typeSet = current;
      } // end node type

      // the first sort
      Arrays.sort(result[i].m_types);
      for (final NodeType<?> type : result[i].m_types) {
        type.m_id = (id++); // assign ids
        if (type.m_childTypes.length <= 0) {
          ++current.m_terminalCount;
        }
      } // end post-processing types
    } // end node type set

    return Objects.requireNonNull(result[0]);
  }

  /**
   * the type set builder
   */
  public static final class Builder {

    /** the types */
    final ArrayList<Object[]> m_types;

    /** is this a root builder? */
    final boolean m_isRoot;

    /** the index */
    int m_index;

    /**
     * create
     *
     * @param isRoot
     *          is this the root builder?
     */
    Builder(final boolean isRoot) {
      super();
      this.m_types = new ArrayList<>();
      this.m_isRoot = isRoot;
    }

    /**
     * Add a node type factory. When the
     * {@link NodeTypeSetBuilder#build()} method is called, this
     * factory function is used to construct the actual node
     * types.
     *
     * @param factory
     *          the factory
     * @param childTypes
     *          the child types
     */
    public final void add(
        final Function<NodeTypeSet<?>[], NodeType<?>> factory,
        final Builder... childTypes) {
      this.m_types.add(new Object[] { //
          Objects.requireNonNull(factory), //
          Objects.requireNonNull(childTypes) });
    }

    /**
     * Automatically create a node type that reflectively creates
     * instances of the given class.
     *
     * @param clazz
     *          the class of which we want to create instances
     * @param childTypes
     *          the child types
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void add(final Class<?> clazz,
        final Builder... childTypes) {
      this.add(new _ReflectiveNodeTypes<>((Class) clazz),
          childTypes);
    }
  }
}
