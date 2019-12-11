package aitoa.examples.jssp;

import java.io.IOException;
import java.nio.file.Path;

import aitoa.algorithms.EA;
import aitoa.algorithms.EAWithFitness;
import aitoa.algorithms.EAWithPruning;
import aitoa.algorithms.EDA;
import aitoa.algorithms.EDAWithFitness;
import aitoa.algorithms.HillClimber;
import aitoa.algorithms.HillClimber2;
import aitoa.algorithms.HillClimber2WithRestarts;
import aitoa.algorithms.HillClimberWithRestarts;
import aitoa.algorithms.HybridEDA;
import aitoa.algorithms.HybridEDAWithFitness;
import aitoa.algorithms.IntFFA;
import aitoa.algorithms.MA;
import aitoa.algorithms.MAWithFitness;
import aitoa.algorithms.MAWithPruning;
import aitoa.algorithms.RandomSampling;
import aitoa.algorithms.SimulatedAnnealing;
import aitoa.algorithms.SingleRandomSample;
import aitoa.algorithms.TemperatureSchedule;
import aitoa.examples.jssp.trees.JSSPTreeRepresentationMapping;
import aitoa.examples.jssp.trees.JobStatistic;
import aitoa.searchSpaces.trees.Node;
import aitoa.searchSpaces.trees.NodeTypeSet;
import aitoa.searchSpaces.trees.NodeTypeSetBuilder;
import aitoa.searchSpaces.trees.TreeBinaryOperator;
import aitoa.searchSpaces.trees.TreeNullaryOperator;
import aitoa.searchSpaces.trees.TreeSpace;
import aitoa.searchSpaces.trees.TreeUnaryOperator;
import aitoa.searchSpaces.trees.math.ATan2;
import aitoa.searchSpaces.trees.math.Add;
import aitoa.searchSpaces.trees.math.Divide;
import aitoa.searchSpaces.trees.math.DoubleConstant;
import aitoa.searchSpaces.trees.math.MathFunction;
import aitoa.searchSpaces.trees.math.Max;
import aitoa.searchSpaces.trees.math.Min;
import aitoa.searchSpaces.trees.math.Multiply;
import aitoa.searchSpaces.trees.math.Subtract;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.IModel;
import aitoa.structure.IUnarySearchOperator;
import aitoa.structure.LogFormat;
import aitoa.utils.ConsoleIO;
import aitoa.utils.Experiment;
import aitoa.utils.IOUtils;
import aitoa.utils.RandomUtils;

/**
 * This is the main class used to generate all the data from our
 * JSSP experiments. It will run a very long time and is not
 * parallelized at all.
 */
public class JSSPExperiment {

  /** the instances to be used */
  public static final String[] INSTANCES = { //
      "abz7", //$NON-NLS-1$
      "la24", //$NON-NLS-1$
      "yn4", //$NON-NLS-1$
      "swv15" };//$NON-NLS-1$

