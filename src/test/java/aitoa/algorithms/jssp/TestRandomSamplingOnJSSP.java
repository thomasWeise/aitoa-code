package aitoa.algorithms.jssp;

import aitoa.algorithms.RandomSampling;
import aitoa.structure.IMetaheuristic;

/**
 * Test the {@linkplain aitoa.algorithms.RandomSampling random
 * sampling algorithm} on the JSSP
 */
public class TestRandomSamplingOnJSSP
    extends TestMetaheuristicOnJSSP {

  /** {@inheritDoc} */
  @Override
  protected IMetaheuristic getAlgorithm() {
    return new RandomSampling();
  }
}
