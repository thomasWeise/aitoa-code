package aitoa.utils.logs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;

import aitoa.examples.bitstrings.BitStringObjectiveFunction;
import aitoa.examples.bitstrings.JumpObjectiveFunction;
import aitoa.examples.bitstrings.LeadingOnesObjectiveFunction;
import aitoa.examples.bitstrings.OneMaxObjectiveFunction;
import aitoa.examples.bitstrings.PlateauObjectiveFunction;
import aitoa.examples.bitstrings.TrapObjectiveFunction;
import aitoa.examples.bitstrings.TwoMaxObjectiveFunction;
import aitoa.examples.jssp.JSSPInstance;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction;
import aitoa.examples.jssp.JSSPMakespanObjectiveFunction2;
import aitoa.structure.IObjectiveFunction;
import aitoa.structure.ISpace;
import aitoa.structure.LogFormat;
import aitoa.utils.Configuration;
import aitoa.utils.ConsoleIO;
import aitoa.utils.IOUtils;

/**
 * This method allows us to convert the output log files produced
 * by our experimenter to the IOHprofiler format.
 */
public class IOHProfiler {

  /** the dat file suffix */
  private static final String DAT_FILE_SUFFIX = ".dat";//$NON-NLS-1$
  /** the meta file suffix */
  private static final String META_FILE_SUFFIX = ".info";//$NON-NLS-1$

  /** the data folder prefix */
  private static final String DATA_FOLDER_PREFIX = "data_f";//$NON-NLS-1$
  /** the file name prefix */
  private static final String FILE_NAME_PREFIX = "IOHprofiler_f";//$NON-NLS-1$

  /** the file name mid string DIM */
  private static final String FILE_MID_DIMENSION = "_DIM";//$NON-NLS-1$

  /** the file name mid string instance */
  private static final String FILE_MID_INSTANCE = "_i";//$NON-NLS-1$

  /** the 1st part of the meta line */
  private static final String META_1_FID = "funcId = ";//$NON-NLS-1$

  /** the 2nd part of the meta line */
  private static final String META_2_DID = ", DIM = ";//$NON-NLS-1$

  /** the 3rd part of the meta line */
  private static final String META_3_AID = ", algId = '";//$NON-NLS-1$

  /** the 4rth part of the meta line */
  private static final char META_4_END = '\'';

  /** the meta data comment line */
  private static final char META_COMMENT_LINE = '%';
  /** the meta data folder separator */
  private static final char META_FOLDER_SEPARATOR = '/';
  /** the meta data separator */
  private static final char[] META_SEPARATOR = { ',', ' ' };

  /** the separator between instance and FEs */
  private static final char META_BETWEEN_INST_AND_FES = ':';
  /** the separator between FEs and F */
  private static final char META_BETWEEN_FES_AND_F = '|';
  /** the raw data separator */
  private static final char RAW_SEPARATOR = ' ';

  /** the header of the dat file */
  private static final String DAT_HEADER =
      "\"function evaluation\"" + IOHProfiler.RAW_SEPARATOR + //$NON-NLS-1$
          "\"best-so-far f(x)\"";//$NON-NLS-1$

  /**
   * If possible, load an objective function for the given setup
   *
   * @param instance
   *          the instance ID
   * @param setup
   *          the setup ID
   * @return the objective function, or {@code null} if none was
   *         found
   */
  @SuppressWarnings("unused")
  public static final IObjectiveFunction<?> getObjectiveFunction(
      final String instance, final SetupData setup) {
    final String functionClass = setup.setup
        .get(LogFormat.classKey(LogFormat.OBJECTIVE_FUNCTION));
    if (functionClass == null) {
      return null;
    }
    try {
      final Class<?> clazz = Class.forName(functionClass);
      if ((clazz != null) && (IObjectiveFunction.class
          .isAssignableFrom(clazz))) {
        try {
          final Constructor<?> constr =
              clazz.getConstructor(String.class);
          return IObjectiveFunction.class.cast(Objects
              .requireNonNull(constr.newInstance(instance)));
        } catch (final NoSuchMethodException ignore2) {
          final Constructor<?> constr = clazz.getConstructor();
          return IObjectiveFunction.class.cast(
              Objects.requireNonNull(constr.newInstance()));
        }
      }
    } catch (final Throwable ignore) {
      // ignore
    }
    return null;
  }

