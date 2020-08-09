package aitoa.utils.graph;

import java.util.Arrays;

/**
 * A compact representation for an
 * {@linkplain DirectedEdgeMultiSet multiset for edges}
 */
final class CompactDirectedEdgeMultiSet
    extends DirectedEdgeMultiSet {
  /** the stored edges */
  private final int[][] mEdges;
  /** the number of times these edges occur */
  private final int[][] mCounts;

  /**
   * Create the multiset.
   *
   * @param pLength
   *          the number of nodes
   * @param pMaxEdgesPerNode
   *          the maximum number of edges per node
   */
  CompactDirectedEdgeMultiSet(final int pLength,
      final int pMaxEdgesPerNode) {
    super(pLength, pMaxEdgesPerNode);
    this.mEdges = new int[pLength + 1][pMaxEdgesPerNode];
    this.mCounts = new int[pLength + 1][pMaxEdgesPerNode];
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    for (final int[] nodes : this.mEdges) {
      Arrays.fill(nodes, Integer.MAX_VALUE);
    }
    for (final int[] counts : this.mCounts) {
      Arrays.fill(counts, 0);
    }
  }

  /** {@inheritDoc} */
  @Override
  public int getEdgeCount(final int a, final int b) {
    final int i = Arrays.binarySearch(this.mEdges[a + 1], b);
    return (i < 0) ? 0 : this.mCounts[a + 1][i];
  }

  /** {@inheritDoc} */
  @Override
  public void addEdge(final int a, final int b) {
    final int[] edges = this.mEdges[a + 1];
    final int[] counts = this.mCounts[a + 1];

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

  /** {@inheritDoc} */
  @Override
  public void removeEdge(final int a, final int b) {
    final int[] edges = this.mEdges[a + 1];

    final int i = Arrays.binarySearch(edges, b);
    if (i >= 0) { // (a,b) is already present:
      final int[] counts = this.mCounts[a + 1];
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
}
