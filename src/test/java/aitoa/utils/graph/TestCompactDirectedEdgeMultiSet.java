package aitoa.utils.graph;

/** Test the {@link CompactDirectedEdgeMultiSet} */
public class TestCompactDirectedEdgeMultiSet
    extends TestDirectedEdgeMultiSet {

  /** {@inheritDoc} */
  @Override
  protected final DirectedEdgeMultiSet create(final int L,
      final int K) {
    return new CompactDirectedEdgeMultiSet(L, K);
  }
}
