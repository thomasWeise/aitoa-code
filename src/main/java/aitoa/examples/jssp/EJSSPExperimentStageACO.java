package aitoa.examples.jssp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import aitoa.algorithms.EDA;
import aitoa.algorithms.EDAWithClearing;
import aitoa.algorithms.EDAWithFitness;
import aitoa.algorithms.HybridEDA;
import aitoa.algorithms.HybridEDAWithClearing;
import aitoa.algorithms.HybridEDAWithFitness;
import aitoa.examples.jssp.aco.JSSPACOIndividual;
import aitoa.examples.jssp.aco.JSSPACOMakespanObjectiveFunction;
import aitoa.examples.jssp.aco.JSSPACOSpace;
import aitoa.examples.jssp.aco.JSSPPACOModelAge;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IMetaheuristic;
import aitoa.utils.Experiment.IExperimentStage;

/** the stages of the JSSP experiment */
public enum EJSSPExperimentStageACO implements
    IExperimentStage<JSSPACOIndividual, JSSPACOIndividual,
        JSSPACOMakespanObjectiveFunction,
        IMetaheuristic<JSSPACOIndividual, JSSPACOIndividual>> {

  /** the first stage: random sampling */
  STAGE_ACO_1 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public Stream<Supplier<
        IMetaheuristic<JSSPACOIndividual, JSSPACOIndividual>>>
        getAlgorithms(//
            final JSSPACOMakespanObjectiveFunction problem) {
      final ArrayList<Supplier<IMetaheuristic<JSSPACOIndividual,
          JSSPACOIndividual>>> list = new ArrayList<>();

      for (final int mu : new int[] { 1 }) {
        for (final int lambda : new int[] { 1024 }) {
          for (final int K : new int[] { 4, 8, 16 }) {
            for (final double beta : new double[] { 1.5d, 2d,
                2.5d, 3d }) {
              for (final double q0 : new double[] { 0.1d, 0.5d,
                  0.9d }) {
                for (final double tauMax : new double[] { 1d }) {
                  list.add(() -> new EDA<>(mu, lambda,
                      new JSSPPACOModelAge(problem.getInstance(), //
                          K, q0, beta, tauMax)));
                }
              }
            }
          }
        }
      }

      return list.stream();
    }
  };

  /**
   * create the problems
   *
   * @return the stream of problems
   */
  @Override
  public Stream<Supplier<JSSPACOMakespanObjectiveFunction>>
      getProblems() {
    return Arrays.stream(EJSSPExperimentStage.INSTANCES).map(//
        s -> () -> new JSSPACOMakespanObjectiveFunction(s));
  }

  /**
   * get the number of runs
   *
   * @param problem
   *          the problem
   * @return the runs
   */
  @Override
  public int
      getRuns(final JSSPACOMakespanObjectiveFunction problem) {
    return 101;
  }

  /**
   * Configure the black box process builder.
   *
   * @param builder
   *          the builder to configure
   */
  @Override
  public void configureBuilder(final BlackBoxProcessBuilder<
      JSSPACOIndividual, JSSPACOIndividual> builder) {
    builder.setMaxTime(TimeUnit.MINUTES.toMillis(3L));
  }

  /**
   * Configure the black box process builder for the given
   * problem.
   *
   * @param builder
   *          the builder to configure
   * @param problem
   *          the problem
   */
  @Override
  public void configureBuilderForProblem(
      final BlackBoxProcessBuilder<JSSPACOIndividual,
          JSSPACOIndividual> builder,
      final JSSPACOMakespanObjectiveFunction problem) {
    final JSSPInstance inst =
        Objects.requireNonNull(problem.getInstance());
    builder.setSearchSpace(new JSSPACOSpace(inst));
    builder.setObjectiveFunction(problem);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public void configureBuilderForProblemAndAlgorithm(
      final BlackBoxProcessBuilder<JSSPACOIndividual,
          JSSPACOIndividual> builder,
      final JSSPACOMakespanObjectiveFunction problem,
      final IMetaheuristic<JSSPACOIndividual,
          JSSPACOIndividual> algorithm) {
    if (algorithm instanceof EDA) {
      builder.setNullarySearchOperator(((EDA) algorithm).model);
    } else {
      if (algorithm instanceof EDAWithFitness) {
        builder.setNullarySearchOperator(
            ((EDAWithFitness) algorithm).model);
      } else {
        if (algorithm instanceof EDAWithClearing) {
          builder.setNullarySearchOperator(
              ((EDAWithClearing) algorithm).model);
        } else {
          if (algorithm instanceof HybridEDA) {
            builder.setNullarySearchOperator(
                ((HybridEDA) algorithm).model);
          } else {
            if (algorithm instanceof HybridEDAWithFitness) {
              builder.setNullarySearchOperator(
                  ((HybridEDAWithFitness) algorithm).model);
            } else {
              if (algorithm instanceof HybridEDAWithClearing) {
                builder.setNullarySearchOperator(
                    ((HybridEDAWithClearing) algorithm).model);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Get the stream of the experiment stages defined here
   *
   * @return the stream
   */
  static final Stream<Supplier<IExperimentStage<?, ?, ?, ?>>>
      stream() {
    return Arrays.stream(EJSSPExperimentStageACO.values())
        .map(s -> () -> s);
  }
}
