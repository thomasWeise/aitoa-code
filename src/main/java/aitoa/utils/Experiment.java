package aitoa.utils;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aitoa.algorithms.RandomSampling;
import aitoa.structure.BlackBoxProcessBuilder;
import aitoa.structure.IBlackBoxProcess;
import aitoa.structure.IMetaheuristic;
import aitoa.structure.IObjectiveFunction;
import aitoa.structure.LogFormat;

/** A class for processing and executing experiments */
public final class Experiment {

  /**
   * Process a name part derived from an object's
   * {@link Object#toString()} method, meaning that all
   * non-acceptable characters are transformed or removed
   *
   * @param part
   *          the string to be processed
   * @return the name part as acceptable for file and directory
   *         names
   */
  public static String nameFromObjectPrepare(final Object part) {
    return Experiment.nameStringPrepare(part.toString());
  }

  /**
   * Process a name part, meaning that all non-acceptable
   * characters are transformed or removed
   *
   * @param part
   *          the string to be processed
   * @return the name part as acceptable for file and directory
   *         names
   */
  public static String nameStringPrepare(final String part) {
    final Object res = Experiment.processNamePart(part);
    if (res == null) {
      return part;
    }
    if (res instanceof char[]) {
      return String.valueOf((char[]) res);
    }
    final Object[] k = ((Object[]) res);
    return String.valueOf(((char[]) (k[0])), 0,
        ((int[]) k[1])[0]);
  }

