package aitoa.utils.logs;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import aitoa.structure.LogFormat;

/** The setup information from a log file */
public final class SetupData implements Comparable<SetupData> {
  /** the random seed string */
  public final String randSeedString;
  /** the random seed long */
  public final long randSeedLong; //
  /** the number of FEs marking the budget */
  public final long budgetFEs;
  /** the budget time */
  public final long budgetTime;
  /** the goal objective value */
  public final double goalF;
  /** all the setup key/values together */
  public final Map<String, String> setup;

  /**
   * this method accepts a setup point from the log file
   *
   * @param _randSeedString
   *          the random seed string
   * @param _randSeedLong
   *          the random seed long
   * @param _budgetFEs
   *          the number of FEs marking the budget
   * @param _budgetTime
   *          the budget time
   * @param _goalF
   *          the goal objective value
   * @param _setup
   *          all the setup key/values together
   */
  SetupData(//
      final String _randSeedString, //
      final long _randSeedLong, //
      final long _budgetFEs, //
      final long _budgetTime, //
      final double _goalF, final Map<String, String> _setup) {
    super();

    this.randSeedString = _randSeedString.trim();
    this.randSeedLong = _randSeedLong;

    try {
      final long l = Long.parseUnsignedLong(this.randSeedString
          .substring(LogFormat.RANDOM_SEED_PREFIX.length()), 16);
      if (l != this.randSeedLong) {
        throw new IllegalStateException("Rand seed string (" + //$NON-NLS-1$
            this.randSeedString + '=' + l
            + ") does not match provided value " //$NON-NLS-1$
            + this.randSeedLong);
      }
    } catch (final Throwable error) {
      throw new IllegalArgumentException(
          "Invalid rand seed string: " //$NON-NLS-1$
              + this.randSeedString,
          error);
    }

    this.budgetFEs = _budgetFEs;
    if (this.budgetFEs <= 0) {
      throw new IllegalArgumentException(
          "Invalid FEs budget: " + this.budgetFEs);//$NON-NLS-1$
    }

    this.budgetTime = _budgetTime;
    if (this.budgetTime <= 0) {
      throw new IllegalArgumentException(
          "Invalid time budget: " + this.budgetTime);//$NON-NLS-1$
    }

    this.goalF = _goalF;
    if ((!Double.isFinite(this.goalF))
        && (!(this.goalF >= Double.NEGATIVE_INFINITY))) {
      throw new IllegalArgumentException(
          "Invalid goal F: " + this.goalF);//$NON-NLS-1$
    }

    this.setup = Collections.unmodifiableMap(
        new TreeMap<>(Objects.requireNonNull(_setup)));
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    int hc = Long.hashCode(this.randSeedLong);
    hc = (31 * hc) + Long.hashCode(this.budgetFEs);
    hc = (31 * hc) + Long.hashCode(this.budgetTime);
    hc = (31 * hc) + Double.hashCode(this.goalF);
    hc = (31 * hc) + this.setup.hashCode();
    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof SetupData) {
      final SetupData s = ((SetupData) o);

      return ((s.budgetFEs == this.budgetFEs)//
          && (s.budgetTime == this.budgetTime)//
          && (Double.compare(this.goalF, s.goalF) == 0)//
          && (this.randSeedLong == s.randSeedLong)//
          && Objects.equals(this.setup, s.setup));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final int compareTo(final SetupData s) {
    if (s == null) {
      return -1;
    }
    int res = Long.compare(this.budgetFEs, s.budgetFEs);
    if (res != 0) {
      return res;
    }
    res = Long.compare(this.budgetTime, s.budgetTime);
    if (res != 0) {
      return res;
    }
    res = Double.compare(this.goalF, s.goalF);
    if (res != 0) {
      return res;
    }
    res = Long.compare(this.randSeedLong, s.randSeedLong);
    if (res != 0) {
      return res;
    }

    final Iterator<Map.Entry<String, String>> a =
        this.setup.entrySet().iterator();
    final Iterator<Map.Entry<String, String>> b =
        s.setup.entrySet().iterator();
    for (;;) {
      if (a.hasNext()) {
        if (!b.hasNext()) {
          return 1;
        }
        final Map.Entry<String, String> ea = a.next();
        final Map.Entry<String, String> eb = b.next();

        res = ea.getKey().compareTo(eb.getKey());
        if (res != 0) {
          return res;
        }

        res = ea.getValue().compareTo(eb.getValue());
        if (res != 0) {
          return res;
        }
      } else {
        if (b.hasNext()) {
          return -1;
        }
        return 0;
      }
    }
  }
}
