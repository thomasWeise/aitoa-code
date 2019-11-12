package aitoa.utils.logs;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import aitoa.TempDir;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;

/** test the log parser */
public class LogParserTest {

  /**
   * test the log parser
   *
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("static-method")
  @Test(timeout = 100000)
  public void testRunExperimentAndParseLog() throws IOException {
    try (final TempDir dir = new TempDir()) {
      // create the process builder
      final BlackBoxProcessBuilder<boolean[],
          boolean[]> builder = Example.problem();

      final IMetaheuristic<boolean[], boolean[]> algorithm =
          Example.algorithm();

      final Path logFile = dir.getPath().resolve("log.txt"); //$NON-NLS-1$
      builder.setLogPath(logFile);
      builder.setRandomRandSeed();
      try (final IBlackBoxProcess<boolean[], boolean[]> p =
          builder.get()) {
        algorithm.solve(p);
      }

      LogParser.parseLogFile(logFile, null, null);
    }
  }
}