  /**
   * If possible, load an space function for the given setup.
   * This method first tries to locate
   *
   * @param instance
   *          the instance ID
   * @param setup
   *          the setup ID
   * @param checkSolutionSpace
   *          should we check for the solution space?
   * @param checkSearchSpace
   *          should we check for the search space?
   * @param checkViaObjectiveFunction
   *          should we attempt to load the space via the
   *          objective function?
   * @return the objective function, or {@code null} if none was
   *         found
   */
  @SuppressWarnings("unused")
  public static final ISpace<?> getSpace(final String instance,
      final SetupData setup, final boolean checkSolutionSpace,
      final boolean checkSearchSpace,
      final boolean checkViaObjectiveFunction) {

    checkSpaces: {
      final String[] spaces;
      if (checkSolutionSpace) {
        if (checkSearchSpace) {
          spaces = new String[] { LogFormat.SOLUTION_SPACE,
              LogFormat.SEARCH_SPACE };
        } else {
          spaces = new String[] { LogFormat.SOLUTION_SPACE };
        }
      } else {
        if (checkSearchSpace) {
          spaces = new String[] { LogFormat.SEARCH_SPACE };
        } else {
          if (!checkViaObjectiveFunction) {
            throw new IllegalArgumentException(
                "Either objective function or search or solution space must be checked."); //$NON-NLS-1$
          }
          break checkSpaces;
        }
      }

      for (final String spaceId : spaces) {
        final String spaceClass =
            setup.setup.get(LogFormat.classKey(spaceId));
        if (spaceClass == null) {
          continue;
        }

        try {
          final Class<?> clazz = Class.forName(spaceClass);
          if ((clazz != null)
              && (ISpace.class.isAssignableFrom(clazz))) {
            try {
              final Constructor<?> constr =
                  clazz.getConstructor(String.class);
              try {
                return ISpace.class.cast(Objects.requireNonNull(
                    constr.newInstance(instance)));
              } catch (IllegalArgumentException
                  | NullPointerException
                  | IllegalStateException ignore) {
                return ISpace.class
                    .cast(Objects.requireNonNull(constr
                        .newInstance(setup.setup.get(spaceId))));
              }
            } catch (final NoSuchMethodException ignore2) {
              final Constructor<?> constr =
                  clazz.getConstructor();
              return ISpace.class.cast(
                  Objects.requireNonNull(constr.newInstance()));
            }
          }
        } catch (final Throwable ignore) {
          // ignore
        }
      }
    }

    if (checkViaObjectiveFunction) {
      final IObjectiveFunction<?> f =
          IOHProfiler.getObjectiveFunction(instance, setup);
      if (f instanceof BitStringObjectiveFunction) {
        return Objects.requireNonNull(
            ((BitStringObjectiveFunction) f).createSpace());
      }
    }

    return null;
  }

  /** the one-dimensional name prefixes */
  private static final String[] PREFIXES_1 =
      { OneMaxObjectiveFunction.NAME_PREFIX,
          LeadingOnesObjectiveFunction.NAME_PREFIX,
          TwoMaxObjectiveFunction.NAME_PREFIX,
          TrapObjectiveFunction.NAME_PREFIX };
  /** the two-dimensional name prefixes */
  private static final String[] PREFIXES_2 =
      { JumpObjectiveFunction.NAME_PREFIX,
          PlateauObjectiveFunction.NAME_PREFIX };

