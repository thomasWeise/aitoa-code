package aitoa.utils.graph;

/** Test the {@link _CompactDirectedEdgeMultiSet} */
public class TestCompactDirectedEdgeMultiSet
    extends TestDirectedEdgeMultiSet {

  /** {@inheritDoc} */
  @Override
  protected final DirectedEdgeMultiSet create(final int L,
      final int K) {
    return new _CompactDirectedEdgeMultiSet(L, K);
  }
}
