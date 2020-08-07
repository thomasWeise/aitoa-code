package aitoa.utils.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import aitoa.structure.LogFormat;
import aitoa.utils.Configuration;
import aitoa.utils.ConsoleIO;
import aitoa.utils.IOUtils;
import aitoa.utils.math.Statistics;

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
  public static final String FILE_NAME = "endResultStatistics"; //$NON-NLS-1$

  /** the setup of the run */
  private static final String COL_SETUP = ".setup";//$NON-NLS-1$
  /** the column with the number of successes */
  public static final String COL_RUNS = "n.runs";//$NON-NLS-1$
  /** the column with the number of successes */
  public static final String COL_SUCCESSES = "n.successes";//$NON-NLS-1$
  /** the column with the ert time */
  public static final String COL_ERT_TIME = "ert.time";//$NON-NLS-1$
  /** the column with the ert fes */
  public static final String COL_ERT_FES = "ert.fes";//$NON-NLS-1$

  /** the statistics about the success FEs */
  public static final String COL_SUCCESS_FES = "success.fes";//$NON-NLS-1$
  /** the statistics about the success time */
  public static final String COL_SUCCESS_TIME = "success.time";//$NON-NLS-1$

  /** the suffix for the minimum column */
  private static final String COL_MIN = ".min"; //$NON-NLS-1$
  /** the suffix for the q050 column */
  private static final String COL_Q050 = ".q050"; //$NON-NLS-1$
  /** the suffix for the q159 column */
  private static final String COL_Q159 = ".q159"; //$NON-NLS-1$
  /** the suffix for the q250 column */
  private static final String COL_Q250 = ".q250"; //$NON-NLS-1$
  /** the suffix for the median column */
  private static final String COL_MEDIAN = ".median"; //$NON-NLS-1$
  /** the suffix for the q750 column */
  private static final String COL_Q750 = ".q750"; //$NON-NLS-1$
  /** the suffix for the q841 column */
  private static final String COL_Q841 = ".q841"; //$NON-NLS-1$
  /** the suffix for the q950 column */
  private static final String COL_Q950 = ".q950"; //$NON-NLS-1$
  /** the suffix for the maximum column */
  private static final String COL_MAX = ".max"; //$NON-NLS-1$
  /** the suffix for the mean column */
  private static final String COL_MEAN = ".mean"; //$NON-NLS-1$
  /** the suffix for the sd column */
  private static final String COL_SD = ".sd";//$NON-NLS-1$

  /** the big quantiles */
  private static final double[] QUANTILES_BIG = { //
      0d, //
      0.05d, //
      Statistics.GAUSSIAN_QUANTILE_159, //
      0.25d, //
      0.5d, //
      0.75d, //
      Statistics.GAUSSIAN_QUANTILE_841, //
      0.95d, //
      1d };//

  /**
   * create the default header for a given key
   *
   * @param key
   *          the key
   * @param addSetupCols
   *          should we add setup columns?
   * @return the header
   */
  private static String makeHeaderBig(final String key,
      final boolean addSetupCols) {
    String[] cols = { key + EndResultStatistics.COL_MIN, //
        key + EndResultStatistics.COL_Q050, //
        key + EndResultStatistics.COL_Q159, //
        key + EndResultStatistics.COL_Q250, //
        key + EndResultStatistics.COL_MEDIAN, //
        key + EndResultStatistics.COL_Q750, //
        key + EndResultStatistics.COL_Q841, //
        key + EndResultStatistics.COL_Q950, //
        key + EndResultStatistics.COL_MAX, //
        key + EndResultStatistics.COL_MEAN, //
        key + EndResultStatistics.COL_SD };
    // expand the columns
    if (addSetupCols) {
      final int len = cols.length - 1;
      final String[] tmp = new String[(len * 2) + 1];

      int i = 0;
      int j = 0;
      for (; i < len; ++i) {
        final String s = cols[i];
        tmp[j++] = s;
        tmp[j++] = s + EndResultStatistics.COL_SETUP;
      }
      tmp[j++] = cols[i];
      cols = tmp;
    }
    return LogFormat.joinLogLine(cols);
  }

  /**
   * find the setup with a result closest to the given value
   *
   * @param value
   *          the value
   * @param unique
   *          the list of unique setups
   * @return the chosen, closest value
   */
  private static final Setup closestSetup(//
      final double value, //
      final Holder.InnerSetup[] unique) {//

    int lower = 0;
    int upper = unique.length - 1;
    int mid = lower;

    // first, do binary search
    while (lower <= upper) {
      mid = (lower + upper) >>> 1;
      final Holder.InnerSetup midVal = unique[mid];
      if (midVal.m_bestF2 < value) {
        lower = mid + 1;
      } else {
        if (midVal.m_bestF2 > value) {
          upper = mid - 1;
        } else {
          return midVal;
        }
      }
    }

    // second: try to re-adjust
    Holder.InnerSetup best = unique[mid];
    double testVal = best.m_bestF2;
    if (testVal == value) {
      return best;
    }
    final double bestDist = Math.abs(testVal - value);
    if (bestDist <= 0d) {
      return best;
    }
    lower = mid;
    upper = mid;

    // test towards lower indices
    int lenience = 2;
    while ((--lower) >= 0) {
      final Holder.InnerSetup test = unique[lower];
      testVal = test.m_bestF2;
      if (testVal == value) {
        return test;
      }
      final double testDist = Math.abs(testVal - value);
      if (testDist <= 0d) {
        return test;
      }
      if (testDist < bestDist) {
        best = test;
      } else {
        if ((--lenience) <= 0) {
          break;
        }
      }
    }

    // test towards higher indices
    lenience = 2;
    while ((++upper) < unique.length) {
      final Holder.InnerSetup test = unique[upper];
      testVal = test.m_bestF2;
      if (testVal == value) {
        return test;
      }
      final double testDist = Math.abs(testVal - value);
      if (testDist <= 0d) {
        return test;
      }
      if (testDist < bestDist) {
        best = test;
      } else {
        if ((--lenience) <= 0) {
          break;
        }
      }
    }

    return best;
  }

  /**
   * print the big statistic
   *
   * @param data
   *          the data
   * @param quantiles
   *          the quantiles to use,
   * @param unique
   *          the unique points
   * @param min
   *          the minimum setup
   * @param max
   *          the maximum setup
   * @param bw
   *          the destination
   * @return the mean
   * @throws IOException
   *           if i/o fails
   */
  private static final Number printStat(//
      final Statistic data, //
      final double[] quantiles, //
      final Holder.InnerSetup[] unique, //
      final Setup min, //
      final Setup max, //
      final BufferedWriter bw) throws IOException {
    for (final double d : quantiles) {
      final Number quantile = data.quantile(d);
      bw.write(LogFormat.numberToStringForLog(quantile));
      bw.write(LogFormat.CSV_SEPARATOR_CHAR);
      if (unique != null) {
        bw.write(EndResultStatistics.closestSetup(//
            quantile.doubleValue(), unique).toString());
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);
      } else {
        if ((d <= 0d) && (min != null)) {
          bw.write(min.toString());
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
        } else {
          if ((d >= 1d) && (max != null)) {
            bw.write(max.toString());
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          }
        }
      }
    }
    final Number[] res = data.meanAndStdDev();
    final Number mean = Objects.requireNonNull(res[0]);
    bw.write(LogFormat.numberToStringForLog(mean));
    bw.write(LogFormat.CSV_SEPARATOR_CHAR);
    if (unique != null) {
      bw.write(EndResultStatistics.closestSetup(//
          mean.doubleValue(), unique).toString());
      bw.write(LogFormat.CSV_SEPARATOR_CHAR);
    }
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
   * @param addSetupCols
   *          do we need the setup columns?
   * @return the header
   */
  private static String makeHeaderSmall(final String key,
      final boolean addSetupCols) {

    if (addSetupCols) {
      final String s, t;
      return LogFormat.joinLogLine(//
          s = key + EndResultStatistics.COL_MIN, //
          s + EndResultStatistics.COL_SETUP, //
          key + EndResultStatistics.COL_MEDIAN, //
          t = key + EndResultStatistics.COL_MAX, //
          t + EndResultStatistics.COL_SETUP, //
          key + EndResultStatistics.COL_MEAN, //
          key + EndResultStatistics.COL_SD);
    }

    return LogFormat.joinLogLine(//
        key + EndResultStatistics.COL_MIN, //
        key + EndResultStatistics.COL_MEDIAN, //
        key + EndResultStatistics.COL_MAX, //
        key + EndResultStatistics.COL_MEAN, //
        key + EndResultStatistics.COL_SD);
  }

  /** the internal header */
  private static final String HEADER = LogFormat.joinLogLine(//
      EndResults.COL_ALGORITHM, //
      EndResults.COL_INSTANCE, //
      EndResultStatistics.COL_RUNS,
      EndResultStatistics.makeHeaderBig(EndResults.COL_BEST_F,
          true), //
      EndResultStatistics
          .makeHeaderBig(EndResults.COL_TOTAL_TIME, false), //
      EndResultStatistics.makeHeaderBig(EndResults.COL_TOTAL_FES,
          false), //
      EndResultStatistics.makeHeaderBig(
          EndResults.COL_LAST_IMPROVEMENT_TIME, false), //
      EndResultStatistics.makeHeaderBig(
          EndResults.COL_LAST_IMPROVEMENT_FES, false), //
      EndResultStatistics.makeHeaderBig(
          EndResults.COL_NUMBER_OF_IMPROVEMENTS, false), //
      EndResultStatistics
          .makeHeaderSmall(EndResults.COL_BUDGET_TIME, false), //
      EndResultStatistics
          .makeHeaderSmall(EndResults.COL_BUDGET_FES, false), //
      EndResultStatistics.COL_SUCCESSES, //
      EndResultStatistics.COL_ERT_TIME, //
      EndResultStatistics.COL_ERT_FES, //
      EndResultStatistics.makeHeaderSmall(
          EndResultStatistics.COL_SUCCESS_TIME, true), //
      EndResultStatistics.makeHeaderSmall(
          EndResultStatistics.COL_SUCCESS_FES, true)//
  );

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
   * @param instanceNameMapper
   *          a function mapping instance names; an instance
   *          mapped to {@code null} will be skipped; instances
   *          mapped to the same name will be treated as the same
   * @param algorithmNameMapper
   *          a function mapping algorithm names; an algorithm
   *          mapped to {@code null} will be skipped; algorithms
   *          mapped to the same name will be treated as the same
   * @param statFileName
   *          the name to be used for this file, can be
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
      final Predicate<EndResult> success,
      final Function<String, String> instanceNameMapper,
      final Function<String, String> algorithmNameMapper,
      final String statFileName, final boolean keepExisting,
      final boolean logProgressToConsole) throws IOException {

    final Path in = IOUtils.requireFile(endResults);
    final Path out =
        IOUtils.requireDirectory(outputFolder, true);

    final String baseName;
    final String sfn =
        ((statFileName == null) ? null : statFileName.trim());
    if ((sfn == null) || (sfn.isEmpty())) {
      baseName =
          EndResultStatistics.FILE_NAME + LogFormat.FILE_SUFFIX;
    } else {
      baseName = sfn + LogFormat.FILE_SUFFIX;
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
    Parser p = new Parser((success == null)
        ? x -> Double.compare(x.bestF, x.goalF) <= 0 : success,
        (instanceNameMapper != null) ? instanceNameMapper
            : Function.identity(),
        (algorithmNameMapper != null) ? algorithmNameMapper
            : Function.identity());
    EndResults.parseEndResultsTable(in, p, logProgressToConsole);
    final Holder[] results = p.doFinalize();
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
        Holder h = results[i];
        results[i] = null;

        if (!h.m_finalized) {
          throw new IllegalStateException();
        }

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

        EndResultStatistics.printStat(//
            h.m_bestF, //
            EndResultStatistics.QUANTILES_BIG, //
            h.m_uniqueBestF, null, null, //
            bw);
        h.m_bestF = null;
        h.m_uniqueBestF = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_totalTime.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.printStat(//
            h.m_totalTime, //
            EndResultStatistics.QUANTILES_BIG, //
            null, null, null, //
            bw);
        h.m_totalTime = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_totalFEs.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.printStat(//
            h.m_totalFEs, //
            EndResultStatistics.QUANTILES_BIG, //
            null, null, null, //
            bw);
        h.m_totalFEs = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_lastImprovementTime.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        final Number lastImprovementTimeMean = //
            EndResultStatistics.printStat(//
                h.m_lastImprovementTime, //
                EndResultStatistics.QUANTILES_BIG, //
                null, null, null, //
                bw);
        h.m_lastImprovementTime = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_lastImprovementFE.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        final Number lastImprovementFEMean =
            EndResultStatistics.printStat(//
                h.m_lastImprovementFE, //
                EndResultStatistics.QUANTILES_BIG, //
                null, null, null, //
                bw);
        h.m_lastImprovementFE = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_numberOfImprovements.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.printStat(//
            h.m_numberOfImprovements, //
            EndResultStatistics.QUANTILES_BIG, //
            null, null, null, //
            bw);
        h.m_numberOfImprovements = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_budgetTime.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.printStat(//
            h.m_budgetTime, //
            EndResultStatistics.QUANTILES_SMALL, //
            null, null, null, //
            bw);
        h.m_budgetTime = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_budgetFEs.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        EndResultStatistics.printStat(//
            h.m_budgetFEs, //
            EndResultStatistics.QUANTILES_SMALL, //
            null, null, null, //
            bw);
        h.m_budgetFEs = null;
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        bw.write(Integer.toString(h.m_successes));
        bw.write(LogFormat.CSV_SEPARATOR_CHAR);

        if (h.m_successes > 0) {
          if (allSuccess) {
            bw.write(LogFormat.numberToStringForLog(//
                lastImprovementTimeMean));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(LogFormat.numberToStringForLog(//
                lastImprovementFEMean));
          } else {
            bw.write(LogFormat.numberToStringForLog(
                h.m_ertTime.divideSumBy(h.m_successes)));
            bw.write(LogFormat.CSV_SEPARATOR_CHAR);
            bw.write(LogFormat.numberToStringForLog(
                h.m_ertFEs.divideSumBy(h.m_successes)));
          }

          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          EndResultStatistics.printStat(//
              h.m_successTime, //
              EndResultStatistics.QUANTILES_SMALL, //
              null, //
              h.m_fastestSuccessTimeSetup, //
              h.m_slowestSuccessTimeSetup, //
              bw);
          h.m_successTime = null;
          h.m_fastestSuccessTimeSetup = null;
          h.m_slowestSuccessTimeSetup = null;

          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          EndResultStatistics.printStat(//
              h.m_successFEs, //
              EndResultStatistics.QUANTILES_SMALL, //
              null, //
              h.m_fastestSuccessFEsSetup, //
              h.m_slowestSuccessFEsSetup, //
              bw);
          h.m_successFEs = null;
          h.m_fastestSuccessFEsSetup = null;
          h.m_slowestSuccessFEsSetup = null;

        } else {
          final String s =
              Double.toString(Double.POSITIVE_INFINITY);
          bw.write(s);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(s);

          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
          bw.write(LogFormat.CSV_SEPARATOR_CHAR);
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
   * Read (and, while doing so, also verify) the end result
   * statistics table.
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
      final Cache cache = new Cache();
      String line2;
      int lineIndex = 0;
      String algorithm = null;
      String instance = null;
      int runs = -1;
      double bestF_min = Double.NaN;
      Setup bestF_min_setup = null;
      double bestF_q050 = Double.NaN;
      Setup bestF_q050_setup = null;
      double bestF_q159 = Double.NaN;
      Setup bestF_q159_setup = null;
      double bestF_q250 = Double.NaN;
      Setup bestF_q250_setup = null;
      double bestF_median = Double.NaN;
      Setup bestF_median_setup = null;
      double bestF_q750 = Double.NaN;
      Setup bestF_q750_setup = null;
      double bestF_q841 = Double.NaN;
      Setup bestF_q841_setup = null;
      double bestF_q950 = Double.NaN;
      Setup bestF_q950_setup = null;
      double bestF_max = Double.NaN;
      Setup bestF_max_setup = null;
      double bestF_mean = Double.NaN;
      Setup bestF_mean_setup = null;
      double bestF_sd = Double.NaN;

      long totalTime_min = -1L;
      double totalTime_q050 = Double.NaN;
      double totalTime_q159 = Double.NaN;
      double totalTime_q250 = Double.NaN;
      double totalTime_median = Double.NaN;
      double totalTime_q750 = Double.NaN;
      double totalTime_q841 = Double.NaN;
      double totalTime_q950 = Double.NaN;
      long totalTime_max = -1L;
      double totalTime_mean = Double.NaN;
      double totalTime_sd = Double.NaN;
      long totalFEs_min = -1L;
      double totalFEs_q050 = Double.NaN;
      double totalFEs_q159 = Double.NaN;
      double totalFEs_q250 = Double.NaN;
      double totalFEs_median = Double.NaN;
      double totalFEs_q750 = Double.NaN;
      double totalFEs_q841 = Double.NaN;
      double totalFEs_q950 = Double.NaN;
      long totalFEs_max = -1L;
      double totalFEs_mean = Double.NaN;
      double totalFEs_sd = Double.NaN;
      long lastImprovementTime_min = -1L;
      double lastImprovementTime_q050 = Double.NaN;
      double lastImprovementTime_q159 = Double.NaN;
      double lastImprovementTime_q250 = Double.NaN;
      double lastImprovementTime_median = Double.NaN;
      double lastImprovementTime_q750 = Double.NaN;
      double lastImprovementTime_q841 = Double.NaN;
      double lastImprovementTime_q950 = Double.NaN;
      long lastImprovementTime_max = -1L;
      double lastImprovementTime_mean = Double.NaN;
      double lastImprovementTime_sd = Double.NaN;
      long lastImprovementFE_min = -1L;
      double lastImprovementFE_q050 = Double.NaN;
      double lastImprovementFE_q159 = Double.NaN;
      double lastImprovementFE_q250 = Double.NaN;
      double lastImprovementFE_median = Double.NaN;
      double lastImprovementFE_q750 = Double.NaN;
      double lastImprovementFE_q841 = Double.NaN;
      double lastImprovementFE_q950 = Double.NaN;
      long lastImprovementFE_max = -1L;
      double lastImprovementFE_mean = Double.NaN;
      double lastImprovementFE_sd = Double.NaN;
      long numberOfImprovements_min = -1L;
      double numberOfImprovements_q050 = Double.NaN;
      double numberOfImprovements_q159 = Double.NaN;
      double numberOfImprovements_q250 = Double.NaN;
      double numberOfImprovements_median = Double.NaN;
      double numberOfImprovements_q750 = Double.NaN;
      double numberOfImprovements_q841 = Double.NaN;
      double numberOfImprovements_q950 = Double.NaN;
      long numberOfImprovements_max = -1L;
      double numberOfImprovements_mean = Double.NaN;
      double numberOfImprovements_sd = Double.NaN;
      long budgetTime_min = -1L;
      double budgetTime_median = Double.NaN;
      long budgetTime_max = -1L;
      double budgetTime_mean = Double.NaN;
      double budgetTime_sd = Double.NaN;
      long budgetFEs_min = -1L;
      double budgetFEs_median = Double.NaN;
      long budgetFEs_max = -1L;
      double budgetFEs_mean = Double.NaN;
      double budgetFEs_sd = Double.NaN;
      int successes = -1;
      double ertTime = Double.NaN;
      double ertFEs = Double.NaN;

      long successTime_min = -1L;
      Setup successTime_min_setup = null;
      double successTime_median = Double.NaN;
      long successTime_max = -1L;
      Setup successTime_max_setup = null;
      double successTime_mean = Double.NaN;
      double successTime_sd = Double.NaN;

      long successFEs_min = -1L;
      Setup successFEs_min_setup = null;
      double successFEs_median = Double.NaN;
      long successFEs_max = -1L;
      Setup successFEs_max_setup = null;
      double successFEs_mean = Double.NaN;
      double successFEs_sd = Double.NaN;

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
        if (EndResultStatistics.HEADER.equals(line)) {
          if (algorithm == null) {
            continue;
          }
          throw new IllegalArgumentException(
              "Header occurs twice?"); //$NON-NLS-1$
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
          algorithm = cache.string(algorithm);
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          instance = line.substring(lastSemi, nextSemi).trim();
          if (instance.isEmpty()) {
            throw new IllegalArgumentException(
                "Instance ID must be specified."); //$NON-NLS-1$
          }
          instance = cache.string(instance);
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          runs = Integer.parseInt(
              line.substring(lastSemi, nextSemi).trim());
          if (runs <= 0) {
            throw new IllegalArgumentException(
                "There cannot be " + runs //$NON-NLS-1$
                    + " runs."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_min = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_min)) {
            throw new IllegalArgumentException(
                "bestF.min must be finite, but is " //$NON-NLS-1$
                    + bestF_min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_min_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_q050)) {
            throw new IllegalArgumentException(
                "bestF.q050 must be finite, but is " //$NON-NLS-1$
                    + bestF_q050);
          }
          if (bestF_q050 < bestF_min) {
            throw new IllegalArgumentException(
                "bestF.q050 (" + bestF_q050 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.min ("//$NON-NLS-1$
                    + bestF_min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q050_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_q159)) {
            throw new IllegalArgumentException(
                "bestF.q159 must be finite, but is " //$NON-NLS-1$
                    + bestF_q159);
          }
          if (bestF_q159 < bestF_min) {
            throw new IllegalArgumentException(
                "bestF.q159 (" + bestF_q159 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q050 ("//$NON-NLS-1$
                    + bestF_q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q159_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_q250)) {
            throw new IllegalArgumentException(
                "bestF.q250 must be finite, but is " //$NON-NLS-1$
                    + bestF_q250);
          }
          if (bestF_q250 < bestF_q050) {
            throw new IllegalArgumentException(
                "bestF.q250 (" + bestF_q250 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q159 ("//$NON-NLS-1$
                    + bestF_q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q250_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_median)) {
            throw new IllegalArgumentException(
                "bestF.median must be finite, but is " //$NON-NLS-1$
                    + bestF_median);
          }
          if (bestF_median < bestF_q250) {
            throw new IllegalArgumentException(
                "bestF.median (" + bestF_median + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q250 ("//$NON-NLS-1$
                    + bestF_q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_median_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_q750)) {
            throw new IllegalArgumentException(
                "bestF.q750 must be finite, but is " //$NON-NLS-1$
                    + bestF_q750);
          }
          if (bestF_q750 < bestF_median) {
            throw new IllegalArgumentException(
                "bestF.q750 (" + bestF_q750 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.median ("//$NON-NLS-1$
                    + bestF_median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q750_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_q841)) {
            throw new IllegalArgumentException(
                "bestF.q841 must be finite, but is " //$NON-NLS-1$
                    + bestF_q841);
          }
          if (bestF_q841 < bestF_q750) {
            throw new IllegalArgumentException(
                "bestF.q841 (" + bestF_q841 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q750 ("//$NON-NLS-1$
                    + bestF_q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q841_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_q950)) {
            throw new IllegalArgumentException(
                "bestF.q950 must be finite, but is " //$NON-NLS-1$
                    + bestF_q950);
          }
          if (bestF_q950 < bestF_q841) {
            throw new IllegalArgumentException(
                "bestF.q950 (" + bestF_q950 + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q841 ("//$NON-NLS-1$
                    + bestF_q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_q950_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_max = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_max)) {
            throw new IllegalArgumentException(
                "bestF.max must be finite, but is " //$NON-NLS-1$
                    + bestF_max);
          }
          if (bestF_max < bestF_q950) {
            throw new IllegalArgumentException(
                "bestF.max (" + bestF_max + //$NON-NLS-1$
                    ") must be greater or equal to bestF.q950 ("//$NON-NLS-1$
                    + bestF_q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_max_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_mean)) {
            throw new IllegalArgumentException(
                "bestF.mean must be finite, but is " //$NON-NLS-1$
                    + bestF_mean);
          }
          if ((bestF_max < bestF_mean)
              || (bestF_min > bestF_mean)) {
            throw new IllegalArgumentException(
                (("bestF.mean (" + bestF_mean + //$NON-NLS-1$
                    ") must be inside [bestF.min, bestF.max], i.e., ("//$NON-NLS-1$
                    + bestF_min) + ',') + bestF_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_mean_setup = new Setup(
              line.substring(lastSemi, nextSemi).trim());
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          bestF_sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(bestF_sd) || (bestF_sd < 0d)) {
            throw new IllegalArgumentException(
                "bestF.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + bestF_sd);
          }
          if ((bestF_max > bestF_min) == (bestF_sd <= 0d)) {
            throw new IllegalArgumentException(
                "bestF.sd=" + bestF_sd + //$NON-NLS-1$
                    " impossible for bestF.min=" + //$NON-NLS-1$
                    bestF_min + " and bestF.max="//$NON-NLS-1$
                    + bestF_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (totalTime_min < 0L) {
            throw new IllegalArgumentException(
                "totalTime.min must be >=0, but is " //$NON-NLS-1$
                    + totalTime_min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalTime_q050)) {
            throw new IllegalArgumentException(
                "totalTime.q050 must be finite, but is " //$NON-NLS-1$
                    + totalTime_q050);
          }
          if (totalTime_q050 < totalTime_min) {
            throw new IllegalArgumentException(
                "totalTime.q050 (" + totalTime_q050 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.min ("//$NON-NLS-1$
                    + totalTime_min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalTime_q159)) {
            throw new IllegalArgumentException(
                "totalTime.q159 must be finite, but is " //$NON-NLS-1$
                    + totalTime_q159);
          }
          if (totalTime_q159 < totalTime_q050) {
            throw new IllegalArgumentException(
                "totalTime.q159 (" + totalTime_q159 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q050 ("//$NON-NLS-1$
                    + totalTime_q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalTime_q250)) {
            throw new IllegalArgumentException(
                "totalTime.q250 must be finite, but is " //$NON-NLS-1$
                    + totalTime_q250);
          }
          if (totalTime_q250 < totalTime_q159) {
            throw new IllegalArgumentException(
                "totalTime.q250 (" + totalTime_q250 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q159 ("//$NON-NLS-1$
                    + totalTime_q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalTime_median)) {
            throw new IllegalArgumentException(
                "totalTime.median must be finite, but is " //$NON-NLS-1$
                    + totalTime_median);
          }
          if (totalTime_median < totalTime_q250) {
            throw new IllegalArgumentException(
                "totalTime.median (" + totalTime_median + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q250 ("//$NON-NLS-1$
                    + totalTime_q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalTime_q750)) {
            throw new IllegalArgumentException(
                "totalTime.q750 must be finite, but is " //$NON-NLS-1$
                    + totalTime_q750);
          }
          if (totalTime_q750 < totalTime_median) {
            throw new IllegalArgumentException(
                "totalTime.q750 (" + totalTime_q750 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.median ("//$NON-NLS-1$
                    + totalTime_median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalTime_q841)) {
            throw new IllegalArgumentException(
                "totalTime.q841 must be finite, but is " //$NON-NLS-1$
                    + totalTime_q841);
          }
          if (totalTime_q841 < totalTime_q750) {
            throw new IllegalArgumentException(
                "totalTime.q841 (" + totalTime_q841 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q750 ("//$NON-NLS-1$
                    + totalTime_q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalTime_q950)) {
            throw new IllegalArgumentException(
                "totalTime.q950 must be finite, but is " //$NON-NLS-1$
                    + totalTime_q950);
          }
          if (totalTime_q950 < totalTime_q841) {
            throw new IllegalArgumentException(
                "totalTime.q950 (" + totalTime_q950 + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q841 ("//$NON-NLS-1$
                    + totalTime_q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (totalTime_max < 0L) {
            throw new IllegalArgumentException(
                "totalTime.max must be >=0, but is " //$NON-NLS-1$
                    + totalTime_max);
          }
          if (totalTime_max < totalTime_q950) {
            throw new IllegalArgumentException(
                "totalTime.max (" + totalTime_max + //$NON-NLS-1$
                    ") must be greater or equal to totalTime.q950 ("//$NON-NLS-1$
                    + totalTime_q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalTime_mean)) {
            throw new IllegalArgumentException(
                "totalTime.mean must be finite, but is " //$NON-NLS-1$
                    + totalTime_mean);
          }
          if ((totalTime_max < totalTime_mean)
              || (totalTime_min > totalTime_mean)) {
            throw new IllegalArgumentException(
                (("totalTime.mean (" + totalTime_mean + //$NON-NLS-1$
                    ") must be inside [totalTime.min, totalTime.max], i.e., ("//$NON-NLS-1$
                    + totalTime_min) + ',') + totalTime_max
                    + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalTime_sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalTime_sd)
              || (totalTime_sd < 0d)) {
            throw new IllegalArgumentException(
                "totalTime.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + totalTime_sd);
          }
          if ((totalTime_max > totalTime_min) == (totalTime_sd <= 0d)) {
            throw new IllegalArgumentException(
                "totalTime.sd=" + totalTime_sd + //$NON-NLS-1$
                    " impossible for totalTime.min=" + //$NON-NLS-1$
                    totalTime_min + " and totalTime.max="//$NON-NLS-1$
                    + totalTime_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (totalFEs_min <= 0L) {
            throw new IllegalArgumentException(
                "totalFEs.min must be >0, but is " //$NON-NLS-1$
                    + totalFEs_min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalFEs_q050)) {
            throw new IllegalArgumentException(
                "totalFEs.q050 must be finite, but is " //$NON-NLS-1$
                    + totalFEs_q050);
          }
          if (totalFEs_q050 < totalFEs_min) {
            throw new IllegalArgumentException(
                "totalFEs.q050 (" + totalFEs_q050 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.min ("//$NON-NLS-1$
                    + totalFEs_min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalFEs_q159)) {
            throw new IllegalArgumentException(
                "totalFEs.q159 must be finite, but is " //$NON-NLS-1$
                    + totalFEs_q159);
          }
          if (totalFEs_q159 < totalFEs_q050) {
            throw new IllegalArgumentException(
                "totalFEs.q159 (" + totalFEs_q159 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q050 ("//$NON-NLS-1$
                    + totalFEs_q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalFEs_q250)) {
            throw new IllegalArgumentException(
                "totalFEs.q250 must be finite, but is " //$NON-NLS-1$
                    + totalFEs_q250);
          }
          if (totalFEs_q250 < totalFEs_q159) {
            throw new IllegalArgumentException(
                "totalFEs.q250 (" + totalFEs_q250 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q159 ("//$NON-NLS-1$
                    + totalFEs_q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalFEs_median)) {
            throw new IllegalArgumentException(
                "totalFEs.median must be finite, but is " //$NON-NLS-1$
                    + totalFEs_median);
          }
          if (totalFEs_median < totalFEs_q250) {
            throw new IllegalArgumentException(
                "totalFEs.median (" + totalFEs_median + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q250 ("//$NON-NLS-1$
                    + totalFEs_q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalFEs_q750)) {
            throw new IllegalArgumentException(
                "totalFEs.q750 must be finite, but is " //$NON-NLS-1$
                    + totalFEs_q750);
          }
          if (totalFEs_q750 < totalFEs_median) {
            throw new IllegalArgumentException(
                "totalFEs.q750 (" + totalFEs_q750 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.median ("//$NON-NLS-1$
                    + totalFEs_median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalFEs_q841)) {
            throw new IllegalArgumentException(
                "totalFEs.q841 must be finite, but is " //$NON-NLS-1$
                    + totalFEs_q841);
          }
          if (totalFEs_q841 < totalFEs_q750) {
            throw new IllegalArgumentException(
                "totalFEs.q841 (" + totalFEs_q841 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q750 ("//$NON-NLS-1$
                    + totalFEs_q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalFEs_q950)) {
            throw new IllegalArgumentException(
                "totalFEs.q950 must be finite, but is " //$NON-NLS-1$
                    + totalFEs_q950);
          }
          if (totalFEs_q950 < totalFEs_q841) {
            throw new IllegalArgumentException(
                "totalFEs.q950 (" + totalFEs_q950 + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q841 ("//$NON-NLS-1$
                    + totalFEs_q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (totalFEs_max <= 0L) {
            throw new IllegalArgumentException(
                "totalFEs.max must be >0, but is " //$NON-NLS-1$
                    + totalFEs_max);
          }
          if (totalFEs_max < totalFEs_q950) {
            throw new IllegalArgumentException(
                "totalFEs.max (" + totalFEs_max + //$NON-NLS-1$
                    ") must be greater or equal to totalFEs.q950 ("//$NON-NLS-1$
                    + totalFEs_q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalFEs_mean)) {
            throw new IllegalArgumentException(
                "totalFEs.mean must be finite, but is " //$NON-NLS-1$
                    + totalFEs_mean);
          }
          if ((totalFEs_max < totalFEs_mean)
              || (totalFEs_min > totalFEs_mean)) {
            throw new IllegalArgumentException(
                (("totalFEs.mean (" + totalFEs_mean + //$NON-NLS-1$
                    ") must be inside [totalFEs.min, totalFEs.max], i.e., ("//$NON-NLS-1$
                    + totalFEs_min) + ',') + totalFEs_max
                    + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          totalFEs_sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(totalFEs_sd)
              || (totalFEs_sd < 0d)) {
            throw new IllegalArgumentException(
                "totalFEs.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + totalFEs_sd);
          }
          if ((totalFEs_max > totalFEs_min) == (totalFEs_sd <= 0d)) {
            throw new IllegalArgumentException(
                "totalFEs.sd=" + totalFEs_sd + //$NON-NLS-1$
                    " impossible for totalFEs.min=" + //$NON-NLS-1$
                    totalFEs_min + " and totalFEs.max="//$NON-NLS-1$
                    + totalFEs_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (lastImprovementTime_min < 0L) {
            throw new IllegalArgumentException(
                "lastImprovementTime.min must be >=0, but is " //$NON-NLS-1$
                    + lastImprovementTime_min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementTime_q050)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q050 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementTime_q050);
          }
          if (lastImprovementTime_q050 < lastImprovementTime_min) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q050 (" //$NON-NLS-1$
                    + lastImprovementTime_q050
                    + ") must be greater or equal to lastImprovementTime.min ("//$NON-NLS-1$
                    + lastImprovementTime_min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementTime_q159)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q159 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementTime_q159);
          }
          if (lastImprovementTime_q159 < lastImprovementTime_q050) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q159 (" //$NON-NLS-1$
                    + lastImprovementTime_q159
                    + ") must be greater or equal to lastImprovementTime.q050 (" //$NON-NLS-1$
                    + lastImprovementTime_q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementTime_q250)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q250 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementTime_q250);
          }
          if (lastImprovementTime_q250 < lastImprovementTime_q159) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q250 (" //$NON-NLS-1$
                    + lastImprovementTime_q250
                    + ") must be greater or equal to lastImprovementTime.q159 ("//$NON-NLS-1$
                    + lastImprovementTime_q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementTime_median)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.median must be finite, but is " //$NON-NLS-1$
                    + lastImprovementTime_median);
          }
          if (lastImprovementTime_median < lastImprovementTime_q250) {
            throw new IllegalArgumentException(
                "lastImprovementTime.median (" //$NON-NLS-1$
                    + lastImprovementTime_median
                    + ") must be greater or equal to lastImprovementTime.q250 ("//$NON-NLS-1$
                    + lastImprovementTime_q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementTime_q750)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q750 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementTime_q750);
          }
          if (lastImprovementTime_q750 < lastImprovementTime_median) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q750 (" //$NON-NLS-1$
                    + lastImprovementTime_q750
                    + ") must be greater or equal to lastImprovementTime.median ("//$NON-NLS-1$
                    + lastImprovementTime_median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementTime_q841)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q841 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementTime_q841);
          }
          if (lastImprovementTime_q841 < lastImprovementTime_q750) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q841 (" //$NON-NLS-1$
                    + lastImprovementTime_q841
                    + ") must be greater or equal to lastImprovementTime.q750 ("//$NON-NLS-1$
                    + lastImprovementTime_q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementTime_q950)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q950 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementTime_q950);
          }
          if (lastImprovementTime_q950 < lastImprovementTime_q841) {
            throw new IllegalArgumentException(
                "lastImprovementTime.q950 (" //$NON-NLS-1$
                    + lastImprovementTime_q950
                    + ") must be greater or equal to lastImprovementTime.q841 ("//$NON-NLS-1$
                    + lastImprovementTime_q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (lastImprovementTime_max < 0L) {
            throw new IllegalArgumentException(
                "lastImprovementTime.max must be >=0, but is " //$NON-NLS-1$
                    + lastImprovementTime_max);
          }
          if (lastImprovementTime_max < lastImprovementTime_q950) {
            throw new IllegalArgumentException(
                "lastImprovementTime.max (" //$NON-NLS-1$
                    + lastImprovementTime_max
                    + ") must be greater or equal to lastImprovementTime.q950 ("//$NON-NLS-1$
                    + lastImprovementTime_q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementTime_mean)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.mean must be finite, but is " //$NON-NLS-1$
                    + lastImprovementTime_mean);
          }
          if ((lastImprovementTime_max < lastImprovementTime_mean)
              || (lastImprovementTime_min > lastImprovementTime_mean)) {
            throw new IllegalArgumentException(
                (("lastImprovementTime.mean (" //$NON-NLS-1$
                    + lastImprovementTime_mean
                    + ") must be inside [lastImprovementTime.min, lastImprovementTime.max], i.e., ("//$NON-NLS-1$
                    + lastImprovementTime_min) + ',')
                    + lastImprovementTime_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementTime_sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementTime_sd)
              || (lastImprovementTime_sd < 0d)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + lastImprovementTime_sd);
          }
          if ((lastImprovementTime_max > lastImprovementTime_min) == (lastImprovementTime_sd <= 0d)) {
            throw new IllegalArgumentException(
                "lastImprovementTime.sd=" //$NON-NLS-1$
                    + lastImprovementTime_sd
                    + " impossible for lastImprovementTime.min="//$NON-NLS-1$
                    + lastImprovementTime_min
                    + " and lastImprovementTime.max="//$NON-NLS-1$
                    + lastImprovementTime_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (lastImprovementFE_min <= 0L) {
            throw new IllegalArgumentException(
                "lastImprovementFE.min must be >0, but is " //$NON-NLS-1$
                    + lastImprovementFE_min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementFE_q050)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q050 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementFE_q050);
          }
          if (lastImprovementFE_q050 < lastImprovementFE_min) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q050 (" //$NON-NLS-1$
                    + lastImprovementFE_q050
                    + ") must be greater or equal to lastImprovementFE.min ("//$NON-NLS-1$
                    + lastImprovementFE_min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementFE_q159)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q159 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementFE_q159);
          }
          if (lastImprovementFE_q159 < lastImprovementFE_q050) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q159 (" //$NON-NLS-1$
                    + lastImprovementFE_q159
                    + ") must be greater or equal to lastImprovementFE.q050 ("//$NON-NLS-1$
                    + lastImprovementFE_q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementFE_q250)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q250 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementFE_q250);
          }
          if (lastImprovementFE_q250 < lastImprovementFE_q159) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q250 (" //$NON-NLS-1$
                    + lastImprovementFE_q250
                    + ") must be greater or equal to lastImprovementFE.q159 ("//$NON-NLS-1$
                    + lastImprovementFE_q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementFE_median)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.median must be finite, but is " //$NON-NLS-1$
                    + lastImprovementFE_median);
          }
          if (lastImprovementFE_median < lastImprovementFE_q250) {
            throw new IllegalArgumentException(
                "lastImprovementFE.median (" //$NON-NLS-1$
                    + lastImprovementFE_median
                    + ") must be greater or equal to lastImprovementFE.q250 ("//$NON-NLS-1$
                    + lastImprovementFE_q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementFE_q750)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q750 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementFE_q750);
          }
          if (lastImprovementFE_q750 < lastImprovementFE_median) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q750 (" //$NON-NLS-1$
                    + lastImprovementFE_q750
                    + ") must be greater or equal to lastImprovementFE.median ("//$NON-NLS-1$
                    + lastImprovementFE_median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementFE_q841)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q841 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementFE_q841);
          }
          if (lastImprovementFE_q841 < lastImprovementFE_q750) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q841 (" //$NON-NLS-1$
                    + lastImprovementFE_q841
                    + ") must be greater or equal to lastImprovementFE.q750 ("//$NON-NLS-1$
                    + lastImprovementFE_q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementFE_q950)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q950 must be finite, but is " //$NON-NLS-1$
                    + lastImprovementFE_q950);
          }
          if (lastImprovementFE_q950 < lastImprovementFE_q841) {
            throw new IllegalArgumentException(
                "lastImprovementFE.q950 (" //$NON-NLS-1$
                    + lastImprovementFE_q950
                    + ") must be greater or equal to lastImprovementFE.q841 ("//$NON-NLS-1$
                    + lastImprovementFE_q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (lastImprovementFE_max <= 0L) {
            throw new IllegalArgumentException(
                "lastImprovementFE.max must be >0, but is " //$NON-NLS-1$
                    + lastImprovementFE_max);
          }
          if (lastImprovementFE_max < lastImprovementFE_q950) {
            throw new IllegalArgumentException(
                "lastImprovementFE.max (" //$NON-NLS-1$
                    + lastImprovementFE_max
                    + ") must be greater or equal to lastImprovementFE.q950 ("//$NON-NLS-1$
                    + lastImprovementFE_q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementFE_mean)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.mean must be finite, but is " //$NON-NLS-1$
                    + lastImprovementFE_mean);
          }
          if ((lastImprovementFE_max < lastImprovementFE_mean)
              || (lastImprovementFE_min > lastImprovementFE_mean)) {
            throw new IllegalArgumentException(
                (("lastImprovementFE.mean (" //$NON-NLS-1$
                    + lastImprovementFE_mean
                    + ") must be inside [lastImprovementFE.min, lastImprovementFE.max], i.e., ("//$NON-NLS-1$
                    + lastImprovementFE_min) + ',')
                    + lastImprovementFE_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          lastImprovementFE_sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(lastImprovementFE_sd)
              || (lastImprovementFE_sd < 0d)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + lastImprovementFE_sd);
          }
          if ((lastImprovementFE_max > lastImprovementFE_min) == (lastImprovementFE_sd <= 0d)) {
            throw new IllegalArgumentException(
                "lastImprovementFE.sd=" + lastImprovementFE_sd //$NON-NLS-1$
                    + " impossible for lastImprovementFE.min="//$NON-NLS-1$
                    + lastImprovementFE_min
                    + " and lastImprovementFE.max="//$NON-NLS-1$
                    + lastImprovementFE_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (numberOfImprovements_min <= 0L) {
            throw new IllegalArgumentException(
                "numberOfImprovements.min must be >0, but is " //$NON-NLS-1$
                    + numberOfImprovements_min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_q050 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(numberOfImprovements_q050)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q050 must be finite, but is " //$NON-NLS-1$
                    + numberOfImprovements_q050);
          }
          if (numberOfImprovements_q050 < numberOfImprovements_min) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q050 (" //$NON-NLS-1$
                    + numberOfImprovements_q050
                    + ") must be greater or equal to numberOfImprovements.min ("//$NON-NLS-1$
                    + numberOfImprovements_min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_q159 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(numberOfImprovements_q159)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q159 must be finite, but is " //$NON-NLS-1$
                    + numberOfImprovements_q159);
          }
          if (numberOfImprovements_q159 < numberOfImprovements_q050) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q159 (" //$NON-NLS-1$
                    + numberOfImprovements_q159
                    + ") must be greater or equal to numberOfImprovements.q050 ("//$NON-NLS-1$
                    + numberOfImprovements_q050 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_q250 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(numberOfImprovements_q250)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q250 must be finite, but is " //$NON-NLS-1$
                    + numberOfImprovements_q250);
          }
          if (numberOfImprovements_q250 < numberOfImprovements_q159) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q250 (" //$NON-NLS-1$
                    + numberOfImprovements_q250
                    + ") must be greater or equal to numberOfImprovements.q159 ("//$NON-NLS-1$
                    + numberOfImprovements_q159 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(numberOfImprovements_median)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.median must be finite, but is " //$NON-NLS-1$
                    + numberOfImprovements_median);
          }
          if (numberOfImprovements_median < numberOfImprovements_q250) {
            throw new IllegalArgumentException(
                "numberOfImprovements.median (" //$NON-NLS-1$
                    + numberOfImprovements_median
                    + ") must be greater or equal to numberOfImprovements.q250 ("//$NON-NLS-1$
                    + numberOfImprovements_q250 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_q750 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(numberOfImprovements_q750)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q750 must be finite, but is " //$NON-NLS-1$
                    + numberOfImprovements_q750);
          }
          if (numberOfImprovements_q750 < numberOfImprovements_median) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q750 (" //$NON-NLS-1$
                    + numberOfImprovements_q750
                    + ") must be greater or equal to numberOfImprovements.median ("//$NON-NLS-1$
                    + numberOfImprovements_median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_q841 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(numberOfImprovements_q841)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q841 must be finite, but is " //$NON-NLS-1$
                    + numberOfImprovements_q841);
          }
          if (numberOfImprovements_q841 < numberOfImprovements_q750) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q841 (" //$NON-NLS-1$
                    + numberOfImprovements_q841
                    + ") must be greater or equal to numberOfImprovements.q750 ("//$NON-NLS-1$
                    + numberOfImprovements_q750 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_q950 = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(numberOfImprovements_q950)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q950 must be finite, but is " //$NON-NLS-1$
                    + numberOfImprovements_q950);
          }
          if (numberOfImprovements_q950 < numberOfImprovements_q841) {
            throw new IllegalArgumentException(
                "numberOfImprovements.q950 (" //$NON-NLS-1$
                    + numberOfImprovements_q950
                    + ") must be greater or equal to numberOfImprovements.q841 ("//$NON-NLS-1$
                    + numberOfImprovements_q841 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (numberOfImprovements_max <= 0L) {
            throw new IllegalArgumentException(
                "numberOfImprovements.max must be >0, but is " //$NON-NLS-1$
                    + numberOfImprovements_max);
          }
          if (numberOfImprovements_max < numberOfImprovements_q950) {
            throw new IllegalArgumentException(
                "numberOfImprovements.max (" //$NON-NLS-1$
                    + numberOfImprovements_max
                    + ") must be greater or equal to numberOfImprovements.q950 ("//$NON-NLS-1$
                    + numberOfImprovements_q950 + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(numberOfImprovements_mean)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.mean must be finite, but is " //$NON-NLS-1$
                    + numberOfImprovements_mean);
          }
          if ((numberOfImprovements_max < numberOfImprovements_mean)
              || (numberOfImprovements_min > numberOfImprovements_mean)) {
            throw new IllegalArgumentException(
                (("numberOfImprovements.mean (" //$NON-NLS-1$
                    + numberOfImprovements_mean
                    + ") must be inside [numberOfImprovements.min, numberOfImprovements.max], i.e., ("//$NON-NLS-1$
                    + numberOfImprovements_min) + ',')
                    + numberOfImprovements_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          numberOfImprovements_sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(numberOfImprovements_sd)
              || (numberOfImprovements_sd < 0d)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + numberOfImprovements_sd);
          }
          if ((numberOfImprovements_max > numberOfImprovements_min) == (numberOfImprovements_sd <= 0d)) {
            throw new IllegalArgumentException(
                "numberOfImprovements.sd=" //$NON-NLS-1$
                    + numberOfImprovements_sd
                    + " impossible for numberOfImprovements.min="//$NON-NLS-1$
                    + numberOfImprovements_min
                    + " and numberOfImprovements.max="//$NON-NLS-1$
                    + numberOfImprovements_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetTime_min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (budgetTime_min < 0L) {
            throw new IllegalArgumentException(
                "budgetTime.min must be >=0, but is " //$NON-NLS-1$
                    + budgetTime_min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetTime_median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(budgetTime_median)) {
            throw new IllegalArgumentException(
                "budgetTime.median must be finite, but is " //$NON-NLS-1$
                    + budgetTime_median);
          }
          if (budgetTime_median < budgetTime_min) {
            throw new IllegalArgumentException(
                "budgetTime.median (" + budgetTime_median + //$NON-NLS-1$
                    ") must be greater or equal to budgetTime.min ("//$NON-NLS-1$
                    + budgetTime_min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetTime_max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (budgetTime_max < 0L) {
            throw new IllegalArgumentException(
                "budgetTime.max must be >=0, but is " //$NON-NLS-1$
                    + budgetTime_max);
          }
          if (budgetTime_max < budgetTime_median) {
            throw new IllegalArgumentException(
                "budgetTime.max (" + budgetTime_max + //$NON-NLS-1$
                    ") must be greater or equal to budgetTime.median ("//$NON-NLS-1$
                    + budgetTime_median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetTime_mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(budgetTime_mean)) {
            throw new IllegalArgumentException(
                "budgetTime.mean must be finite, but is " //$NON-NLS-1$
                    + budgetTime_mean);
          }
          if ((budgetTime_max < budgetTime_mean)
              || (budgetTime_min > budgetTime_mean)) {
            throw new IllegalArgumentException(
                (("budgetTime.mean (" + budgetTime_mean + //$NON-NLS-1$
                    ") must be inside [budgetTime.min, budgetTime.max], i.e., ("//$NON-NLS-1$
                    + budgetTime_min) + ',') + budgetTime_max
                    + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetTime_sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(budgetTime_sd)
              || (budgetTime_sd < 0d)) {
            throw new IllegalArgumentException(
                "budgetTime.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + budgetTime_sd);
          }
          if ((budgetTime_max > budgetTime_min) == (budgetTime_sd <= 0d)) {
            throw new IllegalArgumentException(
                "budgetTime.sd=" + budgetTime_sd + //$NON-NLS-1$
                    " impossible for budgetTime.min=" + //$NON-NLS-1$
                    budgetTime_min + " and budgetTime.max="//$NON-NLS-1$
                    + budgetTime_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetFEs_min = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (budgetFEs_min <= 0L) {
            throw new IllegalArgumentException(
                "budgetFEs.min must be >0, but is " //$NON-NLS-1$
                    + budgetFEs_min);
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetFEs_median = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(budgetFEs_median)) {
            throw new IllegalArgumentException(
                "budgetFEs.median must be finite, but is " //$NON-NLS-1$
                    + budgetFEs_median);
          }
          if (budgetFEs_median < budgetFEs_min) {
            throw new IllegalArgumentException(
                "budgetFEs.median (" + budgetFEs_median + //$NON-NLS-1$
                    ") must be greater or equal to budgetFEs.min ("//$NON-NLS-1$
                    + budgetFEs_min + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetFEs_max = Long.parseLong(
              line.substring(lastSemi, nextSemi).trim());
          if (budgetFEs_max <= 0L) {
            throw new IllegalArgumentException(
                "budgetFEs.max must be >0, but is " //$NON-NLS-1$
                    + budgetFEs_max);
          }
          if (budgetFEs_max < budgetFEs_median) {
            throw new IllegalArgumentException(
                "budgetFEs.max (" + budgetFEs_max + //$NON-NLS-1$
                    ") must be greater or equal to budgetFEs.median ("//$NON-NLS-1$
                    + budgetFEs_median + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetFEs_mean = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(budgetFEs_mean)) {
            throw new IllegalArgumentException(
                "budgetFEs.mean must be finite, but is " //$NON-NLS-1$
                    + budgetFEs_mean);
          }
          if ((budgetFEs_max < budgetFEs_mean)
              || (budgetFEs_min > budgetFEs_mean)) {
            throw new IllegalArgumentException(
                (("budgetFEs.mean (" + budgetFEs_mean + //$NON-NLS-1$
                    ") must be inside [budgetFEs.min, budgetFEs.max], i.e., ("//$NON-NLS-1$
                    + budgetFEs_min) + ',') + budgetFEs_max
                    + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          budgetFEs_sd = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (!Double.isFinite(budgetFEs_sd)
              || (budgetFEs_sd < 0d)) {
            throw new IllegalArgumentException(
                "budgetFEs.sd must be finite and >=0, but is " //$NON-NLS-1$
                    + budgetFEs_sd);
          }
          if ((budgetFEs_max > budgetFEs_min) == (budgetFEs_sd <= 0d)) {
            throw new IllegalArgumentException(
                "budgetFEs.sd=" + budgetFEs_sd + //$NON-NLS-1$
                    " impossible for budgetFEs.min=" + //$NON-NLS-1$
                    budgetFEs_min + " and budgetFEs.max="//$NON-NLS-1$
                    + budgetFEs_max + ").");//$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          successes = Integer.parseInt(
              line.substring(lastSemi, nextSemi).trim());
          if ((successes < 0) || (successes > runs)) {
            throw new IllegalArgumentException(
                "There cannot be " + //$NON-NLS-1$
                    successes + " successes in " + //$NON-NLS-1$
                    runs + " runs."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          ertTime = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (((!Double.isFinite(ertTime))
              && (ertTime != Double.POSITIVE_INFINITY))
              || (ertTime < 0d)) {
            throw new IllegalArgumentException(
                "ertTime cannot be " + //$NON-NLS-1$
                    ertTime);
          }
          if ((successes > 0) && (!Double.isFinite(ertTime))) {
            throw new IllegalArgumentException(
                "ertTime cannot be " + //$NON-NLS-1$
                    ertTime + " if there are " //$NON-NLS-1$
                    + successes + " successes."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          ertFEs = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if (((!Double.isFinite(ertFEs))
              && (ertFEs != Double.POSITIVE_INFINITY))
              || (ertFEs <= 0d)) {
            throw new IllegalArgumentException(
                "ertFEs cannot be " + //$NON-NLS-1$
                    ertFEs);
          }
          if ((successes > 0) && (!Double.isFinite(ertFEs))) {
            throw new IllegalArgumentException(
                "ertFEs cannot be " + //$NON-NLS-1$
                    ertFEs + " if there are " //$NON-NLS-1$
                    + successes + " successes."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;

          if (successes > 0) {

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successTime_min = Long.parseLong(
                line.substring(lastSemi, nextSemi).trim());
            if (successTime_min < 0L) {
              throw new IllegalArgumentException(
                  "successTime.min must be >=0, but is " //$NON-NLS-1$
                      + successTime_min);
            }
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successTime_min_setup = new Setup(
                line.substring(lastSemi, nextSemi).trim());
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successTime_median = Double.parseDouble(
                line.substring(lastSemi, nextSemi).trim());
            if (!Double.isFinite(successTime_median)) {
              throw new IllegalArgumentException(
                  "successTime.median must be finite, but is " //$NON-NLS-1$
                      + successTime_median);
            }
            if (successTime_median < successTime_min) {
              throw new IllegalArgumentException(
                  "successTime.median (" + successTime_median + //$NON-NLS-1$
                      ") must be greater or equal to successTime.min ("//$NON-NLS-1$
                      + successTime_min + ").");//$NON-NLS-1$
            }
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successTime_max = Long.parseLong(
                line.substring(lastSemi, nextSemi).trim());
            if (successTime_max < 0L) {
              throw new IllegalArgumentException(
                  "successTime.max must be >=0, but is " //$NON-NLS-1$
                      + successTime_max);
            }
            if (successTime_max < successTime_median) {
              throw new IllegalArgumentException(
                  "successTime.max (" + successTime_max + //$NON-NLS-1$
                      ") must be greater or equal to successTime.median ("//$NON-NLS-1$
                      + successTime_median + ").");//$NON-NLS-1$
            }
            if (successTime_max > lastImprovementTime_max) {
              throw new IllegalArgumentException(
                  "successTime.max (" + successTime_max + //$NON-NLS-1$
                      ") must be less or equal to lastImprovementTime.max ("//$NON-NLS-1$
                      + lastImprovementTime_max + ").");//$NON-NLS-1$
            }
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successTime_max_setup = new Setup(
                line.substring(lastSemi, nextSemi).trim());
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successTime_mean = Double.parseDouble(
                line.substring(lastSemi, nextSemi).trim());
            if (!Double.isFinite(successTime_mean)) {
              throw new IllegalArgumentException(
                  "successTime.mean must be finite, but is " //$NON-NLS-1$
                      + successTime_mean);
            }
            if ((successTime_max < successTime_mean)
                || (successTime_min > successTime_mean)) {
              throw new IllegalArgumentException(
                  (("successTime.mean (" + successTime_mean + //$NON-NLS-1$
                      ") must be inside [successTime.min, successTime.max], i.e., ("//$NON-NLS-1$
                      + successTime_min) + ',') + successTime_max
                      + ").");//$NON-NLS-1$
            }
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successTime_sd = Double.parseDouble(
                line.substring(lastSemi, nextSemi).trim());
            if (!Double.isFinite(successTime_sd)
                || (successTime_sd < 0d)) {
              throw new IllegalArgumentException(
                  "successTime.sd must be finite and >=0, but is " //$NON-NLS-1$
                      + successTime_sd);
            }
            if ((successTime_max > successTime_min) == (successTime_sd <= 0d)) {
              throw new IllegalArgumentException(
                  "successTime.sd=" + successTime_sd + //$NON-NLS-1$
                      " impossible for successTime.min=" + //$NON-NLS-1$
                      successTime_min + " and successTime.max="//$NON-NLS-1$
                      + successTime_max + ").");//$NON-NLS-1$
            }
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successFEs_min = Long.parseLong(
                line.substring(lastSemi, nextSemi).trim());
            if (successFEs_min <= 0L) {
              throw new IllegalArgumentException(
                  "successFEs.min must be >0, but is " //$NON-NLS-1$
                      + successFEs_min);
            }
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successFEs_min_setup = new Setup(
                line.substring(lastSemi, nextSemi).trim());
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successFEs_median = Double.parseDouble(
                line.substring(lastSemi, nextSemi).trim());
            if (!Double.isFinite(successFEs_median)) {
              throw new IllegalArgumentException(
                  "successFEs.median must be finite, but is " //$NON-NLS-1$
                      + successFEs_median);
            }
            if (successFEs_median < successFEs_min) {
              throw new IllegalArgumentException(
                  "successFEs.median (" + successFEs_median + //$NON-NLS-1$
                      ") must be greater or equal to successFEs.min ("//$NON-NLS-1$
                      + successFEs_min + ").");//$NON-NLS-1$
            }
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successFEs_max = Long.parseLong(
                line.substring(lastSemi, nextSemi).trim());
            if (successFEs_max <= 0L) {
              throw new IllegalArgumentException(
                  "successFEs.max must be >0, but is " //$NON-NLS-1$
                      + successFEs_max);
            }
            if (successFEs_max < successFEs_median) {
              throw new IllegalArgumentException(
                  "successFEs.max (" + successFEs_max + //$NON-NLS-1$
                      ") must be greater or equal to successFEs.median ("//$NON-NLS-1$
                      + successFEs_median + ").");//$NON-NLS-1$
            }
            if (successFEs_max > lastImprovementFE_max) {
              throw new IllegalArgumentException(
                  "successFEs.max (" + successFEs_max + //$NON-NLS-1$
                      ") must be less or equal to lastImprovementFE_max ("//$NON-NLS-1$
                      + lastImprovementFE_max + ").");//$NON-NLS-1$
            }
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successFEs_max_setup = new Setup(
                line.substring(lastSemi, nextSemi).trim());
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            successFEs_mean = Double.parseDouble(
                line.substring(lastSemi, nextSemi).trim());
            if (!Double.isFinite(successFEs_mean)) {
              throw new IllegalArgumentException(
                  "successFEs.mean must be finite, but is " //$NON-NLS-1$
                      + successFEs_mean);
            }
            if ((successFEs_max < successFEs_mean)
                || (successFEs_min > successFEs_mean)) {
              throw new IllegalArgumentException(
                  (("successFEs.mean (" + successFEs_mean + //$NON-NLS-1$
                      ") must be inside [successFEs.min, successFEs.max], i.e., ("//$NON-NLS-1$
                      + successFEs_min) + ',') + successFEs_max
                      + ").");//$NON-NLS-1$
            }
            lastSemi = nextSemi;

            nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                ++lastSemi);
            if (nextSemi >= 0) {
              throw new IllegalStateException(
                  "too many columns!");//$NON-NLS-1$
            }
            nextSemi = line.length();
            successFEs_sd = Double.parseDouble(
                line.substring(lastSemi, nextSemi).trim());
            if (!Double.isFinite(successFEs_sd)
                || (successFEs_sd < 0d)) {
              throw new IllegalArgumentException(
                  "successFEs.sd must be finite and >=0, but is " //$NON-NLS-1$
                      + successFEs_sd);
            }
            if ((successFEs_max > successFEs_min) == (successFEs_sd <= 0d)) {
              throw new IllegalArgumentException(
                  "successFEs.sd=" + successFEs_sd + //$NON-NLS-1$
                      " impossible for successFEs.min=" + //$NON-NLS-1$
                      successFEs_min + " and successFEs.max="//$NON-NLS-1$
                      + successFEs_max + ").");//$NON-NLS-1$
            }
            lastSemi = nextSemi;
          }

          consumer.accept(new EndResultStatistic(algorithm, //
              instance, //
              runs, //
              new EndResultStatistic.DoubleStatisticsBig(
                  bestF_min, //
                  bestF_min_setup, //
                  bestF_q050, //
                  bestF_q050_setup, //
                  bestF_q159, //
                  bestF_q159_setup, //
                  bestF_q250, //
                  bestF_q250_setup, //
                  bestF_median, //
                  bestF_median_setup, //
                  bestF_q750, //
                  bestF_q750_setup, //
                  bestF_q841, //
                  bestF_q841_setup, //
                  bestF_q950, //
                  bestF_q950_setup, //
                  bestF_max, //
                  bestF_max_setup, //
                  bestF_mean, //
                  bestF_mean_setup, //
                  bestF_sd), //
              new EndResultStatistic.IntStatisticsBig(
                  totalTime_min, totalTime_q050, totalTime_q159,
                  totalTime_q250, totalTime_median,
                  totalTime_q750, totalTime_q841, totalTime_q950,
                  totalTime_max, totalTime_mean, totalTime_sd), //
              new EndResultStatistic.IntStatisticsBig(
                  totalFEs_min, totalFEs_q050, totalFEs_q159,
                  totalFEs_q250, totalFEs_median, totalFEs_q750,
                  totalFEs_q841, totalFEs_q950, totalFEs_max,
                  totalFEs_mean, totalFEs_sd), //
              new EndResultStatistic.IntStatisticsBig(
                  lastImprovementTime_min,
                  lastImprovementTime_q050,
                  lastImprovementTime_q159,
                  lastImprovementTime_q250,
                  lastImprovementTime_median,
                  lastImprovementTime_q750,
                  lastImprovementTime_q841,
                  lastImprovementTime_q950,
                  lastImprovementTime_max,
                  lastImprovementTime_mean,
                  lastImprovementTime_sd), //
              new EndResultStatistic.IntStatisticsBig(
                  lastImprovementFE_min, lastImprovementFE_q050,
                  lastImprovementFE_q159, lastImprovementFE_q250,
                  lastImprovementFE_median,
                  lastImprovementFE_q750, lastImprovementFE_q841,
                  lastImprovementFE_q950, lastImprovementFE_max,
                  lastImprovementFE_mean, lastImprovementFE_sd), //
              new EndResultStatistic.IntStatisticsBig(
                  numberOfImprovements_min,
                  numberOfImprovements_q050,
                  numberOfImprovements_q159,
                  numberOfImprovements_q250,
                  numberOfImprovements_median,
                  numberOfImprovements_q750,
                  numberOfImprovements_q841,
                  numberOfImprovements_q950,
                  numberOfImprovements_max,
                  numberOfImprovements_mean,
                  numberOfImprovements_sd), //
              new EndResultStatistic.IntStatisticsSmall(
                  budgetTime_min, budgetTime_median,
                  budgetTime_max, budgetTime_mean,
                  budgetTime_sd), //
              new EndResultStatistic.IntStatisticsSmall(
                  budgetFEs_min, budgetFEs_median, budgetFEs_max,
                  budgetFEs_mean, budgetFEs_sd), //
              successes, //
              ertTime, //
              ertFEs, //
              (successes <= 0) ? null : //
                  new EndResultStatistic.IntStatisticsSmallWithSetups(
                      successTime_min, //
                      successTime_min_setup, //
                      successTime_median, //
                      successTime_max, //
                      successTime_max_setup, //
                      successTime_mean, //
                      successTime_sd), //
              (successes <= 0) ? null : //
                  new EndResultStatistic.IntStatisticsSmallWithSetups(
                      successFEs_min, //
                      successFEs_min_setup, //
                      successFEs_median, //
                      successFEs_max, //
                      successFEs_max_setup, //
                      successFEs_mean, //
                      successFEs_sd)));
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
  private static final class Parser
      implements Consumer<EndResult> {
    /** the holders */
    private HashMap<String, HashMap<String, Holder>> m_holders;
    /** the success predicate */
    private Predicate<EndResult> m_success;
    /** the instance name mapper */
    private final Function<String, String> m_instanceNameMapper;
    /** the algorithm name mapper */
    private final Function<String, String> m_algorithmNameMapper;

    /**
     * create
     *
     * @param success
     *          the success predicate
     * @param instanceNameMapper
     *          the instance name mapper
     * @param algorithmNameMapper
     *          he algorithm name mapper
     */
    Parser(final Predicate<EndResult> success,
        final Function<String, String> instanceNameMapper,
        final Function<String, String> algorithmNameMapper) {
      super();
      this.m_holders = new HashMap<>();
      this.m_success = Objects.requireNonNull(success);
      this.m_instanceNameMapper =
          Objects.requireNonNull(instanceNameMapper);
      this.m_algorithmNameMapper =
          Objects.requireNonNull(algorithmNameMapper);
    }

    /** {@inheritDoc} */
    @Override
    public void accept(final EndResult t) {
      final String useAlgo =
          this.m_algorithmNameMapper.apply(t.algorithm);
      if (useAlgo == null) {
        return;
      }
      final String useInst =
          this.m_instanceNameMapper.apply(t.instance);
      if (useInst == null) {
        return;
      }

      HashMap<String, Holder> ifa = this.m_holders.get(useAlgo);
      if (ifa == null) {
        ifa = new HashMap<>();
        if (this.m_holders.put(useAlgo, ifa) != null) {
          throw new ConcurrentModificationException();
        }
      }

      Holder h = ifa.get(useInst);
      if (h == null) {
        h = new Holder(useAlgo, useInst, this.m_success);
        if (ifa.put(useInst, h) != null) {
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
    Holder[] doFinalize() {
      final Holder[] holders = this.m_holders.values().stream()
          .flatMap(v -> v.values().stream()).sorted()
          .toArray(i -> new Holder[i]);
      this.m_holders.clear();
      this.m_holders = null;
      this.m_success = null;
      for (final Holder h : holders) {
        h.doFinalize();
      }
      return holders;
    }
  }

  /** the data holder */
  private static final class Holder
      implements Comparable<Holder>, Consumer<EndResult> {
    /** the algorithm */
    final String algorithm;

    /** the instance */
    final String instance;

    /** the setups */
    private HashSet<InnerSetup> m_setups;
    /** the unique best f setups */
    InnerSetup[] m_uniqueBestF;

    /** the success predicate */
    private Predicate<EndResult> m_success;

    /** the fastest successful run in terms of runtime */
    Setup m_fastestSuccessTimeSetup;
    /** the fastest successful run in terms of runtime */
    long m_fastestSuccessTime;
    /** the slowest successful run in terms of runtime */
    Setup m_slowestSuccessTimeSetup;
    /** the slowest successful run in terms of runtime */
    long m_slowestSuccessTime;
    /** the fastest successful run in terms of runFEs */
    Setup m_fastestSuccessFEsSetup;
    /** the fastest successful run in terms of runFEs */
    long m_fastestSuccessFEs;
    /** the slowest successful run in terms of runFEs */
    Setup m_slowestSuccessFEsSetup;
    /** the slowest successful run in terms of runFEs */
    long m_slowestSuccessFEs;

    /** the best-f statistic */
    Statistic m_bestF;
    /** the total time statistic */
    Statistic m_totalTime;
    /** the total FEs statistic */
    Statistic m_totalFEs;
    /** the last improvement time statistic */
    Statistic m_lastImprovementTime;
    /** the last improvement FEs statistic */
    Statistic m_lastImprovementFE;
    /** the number of improvements statistic */
    Statistic m_numberOfImprovements;
    /** the budget time statistic */
    Statistic m_budgetTime;
    /** the budget FEs statistic */
    Statistic m_budgetFEs;
    /** the empirical expected running time statistic */
    Statistic m_ertTime;
    /** the empirical expected running FEs statistic */
    Statistic m_ertFEs;
    /** the success FEs */
    Statistic m_successFEs;
    /** the success time */
    Statistic m_successTime;

    /** the number of successes */
    int m_successes;
    /** are we finalized? */
    boolean m_finalized;

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
    Holder(final String _algo, final String _inst,
        final Predicate<EndResult> success) {
      this.algorithm = Objects.requireNonNull(_algo);
      this.instance = Objects.requireNonNull(_inst);
      this.m_setups = new HashSet<>();

      this.m_bestF = new Doubles();
      this.m_totalTime = new Longs();
      this.m_totalFEs = new Longs();
      this.m_lastImprovementTime = new Longs();
      this.m_lastImprovementFE = new Longs();
      this.m_numberOfImprovements = new Longs();
      this.m_budgetTime = new Longs();
      this.m_budgetFEs = new Longs();
      this.m_ertTime = new Longs();
      this.m_ertFEs = new Longs();
      this.m_successFEs = new Longs();
      this.m_successTime = new Longs();
      this.m_success = Objects.requireNonNull(success);
      this.m_fastestSuccessFEs = Long.MAX_VALUE;
      this.m_slowestSuccessFEs = Long.MIN_VALUE;
      this.m_fastestSuccessTime = Long.MAX_VALUE;
      this.m_slowestSuccessTime = Long.MIN_VALUE;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final Holder o) {
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
    public void accept(final EndResult t) {
      if (!this.m_setups.add(new InnerSetup(t))) {
        throw new IllegalStateException("Seed '" + //$NON-NLS-1$
            t.seed + "' appears twice for algorithm '"//$NON-NLS-1$
            + t.algorithm + "' on instance '"//$NON-NLS-1$
            + t.instance + "'.");//$NON-NLS-1$
      }

      this.m_bestF.add(t.bestF);
      this.m_totalTime.add(t.totalTime);
      this.m_totalFEs.add(t.totalFEs);
      this.m_lastImprovementTime.add(t.lastImprovementTime);
      this.m_lastImprovementFE.add(t.lastImprovementFE);
      this.m_numberOfImprovements.add(t.numberOfImprovements);
      this.m_budgetTime.add(t.budgetTime);
      this.m_budgetFEs.add(t.budgetFEs);

      if (this.m_success.test(t)) {
        ++this.m_successes;
        this.m_ertTime.add(t.lastImprovementTime);
        this.m_ertFEs.add(t.lastImprovementFE);
        this.m_successFEs.add(t.lastImprovementFE);
        this.m_successTime.add(t.lastImprovementTime);

        Setup use = null;
        if (t.lastImprovementFE < this.m_fastestSuccessFEs) {
          this.m_fastestSuccessFEs = t.lastImprovementFE;
          this.m_fastestSuccessFEsSetup = use = new Setup(t);
        }

        if (t.lastImprovementFE > this.m_slowestSuccessFEs) {
          this.m_slowestSuccessFEs = t.lastImprovementFE;
          this.m_slowestSuccessFEsSetup =
              ((use == null) ? (use = new Setup(t)) : use);
        }

        if (t.lastImprovementTime < this.m_fastestSuccessTime) {
          this.m_fastestSuccessTime = t.lastImprovementTime;
          this.m_fastestSuccessTimeSetup =
              ((use == null) ? (use = new Setup(t)) : use);
        }

        if (t.lastImprovementTime > this.m_slowestSuccessTime) {
          this.m_slowestSuccessTime = t.lastImprovementTime;
          this.m_slowestSuccessTimeSetup =
              ((use == null) ? (use = new Setup(t)) : use);
        }

      } else {
        this.m_ertTime.add(t.totalTime);
        this.m_ertFEs.add(t.totalFEs);
      }
    }

    /** finalize */
    void doFinalize() {
      if (this.m_finalized) {
        throw new IllegalStateException();
      }
      this.m_finalized = true;

      final HashSet<InnerSetup> data = this.m_setups;
      this.m_setups = null;
      final InnerSetup[] tmp =
          data.toArray(new InnerSetup[data.size()]);
      data.clear();
      for (final InnerSetup s : tmp) {
        data.add(s);
      }
      data.toArray(tmp);
      Arrays.sort(
          this.m_uniqueBestF = Arrays.copyOf(tmp, data.size()));
      data.clear();

      this.m_success = null;

      this.m_bestF = this.m_bestF.doFinalize();
      this.m_totalTime = this.m_totalTime.doFinalize();
      this.m_totalFEs = this.m_totalFEs.doFinalize();
      this.m_lastImprovementTime =
          this.m_lastImprovementTime.doFinalize();
      this.m_lastImprovementFE =
          this.m_lastImprovementFE.doFinalize();
      this.m_numberOfImprovements =
          this.m_numberOfImprovements.doFinalize();
      this.m_budgetTime = this.m_budgetTime.doFinalize();
      this.m_budgetFEs = this.m_budgetFEs.doFinalize();
      this.m_ertTime = this.m_ertTime.doFinalize();
      this.m_ertFEs = this.m_ertFEs.doFinalize();

      if (this.m_successes > 0) {
        this.m_successFEs = this.m_successFEs.doFinalize();
        this.m_successTime = this.m_successTime.doFinalize();
      } else {
        this.m_successFEs = null;
        this.m_successTime = null;
      }
    }

    /** the inner setup */
    private final class InnerSetup extends Setup {
      /** the inner setup */
      final double m_bestF2;

      /**
       * create the inner setup
       *
       * @param e
       *          the end result record
       */
      InnerSetup(final EndResult e) {
        super(e);
        this.m_bestF2 = e.bestF;
      }

      /** {@inheritDoc} */
      @Override
      public int hashCode() {
        if (Holder.this.m_finalized) {
          return Double.hashCode(this.m_bestF2);
        }
        return super.hashCode();
      }

      /** {@inheritDoc} */
      @Override
      public boolean equals(final Object o) {
        if (o == this) {
          return true;
        }
        if (Holder.this.m_finalized) {
          return Double.compare(this.m_bestF2,
              ((InnerSetup) o).m_bestF2) == 0;
        }
        return super.equals(o);
      }

      /** {@inheritDoc} */
      @Override
      public int compareTo(final Setup s) {
        if (Holder.this.m_finalized) {
          return Double.compare(this.m_bestF2,
              ((InnerSetup) s).m_bestF2);
        }
        return super.compareTo(s);
      }
    }
  }

  /**
   * print the arguments
   *
   * @param s
   *          the print stream
   */
  static final void printArgs(final PrintStream s) {
    EndResults.printArgs(s);
    CommandLineArgs.printEndResultsStatFile(s);
    CommandLineArgs.printSuccess(s);
    CommandLineArgs.printAlgorithmNameMapper(s);
    CommandLineArgs.printInstanceNameMapper(s);
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    ConsoleIO.stdout(s -> {
      s.println(
          "Welcome to the End-Result Statistics CSV Table Generator"); //$NON-NLS-1$
      s.println("The command line arguments are as follows: ");//$NON-NLS-1$
      EndResultStatistics.printArgs(s);
      s.println(
          "If you do not set the arguments, defaults will be used.");//$NON-NLS-1$
    });

    Configuration.putCommandLine(args);

    final Path in = CommandLineArgs.getSourceDir();
    final Path out = CommandLineArgs.getDestDir();
    final String name = CommandLineArgs.getEndResultsStatFile();
    final Function<String, String> algoNameMap =
        CommandLineArgs.getAlgorithmNameMapper();
    final Function<String, String> instNameMap =
        CommandLineArgs.getInstanceNameMapper();
    final Predicate<EndResult> success =
        CommandLineArgs.getSuccess();
    Configuration.print();

    try {
      final Path endResults =
          EndResults.makeEndResultsTable(in, out, true);

      EndResultStatistics.makeEndResultStatisticsTable(
          endResults, out, success, instNameMap, algoNameMap,
          name, false, true);

    } catch (final Throwable error) {
      ConsoleIO.stderr(
          "An error occured while creating the end result statistics tables.", //$NON-NLS-1$
          error);
      System.exit(1);
    }
  }
}