  /**
   * The default method to obtain the function name for a given
   * instance and setup
   *
   * @param instance
   *          the instance ID component of the log file folder
   * @param setup
   *          the setup of the run
   * @return the function name and dimension
   */
  public static final FunctionMetaData
      defaultGetFunctionMetaData(final String instance,
          final SetupData setup) {
    Objects.requireNonNull(instance);
    Objects.requireNonNull(setup);

    // first, deal with the simple cases
    for (final String p : IOHProfiler.PREFIXES_1) {
      if (instance.startsWith(p)) {
        try {
          return new FunctionMetaData(
              p.substring(0, p.length() - 1),
              Integer.parseInt(instance.substring(p.length())));
        } catch (final NumberFormatException nfe) {
          throw new IllegalArgumentException(instance, nfe);
        }
      }
    }

    for (final String p : IOHProfiler.PREFIXES_2) {
      if (instance.startsWith(p)) {
        final int j = instance.lastIndexOf('_');
        if (j <= p.length()) {
          throw new IllegalArgumentException(instance);
        }
        final String k = instance.substring(j + 1);
        final String n = instance.substring(p.length(), j);
        try {
          Integer.parseInt(k);
          return new FunctionMetaData(p + k,
              Integer.parseInt(n));
        } catch (final NumberFormatException nfe) {
          throw new IllegalArgumentException(instance, nfe);
        }
      }
    }

    // Ok, it won't be any of the simple, default bit string
    // problems

    long instanceId = 1L;
    String funcId = instance;

    // Maybe the instance name follows the "-instance" paradigm?
    final int lastDash = instance.lastIndexOf('-');
    if ((lastDash > 0) && (lastDash < (instance.length() - 1))) {
      try {
        final long id =
            Long.parseLong(instance.substring(lastDash + 1));
        if (id > 0L) {
          instanceId = id;
          funcId = instance.substring(0, lastDash);
        }
      } catch (@SuppressWarnings("unused") final NumberFormatException nfe) {
        // ignore
      }
    }
    // We potentially have a valid instance Id now.

    final IObjectiveFunction<?> f =
        IOHProfiler.getObjectiveFunction(instance, setup);

    if (f != null) {
      // is it a JSSP instance?
      JSSPInstance inst = null;
      if (f instanceof JSSPMakespanObjectiveFunction) {
        inst = ((JSSPMakespanObjectiveFunction) f).instance;
      } else {
        if (f instanceof JSSPMakespanObjectiveFunction2) {
          inst = ((JSSPMakespanObjectiveFunction2) f).instance;
        }
      }
      if (inst != null) {
        return new FunctionMetaData(inst.toString(),
            inst.getScale(), instanceId);
      }
    }

    // So we cannot really determine the problem name and are
    // stuck with the instance Id.
    // But maybe we can get the problem scale via the search
    // space?
    final ISpace<?> space =
        IOHProfiler.getSpace(instance, setup, true, true, true);
    if (space != null) {
      return new FunctionMetaData(funcId, space, instanceId);
    }

    // No dice whatsoever. Let's return the default setting: Just
    // the instance name as is.
    return new FunctionMetaData(funcId, 1L, instanceId);
  }