  /**
   * Run all the experiments in a full-factorial experimental
   * design.
   *
   * @param args
   *          only first element considered: the destination path
   * @throws IOException
   *           if I/O fails
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static final void main(final String[] args)
      throws IOException {
    final Path out = IOUtils.canonicalizePath(
        (args.length > 0) ? args[0] : "results");//$NON-NLS-1$

    for (final String instId : JSSPExperiment.INSTANCES) {
      // load the instance
      final JSSPInstance inst = new JSSPInstance(instId);
      final int upperBound =
          JSSPExperiment.__get_makespan_upper_bound(inst);
      boolean plainEDADone = false;

// random samplers
      JSSPExperiment.run(new SingleRandomSample<>(), null, null,
          inst, out);
      JSSPExperiment.run(new RandomSampling<>(), null, null,
          inst, out);

      for (final IUnarySearchOperator<int[]> unary : //
      new IUnarySearchOperator[] { //
          new JSSPUnaryOperator1Swap(), //
          new JSSPUnaryOperator1SwapR(inst), //
// new JSSPUnaryOperator12Swap(),
          new JSSPUnaryOperatorNSwap() }) {

        if (!(unary instanceof JSSPUnaryOperator1SwapR)) {
// plain hill climbers which do not enumerate their neighborhood
          JSSPExperiment.run(new HillClimber<>(), unary, null,
              inst, out);

// hill climbers which do not enumerate neighborhood with
// restarts
          for (final double inc : new double[] { 0d, 0.05d }) {
            JSSPExperiment.run(
                new HillClimberWithRestarts<>(256, "256", inc), //$NON-NLS-1$
                unary, null, inst, out);
          } // end restart settings
        } // only use basic unary ops

// hill climbers with neighborhood enumeration
        if (unary.canEnumerate()) {
          JSSPExperiment.run(new HillClimber2<>(), unary, null,
              inst, out);
          JSSPExperiment.run(new HillClimber2WithRestarts<>(),
              unary, null, inst, out);
        } // end enumerable unary ops

// simulated annealing
        if (!(unary instanceof JSSPUnaryOperator1SwapR)) {
          for (final double Ts : new double[] { 20d, 0.5d * 20d,
              0.25d * 20d }) {
            JSSPExperiment.run(
                new SimulatedAnnealing<>(
                    new TemperatureSchedule.Logarithmic(Ts, 1)),
                unary, null, inst, out);
          } // end start temperature
          for (final double ep : new double[] { 2e-7d, 4e-7d,
              8e-7d }) {
            JSSPExperiment.run(new SimulatedAnnealing<>(
                new TemperatureSchedule.Exponential(20d, ep)),
                unary, null, inst, out);
          } // end epsilon
        } // only use basic unary ops

// create the binary search operator
        final IBinarySearchOperator[] binops =
            new IBinarySearchOperator[] {
                new JSSPBinaryOperatorSequence(inst),
                new JSSPBinaryOperatorUniform(inst) };
        for (final IBinarySearchOperator<
            int[]> binary : binops) {
// evolutionary algorithms
          for (final int mu : new int[] { 16, 32, 64, 512, 2048,
              4096 }) {
            for (final int lambda : new int[] { mu }) {
              for (final double cr : new double[] { 0, 0.05,
                  0.3 }) {
                if ((cr <= 0d) && (binary != binops[0])) {
                  continue; // test only binary op for cr=0
                }
                if (!(unary instanceof JSSPUnaryOperator1SwapR)) {
// the plain EA
                  JSSPExperiment.run(new EA<>(cr, mu, lambda),
                      unary, binary, inst, out);
// the EA with pruning, i.e., which enforces population diversity
                  JSSPExperiment.run(
                      new EAWithPruning<>(cr, mu, lambda), unary,
                      binary, inst, out);
// the EA with frequency fitness assignment
                  JSSPExperiment.run(
                      new EAWithFitness<>(cr, mu, lambda,
                          new IntFFA(upperBound)),
                      unary, binary, inst, out);
                } // only use basic unary ops
              } // end enumerate cr

              if (unary.canEnumerate()) {
                for (final int steps : new int[] {
                    Integer.MAX_VALUE, 10, 100 }) {
// memetic algorithms here rely on enumeration and use cr=1
                  JSSPExperiment.run(
                      new MAWithPruning<>(mu, lambda, steps),
                      unary, binary, inst, out);
                  JSSPExperiment.run(new MA<>(mu, lambda, steps),
                      unary, binary, inst, out);
                  // the EA with frequency fitness assignment
                  JSSPExperiment.run(
                      new MAWithFitness<>(mu, lambda, steps,
                          new IntFFA(upperBound)),
                      unary, binary, inst, out);
                }
              } // end memetic algorithm
            } // end lambda
          } // end mu
        } // end binary op

// the estimation of distribution algorithms
        for (final IModel<int[]> model : new IModel[] {
            new JSSPUMDAModel(inst) }) { // models for EDAs
// test different number of samples
          for (final int lambda : new int[] { 16, 64, 256,
              1024 }) {
            int[] mus;
            if (lambda > 16) {
              if (lambda > 64) {
                mus = new int[] { 1, 2, 4, 16, 64 };
              } else {
                mus = new int[] { 1, 2, 4, 16 };
              }
            } else {
              mus = new int[] { 1, 2, 4 };
            }
            for (final int mu : mus) {
              if (!plainEDADone) {
// only do EDA once
                JSSPExperiment.run(new EDA<>(mu, lambda, model),
                    null, null, inst, out);
// and the version with fitness
                JSSPExperiment.run(
                    new EDAWithFitness<>(mu, lambda, model,
                        new IntFFA(upperBound)),
                    null, null, inst, out);
              }

              if (unary.canEnumerate()) {
                if (!(unary instanceof JSSPUnaryOperator1SwapR)) {
                  for (final int steps : new int[] {
                      Integer.MAX_VALUE, 10, 100 }) {
                    JSSPExperiment.run(new HybridEDA<>(mu,
                        lambda, steps, model), unary, null, inst,
                        out);
                    JSSPExperiment.run(
                        new HybridEDAWithFitness<>(mu, lambda,
                            steps, model,
                            new IntFFA(upperBound)),
                        unary, null, inst, out);
                  }
                } // only use randomized enumeration
              } // can enumerate
            } // mu
          } // lambda
        } // models
        plainEDADone = true;
      } // end unary operators

      // random sampling with gp
      for (final int maxDepth : new int[] { 6, 8 }) {
        JSSPExperiment.__runGP(new RandomSampling<>(), maxDepth,
            inst, out);
        // evolutionary algorithms
        for (final int mu : new int[] { 16, 128, 1024 }) {
          for (final int lambda : new int[] { mu }) {
            for (final double cr : new double[] { 0.05, 0.3 }) {
              // the plain EA
              JSSPExperiment.__runGP(new EA<>(cr, mu, lambda),
                  maxDepth, inst, out);
// the EA with pruning, i.e., which enforces population diversity
              JSSPExperiment.__runGP(
                  new EAWithPruning<>(cr, mu, lambda), maxDepth,
                  inst, out);
              JSSPExperiment.__runGP(
                  new EAWithFitness<>(cr, mu, lambda,
                      new IntFFA(upperBound)),
                  maxDepth, inst, out);
            } // end enumerate cr
          } // end lambda
        } // end mu
      } // end maxDepth
    } // end instances
  }

  /** the maximum time */
  private static final long MAX_TIME = 3L * 60L * 1000L;

