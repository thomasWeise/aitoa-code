package aitoa.utils.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;

import aitoa.structure.LogFormat;
import aitoa.utils.Configuration;
import aitoa.utils.ConsoleIO;
import aitoa.utils.IOUtils;

/**
 * Create data for Ert-EcdF diagrams.
 * <p>
 * Warning: This class assumes that the goals are somehow the
 * global optima. In other words, it is not possible to make
 * improvements after the goals are reached. If this assumption
 * does not hold, then we need another approach and this class
 * cannot be used. If the assumption holds, then the ert-ecdf can
 * directly be created from the end results statistics file.
 */
public final class ErtEcdf {
  /** the base folder name for ert ecdf files */
  public static final String ERT_ECDF_FOLDER = "ertEcdf"; //$NON-NLS-1$
  /** the base base name for ert ecdf diagrams */
  public static final String ERT_ECDF_DIAGRAM_BASE_NAME =
      ErtEcdf.ERT_ECDF_FOLDER;
  /** the name for time column */
  public static final String COL_TIME = "ert.time";//$NON-NLS-1$
  /** the name for fes columns */
  public static final String COL_FES = "ert.FEs";//$NON-NLS-1$
  /** the relative ECDF value, always between 0 and 1 */
  public static final String COL_ECDF_REL = "ecdf.rel";//$NON-NLS-1$
  /** the absolute ecdf value, always an integer */
  public static final String COL_ECDF_ABS = "ecdf.abs";//$NON-NLS-1$
  /** the name for time sub-folders */
  public static final String USE_TIME_FOLDER = "ertTime";//$NON-NLS-1$
  /** the name for fes sub-folders */
  public static final String USE_FES_FOLDER = "ertFEs";//$NON-NLS-1$

  /**
   * write an ecdf line
   *
   * @param time
   *          the time
   * @param ecdfR
   *          the relative ecdf
   * @param ecdfA
   *          the absolute ecdf
   * @param bw
   *          the buffered writer
   * @throws IOException
   *           if i/o fails
   */
  private static final void __line(final double time,
      final double ecdfR, final int ecdfA,
      final BufferedWriter bw) throws IOException {
    bw.write(LogFormat.doubleToStringForLog(time));
    bw.write(LogFormat.CSV_SEPARATOR_CHAR);
    bw.write(LogFormat.doubleToStringForLog(ecdfR));
    bw.write(LogFormat.CSV_SEPARATOR_CHAR);
    bw.write(Integer.toString(ecdfA));
    bw.newLine();
  }

