package aitoa.examples.jssp;

import java.util.Arrays;

/**
 * A modified univariate model for the use in an EDA for the
 * JSSP. In the {@linkplain JSSPUMDAModel original UMDA model},
 * we remember how often each job id occurred at every index. We
 * then try to sample solutions which approximately represent
 * this distribution. The original model was univariate, since we
 * consider each index of the schedule representation separately.
 * <p>
 * However, now we realize that if a certain job was contained at
 * a position {@code i} in a solution, it may not be best to
 * <em>only</em> consider position {@code i} as a good location
 * for that job. If it occurred at {@code i+1} or {@code i-2},
 * that may also be OK. Thus, instead of just increasing the
 * probability of sampling it at index {@code i}, we also
 * increase the sampling probability at adjacent indices. We
 * define a base "spread" value, say {@code 16}. Then the
 * frequency at index {@code i} of the given job is increased by
 * {@code 16}. The frequencies at {@code i+1} and {@code i-1} are
 * increased by {@code 8}, those at {@code i-2} and {@code i+2}
 * by {@code 4}, and so on. If we hit the start or end of the
 * schedule, then we instead add double the values at the indices
 * at the "other" side.
 */
public final class JSSPSpreadModel extends JSSPUMDAModel {

  /** the spread base */
  public final int spreadBase;

  /**
   * create a model for the given jssp instance
   *
   * @param instance
   *          the instance
   * @param _spreadBase
   *          the spread base
   */
  public JSSPSpreadModel(final JSSPInstance instance,
      final int _spreadBase) {
    super(instance);

    if (_spreadBase < 1) {
      throw new IllegalArgumentException(
          "Spread base must be >= 1, but is " //$NON-NLS-1$
              + _spreadBase);
    }
    this.spreadBase = _spreadBase;
  }

  /**
   * create a model for the given jssp instance
   *
   * @param instance
   *          the instance
   */
  public JSSPSpreadModel(final JSSPInstance instance) {
    this(instance, 16);
  }

  /**
   * create a JSSP spread model from a string
   *
   * @param strings
   *          the strings
   */
  private JSSPSpreadModel(final String[] strings) {
    this(new JSSPInstance(strings[0]), //
        (strings.length > 1) ? Integer.parseInt(strings[1])
            : 16);
  }

  /**
   * create a JSSP spread model from a string
   *
   * @param string
   *          the strings
   */
  public JSSPSpreadModel(final String string) {
    this(string.split("_")); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public void initialize() {
    for (final long[] l : this.m_counts) {
      Arrays.fill(l, this.spreadBase);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void update(final Iterable<int[]> selected) {
    for (final int[] sel : selected) {
      for (int j = sel.length; (--j) >= 0;) {
        final int job = sel[j];
        int spread = this.spreadBase;

        this.m_counts[j][job] += spread;
        distributeToNeighbors: for (int i = 1;; i++) {
          spread >>>= 1;
          if (spread <= 0) {
            break distributeToNeighbors;
          }
          if (j >= i) {
            this.m_counts[j - i][job] +=
                ((j + i) < sel.length) ? spread : (spread << 1);
          }
          if ((j + i) < sel.length) {
            this.m_counts[j + i][job] +=
                (j >= i) ? spread : (spread << 1);
          }
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "spread_" + this.spreadBase; //$NON-NLS-1$
  }
}
