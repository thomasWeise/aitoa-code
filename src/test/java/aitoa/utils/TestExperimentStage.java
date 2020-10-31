package aitoa.utils;

import java.io.CharArrayWriter;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import aitoa.ObjectTest;
import aitoa.TestTools;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.IObjectiveFunction;
import aitoa.utils.Experiment.IExperimentStage;

/**
 * Test an experiment stage
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
public abstract class TestExperimentStage<X, Y,
    P extends IObjectiveFunction<Y>,
    M extends IMetaheuristic<X, Y>,
    S extends IExperimentStage<X, Y, P, M>>
    extends ObjectTest<S> {

  /** create */
  protected TestExperimentStage() {
    super();
  }

  /**
   * test the experiment stage via the
   * {@link IExperimentStage#getProblems()} method
   */
  @Test(timeout = 3600000)
  public void testStage() {
    final S stage = this.getInstance();
    Assert.assertNotNull(stage);

    final BlackBoxProcessBuilder<X, Y> builder =
        new BlackBoxProcessBuilder<>();
    stage.configureBuilder(builder);

    final Stream<Supplier<P>> problems = stage.getProblems();
    Assert.assertNotNull(problems);

    problems.forEach(supplier -> {
      Assert.assertNotNull(supplier);
      final P problem = supplier.get();
      Assert.assertNotNull(problem);

      stage.configureBuilderForProblem(builder, problem);

      TestTools.assertGreater(
          Experiment.nameFromObjectPrepare(problem).length(), 0);
      TestTools.assertGreaterOrEqual(problem.upperBound(),
          problem.lowerBound());
      final int runs = stage.getRuns(problem);
      TestTools.assertGreaterOrEqual(runs, 0);
      if (runs > 0) {
        final Stream<Supplier<M>> algorithms =
            stage.getAlgorithms(problem);
        Assert.assertNotNull(algorithms);
        algorithms.forEach((asupplier) -> {
          Assert.assertNotNull(asupplier);
          final M algorithm = asupplier.get();
          Assert.assertNotNull(algorithm);
          try (final CharArrayWriter caw =
              new CharArrayWriter()) {
            algorithm.printSetup(caw);
          } catch (final Throwable error) {
            throw new AssertionError(
                "There should be no error here.", //$NON-NLS-1$
                error);
          }
        });
      }
    });
  }

  /** {@inheritDoc} */
  @Override
  protected void runAllTests() {
    super.runAllTests();
    this.testStage();
  }
}
