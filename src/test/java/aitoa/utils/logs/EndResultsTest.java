package aitoa.utils.logs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.LogFormat;
import aitoa.utils.TempDir;

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
        for (char inst = 'W'; inst <= 'Z'; inst++) {
          final Path instDir =
              algoDir.resolve(Character.toString(inst));
          Files.createDirectories(instDir);

          for (int r = 1; r <= 6; r++) {

            final Path logFile = instDir.resolve(
                ((((Character.toString(algo) + '_') + inst)
                    + '_') + r) + LogFormat.FILE_SUFFIX);
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

      final Path endResultStatistics =
          EndResultStatistics.makeEndResultStatisticsTable(
              endResults, evalDir, null, null, false, false);

      EndResultStatistics.parseEndResultStatisticsTable(
          endResultStatistics, (a) -> {
            a.hashCode();
          }, false);

      final Map<String, Path> ertEcdf =
          ErtEcdf.makeErtEcdf(endResultStatistics, evalDir, true,
              null, null, null, false);

      Assert.assertEquals(('d' - 'a') + 2, ertEcdf.size());
      final Path ertEcdfDir = ertEcdf.get(null);
      Assert.assertNotNull(ertEcdfDir);

      ErtEcdf.parseErtEcdfFiles(ertEcdfDir, (s) -> ((a) -> {
        /* */ }), false);
    }
  }
}
