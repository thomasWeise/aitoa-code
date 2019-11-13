package aitoa.utils.logs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import aitoa.structure.LogFormat;
import aitoa.utils.ConsoleIO;
import aitoa.utils.IOUtils;
import aitoa.utils.Statistics;
import aitoa.utils.logs.EndResults.EndResult;

/**
 * With this class we create a CSV file with end result
 * statistics
 */
public class EndResultStatistics {

  /** the file name used for end result statistics tables */
  public static final String FILE_NAME =
      "endResultStatistics.txt"; //$NON-NLS-1$

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
   * Create the end results table.
   *
   * @param endResults
   *          the path to the end results file
   * @param outputFolder
   *          the output folder
   * @param success
   *          the success predicate
   * @param keepExisting
   *          if the end results table exists, should it be
   *          preserved?
   * @param logProgressToConsole
   *          should logging information be printed?
   * @return the path to the end results table
   * @throws IOException
   *           if i/o fails
   */
  public static final Path makeEndResultStatisticsTable(
      final Path endResults, final Path outputFolder,
      final Predicate<EndResult> success,
      final boolean keepExisting,
      final boolean logProgressToConsole) throws IOException {

    final Path in = IOUtils.canonicalizePath(endResults);
    if (!(Files.exists(in) && Files.isRegularFile(in)
        && Files.isReadable(in))) {
      throw new IOException(in + " is not a readable file."); //$NON-NLS-1$
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
        out.resolve(EndResultStatistics.FILE_NAME));
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
    __Parser p = new __Parser(success);
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

        if (h.m_lastImprovementFEs.size() != runs) {
          throw new IllegalStateException(
              "inconsistent number of runs."); //$NON-NLS-1$
        }
        final Number lastImprovementFEsMean = EndResultStatistics
            .__printStat(h.m_lastImprovementFEs,
                EndResultStatistics.QUANTILES_BIG, bw);
        h.m_lastImprovementFEs = null;
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
                .numberToStringForLog(lastImprovementFEsMean));
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

    return end;
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
    _Statistic m_lastImprovementFEs;
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
      this.m_lastImprovementFEs = new _Longs();
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
      this.m_lastImprovementFEs._add(t.lastImprovementFEs);
      this.m_numberOfImprovements._add(t.numberOfImprovements);
      this.m_budgetTime._add(t.budgetTime);
      this.m_budgetFEs._add(t.budgetFEs);

      if (this.m_success.test(t)) {
        ++this.m_successes;
        this.m_ertTime._add(t.lastImprovementTime);
        this.m_ertFEs._add(t.lastImprovementFEs);
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
      this.m_lastImprovementFEs =
          this.m_lastImprovementFEs._finalize();
      this.m_numberOfImprovements =
          this.m_numberOfImprovements._finalize();
      this.m_budgetTime = this.m_budgetTime._finalize();
      this.m_budgetFEs = this.m_budgetFEs._finalize();
      this.m_ertTime = this.m_ertTime._finalize();
      this.m_ertFEs = this.m_ertFEs._finalize();
    }
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    ConsoleIO.stdout(
        "Welcome to the End-Result Statistics CSV Table Generator"); //$NON-NLS-1$
    if ((args.length < 2) || (args.length > 3)) {
      ConsoleIO.stdout((s) -> {
        s.println(
            "You must provide at least two and at most three command line arguments: srcDir, dstDir, and optionally goal.");//$NON-NLS-1$
        s.println(
            " srcDir is the directory with the recorded experiment results (log file root dir).");//$NON-NLS-1$
        s.println(
            " dstDir is the directory where the table should be written to.");//$NON-NLS-1$
        s.println(
            " goal is the objective value at which a run is considered as success (if not provided, goalF will be taken).");//$NON-NLS-1$
      });
    }

    final Predicate<EndResult> pred;
    if (args.length == 3) {
      final double threshold = Double.parseDouble(args[2]);
      pred = (x) -> (x.bestF <= threshold);
    } else {
      pred = (x) -> (x.bestF <= x.goalF);
    }

    try {
      final Path in = IOUtils.canonicalizePath(args[0]);
      ConsoleIO.stdout(("srcDir = '" + in) + '\'');//$NON-NLS-1$
      final Path out = IOUtils.canonicalizePath(args[1]);
      ConsoleIO.stdout(("dstDir = '" + out) + '\'');//$NON-NLS-1$

      final Path endResults =
          EndResults.makeEndResultsTable(in, out, true);
      EndResultStatistics.makeEndResultStatisticsTable(
          endResults, out, pred, false, true);
    } catch (final Throwable error) {
      ConsoleIO.stderr(
          "An error occured while creating the end result statistics tables.", //$NON-NLS-1$
          error);
    }
  }
}
