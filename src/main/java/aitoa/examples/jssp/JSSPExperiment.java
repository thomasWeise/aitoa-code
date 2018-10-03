package aitoa.examples.jssp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import aitoa.algorithms.RandomSampling;
import aitoa.algorithms.SingleRandomSample;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.utils.ConsoleIO;
import aitoa.utils.RandomUtils;

/** The jssp experiment runner */
public class JSSPExperiment {

  /** the instances to be used */
  private static final String[] INSTANCES = { //
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
  public static final void main(final String[] args) {
    final Path out;
    if (args.length > 0) {
      out = Paths.get(args[0]);
    } else {
      out = Paths.get("results");//$NON-NLS-1$
    }

    JSSPExperiment.run(new SingleRandomSample(), out);
    JSSPExperiment.run(new RandomSampling(), out);
  }

  /**
   * Run the metaheuristic algorithm
   *
   * @param algorithm
   *          the algorithm
   * @param baseDir
   *          the base directory
   */
  public static final void run(final IMetaheuristic algorithm,
      final Path baseDir) {

    // create the process builder
    final BlackBoxProcessBuilder<int[],
        JSSPCandidateSolution> builder =
            new BlackBoxProcessBuilder<>();
// set the maximum runtime
    builder.setMaxTime(3L * 60L * 1000L);

    try {
      // create the algorithm directory
      final String algoName = algorithm.toString();
      final Path adir =
          baseDir.resolve(algoName).toAbsolutePath();
      Files.createDirectories(adir);
      for (final String instId : JSSPExperiment.INSTANCES) {
        // load the instance
        final JSSPInstance inst = new JSSPInstance(instId);

        // create the instance directory
        final String instName = inst.id;
        final Path idir =
            adir.resolve(instName).toAbsolutePath();
        Files.createDirectories(idir);

        // setup the data
        final JSSPSearchSpace searchSpace =
            new JSSPSearchSpace(inst);
        builder.setSearchSpace(searchSpace);
        builder.setSolutionSpace(new JSSPSolutionSpace(inst));
        builder.setRepresentationMapping(
            new JSSPRepresentationMapping(inst));
        builder.setObjectiveFunction(
            new JSSPMakespanObjectiveFunction());
        builder.setNullarySearchOperator(
            new JSSPNullaryOperator(inst));

        // iterate over the random seeds
        for (final long seed : RandomUtils
            .uniqueRandomSeeds(instName, 101)) {
          final Path file =
              idir.resolve(algoName + '_' + instName + '_'
                  + RandomUtils.randSeedToString(seed) //
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
            }
          }
        }
      }
    } catch (final IOException error) {
      throw new RuntimeException(error);
    }
  }

}