  /** the number of runs */
  private static final int N_RUNS = 101;

  /**
   * Apply a metaheuristic algorithm with the given operators to
   * the specified instance and log the results to the provided
   * directory.
   *
   * @param algorithm
   *          the algorithm
   * @param unary
   *          the unary search operator, or {@code null}
   * @param binary
   *          the binary search operator, or {@code null}
   * @param inst
   *          the instance
   * @param baseDir
   *          the base directory
   * @throws IOException
   *           if I/O fails
   */
  public static final void
      run(final IMetaheuristic<int[],
          JSSPCandidateSolution> algorithm,
          final IUnarySearchOperator<int[]> unary,
          final IBinarySearchOperator<int[]> binary,
          final JSSPInstance inst, final Path baseDir)
          throws IOException {

    // create the process builder
    final BlackBoxProcessBuilder<int[],
        JSSPCandidateSolution> builder =
            new BlackBoxProcessBuilder<>();
// set the maximum runtime
    builder.setMaxTime(JSSPExperiment.MAX_TIME);

    // create the algorithm directory
    final String algoName = Experiment.nameFromObjectsMerge(//
        algorithm, unary, binary);
    // create the instance bane
    final String instName =
        Experiment.nameFromObjectPrepare(inst);

    // setup the data
    final JSSPSearchSpace searchSpace =
        new JSSPSearchSpace(inst);
    builder.setSearchSpace(searchSpace);
    builder.setSolutionSpace(new JSSPSolutionSpace(inst));
    builder.setRepresentationMapping(
        new JSSPRepresentationMapping(inst));
    builder.setObjectiveFunction(
        new JSSPMakespanObjectiveFunction(inst));
    builder
        .setNullarySearchOperator(new JSSPNullaryOperator(inst));
    if (unary != null) {
      builder.setUnarySearchOperator(unary);
    }
    if (binary != null) {
      builder.setBinarySearchOperator(binary);
    }
    // iterate over the random seeds
    for (final long seed : RandomUtils
        .uniqueRandomSeeds(instName, JSSPExperiment.N_RUNS)) {
      final Path file =
          Experiment.logFile(baseDir, algoName, instName, seed);
      if (file == null) {
        ConsoleIO.stdout(((((("Logfile for run " + algoName)//$NON-NLS-1$
            + ';') + instName) + ';') + seed)
            + " already exists, skipping run."); //$NON-NLS-1$
      } else {
        ConsoleIO.stdout("Now performing run '"//$NON-NLS-1$
            + file + "'."); //$NON-NLS-1$

        builder.setRandSeed(seed);
        builder.setLogPath(file);
        try (final IBlackBoxProcess<int[],
            JSSPCandidateSolution> process = builder.get()) {
          algorithm.solve(process);
          process.printLogSection("ALGORITHM_SETUP", //$NON-NLS-1$
              (bw) -> {
                try {
                  algorithm.printSetup(bw);
                } catch (final IOException ioe) {
                  throw new RuntimeException(ioe);
                }
              });
        }
      }
    }
  }

