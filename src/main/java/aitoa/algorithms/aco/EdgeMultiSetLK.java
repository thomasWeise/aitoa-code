package aitoa.algorithms.aco;

import java.util.Arrays;

/**
 * A multiset for edges, where each of the {@code L} nodes may
 * occur in at most {@code K} edges, which do not need to be
 * unique. The valid node IDs range from {@code 0} to
 * {@code L-1}, with a special node of id {@code -1} is permitted
 * only as starting node for edges but never at edge ends.
 */
final class EdgeMultiSetLK {
  /** the edges stored in the pheromone matrix */
  private final int[][] m_edges;
  /** the number of times these edges occur */
  private final int[][] m_counts;

  /**
   * Create the multiset.
   *
   * @param _L
   *          the number of nodes
   * @param _K
   *          the maximum number of edges per node
   */
  EdgeMultiSetLK(final int _L, final int _K) {
    super();
    if ((_L <= 1) || (_K <= 0) || (_K > _L)) {
      throw new IllegalArgumentException(//
          "Invalid values for L=" //$NON-NLS-1$
              + _L + " and K=" + _K);//$NON-NLS-1$
    }

    this.m_edges = new int[_L + 1][_K];
    this.m_counts = new int[_L + 1][_K];
  }

  /** Clear the edge set */
  void clear() {
    for (final int[] nodes : this.m_edges) {
      Arrays.fill(nodes, Integer.MAX_VALUE);
    }
    for (final int[] counts : this.m_counts) {
      Arrays.fill(counts, 0);
    }
  }

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
  int getEdgeCount(final int a, final int b) {
    final int i = Arrays.binarySearch(this.m_edges[a + 1], b);
    return (i < 0) ? 0 : this.m_counts[a + 1][i];
  }

  /**
   * Add the edge {@code (a,b)} to the edge multiset
   *
   * @param a
   *          the starting node, in {@code -1..(L-1)}
   * @param b
   *          the end node, in {@code 0..(L-1)}
   */
  void addEdge(final int a, final int b) {
    final int[] edges = this.m_edges[a + 1];
    final int[] counts = this.m_counts[a + 1];

    int i = Arrays.binarySearch(edges, b);
    if (i >= 0) { // (a,b) is already present:
      ++counts[i]; // increase its count
    } else {
      i = -(i + 1); // this is the insertion point
      final int dst = i + 1; // we need to move
      final int cpy = edges.length - dst; // the rest
      System.arraycopy(edges, i, edges, dst, cpy);
      edges[i] = b;
      System.arraycopy(counts, i, counts, dst, cpy);
      counts[i] = 1;
    }
  }

  /**
   * Remove the edge {@code (a,b)} from the edge multiset
   *
   * @param a
   *          the starting node, in {@code -1..(L-1)}
   * @param b
   *          the end node, in {@code 0..(L-1)}
   */
  void removeEdge(final int a, final int b) {
    final int[] edges = this.m_edges[a + 1];

    final int i = Arrays.binarySearch(edges, b);
    if (i >= 0) { // (a,b) is already present:
      final int[] counts = this.m_counts[a + 1];
      if ((--counts[i]) <= 0) {
// the edge reached count 0: remove it
        final int src = i + 1;
        final int last = edges.length - 1;
        final int cpy = last - i;
        System.arraycopy(edges, src, edges, i, cpy);
        edges[last] = Integer.MAX_VALUE;
        System.arraycopy(counts, src, counts, i, cpy);
        counts[last] = 0;
      }
    } else {
      throw new IllegalStateException(
          (("Edge (" + a) + ',') + b + //$NON-NLS-1$
              ") does not exist."); //$NON-NLS-1$
    }
  }

  /**
   * Add a complete permutation to the edge multiset
   *
   * @param pi
   *          the permutation
   * @see #addEdge(int, int)
   */
  void addPermutation(final int[] pi) {
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
  void removePermutation(final int[] pi) {
    int last = -1;
    for (final int p : pi) {
      this.removeEdge(last, p);
      last = p;
    }
  }
}
