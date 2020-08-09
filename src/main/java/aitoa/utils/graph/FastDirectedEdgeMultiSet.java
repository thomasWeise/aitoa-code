package aitoa.utils.graph;

import java.util.Arrays;

/**
 * A fast, but memory intense representation for an
 * {@linkplain DirectedEdgeMultiSet multiset for edges}
 */
final class FastDirectedEdgeMultiSet
    extends DirectedEdgeMultiSet {
  /** the number of times the edges exist in the set */
  private final int[] mEdges;

  /**
   * Create the multiset.
   *
   * @param pLength
   *          the number of nodes
   * @param pMaxEdgesPerNode
   *          the maximum number of edges per node
   */
  FastDirectedEdgeMultiSet(final int pLength,
      final int pMaxEdgesPerNode) {
    super(pLength, pMaxEdgesPerNode);
    this.mEdges = new int[(pLength + 1) * pLength];
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    Arrays.fill(this.mEdges, 0);
  }

  /** {@inheritDoc} */
  @Override
  public int getEdgeCount(final int a, final int b) {
    return this.mEdges[b + ((a + 1) * this.length)];
  }

  /** {@inheritDoc} */
  @Override
  public void addEdge(final int a, final int b) {
    if ((++this.mEdges[b
        + ((a + 1) * this.length)]) > this.maxEdgesPerNode) {
      throw new IllegalStateException(
          (("Edge (" + a) + ',') + b + //$NON-NLS-1$
              ") occurs more then " //$NON-NLS-1$
              + this.maxEdgesPerNode + " times.");//$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public void removeEdge(final int a, final int b) {
    if ((--this.mEdges[b + ((a + 1) * this.length)]) < 0) {
      throw new IllegalStateException(
          (("Edge (" + a) + ',') + b + //$NON-NLS-1$
              ") does not exist."); //$NON-NLS-1$
    }
  }
}