  /**
   * Process an array of name parts and merge them.
   *
   * @param parts
   *          the strings to be processed
   * @return the name part as acceptable for file and directory
   *         names
   */
  public static final String
      nameStringsMerge(final String... parts) {
    switch (parts.length) {
      case 0: {
        throw new IllegalArgumentException(
            "There must be at least one name part."); //$NON-NLS-1$
      }
      case 1: {
        return Experiment.nameStringPrepare(parts[0]);
      }
      default: {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String part : parts) {
          if (first) {
            first = false;
          } else {
            sb.append('_');
          }
          final Object res = Experiment.processNamePart(part);
          if (res == null) {
            sb.append(part);
          } else {
            if (res instanceof char[]) {
              sb.append((char[]) res);
            } else {
              final Object[] k = ((Object[]) res);
              sb.append(((char[]) (k[0])), 0, ((int[]) k[1])[0]);
            }
          }
        }
        return (sb.toString());
      }
    }
  }

  /**
   * Process an array of objects and convert each non-null object
   * to a name part and then merge these parts.
   *
   * @param parts
   *          the string to be processed
   * @return the name part as acceptable for file and directory
   *         names
   */
  public static final String
      nameFromObjectsMerge(final Object... parts) {
    int length = 0;
    for (int i = parts.length; (--i) >= 0;) {
      if (parts[i] != null) {
        length++;
      }
    }
    if (length <= 0) {
      throw new IllegalArgumentException(
          "There must be at least one non-null name component."); //$NON-NLS-1$
    }
    final String[] names = new String[length];

    int i = 0;
    for (final Object part : parts) {
      if (part != null) {
        names[i++] = part.toString();
      }
    }

    return Experiment.nameStringsMerge(names);
  }

  /**
   * check if a character is a white space
   *
   * @param ch
   *          the character
   * @return {@code true} if it is, {@code false} if it isn't
   */
  private static boolean isWhiteSpace(final char ch) {
    return (ch <= 32) || (ch == '_')//
        || (ch == '\u00A0') || (ch == '\u2007')
        || (ch == '\u202F')//
        || Character.isWhitespace(ch) //
        || Character.isSpaceChar(ch);
  }

  /**
   * pseudo-delete a character from a character array
   *
   * @param data
   *          the data
   * @param index
   *          the index
   * @param lengthMinusOne
   *          the length - 1
   */
  private static void delete(final char[] data, final int index,
      final int lengthMinusOne) {
    if (index < lengthMinusOne) {
      System.arraycopy(data, index + 1, data, index,
          lengthMinusOne - index);
    }
  }

  /**
   * Append a given name part to the specified string builder
   *
   * @param s
   *          the name part
   * @return either null if the string can be used as-is, a
   *         full-length char array or a two-object array, where
   *         the first one is the character array and the second
   *         one an int array of length 1 with the array length
   *         of the first array
   */
  private static Object processNamePart(final String s) {
    final char[] chars = s.toCharArray();
    boolean unchanged = true;
    int length = chars.length;

    if (length <= 0) {
      throw new IllegalArgumentException(
          "name part cannot be empty."); //$NON-NLS-1$
    }

    trimRight: for (;;) { // trim right
      final int next = length - 1;
      if (Experiment.isWhiteSpace(chars[next])) {
        if (next <= 0) {
          throw new IllegalArgumentException("name part '"//$NON-NLS-1$
              + s + "' only consists of white space!");//$NON-NLS-1$
        }
        length = next;
      } else {
        break trimRight;
      }
    }

    trimLeft: for (;;) { // trim left
      if (Experiment.isWhiteSpace(chars[0])) {
        Experiment.delete(chars, 0, --length);
      } else {
        break trimLeft;
      }
    }

    // cleanse
    boolean acceptSpace = false;
    looper: for (int i = 0; i < length; i++) {
      final char ch = chars[i];
      switch (ch) {
        case '!':
        case '"':
        case '#':
        case '$':
        case '%':
        case '&':
        case '\'':
        case '*':
        case '/':
        case ':':
        case ';':
        case '<':
        case '>':
        case '?':
        case '[':
        case '\\':
        case ']':
        case '^':
        case '`':
        case '{':
        case '|':
        case '}':
        case '‘': {
          chars[i] = '_';
          unchanged = false;
        } //$FALL-THROUGH$
        case '_': {
          if (!acceptSpace) {
            Experiment.delete(chars, i, --length);
          }
          acceptSpace = false;
          continue looper;
        }
        case '.': {
          chars[i] = 'd';
          unchanged = false;
          acceptSpace = true;
          continue looper;
        }
        default: {
          if (Experiment.isWhiteSpace(ch)) {
            if (acceptSpace) {
              chars[i] = '_';
              acceptSpace = false;
            } else {
              Experiment.delete(chars, i, --length);
            }
            continue looper;
          }
          acceptSpace = true;
          continue looper;
        }
      }
    }

    trimRight: for (;;) { // trim right
      final int next = length - 1;
      if (Experiment.isWhiteSpace(chars[next])) {
        if (next <= 0) {
          throw new IllegalArgumentException("name part '"//$NON-NLS-1$
              + s
              + "' only consists of characters that map to white space!");//$NON-NLS-1$
        }
        length = next;
      } else {
        break trimRight;
      }
    }

    // return
    if (length >= chars.length) {
      if (unchanged) {
        return null;
      }
      return chars;
    }
    return new Object[] { chars, new int[] { length } };
  }

  /**
   * Get the path to a suitable log file for the given
   * experimental. If {@code onlyComputePath==true}, then only
   * the path will be computed and returned. If
   * {@code onlyComputePath==false}, then the path will be
   * computed and it is checked whether it already exists. If so,
   * {@code null} is returned. Otherwise, the (empty) new log
   * file is created and the path is returned.
   *
   * @param root
   *          the root path
   * @param instance
   *          the instance name
   * @param algorithm
   *          the algorithm setup
   * @param randSeed
   *          the random seed
   * @param onlyComputePath
   *          should the log file be created ({@code false}) or
   *          just the path be composed ({@code true})?
   * @return the path, or {@code null} if the run should not be
   *         performed and {@code onlyComputePath==true}
   * @throws IOException
   *           if I/O fails
   */
  public static Path logFile(final Path root,
      final String algorithm, final String instance,
      final long randSeed, final boolean onlyComputePath)
      throws IOException {
    return Experiment.logFile(root, algorithm, instance,
        randSeed, null, onlyComputePath);
  }

  /**
   * Get the path to a suitable log file for the given
   * experimental run if that log file does not yet exist. This
   * method allows for both parallel execution and for restarting
   * of experiments.
   * <p>
   * The idea is that we allow for running several instances of
   * the JVM in parallel, each executing the same kind of
   * experiment. Before a new run is started, we create the
   * corresponding log file. File creation is an
   * {@linkplain java.nio.file.Files#createFile(Path, java.nio.file.attribute.FileAttribute...)
   * atomic} operation, meaning that it is impossible that two
   * threads/processes can successfully create the same file (for
   * one of them, it will always already exist). Thus, if we fail
   * to create the log file for a run anew, then this run is
   * already ongoing. We will then skip it. Via this mechanism,
   * we can very easily execute several experiments in parallel.
   * <p>
   * If an experiment was aborted, say, due to a crash of a
   * machine or power outage, then we can use the same mechanism
   * to resume experiments. We simply have to delete all
   * zero-sized files and then start the experiments. Since the
   * log data will be written to the log files only
   * <em>after</em> the runs, only log files of completed runs
   * will have a size larger than zero.
   * <p>
   * Overall, this mechanism allows us to do experiments in
   * parallel while not caring about threads or parallelism in
   * anyway. We just start the program as often as we have cores.
   *
   * @param root
   *          the root path
   * @param instance
   *          the instance name
   * @param algorithm
   *          the algorithm setup
   * @param randSeed
   *          the random seed
   * @param done
   *          a hash set to receive and remember the attempted
   *          runs and existing directories
   * @param onlyComputePath
   *          should the log file be created ({@code false}) or
   *          just the path be composed ({@code true})?
   * @return the path, or {@code null} if the run should not be
   *         performed
   * @throws IOException
   *           if I/O fails
   */
  private static Path logFile(final Path root,
      final String algorithm, final String instance,
      final long randSeed, final FileSet done,
      final boolean onlyComputePath) throws IOException {

    synchronized (IOUtils.IO_SYNCH) {

      final Path r = IOUtils.canonicalizePath(root);
      final String algo =
          Experiment.nameStringPrepare(algorithm);
      final Path algoPath =
          IOUtils.canonicalizePath(r.resolve(algo));

      final String inst = Experiment.nameStringPrepare(instance);
      final Path instPath =
          IOUtils.canonicalizePath(algoPath.resolve(inst));

      final Path filePath = IOUtils.canonicalizePath(
          instPath.resolve(Experiment.nameStringsMerge(algo,
              inst, RandomUtils.randSeedToString(randSeed))
              + LogFormat.FILE_SUFFIX));

      if (onlyComputePath) {
        // we got the path, and don't need to do anything else
        return filePath;
      }

      if ((done != null) && (!done.add(filePath))) {
        return null;
      }

      // Check if the instance directory exists and, if it does,
      // remember that it does.
      if ((done == null) || (!done.contains(instPath))) {
        try {
          Files.createDirectories(instPath);
          if (done != null) {
            done.add(instPath);
          }
        } catch (final IOException error) {
          throw new IOException(
              (("Could not create instance directory '" + //$NON-NLS-1$
                  instPath) + '\'') + '.',
              error);
        }
      }

// We try to create the log file. If that succeeds, then the no
// such log file existed before, meaning that we have to do the
// run and return the file. If it fails, there are two possible
// reasons: 1) The file already exists. That is OK, because then
// the run is either already in progress or already completed. 2)
// The file does not exist but we could not create it either. Or
// it cannot be determined whether it exists or not and it also
// cannot be created. In this case, we need to throw an I/O
// exception.
      try {
        Files.createFile(filePath);
      } catch (@SuppressWarnings("unused") final FileAlreadyExistsException error) {
        // The file exists: We do not need to do the run.
        return null;
      } catch (final IOException error) {
        // Since FileAlreadyExistsException is optional, we need
        // to double-check if the file really exist.
        if (Files.exists(filePath)) {
          // Ok, it did exist, so we are done here, everything is
          // good.
          return null;
        }
        // OK, if we get here, it was either not possible to
        // determine whether the file exists or not or it does
        // not exist but also could not be created.
        // In this case, we give up and throw an I/O exception
        throw new IOException("Could not create log file '" + //$NON-NLS-1$
            filePath + '\'', error);
      }

      return filePath;
    }
  }

  /**
   * This is a utility method for converting {@code double}
   * values to strings
   *
   * @param d
   *          the {@code double}
   * @return the string
   */
  public static String doubleToStringForName(final double d) {
    if ((d <= Double.NEGATIVE_INFINITY) || //
        (d >= Double.POSITIVE_INFINITY) || //
        Double.isNaN(d)) {
      return Double.toString(d);
    }

    if ((d >= Long.MIN_VALUE) && (d <= Long.MAX_VALUE)) {
      final long l = Math.round(d);
      if (l == d) {
        return Long.toString(l);
      }
    }

    final String s = Double.toString(d);
    if (s.indexOf('E') < 0) {
      return s;
    }

    try {
      final BigDecimal bd = BigDecimal.valueOf(d);
      try {
        final String bis = bd.toBigInteger().toString();
        if (Double.parseDouble(bis) == d) {
          return bis;
        }
      } catch (@SuppressWarnings("unused") final Throwable error1) {
        // ignore
      }
      final String bds = bd.toPlainString();
      if (Double.parseDouble(bds) == d) {
        String best = bds;
        inner: for (int i = bds.length(); (--i) > 0;) {
          try {
            final String test = bds.substring(0, i);
            if (Double.parseDouble(test) == d) {
              best = test;
            }
          } catch (@SuppressWarnings("unused") final Throwable error3) {
            break inner;
          }
        }
        return best;
      }
    } catch (@SuppressWarnings("unused") final Throwable error2) {
      // ignore
    }

    return s;
  }

  /**
   * An experiment stage is a step in a bigger experiment.
   * Experiment stages select algorithms to be executed on
   * selected instances as well as the number of runs to do for a
   * given setup. They also set up the black box processes.
   * Experiment stages must be stateless and all their method
   * calls should idempotent.
   *
   * @param <X>
   *          the search space type
   * @param <Y>
   *          the solution space type
   * @param <P>
   *          the problem type
   * @param <M>
   *          the metaheuristic type
   */
  public interface IExperimentStage<X, Y,
      P extends IObjectiveFunction<Y>,
      M extends IMetaheuristic<X, Y>> {
    /**
     * Get a stream of objective function suppliers to be solved
     * in this state.
     *
     * @return the stream of suppliers, each of which can return
     *         one problem instance
     */
    Stream<Supplier<P>> getProblems();

    /**
     * Get the number of runs to be executed for a given problem.
     *
     * @param problem
     *          the problem
     * @return the number of runs to be executed
     */
    default int getRuns(final P problem) {
      return 21;
    }

    /**
     * Get a stream of algorithm suppliers for a given problem
     *
     * @param problem
     *          the problem
     * @return the stream of suppliers
     */
    @SuppressWarnings("unchecked")
    default Stream<Supplier<M>> getAlgorithms(final P problem) {
      return Stream.of(() -> ((M) (new RandomSampling<>())));
    }

    /**
     * Configure the black box process builder.
     *
     * @param builder
     *          the builder to configure
     */
    default void configureBuilder(
        final BlackBoxProcessBuilder<X, Y> builder) {
      //
    }

    /**
     * Configure the black box process builder for the given
     * problem.
     *
     * @param builder
     *          the builder to configure
     * @param problem
     *          the problem
     */
    default void configureBuilderForProblem(
        final BlackBoxProcessBuilder<X, Y> builder,
        final P problem) {
      //
    }

    /**
     * Configure the black box process builder for the given
     * problem and algorithm
     *
     * @param builder
     *          the builder to configure
     * @param problem
     *          the problem
     * @param algorithm
     *          the algorithm
     */
    default void configureBuilderForProblemAndAlgorithm(
        final BlackBoxProcessBuilder<X, Y> builder,
        final P problem, final M algorithm) {
      //
    }
  }

  /**
   * sleep for a random time interval
   *
   * @param min
   *          the minimum time
   * @param random
   *          the randomizer
   */
  private static void sleep(final long min,
      final ThreadLocalRandom random) {
    Thread.yield();
    if (min > 0L) {
      try {
        Thread.sleep(random.nextLong(min, 10L * min));
      } catch (@SuppressWarnings("unused") final InterruptedException ie) {
        // ignore
      }
      Thread.yield();
    }
  }

  /**
   * Execute an experiment over, potentially, several
   * {@linkplain IExperimentStage stages}.
   * <p>
   * How can we run experiments with optimization algorithms over
   * many runs on different problem instances? We would surely
   * like to execute the runs in parallel. With this procedure,
   * we provide a very simple mechanism to achieve that. The idea
   * is that we simply start several instances of the same
   * program, each of which calls this method with exactly the
   * same parameters. Each run, being a combination of a problem
   * instance (objective function), algorithm setup, and random
   * seed, uniquely fits to one log file in a sub folder of
   * {@code outputDir} will produce output in that one log file
   * (via the {@link aitoa.structure.IBlackBoxProcess}). Before
   * beginning to execute the run, we try to create the log file.
   * This is an atomic operation of the file system which fails
   * if the log file already exists. This way, we can tell
   * whether any parallel process is already doing that run. If
   * so, we just skip it in the current process. If not, we
   * execute it and, after it ends, write its output into the log
   * file. This simple concept of parallelism will also work with
   * shared folders. In other words, you can also use it to
   * execute bigger experiments in a cluster.
   * <p>
   * Experiments can proceed in (what I call)
   * "{@linkplain IExperimentStage stages}". Each stage consists
   * of a set of problem instances, a set of algorithms, and a
   * number of runs for the algorithm-instance combinations. A
   * stage is completed when all runs for it have been finished.
   * This allows for some form of batch processing of
   * sub-experiments. But you can also have stages of increasing
   * numbers of runs. For example, you could first want to have
   * 11 runs for each algorithm on each instance in "stage 1".
   * Then you can set the number of runs to 21 in "stage 2".
   * Since our random seeds are generated deterministically, this
   * will keep the existing runs and add ten more. The advantage
   * is that you will, at some point, already have results for
   * each problem instance and algorithm that you can interpret
   * and analyze. While you are doing that, the experiment
   * executer will tirelessly add more results. If you have a
   * scalable problem, say
   * {@linkplain aitoa.examples.bitstrings.OneMaxObjectiveFunction
   * OneMax}, you could also solve scales up to, say 40 in the
   * first stage and then go to 100 in the second and to 200 in
   * third. Or you could also apply more algorithm setups in the
   * later stages. Or any combination of that. The point and goal
   * of this staging approach is that you can first do some
   * exploration of the results, which would allow you to, e.g.,
   * begin to write a report, while more results are appearing
   * during this time which can then help you to gain more
   * statistical confidence.
   * <p>
   * In order to not waste too many resources, the problem
   * instances and algorithms are instantiated as lazily as
   * possible, via instances of
   * {@link java.util.function.Supplier}s provided by
   * {@link java.util.stream.Stream}s.
   * <p>
   * While this method runs a single experiment in a single
   * thread, you can use
   * {@link #executeExperimentInParallel(Stream, Path)} and its
   * variants to launch multiple threads in parallel. This has
   * the advantage that these threads will share information
   * about which runs have already been conducted, which speeds
   * up the experiment execution and reduces the access to the
   * underlying file system-
   * <p>
   * If you run the experiment with many processes on many
   * computers via a shared folder, then this allows for a large
   * amount of parallelism. It can also lead to network trouble,
   * if too many processes try to check for file creation at
   * once. In order to alleviate this, this function here makes
   * as few calls to the file creation routine as possible. Also,
   * it will sometimes wait for a short time before issuing the
   * next file creation request. This would hopefully reduce the
   * load on the server a little bit. If we still get I/O errors,
   * then we will try to wait a longer time and then restart the
   * experiment (of course, skipping all already performed runs).
   * We will then also increase the waiting time between file
   * operation. The goal is to complete the experiment, no matter
   * what. However, all exceptions different from
   * {@link java.io.IOException} will and should lead to a
   * termination of the experiment.
   * <p>
   * This method will write a short note to the console whenever
   * a new run or a new stage starts. The log lines are prepended
   * by a prefix of: {@code processId:threadId:trial:stage date},
   * where {@code processId} is the process ID in
   * base-{@value java.lang.Character#MAX_RADIX} and
   * {@code threadId} is the ID of the current thread in
   * base-{@value java.lang.Character#MAX_RADIX}. These two
   * numbers should uniquely identify a strand of execution on
   * the current computer. {@code trialId} is the number of the
   * trial of the experiment in decimal. Normally, this will be
   * 1. However, every time we have restart due to an
   * {@link java.io.IOException}, which hopefully never happens,
   * then it will be increased. {@code stage} is the index of the
   * current stage (in decimal) and, finally, {@code date} is
   * current date and time.
   *
   * @param stages
   *          the stages
   * @param outputDir
   *          the output directory
   * @see #executeExperiment(Stream, Path, boolean, boolean,
   *      boolean, boolean)
   * @param <X>
   *          the search space
   * @param <Y>
   *          the solution space
   */
  public static <X, Y> void
      executeExperiment(
          final Stream<Supplier<IExperimentStage<? extends X,
              ? extends Y, ?, ?>>> stages,
          final Path outputDir) {
    Experiment.executeExperiment(stages, outputDir, true, true,
        true, true);
  }

  /**
   * Execute an experiment over, potentially, several
   * {@linkplain IExperimentStage stages}.
   *
   * @param stages
   *          the stages
   * @param outputDir
   *          the output directory
   * @param writeLogInfos
   *          should we print log information?
   * @param waitAfterSkippedRuns
   *          If this is {@code true}, then sometimes the
   *          experiment execution will wait for a very short
   *          time before it continues. This can be useful if we
   *          are working on a shared network drive and may
   *          processes run the same experiment. Then, these
   *          short delays may reduce the stress on the central
   *          server and, thus, may make I/O errors less likely.
   * @param waitAfterWorkWasDone
   *          should we add some short wait time after
   *          significant work was done?
   * @param waitAfterIOError
   *          should we wait for a longer time period if an I/O
   *          error occurs? This also can help to reduce the load
   *          on the server hosting a shared drive and may make
   *          it more likely that we can continue successfully
   *          after the wait
   * @see #executeExperiment(Stream, Path)
   * @param <X>
   *          the search space
   * @param <Y>
   *          the solution space
   */
  public static <X, Y> void executeExperiment(
      final Stream<Supplier<IExperimentStage<? extends X,
          ? extends Y, ?, ?>>> stages,
      final Path outputDir, final boolean writeLogInfos,
      final boolean waitAfterSkippedRuns,
      final boolean waitAfterWorkWasDone,
      final boolean waitAfterIOError) {
    Experiment.executeExperiment(stages, outputDir,
        writeLogInfos, waitAfterSkippedRuns,
        waitAfterWorkWasDone, waitAfterIOError, new FileSet());
  }

  /** perform garbage collection */
  private static void doGc() {
    final Runtime runtime = Runtime.getRuntime();

    for (int i = 10; (--i) >= 0;) {
      long nfm = runtime.freeMemory();
      inner: for (int j = 256; (--j) >= 0;) {
        final long fm = nfm;
        System.gc();
        nfm = runtime.freeMemory();
        if (fm <= nfm) {
          break inner;
        }
      }

      try {
        Thread.sleep(1L);
      } catch (@SuppressWarnings("unused") final InterruptedException ie) {
        //
      }
    }
  }

  /**
   * Execute an experiment over, potentially, several
   * {@linkplain IExperimentStage stages}.
   *
   * @param stages
   *          the stages
   * @param outputDir
   *          the output directory
   * @param writeLogInfos
   *          should we print log information?
   * @param waitAfterSkippedRuns
   *          If this is {@code true}, then sometimes the
   *          experiment execution will wait for a very short
   *          time before it continues. This can be useful if we
   *          are working on a shared network drive and may
   *          processes run the same experiment. Then, these
   *          short delays may reduce the stress on the central
   *          server and, thus, may make I/O errors less likely.
   * @param waitAfterWorkWasDone
   *          should we add some short wait time after
   *          significant work was done?
   * @param waitAfterIOError
   *          should we wait for a longer time period if an I/O
   *          error occurs? This also can help to reduce the load
   *          on the server hosting a shared drive and may make
   *          it more likely that we can continue successfully
   *          after the wait
   * @param done
   *          the hash set for the runs that are done
   * @param <X>
   *          the search space
   * @param <Y>
   *          the solution space
   * @see #executeExperiment(Stream, Path)
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  static <X, Y> void executeExperiment(
      final Stream<Supplier<IExperimentStage<? extends X,
          ? extends Y, ?, ?>>> stages,
      final Path outputDir, final boolean writeLogInfos,
      final boolean waitAfterSkippedRuns,
      final boolean waitAfterWorkWasDone,
      final boolean waitAfterIOError, final FileSet done) {

    try {
      final Supplier<IExperimentStage>[] stageList =
          stages.toArray(i -> new Supplier[i]);
      if ((stageList == null) || (stageList.length <= 0)) {
        throw new IllegalStateException(
            "Stage stream cannot be empty."); //$NON-NLS-1$
      }

      final Path useDir = IOUtils.canonicalizePath(//
          Objects.requireNonNull(outputDir));
      final ThreadLocalRandom random =
          ThreadLocalRandom.current();

      long tryIndex = 0L;

      for (;;) {
// We will try iterating and performing all stages. If an I/O
// error occurs, we just try again and begin from the front.
        ++tryIndex;

        try {
// Catch I/O exceptions: Here the real trial begins.

// We now iterate over the stage suppliers.
          for (int stageIndex = 0; stageIndex < stageList.length;
              stageIndex++) {

// Let's take the supplier. If it is null, then the stage does
// not need to be performed. We already cleared it before.
            final Supplier<IExperimentStage> stageSupplier =
                stageList[stageIndex];
            if (stageSupplier == null) {
              continue;
            }

            final String stageString =
                Integer.toString(1 + stageIndex);
            if (writeLogInfos) {
              ConsoleIO.setIDSuffix(
                  Long.toString(tryIndex) + ':' + stageString);
              ConsoleIO.stdout("Beginning Stage " + stageString); //$NON-NLS-1$
            }

// If the supplier is there, then it must return a non-null
// stage.
            final IExperimentStage stage =
                Objects.requireNonNull(stageSupplier.get());

// We create a new black-box process builder and configure it.
            final BlackBoxProcessBuilder builder =
                new BlackBoxProcessBuilder<>();
            stage.configureBuilder(builder);

// Now we take the problem stream and flatten it.
            final Supplier<IObjectiveFunction>[] problems =
                ((Stream<Supplier>) (stage.getProblems()))
                    .toArray(i -> new Supplier[i]);
            if ((problems == null) || (problems.length <= 0)) {
              throw new IllegalStateException(
                  "Experiment stage " + stageString + //$NON-NLS-1$
                      " must provide at least one problem."); //$NON-NLS-1$
            }

// We want to process the problems in a random order. If we have
// launched multiple processes doing the same experiment in the
// same folder, this will make it more likely that one process
// can work on one problem until it is completed, because
// everyone starts at a different problem. This also means we get
// data from different problems earlier.
            RandomUtils.shuffle(random, problems, 0,
                problems.length);

            for (final Supplier<
                IObjectiveFunction> problemSupplier : problems) {

// instantiate the objective function
              final IObjectiveFunction f =
                  Objects.requireNonNull(problemSupplier.get());

// how many runs should we do?
              final int runs = stage.getRuns(f);
              if (runs <= 0) {
                continue;
              }

// Now it is time to get the list of algorithms.
              final Supplier<IMetaheuristic>[] algorithms =
                  ((Stream<Supplier>) (stage.getAlgorithms(f)))
                      .toArray(i -> new Supplier[i]);
              if ((algorithms == null)
                  || (algorithms.length <= 0)) {
                continue;
              }
// And we will process them again in a random order.
              RandomUtils.shuffle(random, algorithms, 0,
                  algorithms.length);

// Get the problem instance name.
              final String instName =
                  Experiment.nameFromObjectPrepare(f);

// We generate one random seed for each run. All algorithms use
// the same random seeds.
              final long[] seeds =
                  RandomUtils.uniqueRandomSeeds(instName, runs);
              if (seeds.length != runs) {
                throw new IllegalStateException(
                    "Invalid number of seeds: should never happen."); //$NON-NLS-1$
              }

// If we get here, we definitely will do some runs with the
// problem, so we adjust the builder to it.
              stage.configureBuilderForProblem(builder, f);
              builder.setObjectiveFunction(f);

// Now we iterate over the algorithms, in their random order.
              for (final Supplier<
                  IMetaheuristic> algorithmSupplier : algorithms) {
                final IMetaheuristic algorithm = Objects
                    .requireNonNull(algorithmSupplier.get());

// configure the builder for using the algorithm
                stage.configureBuilderForProblemAndAlgorithm(
                    builder, f, algorithm);

// For each algorithm, we will process the random seeds again in
// a random order.
                RandomUtils.shuffle(random, seeds, 0,
                    seeds.length);

// Get the algorithm name.
                final String algoName =
                    algorithm.getSetupName(builder);
                if (algoName.isEmpty()) {
                  throw new IllegalArgumentException(
                      "Name of algorithm cannot be null or empty, but is " //$NON-NLS-1$
                          + algoName);
                }

                for (final long seed : seeds) {

// We create the log file for this run of the current algorithm
// on the current problem with the current seed. We remember a
// hash set of all runs we already did. This allows for different
// experimental stages to raise the number of runs step-by-step
// without us needed to access the file system for runs that we
// already performed in the past.
                  final Path logFile;
                  final boolean runNotLocallyDone;
                  synchronized (done) {
                    final int currentSize = done.size();
                    logFile = Experiment.logFile(useDir,
                        algoName, instName, seed, done, false);
                    runNotLocallyDone =
                        (done.size() > currentSize);
                  }

// If the logFile is null, then we do not need to do the run.
                  if (logFile == null) {
                    if (runNotLocallyDone
                        && waitAfterSkippedRuns) {
// If runNotLocallyDone is true, then the predicate had suggested
// to do the run, but the log file already existed. We found this
// by querying the file system. If we do this very often a shared
// drive, this may annoy the file server. In this case, we may
// want to wait a bit to relief the file system.
                      Experiment.sleep(tryIndex - 1L, random);
                    }
                    // nothing to do here
                    continue;
                  }

// If we get here, we have created the log file which uniquely
// identifies this run. So we can actually execute it.

                  if (writeLogInfos) {
                    ConsoleIO.stdout("Now performing run '"//$NON-NLS-1$
                        + logFile + "'."); //$NON-NLS-1$
                  }

// Set the seed and log path.
                  builder.setRandSeed(seed);
                  builder.setLogPath(logFile);

// Create the process, apply the algorithm, and write the log
// information.
                  try (final IBlackBoxProcess process =
                      builder.get()) {
                    algorithm.solve(process);
                    process.printLogSection(
                        LogFormat.ALGORITHM_SETUP_LOG_SECTION,
                        bw -> algorithm.printSetup((Writer) bw));
                  } catch (final IOException
                      | OutOfMemoryError error) {
                    synchronized (done) {
                      if (error instanceof OutOfMemoryError) {
// The reason for the out-of-memory situation might have been
// that "done" grew too big. Maybe if we just clear it, we
// can prevent the error from occurring again.
                        done.clear();
                      } else {
                        done.remove(logFile);
                      }
                    }
                    Experiment.doGc();

                    if (writeLogInfos) {
                      if (error instanceof OutOfMemoryError) {
                        ConsoleIO.stderr(
                            "We ran out of memory in the experimental run '" //$NON-NLS-1$
                                + logFile
                                + "'. We will try to delete the log file and then continue.", //$NON-NLS-1$
                            error);
                      } else {
                        ConsoleIO.stderr(
                            "We got an I/O error in the experimental run '" //$NON-NLS-1$
                                + logFile
                                + "'. We will try to delete the log file and then continue.", //$NON-NLS-1$
                            error);
                      }
                    }
                    if (waitAfterIOError) {
                      Experiment.sleep(10_000L * tryIndex,
                          random);
                    }

                    Experiment.doGc();

// If we got here, there must have been an error when writing out
// the result. This means the data of the run was lost. But the
// log file had been created empty, so no other process would try
// to repeat the run. We therefore try to delete that log file,
// while will probably fail, but let's try.
                    synchronized (done) {
                      try {
                        Files.delete(logFile);
                      } catch (final Throwable error2) {
                        if (writeLogInfos) {
                          ConsoleIO.stderr(
                              "We got an error when trying to delete file '" //$NON-NLS-1$
                                  + logFile + "'.", //$NON-NLS-1$
                              error2);
                        }
                      } finally {
                        done.remove(logFile);
                      }
                    }

                    Experiment.doGc();

                    throw error;
                  }

                  Thread.yield();
                } // run

                if (waitAfterWorkWasDone) {
                  Experiment.sleep(tryIndex - 1L, random);
                }
              } // end of the algorithm

              if (waitAfterWorkWasDone) {
                Experiment.sleep(tryIndex - 1L, random);
              }
            } // end of the problem

            Thread.yield();
            if (writeLogInfos) {
              ConsoleIO.stdout(
                  "Successfully Finished Stage " + stageString); //$NON-NLS-1$
            }
            stageList[stageIndex] = null; // stage is clear
            Thread.yield();
            System.gc();
            Thread.yield();
          } // end of the stage

          if (writeLogInfos) {
            ConsoleIO.stdout(//
                "Successfully Finished Experiment."); //$NON-NLS-1$
          }
          return; // successful end of the trial
        } catch (final IOException | OutOfMemoryError error) {
          Experiment.doGc();

          if (writeLogInfos) {
            if (error instanceof OutOfMemoryError) {
              // out of memory
              ConsoleIO.stderr(waitAfterIOError
                  ? "Ran out of memory in the experiment, will now wait before retrying." //$NON-NLS-1$
                  : "Ran out of memory in the experiment, will now continue without waiting.", //$NON-NLS-1$
                  error);
            } else {
              // failure in trial due to I/O error
              ConsoleIO.stderr(waitAfterIOError
                  ? "Got an I/O Error in the experiment, will now wait before retrying." //$NON-NLS-1$
                  : "Got an I/O Error in the experiment, will now continue without waiting.", //$NON-NLS-1$
                  error);
            }
          }
          if (waitAfterIOError) {
            Experiment.sleep(10_000L * tryIndex, random);
          }
          Experiment.doGc();
        }
      } // end trial
    } catch (final Throwable error) {
      Experiment.doGc();

      final String message =
          "An unrecoverable error has appeared during the experiment."; //$NON-NLS-1$
      if (writeLogInfos) {
        ConsoleIO.stderr(message, error);
      }
      if (error instanceof Error) {
        throw ((Error) error);
      }
      if (error instanceof RuntimeException) {
        throw ((RuntimeException) error);
      }
      throw new RuntimeException(message, error);
    } finally {
      if (writeLogInfos) {
        ConsoleIO.clearIDSuffix();
      }
    }
  }

  /**
   * Execute an experiment over, potentially, several
   * {@linkplain IExperimentStage stages} and on several
   * {@code cores}.
   *
   * @param stages
   *          the stages
   * @param outputDir
   *          the output directory
   * @param cores
   *          the number of cores to use
   * @see #executeExperiment(Stream, Path)
   */
  public static void executeExperimentInParallel(
      final Stream<
          Supplier<IExperimentStage<?, ?, ?, ?>>> stages,
      final Path outputDir, final int cores) {
    Experiment.executeExperimentInParallel(stages, outputDir,
        cores, true, true, true, true);
  }

  /**
   * Execute an experiment over, potentially, several
   * {@linkplain IExperimentStage stages} and on all available
   * cores.
   * <p>
   * The advantage of this routine over others is that it can
   * synchronize the file existence checks between the different
   * runs at least somewhat. For example, if one thread detects
   * that a run with a given setup has already been performed
   * (i.e., that the corresponding file already exists), the
   * other threads will not test that again. This may
   * significantly reduce the file system operations. Again, with
   * the goal to relief the central, shared file server. This may
   * increase both the performance and the reliability.
   *
   * @param stages
   *          the stages
   * @param outputDir
   *          the output directory
   * @see #executeExperimentInParallel(Stream, Path, int)
   * @see #executeExperiment(Stream, Path)
   */
  public static void executeExperimentInParallel(
      final Stream<
          Supplier<IExperimentStage<?, ?, ?, ?>>> stages,
      final Path outputDir) {
    Experiment.executeExperimentInParallel(stages, outputDir,
        Experiment.defaultThreadCount());
  }

  /**
   * Get the default thread count
   *
   * @return the default thread count
   */
  public static int defaultThreadCount() {
    final int i = Runtime.getRuntime().availableProcessors();
    if (i < 1) {
      throw new IllegalStateException("No processors?"); //$NON-NLS-1$
    }
    return i;
  }

  /**
   * Execute an experiment over, potentially, several
   * {@linkplain IExperimentStage stages} and on several
   * {@code cores}.
   *
   * @param stages
   *          the stages
   * @param outputDir
   *          the output directory
   * @param cores
   *          the number of cores to use
   * @param writeLogInfos
   *          should we print log information?
   * @param waitAfterSkippedRuns
   *          If this is {@code true}, then sometimes the
   *          experiment execution will wait for a very short
   *          time before it continues. This can be useful if we
   *          are working on a shared network drive and may
   *          processes run the same experiment. Then, these
   *          short delays may reduce the stress on the central
   *          server and, thus, may make I/O errors less likely.
   * @param waitAfterWorkWasDone
   *          should we add some short wait time after
   *          significant work was done?
   * @param waitAfterIOError
   *          should we wait for a longer time period if an I/O
   *          error occurs? This also can help to reduce the load
   *          on the server hosting a shared drive and may make
   *          it more likely that we can continue successfully
   *          after the wait
   * @see #executeExperiment(Stream, Path, boolean, boolean,
   *      boolean, boolean)
   * @param <X>
   *          the search space
   * @param <Y>
   *          the solution space
   */
  public static <X, Y> void executeExperimentInParallel(
      final Stream<Supplier<IExperimentStage<? extends X,
          ? extends Y, ?, ?>>> stages,
      final Path outputDir, final int cores,
      final boolean writeLogInfos,
      final boolean waitAfterSkippedRuns,
      final boolean waitAfterWorkWasDone,
      final boolean waitAfterIOError) {

    if (cores <= 0) {
      throw new IllegalArgumentException(
          "Number of cores must be >= 1, but is "//$NON-NLS-1$
              + cores);
    }
    Objects.requireNonNull(outputDir);
    Objects.requireNonNull(stages);

    final List<Supplier<IExperimentStage<? extends X,
        ? extends Y, ?, ?>>> stageList =
            stages.collect(Collectors.toList());
    if (stageList.size() <= 0) {
      throw new IllegalArgumentException(
          "There must be at least one stage.");//$NON-NLS-1$
    }

    final Thread[] threads = new Thread[cores];

    if (writeLogInfos) {
      ConsoleIO.stdout("Now launching "//$NON-NLS-1$
          + cores + " worker threads.");//$NON-NLS-1$
    }

    final FileSet done = new FileSet();

    for (int i = threads.length; (--i) >= 0;) {
      final Thread t = threads[i] = new Thread(
          () -> Experiment.executeExperiment(stageList.stream(),
              outputDir, writeLogInfos, waitAfterSkippedRuns,
              waitAfterWorkWasDone, waitAfterIOError, done),
          "ExperimentWorker_" + (i + 1)); //$NON-NLS-1$
      t.setDaemon(true);
      t.setPriority(Thread.MIN_PRIORITY);
      t.start();
    }

    if (writeLogInfos) {
      ConsoleIO.stdout("Finished launching "//$NON-NLS-1$
          + cores + //
          " worker threads, now waiting for experiment to complete.");//$NON-NLS-1$
    }

    outer: for (;;) {
      for (final Thread t : threads) {
        try {
          t.join();
        } catch (final InterruptedException ie) {
          if (writeLogInfos) {
            ConsoleIO.stderr("Error while waiting for thread "//$NON-NLS-1$
                + t.getName(), ie);
          }
          continue outer;
        }
      }
      break outer;
    }

    if (writeLogInfos) {
      ConsoleIO.stdout("Finished waiting for " + //$NON-NLS-1$
          cores
          + " worker threads, the experiment is complete.");//$NON-NLS-1$
    }
  }

  /** forbidden */
  private Experiment() {
    throw new UnsupportedOperationException();
  }

  /** the set of paths */
  private static final class FileSet
      extends LinkedHashMap<Path, Object> {
    /** the serial version uid */
    private static final long serialVersionUID = 1L;
    /** the key */
    private static final Object KEY = new Object();
    /** the maximum map size */
    private static final int MAX_SIZE = 1024 * 1024;

    /** create */
    FileSet() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    protected boolean removeEldestEntry(final Map.Entry eldest) {
      return this.size() > FileSet.MAX_SIZE;
    }

    /**
     * Add an element
     *
     * @param path
     *          the path
     * @return {@code true} if the path was new, {@code false}
     *         otherwise
     */
    public boolean add(final Path path) {
      return (this.put(path, FileSet.KEY) == null);
    }

    /**
     * Check if the element {@code path} is present
     *
     * @param path
     *          the path
     * @return {@code true} if the element {@code path} is
     *         present, {@code false} if not
     */
    public boolean contains(final Path path) {
      return this.get(path) != null;
    }
  }
}
