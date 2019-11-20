package aitoa.utils.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import aitoa.structure.LogFormat;
import aitoa.utils.Configuration;
import aitoa.utils.ConsoleIO;
import aitoa.utils.Experiment;
import aitoa.utils.IOUtils;
import aitoa.utils.Statistics;

/**
 * With this class we create a CSV file with end result
 * statistics.
 * <p>
 * Warning: This class assumes that the goals used to compute the
 * ERTs are somehow the global optima. In other words, it is not
 * possible to make improvements after the goals are reached. If
 * this assumption does not hold, then the ERTs computed here
 * cannot be used and will be wrong.
 */
public class EndResultStatistics {

  /** the file name used for end result statistics tables */
  public static final String FILE_NAME = "endResultStatistics" //$NON-NLS-1$
      + LogFormat.FILE_SUFFIX;

  /** the column with the number of successes */
  public static final String COL_RUNS = "n.runs";//$NON-NLS-1$
  /** the column with the number of successes */
  public static final String COL_SUCCESSES = "n.successes";//$NON-NLS-1$
  /** the column with the ert time */
  public static final String COL_ERT_TIME = "ert.time";//$NON-NLS-1$
  /** the column with the ert fes */
  public static final String COL_ERT_FES = "ert.fes";//$NON-NLS-1$

  /** the big quantiles */
  private static final double[] QUANTILES_BIG =
      { 0d, 0.05d, Statistics.GAUSSIAN_QUANTILE_159, 0.25d, 0.5d,
          0.75d, Statistics.GAUSSIAN_QUANTILE_841, 0.95d, 1d };

  /**
   * create the default header for a given key
   *
   * @param key
   *          the key
   * @return the header
   */
  private static String __makeHeaderBig(final String key) {
    return LogFormat.joinLogLine(//
        key + ".min", //$NON-NLS-1$
        key + ".q050", //$NON-NLS-1$
        key + ".q159", //$NON-NLS-1$
        key + ".q250", //$NON-NLS-1$
        key + ".median", //$NON-NLS-1$
        key + ".q750", //$NON-NLS-1$
        key + ".q841", //$NON-NLS-1$
        key + ".q950", //$NON-NLS-1$
        key + ".max", //$NON-NLS-1$
        key + ".mean", //$NON-NLS-1$
        key + ".sd");//$NON-NLS-1$
  }

  /**
   * print the big statistic
   *
   * @param data
   *          the data
   * @param quantiles
   *          the quantiles to use,
   * @param bw
   *          the destination
   * @return the mean
   * @throws IOException
   *           if i/o fails
   */
  private static final Number __printStat(final _Statistic data,
      final double[] quantiles, final BufferedWriter bw)
      throws IOException {
    for (final double d : quantiles) {
      bw.write(
          LogFormat.numberToStringForLog(data._quantile(d)));
      bw.write(LogFormat.CSV_SEPARATOR_CHAR);
    }
    final Number[] res = data._meanAndStdDev();
    final Number mean = Objects.requireNonNull(res[0]);
    bw.write(LogFormat.numberToStringForLog(mean));
    bw.write(LogFormat.CSV_SEPARATOR_CHAR);
    bw.write(LogFormat.numberToStringForLog(//
        Objects.requireNonNull(res[1])));
    return mean;
  }

  /** the big quantiles */
  private static final double[] QUANTILES_SMALL =
      { 0d, 0.5d, 1d };

  /**
   * create the default header for a given key
   *
   * @param key
   *          the key
   * @return the header
   */
  private static String __makeHeaderSmall(final String key) {
    return LogFormat.joinLogLine(//
        key + ".min", //$NON-NLS-1$
        key + ".median", //$NON-NLS-1$
        key + ".max", //$NON-NLS-1$
        key + ".mean", //$NON-NLS-1$
        key + ".sd");//$NON-NLS-1$
  }

  /** the internal header */
  private static final char[] HEADER =
      LogFormat.asComment(LogFormat.joinLogLine(//
          EndResults.COL_ALGORITHM, //
          EndResults.COL_INSTANCE, //
          EndResultStatistics.COL_RUNS,
          EndResultStatistics
              .__makeHeaderBig(EndResults.COL_BEST_F), //
          EndResultStatistics
              .__makeHeaderBig(EndResults.COL_TOTAL_TIME), //
          EndResultStatistics
              .__makeHeaderBig(EndResults.COL_TOTAL_FES), //
          EndResultStatistics.__makeHeaderBig(
              EndResults.COL_LAST_IMPROVEMENT_TIME), //
          EndResultStatistics.__makeHeaderBig(
              EndResults.COL_LAST_IMPROVEMENT_FES), //
          EndResultStatistics.__makeHeaderBig(
              EndResults.COL_NUMBER_OF_IMPROVEMENTS), //
          EndResultStatistics
              .__makeHeaderSmall(EndResults.COL_BUDGET_TIME), //
          EndResultStatistics
              .__makeHeaderSmall(EndResults.COL_BUDGET_FES), //
          EndResultStatistics.COL_SUCCESSES, //
          EndResultStatistics.COL_ERT_TIME, //
          EndResultStatistics.COL_ERT_FES//
      )).toCharArray();