  /**
   * Convert our format to the IOHprofiler format.
   *
   * @param inputFolder
   *          the input folder
   * @param outputFolder
   *          the output folder preserved?
   * @param getFunctionMetaData
   *          the function transforming information to function
   *          Ids, dimensions, and instance ids {@code null} for
   *          default mapping
   * @param logProgressToConsole
   *          should logging information be printed?
   * @return a map containing the meta-data files as keys and the
   *         corresponding raw data file list as value
   * @throws IOException
   *           if i/o fails
   */
  public static final Path convertToIOHprofilerData(
      final Path inputFolder, final Path outputFolder,
      final BiFunction<String, SetupData,
          FunctionMetaData> getFunctionMetaData,
      final boolean logProgressToConsole) throws IOException {

    final Path in = IOUtils.requireDirectory(inputFolder);

    final Path out =
        IOUtils.requireDirectory(outputFolder, true);

    if (logProgressToConsole) {
      ConsoleIO.stdout(//
          "Now beginning to create IOHprofiler data in folder '" //$NON-NLS-1$
              + out + "'.");//$NON-NLS-1$
    }

    final Path[] algorithms = IOUtils.subDirectories(in);
    if (logProgressToConsole) {
      ConsoleIO.stdout("Found " + algorithms.length + //$NON-NLS-1$
          " potential algorithm directories.");//$NON-NLS-1$
    }

    final ArrayList<__Point> lines = new ArrayList<>();
    final SetupData[] setup = new SetupData[1];
    final long[] lastFE = { -1L };
    final HashMap<String, FunctionMetaData> functionMetaDatas =
        new HashMap<>();

    final HashMap<String,
        HashMap<Long,
            HashMap<Long, ArrayList<__Point[]>>>> traces =
                new HashMap<>();

    final BiFunction<String, SetupData,
        FunctionMetaData> _getFunctionMetaData =
            ((getFunctionMetaData != null) ? getFunctionMetaData
                : IOHProfiler::defaultGetFunctionMetaData);

    for (final Path algorithm : algorithms) {
      final String algoName =
          algorithm.getFileName().toString().trim();
      if (logProgressToConsole) {
        ConsoleIO.stdout(//
            "Now processing algorithm '" + algoName + //$NON-NLS-1$
                "'.");//$NON-NLS-1$
      }

      final Path[] instances = IOUtils.subDirectories(algorithm);
      for (final Path instance : instances) {
        final String instName =
            instance.getFileName().toString().trim();
        if (logProgressToConsole) {
          ConsoleIO.stdout(//
              "Now processing instance '" + instName + //$NON-NLS-1$
                  "' for algorithm '" + algoName //$NON-NLS-1$
                  + "'.");//$NON-NLS-1$
        }

        for (final Path file : IOUtils
            .pathArray(IOUtils.filesStream(instance) //
                .filter((ff) -> ff.getFileName().toString()
                    .endsWith(".txt"))//$NON-NLS-1$
            )) {

          lastFE[0] = -1L;
          LogParser.parseLogFile(file, (l) -> {
            final long curFE = l.fe_max;
            if (curFE > lastFE[0]) {
              lines.add(new __Point(curFE, l.f_min));
              lastFE[0] = curFE;
            }
          }, (s) -> setup[0] = Objects.requireNonNull(s));

          if (setup[0] == null) {
            throw new IllegalStateException(
                "No setup for file '" + file + //$NON-NLS-1$
                    "'.");//$NON-NLS-1$
          }
          if (lines.isEmpty()) {
            throw new IllegalStateException(
                "No log lines in file '" + file + //$NON-NLS-1$
                    "'.");//$NON-NLS-1$
          }

          final FunctionMetaData functionMetaData =
              functionMetaDatas.computeIfAbsent(instName,
                  (n) -> Objects.requireNonNull(
                      _getFunctionMetaData.apply(n, setup[0])));
          final String functionId =
              Objects.requireNonNull(functionMetaData.id);
          final Long instanceId =
              Long.valueOf(functionMetaData.instance);
          final Long functionDim =
              Long.valueOf(functionMetaData.dimension);

          // OK, we got valid function and instance IDs as well
          // as valid dimensions

          final HashMap<Long,
              HashMap<Long, ArrayList<__Point[]>>> insts =
                  traces.computeIfAbsent(functionId,
                      i -> new HashMap<>());

          final HashMap<Long, ArrayList<__Point[]>> dims = insts
              .computeIfAbsent(instanceId, i -> new HashMap<>());

          final ArrayList<__Point[]> dim = dims.computeIfAbsent(
              functionDim, i -> new ArrayList<>());
          dim.add(lines.toArray(new __Point[lines.size()]));

          lines.clear();
          setup[0] = null;
        } // end log file
      } // end instance

      // all the data has been loaded, now we can write the
      // output

      if (logProgressToConsole) {
        ConsoleIO.stdout(//
            "Finished loading data for algorithm '" + //$NON-NLS-1$
                algoName + "', found " + //$NON-NLS-1$
                traces.size() + " functions.");//$NON-NLS-1$
        if (traces.isEmpty()) {
          continue;
        }
      }

      final Path algoDir =
          IOUtils.requireDirectory(out.resolve(algoName), true);

      if (logProgressToConsole) {
        ConsoleIO.stdout(//
            "Now writing IOHprofiler data for algorithm '" + //$NON-NLS-1$
                algoName + "', to folder '" + //$NON-NLS-1$
                algoDir + "'.");//$NON-NLS-1$
      }

      final String[] funcs =
          traces.keySet().toArray(new String[traces.size()]);
      Arrays.sort(funcs);

      for (final String functionId : funcs) {
        // iterate over the functions
        final String dataFolderName =
            IOHProfiler.DATA_FOLDER_PREFIX + functionId;
        final Path dataFolder = IOUtils.requireDirectory(
            algoDir.resolve(dataFolderName), true);

        final HashMap<Long,
            HashMap<Long, ArrayList<__Point[]>>> insts =
                Objects.requireNonNull(traces
                    .remove(Objects.requireNonNull(functionId)));
        final Long[] insts2 =
            insts.keySet().toArray(new Long[insts.size()]);
        if (insts2.length <= 0) {
          throw new IllegalStateException("instances empty?"); //$NON-NLS-1$
        }
        Arrays.sort(insts2);

        for (final Long instanceId : insts2) {
          final String instanceIdStr = instanceId.toString();
          final String metaFileName =
              IOHProfiler.FILE_NAME_PREFIX + functionId
                  + IOHProfiler.FILE_MID_INSTANCE + instanceIdStr
                  + IOHProfiler.META_FILE_SUFFIX;
          final Path metaFile = IOUtils
              .canonicalizePath(algoDir.resolve(metaFileName));

          try (final BufferedWriter metaData =
              Files.newBufferedWriter(metaFile)) {

            final HashMap<Long, ArrayList<__Point[]>> dims =
                Objects.requireNonNull(insts.remove(instanceId));
            final Long[] dims2 =
                dims.keySet().toArray(new Long[dims.size()]);
            if (dims2.length <= 0) {
              throw new IllegalStateException(
                  "dimensions empty?"); //$NON-NLS-1$
            }
            Arrays.sort(dims2);

            for (final Long functionDim : dims2) {
              final String functionDimStr =
                  functionDim.toString();
              final String datFileName =
                  IOHProfiler.FILE_NAME_PREFIX + functionId
                      + IOHProfiler.FILE_MID_DIMENSION
                      + functionDimStr
                      + IOHProfiler.FILE_MID_INSTANCE
                      + instanceIdStr
                      + IOHProfiler.DAT_FILE_SUFFIX;
              final Path datFile = IOUtils.canonicalizePath(
                  dataFolder.resolve(datFileName));

              metaData.write(IOHProfiler.META_1_FID);
              metaData.write(functionId);
              metaData.write(IOHProfiler.META_2_DID);
              metaData.write(functionDimStr);
              metaData.write(IOHProfiler.META_3_AID);
              metaData.write(algoName);
              metaData.write(IOHProfiler.META_4_END);
              metaData.newLine();
              metaData.write(IOHProfiler.META_COMMENT_LINE);
              metaData.newLine();
              metaData.write(dataFolderName);
              metaData.write(IOHProfiler.META_FOLDER_SEPARATOR);
              metaData.write(datFileName);

              final ArrayList<__Point[]> allPoints = Objects
                  .requireNonNull(dims.remove(functionDim));
              if (allPoints.isEmpty()) {
                throw new IllegalStateException(
                    "log points empty?"); //$NON-NLS-1$
              }

              // sort the data a bit for aesthetic reasons
              final __Point[][] allPoints2 = allPoints
                  .toArray(new __Point[allPoints.size()][]);
              allPoints.clear();
              Arrays.sort(allPoints2, (p1, p2) -> {
                final int p1l = p1.length - 1;
                final int p1ll =
                    (p1l > 0) ? ((p1[p1l - 1].f <= p1[p1l].f)
                        ? (p1l - 1) : p1l) : p1l;
                final int p2l = p2.length - 1;
                final int p2ll =
                    (p2l > 0) ? ((p2[p2l - 1].f <= p2[p2l].f)
                        ? (p2l - 1) : p2l) : p2l;

                int r = Double.compare(p1[p1ll].f, p2[p2ll].f);
                if (r != 0) {
                  return r;
                }
                r = Long.compare(p1[p1ll].fes, p2[p2ll].fes);
                if (r != 0) {
                  return r;
                }
                r = Integer.compare(p1ll, p2ll);
                if (r != 0) {
                  return r;
                }

                r = Double.compare(p1[p1l].f, p2[p2l].f);
                if (r != 0) {
                  return r;
                }
                r = Long.compare(p1[p1l].fes, p2[p2l].fes);
                if (r != 0) {
                  return r;
                }
                return Integer.compare(p1l, p2l);
              });

              try (final BufferedWriter rawData =
                  Files.newBufferedWriter(datFile)) {
                for (final __Point[] points : allPoints2) {

                  rawData.write(IOHProfiler.DAT_HEADER);
                  rawData.newLine();
                  for (final __Point p : points) {
                    rawData.write(Long.toString(p.fes));
                    rawData.write(IOHProfiler.RAW_SEPARATOR);
                    rawData.write(
                        LogFormat.doubleToStringForLog(p.f));
                    rawData.newLine();
                  }

                  metaData.write(IOHProfiler.META_SEPARATOR);
                  metaData.write(instanceIdStr);
                  metaData.write(
                      IOHProfiler.META_BETWEEN_INST_AND_FES);
                  final __Point e = Objects
                      .requireNonNull(points[points.length - 1]);
                  metaData.write(Long.toString(e.fes));
                  metaData
                      .write(IOHProfiler.META_BETWEEN_FES_AND_F);
                  metaData.write(
                      LogFormat.doubleToStringForLog(e.f));
                }
              } // end raw writer
              metaData.newLine();
            } // end dimensions

          } // end meta-data writer

        } // end instances
      } // end functions

      traces.clear();
    } // end algorithm

    if (logProgressToConsole) {
      ConsoleIO.stdout(//
          "Finished creating IOHprofiler data in folder '" + //$NON-NLS-1$
              out + "'.");//$NON-NLS-1$
    }

    return out;
  }