  /**
   * Create a folder with ERT-ECDF diagram data files. For each
   * selected algorithm, one file will be generated.
   *
   * @param endResultStatistics
   *          the path to the end results statistics file
   * @param outputFolder
   *          the output folder
   * @param useFEs
   *          {@code true} to use FEs as time measure,
   *          {@code false} to use runtime
   * @param useInstance
   *          a predicate checking whether an instance should be
   *          included in the diagram; if this is {@code null},
   *          all instances will be considered
   * @param useAlgorithm
   *          a predicate checking whether an algorithm should be
   *          included in the diagram; if this is {@code null},
   *          all algorithms will be considered
   * @param selectionID
   *          the id of the algorithm/instance selection, can be
   *          {@code null} or empty
   * @param logProgressToConsole
   *          should logging information be printed?
   * @return a map associating algorithm ids with the generated
   *         ert-ecdf data files, which additionally contains the
   *         path to the generated directory under key
   *         {@code null}
   * @throws IOException
   *           if i/o fails
   */
  public static final Map<String, Path> makeErtEcdf(
      final Path endResultStatistics, final Path outputFolder,
      final boolean useFEs, final Predicate<String> useInstance,
      final Predicate<String> useAlgorithm,
      final String selectionID,
      final boolean logProgressToConsole) throws IOException {

    final Path in = IOUtils.requireFile(endResultStatistics);
    final Path out =
        IOUtils.requireDirectory(outputFolder, true);

    final ToDoubleFunction<EndResultStatistic> timeGetter;
    final String ertName;
    if (useFEs) {
      timeGetter = (s) -> s.ertFEs;
      ertName = ErtEcdf.USE_FES_FOLDER;
    } else {
      timeGetter = (s) -> s.ertTime;
      ertName = ErtEcdf.USE_TIME_FOLDER;
    }

    final Path endFolder;
    if ((selectionID == null) || (selectionID.isEmpty())) {
      endFolder = IOUtils.canonicalizePath(
          out.resolve(ErtEcdf.ERT_ECDF_FOLDER).resolve(ertName));
    } else {
      endFolder = IOUtils
          .canonicalizePath(out.resolve(ErtEcdf.ERT_ECDF_FOLDER)
              .resolve(ertName).resolve(selectionID));
    }

    if (Files.exists(endFolder)) {
      if (!Files.isDirectory(endFolder)) {
        throw new IOException(endFolder + " is not a folder."); //$NON-NLS-1$
      }
      if (logProgressToConsole) {
        ConsoleIO.stdout("Ert-ecdf folder '" + //$NON-NLS-1$
            endFolder
            + "' found, but will be deleted and re-created.");//$NON-NLS-1$
      }
      IOUtils.delete(endFolder);
    }
    Files.createDirectories(endFolder);

    if (logProgressToConsole) {
      ConsoleIO.stdout(
          "Now beginning to load data from result statistics table '" //$NON-NLS-1$
              + in + "'.");//$NON-NLS-1$
    }

    __Parser parser = new __Parser(timeGetter,
        ((useInstance == null) ? (x) -> true : useInstance),
        (useAlgorithm == null) ? (x) -> true : useAlgorithm);
    EndResultStatistics.parseEndResultStatisticsTable(
        endResultStatistics, parser, logProgressToConsole);

    final __Result result = parser._finalize();
    parser = null;

    if (logProgressToConsole) {
      ConsoleIO
          .stdout("Finished loading data from results table '" //$NON-NLS-1$
              + in + "', now computing ECDF and writing to '"//$NON-NLS-1$
              + endFolder + "'.");//$NON-NLS-1$
    }

    double maxErt = Double.NEGATIVE_INFINITY;
    double minErt = Double.POSITIVE_INFINITY;
    for (final __Algorithm a : result.algorithms) {
      if (a.solutions.length > 0) {
        minErt = Math.min(minErt, a.solutions[0]);
        maxErt = Math.max(maxErt,
            a.solutions[a.solutions.length - 1]);
      }
    }
    if (minErt >= maxErt) {
      throw new IllegalStateException(
          ((("Not enough successful data found, ert range collapses to ["//$NON-NLS-1$
              + minErt) + ',') + maxErt) + ']');
    }
    if (minErt < 0d) {
      throw new IllegalArgumentException(
          "minert cannot be <0, but is "//$NON-NLS-1$
              + minErt);
    }

    final LinkedHashMap<String, Path> output =
        new LinkedHashMap<>();
    final char[] header = LogFormat
        .asComment(LogFormat.joinLogLine(ertName,
            ErtEcdf.COL_ECDF_REL, ErtEcdf.COL_ECDF_ABS))
        .toCharArray();

    final double div = result.instances;

    for (final __Algorithm algo : result.algorithms) {
      final Path path = IOUtils.canonicalizePath(
          endFolder.resolve(algo.algorithm + ".txt"));//$NON-NLS-1$

      try (final BufferedWriter bw =
          Files.newBufferedWriter(path)) {
        bw.write(header);
        bw.newLine();

        double time = 0d;
        int ecdf = 0;
        for (final double d : algo.solutions) {
          if (d > time) {
            ErtEcdf.__line(time, ecdf / div, ecdf, bw);
          }
          ++ecdf;
          time = d;
        }
        ErtEcdf.__line(time, ecdf / div, ecdf, bw);
        if (time < maxErt) {
          ErtEcdf.__line(maxErt, ecdf / div, ecdf, bw);
        } else {
          if (time > maxErt) {
            throw new IllegalStateException(//
                "ert>maxErt?"); //$NON-NLS-1$
          }
        }
      }

      if (output.put(algo.algorithm,
          IOUtils.requireFile(path)) != null) {
        throw new ConcurrentModificationException(
            algo.algorithm);
      }
    }

    if (logProgressToConsole) {
      ConsoleIO.stdout("Done writing to '"//$NON-NLS-1$
          + endFolder + "'.");//$NON-NLS-1$
    }

    if (output.put(null,
        IOUtils.requireDirectory(endFolder)) != null) {
      throw new ConcurrentModificationException();
    }

    return Collections.unmodifiableMap(output);
  }

