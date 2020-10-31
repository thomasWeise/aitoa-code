package aitoa.utils;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.ObjectTest;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.IObjectiveFunction;
import aitoa.utils.Experiment.IExperimentStage;

/**
 * Test a stream of experiment stages
 *
 * @param <X>
 *          the search space type
 * @param <Y>
 *          the solution space type
 * @param <P>
 *          the problem type
 * @param <M>
 *          the metaheuristic type
 * @param <S>
 *          the experiment stage type
 */
@Ignore
public abstract class TestExperimentStages<X, Y,
    P extends IObjectiveFunction<Y>,
    M extends IMetaheuristic<X, Y>,
    S extends IExperimentStage<X, Y, P, M>>
    extends ObjectTest<Stream<S>> {

  /** create */
  protected TestExperimentStages() {
    super();
  }

  /**
   * test the experiment stage via the
   * {@link IExperimentStage#getProblems()} method
   */
  @Test(timeout = 3600000)
  public void testStages() {
    final Stream<S> stages = this.getInstance();
    Assert.assertNotNull(stages);

    stages.forEach(stage -> {
      new TestExperimentStage<X, Y, P, M, S>() {
        @Override
        protected S getInstance() {
          return stage;
        }
      }.runAllTests();
    });
  }

  /** {@inheritDoc} */
  @Override
  protected void runAllTests() {
    super.runAllTests();
    this.testStages();
  }
}
