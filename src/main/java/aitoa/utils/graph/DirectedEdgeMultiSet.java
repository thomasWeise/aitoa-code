package aitoa.utils.graph;

/**
 * A multiset for edges, where each of the {@code L} nodes may
 * occur in at most {@code K} edges, which do not need to be
 * unique. The valid node IDs range from {@code 0} to
 * {@code L-1}, with a special node of id {@code -1} being
 * permitted only as starting node for edges but never at edge
 * ends.
 * <p>
 * This here is an abstract base class, which is instantiated via
 * the {@link #create(int, int)} method. This method decides
 * based on its two parameters {@code L} and {@code K} which
 * implementation to use. If {@code L} is small enough, then we
 * can just store one integer for each possible edge an
 * add/remove edges in O(1). This will be the most commonly used
 * implementation in reasonably-sized scenarios with, say, at
 * most some 10'000 nodes. If {@code L} is big, we use a more
 * compact representation which needed {@code (L+1)*K} integers
 * and allows adding/removing of edges in O(NK ln(K)).
 */
public abstract class DirectedEdgeMultiSet {

  /** the number of nodes */
  public final int length;

  /** the maximum number of edges */
  public final int maxEdgesPerNode;

  /**
   * Create the multiset.
   *
   * @param pLegth
   *          the number of nodes
   * @param pMaxEdgesPerNode
   *          the maximum number of edges per node
   */
  DirectedEdgeMultiSet(final int pLegth,
      final int pMaxEdgesPerNode) {
    super();
    if ((pLegth <= 1) || (pMaxEdgesPerNode <= 0)) {
      throw new IllegalArgumentException(//
          "Invalid values for L=" //$NON-NLS-1$
              + pLegth + " and K=" + pMaxEdgesPerNode);//$NON-NLS-1$
    }
    this.length = pLegth;
    this.maxEdgesPerNode = pMaxEdgesPerNode;
  }

  /** Clear the edge set */
  public abstract void clear();

  /**
   * Get the number of times the edge {@code (a,b)} is present
   *
   * @param a
   *          the starting node, in {@code -1..(L-1)}
   * @param b
   *          the end node, in {@code 0..(L-1)}
   * @return the number of times the edge {@code (a,b)} is
   *         registered
   */
  public abstract int getEdgeCount(final int a, final int b);

  /**
   * Add the edge {@code (a,b)} to the edge multiset
   *
   * @param a
   *          the starting node, in {@code -1..(L-1)}
   * @param b
   *          the end node, in {@code 0..(L-1)}
   */
  public abstract void addEdge(final int a, final int b);

  /**
   * Remove the edge {@code (a,b)} from the edge multiset
   *
   * @param a
   *          the starting node, in {@code -1..(L-1)}
   * @param b
   *          the end node, in {@code 0..(L-1)}
   */
  public abstract void removeEdge(final int a, final int b);

  /**
   * Add a complete permutation to the edge multiset
   *
   * @param pi
   *          the permutation
   * @see #addEdge(int, int)
   */
  public final void addPermutation(final int[] pi) {
    int last = -1;
    for (final int p : pi) {
      this.addEdge(last, p);
      last = p;
    }
  }

  /**
   * Remove a complete permutation from the edge multiset
   *
   * @param pi
   *          the permutation
   * @see #removeEdge(int, int)
   */
  public final void removePermutation(final int[] pi) {
    int last = -1;
    for (final int p : pi) {
      this.removeEdge(last, p);
      last = p;
    }
  }

  /**
   * Create a suitable implementation of the
   * {@link DirectedEdgeMultiSet}
   *
   * @param pLength
   *          the number of nodes
   * @param pMaxEdgesPerNode
   *          the maximum number of edges per node
   * @return the new, empty multiset
   */
  public static final DirectedEdgeMultiSet
      create(final int pLength, final int pMaxEdgesPerNode) {
    if ((((pLength + 1L) * pLength) * 2L) < Integer.MAX_VALUE) {
      try {
        return new FastDirectedEdgeMultiSet(pLength,
            pMaxEdgesPerNode);
      } catch (@SuppressWarnings("unused") final OutOfMemoryError oome) {
        // ignore
      }
    }
    return new CompactDirectedEdgeMultiSet(pLength,
        pMaxEdgesPerNode);
  }
}
