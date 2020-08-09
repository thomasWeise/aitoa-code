package aitoa.examples.jssp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import aitoa.utils.Experiment;
import aitoa.utils.IOUtils;

/**
 * This is the main class used to generate all the data from our
 * JSSP experiments. It will run a very long time and is not
 * parallelized at all.
 */
public final class JSSPExperiment {

  /** forbidden */
  private JSSPExperiment() {
    throw new UnsupportedOperationException();
  }

  /**
   * Run all the experiments in a full-factorial experimental
   * design.
   *
   * @param args
   *          only first element considered: the destination path
   * @throws IOException
   *           if I/O fails
   */
  public static void main(final String[] args)
      throws IOException {
    final Path out = IOUtils.canonicalizePath(
        (args.length > 0) ? args[0] : "results");//$NON-NLS-1$

    final int processors =
        Math.min(Runtime.getRuntime().availableProcessors(),
            (args.length > 1) ? Integer.parseInt(args[1])
                : Integer.MAX_VALUE);

    Experiment.executeExperimentInParallel(//
        Stream.concat(EJSSPExperimentStage.stream(),
            EJSSPExperimentStageACO.stream()),
        out, processors);
  }
}
