package aitoa.utils.graph;

/** Test the {@link CompactDirectedEdgeMultiSet} */
public class TestDefaultDirectedEdgeMultiSet
    extends TestDirectedEdgeMultiSet {

  /** {@inheritDoc} */
  @Override
  protected final DirectedEdgeMultiSet create(final int pL,
      final int pK) {
    return DirectedEdgeMultiSet.create(pL, pK);
  }
}
