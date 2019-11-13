package aitoa.utils.logs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import aitoa.TempDir;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;

/** test the end results table generator */
public class EndResultsTest {

  /**
   * test the end results table generator
   *
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 500000)
  public void testRunExperimentAndParseLog() throws IOException {
    try (final TempDir dir = new TempDir()) {
      // create the process builder
      final BlackBoxProcessBuilder<boolean[],
          boolean[]> builder = Example.problem();

      final IMetaheuristic<boolean[], boolean[]> algorithm =
          Example.algorithm();

      final Path resultsDir = dir.getPath().resolve("results");//$NON-NLS-1$
      Files.createDirectories(resultsDir);

      for (char algo = 'a'; algo <= 'd'; algo++) {
        final Path algoDir =
            resultsDir.resolve(Character.toString(algo));
        Files.createDirectories(algoDir);
        for (char inst = 'X'; inst <= 'Z'; inst++) {
          final Path instDir =
              algoDir.resolve(Character.toString(inst));
          Files.createDirectories(instDir);

          for (int r = 1; r <= 6; r++) {

            final Path logFile = instDir.resolve(
                ((((Character.toString(algo) + '_') + inst)
                    + '_') + r) + ".txt"); //$NON-NLS-1$
            builder.setLogPath(logFile);
            builder.setRandomRandSeed();
            try (final IBlackBoxProcess<boolean[], boolean[]> p =
                builder.get()) {
              algorithm.solve(p);
            }
          }
        }
      }

      final Path evalDir = dir.getPath().resolve("evaluation");//$NON-NLS-1$
      Files.createDirectories(evalDir);

      final Path endResults = EndResults.makeEndResultsTable(
          resultsDir, evalDir, false, false);

      EndResults.parseEndResultsTable(endResults, (a) -> {
        a.hashCode();
      }, false);

      final Path endResultStatistics = EndResultStatistics
          .makeEndResultStatisticsTable(endResults, evalDir,
              (a) -> (a.bestF <= a.goalF), false, false);

      EndResultStatistics.parseEndResultStatisticsTable(
          endResultStatistics, (a) -> {
            a.hashCode();
          }, false);
    }
  }
}
