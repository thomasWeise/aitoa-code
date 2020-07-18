package aitoa.utils.graph;

/**
 * A multiset for edges, where each of the {@code L} nodes may
 * occur in at most {@code K} edges, which do not need to be
 * unique. The valid node IDs range from {@code 0} to
 * {@code L-1}, with a special node of id {@code -1} being
 * permitted only as starting node for edges but never at edge
 * ends.
 */
public abstract class DirectedEdgeMultiSet {

  /** the number of nodes */
  public final int L;

  /** the maximum number of edges */
  public final int K;

  /**
   * Create the multiset.
   *
   * @param _L
   *          the number of nodes
   * @param _K
   *          the maximum number of edges per node
   */
  DirectedEdgeMultiSet(final int _L, final int _K) {
    super();
    if ((_L <= 1) || (_K <= 0)) {
      throw new IllegalArgumentException(//
          "Invalid values for L=" //$NON-NLS-1$
              + _L + " and K=" + _K);//$NON-NLS-1$
    }
    this.L = _L;
    this.K = _K;
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
   * @param L
   *          the number of nodes
   * @param K
   *          the maximum number of edges per node
   * @return the new, empty multiset
   */
  public static final DirectedEdgeMultiSet create(final int L,
      final int K) {
    if ((((L + 1L) * L) * 2L) >= Integer.MAX_VALUE) {
      return new _CompactDirectedEdgeMultiSet(L, K);
    }
    return new _FastDirectedEdgeMultiSet(L, K);
  }
}