  /** the log point */
  private static final class __Point {
    /** the FEs */
    final long fes;
    /** the objective value */
    final double f;

    /**
     * create the point
     *
     * @param _fes
     *          the fes
     * @param _f
     *          the f
     */
    __Point(final long _fes, final double _f) {
      super();
      this.fes = _fes;
      this.f = _f;
    }
  }

  /** a holder for ID and dimension */
  public static final class FunctionMetaData
      implements Comparable<FunctionMetaData> {
    /** the id */
    public final String id;
    /** the dimension */
    public final long dimension;
    /** the instance id */
    public final long instance;

    /**
     * Create an Id an dimension
     *
     * @param _id
     *          the function id
     * @param _dimension
     *          the function dimension
     * @param _instance
     *          the instance
     */
    public FunctionMetaData(final String _id,
        final long _dimension, final long _instance) {
      super();
      this.id = _id.trim().replace('_', '-').replaceAll("--", //$NON-NLS-1$
          "-");//$NON-NLS-1$
      if (this.id.isEmpty()) {
        throw new IllegalArgumentException(
            "Function ID cannot be empty or blank, but '" //$NON-NLS-1$
                + _id + "' with dimension "//$NON-NLS-1$
                + _dimension + " and instance "//$NON-NLS-1$
                + _instance + " is.");//$NON-NLS-1$
      }
      if (_dimension <= 0L) {
        throw new IllegalArgumentException(//
            "Function dimension must not be "//$NON-NLS-1$
                + _dimension + " but is for '" + //$NON-NLS-1$
                _id + " and instance "//$NON-NLS-1$
                + _instance + "'.");//$NON-NLS-1$
      }
      this.dimension = _dimension;
      if (_instance <= 0L) {
        throw new IllegalArgumentException(//
            "Instance Id must not be "//$NON-NLS-1$
                + _instance + " but is for '" + //$NON-NLS-1$
                _id + " and dimension "//$NON-NLS-1$
                + _dimension + "'.");//$NON-NLS-1$
      }
      this.instance = _instance;
    }

    /**
     * Create an Id an dimension
     *
     * @param _id
     *          the function id
     * @param _dimension
     *          the function dimension
     */
    public FunctionMetaData(final String _id,
        final long _dimension) {
      this(_id, _dimension, 1L);
    }

    /**
     * Create an Id an dimension
     *
     * @param _id
     *          the function id
     */
    public FunctionMetaData(final String _id) {
      this(_id, 1L);
    }

    /**
     * Create an Id an dimension
     *
     * @param _id
     *          the function id
     * @param _dimension
     *          the function dimension
     * @param _instance
     *          the instance
     */
    public FunctionMetaData(final String _id,
        final double _dimension, final long _instance) {
      this(_id,
          FunctionMetaData.__conv(_id, _dimension, _instance),
          _instance);
    }

    /**
     * Create an Id an dimension
     *
     * @param _id
     *          the function id
     * @param _dimension
     *          the function dimension
     */
    public FunctionMetaData(final String _id,
        final double _dimension) {
      this(_id, _dimension, 1L);
    }

    /**
     * Create an Id an dimension
     *
     * @param _id
     *          the function id
     * @param _space
     *          the space from which we take the dimension/scale
     * @param _instance
     *          the instance
     */
    public FunctionMetaData(final String _id,
        final ISpace<?> _space, final long _instance) {
      this(_id, _space.getScale(), _instance);
    }

    /**
     * Create an Id an dimension
     *
     * @param _id
     *          the function id
     * @param _space
     *          the space from which we take the dimension/scale
     */
    public FunctionMetaData(final String _id,
        final ISpace<?> _space) {
      this(_id, _space, 1L);
    }

    /**
     * do the dimension conversation
     *
     * @param _id
     *          the id
     * @param _dimension
     *          the dimension as {@code double}
     * @param _instance
     *          the instance
     * @return the dimension as {@code long}
     */
    private static final long __conv(final String _id,
        final double _dimension, final long _instance) {
      if (Double.isFinite(_dimension) && (_dimension > 0d)
          && (_dimension < Long.MAX_VALUE)) {
        final long l1 = Math.round(_dimension);
        return ((l1 == 0L) ? 1L : l1);
      }
      throw new IllegalArgumentException(//
          "Function dimension must not be "//$NON-NLS-1$
              + _dimension + " but is for '" + //$NON-NLS-1$
              _id + " and instance " + //$NON-NLS-1$
              _instance + "'.");//$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    public final int hashCode() {
      return (((this.id.hashCode() * 31)
          + Long.hashCode(this.dimension)) * 31)
          + Long.hashCode(this.instance);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object o) {
      if (o instanceof FunctionMetaData) {
        final FunctionMetaData f = ((FunctionMetaData) o);
        return ((this.id.equals(f.id))
            && (this.dimension == f.dimension)
            && (this.instance == f.instance));
      }
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final FunctionMetaData o) {
      int r = this.id.compareTo(o.id);
      if (r != 0) {
        return r;
      }
      r = Long.compare(this.dimension, o.dimension);
      if (r != 0) {
        return r;
      }
      return Long.compare(this.instance, o.instance);
    }
  }

  /**
   * print the arguments
   *
   * @param s
   *          the print stream
   */
  static final void _printArgs(final PrintStream s) {
    _CommandLineArgs._printSourceDir(s);
    _CommandLineArgs._printDestDir(s);
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    ConsoleIO.stdout((s) -> {
      s.println("Welcome to the IOHprofiler Data Converter"); //$NON-NLS-1$
      s.println("The command line arguments are as follows: ");//$NON-NLS-1$
      EndResults._printArgs(s);
      s.println(
          "If you do not set the arguments, defaults will be used.");//$NON-NLS-1$
    });

    Configuration.putCommandLine(args);

    final Path in = _CommandLineArgs._getSourceDir();
    final Path out = _CommandLineArgs._getDestDir();

    Configuration.print();

    try {
      IOHProfiler.convertToIOHprofilerData(in, out, null, true);
    } catch (final Throwable error) {
      ConsoleIO.stderr(
          "An error occured while converting the data.", //$NON-NLS-1$
          error);
    }
  }
}
