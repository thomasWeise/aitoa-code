package aitoa.utils.logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import aitoa.structure.LogFormat;

/**
 * This class allows for efficient parsing of the log files
 * produced by our experimenter.
 */
public final class LogParser {

  /** the interface for consumers of log points */
  public static interface ILogPointConsumer {
    /**
     * accept a log point
     *
     * @param fe_last_improvement
     *          the FE where the last improvement took place
     *          (will be this one, if {@code is_improvement} is
     *          {@code true})
     * @param fe_max
     *          the total consumed function evaluations
     * @param time_last_improvement
     *          the time where the last improvement took place
     *          (will be this one, if {@code is_improvement} is
     *          {@code true})
     * @param time_max
     *          the total consumed runtime
     * @param improvements
     *          the total number of improvements (including this
     *          one, if {@code is_improvement} is {@code true})
     * @param f_min
     *          the best-so-far objective value
     * @param is_improvement
     *          {@code true} if this log point has a better
     *          {@code f_min} value than the one before,
     *          {@code false} otherwise
     */
    public abstract void accept(final long fe_last_improvement,
        final long fe_max, final long time_last_improvement,
        final long time_max, final long improvements,
        final double f_min, final boolean is_improvement);
  }

  /** consume the setup items from the log file */
  public static interface ISetupConsumer {
    /**
     * this method accepts a setup point from the log file
     *
     * @param key
     *          the setup key
     * @param value
     *          the setup value
     * @param isStandard
     *          {@code true} if and only if this is a standard
     *          parameter that will also be passed to
     *          {@link #acceptStandard(String, long, long, long, double)},
     *          {@code false} if it is a different parameter that
     *          will not be passed to
     *          {@link #acceptStandard(String, long, long, long, double)}
     */
    public default void accept(final String key,
        final String value, final boolean isStandard) {
      //
    }

    /**
     * Accept the standard parameters, which define the random
     * seed and the termination criterion
     *
     * @param randSeedString
     *          the random seed as {@link String}
     * @param randSeedLong
     *          the random seed as {@code long}
     * @param budgetFEs
     *          the budget of function evaluations
     * @param budgetTime
     *          the budget of runtime (in milliseconds)
     * @param goalF
     *          the goal objective value
     */
    public default void acceptStandard(//
        final String randSeedString, //
        final long randSeedLong, //
        final long budgetFEs, //
        final long budgetTime, //
        final double goalF) {
      //
    }
  }

  /** the set of standard setup keys */
  private static final List<String> STANDARD_SETUP_KEYS =
      Arrays.asList(LogFormat.MAX_FES, LogFormat.MAX_TIME,
          LogFormat.GOAL_F, LogFormat.RANDOM_SEED);

  /** the state keys */
  private static final List<String> STATE_KEYS =
      Arrays.asList(LogFormat.CONSUMED_FES,
          LogFormat.LAST_IMPROVEMENT_FE, LogFormat.CONSUMED_TIME,
          LogFormat.LAST_IMPROVEMENT_TIME, LogFormat.BEST_F);

  /**
   * check if a given time value exceeds a budget limit
   *
   * @param time
   *          the time
   * @param budgetTime
   *          the time budget
   */
  static final void _checkTime(final long time,
      final long budgetTime) {
    if (time <= budgetTime) {
      return;
    }
    long t = Math.max(budgetTime + 10000L,
        Math.max((budgetTime * 11L) / 10L,
            Math.round(budgetTime * 1.1d)));
    if (time <= t) {
      return;
    }
    t = Math.max(t, t + 5000L);
    if (time <= t) {
      return;
    }
    long c = Math.min(time, Math.round(0.95d * time));
    if (c <= t) {
      return;
    }
    c = Math.max(0, Math.min(c, c - 10000L));
    throw new IllegalStateException(//
        "The consumed time " + time + //$NON-NLS-1$
            "ms exceeds the budget of " + //$NON-NLS-1$
            budgetTime + "ms, even if we extend it to " + //$NON-NLS-1$
            t + "ms and reduce the consumed time to " //$NON-NLS-1$
            + c
            + "ms to cater for delays, scheduling, and graceful termination.");//$NON-NLS-1$
  }

