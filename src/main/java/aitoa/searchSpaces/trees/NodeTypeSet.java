package aitoa.searchSpaces.trees;

import java.util.Arrays;
import java.util.Random;

/**
 * A set of node type records. For each child position of a
 * genotype, we need to specify such a set of possible child
 * types. This set provides an easy interface to access the
 * possible types, the possible non-leaf types, and the possible
 * leaf-types stored in it. Also, we can find very efficiently if
 * a node type is in a node type set (in O(1)), which is a
 * necessary operation of all tree mutation and crossover
 * operations of strongly-typed Genetic Programming.
 *
 * @param <T>
 *          the base type for all nodes in this set
 */
public final class NodeTypeSet<T extends Node> {
  /**
   * the list of types: each type can have arbitrary child types,
   * but must extend NT
   */
  final NodeType<? extends T>[] m_types;

  /** the number of terminal node type records */
  int m_terminalCount;

  /**
   * Create a new node type set
   *
   * @param types
   *          the types
   */
  NodeTypeSet(final NodeType<? extends T>[] types) {
    super();
    if (types.length <= 0) {
      throw new IllegalArgumentException(
          "a node type set must contain at least one node type."); //$NON-NLS-1$
    }
    this.m_types = types;
  }

  /**
   * Get the number of entries
   *
   * @return the number of entries
   */
  public final int getTypeCount() {
    return this.m_types.length;
  }

  /**
   * Get the node type at the specified index
   *
   * @param index
   *          the index into the information set
   * @return the node type at the specified index
   */
  public final NodeType<? extends T> getType(final int index) {
    return this.m_types[index];
  }

  /**
   * Get the number of terminal node types
   *
   * @return the number of terminal node types
   */
  public final int getTerminalTypeCount() {
    return this.m_terminalCount;
  }

  /**
   * Get the terminal node type at the specified index
   *
   * @param index
   *          the index into the information set
   * @return the node type at the specified index
   */
  public final NodeType<? extends T>
      getTerminalType(final int index) {
    if ((index >= 0) && (index < this.m_terminalCount)) {
      return this.m_types[index];
    }
    throw new IndexOutOfBoundsException("index "//$NON-NLS-1$
        + index //
        + " is invalid, there are only "//$NON-NLS-1$
        + this.m_terminalCount + //
        " terminal node types in total."); //$NON-NLS-1$
  }

  /**
   * Get the number of non-terminal node types
   *
   * @return the number of non-terminal node types
   */
  public final int getNonTerminalTypeCount() {
    return this.m_types.length - this.m_terminalCount;
  }

  /**
   * Get the non-terminal node type at the specified index
   *
   * @param index
   *          the index into the information set
   * @return the node type at the specified index
   */
  public final NodeType<? extends T>
      getNonTerminalType(final int index) {
    final int tc = this.m_terminalCount;
    final int ts = (this.m_types.length - tc);
    if ((index >= 0) && (index < ts)) {
      return this.m_types[tc + index];
    }
    throw new IndexOutOfBoundsException("index "//$NON-NLS-1$
        + index //
        + " is invalid, there are only "//$NON-NLS-1$
        + ts + //
        " non-terminal node types in total."); //$NON-NLS-1$
  }

  /**
   * Obtain a random node type
   *
   * @param r
   *          the random number generator
   * @return the node type
   */
  public final NodeType<? extends T>
      getRandomType(final Random r) {
    final int i = this.m_types.length;
    if (i <= 0) {
      return null;
    }
    return this.m_types[r.nextInt(i)];
  }

  /**
   * Obtain a random terminal node type
   *
   * @param r
   *          the random number generator
   * @return the terminal node type
   */
  public final NodeType<? extends T>
      getRandomTerminalType(final Random r) {
    final int i = this.m_terminalCount;
    if (i <= 0) {
      return null;
    }
    return this.m_types[r.nextInt(i)];
  }

  /**
   * Obtain a random non-terminal node type
   *
   * @param r
   *          the random number generator
   * @return the non-terminal node type
   */
  public final NodeType<? extends T>
      getRandomNonTerminalType(final Random r) {
    final int o = this.m_terminalCount;
    final int i = this.m_types.length - o;
    if (i <= 0) {
      return null;
    }
    return this.m_types[r.nextInt(i) + o];
  }

  /**
   * Check whether the given node type is contained in this type
   * set or not
   *
   * @param t
   *          the node type
   * @return true if the type is contained, false otherwise
   */
  public final boolean containsType(final NodeType<?> t) {
    return ((t != null) && //
        (Arrays.binarySearch(this.m_types, t) >= 0));
  }

  /**
   * Check whether the given node is an instance of a type in
   * this node type set
   *
   * @param n
   *          the node
   * @return {@code true} if the node is permitted is an instance
   *         of a type in this node type set, {@code false}
   *         otherwise
   */
  public final boolean containsNode(final Node n) {
    return ((n != null) && //
        (Arrays.binarySearch(this.m_types, n.m_type) >= 0));
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
    if (o instanceof NodeTypeSet) {
      if (this.hashCode() == o.hashCode()) {
        final NodeTypeSet t = ((NodeTypeSet) o);
        return Arrays.equals(this.m_types, t.m_types);
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return super.hashCode();
  }
}