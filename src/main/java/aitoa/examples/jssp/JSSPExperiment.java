package aitoa.examples.jssp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import aitoa.algorithms.EA;
import aitoa.algorithms.EAWithPruning;
import aitoa.algorithms.EAWithRestarts;
import aitoa.algorithms.HillClimber;
import aitoa.algorithms.HillClimberWithRestarts;
import aitoa.algorithms.RandomSampling;
import aitoa.algorithms.SingleRandomSample;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBinarySearchOperator;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.IUnarySearchOperator;
import aitoa.utils.ConsoleIO;
import aitoa.utils.RandomUtils;

/** The jssp experiment runner */
public class JSSPExperiment {

  /** the instances to be used */
  public static final String[] INSTANCES = { //
      "abz7", //$NON-NLS-1$
      "la24", //$NON-NLS-1$
      "yn4", //$NON-NLS-1$
      "swv15" };//$NON-NLS-1$

  /**
   * Run the experiments
   *
   * @param args
   *          only first element considered: the dest path
   */
  @SuppressWarnings("unchecked")
  public static final void main(final String[] args) {
    final Path out;
    if (args.length > 0) {
      out = Paths.get(args[0]);
    } else {
      out = Paths.get("results");//$NON-NLS-1$
    }

    for (final String instId : JSSPExperiment.INSTANCES) {
      // load the instance
      final JSSPInstance inst = new JSSPInstance(instId);

// random samplers
      JSSPExperiment.run(new SingleRandomSample(), null, null,
          inst, out);
      JSSPExperiment.run(new RandomSampling(), null, null, inst,
          out);

      for (final IUnarySearchOperator<int[]> unary : //
      new IUnarySearchOperator[] { new JSSPUnaryOperator1Swap(),
          new JSSPUnaryOperatorNSwap() }) {

// hill climbers
        JSSPExperiment.run(new HillClimber(), unary, null, inst,
            out);

// hill climbers with restarts
        for (final double inc : new double[] { 0d, 0.05d }) {
          JSSPExperiment.run(
              new HillClimberWithRestarts(256, "256", inc), //$NON-NLS-1$
              unary, null, inst, out);
          JSSPExperiment.run(
              new HillClimberWithRestarts(inst.n * inst.m, "mxn", //$NON-NLS-1$
                  inc),
              unary, null, inst, out);
        }

// create the binary search operator
        final IBinarySearchOperator<int[]> binary =
            new JSSPOperatorBinarySequence(inst);
// plain EAs
        for (final double cr : new double[] { 0, 0.05, 0.3 }) {
          for (final int mu : new int[] { 512, 2048, 4096 }) {
            for (final int lambda : new int[] { mu }) {
              // ea
              JSSPExperiment.run(new EA(cr, mu, lambda), unary,
                  binary, inst, out);

              JSSPExperiment.run(
                  new EAWithPruning(cr, mu, lambda), unary,
                  binary, inst, out);

              for (final int genRs : new int[] { 16 }) {
                // ea with restarts
                JSSPExperiment.run(
                    new EAWithRestarts(cr, mu, lambda, genRs),
                    unary, binary, inst, out);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Run the metaheuristic algorithm
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
  public static final void run(final IMetaheuristic algorithm,
      final IUnarySearchOperator<int[]> unary,
      final IBinarySearchOperator<int[]> binary,
      final JSSPInstance inst, final Path baseDir) {

    // create the process builder
    final BlackBoxProcessBuilder<int[],
        JSSPCandidateSolution> builder =
            new BlackBoxProcessBuilder<>();
// set the maximum runtime
    builder.setMaxTime(3L * 60L * 1000L);

    try {
      // create the algorithm directory
      String algoName = algorithm.toString().replace('.', 'd');
      if (unary != null) {
        algoName += '_' + unary.toString().replace('.', 'd');
      }
      if (binary != null) {
        algoName += '_' + binary.toString().replace('.', 'd');
      }

      final Path adir =
          baseDir.resolve(algoName).toAbsolutePath();
      Files.createDirectories(adir);

      // create the instance directory
      final String instName = inst.id.replace('.', 'd');
      final Path idir = adir.resolve(instName).toAbsolutePath();
      Files.createDirectories(idir);

      // setup the data
      final JSSPSearchSpace searchSpace =
          new JSSPSearchSpace(inst);
      builder.setSearchSpace(searchSpace);
      builder.setSolutionSpace(new JSSPSolutionSpace(inst));
      builder.setRepresentationMapping(
          new JSSPRepresentationMapping(inst));
      builder.setObjectiveFunction(
          new JSSPMakespanObjectiveFunction(inst));
      builder.setNullarySearchOperator(
          new JSSPNullaryOperator(inst));
      if (unary != null) {
        builder.setUnarySearchOperator(unary);
      }
      if (binary != null) {
        builder.setBinarySearchOperator(binary);
      }
      // iterate over the random seeds
      for (final long seed : RandomUtils
          .uniqueRandomSeeds(instName, 101)) {
        final Path file = idir.resolve(algoName + '_' + instName
            + '_' + RandomUtils.randSeedToString(seed) //
            + ".txt"); //$NON-NLS-1$
        if (Files.exists(file)) {
          ConsoleIO.stdout("File '" + file //$NON-NLS-1$
              + "' already exists, skipping run."); //$NON-NLS-1$
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
    } catch (final IOException error) {
      throw new RuntimeException(error);
    }
  }

}