  /**
   * This function parses a given log file and ensures that the
   * data therein meets all reasonable criteria for monotonicity
   * and presence. It passes all information to the provided
   * consumers.
   *
   * @param file
   *          the file
   * @param logConsumer
   *          the log point consumer, can be {@code null}
   * @param setupConsumer
   *          the setup consumer, can be {@code null}
   * @throws IOException
   *           if i/o fails or if any data in the file violates
   *           the sanity checks
   */
  @SuppressWarnings("null")
  public static final void parseLogFile(final Path file,
      final ILogPointConsumer logConsumer,
      final ISetupConsumer setupConsumer) throws IOException {

    final String name = file.getFileName().toString();
    if (!name.endsWith(LogFormat.FILE_SUFFIX)) {
      throw new IllegalArgumentException(//
          "Invalid file name '" + file //$NON-NLS-1$
              + "', must end with '" + //$NON-NLS-1$
              LogFormat.FILE_SUFFIX + "'.");//$NON-NLS-1$
    }

    try (final BufferedReader in =
        Files.newBufferedReader(file)) {

      // statistics
      long fe_max = 0L;
      long fe_last_improvement = 0L;
      long time_max = 0L;
      long time_last_improvement = 0L;
      double f_min = Double.POSITIVE_INFINITY;
      long improvements = 0L;

      long budgetFEs = Long.MAX_VALUE;
      long budgetTime = Long.MAX_VALUE;
      String randSeedString = null;
      long randSeedLong = 0L;
      double goalF = Double.NEGATIVE_INFINITY;

      // states: 0=before, 1=in, 2=after
      int state_log = 0;
      int state_setup = 0;
      int state_state = 0;
      HashSet<String> setupKeys = null;
      HashSet<String> stateKeys = null;

      boolean invokeLogAfterState = false;

      String line2 = null;
      int lineIndex = 0;
      while ((line2 = in.readLine()) != null) {
        ++lineIndex;
        String line = line2.trim();
        if (line.length() <= 0) {
          continue;
        }

        try {

          // we enter a comment?
          if (line.charAt(0) == LogFormat.COMMENT_CHAR) {
            line = line.substring(1).trim();
            if (line.length() <= 0) {
              continue;
            }

            if (LogFormat.BEGIN_LOG.equals(line)) {
              switch (state_log) {
                case 0: {
                  if (state_setup == 1) {
                    throw new IllegalStateException(
                        "Cannot begin log section inside setup section.");//$NON-NLS-1$
                  }
                  if (state_state == 1) {
                    throw new IllegalStateException(
                        "Cannot begin log section inside state section??");//$NON-NLS-1$
                  }
                  if (state_state != 0) {
                    throw new IllegalStateException(
                        "Log section must come before state section??");//$NON-NLS-1$
                  }
                  state_log = 1;
                  continue;
                }
                case 1: {
                  throw new IllegalStateException(
                      "Log section cannot begin inside log section.");//$NON-NLS-1$
                }
                default: {
                  throw new IllegalStateException(
                      "Log section can only occur once.");//$NON-NLS-1$
                }
              }
            }

            if (LogFormat.END_OF_LOG.equals(line)) {
              switch (state_log) {
                case 0: {
                  throw new IllegalStateException(
                      "Log section can only end after log section begins.");//$NON-NLS-1$
                }
                case 1: {
                  if (state_setup == 1) {
                    throw new IllegalStateException(
                        "Log section cannot end inside setup section??");//$NON-NLS-1$
                  }
                  if (state_state == 1) {
                    throw new IllegalStateException(
                        "Log section cannot end inside state section??");//$NON-NLS-1$
                  }
                  if (state_setup != 0) {
                    throw new IllegalStateException(
                        "Log section must end before state section??");//$NON-NLS-1$
                  }
                  state_log = 2;
                  continue;
                }
                default: {
                  throw new IllegalStateException(
                      "Log section can only end once.");//$NON-NLS-1$
                }
              }
            }

            if (LogFormat.BEGIN_SETUP.equals(line)) {
              switch (state_setup) {
                case 0: {
                  if (state_log == 1) {
                    throw new IllegalStateException(
                        "Cannot begin setup section inside log section.");//$NON-NLS-1$
                  }
                  if (state_state == 1) {
                    throw new IllegalStateException(
                        "Cannot begin setup section inside state section.");//$NON-NLS-1$
                  }
                  state_setup = 1;
                  setupKeys = new HashSet<>();
                  continue;
                }
                case 1: {
                  throw new IllegalStateException(
                      "Setup section cannot begin inside setup section.");//$NON-NLS-1$
                }
                default: {
                  throw new IllegalStateException(
                      "Setup section can only occur once.");//$NON-NLS-1$
                }
              }
            }

            if (LogFormat.END_SETUP.equals(line)) {
              switch (state_setup) {
                case 0: {
                  throw new IllegalStateException(
                      "Setup section can only end after setup section begins.");//$NON-NLS-1$
                }
                case 1: {
                  if (state_log == 1) {
                    throw new IllegalStateException(
                        "Setup section cannot end inside log section??");//$NON-NLS-1$
                  }
                  if (state_state == 1) {
                    throw new IllegalStateException(
                        "Setup section cannot end inside state section??");//$NON-NLS-1$
                  }
                  state_setup = 2;
                  continue;
                }
                default: {
                  throw new IllegalStateException(
                      "Setup section can only end once.");//$NON-NLS-1$
                }
              }
            }

            if (LogFormat.BEGIN_STATE.equals(line)) {
              switch (state_state) {
                case 0: {
                  if (state_log == 1) {
                    throw new IllegalStateException(
                        "Cannot begin state section inside log section.");//$NON-NLS-1$
                  }
                  if (state_log != 2) {
                    throw new IllegalStateException(
                        "Can begin state section only after log section.");//$NON-NLS-1$
                  }
                  if (state_setup == 1) {
                    throw new IllegalStateException(
                        "Cannot begin state section inside setup section.");//$NON-NLS-1$
                  }
                  state_state = 1;
                  stateKeys = new HashSet<>();
                  continue;
                }
                case 1: {
                  throw new IllegalStateException(
                      "State section cannot begin inside state section.");//$NON-NLS-1$
                }
                default: {
                  throw new IllegalStateException(
                      "State section can only occur once.");//$NON-NLS-1$
                }
              }
            }

            if (LogFormat.END_STATE.equals(line)) {
              switch (state_state) {
                case 0: {
                  throw new IllegalStateException(
                      "State section can only end after state section begins.");//$NON-NLS-1$
                }
                case 1: {
                  if (state_log == 1) {
                    throw new IllegalStateException(
                        "State section cannot end inside log section??");//$NON-NLS-1$
                  }
                  if (state_log != 2) {
                    throw new IllegalStateException(
                        "State section can only end after log section??");//$NON-NLS-1$
                  }
                  if (state_setup == 1) {
                    throw new IllegalStateException(
                        "State section cannot end inside setup section??");//$NON-NLS-1$
                  }
                  state_state = 2;

                  if (invokeLogAfterState
                      && (logConsumer != null)) {
                    logConsumer.accept(fe_last_improvement,
                        fe_max, time_last_improvement, time_max,
                        improvements, f_min, false);
                  }

                  continue;
                }
                default: {
                  throw new IllegalStateException(
                      "State section can only end once.");//$NON-NLS-1$
                }
              }
            }

            if (state_setup == 1) {
              // ok, we are in the setup section

              final int colon =
                  line.indexOf(LogFormat.MAP_SEPARATOR_CHAR);
              if ((colon <= 0)
                  || (colon >= (line.length() - 1))) {
                throw new IllegalArgumentException(
                    "Invalid setup line '" //$NON-NLS-1$
                        + line + "'.");//$NON-NLS-1$
              }

              final String key = line.substring(0, colon).trim();
              final String value =
                  line.substring(colon + 1).trim();

              if (key.isEmpty() || value.isEmpty()) {
                throw new IllegalArgumentException(
                    "Invalid setup line '" //$NON-NLS-1$
                        + line
                        + "': neither key nor value must be empty.");//$NON-NLS-1$
              }

              if (!setupKeys.add(key)) {
                throw new IllegalArgumentException(
                    "Invalid setup line '" //$NON-NLS-1$
                        + line + "': key '" + //$NON-NLS-1$
                        key + "' already appeared.");//$NON-NLS-1$
              }

              final boolean isStandard =
                  LogParser.STANDARD_SETUP_KEYS.contains(key);

              if (isStandard) {
                switch (key) {
                  case LogFormat.MAX_FES: {
                    final long fes = Long.parseLong(value);
                    if (fes <= 0L) {
                      throw new IllegalArgumentException(
                          "FEs budget must be positive, but is " //$NON-NLS-1$
                              + fes);
                    }
                    budgetFEs = fes;
                    break;
                  }
                  case LogFormat.MAX_TIME: {
                    final long time = Long.parseLong(value);
                    if (time <= 0L) {
                      throw new IllegalArgumentException(
                          "Time budget must be positive, but is " //$NON-NLS-1$
                              + time);
                    }
                    budgetTime = time;
                    break;
                  }
                  case LogFormat.GOAL_F: {
                    final double f = Double.parseDouble(value);
                    if (Double.isNaN(f)
                        || (f >= Double.POSITIVE_INFINITY)) {
                      throw new IllegalArgumentException(
                          "Goal objective value must be finite or negative infinite, but is " //$NON-NLS-1$
                              + f);
                    }
                    goalF = f;
                    break;
                  }
                  case LogFormat.RANDOM_SEED: {
                    randSeedString = value;
                    if ((!value.startsWith(
                        LogFormat.RANDOM_SEED_PREFIX))
                        || (value.length() < 3)) {
                      throw new IllegalArgumentException(
                          "Random seed must start with '" //$NON-NLS-1$
                              + LogFormat.RANDOM_SEED_PREFIX
                              + "' and contain at least one hexadecimal digit, but is "//$NON-NLS-1$
                              + value);
                    }
                    randSeedLong = Long.parseUnsignedLong(
                        value.substring(2), 16);
                    break;
                  }
                  default: {
                    throw new IllegalStateException(
                        "Invalid standard setup key: "//$NON-NLS-1$
                            + key);
                  }
                }
              }

              if (setupConsumer != null) {
                setupConsumer.accept(key, value, isStandard);
              }
              // end setup section
            }

            if (state_state == 1) {
              // inside state section

              final int colon =
                  line.indexOf(LogFormat.MAP_SEPARATOR_CHAR);
              if ((colon <= 0)
                  || (colon >= (line.length() - 1))) {
                throw new IllegalArgumentException(
                    "Invalid state line '" //$NON-NLS-1$
                        + line + "'.");//$NON-NLS-1$
              }

              final String key = line.substring(0, colon).trim();
              final String value =
                  line.substring(colon + 1).trim();

              if (key.isEmpty() || value.isEmpty()) {
                throw new IllegalArgumentException(
                    "Invalid state line '" //$NON-NLS-1$
                        + line
                        + "': neither key nor value must be empty.");//$NON-NLS-1$
              }

              if (!stateKeys.add(key)) {
                throw new IllegalArgumentException(
                    "Invalid state line '" //$NON-NLS-1$
                        + line + "': key '" + //$NON-NLS-1$
                        key + "' already appeared.");//$NON-NLS-1$
              }

              switch (key) {

                case LogFormat.CONSUMED_FES: {
                  final long t = Long.parseLong(value);
                  if (t <= 0L) {
                    throw new IllegalArgumentException(
                        "Consumed FEs in state must be positive, but are "//$NON-NLS-1$
                            + t);
                  }
                  if (t < fe_max) {
                    throw new IllegalArgumentException(
                        "Consumed FEs in state must be at least as much as in log, but are "//$NON-NLS-1$
                            + t + " compared to the " + fe_max + //$NON-NLS-1$
                            " in the log.");//$NON-NLS-1$
                  }
                  if (t > fe_max) {
                    fe_max = t;
                    invokeLogAfterState = true;
                  }
                  break;
                }

                case LogFormat.LAST_IMPROVEMENT_FE: {
                  final long t = Long.parseLong(value);
                  if (t != fe_last_improvement) {
                    throw new IllegalArgumentException(
                        "Last improvement FEs in state must be same as in log, but are "//$NON-NLS-1$
                            + t + " compared to the " //$NON-NLS-1$
                            + fe_last_improvement
                            + " in the log.");//$NON-NLS-1$
                  }
                  if (t > fe_max) {
                    throw new IllegalArgumentException(
                        "Last improvement FEs in state must be less than max FEs, but are "//$NON-NLS-1$
                            + t + " compared to the " + fe_max + //$NON-NLS-1$
                            " in the log.");//$NON-NLS-1$
                  }
                  break;
                }

                case LogFormat.CONSUMED_TIME: {
                  final long t = Long.parseLong(value);
                  if (t < 0L) {
                    throw new IllegalArgumentException(
                        "Consumed time in state must be 0 or positive, but is "//$NON-NLS-1$
                            + t);
                  }
                  if (t < time_max) {
                    throw new IllegalArgumentException(
                        "Consumed time in state must be at least as much as in log, but is "//$NON-NLS-1$
                            + t + " compared to the " + time_max //$NON-NLS-1$
                            + " in the log.");//$NON-NLS-1$
                  }
                  if (t > time_max) {
                    time_max = t;
                    invokeLogAfterState = true;
                  }
                  break;
                }

                case LogFormat.LAST_IMPROVEMENT_TIME: {
                  final long t = Long.parseLong(value);
                  if (t != time_last_improvement) {
                    throw new IllegalArgumentException(
                        "Last improvement time in state must be same as in log, but is "//$NON-NLS-1$
                            + t + " compared to the " //$NON-NLS-1$
                            + time_last_improvement
                            + " in the log.");//$NON-NLS-1$
                  }
                  if (t > time_max) {
                    throw new IllegalArgumentException(
                        "Last improvement time in state must be less than max time, but is "//$NON-NLS-1$
                            + t + " compared to the " + time_max //$NON-NLS-1$
                            + " in the log.");//$NON-NLS-1$
                  }
                  break;
                }

                case LogFormat.BEST_F: {
                  final double t = Double.parseDouble(value);
                  if (t != f_min) {
                    throw new IllegalArgumentException(
                        "Best-f value in state must be same as in log, but is "//$NON-NLS-1$
                            + t + " compared to the " + f_min + //$NON-NLS-1$
                            " in the log.");//$NON-NLS-1$
                  }
                  if (!Double.isFinite(f_min)) {
                    throw new IllegalArgumentException(
                        ("Best-f value in state must be finite, but is "//$NON-NLS-1$
                            + t) + '.');
                  }
                  break;
                }

                default: {
                  throw new IllegalArgumentException(
                      "Invalid state key: " + key);//$NON-NLS-1$
                }
              }

              // end state section
            }

            continue;
          }

          // ok, no comment or tag
          if (state_log == 1) {
            final int semi_1 =
                line.indexOf(LogFormat.CSV_SEPARATOR_CHAR);
            final int semi_2 =
                line.lastIndexOf(LogFormat.CSV_SEPARATOR_CHAR);
            if ((semi_1 <= 0) || (semi_2 <= semi_1)
                || (semi_2 >= (line.length() - 1))) {
              throw new IllegalArgumentException(//
                  "Invalid log point '" + //$NON-NLS-1$
                      line + "', must contain '" + //$NON-NLS-1$
                      LogFormat.CSV_SEPARATOR_CHAR + "' twice."); //$NON-NLS-1$
            }

            try {
              final double f = Double
                  .parseDouble(line.substring(0, semi_1).trim());
              if (!(Double.isFinite(f))) {
                throw new IllegalArgumentException(
                    "Objective values must be finite, but encountered: " //$NON-NLS-1$
                        + f);
              }
              if (f > f_min) {
                throw new IllegalArgumentException(
                    "Obj        ective values must be monotonously decreasing, but encountered: " //$NON-NLS-1$
                        + f + " after " + f_min); //$NON-NLS-1$
              }

              final long fes = Long.parseLong(
                  line.substring(semi_1 + 1, semi_2).trim());
              if (fes < 1L) {
                throw new IllegalArgumentException(
                    "FEs must be positive, but encountered: " //$NON-NLS-1$
                        + fes);
              }
              if (fes < fe_max) {
                throw new IllegalArgumentException(
                    "Function evaluations must be monotonously increasing, but encountered: " //$NON-NLS-1$
                        + fes + " after " + fe_max); //$NON-NLS-1$
              }
              if ((fes == fe_max) && (f != f_min)) {
                throw new IllegalArgumentException(
                    "If function evaluations don't increase, best.f cannot decrease, but found: " //$NON-NLS-1$
                        + fes + " after " + fe_max + //$NON-NLS-1$
                        " and " + f + //$NON-NLS-1$
                        " after " + f_min);//$NON-NLS-1$
              }
              if (fes > budgetFEs) {
                throw new IllegalArgumentException(
                    "Function evaluations " + fes + //$NON-NLS-1$
                        " exceed budget of " + budgetFEs); //$NON-NLS-1$
              }

              final long time = Long
                  .parseLong(line.substring(semi_2 + 1).trim());
              if (time < 0L) {
                throw new IllegalArgumentException(
                    "Times must be 0 or positive, but encountered: " //$NON-NLS-1$
                        + time);
              }
              if (time < time_max) {
                throw new IllegalArgumentException(
                    "Times must be monotonously increasing, but encountered: " //$NON-NLS-1$
                        + time + " after " //$NON-NLS-1$
                        + time_max);
              }
              LogParser._checkTime(time, budgetTime);

              boolean invokeLog = (logConsumer != null);
              final boolean is_improvement = (f < f_min);
              if (is_improvement) {
                f_min = f;
                ++improvements;
                time_last_improvement = time;
                fe_last_improvement = fes;
              } else {
                invokeLog &=
                    ((time > time_max) || (fes > fe_max));
              }
              time_max = time;
              fe_max = fes;

              if (invokeLog) {
                logConsumer.accept(fe_last_improvement, fe_max,
                    time_last_improvement, time_max,
                    improvements, f_min, is_improvement);
              }
            } catch (final Throwable error2) {
              throw new IllegalArgumentException(//
                  "Invalid log point '" + line//$NON-NLS-1$
                      + "', parse- or validation error.", //$NON-NLS-1$
                  error2);
            }

            // end log state
            continue;
          }

        } catch (final Throwable error2) {
          throw new IOException(//
              "Line " + lineIndex //$NON-NLS-1$
                  + " is invalid: '" //$NON-NLS-1$
                  + line2 + "'.", //$NON-NLS-1$
              error2);
        }
      }

      // check states
      switch (state_log) {
        case 0: {
          throw new IllegalStateException(
              "No log section found.");//$NON-NLS-1$
        }
        case 1: {
          throw new IllegalStateException(
              "Log section has no end.");//$NON-NLS-1$
        }
        default: // nothing
      }
      switch (state_setup) {
        case 0: {
          throw new IllegalStateException(
              "No setup section found.");//$NON-NLS-1$
        }
        case 1: {
          throw new IllegalStateException(
              "Setup section has no end.");//$NON-NLS-1$
        }
        default: // nothing
      }
      switch (state_state) {
        case 0: {
          throw new IllegalStateException(
              "No state section found.");//$NON-NLS-1$
        }
        case 1: {
          throw new IllegalStateException(
              "State section has no end.");//$NON-NLS-1$
        }
        default: // nothing
      }

      if (improvements <= 0L) {
        throw new IllegalStateException(
            "No improvement was made during the run, i.e., there was no log point.");//$NON-NLS-1$
      }
      if (fe_max <= 0L) {
        throw new IllegalStateException(
            "Impossible: There were improvements, but 0 FEs??");//$NON-NLS-1$
      }
      if (fe_max < fe_last_improvement) {
        throw new IllegalStateException(
            "Impossible: last improvement FEs ("//$NON-NLS-1$
                + fe_last_improvement + ") after total FEs (" //$NON-NLS-1$
                + fe_max + ")??");//$NON-NLS-1$
      }
      if (time_max < time_last_improvement) {
        throw new IllegalStateException(
            "Impossible: last improvement time ("//$NON-NLS-1$
                + time_max + ") after total time (" //$NON-NLS-1$
                + time_last_improvement + ")??");//$NON-NLS-1$
      }
      if (!Double.isFinite(f_min)) {
        throw new IllegalStateException(
            "Impossible: There were improvements, best.f is " //$NON-NLS-1$
                + f_min);
      }
      if (setupKeys.isEmpty()) {
        throw new IllegalStateException(
            "Setup section is empty?"); //$NON-NLS-1$
      }
      if (!setupKeys
          .containsAll(LogParser.STANDARD_SETUP_KEYS)) {
        throw new IllegalStateException(
            "Setup section must have at least the keys " + //$NON-NLS-1$
                LogParser.STANDARD_SETUP_KEYS + " but has " + //$NON-NLS-1$
                setupKeys.toString() + ", i.e., lacks " + //$NON-NLS-1$
                new HashSet<>(LogParser.STANDARD_SETUP_KEYS)
                    .removeAll(setupKeys));
      }
      if ((stateKeys.size() != LogParser.STATE_KEYS.size())
          || (!stateKeys.containsAll(LogParser.STATE_KEYS))) {
        throw new IllegalStateException(
            "State section must have exactly the keys " + //$NON-NLS-1$
                LogParser.STATE_KEYS + " but has " + //$NON-NLS-1$
                stateKeys.toString());
      }

      if (fe_max > budgetFEs) {
        throw new IllegalStateException("Consumed FEs (" + fe_max //$NON-NLS-1$
            + ") exceed budget of " + budgetFEs + //$NON-NLS-1$
            " FEs."); //$NON-NLS-1$
      }
      LogParser._checkTime(time_max, budgetTime);

      if (randSeedString == null) {
        throw new IllegalStateException(
            "Random seed not defined???");//$NON-NLS-1$
      }

      if (setupConsumer != null) {
        setupConsumer.acceptStandard(randSeedString,
            randSeedLong, budgetFEs, budgetTime, goalF);
      }

    } catch (final Throwable error) {
      throw new IOException(//
          "Error while parsing log file '" + file //$NON-NLS-1$
              + "'.", //$NON-NLS-1$
          error);
    }
  }
}
