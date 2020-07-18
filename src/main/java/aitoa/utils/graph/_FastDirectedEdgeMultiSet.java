package aitoa.utils.graph;

import java.util.Arrays;

/**
 * A fast, but memory intense representation for an
 * {@linkplain DirectedEdgeMultiSet multiset for edges}
 */
final class _FastDirectedEdgeMultiSet
    extends DirectedEdgeMultiSet {
  /** the number of times the edges exist in the set */
  private final int[] m_edges;

  /**
   * Create the multiset.
   *
   * @param _L
   *          the number of nodes
   * @param _K
   *          the maximum number of edges per node
   */
  _FastDirectedEdgeMultiSet(final int _L, final int _K) {
    super(_L, _K);
    this.m_edges = new int[(_L + 1) * _L];
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    Arrays.fill(this.m_edges, 0);
  }

  /** {@inheritDoc} */
  @Override
  public int getEdgeCount(final int a, final int b) {
    return this.m_edges[b + ((a + 1) * this.L)];
  }

  /** {@inheritDoc} */
  @Override
  public void addEdge(final int a, final int b) {
    if ((++this.m_edges[b + ((a + 1) * this.L)]) > this.K) {
      throw new IllegalStateException(
          (("Edge (" + a) + ',') + b + //$NON-NLS-1$
              ") occurs more then " //$NON-NLS-1$
              + this.K + " times.");//$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public void removeEdge(final int a, final int b) {
    if ((--this.m_edges[b + ((a + 1) * this.L)]) < 0) {
      throw new IllegalStateException(
          (("Edge (" + a) + ',') + b + //$NON-NLS-1$
              ") does not exist."); //$NON-NLS-1$
    }
  }
}
