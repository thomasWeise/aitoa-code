package aitoa.utils.graph;

/** Test the {@link CompactDirectedEdgeMultiSet} */
public class TestCompactDirectedEdgeMultiSet
    extends TestDirectedEdgeMultiSet {

  /** {@inheritDoc} */
  @Override
  protected final DirectedEdgeMultiSet create(final int pL,
      final int pK) {
    return new CompactDirectedEdgeMultiSet(pL, pK);
  }
}