  /**
   * Apply a metaheuristic algorithm with the given operators to
   * the specified instance and log the results to the provided
   * directory.
   *
   * @param algorithm
   *          the algorithm
   * @param inst
   *          the instance
   * @param maxDepth
   *          the maximum depth
   * @param baseDir
   *          the base directory
   * @throws IOException
   *           if I/O fails
   */
  private static final void __runGP(
      final IMetaheuristic<Node[],
          JSSPCandidateSolution> algorithm,
      final int maxDepth, final JSSPInstance inst,
      final Path baseDir) throws IOException {

    // create the process builder
    final BlackBoxProcessBuilder<Node[],
        JSSPCandidateSolution> builder =
            new BlackBoxProcessBuilder<>();
// set the maximum runtime
    builder.setMaxTime(JSSPExperiment.MAX_TIME);

    // create the algorithm directory
    final String algoName = Experiment.nameFromObjectsMerge(//
        "gp" + maxDepth, algorithm); //$NON-NLS-1$
    // create the instance bane
    final String instName =
        Experiment.nameFromObjectPrepare(inst);

    final TreeSpace searchSpace = new TreeSpace(maxDepth);

    final NodeTypeSetBuilder ntsb = new NodeTypeSetBuilder();
    final NodeTypeSetBuilder.Builder nodes =
        ntsb.rootNodeTypeSet();
    nodes.add(Add.class, nodes, nodes);
    nodes.add(ATan2.class, nodes, nodes);
    nodes.add(Divide.class, nodes, nodes);
    nodes.add(DoubleConstant.type());
    nodes.add(Max.class, nodes, nodes);
    nodes.add(Min.class, nodes, nodes);
    nodes.add(Multiply.class, nodes, nodes);
    nodes.add(Subtract.class, nodes, nodes);
    nodes.add(JobStatistic.type());
    final NodeTypeSet<MathFunction<double[][]>> root =
        ntsb.build();

    // setup the data
    builder.setSearchSpace(searchSpace);
    builder.setSolutionSpace(new JSSPSolutionSpace(inst));
    builder.setRepresentationMapping(
        new JSSPTreeRepresentationMapping(inst));
    builder.setObjectiveFunction(
        new JSSPMakespanObjectiveFunction(inst));
    builder.setNullarySearchOperator(
        new TreeNullaryOperator(root, maxDepth));

    builder
        .setUnarySearchOperator(new TreeUnaryOperator(maxDepth));

    builder.setBinarySearchOperator(
        new TreeBinaryOperator(maxDepth));

    // iterate over the random seeds
    for (final long seed : RandomUtils
        .uniqueRandomSeeds(instName, JSSPExperiment.N_RUNS)) {
      final Path file =
          Experiment.logFile(baseDir, algoName, instName, seed);
      if (file == null) {
        ConsoleIO.stdout(((((("Logfile for run " + algoName)//$NON-NLS-1$
            + ';') + instName) + ';') + seed)
            + " already exists, skipping run."); //$NON-NLS-1$
      } else {
        ConsoleIO.stdout("Now performing run '"//$NON-NLS-1$
            + file + "'."); //$NON-NLS-1$

        builder.setRandSeed(seed);
        builder.setLogPath(file);
        try (final IBlackBoxProcess<Node[],
            JSSPCandidateSolution> process = builder.get()) {
          algorithm.solve(process);
          process.printLogSection(
              LogFormat.ALGORITHM_SETUP_LOG_SECTION, (bw) -> {
                try {
                  algorithm.printSetup(bw);
                } catch (final IOException ioe) {
                  throw new RuntimeException(ioe);
                }
              });
        }
      }
    }
  }

  /**
   * Get the upper bound for the makespan of any solution for a
   * JSSP instance
   *
   * @param instance
   *          the instance
   * @return the upper bound
   */
  private static final int
      __get_makespan_upper_bound(final JSSPInstance instance) {
    return ((int) (new JSSPMakespanObjectiveFunction(instance)
        .upperBound() + 0.5));
  }
}
