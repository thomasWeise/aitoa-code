package aitoa.utils.logs;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;

import aitoa.utils.Configuration;

/**
 * A class just defining command line arguments to be used by
 * many tools inside this package
 */
abstract class _CommandLineArgs {

  /** the source directory parameter */
  private static final String PARAM_SRC_DIR = "src"; //$NON-NLS-1$
  /** the destination directory parameter */
  private static final String PARAM_DST_DIR = "dest";//$NON-NLS-1$

  /**
   * print the source directory argument
   *
   * @param s
   *          the print stream
   */
  static final void _printSourceDir(final PrintStream s) {
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_SRC_DIR);
    s.println(
        "=dir: is the directory with the recorded experiment results (log file root dir).");//$NON-NLS-1$
  }

  /**
   * get the input path
   *
   * @return the input path
   */
  static final Path _getSourceDir() {
    return Configuration.getPath(_CommandLineArgs.PARAM_SRC_DIR,
        () -> Paths.get("results"));//$NON-NLS-1$ ;
  }

  /**
   * print the destination directory argument
   *
   * @param s
   *          the print stream
   */
  static final void _printDestDir(final PrintStream s) {
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_DST_DIR);
    s.println(
        "=dir: is the directory where the output should be written to.");//$NON-NLS-1$
  }

  /**
   * get the output path
   *
   * @return the output path
   */
  static final Path _getDestDir() {
    return Configuration.getPath(_CommandLineArgs.PARAM_DST_DIR,
        () -> Paths.get("evaluation"));//$NON-NLS-1$ ;
  }

  /** the goal function value */
  private static final String PARAM_GOAL = "goal"; //$NON-NLS-1$
  /** the goal function class */
  private static final String PARAM_GOAL_FUNC = "goalFuncClass"; //$NON-NLS-1$

  /**
   * get the success predicate
   *
   * @return the success predicate
   */
  @SuppressWarnings("unchecked")
  static final Predicate<EndResult> _getSuccess() {
    final String goalFunc = Configuration.getString(//
        _CommandLineArgs.PARAM_GOAL_FUNC);

    final ToDoubleFunction<String> func;
    if (goalFunc != null) {
      try {
        func =
            ToDoubleFunction.class.cast(Class.forName(goalFunc)
                .getDeclaredConstructor().newInstance());
      } catch (final Throwable error) {
        throw new IllegalArgumentException(
            "Cannot instantiate class '" //$NON-NLS-1$
                + goalFunc + "' from parameter '" + //$NON-NLS-1$
                _CommandLineArgs.PARAM_GOAL_FUNC + "'.", //$NON-NLS-1$
            error);
      }
    } else {
      func = null;
    }

    final Double goal =
        Configuration.getDouble(_CommandLineArgs.PARAM_GOAL);
    if (goal != null) {
      final double threshold = goal.doubleValue();
      if (func == null) {
        return (x) -> (x.bestF <= threshold);
      }
      return (x) -> {
        final double d = func.applyAsDouble(x.instance);
        return (x.bestF <= (Double.isFinite(d) ? d : threshold));
      };
    }

    if (func != null) {
      return (x) -> (x.bestF <= func.applyAsDouble(x.instance));
    }
    return null;
  }

  /**
   * Print the description of the success predicate argument to
   * the print stream
   *
   * @param s
   *          the print stream
   */
  static final void _printSuccess(final PrintStream s) {
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_GOAL);
    s.println(
        "=value: is the objective value at which a run is considered as success.");//$NON-NLS-1$
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_GOAL_FUNC);
    s.println(
        "=classname: the canonical classname of a function accepting an instance name and returning a goal objective value.");//$NON-NLS-1$
    s.print(" If both "); //$NON-NLS-1$
    s.print(_CommandLineArgs.PARAM_GOAL);
    s.print(" and "); //$NON-NLS-1$
    s.print(_CommandLineArgs.PARAM_GOAL_FUNC);
    s.print(" are specified, "); //$NON-NLS-1$
    s.print(_CommandLineArgs.PARAM_GOAL);
    s.print(" will be used when ");//$NON-NLS-1$
    s.print(_CommandLineArgs.PARAM_GOAL_FUNC);
    s.print(" returns a non-finite value.");//$NON-NLS-1$
    s.print(
        " If neither is provided, goalF from the log files we be used.");//$NON-NLS-1$
  }

  /** the file name */
  private static final String PARAM_END_RESULTS_STAT_FILE =
      "endResultsStatFile"; //$NON-NLS-1$

  /**
   * Print the description of the statistics file name
   *
   * @param s
   *          the print stream
   */
  static final void
      _printEndResultsStatFile(final PrintStream s) {
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_END_RESULTS_STAT_FILE);
    s.println(
        "=file name: is the name for the end result statistics file; if not provided, the default is used.");//$NON-NLS-1$
  }

  /**
   * Get the end result statistics file name
   *
   * @return the end result statistics file name
   */
  static final String _getEndResultsStatFile() {
    final String s = Configuration
        .getString(_CommandLineArgs.PARAM_END_RESULTS_STAT_FILE);
    return (s == null) ? EndResultStatistics.FILE_NAME : s;
  }

  /**
   * make a name part mapping function
   *
   * @param src
   *          the source string
   * @param omitOthers
   *          should all others be omitted?
   * @return the name mapping function
   */
  private static final Function<String, String>
      __nameMap(final String src, final boolean omitOthers) {
    final int i = src.indexOf("->"); //$NON-NLS-1$
    if (i < 0) {
      final Predicate<String> p =
          Pattern.compile(src).asPredicate();
      return (s) -> (p.test(s) ? s : null);
    }
    final String a = src.substring(0, i).trim();
    final String b = src.substring(i + 2).trim();
    if (a.isEmpty() || b.isEmpty()) {
      throw new IllegalArgumentException(
          "If '->' is included, none of its siedes can be empty, but at least one is in '"//$NON-NLS-1$
              + src + "'.");//$NON-NLS-1$
    }

    final Pattern pa = Pattern.compile(a);
    final Function<String, String> func =
        (s) -> pa.matcher(s).replaceAll(b);

    if (omitOthers) {
      final Predicate<String> paa = pa.asPredicate();
      return (s) -> paa.test(s) ? func.apply(s) : null;
    }

    return func;
  }

  /** the algorithm name map */
  private static final String PARAM_ALGO_NAME_MAP =
      "algoNameMap";//$NON-NLS-1$
  /**
   * should algorithms that do not match to the name map be
   * omitted?
   */
  private static final String PARAM_ALGO_NAME_MAP_OMIT_OTHERS =
      "algoNameMapOmitOthers";//$NON-NLS-1$

  /**
   * Get the algorithm name mapper
   *
   * @return the algorithm name mapper
   */
  static final Function<String, String>
      _getAlgorithmNameMapper() {
    final String s = Configuration
        .getString(_CommandLineArgs.PARAM_ALGO_NAME_MAP);
    if (s != null) {
      return _CommandLineArgs.__nameMap(s,
          Configuration.getBoolean(
              _CommandLineArgs.PARAM_ALGO_NAME_MAP_OMIT_OTHERS));
    }
    return Function.identity();
  }

  /**
   * Print the description of the algorithm name mapper
   *
   * @param s
   *          the print stream
   */
  static final void
      _printAlgorithmNameMapper(final PrintStream s) {
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_ALGO_NAME_MAP);
    s.println(
        "=in->out: map the algorithm names that match regular expression 'in' to 'out'.");//$NON-NLS-1$

    s.print(' ');
    s.print(_CommandLineArgs.PARAM_ALGO_NAME_MAP_OMIT_OTHERS);
    s.print(
        "=true|false: omit all algorithms not matching to 'in' given in ");//$NON-NLS-1$
    s.print(_CommandLineArgs.PARAM_ALGO_NAME_MAP);
    s.println('.');
    s.print(
        " Is assumed to be true if no '->' is contained in ");//$NON-NLS-1$
    s.print(_CommandLineArgs.PARAM_ALGO_NAME_MAP);
    s.println('.');
  }

  /** the instance name map */
  private static final String PARAM_INST_NAME_MAP =
      "instNameMap";//$NON-NLS-1$
  /**
   * should instances that do not match to the name map be
   * omitted?
   */
  private static final String PARAM_INST_NAME_MAP_OMIT_OTHERS =
      "instNameMapOmitOthers";//$NON-NLS-1$

  /**
   * Get the instance name mapper
   *
   * @return the instance name mapper
   */
  static final Function<String, String>
      _getInstanceNameMapper() {
    final String s = Configuration
        .getString(_CommandLineArgs.PARAM_INST_NAME_MAP);
    if (s != null) {
      return _CommandLineArgs.__nameMap(s,
          Configuration.getBoolean(
              _CommandLineArgs.PARAM_INST_NAME_MAP_OMIT_OTHERS));
    }
    return Function.identity();
  }

  /**
   * Print the description of the instance name mapper
   *
   * @param s
   *          the print stream
   */
  static final void
      _printInstanceNameMapper(final PrintStream s) {
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_INST_NAME_MAP);
    s.println(
        "=in->out: map the instance names that match regular expression 'in' to 'out'.");//$NON-NLS-1$

    s.print(' ');
    s.print(_CommandLineArgs.PARAM_INST_NAME_MAP_OMIT_OTHERS);
    s.println(
        "=true|false: omit all instance not matching to 'in' given in ");//$NON-NLS-1$
    s.print(_CommandLineArgs.PARAM_INST_NAME_MAP);
    s.println('.');
    s.print(
        " Is assumed to be true if no '->' is contained in ");//$NON-NLS-1$
    s.print(_CommandLineArgs.PARAM_INST_NAME_MAP);
    s.println('.');
  }

  /** use time? */
  private static final String PARAM_USE_TIME = "time"; //$NON-NLS-1$
  /** use FEs? */
  private static final String PARAM_USE_FES = "fes"; //$NON-NLS-1$
  /** the name for the diagram */
  private static final String PARAM_ERTECDF_NAME = "ertEcdfFile"; //$NON-NLS-1$

  /**
   * print the arguments
   *
   * @param s
   *          the print stream
   */
  static final void _printUseFEs(final PrintStream s) {
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_USE_FES);
    s.println(": use FEs as time measure (default).");//$NON-NLS-1$
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_USE_TIME);
    s.println(": use runtime as time measure.");//$NON-NLS-1$
  }

  /**
   * get whether FEs should be used
   *
   * @return the whether FEs should be used
   */
  static final boolean _getUseFEs() {
    if (Configuration
        .getBoolean(_CommandLineArgs.PARAM_USE_FES)) {
      return true;
    }
    if (Configuration
        .getBoolean(_CommandLineArgs.PARAM_USE_TIME)) {
      return false;
    }
    return true;
  }

  /**
   * get the ert-ecdf file name
   *
   * @return the name
   */
  static final String _getErtEcdfFileName() {
    final String s = Configuration
        .getString(_CommandLineArgs.PARAM_ERTECDF_NAME);
    return (s != null) ? s : ErtEcdf.ERT_ECDF_DIAGRAM_BASE_NAME;
  }

  /**
   * get whether FEs should be used
   *
   * @param s
   *          the print stream to write to
   */
  static final void _printErtEcdfFileName(final PrintStream s) {
    s.print(' ');
    s.print(_CommandLineArgs.PARAM_ERTECDF_NAME);
    s.println(": the name of the ERT-ECDF diagram file.");//$NON-NLS-1$
  }
}
