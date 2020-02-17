package aitoa.utils.logs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import aitoa.utils.Experiment;
import aitoa.utils.RandomUtils;

/** A unique setup record */
public class Setup implements Comparable<Setup> {

  /** the algorithm id */
  public final String algorithm;
  /** the instance id */
  public final String instance;
  /** the seed */
  public final long seed;

  /**
   * create
   *
   * @param _algorithm
   *          the algorithm id
   * @param _instance
   *          the instance id
   * @param _seed
   *          the seed
   */
  public Setup(final String _algorithm, final String _instance,
      final String _seed) {
    this(_algorithm, _instance,
        RandomUtils.stringToRandSeed(_seed));
  }

  /**
   * create
   *
   * @param _algorithm
   *          the algorithm id
   * @param _instance
   *          the instance id
   * @param _seed
   *          the seed
   */
  public Setup(final String _algorithm, final String _instance,
      final long _seed) {
    this.algorithm = _algorithm.trim();
    if (this.algorithm.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot have '" + _algorithm + //$NON-NLS-1$
              "' as algorithm name.");//$NON-NLS-1$
    }

    this.instance = _instance.trim();
    if (this.instance.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot have '" + _instance + //$NON-NLS-1$
              "' as instance name.");//$NON-NLS-1$
    }

    this.seed = _seed;
  }

  /**
   * create
   *
   * @param strings
   *          the strings
   */
  private Setup(final String[] strings) {
    this(strings[0], strings[1], strings[2]);
  }

  /**
   * Create the setup from a single string, as returned by
   * {@link #toString()}
   *
   * @param s
   *          the string
   */
  public Setup(final String s) {
    this(Setup.__split(s));
  }

  /**
   * Split a string for parsing
   *
   * @param s
   *          the string
   * @return the split string
   */
  private static final String[] __split(final String s) {
    final String u = s.trim();
    final int i = u.indexOf('/');
    final int j = u.lastIndexOf('/');

    if ((i <= 0) || (i >= j) || (j >= u.length())
        || (u.indexOf('/', i + 1) != j)) {
      throw new IllegalArgumentException(
          "Invalid setup string: '" + s //$NON-NLS-1$
              + "'."); //$NON-NLS-1$
    }

    return new String[] { //
        u.substring(0, i), //
        u.substring(i + 1, j), //
        u.substring(j + 1)//
    };
  }

  /**
   * create
   *
   * @param other
   *          the setup to copy
   */
  protected Setup(final Setup other) {
    super();
    this.algorithm = other.algorithm;
    this.instance = other.instance;
    this.seed = other.seed;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int hc = this.algorithm.hashCode();
    hc = (31 * hc) + this.instance.hashCode();
    hc = (31 * hc) + Long.hashCode(this.seed);
    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Setup) {
      final Setup e = ((Setup) o);
      return (Objects.equals(this.algorithm, e.algorithm) && //
          Objects.equals(this.instance, e.instance) && //
          (this.seed == e.seed));
    }
    return false;
  }

  /**
   * Resolve this setup to get a log file path
   *
   * @param root
   *          the root folder
   * @return the path to where the log file would be located
   */
  public final Path logFile(final Path root) {
    try {
      return Experiment.logFile(root, this.algorithm,
          this.instance, this.seed, true);
    } catch (final IOException ioe) {
      throw new IllegalArgumentException(
          "Error when converting setup '" //$NON-NLS-1$
              + this.toString() + "' to path.", //$NON-NLS-1$
          ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(final Setup o) {
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
    return Long.compareUnsigned(this.seed, o.seed);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.algorithm + '/' + this.instance + '/'
        + RandomUtils.randSeedToString(this.seed);
  }
}