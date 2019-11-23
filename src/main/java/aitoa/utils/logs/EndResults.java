package aitoa.utils.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Consumer;

import aitoa.structure.LogFormat;
import aitoa.utils.Configuration;
import aitoa.utils.ConsoleIO;
import aitoa.utils.IOUtils;

/**
 * This class allows you to create a (potentially large) csv
 * table with the end results from all the runs.
 */
public final class EndResults {

  /** the file name used for end results tables */
  public static final String FILE_NAME = "endResults" //$NON-NLS-1$
      + LogFormat.FILE_SUFFIX;
  /** the column with the algorithm id */
  public static final String COL_ALGORITHM = "algorithm";//$NON-NLS-1$
  /** the column with the instance id */
  public static final String COL_INSTANCE = "instance";//$NON-NLS-1$
  /** the column with the seed */
  public static final String COL_SEED = "seed";//$NON-NLS-1$
  /** the column with the best f */
  public static final String COL_BEST_F = "best.f";//$NON-NLS-1$
  /** the column with the total time */
  public static final String COL_TOTAL_TIME = "total.time";//$NON-NLS-1$
  /** the column with the total fes */
  public static final String COL_TOTAL_FES = "total.fes";//$NON-NLS-1$
  /** the column with the last improvement time */
  public static final String COL_LAST_IMPROVEMENT_TIME =
      "last.improvement.time";//$NON-NLS-1$
  /** the column with the last improvement fes */
  public static final String COL_LAST_IMPROVEMENT_FES =
      "last.improvement.fes";//$NON-NLS-1$
  /** the column with the number of improvements */
  public static final String COL_NUMBER_OF_IMPROVEMENTS =
      "n.improvements";//$NON-NLS-1$
  /** the column with the budget time */
  public static final String COL_BUDGET_TIME = "budget.time";//$NON-NLS-1$
  /** the column with the fe budget */
  public static final String COL_BUDGET_FES = "budget.FEs";//$NON-NLS-1$
  /** the column with the goal f */
  public static final String COL_GOAL_F = "goal.f";//$NON-NLS-1$

  /** the internal header */
  private static final char[] HEADER = LogFormat
      .asComment(LogFormat.joinLogLine(EndResults.COL_ALGORITHM,
          EndResults.COL_INSTANCE, EndResults.COL_SEED,
          EndResults.COL_BEST_F, EndResults.COL_TOTAL_TIME,
          EndResults.COL_TOTAL_FES,
          EndResults.COL_LAST_IMPROVEMENT_TIME,
          EndResults.COL_LAST_IMPROVEMENT_FES,
          EndResults.COL_NUMBER_OF_IMPROVEMENTS,
          EndResults.COL_BUDGET_TIME, EndResults.COL_BUDGET_FES,
          EndResults.COL_GOAL_F))
      .toCharArray();

  /**
   * Create the end results table.
   *
   * @param inputFolder
   *          the input folder
   * @param outputFolder
   *          the output folder
   * @return the path to the end results table
   * @throws IOException
   *           if i/o fails
   */
  public static final Path makeEndResultsTable(
      final Path inputFolder, final Path outputFolder)
      throws IOException {
    return EndResults.makeEndResultsTable(inputFolder,
        outputFolder, true);
  }

  /**
   * Create the end results table.
   *
   * @param inputFolder
   *          the input folder
   * @param outputFolder
   *          the output folder
   * @param keepExisting
   *          if the end results table exists, should it be
   *          preserved?
   * @return the path to the end results table
   * @throws IOException
   *           if i/o fails
   */
  public static final Path makeEndResultsTable(
      final Path inputFolder, final Path outputFolder,
      final boolean keepExisting) throws IOException {
    return EndResults.makeEndResultsTable(inputFolder,
        outputFolder, keepExisting, true);
  }

