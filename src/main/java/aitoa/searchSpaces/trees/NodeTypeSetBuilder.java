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
  private ArrayList<Builder> mBuilders;

  /** do we have the root builder? */
  private boolean mHasRoot;

  /** Create the node type system builder */
  public NodeTypeSetBuilder() {
    super();
    this.mBuilders = new ArrayList<>();
  }

  /**
   * create a new node type set
   *
   * @return the node type set builder
   */
  public final Builder newNodeTypeSet() {
    final Builder n = new Builder(false);
    this.mBuilders.add(n);
    return n;
  }

  /**
   * create the root node type set
   *
   * @return the node type set
   */
  public final Builder rootNodeTypeSet() {
    if (this.mHasRoot) {
      throw new IllegalStateException(
          "only one root node type set can be constructed."); //$NON-NLS-1$
    }
    final Builder n = new Builder(true);
    this.mBuilders.add(0, n);
    return n;
  }

  /**
   * build the root node type set
   *
   * @return the root node type set
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public final NodeTypeSet build() {
    final ArrayList<Builder> builders = this.mBuilders;
    this.mBuilders = null;

    final int size = builders.size();
    if (size <= 0) {
      throw new IllegalStateException(//
          "there must be builders."); //$NON-NLS-1$
    }
    if (!(builders.get(0).mIsRoot)) {
      throw new IllegalStateException(//
          "you must create a root node type.");//$NON-NLS-1$
    }

    // create the node type sets, but leave them empty for now
    final NodeTypeSet[] result = new NodeTypeSet[size];
    for (int i = size; (--i) >= 0;) {
      final Builder ntsb = builders.get(i);
      ntsb.mIndex = i;
      final NodeType[] types = new NodeType[ntsb.mTypes.size()];
      result[i] = new NodeTypeSet<>(types);
    }

    final NodeTypeSet[] empty = new NodeTypeSet[0];

    // create the node types
    int id = 0;
    for (int i = size; (--i) >= 0;) {
      // prepare each node type set

      final NodeTypeSet current = result[i];
      final Builder ntsb = builders.get(i);
      final ArrayList<Object[]> types = ntsb.mTypes;

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
            children[k] = result[childTypes[k].mIndex];
          }
        } else {
          children = empty;
        }

        final NodeType type =
            Objects.requireNonNull(factory.apply(children));
        current.mTypes[j] = type;
        type.mTypeSet = current;
      } // end node type

      // the first sort
      Arrays.sort(result[i].mTypes);
      for (final NodeType<?> type : result[i].mTypes) {
        type.mId = (id++); // assign ids
        if (type.mChildTypes.length <= 0) {
          ++current.mTerminalCount;
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
    final ArrayList<Object[]> mTypes;

    /** is this a root builder? */
    final boolean mIsRoot;

    /** the index */
    int mIndex;

    /**
     * create
     *
     * @param pIsRoot
     *          is this the root builder?
     */
    Builder(final boolean pIsRoot) {
      super();
      this.mTypes = new ArrayList<>();
      this.mIsRoot = pIsRoot;
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
      this.mTypes.add(new Object[] { //
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
      this.add(new ReflectiveNodeTypes<>((Class) clazz),
          childTypes);
    }
  }
}
