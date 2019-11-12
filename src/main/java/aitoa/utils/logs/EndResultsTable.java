package aitoa.utils.logs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import aitoa.structure.LogFormat;
import aitoa.utils.ConsoleIO;
import aitoa.utils.IOUtils;

/**
 * This class allows you to create a (potentially large) csv
 * table with the end results from all the runs.
 */
public final class EndResultsTable {

  /** the file name used for end results tables */
  public static final String FILE_NAME = "endResults.txt"; //$NON-NLS-1$
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
      "number.of.improvements";//$NON-NLS-1$
  /** the column with the budget time */
  public static final String COL_BUDGET_TIME = "budget.time";//$NON-NLS-1$
  /** the column with the fe budget */
  public static final String COL_BUDGET_FES = "budget.FEs";//$NON-NLS-1$
  /** the column with the goal f */
  public static final String COL_GOAL_F = "goal.f";//$NON-NLS-1$

  /** the internal header */
  private static final char[] HEADER = LogFormat.asComment(
      LogFormat.joinLogLine(EndResultsTable.COL_ALGORITHM,
          EndResultsTable.COL_INSTANCE, EndResultsTable.COL_SEED,
          EndResultsTable.COL_BEST_F,
          EndResultsTable.COL_TOTAL_TIME,
          EndResultsTable.COL_TOTAL_FES,
          EndResultsTable.COL_LAST_IMPROVEMENT_TIME,
          EndResultsTable.COL_LAST_IMPROVEMENT_FES,
          EndResultsTable.COL_NUMBER_OF_IMPROVEMENTS,
          EndResultsTable.COL_BUDGET_TIME,
          EndResultsTable.COL_BUDGET_FES,
          EndResultsTable.COL_GOAL_F))
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
    return EndResultsTable.makeEndResultsTable(inputFolder,
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
    return EndResultsTable.makeEndResultsTable(inputFolder,
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
    final Path in = IOUtils.canonicalizePath(inputFolder);
    if (!(Files.exists(in) && Files.isDirectory(in))) {
      throw new IOException(
          inputFolder + " is not a directory."); //$NON-NLS-1$
    }

    final Path out = IOUtils.canonicalizePath(outputFolder);
    if (Files.exists(out)) {
      if (!Files.isDirectory(out)) {
        throw new IOException(
            outputFolder + " is not a directory."); //$NON-NLS-1$
      }
    } else {
      Files.createDirectories(out);
    }

    final Path end = IOUtils.canonicalizePath(
        out.resolve(EndResultsTable.FILE_NAME));
    if (Files.exists(end)) {
      if (!Files.isRegularFile(end)) {
        throw new IOException(end + " is not a file."); //$NON-NLS-1$
      }
      if (keepExisting) {
        if (logProgressToConsole) {
          ConsoleIO.stdout("End result table '" + //$NON-NLS-1$
              end + "' found.");//$NON-NLS-1$
        }
      } else {
        if (logProgressToConsole) {
          ConsoleIO.stdout("End result table '" + //$NON-NLS-1$
              end
              + "' found, but will be deleted and re-created.");//$NON-NLS-1$
        }
        Files.delete(end);
      }
    }

    if (logProgressToConsole) {
      ConsoleIO
          .stdout("Now beginning to create end result table '" + //$NON-NLS-1$
              end + "'.");//$NON-NLS-1$
    }
    try (
        final BufferedWriter bw = Files.newBufferedWriter(end)) {

      bw.write(EndResultsTable.HEADER);
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
            LogParser.parseLogFile(file, line, line);

            bw.write(algoName);
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(instName);
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(line.m_seed);
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(EndResultsTable.__str(line.m_f_min));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_time_max));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_fe_max));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(
                Long.toString(line.m_time_last_improvement));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_fe_last_improvement));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_improvements));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_budgetTime));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(Long.toString(line.m_budgetFEs));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(EndResultsTable.__str(line.m_goalF));
            bw.newLine();
          }
        }
      }
    }

    if (logProgressToConsole) {
      ConsoleIO.stdout("Finished creating end result table '" + //$NON-NLS-1$
          end + "'.");//$NON-NLS-1$
    }
    return end;
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
  private static final class __Line implements
      LogParser.ILogPointConsumer, LogParser.ISetupConsumer {

    /** the random seed */
    String m_seed;
    /** the last improvement fe */
    long m_fe_last_improvement;
    /** the max fes */
    long m_fe_max;
    /** the last improvement time */
    long m_time_last_improvement;
    /** the max time */
    long m_time_max;
    /** the number of improvements */
    long m_improvements;
    /** the best f min */
    double m_f_min;
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

    /** {@inheritDoc} */
    @Override
    public final void acceptStandard(//
        final String randSeedString, //
        final long randSeedLong, //
        final long budgetFEs, //
        final long budgetTime, //
        final double goalF) {
      this.m_budgetFEs = budgetFEs;
      this.m_seed = randSeedString;
      this.m_budgetTime = budgetTime;
      this.m_goalF = goalF;
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final long fe_last_improvement,
        final long fe_max, final long time_last_improvement,
        final long time_max, final long improvements,
        final double f_min, final boolean is_improvement) {
      this.m_fe_last_improvement = fe_last_improvement;
      this.m_fe_max = fe_max;
      this.m_time_last_improvement = time_last_improvement;
      this.m_time_max = time_max;
      this.m_improvements = improvements;
      this.m_f_min = f_min;
    }
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    ConsoleIO
        .stdout("Welcome to the End-Result CSV Table Generator"); //$NON-NLS-1$
    if (args.length != 2) {
      ConsoleIO.stdout((s) -> {
        s.println(
            "You must provide two command line arguments: srcDir and dstDir.");//$NON-NLS-1$
        s.println(
            " srcDir is the directory with the recorded experiment results (log file root dir).");//$NON-NLS-1$
        s.println(
            " dstDir is the directory where the table should be written to.");//$NON-NLS-1$
      });
    }

    try {
      final Path in = IOUtils.canonicalizePath(args[0]);
      ConsoleIO.stdout(("srcDir = '" + in) + '\'');//$NON-NLS-1$
      final Path out = IOUtils.canonicalizePath(args[1]);
      ConsoleIO.stdout(("dstDir = '" + out) + '\'');//$NON-NLS-1$

      EndResultsTable.makeEndResultsTable(in, out, false);
    } catch (final Throwable error) {
      ConsoleIO.stderr(
          "An error occured while creating the end result tables.", //$NON-NLS-1$
          error);
    }
  }
}
