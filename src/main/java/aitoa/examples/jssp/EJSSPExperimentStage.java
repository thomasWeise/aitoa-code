package aitoa.examples.jssp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import aitoa.algorithms.EA;
import aitoa.algorithms.EAWithClearing;
import aitoa.algorithms.EDA;
import aitoa.algorithms.EDAWithClearing;
import aitoa.algorithms.HillClimber;
import aitoa.algorithms.HillClimber2;
import aitoa.algorithms.HillClimber2WithRestarts;
import aitoa.algorithms.HillClimberWithRestarts;
import aitoa.algorithms.HybridEDA;
import aitoa.algorithms.HybridEDAWithClearing;
import aitoa.algorithms.MA;
import aitoa.algorithms.MAWithClearing;
import aitoa.algorithms.RandomSampling;
import aitoa.algorithms.SimulatedAnnealing;
import aitoa.algorithms.SingleRandomSample;
import aitoa.algorithms.TemperatureSchedule;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IMetaheuristic;
import aitoa.utils.Experiment.IExperimentStage;

/** the stages of the JSSP experiment */
public enum EJSSPExperimentStage implements
    IExperimentStage<int[], JSSPCandidateSolution,
        JSSPMakespanObjectiveFunction,
        IMetaheuristic<int[], JSSPCandidateSolution>> {

  /** the first stage: random sampling */
  STAGE_1 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {
      return Stream.of(//
          SingleRandomSample::new, //
          RandomSampling::new);
    }
  },

  /**
   * the second stage: hill climbing and hill climbing with
   * restarts with the 1-swap operator
   */
  STAGE_2 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {
      return EJSSPExperimentStage._hillClimbers();
    }

    /**
     * Configure the black box process builder for the given
     * problem.
     *
     * @param builder
     *          the builder to configure
     */
    @Override
    public void configureBuilder(final BlackBoxProcessBuilder<
        int[], JSSPCandidateSolution> builder) {
      super.configureBuilder(builder);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperator1Swap());
    }
  },

  /**
   * the third stage: hill climbing and hill climbing with
   * restarts with the n-swap operator
   */
  STAGE_3 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {
      return EJSSPExperimentStage._hillClimbers();
    }

    /**
     * Configure the black box process builder for the given
     * problem.
     *
     * @param builder
     *          the builder to configure
     */
    @Override
    public void configureBuilder(final BlackBoxProcessBuilder<
        int[], JSSPCandidateSolution> builder) {
      super.configureBuilder(builder);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperatorNSwap());
    }
  },

  /**
   * the fourth stage: EAs with the 1-swap operator
   */
  STAGE_4 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {
      return Stream.concat(//
          EJSSPExperimentStage._eas(//
              new int[] { 128, 256, 512, 1024, 2048, 4096, 8192,
                  16384, 32768, 65536 }, //
              new double[] { 0, 0.05, 0.3 }, //
              false), //
          EJSSPExperimentStage._eas(//
              new int[] { 128, 256, 512, 1024, }, //
              new double[] { 0, 0.05, 0.3 }, //
              true));
    }

    /**
     * Configure the black box process builder for the given
     * problem.
     *
     * @param builder
     *          the builder to configure
     */
    @Override
    public void configureBuilder(final BlackBoxProcessBuilder<
        int[], JSSPCandidateSolution> builder) {
      super.configureBuilder(builder);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperator1Swap());
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
        final BlackBoxProcessBuilder<int[],
            JSSPCandidateSolution> builder,
        final JSSPMakespanObjectiveFunction problem) {
      super.configureBuilderForProblem(builder, problem);
      builder.setBinarySearchOperator(
          new JSSPBinaryOperatorSequence(problem.instance));
    }
  },

  /**
   * the fifth stage: EAs with the n-swap operator
   */
  STAGE_5 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {
      return Stream.concat(//
          EJSSPExperimentStage._eas(
              new int[] { 128, 256, 512, 1024, 2048, 4096, 8192,
                  16384, 16384, 32768, 65536 },
              new double[] { 0, 0.05, 0.3, 0.98 }, false),
          EJSSPExperimentStage._eas(
              new int[] { 4, 8, 16, 32, 64, 128, 256, 512, 1024,
                  2048, 4096, 8192, 16384, 16384, 32768, 65536 },
              new double[] { 0.05 }, true));
    }

    /**
     * Configure the black box process builder for the given
     * problem.
     *
     * @param builder
     *          the builder to configure
     */
    @Override
    public void configureBuilder(final BlackBoxProcessBuilder<
        int[], JSSPCandidateSolution> builder) {
      super.configureBuilder(builder);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperatorNSwap());
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
        final BlackBoxProcessBuilder<int[],
            JSSPCandidateSolution> builder,
        final JSSPMakespanObjectiveFunction problem) {
      super.configureBuilderForProblem(builder, problem);
      builder.setBinarySearchOperator(
          new JSSPBinaryOperatorSequence(problem.instance));
    }
  },

  /**
   * the sixth stage: simulated annealing with the 1-swap
   * operator
   */
  STAGE_6 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {
      final ArrayList<Supplier<
          IMetaheuristic<int[], JSSPCandidateSolution>>> list =
              new ArrayList<>();

      for (final double Ts : new double[] { 20d, 0.5d * 20d,
          0.25d * 20d }) {
        list.add(() -> new SimulatedAnnealing<>(
            new TemperatureSchedule.Logarithmic(Ts, 1)));
      } // end
        // start
        // temperature
      for (final double ep : new double[] { //
          0.25e-7d, //
          0.5e-7d, //
          1e-7d, //
          1.5e-7d, //
          2e-7d, //
          4e-7d, //
          8e-7d }) {
        list.add(() -> new SimulatedAnnealing<>(
            new TemperatureSchedule.Exponential(20d, ep)));
      } // end
        // epsilon

      return list.stream();
    }

    /**
     * Configure the black box process builder for the given
     * problem.
     *
     * @param builder
     *          the builder to configure
     */
    @Override
    public void configureBuilder(final BlackBoxProcessBuilder<
        int[], JSSPCandidateSolution> builder) {
      super.configureBuilder(builder);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperator1Swap());
    }
  },

  /**
   * the seventh stage: enumerating hill climbing with the 1-swap
   * operator
   */
  STAGE_7 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {
      return EJSSPExperimentStage._hc2();
    }

    /**
     * Configure the black box process builder for the given
     * problem.
     *
     * @param builder
     *          the builder to configure
     */
    @Override
    public void configureBuilder(final BlackBoxProcessBuilder<
        int[], JSSPCandidateSolution> builder) {
      super.configureBuilder(builder);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperator1Swap());
    }
  },

  /**
   * the eighth stage: enumerating hill climbing with the 1-swap
   * operator with randomized neighbor sampling
   */
  STAGE_8 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {
      return EJSSPExperimentStage._hc2();
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
        final BlackBoxProcessBuilder<int[],
            JSSPCandidateSolution> builder,
        final JSSPMakespanObjectiveFunction problem) {
      super.configureBuilderForProblem(builder, problem);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperator1SwapU(problem.instance));
    }
  },

  /**
   * the eighth stage: enumerating hill climbing with the 12-swap
   * operator
   */
  STAGE_9 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {
      return EJSSPExperimentStage._hc2();
    }

    /**
     * Configure the black box process builder for the given
     * problem.
     *
     * @param builder
     *          the builder to configure
     */
    @Override
    public void configureBuilder(final BlackBoxProcessBuilder<
        int[], JSSPCandidateSolution> builder) {
      super.configureBuilder(builder);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperator12Swap());
    }
  },

  /**
   * the tenth stage: memetic algorithms with the 1-swap operator
   * with randomized neighbor sampling
   */
  STAGE_10 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {

      final ArrayList<Supplier<
          IMetaheuristic<int[], JSSPCandidateSolution>>> list =
              new ArrayList<>();

      for (final int mu : new int[] { 8, 16 }) {
        for (final int ls : new int[] { Integer.MAX_VALUE,
            32 }) {
          list.add(() -> new MA<>(mu, mu, ls));
          list.add(() -> new MAWithClearing<>(mu, mu, ls));
        }
      }
      return list.stream();
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
        final BlackBoxProcessBuilder<int[],
            JSSPCandidateSolution> builder,
        final JSSPMakespanObjectiveFunction problem) {
      super.configureBuilderForProblem(builder, problem);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperator1SwapU(problem.instance));
      builder.setBinarySearchOperator(
          new JSSPBinaryOperatorSequence(problem.instance));
    }
  },

  /**
   * the eleventh stage: estimation of distribution algorithm
   */
  STAGE_11 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {

      final ArrayList<Supplier<
          IMetaheuristic<int[], JSSPCandidateSolution>>> list =
              new ArrayList<>();

      for (final int mu : new int[] { 2, 3, 4, 10, 1000 }) {
        list.add(() -> new EDAWithClearing<>(mu, 32768, //
            new JSSPUMDAModel(problem.instance)));
        list.add(() -> new EDAWithClearing<>(mu, 4096, //
            new JSSPUMDAModel(problem.instance)));

        for (int lambdaShift = 4; lambdaShift <= 18;
            lambdaShift++) {
          final int lambda = 1 << lambdaShift;
          if (mu < lambda) {
            if (lambda <= 256) {
              list.add(() -> new EDAWithClearing<>(mu, lambda, //
                  new JSSPUMDAModel(problem.instance)));
            }
            list.add(() -> new EDA<>(mu, lambda, //
                new JSSPUMDAModel(problem.instance)));
          }
        }
      }
      return list.stream();
    }
  },

  /**
   * the twelfth stage: estimation of distribution algorithm with
   * local search
   */
  STAGE_12 {

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @Override
    public
        Stream<Supplier<
            IMetaheuristic<int[], JSSPCandidateSolution>>>
        getAlgorithms(//
            final JSSPMakespanObjectiveFunction problem) {

      final ArrayList<Supplier<
          IMetaheuristic<int[], JSSPCandidateSolution>>> list =
              new ArrayList<>();

      for (int lambdaShift = 2; lambdaShift <= 6;
          lambdaShift++) {
        final int lambda = 1 << lambdaShift;
        for (final int mu : new int[] { 2, 4, 8, 16 }) {
          if (mu < lambda) {
            list.add(() -> new HybridEDAWithClearing<>(mu,
                lambda, Integer.MAX_VALUE, //
                new JSSPUMDAModel(problem.instance)));
            list.add(() -> new HybridEDA<>(mu, lambda,
                Integer.MAX_VALUE, //
                new JSSPUMDAModel(problem.instance)));
          }
        }
      }
      return list.stream();
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
        final BlackBoxProcessBuilder<int[],
            JSSPCandidateSolution> builder,
        final JSSPMakespanObjectiveFunction problem) {
      super.configureBuilderForProblem(builder, problem);
      builder.setUnarySearchOperator(//
          new JSSPUnaryOperator1SwapU(problem.instance));
    }
  };

  /** the instances to be used */
  public static final String[] INSTANCES = { //
      "abz7", //$NON-NLS-1$
      "la24", //$NON-NLS-1$
      "yn4", //$NON-NLS-1$
      "swv15" };//$NON-NLS-1$

  /**
   * create the problems
   *
   * @return the stream of problems
   */
  @Override
  public Stream<Supplier<JSSPMakespanObjectiveFunction>>
      getProblems() {
    return Arrays.stream(EJSSPExperimentStage.INSTANCES).map(//
        s -> () -> new JSSPMakespanObjectiveFunction(s));
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
      getRuns(final JSSPMakespanObjectiveFunction problem) {
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
      int[], JSSPCandidateSolution> builder) {
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
      final BlackBoxProcessBuilder<int[],
          JSSPCandidateSolution> builder,
      final JSSPMakespanObjectiveFunction problem) {
    final JSSPInstance inst =
        Objects.requireNonNull(problem.instance);
    builder.setNullarySearchOperator(//
        new JSSPNullaryOperator(inst));
    builder.setRepresentationMapping(
        new JSSPRepresentationMapping(inst));
    builder.setSearchSpace(new JSSPSearchSpace(inst));
    builder.setSolutionSpace(new JSSPSolutionSpace(inst));
    builder.setObjectiveFunction(problem);
  }

  /**
   * create the stream of hill climbers
   *
   * @return the stream of hill climbers
   */
  static final
      Stream<
          Supplier<IMetaheuristic<int[], JSSPCandidateSolution>>>
      _hillClimbers() {
    final ArrayList<Supplier<
        IMetaheuristic<int[], JSSPCandidateSolution>>> list =
            new ArrayList<>();

    list.add(HillClimber::new);
    for (int i = 7; i <= 18; i++) {
      final int pow = 1 << i;
      list.add(() -> new HillClimberWithRestarts<>(pow));
    }

    return (list.stream());
  }

  /**
   * create the stream of EAs
   *
   * @param populationSizes
   *          the population sizes
   * @param crossoverRates
   *          the crossover rates
   * @param withPruning
   *          should we run the experiment with pruning?
   * @return the stream of EAs
   */
  static final
      Stream<
          Supplier<IMetaheuristic<int[], JSSPCandidateSolution>>>
      _eas(final int[] populationSizes,
          final double[] crossoverRates,
          final boolean withPruning) {

    final ArrayList<Supplier<
        IMetaheuristic<int[], JSSPCandidateSolution>>> list =
            new ArrayList<>();

    for (final int ps : ((populationSizes == null)
        ? new int[] { 2048, 4096 } : populationSizes)) {
      for (final double cr : ((crossoverRates == null)
          ? new double[] { 0d, 0.05d, 0.3d } : crossoverRates)) {
        list.add(() -> new EA<>(cr, ps, ps));
        if (withPruning) {
          list.add(() -> new EAWithClearing<>(cr, ps, ps));
        }
      }
    }

    return list.stream();
  }

  /**
   * create the hill climbers that enumerate neighborhoods
   *
   * @return the hill climbers that enumerate neighborhoods
   */
  static final
      Stream<
          Supplier<IMetaheuristic<int[], JSSPCandidateSolution>>>
      _hc2() {
    return Stream.of(//
        HillClimber2::new, //
        HillClimber2WithRestarts::new);
  }

  /**
   * Get the stream of the experiment stages defined here
   *
   * @return the stream
   */
  static final Stream<Supplier<IExperimentStage<?, ?, ?, ?>>>
      stream() {
    return Arrays.stream(EJSSPExperimentStage.values())
        .map(s -> () -> s);
  }
}