  /**
   * Create the end result statistics table.
   *
   * @param endResults
   *          the path to the end results file
   * @param outputFolder
   *          the output folder
   * @param success
   *          the success predicate; If this is {@code null}, a
   *          run is considered successful if it's best-F value
   *          has reached or surpassed the goal-F value
   *          (downward-wise)
   * @param successID
   *          the id of the success predicate, can be
   *          {@code null} or empty to use default file name
   * @param keepExisting
   *          if the end result statistics table exists, should
   *          it be preserved, i.e., not computed anew?
   * @param logProgressToConsole
   *          should logging information be printed?
   * @return the path to the end results table
   * @throws IOException
   *           if i/o fails
   */
  public static final Path makeEndResultStatisticsTable(
      final Path endResults, final Path outputFolder,
      final Predicate<EndResult> success, final String successID,
      final boolean keepExisting,
      final boolean logProgressToConsole) throws IOException {

    final Path in = IOUtils.requireFile(endResults);
    final Path out =
        IOUtils.requireDirectory(outputFolder, true);

    final String baseName;
    if ((successID == null) || (successID.isEmpty())) {
      baseName = EndResultStatistics.FILE_NAME;
    } else {
      baseName = Experiment.nameStringsMerge(
          EndResultStatistics.FILE_NAME, successID);
    }

    final Path end =
        IOUtils.canonicalizePath(out.resolve(baseName));
    if (Files.exists(end)) {
      if (!Files.isRegularFile(end)) {
        throw new IOException(end + " is not a file."); //$NON-NLS-1$
      }
      if (keepExisting) {
        if (logProgressToConsole) {
          ConsoleIO.stdout("End result statistics table '" + //$NON-NLS-1$
              end + "' found.");//$NON-NLS-1$
        }
        return end;
      }
      if (logProgressToConsole) {
        ConsoleIO.stdout("End result statistics table '" + //$NON-NLS-1$
            end
            + "' found, but will be deleted and re-created.");//$NON-NLS-1$
      }
      Files.delete(end);
    }

    if (logProgressToConsole) {
      ConsoleIO.stdout(
          "Now beginning to load data from results table '" //$NON-NLS-1$
              + in + "'.");//$NON-NLS-1$
    }

    // compute the data
    __Parser p = new __Parser((success == null)
        ? (x) -> Double.compare(x.bestF, x.goalF) <= 0
        : success);
    EndResults.parseEndResultsTable(in, p, logProgressToConsole);
    final __Holder[] results = p._finalize();
    p = null;

    if (logProgressToConsole) {
      ConsoleIO.stdout(//
          "Finished loading data from results table '" //$NON-NLS-1$
              + in
              + "', now computing statistics and writing to file '"//$NON-NLS-1$
              + end + "'.");//$NON-NLS-1$
    }

    try (
        final BufferedWriter bw = Files.newBufferedWriter(end)) {

      bw.write(EndResultStatistics.HEADER);
      bw.newLine();

      for (int i = 0; i < results.length; i++) {
        __Holder h = results[i];
        results[i] = null;

        bw.write(h.algorithm);
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);
        bw.write(h.instance);
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);
        final int runs = h.m_bestF.size();
        if (runs <= 0) {
          throw new IllegalStateException(
              "no runs for algorithm '" + //$NON-NLS-1$
                  h.algorithm + "' on instance '" + //$NON-NLS-1$
                  h.instance + "'."); //$NON-NLS-1$
        }
        if (runs < h.m_successes) {
          throw new IllegalStateException(
              "more successes(" + h.m_successes//$NON-NLS-1$
                  + ") for algorithm '" + //$NON-NLS-1$
                  h.algorithm + "' on instance '" + //$NON-NLS-1$
                  h.instance + "' than runs (" //$NON-NLS-1$
                  + runs + ").");//$NON-NLS-1$
        }
        final boolean allSuccess = (runs == h.m_successes);
        bw.write(Integer.toString(runs));
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        EndResultStatistics.__printStat(h.m_bestF,
            EndResultStatistics.QUANTILES_BIG, bw);
        h.m_bestF = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_totalTime.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.__printStat(h.m_totalTime,
            EndResultStatistics.QUANTILES_BIG, bw);
        h.m_totalTime = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_totalFEs.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.__printStat(h.m_totalFEs,
            EndResultStatistics.QUANTILES_BIG, bw);
        h.m_totalFEs = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_lastImprovementTime.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        final Number lastImprovementTimeMean = //
            EndResultStatistics.__printStat(
                h.m_lastImprovementTime,
                EndResultStatistics.QUANTILES_BIG, bw);
        h.m_lastImprovementTime = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_lastImprovementFE.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        final Number lastImprovementFEMean = EndResultStatistics
            .__printStat(h.m_lastImprovementFE,
                EndResultStatistics.QUANTILES_BIG, bw);
        h.m_lastImprovementFE = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_numberOfImprovements.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.__printStat(h.m_numberOfImprovements,
            EndResultStatistics.QUANTILES_BIG, bw);
        h.m_numberOfImprovements = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_budgetTime.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.__printStat(h.m_budgetTime,
            EndResultStatistics.QUANTILES_SMALL, bw);
        h.m_budgetTime = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_budgetFEs.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.__printStat(h.m_budgetFEs,
            EndResultStatistics.QUANTILES_SMALL, bw);
        h.m_budgetFEs = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        bw.write(Integer.toString(h.m_successes));
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_successes > 0) {
          if (allSuccess) {
            bw.write(LogFormat
                .numberToStringForLog(lastImprovementTimeMean));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(LogFormat
                .numberToStringForLog(lastImprovementFEMean));
          } else {
            bw.write(LogFormat.numberToStringForLog(
                h.m_ertTime._divideSumBy(h.m_successes)));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(LogFormat.numberToStringForLog(
                h.m_ertFEs._divideSumBy(h.m_successes)));
          }
        } else {
          final String s =
              Double.toString(Double.POSITIVE_INFINITY);
          bw.write(s);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(s);
        }
        h.m_ertFEs = null;
        h.m_ertTime = null;
        h = null;

