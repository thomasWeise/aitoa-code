package aitoa.utils.graph;

/** Test the {@link CompactDirectedEdgeMultiSet} */
public class TestFastDirectedEdgeMultiSet
    extends TestDirectedEdgeMultiSet {

  /** {@inheritDoc} */
  @Override
  protected final DirectedEdgeMultiSet create(final int pL,
      final int pK) {
    return new FastDirectedEdgeMultiSet(pL, pK);
  }
}
