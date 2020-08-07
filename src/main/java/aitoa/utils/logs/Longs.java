package aitoa.utils.logs;

import java.util.Arrays;

import aitoa.utils.math.Statistics;

/** a store of longs */
final class Longs extends Statistic {

  /** the internal data */
  private long[] m_data;

  /** the size */
  private int m_size;

  /** create */
  Longs() {
    this.m_data = new long[10];
  }

  /**
   * create
   *
   * @param data
   *          the data
   */
  Longs(final long[] data) {
    this.m_data = data;
    this.m_size = data.length;
  }

  /** {@inheritDoc} */
  @Override
  void add(final long value) {
    final int size = this.m_size;
    long[] data = this.m_data;

    if (value < 0L) {
      throw new IllegalArgumentException(
          "all long values must be >= 0, but encountered " //$NON-NLS-1$
              + value);
    }

    if (size >= data.length) {
      this.m_data =
          data = Arrays.copyOf(data, Statistic.incSize(size));
    }

    data[size] = value;
    this.m_size = (size + 1);
  }

  /** {@inheritDoc} */
  @Override
  Longs doFinalize() {
    if (this.m_size <= 0) {
      throw new IllegalStateException("empty data array?"); //$NON-NLS-1$
    }
    this.m_data = Arrays.copyOf(this.m_data, this.m_size);
    Arrays.sort(this.m_data);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  int size() {
    return this.m_size;
  }

  /** {@inheritDoc} */
  @Override
  Number quantile(final double p) {
    return Statistics.quantile(p, this.m_data);
  }

  /** {@inheritDoc} */
  @Override
  Number[] meanAndStdDev() {
    return Statistics
        .sampleMeanAndStandardDeviation(this.m_data);
  }

  /** {@inheritDoc} */
  @Override
  Number divideSumBy(final int by) {
    return Statistics.divideExact(Statistics.sum(this.m_data),
        by);
  }
}
