package aitoa.utils.graph;

/** Test the {@link _CompactDirectedEdgeMultiSet} */
public class TestDefaultDirectedEdgeMultiSet
    extends TestDirectedEdgeMultiSet {

  /** {@inheritDoc} */
  @Override
  protected final DirectedEdgeMultiSet create(final int L,
      final int K) {
    return DirectedEdgeMultiSet.create(L, K);
  }
}