        bw.newLine();
      }
    }

    if (logProgressToConsole) {
      ConsoleIO.stdout(
          "Finished computing statistics and writing results table '" //$NON-NLS-1$
              + end + "'.");//$NON-NLS-1$
    }

    return IOUtils.requireFile(end);
  }

  /**
   * Read and verify the end result statistics table.
   *
   * @param path
   *          the path to end results table
   * @param consumer
   *          the consumer for the data
   * @param logProgressToConsole
   *          should logging information be printed?
   * @throws IOException
   *           if i/o fails
   */
  public static final void parseEndResultStatisticsTable(
      final Path path,
      final Consumer<EndResultStatistic> consumer,
      final boolean logProgressToConsole) throws IOException {

    final Path p = IOUtils.requireFile(path);

    if (consumer == null) {
      throw new NullPointerException(//
          "null end result consumer");//$NON-NLS-1$
    }

    try (final BufferedReader br = Files.newBufferedReader(p)) {
      final EndResultStatistic e = new EndResultStatistic();
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
          e.algorithm =
              line.substring(lastSemi, nextSemi).trim();
          if (e.algorithm.isEmpty()) {
            throw new IllegalArgumentException(
                "Algorithm ID must be specified."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.instance = line.substring(lastSemi, nextSemi).trim();
          if (e.instance.isEmpty()) {
            throw new IllegalArgumentException(
                "Instance ID must be specified."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.runs = Integer.parseInt(
              line.substring(lastSemi, nextSemi).trim());
          if (e.runs <= 0) {
            throw new IllegalArgumentException(
                "There cannot be " + e.runs //$NON-NLS-1$
                    + " runs."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.min = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.min)) {
            throw new IllegalArgumentException(
                "bestF.min must be finite, but is " //$NON-NLS-1$
                    + e.bestF.min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.q050)) {
            throw new IllegalArgumentException(
                "bestF.q050 must be finite, but is " //$NON-NLS-1$
                    + e.bestF.q050);
          }
          if (e.bestF.q050 < e.bestF.min) {
            throw new IllegalArgumentException(
                "bestF.q050 (" + e.bestF.q050 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.min ("//$NON-NLS-1$
                    + e.bestF.min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.q159)) {
            throw new IllegalArgumentException(
                "bestF.q159 must be finite, but is " //$NON-NLS-1$
                    + e.bestF.q159);
          }
          if (e.bestF.q159 < e.bestF.min) {
            throw new IllegalArgumentException(
                "bestF.q159 (" + e.bestF.q159 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q050 ("//$NON-NLS-1$
                    + e.bestF.q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.q250)) {
            throw new IllegalArgumentException(
                "bestF.q250 must be finite, but is " //$NON-NLS-1$
                    + e.bestF.q250);
          }
          if (e.bestF.q250 < e.bestF.q050) {
            throw new IllegalArgumentException(
                "bestF.q250 (" + e.bestF.q250 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q159 ("//$NON-NLS-1$
                    + e.bestF.q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.median)) {
            throw new IllegalArgumentException(
                "bestF.median must be finite, but is " //$NON-NLS-1$
                    + e.bestF.median);
          }
          if (e.bestF.median < e.bestF.q250) {
            throw new IllegalArgumentException(
                "bestF.median (" + e.bestF.median + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q250 ("//$NON-NLS-1$
                    + e.bestF.q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.q750)) {
            throw new IllegalArgumentException(
                "bestF.q750 must be finite, but is " //$NON-NLS-1$
                    + e.bestF.q750);
          }
          if (e.bestF.q750 < e.bestF.median) {
            throw new IllegalArgumentException(
                "bestF.q750 (" + e.bestF.q750 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.median ("//$NON-NLS-1$
                    + e.bestF.median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.q841)) {
            throw new IllegalArgumentException(
                "bestF.q841 must be finite, but is " //$NON-NLS-1$
                    + e.bestF.q841);
          }
          if (e.bestF.q841 < e.bestF.q750) {
            throw new IllegalArgumentException(
                "bestF.q841 (" + e.bestF.q841 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q750 ("//$NON-NLS-1$
                    + e.bestF.q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.q950)) {
            throw new IllegalArgumentException(
                "bestF.q950 must be finite, but is " //$NON-NLS-1$
                    + e.bestF.q950);
          }
          if (e.bestF.q950 < e.bestF.q841) {
            throw new IllegalArgumentException(
                "bestF.q950 (" + e.bestF.q950 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q841 ("//$NON-NLS-1$
                    + e.bestF.q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.max = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.max)) {
            throw new IllegalArgumentException(
                "bestF.max must be finite, but is " //$NON-NLS-1$
                    + e.bestF.max);
          }
          if (e.bestF.max < e.bestF.q950) {
            throw new IllegalArgumentException(
                "bestF.max (" + e.bestF.max + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q950 ("//$NON-NLS-1$
                    + e.bestF.q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.mean)) {
            throw new IllegalArgumentException(
                "bestF.mean must be finite, but is " //$NON-NLS-1$
                    + e.bestF.mean);
          }
          if ((e.bestF.max < e.bestF.mean)
              || (e.bestF.min > e.bestF.mean)) {
            throw new IllegalArgumentException(
                (("bestF.mean (" + e.bestF.mean + //$NON-NLS-1$
                    ") must be inside [bestF.min, bestF.max], i.e., ("//$NON-NLS-1$
                    + e.bestF.min) + ',') + e.bestF.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.bestF.sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.bestF.sd)
              || (e.bestF.sd < 0d)) {
            throw new IllegalArgumentException(
                "bestF.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + e.bestF.sd);
          }
          if ((e.bestF.max > e.bestF.min) == (e.bestF.sd <= 0d)) {
            throw new IllegalArgumentException(
                "bestF.sd=" + e.bestF.sd + //$NON-NLS-1$
                    " impossible for bestF.min=" + //$NON-NLS-1$
                    e.bestF.min + " and bestF.max="//$NON-NLS-1$
                    + e.bestF.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.totalTime.min < 0L) {
            throw new IllegalArgumentException(
                "totalTime.min must be >=0, but is " //$NON-NLS-1$
                    + e.totalTime.min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalTime.q050)) {
            throw new IllegalArgumentException(
                "totalTime.q050 must be finite, but is " //$NON-NLS-1$
                    + e.totalTime.q050);
          }
          if (e.totalTime.q050 < e.totalTime.min) {
            throw new IllegalArgumentException(
                "totalTime.q050 (" + e.totalTime.q050 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.min ("//$NON-NLS-1$
                    + e.totalTime.min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalTime.q159)) {
            throw new IllegalArgumentException(
                "totalTime.q159 must be finite, but is " //$NON-NLS-1$
                    + e.totalTime.q159);
          }
          if (e.totalTime.q159 < e.totalTime.q050) {
            throw new IllegalArgumentException(
                "totalTime.q159 (" + e.totalTime.q159 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q050 ("//$NON-NLS-1$
                    + e.totalTime.q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalTime.q250)) {
            throw new IllegalArgumentException(
                "totalTime.q250 must be finite, but is " //$NON-NLS-1$
                    + e.totalTime.q250);
          }
          if (e.totalTime.q250 < e.totalTime.q159) {
            throw new IllegalArgumentException(
                "totalTime.q250 (" + e.totalTime.q250 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q159 ("//$NON-NLS-1$
                    + e.totalTime.q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalTime.median)) {
            throw new IllegalArgumentException(
                "totalTime.median must be finite, but is " //$NON-NLS-1$
                    + e.totalTime.median);
          }
          if (e.totalTime.median < e.totalTime.q250) {
            throw new IllegalArgumentException(
                "totalTime.median (" + e.totalTime.median + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q250 ("//$NON-NLS-1$
                    + e.totalTime.q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalTime.q750)) {
            throw new IllegalArgumentException(
                "totalTime.q750 must be finite, but is " //$NON-NLS-1$
                    + e.totalTime.q750);
          }
          if (e.totalTime.q750 < e.totalTime.median) {
            throw new IllegalArgumentException(
                "totalTime.q750 (" + e.totalTime.q750 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.median ("//$NON-NLS-1$
                    + e.totalTime.median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalTime.q841)) {
            throw new IllegalArgumentException(
                "totalTime.q841 must be finite, but is " //$NON-NLS-1$
                    + e.totalTime.q841);
          }
          if (e.totalTime.q841 < e.totalTime.q750) {
            throw new IllegalArgumentException(
                "totalTime.q841 (" + e.totalTime.q841 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q750 ("//$NON-NLS-1$
                    + e.totalTime.q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalTime.q950)) {
            throw new IllegalArgumentException(
                "totalTime.q950 must be finite, but is " //$NON-NLS-1$
                    + e.totalTime.q950);
          }
          if (e.totalTime.q950 < e.totalTime.q841) {
            throw new IllegalArgumentException(
                "totalTime.q950 (" + e.totalTime.q950 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q841 ("//$NON-NLS-1$
                    + e.totalTime.q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.totalTime.max < 0L) {
            throw new IllegalArgumentException(
                "totalTime.max must be >=0, but is " //$NON-NLS-1$
                    + e.totalTime.max);
          }
          if (e.totalTime.max < e.totalTime.q950) {
            throw new IllegalArgumentException(
                "totalTime.max (" + e.totalTime.max + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q950 ("//$NON-NLS-1$
                    + e.totalTime.q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalTime.mean)) {
            throw new IllegalArgumentException(
                "totalTime.mean must be finite, but is " //$NON-NLS-1$
                    + e.totalTime.mean);
          }
          if ((e.totalTime.max < e.totalTime.mean)
              || (e.totalTime.min > e.totalTime.mean)) {
            throw new IllegalArgumentException(
                (("totalTime.mean (" + e.totalTime.mean + //$NON-NLS-1$
                    ") must be inside [totalTime.min, totalTime.max], i.e., ("//$NON-NLS-1$
                    + e.totalTime.min) + ',') + e.totalTime.max
                    + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalTime.sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalTime.sd)
              || (e.totalTime.sd < 0d)) {
            throw new IllegalArgumentException(
                "totalTime.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + e.totalTime.sd);
          }
          if ((e.totalTime.max > e.totalTime.min) == (e.totalTime.sd <= 0d)) {
            throw new IllegalArgumentException(
                "totalTime.sd=" + e.totalTime.sd + //$NON-NLS-1$
                    " impossible for totalTime.min=" + //$NON-NLS-1$
                    e.totalTime.min + " and totalTime.max="//$NON-NLS-1$
                    + e.totalTime.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.totalFEs.min <= 0L) {
            throw new IllegalArgumentException(
                "totalFEs.min must be >0, but is " //$NON-NLS-1$
                    + e.totalFEs.min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalFEs.q050)) {
            throw new IllegalArgumentException(
                "totalFEs.q050 must be finite, but is " //$NON-NLS-1$
                    + e.totalFEs.q050);
          }
          if (e.totalFEs.q050 < e.totalFEs.min) {
            throw new IllegalArgumentException(
                "totalFEs.q050 (" + e.totalFEs.q050 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.min ("//$NON-NLS-1$
                    + e.totalFEs.min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalFEs.q159)) {
            throw new IllegalArgumentException(
                "totalFEs.q159 must be finite, but is " //$NON-NLS-1$
                    + e.totalFEs.q159);
          }
          if (e.totalFEs.q159 < e.totalFEs.q050) {
            throw new IllegalArgumentException(
                "totalFEs.q159 (" + e.totalFEs.q159 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q050 ("//$NON-NLS-1$
                    + e.totalFEs.q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalFEs.q250)) {
            throw new IllegalArgumentException(
                "totalFEs.q250 must be finite, but is " //$NON-NLS-1$
                    + e.totalFEs.q250);
          }
          if (e.totalFEs.q250 < e.totalFEs.q159) {
            throw new IllegalArgumentException(
                "totalFEs.q250 (" + e.totalFEs.q250 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q159 ("//$NON-NLS-1$
                    + e.totalFEs.q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalFEs.median)) {
            throw new IllegalArgumentException(
                "totalFEs.median must be finite, but is " //$NON-NLS-1$
                    + e.totalFEs.median);
          }
          if (e.totalFEs.median < e.totalFEs.q250) {
            throw new IllegalArgumentException(
                "totalFEs.median (" + e.totalFEs.median + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q250 ("//$NON-NLS-1$
                    + e.totalFEs.q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalFEs.q750)) {
            throw new IllegalArgumentException(
                "totalFEs.q750 must be finite, but is " //$NON-NLS-1$
                    + e.totalFEs.q750);
          }
          if (e.totalFEs.q750 < e.totalFEs.median) {
            throw new IllegalArgumentException(
                "totalFEs.q750 (" + e.totalFEs.q750 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.median ("//$NON-NLS-1$
                    + e.totalFEs.median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalFEs.q841)) {
            throw new IllegalArgumentException(
                "totalFEs.q841 must be finite, but is " //$NON-NLS-1$
                    + e.totalFEs.q841);
          }
          if (e.totalFEs.q841 < e.totalFEs.q750) {
            throw new IllegalArgumentException(
                "totalFEs.q841 (" + e.totalFEs.q841 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q750 ("//$NON-NLS-1$
                    + e.totalFEs.q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalFEs.q950)) {
            throw new IllegalArgumentException(
                "totalFEs.q950 must be finite, but is " //$NON-NLS-1$
                    + e.totalFEs.q950);
          }
          if (e.totalFEs.q950 < e.totalFEs.q841) {
            throw new IllegalArgumentException(
                "totalFEs.q950 (" + e.totalFEs.q950 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q841 ("//$NON-NLS-1$
                    + e.totalFEs.q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.totalFEs.max <= 0L) {
            throw new IllegalArgumentException(
                "totalFEs.max must be >0, but is " //$NON-NLS-1$
                    + e.totalFEs.max);
          }
          if (e.totalFEs.max < e.totalFEs.q950) {
            throw new IllegalArgumentException(
                "totalFEs.max (" + e.totalFEs.max + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q950 ("//$NON-NLS-1$
                    + e.totalFEs.q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalFEs.mean)) {
            throw new IllegalArgumentException(
                "totalFEs.mean must be finite, but is " //$NON-NLS-1$
                    + e.totalFEs.mean);
          }
          if ((e.totalFEs.max < e.totalFEs.mean)
              || (e.totalFEs.min > e.totalFEs.mean)) {
            throw new IllegalArgumentException(
                (("totalFEs.mean (" + e.totalFEs.mean + //$NON-NLS-1$
                    ") must be inside [totalFEs.min, totalFEs.max], i.e., ("//$NON-NLS-1$
                    + e.totalFEs.min) + ',') + e.totalFEs.max
                    + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.totalFEs.sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.totalFEs.sd)
              || (e.totalFEs.sd < 0d)) {
            throw new IllegalArgumentException(
                "totalFEs.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + e.totalFEs.sd);
          }
          if ((e.totalFEs.max > e.totalFEs.min) == (e.totalFEs.sd <= 0d)) {
            throw new IllegalArgumentException(
                "totalFEs.sd=" + e.totalFEs.sd + //$NON-NLS-1$
                    " impossible for totalFEs.min=" + //$NON-NLS-1$
                    e.totalFEs.min + " and totalFEs.max="//$NON-NLS-1$
                    + e.totalFEs.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.lastImprovementTime.min < 0L) {
            throw new IllegalArgumentException(
                "lastImprovementTime.min must be >=0, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementTime.q050)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q050 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.q050);
          }
          if (e.lastImprovementTime.q050 < e.lastImprovementTime.min) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q050 (" //$NON-NLS-1$
                    + e.lastImprovementTime.q050
                    + ") must be greater or equal to lastImprovementTime.min ("//$NON-NLS-1$
                    + e.lastImprovementTime.min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementTime.q159)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q159 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.q159);
          }
          if (e.lastImprovementTime.q159 < e.lastImprovementTime.q050) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q159 (" //$NON-NLS-1$
                    + e.lastImprovementTime.q159
                    + ") must be greater or equal to lastImprovementTime.q050 (" //$NON-NLS-1$
                    + e.lastImprovementTime.q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementTime.q250)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q250 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.q250);
          }
          if (e.lastImprovementTime.q250 < e.lastImprovementTime.q159) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q250 (" //$NON-NLS-1$
                    + e.lastImprovementTime.q250
                    + ") must be greater or equal to lastImprovementTime.q159 ("//$NON-NLS-1$
                    + e.lastImprovementTime.q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementTime.median)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.median must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.median);
          }
          if (e.lastImprovementTime.median < e.lastImprovementTime.q250) {
            throw new IllegalArgumentException(
                "lastImprovementTime.median (" //$NON-NLS-1$
                    + e.lastImprovementTime.median
                    + ") must be greater or equal to lastImprovementTime.q250 ("//$NON-NLS-1$
                    + e.lastImprovementTime.q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementTime.q750)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q750 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.q750);
          }
          if (e.lastImprovementTime.q750 < e.lastImprovementTime.median) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q750 (" //$NON-NLS-1$
                    + e.lastImprovementTime.q750
                    + ") must be greater or equal to lastImprovementTime.median ("//$NON-NLS-1$
                    + e.lastImprovementTime.median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementTime.q841)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q841 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.q841);
          }
          if (e.lastImprovementTime.q841 < e.lastImprovementTime.q750) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q841 (" //$NON-NLS-1$
                    + e.lastImprovementTime.q841
                    + ") must be greater or equal to lastImprovementTime.q750 ("//$NON-NLS-1$
                    + e.lastImprovementTime.q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementTime.q950)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q950 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.q950);
          }
          if (e.lastImprovementTime.q950 < e.lastImprovementTime.q841) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q950 (" //$NON-NLS-1$
                    + e.lastImprovementTime.q950
                    + ") must be greater or equal to lastImprovementTime.q841 ("//$NON-NLS-1$
                    + e.lastImprovementTime.q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.lastImprovementTime.max < 0L) {
            throw new IllegalArgumentException(
                "lastImprovementTime.max must be >=0, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.max);
          }
          if (e.lastImprovementTime.max < e.lastImprovementTime.q950) {
            throw new IllegalArgumentException(
                "lastImprovementTime.max (" //$NON-NLS-1$
                    + e.lastImprovementTime.max
                    + ") must be greater or equal to lastImprovementTime.q950 ("//$NON-NLS-1$
                    + e.lastImprovementTime.q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementTime.mean)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.mean must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.mean);
          }
          if ((e.lastImprovementTime.max < e.lastImprovementTime.mean)
              || (e.lastImprovementTime.min > e.lastImprovementTime.mean)) {
            throw new IllegalArgumentException(
                (("lastImprovementTime.mean (" //$NON-NLS-1$
                    + e.lastImprovementTime.mean
                    + ") must be inside [lastImprovementTime.min, lastImprovementTime.max], i.e., ("//$NON-NLS-1$
                    + e.lastImprovementTime.min) + ',')
                    + e.lastImprovementTime.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementTime.sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementTime.sd)
              || (e.lastImprovementTime.sd < 0d)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + e.lastImprovementTime.sd);
          }
          if ((e.lastImprovementTime.max > e.lastImprovementTime.min) == (e.lastImprovementTime.sd <= 0d)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.sd=" //$NON-NLS-1$
                    + e.lastImprovementTime.sd
                    + " impossible for lastImprovementTime.min="//$NON-NLS-1$
                    + e.lastImprovementTime.min
                    + " and lastImprovementTime.max="//$NON-NLS-1$
                    + e.lastImprovementTime.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.lastImprovementFE.min <= 0L) {
            throw new IllegalArgumentException(
                "lastImprovementFE.min must be >0, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementFE.q050)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q050 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.q050);
          }
          if (e.lastImprovementFE.q050 < e.lastImprovementFE.min) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q050 (" //$NON-NLS-1$
                    + e.lastImprovementFE.q050
                    + ") must be greater or equal to lastImprovementFE.min ("//$NON-NLS-1$
                    + e.lastImprovementFE.min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementFE.q159)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q159 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.q159);
          }
          if (e.lastImprovementFE.q159 < e.lastImprovementFE.q050) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q159 (" //$NON-NLS-1$
                    + e.lastImprovementFE.q159
                    + ") must be greater or equal to lastImprovementFE.q050 ("//$NON-NLS-1$
                    + e.lastImprovementFE.q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementFE.q250)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q250 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.q250);
          }
          if (e.lastImprovementFE.q250 < e.lastImprovementFE.q159) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q250 (" //$NON-NLS-1$
                    + e.lastImprovementFE.q250
                    + ") must be greater or equal to lastImprovementFE.q159 ("//$NON-NLS-1$
                    + e.lastImprovementFE.q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementFE.median)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.median must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.median);
          }
          if (e.lastImprovementFE.median < e.lastImprovementFE.q250) {
            throw new IllegalArgumentException(
                "lastImprovementFE.median (" //$NON-NLS-1$
                    + e.lastImprovementFE.median
                    + ") must be greater or equal to lastImprovementFE.q250 ("//$NON-NLS-1$
                    + e.lastImprovementFE.q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementFE.q750)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q750 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.q750);
          }
          if (e.lastImprovementFE.q750 < e.lastImprovementFE.median) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q750 (" //$NON-NLS-1$
                    + e.lastImprovementFE.q750
                    + ") must be greater or equal to lastImprovementFE.median ("//$NON-NLS-1$
                    + e.lastImprovementFE.median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementFE.q841)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q841 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.q841);
          }
          if (e.lastImprovementFE.q841 < e.lastImprovementFE.q750) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q841 (" //$NON-NLS-1$
                    + e.lastImprovementFE.q841
                    + ") must be greater or equal to lastImprovementFE.q750 ("//$NON-NLS-1$
                    + e.lastImprovementFE.q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementFE.q950)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q950 must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.q950);
          }
          if (e.lastImprovementFE.q950 < e.lastImprovementFE.q841) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q950 (" //$NON-NLS-1$
                    + e.lastImprovementFE.q950
                    + ") must be greater or equal to lastImprovementFE.q841 ("//$NON-NLS-1$
                    + e.lastImprovementFE.q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.lastImprovementFE.max <= 0L) {
            throw new IllegalArgumentException(
                "lastImprovementFE.max must be >0, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.max);
          }
          if (e.lastImprovementFE.max < e.lastImprovementFE.q950) {
            throw new IllegalArgumentException(
                "lastImprovementFE.max (" //$NON-NLS-1$
                    + e.lastImprovementFE.max
                    + ") must be greater or equal to lastImprovementFE.q950 ("//$NON-NLS-1$
                    + e.lastImprovementFE.q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementFE.mean)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.mean must be finite, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.mean);
          }
          if ((e.lastImprovementFE.max < e.lastImprovementFE.mean)
              || (e.lastImprovementFE.min > e.lastImprovementFE.mean)) {
            throw new IllegalArgumentException(
                (("lastImprovementFE.mean (" //$NON-NLS-1$
                    + e.lastImprovementFE.mean
                    + ") must be inside [lastImprovementFE.min, lastImprovementFE.max], i.e., ("//$NON-NLS-1$
                    + e.lastImprovementFE.min) + ',')
                    + e.lastImprovementFE.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.lastImprovementFE.sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.lastImprovementFE.sd)
              || (e.lastImprovementFE.sd < 0d)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + e.lastImprovementFE.sd);
          }
          if ((e.lastImprovementFE.max > e.lastImprovementFE.min) == (e.lastImprovementFE.sd <= 0d)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.sd=" + e.lastImprovementFE.sd //$NON-NLS-1$
                    + " impossible for lastImprovementFE.min="//$NON-NLS-1$
                    + e.lastImprovementFE.min
                    + " and lastImprovementFE.max="//$NON-NLS-1$
                    + e.lastImprovementFE.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.numberOfImprovements.min <= 0L) {
            throw new IllegalArgumentException(
                "numberOfImprovements.min must be >0, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.numberOfImprovements.q050)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q050 must be finite, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.q050);
          }
          if (e.numberOfImprovements.q050 < e.numberOfImprovements.min) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q050 (" //$NON-NLS-1$
                    + e.numberOfImprovements.q050
                    + ") must be greater or equal to numberOfImprovements.min ("//$NON-NLS-1$
                    + e.numberOfImprovements.min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.numberOfImprovements.q159)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q159 must be finite, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.q159);
          }
          if (e.numberOfImprovements.q159 < e.numberOfImprovements.q050) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q159 (" //$NON-NLS-1$
                    + e.numberOfImprovements.q159
                    + ") must be greater or equal to numberOfImprovements.q050 ("//$NON-NLS-1$
                    + e.numberOfImprovements.q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.numberOfImprovements.q250)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q250 must be finite, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.q250);
          }
          if (e.numberOfImprovements.q250 < e.numberOfImprovements.q159) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q250 (" //$NON-NLS-1$
                    + e.numberOfImprovements.q250
                    + ") must be greater or equal to numberOfImprovements.q159 ("//$NON-NLS-1$
                    + e.numberOfImprovements.q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.numberOfImprovements.median)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.median must be finite, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.median);
          }
          if (e.numberOfImprovements.median < e.numberOfImprovements.q250) {
            throw new IllegalArgumentException(
                "numberOfImprovements.median (" //$NON-NLS-1$
                    + e.numberOfImprovements.median
                    + ") must be greater or equal to numberOfImprovements.q250 ("//$NON-NLS-1$
                    + e.numberOfImprovements.q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.numberOfImprovements.q750)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q750 must be finite, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.q750);
          }
          if (e.numberOfImprovements.q750 < e.numberOfImprovements.median) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q750 (" //$NON-NLS-1$
                    + e.numberOfImprovements.q750
                    + ") must be greater or equal to numberOfImprovements.median ("//$NON-NLS-1$
                    + e.numberOfImprovements.median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.numberOfImprovements.q841)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q841 must be finite, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.q841);
          }
          if (e.numberOfImprovements.q841 < e.numberOfImprovements.q750) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q841 (" //$NON-NLS-1$
                    + e.numberOfImprovements.q841
                    + ") must be greater or equal to numberOfImprovements.q750 ("//$NON-NLS-1$
                    + e.numberOfImprovements.q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.numberOfImprovements.q950)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q950 must be finite, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.q950);
          }
          if (e.numberOfImprovements.q950 < e.numberOfImprovements.q841) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q950 (" //$NON-NLS-1$
                    + e.numberOfImprovements.q950
                    + ") must be greater or equal to numberOfImprovements.q841 ("//$NON-NLS-1$
                    + e.numberOfImprovements.q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.numberOfImprovements.max <= 0L) {
            throw new IllegalArgumentException(
                "numberOfImprovements.max must be >0, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.max);
          }
          if (e.numberOfImprovements.max < e.numberOfImprovements.q950) {
            throw new IllegalArgumentException(
                "numberOfImprovements.max (" //$NON-NLS-1$
                    + e.numberOfImprovements.max
                    + ") must be greater or equal to numberOfImprovements.q950 ("//$NON-NLS-1$
                    + e.numberOfImprovements.q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.numberOfImprovements.mean)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.mean must be finite, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.mean);
          }
          if ((e.numberOfImprovements.max < e.numberOfImprovements.mean)
              || (e.numberOfImprovements.min > e.numberOfImprovements.mean)) {
            throw new IllegalArgumentException(
                (("numberOfImprovements.mean (" //$NON-NLS-1$
                    + e.numberOfImprovements.mean
                    + ") must be inside [numberOfImprovements.min, numberOfImprovements.max], i.e., ("//$NON-NLS-1$
                    + e.numberOfImprovements.min) + ',')
                    + e.numberOfImprovements.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.numberOfImprovements.sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.numberOfImprovements.sd)
              || (e.numberOfImprovements.sd < 0d)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + e.numberOfImprovements.sd);
          }
          if ((e.numberOfImprovements.max > e.numberOfImprovements.min) == (e.numberOfImprovements.sd <= 0d)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.sd=" //$NON-NLS-1$
                    + e.numberOfImprovements.sd
                    + " impossible for numberOfImprovements.min="//$NON-NLS-1$
                    + e.numberOfImprovements.min
                    + " and numberOfImprovements.max="//$NON-NLS-1$
                    + e.numberOfImprovements.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetTime.min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.budgetTime.min < 0L) {
            throw new IllegalArgumentException(
                "budgetTime.min must be >=0, but is " //$NON-NLS-1$
                    + e.budgetTime.min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetTime.median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.budgetTime.median)) {
            throw new IllegalArgumentException(
                "budgetTime.median must be finite, but is " //$NON-NLS-1$
                    + e.budgetTime.median);
          }
          if (e.budgetTime.median < e.budgetTime.min) {
            throw new IllegalArgumentException(
                "budgetTime.median (" + e.budgetTime.median + //$NON-NLS-1$
                    ") must be greater or equal to budgetTime.min ("//$NON-NLS-1$
                    + e.budgetTime.min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetTime.max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.budgetTime.max < 0L) {
            throw new IllegalArgumentException(
                "budgetTime.max must be >=0, but is " //$NON-NLS-1$
                    + e.budgetTime.max);
          }
          if (e.budgetTime.max < e.budgetTime.median) {
            throw new IllegalArgumentException(
                "budgetTime.max (" + e.budgetTime.max + //$NON-NLS-1$
                    ") must be greater or equal to budgetTime.median ("//$NON-NLS-1$
                    + e.budgetTime.median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetTime.mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.budgetTime.mean)) {
            throw new IllegalArgumentException(
                "budgetTime.mean must be finite, but is " //$NON-NLS-1$
                    + e.budgetTime.mean);
          }
          if ((e.budgetTime.max < e.budgetTime.mean)
              || (e.budgetTime.min > e.budgetTime.mean)) {
            throw new IllegalArgumentException(
                (("budgetTime.mean (" + e.budgetTime.mean + //$NON-NLS-1$
                    ") must be inside [budgetTime.min, budgetTime.max], i.e., ("//$NON-NLS-1$
                    + e.budgetTime.min) + ',') + e.budgetTime.max
                    + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetTime.sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.budgetTime.sd)
              || (e.budgetTime.sd < 0d)) {
            throw new IllegalArgumentException(
                "budgetTime.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + e.budgetTime.sd);
          }
          if ((e.budgetTime.max > e.budgetTime.min) == (e.budgetTime.sd <= 0d)) {
            throw new IllegalArgumentException(
                "budgetTime.sd=" + e.budgetTime.sd + //$NON-NLS-1$
                    " impossible for budgetTime.min=" + //$NON-NLS-1$
                    e.budgetTime.min + " and budgetTime.max="//$NON-NLS-1$
                    + e.budgetTime.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetFEs.min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.budgetFEs.min <= 0L) {
            throw new IllegalArgumentException(
                "budgetFEs.min must be >0, but is " //$NON-NLS-1$
                    + e.budgetFEs.min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetFEs.median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.budgetFEs.median)) {
            throw new IllegalArgumentException(
                "budgetFEs.median must be finite, but is " //$NON-NLS-1$
                    + e.budgetFEs.median);
          }
          if (e.budgetFEs.median < e.budgetFEs.min) {
            throw new IllegalArgumentException(
                "budgetFEs.median (" + e.budgetFEs.median + //$NON-NLS-1$
                    ") must be greater or equal to budgetFEs.min ("//$NON-NLS-1$
                    + e.budgetFEs.min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetFEs.max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (e.budgetFEs.max <= 0L) {
            throw new IllegalArgumentException(
                "budgetFEs.max must be >0, but is " //$NON-NLS-1$
                    + e.budgetFEs.max);
          }
          if (e.budgetFEs.max < e.budgetFEs.median) {
            throw new IllegalArgumentException(
                "budgetFEs.max (" + e.budgetFEs.max + //$NON-NLS-1$
                    ") must be greater or equal to budgetFEs.median ("//$NON-NLS-1$
                    + e.budgetFEs.median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetFEs.mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.budgetFEs.mean)) {
            throw new IllegalArgumentException(
                "budgetFEs.mean must be finite, but is " //$NON-NLS-1$
                    + e.budgetFEs.mean);
          }
          if ((e.budgetFEs.max < e.budgetFEs.mean)
              || (e.budgetFEs.min > e.budgetFEs.mean)) {
            throw new IllegalArgumentException(
                (("budgetFEs.mean (" + e.budgetFEs.mean + //$NON-NLS-1$
                    ") must be inside [budgetFEs.min, budgetFEs.max], i.e., ("//$NON-NLS-1$
                    + e.budgetFEs.min) + ',') + e.budgetFEs.max
                    + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.budgetFEs.sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(e.budgetFEs.sd)
              || (e.budgetFEs.sd < 0d)) {
            throw new IllegalArgumentException(
                "budgetFEs.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + e.budgetFEs.sd);
          }
          if ((e.budgetFEs.max > e.budgetFEs.min) == (e.budgetFEs.sd <= 0d)) {
            throw new IllegalArgumentException(
                "budgetFEs.sd=" + e.budgetFEs.sd + //$NON-NLS-1$
                    " impossible for budgetFEs.min=" + //$NON-NLS-1$
                    e.budgetFEs.min + " and budgetFEs.max="//$NON-NLS-1$
                    + e.budgetFEs.max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.successes = Integer.parseInt(
              line.substring(lastSemi, nextSemi).trim());
          if ((e.successes < 0) || (e.successes > e.runs)) {
            throw new IllegalArgumentException(
                "There cannot be " + //$NON-NLS-1$
                    e.successes + " successes in " + //$NON-NLS-1$
                    e.runs + " runs."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e.ertTime = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (((!Double.isFinite(e.ertTime))
              && (e.ertTime != Double.POSITIVE_INFINITY))
              || (e.ertTime < 0d)) {
            throw new IllegalArgumentException(
                "ertTime cannot be " + //$NON-NLS-1$
                    e.ertTime);
          }
          if ((e.successes > 0)
              && (!Double.isFinite(e.ertTime))) {
            throw new IllegalArgumentException(
                "ertTime cannot be " + //$NON-NLS-1$
                    e.ertTime + " if there are " //$NON-NLS-1$
                    + e.successes + " successes."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          if (nextSemi >= 0) {
            throw new IllegalStateException("too many columns!");//$NON-NLS-1$
          }
          nextSemi = line.length();
          e.ertFEs = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (((!Double.isFinite(e.ertFEs))
              && (e.ertFEs != Double.POSITIVE_INFINITY))
              || (e.ertFEs <= 0d)) {
            throw new IllegalArgumentException(
                "ertFEs cannot be " + //$NON-NLS-1$
                    e.ertFEs);
          }
          if ((e.successes > 0)
              && (!Double.isFinite(e.ertFEs))) {
            throw new IllegalArgumentException(
                "ertFEs cannot be " + //$NON-NLS-1$
                    e.ertFEs + " if there are " //$NON-NLS-1$
                    + e.successes + " successes."); //$NON-NLS-1$
          }

          consumer.accept(e);
        } catch (final Throwable error) {
          throw new IOException(//
              "Line " + lineIndex //$NON-NLS-1$
                  + " is invalid: '" //$NON-NLS-1$
                  + line2 + "'.", //$NON-NLS-1$
              error);
        }
      }
    } catch (final Throwable error) {
      throw new IOException(
          "Error when parsing end result statistics file '"//$NON-NLS-1$
              + p + "'.", //$NON-NLS-1$
          error);
    }
  }

  /** the internal parser class */
  private static final class __Parser
      implements Consumer<EndResult> {
    /** the holders */
    private HashMap<String, HashMap<String, __Holder>> m_holders;
    /** the success predicate */
    private Predicate<EndResult> m_success;

    /**
     * create
     *
     * @param success
     *          the success predicate
     */
    __Parser(final Predicate<EndResult> success) {
      super();
      this.m_holders = new HashMap<>();
      this.m_success = Objects.requireNonNull(success);
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final EndResult t) {
      HashMap<String, __Holder> ifa =
          this.m_holders.get(t.algorithm);
      if (ifa == null) {
        ifa = new HashMap<>();
        if (this.m_holders.put(t.algorithm, ifa) != null) {
          throw new ConcurrentModificationException();
        }
      }
      __Holder h = ifa.get(t.instance);
      if (h == null) {
        h = new __Holder(t.algorithm, t.instance,
            this.m_success);
        if (ifa.put(t.instance, h) != null) {
          throw new ConcurrentModificationException();
        }
      }

      h.accept(t);
    }

    /**
     * finalize and get the result array
     *
     * @return the holder array
     */
    final __Holder[] _finalize() {
      final __Holder[] holders = this.m_holders.values().stream()
          .flatMap((v) -> v.values().stream()).sorted()
          .toArray((i) -> new __Holder[i]);
      this.m_holders.clear();
      this.m_holders = null;
      this.m_success = null;
      for (final __Holder h : holders) {
        h._finalize();
      }
      return holders;
    }
  }

  /** the data holder */
  private static final class __Holder
      implements Comparable<__Holder>, Consumer<EndResult> {
    /** the algorithm */
    final String algorithm;

    /** the instance */
    final String instance;

    /** the seeds */
    private HashSet<String> m_seeds;
    /** the success predicate */
    private Predicate<EndResult> m_success;

    /** the best-f statistic */
    _Statistic m_bestF;
    /** the total time statistic */
    _Statistic m_totalTime;
    /** the total FEs statistic */
    _Statistic m_totalFEs;
    /** the last improvement time statistic */
    _Statistic m_lastImprovementTime;
    /** the last improvement FEs statistic */
    _Statistic m_lastImprovementFE;
    /** the number of improvements statistic */
    _Statistic m_numberOfImprovements;
    /** the budget time statistic */
    _Statistic m_budgetTime;
    /** the budget FEs statistic */
    _Statistic m_budgetFEs;
    /** the empirical expected running time statistic */
    _Statistic m_ertTime;
    /** the empirical expected running FEs statistic */
    _Statistic m_ertFEs;
    /** the number of successes */
    int m_successes;

    /**
     * create the holder
     *
     * @param _algo
     *          the algorithm
     * @param _inst
     *          the instance
     * @param success
     *          the success predicate
     */
    __Holder(final String _algo, final String _inst,
        final Predicate<EndResult> success) {
      this.algorithm = Objects.requireNonNull(_algo);
      this.instance = Objects.requireNonNull(_inst);
      this.m_seeds = new HashSet<>();

      this.m_bestF = new _Doubles();
      this.m_totalTime = new _Longs();
      this.m_totalFEs = new _Longs();
      this.m_lastImprovementTime = new _Longs();
      this.m_lastImprovementFE = new _Longs();
      this.m_numberOfImprovements = new _Longs();
      this.m_budgetTime = new _Longs();
      this.m_budgetFEs = new _Longs();
      this.m_ertTime = new _Longs();
      this.m_ertFEs = new _Longs();
      this.m_success = Objects.requireNonNull(success);
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __Holder o) {
      if (o == this) {
        return 0;
      }
      int r = this.algorithm.compareTo(o.algorithm);
      if (r != 0) {
        return r;
      }
      r = this.instance.compareTo(o.instance);
      if (r != 0) {
        return r;
      }
      throw new IllegalStateException(
          "Identical setups discovered for algorithm '" //$NON-NLS-1$
              + this.algorithm + "' and instance '" //$NON-NLS-1$
              + this.instance + "'."); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final EndResult t) {
      if (!this.m_seeds.add(t.seed)) {
        throw new IllegalStateException("Seed '" + //$NON-NLS-1$
            t.seed + "' appears twice for algorithm '"//$NON-NLS-1$
            + t.algorithm + "' on instance '"//$NON-NLS-1$
            + t.instance + "'.");//$NON-NLS-1$
      }

      this.m_bestF._add(t.bestF);
      this.m_totalTime._add(t.totalTime);
      this.m_totalFEs._add(t.totalFEs);
      this.m_lastImprovementTime._add(t.lastImprovementTime);
      this.m_lastImprovementFE._add(t.lastImprovementFE);
      this.m_numberOfImprovements._add(t.numberOfImprovements);
      this.m_budgetTime._add(t.budgetTime);
      this.m_budgetFEs._add(t.budgetFEs);

      if (this.m_success.test(t)) {
        ++this.m_successes;
        this.m_ertTime._add(t.lastImprovementTime);
        this.m_ertFEs._add(t.lastImprovementFE);
      } else {
        this.m_ertTime._add(t.totalTime);
        this.m_ertFEs._add(t.totalFEs);
      }
    }

    /** finalize */
    final void _finalize() {
      this.m_seeds.clear();
      this.m_seeds = null;
      this.m_success = null;

      this.m_bestF = this.m_bestF._finalize();
      this.m_totalTime = this.m_totalTime._finalize();
      this.m_totalFEs = this.m_totalFEs._finalize();
      this.m_lastImprovementTime =
          this.m_lastImprovementTime._finalize();
      this.m_lastImprovementFE =
          this.m_lastImprovementFE._finalize();
      this.m_numberOfImprovements =
          this.m_numberOfImprovements._finalize();
      this.m_budgetTime = this.m_budgetTime._finalize();
      this.m_budgetFEs = this.m_budgetFEs._finalize();
      this.m_ertTime = this.m_ertTime._finalize();
      this.m_ertFEs = this.m_ertFEs._finalize();
    }
  }

  /** the goal function value */
  private static final String PARAM_GOAL = "goal"; //$NON-NLS-1$

  /**
   * print the arguments
   *
   * @param s
   *          the print stream
   */
  static final void _printArgs(final PrintStream s) {
    EndResults._printArgs(s);
    s.println(' ' + EndResultStatistics.PARAM_GOAL
        + "=value: is the objective value at which a run is considered as success (if not provided, goalF will be taken).");//$NON-NLS-1$

  }

  /**
   * get the success predicate
   *
   * @return the success predicate
   */
  static final Predicate<EndResult> _argSuccess() {
    final Double goal =
        Configuration.getDouble(EndResultStatistics.PARAM_GOAL);
    if (goal != null) {
      final double threshold = goal.doubleValue();
      return (x) -> (x.bestF <= threshold);
    }
    return null;
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    ConsoleIO.stdout((s) -> {
      s.println(
          "Welcome to the End-Result Statistics CSV Table Generator"); //$NON-NLS-1$
      s.println("The command line arguments are as follows: ");//$NON-NLS-1$
      EndResultStatistics._printArgs(s);
      s.println(
          "If you do not set the arguments, defaults will be used.");//$NON-NLS-1$
    });

    Configuration.putCommandLine(args);

    final Predicate<EndResult> pred =
        EndResultStatistics._argSuccess();

    final Path in = EndResults._argIn();
    final Path out = EndResults._argOut();
    Configuration.print();

    try {
      final Path endResults =
          EndResults.makeEndResultsTable(in, out, true);
      EndResultStatistics.makeEndResultStatisticsTable(
          endResults, out, pred, null, false, true);
    } catch (final Throwable error) {
      ConsoleIO.stderr(
          "An error occured while creating the end result statistics tables.", //$NON-NLS-1$
          error);
    }
  }
}
