package aitoa.examples.jssp;

import java.io.IOException;
import java.nio.file.Path;

import aitoa.algorithms.EA;
import aitoa.algorithms.EAWithPruning;
import aitoa.algorithms.EDA;
import aitoa.algorithms.HillClimber;
import aitoa.algorithms.HillClimber2;
import aitoa.algorithms.HillClimber2WithRestarts;
import aitoa.algorithms.HillClimberWithRestarts;
import aitoa.algorithms.HybridEDA;
import aitoa.algorithms.MA;
import aitoa.algorithms.MAWithPruning;
import aitoa.algorithms.RandomSampling;
import aitoa.algorithms.SimulatedAnnealing;
import aitoa.algorithms.SingleRandomSample;
import aitoa.algorithms.TemperatureSchedule;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.IModel;
import aitoa.structure.IUnarySearchOperator;
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
   */
  @SuppressWarnings("unchecked")
  public static final void main(final String[] args) {
    final Path out = IOUtils.canonicalizePath(
        (args.length > 0) ? args[0] : "results");//$NON-NLS-1$

    for (final String instId : JSSPExperiment.INSTANCES) {
      // load the instance
      final JSSPInstance inst = new JSSPInstance(instId);

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
          for (final double Ts : new double[] { 1000d, 100d,
              10d }) {
            JSSPExperiment.run(
                new SimulatedAnnealing<>(
                    new TemperatureSchedule.Logarithmic(Ts)),
                unary, null, inst, out);
            for (final double ep : new double[] { 0.001d, 0.01d,
                0.1d }) {
              JSSPExperiment.run(new SimulatedAnnealing<>(
                  new TemperatureSchedule.Exponential(Ts, ep)),
                  unary, null, inst, out);
            } // end epsilon
          } // end start temperature
        } // only use basic unary ops

// create the binary search operator
        final IBinarySearchOperator<int[]> binary =
            new JSSPOperatorBinarySequence(inst);
// evolutionary algorithms
        for (final int mu : new int[] { 16, 32, 64, 512, 2048,
            4096 }) {
          for (final int lambda : new int[] { mu }) {
            for (final double cr : new double[] { 0, 0.05,
                0.3 }) {
              if (!(unary instanceof JSSPUnaryOperator1SwapR)) {
// the plain EA
                JSSPExperiment.run(new EA<>(cr, mu, lambda),
                    unary, binary, inst, out);
// the EA with pruning, i.e., which enforces population diversity
                JSSPExperiment.run(
                    new EAWithPruning<>(cr, mu, lambda), unary,
                    binary, inst, out);
              } // only use basic unary ops
            } // end enumerate cr

            if (!(unary instanceof JSSPUnaryOperator1Swap)) {
              if (unary.canEnumerate()) {
// memetic algorithms here rely on enumeration and use cr=1
                JSSPExperiment.run(
                    new MAWithPruning<>(mu, lambda), unary,
                    binary, inst, out);
                JSSPExperiment.run(new MA<>(mu, lambda), unary,
                    binary, inst, out);
              } // end memetic algorithm
            } // only use randomized enumeration
          } // end lambda
        } // end mu

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
              JSSPExperiment.run(new EDA<>(mu, lambda, model),
                  unary, null, inst, out);

              if (unary.canEnumerate()) {
                if (!(unary instanceof JSSPUnaryOperator1Swap)) {
                  JSSPExperiment.run(
                      new HybridEDA<>(mu, lambda, model), unary,
                      null, inst, out);
                } // only use randomized enumeration
              } // can enumerate
            } // mu
          } // lambda
        } // models
      } // end unary operators
    } // end instances
  }

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
   */
  public static final void
      run(final IMetaheuristic<int[],
          JSSPCandidateSolution> algorithm,
          final IUnarySearchOperator<int[]> unary,
          final IBinarySearchOperator<int[]> binary,
          final JSSPInstance inst, final Path baseDir) {

    // create the process builder
    final BlackBoxProcessBuilder<int[],
        JSSPCandidateSolution> builder =
            new BlackBoxProcessBuilder<>();
// set the maximum runtime
    builder.setMaxTime(3L * 60L * 1000L);

    // create the algorithm directory
    final String algoName = Experiment
        .nameFromObjectsMerge(algorithm, unary, binary);
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
        .uniqueRandomSeeds(instName, 101)) {
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
}
