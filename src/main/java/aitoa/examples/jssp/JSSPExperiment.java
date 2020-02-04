package aitoa.examples.jssp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import aitoa.utils.Experiment;
import aitoa.utils.IOUtils;

/**
 * This is the main class used to generate all the data from our
 * JSSP experiments. It will run a very long time and is not
 * parallelized at all.
 */
public class JSSPExperiment {

  /**
   * Run all the experiments in a full-factorial experimental
   * design.
   *
   * @param args
   *          only first element considered: the destination path
   * @throws IOException
   *           if I/O fails
   */
  public static final void main(final String[] args)
      throws IOException {
    final Path out = IOUtils.canonicalizePath(
        (args.length > 0) ? args[0] : "results");//$NON-NLS-1$

    Experiment.executeExperimentInParallel(
        Arrays.stream(EJSSPExperimentStage.values())
            .map((s) -> () -> s),
        out, Math.max(1,
            Runtime.getRuntime().availableProcessors() >>> 1));
  }
}