  /** the internal parser class */
  private static final class __Parser
      implements Consumer<EndResultStatistic> {
    /** which instance to use */
    private final Predicate<String> m_useInstance;
    /** which algorithms to use */
    private final Predicate<String> m_useAlgorithm;
    /** the time getter */
    private final ToDoubleFunction<
        EndResultStatistic> m_timeGetter;
    /** the data */
    private HashMap<String, ArrayList<__Solution>> m_data;
    /** the instance counters */
    private HashMap<String, int[]> m_instanceCounters;

    /**
     * create
     *
     * @param timeGetter
     *          the method to extract the time
     * @param useInstance
     *          a predicate checking whether an instance should
     *          be included in the diagram; if this is
     *          {@code null}, all instances will be considered
     * @param useAlgorithm
     *          a predicate checking whether an algorithm should
     *          be included in the diagram; if this is
     *          {@code null}, all algorithms will be considered
     */
    __Parser(
        final ToDoubleFunction<EndResultStatistic> timeGetter,
        final Predicate<String> useInstance,
        final Predicate<String> useAlgorithm) {
      super();
      this.m_timeGetter = Objects.requireNonNull(timeGetter);
      this.m_useAlgorithm = Objects.requireNonNull(useAlgorithm);
      this.m_useInstance = Objects.requireNonNull(useInstance);
      this.m_data = new HashMap<>();
      this.m_instanceCounters = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public final void accept(final EndResultStatistic t) {
      if (this.m_useAlgorithm.test(t.algorithm)
          && this.m_useInstance.test(t.instance)) {

        final int[] count =
            this.m_instanceCounters.get(t.instance);
        if (count == null) {
          this.m_instanceCounters.put(t.instance,
              new int[] { 1 });
        } else {
          ++count[0];
        }

        final double time = this.m_timeGetter.applyAsDouble(t);

        if (Double.isFinite(time)) {
          if (time < 0d) {
            throw new IllegalArgumentException(//
                "time cannot be <0, but is " + time); //$NON-NLS-1$
          }
          ArrayList<__Solution> sols =
              this.m_data.get(t.algorithm);
          if (sols == null) {
            sols = new ArrayList<>();
            this.m_data.put(t.algorithm, sols);
          }
          sols.add(new __Solution(t.instance, time));
        }
      }
    }

    /**
     * finalize the results
     *
     * @return the results
     */
    final __Result _finalize() {
      if (this.m_data.isEmpty()) {
        throw new IllegalStateException("no algorithm found."); //$NON-NLS-1$
      }
      if (this.m_instanceCounters.isEmpty()) {
        throw new IllegalStateException("no instance found."); //$NON-NLS-1$
      }

      // we only consider instances to which all algorithms were
      // applied
      final int requiredCount = this.m_data.size();

      this.m_instanceCounters.entrySet()
          .removeIf((e) -> (e.getValue()[0] < requiredCount));
      final int instances = this.m_instanceCounters.size();
      if (instances <= 0) {
        throw new IllegalStateException(
            "no common instance found."); //$NON-NLS-1$
      }

      // filter the algorithms
      final __Algorithm[] algorithms =
          this.m_data.entrySet().stream()//
              .map((e) -> new __Algorithm(e.getKey(), //
                  e.getValue().stream()//
                      // keep only those instances to which
                      // all algorithms were applied
                      .filter((v) -> (this.m_instanceCounters
                          .containsKey(v.instance)))//
                      .mapToDouble((s) -> s.ert)//
                      .toArray())//
              ).toArray(i -> new __Algorithm[i]);

      this.m_data.clear();
      this.m_data = null;

      final __Result result =
          new __Result(instances, algorithms);
      this.m_instanceCounters.clear();
      this.m_instanceCounters = null;

      return result;
    }
  }

  /** the algorithm data */
  private static final class __Result {
    /** the instances */
    final int instances;
    /** the algorithms */
    final __Algorithm[] algorithms;

    /**
     * create
     *
     * @param i
     *          the instances
     * @param a
     *          the algorithm data
     */
    __Result(final int i, final __Algorithm[] a) {
      super();
      this.instances = i;
      if (i <= 0) {
        throw new IllegalStateException("no instances?"); //$NON-NLS-1$
      }
      this.algorithms = Objects.requireNonNull(a);
      Arrays.sort(a);
      if (a.length <= 0) {
        throw new IllegalStateException("no algorithms?"); //$NON-NLS-1$
      }
    }
  }

  /** the algorithm data */
  private static final class __Algorithm
      implements Comparable<__Algorithm> {
    /** the algorithm */
    final String algorithm;
    /** the data */
    final double[] solutions;

    /**
     * create the algorithm
     *
     * @param a
     *          the algorithm
     * @param d
     *          the data
     */
    __Algorithm(final String a, final double[] d) {
      super();
      this.algorithm = Objects.requireNonNull(a);
      this.solutions = Objects.requireNonNull(d);
      Arrays.sort(d);
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __Algorithm o) {
      return this.algorithm.compareTo(o.algorithm);
    }
  }

  /** a record of a solution */
  private static final class __Solution
      implements Comparable<__Solution> {
    /** the solved instance */
    final String instance;
    /** the ert */
    final double ert;

    /**
     * create the record
     *
     * @param i
     *          the instance
     * @param e
     *          the ert
     */
    __Solution(final String i, final double e) {
      super();
      this.instance = Objects.requireNonNull(i);
      if ((!Double.isFinite(e)) || (e < 0d)) {
        throw new IllegalStateException("invalid ert: " + e); //$NON-NLS-1$
      }
      this.ert = e;
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __Solution o) {
      final int i = Double.compare(this.ert, o.ert);
      if (i != 0) {
        return i;
      }
      return this.instance.compareTo(o.instance);
    }
  }

  /**
   * Read and verify an ert-ecdf file.
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
  public static final void parseErtEcdfFile(final Path path,
      final Consumer<ErtEcdfPoint> consumer,
      final boolean logProgressToConsole) throws IOException {

    final Path p = IOUtils.requireFile(path);

    if (!(p.getFileName().toString()
        .endsWith(LogFormat.FILE_SUFFIX))) {
      throw new IllegalArgumentException(//
          "File '" + p + //$NON-NLS-1$
              "' is not a valid ert-ecdf file, must end with '" //$NON-NLS-1$
              + LogFormat.FILE_SUFFIX + "'.");//$NON-NLS-1$
    }

    if (consumer == null) {
      throw new NullPointerException(//
          "null end result consumer");//$NON-NLS-1$
    }

    if (logProgressToConsole) {
      ConsoleIO.stdout(//
          "Now parsing ert-ecdf file '"//$NON-NLS-1$
              + p + "'.");//$NON-NLS-1$
    }

    try (final BufferedReader br = Files.newBufferedReader(p)) {
      ErtEcdfPoint e1 = new ErtEcdfPoint();
      ErtEcdfPoint e2 = new ErtEcdfPoint();
      ErtEcdfPoint etemp;
      String line2;
      int lineIndex = 0;
      boolean hasLine = false;
      boolean mustBeLast = false;

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

        if (mustBeLast) {
          throw new IllegalStateException(
              "Line comes after last permitted one."); //$NON-NLS-1$
        }

        try {
          int lastSemi = -1;
          int nextSemi =
              line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
                  ++lastSemi);
          e1.ert = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if ((!Double.isFinite(e1.ert)) || (e1.ert < 0d)) {
            throw new IllegalArgumentException(
                "ERT values must be >=0 and finite, but encountered " //$NON-NLS-1$
                    + e1.ert);
          }
          if (hasLine) {
            if ((e1.ert <= e2.ert) && (e1.ert != 0d)) {
              throw new IllegalStateException(
                  "ERT values must be increasing, with the only possible exception at time 0. But encountered " //$NON-NLS-1$
                      + e1.ert + " after " + e2.ert); //$NON-NLS-1$
            }
          }
          lastSemi = nextSemi;

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          e1.ecdfRel = Double.parseDouble(
              line.substring(lastSemi, nextSemi).trim());
          if ((!Double.isFinite(e1.ecdfRel)) || (e1.ecdfRel < 0d)
              || (e1.ecdfRel > 1d)) {
            throw new IllegalArgumentException(
                "Relative ECDF must be in [0,1], but encountered " //$NON-NLS-1$
                    + e1.ecdfRel);
          }
          lastSemi = nextSemi;
          if (hasLine) {
            if (e1.ecdfRel < e2.ecdfRel) {
              throw new IllegalStateException(
                  "ECDF values must be increasing, but encountered " //$NON-NLS-1$
                      + e1.ecdfRel + " after " + e2.ecdfRel); //$NON-NLS-1$
            }
            if (e1.ecdfRel <= e2.ecdfRel) {
              mustBeLast = true;
            }
          }

          nextSemi = line.indexOf(LogFormat.CSV_SEPARATOR_CHAR, //
              ++lastSemi);
          if (nextSemi >= 0) {
            throw new IllegalStateException("too many columns"); //$NON-NLS-1$
          }
          nextSemi = line.length();

          e1.ecdfAbs = Integer.parseInt(
              line.substring(lastSemi, nextSemi).trim());
          if (e1.ecdfAbs < 0) {
            throw new IllegalArgumentException(
                "There cannot be " + e1.ecdfAbs//$NON-NLS-1$
                    + " successful runs."); //$NON-NLS-1$
          }
          lastSemi = nextSemi;
          if (hasLine) {
            if (e1.ecdfAbs < e2.ecdfAbs) {
              throw new IllegalStateException(
                  "ECDF values must be increasing, but encountered " //$NON-NLS-1$
                      + e1.ecdfAbs + " after " + e2.ecdfAbs); //$NON-NLS-1$
            }
            if (e1.ecdfAbs <= e2.ecdfAbs) {
              if (!mustBeLast) {
                throw new IllegalStateException(
                    "If last ecdf.rel is "//$NON-NLS-1$
                        + e2.ecdfRel
                        + " and current ecdf.rel is " + //$NON-NLS-1$
                        e1.ecdfRel
                        + ", then current ecdf.abs cannot be " + //$NON-NLS-1$
                        e1.ecdfAbs + " if last ecdf.abs was " //$NON-NLS-1$
                        + e2.ecdfAbs);
              }
            }
          }

          consumer.accept(e1);

          hasLine = true;
          etemp = e1;
          e1 = e2;
          e2 = etemp;
        } catch (final Throwable error2) {
          throw new IOException(//
              "Line " + lineIndex //$NON-NLS-1$
                  + " is invalid: '" //$NON-NLS-1$
                  + line2 + "'.", //$NON-NLS-1$
              error2);
        }
      }

      if (!hasLine) {
        throw new IOException("no ert-ecdf data found");//$NON-NLS-1$
      }
    } catch (final Throwable error) {
      throw new IOException("Error when parsing ertEcdf file '"//$NON-NLS-1$
          + p + "'.", error);//$NON-NLS-1$
    }
  }

  /**
   * Read and verify a directory of ert-ecdf files.
   *
   * @param path
   *          the path to end results table
   * @param consumers
   *          a function which provides consumers for algorithm
   *          files. The input is the algorithm id, the output
   *          must be the consumer to parse the file.
   * @param logProgressToConsole
   *          should logging information be printed?
   * @throws IOException
   *           if i/o fails
   */
  public static final void parseErtEcdfFiles(final Path path,
      final Function<String, Consumer<ErtEcdfPoint>> consumers,
      final boolean logProgressToConsole) throws IOException {

    if (consumers == null) {
      throw new NullPointerException(//
          "null end result consumers");//$NON-NLS-1$
    }

    final Path in = IOUtils.requireDirectory(path);

    if (logProgressToConsole) {
      ConsoleIO.stdout(//
          "Now parsing all files in ert-ecdf directory '"//$NON-NLS-1$
              + in + "'.");//$NON-NLS-1$
    }

    boolean hasFile = false;
    for (final Path file : IOUtils.files(in)) {
      final String name = file.getFileName().toString();
      if (name.endsWith(LogFormat.FILE_SUFFIX)) {
        final String algo = name.substring(0, //
            name.length() - LogFormat.FILE_SUFFIX.length())
            .trim();
        if (algo.isEmpty()) {
          throw new IOException(
              "Invalid algorithm name for file '"//$NON-NLS-1$
                  + file + "'.");//$NON-NLS-1$
        }
        ErtEcdf.parseErtEcdfFile(file, consumers.apply(algo),
            logProgressToConsole);
        hasFile = true;
      } else {
        continue;
      }
    }

    if (!hasFile) {
      throw new IOException(//
          "Ert-Ecdf directory '" + path + //$NON-NLS-1$
              "' does not contain any file!");//$NON-NLS-1$
    }
  }

  /** use time? */
  private static final String PARAM_USE_TIME = "time"; //$NON-NLS-1$
  /** use FEs? */
  private static final String PARAM_USE_FES = "fes"; //$NON-NLS-1$
  /** the regular expression for algorithms */
  private static final String PARAM_ALGO_REGEX = "algoRegEx"; //$NON-NLS-1$
  /** the regular expression for instances */
  private static final String PARAM_INST_REGEX = "instRegEx"; //$NON-NLS-1$
  /** the name for the diagram */
  private static final String PARAM_NAME = "name"; //$NON-NLS-1$

  /**
   * print the arguments
   *
   * @param s
   *          the print stream
   */
  static final void _printArgs(final PrintStream s) {
    EndResultStatistics._printArgs(s);
    s.println(' ' + ErtEcdf.PARAM_USE_FES
        + ": use FEs as time measure (default).");//$NON-NLS-1$
    s.println(' ' + ErtEcdf.PARAM_USE_TIME
        + ": use runtime as time measure.");//$NON-NLS-1$
    s.println(' ' + ErtEcdf.PARAM_ALGO_REGEX
        + "=regex: an optional regular expression for selecting algorithms");//$NON-NLS-1$
    s.println(' ' + ErtEcdf.PARAM_INST_REGEX
        + "=regex: an optional regular expression for selecting instances");//$NON-NLS-1$
    s.println(' ' + ErtEcdf.PARAM_NAME
        + "=name: a name for the diagram");//$NON-NLS-1$
  }

  /**
   * get whether FEs should be used
   *
   * @return the whether FEs should be used
   */
  static final boolean _argUseFEs() {
    if (Configuration.getBoolean(ErtEcdf.PARAM_USE_FES)) {
      return true;
    }
    if (Configuration.getBoolean(ErtEcdf.PARAM_USE_TIME)) {
      return false;
    }
    return true;
  }

  /**
   * get a predicate
   *
   * @param key
   *          the key
   * @return the predicate
   */
  private static final Predicate<String>
      __predicate(final String key) {
    final String s = Configuration.getString(key);
    if (s == null) {
      return null;
    }
    return Pattern.compile(s).asPredicate();
  }

  /**
   * the algorithm selector
   *
   * @return the algorithm selector
   */
  static final Predicate<String> _argAlgoSelect() {
    return ErtEcdf.__predicate(ErtEcdf.PARAM_ALGO_REGEX);
  }

  /**
   * the instance selector
   *
   * @return the instance selector
   */
  static final Predicate<String> _argInstSelect() {
    return ErtEcdf.__predicate(ErtEcdf.PARAM_INST_REGEX);
  }

  /**
   * get the name
   *
   * @return the name
   */
  static final String _argName() {
    return Configuration.getString(ErtEcdf.PARAM_NAME);
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    ConsoleIO.stdout((s) -> {
      s.println("Welcome to the ERT-ECDF Generator"); //$NON-NLS-1$
      s.println("The command line arguments are as follows: ");//$NON-NLS-1$
      ErtEcdf._printArgs(s);
      s.println(
          "If you do not set the arguments, defaults will be used.");//$NON-NLS-1$
    });

    Configuration.putCommandLine(args);

    final Predicate<EndResult> pred =
        EndResultStatistics._argSuccess();

    final Path in = EndResults._argIn();
    final Path out = EndResults._argOut();

    final boolean useFEs = ErtEcdf._argUseFEs();

    final Predicate<String> algoSelect =
        ErtEcdf._argAlgoSelect();
    final Predicate<String> instSelect =
        ErtEcdf._argInstSelect();
    final String name = ErtEcdf._argName();

    Configuration.print();

    try {
      final Path endResults =
          EndResults.makeEndResultsTable(in, out, true);
      final Path endResultStatistics =
          EndResultStatistics.makeEndResultStatisticsTable(
              endResults, out, pred, null, pred == null, true);

      ErtEcdf.makeErtEcdf(endResultStatistics, out, useFEs,
          instSelect, algoSelect, name, true);

    } catch (final Throwable error) {
      ConsoleIO.stderr(
          "An error occured while creating the ERT-ECDF data.", //$NON-NLS-1$
          error);
    }
  }
}