  /**
   * Create the end results table.
   *
   * @param inputFolder
   *          the input folder
   * @param outputFolder
   *          the output folder
   * @param keepExisting
   *          if the end results table exists, should it be
   *          preserved?
   * @param logProgressToConsole
   *          should logging information be printed?
   * @return the path to the end results table
   * @throws IOException
   *           if i/o fails
   */
  public static final Path makeEndResultsTable(
      final Path inputFolder, final Path outputFolder,
      final boolean keepExisting,
      final boolean logProgressToConsole) throws IOException {

    final Path in = IOUtils.requireDirectory(inputFolder);

    final Path out =
        IOUtils.requireDirectory(outputFolder, true);

    final Path end = IOUtils
        .canonicalizePath(out.resolve(EndResults.FILE_NAME));
    if (Files.exists(end)) {
      if (!Files.isRegularFile(end)) {
        throw new IOException(end + " is not a file."); //$NON-NLS-1$
      }
      if (keepExisting) {
        if (logProgressToConsole) {
          ConsoleIO.stdout("End result table '" + //$NON-NLS-1$
              end + "' found.");//$NON-NLS-1$
        }
        return end;
      }
      if (logProgressToConsole) {
        ConsoleIO.stdout("End result table '" + //$NON-NLS-1$
            end
            + "' found, but will be deleted and re-created.");//$NON-NLS-1$
      }
      Files.delete(end);
    }

    if (logProgressToConsole) {
      ConsoleIO
          .stdout("Now beginning to create end result table '" + //$NON-NLS-1$
              end + "'.");//$NON-NLS-1$
    }
    try (
        final BufferedWriter bw = Files.newBufferedWriter(end)) {

      bw.write(EndResults.HEADER);
      bw.newLine();

      final Path[] algorithms = IOUtils.subDirectories(in);
      if (logProgressToConsole) {
        ConsoleIO.stdout("Found " + algorithms.length + //$NON-NLS-1$
            " potential algorithm directories.");//$NON-NLS-1$
      }

      for (final Path algorithm : algorithms) {
        final String algoName =
            algorithm.getFileName().toString().trim();
        if (logProgressToConsole) {
          ConsoleIO
              .stdout("Now processing algorithm '" + algoName + //$NON-NLS-1$
                  "'.");//$NON-NLS-1$
        }

        final Path[] instances =
            IOUtils.subDirectories(algorithm);
        for (final Path instance : instances) {
          final String instName =
              instance.getFileName().toString().trim();
          if (logProgressToConsole) {
            ConsoleIO
                .stdout("Now processing instance '" + instName + //$NON-NLS-1$
                    "' for algorithm '" + algoName //$NON-NLS-1$
                    + "'.");//$NON-NLS-1$
          }

          for (final Path file : IOUtils
              .pathArray(IOUtils.filesStream(instance) //
                  .filter((ff) -> ff.getFileName().toString()
                      .endsWith(".txt"))//$NON-NLS-1$
              )) {

            final __Line line = new __Line();
            LogParser.parseLogFile(file, line,
                (s) -> line._acceptSetup(s));

            bw.write(algoName);
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(instName);
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(line.m_seed);
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(EndResults.__str(line.m_lastLine.f_min));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_lastLine.time_max));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_lastLine.fe_max));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(
                line.m_lastLine.time_last_improvement));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long
                .toString(line.m_lastLine.fe_last_improvement));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(
                Long.toString(line.m_lastLine.improvements));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_budgetTime));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_budgetFEs));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(EndResults.__str(line.m_goalF));
            bw.newLine();
          }
        }
      }
    }

    if (logProgressToConsole) {
      ConsoleIO.stdout("Finished creating end result table '" + //$NON-NLS-1$
          end + "'.");//$NON-NLS-1$
    }
    return IOUtils.requireFile(end);
  }

  /**
   * an NaN value was encountered
   *
   * @param d
   *          the double
   * @return the string
   */
  private static final String __str(final double d) {
    if (Double.isNaN(d)) {
      throw new IllegalStateException("NaN value encountered."); //$NON-NLS-1$
    }

    return LogFormat.doubleToStringForLog(d);
  }

  /** the holder for a line */
  private static final class __Line
      implements Consumer<LogLine> {

    /** the random seed */
    String m_seed;
    /** the last log line */
    LogLine m_lastLine;
    /** the FEs budget */
    long m_budgetFEs;
    /** the time budget */
    long m_budgetTime;
    /** the goal objective value */
    double m_goalF;

    /** create */
    __Line() {
      super();
    }

    /**
     * accept setup data
     *
     * @param setup
     *          the setup
     */
    final void _acceptSetup(//
        final SetupData setup) {
      this.m_budgetFEs = setup.budgetFEs;
      this.m_seed = setup.randSeedString;
      this.m_budgetTime = setup.budgetTime;
      this.m_goalF = setup.goalF;
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final LogLine t) {
      this.m_lastLine = Objects.requireNonNull(t);
    }
  }

  /**
   * Read and verify the end results table.
   *
   * @param path
   *          the path to end results table
   * @param consumer
   *          the consumer for the data.
   * @param logProgressToConsole
   *          should logging information be printed?
   * @throws IOException
   *           if i/o fails
   */
  public static final void parseEndResultsTable(final Path path,
      final Consumer<EndResult> consumer,
      final boolean logProgressToConsole) throws IOException {

    final Path p = IOUtils.requireFile(path);

    if (consumer == null) {
      throw new NullPointerException(//
          "null end result consumer");//$NON-NLS-1$
    }

    try (final BufferedReader br = Files.newBufferedReader(p)) {
      String algorithm = null;
      String instance = null;
      String seed = null;
      double bestF = Double.POSITIVE_INFINITY;
      long totalTime = -1L;
      long totalFEs = -1L;
      long lastImprovementTime = -1L;
      long lastImprovementFE = -1L;
      long numberOfImprovements = -1L;
      long budgetTime = -1L;
      long budgetFEs = -1L;
      double goalF = Double.POSITIVE_INFINITY;

      String line2;
      int lineIndex = 0;

      while ((line2 = br.readLine()) != null) {
        ++lineIndex;
        if (line2.isEmpty()) {
          continue;
        }
        final String line = line2.trim();
        if (line.isEmpty()) {
          continue;
        }
        if (line.charAt(0) == LogFormat.COMMENT_CHAR) {
          continue;
        }

        try {
          int lastSemi = -1;
          int nextSemi =
              line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                  ++lastSemi);
          algorithm = line.substring(lastSemi, nextSemi).trim();
          if (algorithm.isEmpty()) {
            throw new IllegalArgumentException(
                "Algorithm ID must be specified."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          instance = line.substring(lastSemi, nextSemi).trim();
          if (instance.isEmpty()) {
            throw new IllegalArgumentException(
                "Instance ID must be specified."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          seed = line.substring(lastSemi, nextSemi).trim();
          if ((seed.length() < 3) || (!seed
              .startsWith(LogFormat.RANDOM_SEED_PREFIX))) {
            throw new IllegalArgumentException(
                "Random seed invalid, must start with " + //$NON-NLS-1$
                    LogFormat.RANDOM_SEED_PREFIX);
          }
          try {
            Long.parseUnsignedLong(seed.substring(2), 16);
          } catch (final Throwable error2) {
            throw new IllegalArgumentException(
                "Invalid random seed: '" + seed + //$NON-NLS-1$
                    "'.", //$NON-NLS-1$
                error2);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF)) {
            throw new IllegalArgumentException(
                "Invalid best-F value: " + bestF); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (totalTime < 0L) {
            throw new IllegalArgumentException(
                "Invalid total time: " + totalTime); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (totalFEs < 1L) {
            throw new IllegalArgumentException(
                "Invalid total FEs: " + totalFEs); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (lastImprovementTime < 0L) {
            throw new IllegalArgumentException(
                "Invalid last improvement time: " //$NON-NLS-1$
                    + lastImprovementTime);
          }
          if (lastImprovementTime > totalTime) {
            throw new IllegalArgumentException(
                "Last last improvement time " //$NON-NLS-1$
                    + lastImprovementTime
                    + " cannot be bigger than total time "//$NON-NLS-1$
                    + totalTime);

          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (lastImprovementFE < 1L) {
            throw new IllegalArgumentException(
                "Invalid last improvement FEs: " //$NON-NLS-1$
                    + lastImprovementFE);
          }
          if (lastImprovementFE > totalFEs) {
            throw new IllegalArgumentException(
                "Last last improvement FEs " //$NON-NLS-1$
                    + lastImprovementFE
                    + " cannot be bigger than total FEs "//$NON-NLS-1$
                    + totalFEs);

          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (numberOfImprovements < 1L) {
            throw new IllegalArgumentException(
                "Invalid number of improvements: " //$NON-NLS-1$
                    + numberOfImprovements);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetTime = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (budgetTime < 0L) {
            throw new IllegalArgumentException(
                "Invalid time budget: " //$NON-NLS-1$
                    + budgetTime);
          }
          LogParser._checkTime(totalTime, budgetTime);
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetFEs = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (budgetFEs < 1L) {
            throw new IllegalArgumentException(
                "Invalid last FE budget: " //$NON-NLS-1$
                    + budgetFEs);
          }
          if (totalFEs > budgetFEs) {
            throw new IllegalArgumentException("Last total FEs " //$NON-NLS-1$
                + totalFEs + " cannot be bigger than FEs budget "//$NON-NLS-1$
                + budgetFEs);

          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          if (nextSemi > lastSemi) {
            throw new IllegalArgumentException(
                "line has too many columns");//$NON-NLS-1$
          }
          nextSemi = line.length();
          goalF = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if ((!Double.isFinite(goalF))
              && (!(goalF == Double.NEGATIVE_INFINITY))) {
            throw new IllegalArgumentException(
                "Invalid goal-F value: " + goalF); //$NON-NLS-1$
          }

          consumer.accept(
              new EndResult(algorithm, instance, seed, bestF,
                  totalTime, totalFEs, lastImprovementTime,
                  lastImprovementFE, numberOfImprovements,
                  budgetTime, budgetFEs, goalF));

        } catch (final Throwable error2) {
          throw new IOException(//
              "Line " + lineIndex //$NON-NLS-1$
                  + " is invalid: '" //$NON-NLS-1$
                  + line2 + "'.", //$NON-NLS-1$
              error2);
        }
      }
    } catch (final Throwable error) {
      throw new IOException(
          "Error when parsing end results  file '"//$NON-NLS-1$
              + p + "'.", //$NON-NLS-1$
          error);
    }
  }

  /** the source directory parameter */
  private static final String PARAM_SRC_DIR = "src"; //$NON-NLS-1$
  /** the destination directory parameter */
  private static final String PARAM_DST_DIR = "dest";//$NON-NLS-1$

  /**
   * print the arguments
   *
   * @param s
   *          the print stream
   */
  static final void _printArgs(final PrintStream s) {
    s.println(' ' + EndResults.PARAM_SRC_DIR
        + "=sourceDir: is the directory with the recorded experiment results (log file root dir).");//$NON-NLS-1$
    s.println(' ' + EndResults.PARAM_DST_DIR
        + "=destDir: is the directory where the table should be written to.");//$NON-NLS-1$
  }

  /**
   * get the input path
   *
   * @return the input path
   */
  static final Path _argIn() {
    return Configuration.getPath(EndResults.PARAM_SRC_DIR,
        () -> Paths.get("results"));//$NON-NLS-1$ ;
  }

  /**
   * get the output path
   *
   * @return the output path
   */
  static final Path _argOut() {
    return Configuration.getPath(EndResults.PARAM_DST_DIR,
        () -> Paths.get("evaluation"));//$NON-NLS-1$ ;
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    ConsoleIO.stdout((s) -> {
      s.println("Welcome to the End-Result CSV Table Generator"); //$NON-NLS-1$
      s.println("The command line arguments are as follows: ");//$NON-NLS-1$
      EndResults._printArgs(s);
      s.println(
          "If you do not set the arguments, defaults will be used.");//$NON-NLS-1$
    });

    Configuration.putCommandLine(args);

    final Path in = EndResults._argIn();
    final Path out = EndResults._argOut();

    Configuration.print();

    try {
      EndResults.makeEndResultsTable(in, out, false);
    } catch (final Throwable error) {
      ConsoleIO.stderr(
          "An error occured while creating the end result tables.", //$NON-NLS-1$
          error);
    }
  }
}
