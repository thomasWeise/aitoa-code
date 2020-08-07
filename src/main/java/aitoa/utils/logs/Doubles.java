package aitoa.utils.logs;

import java.util.Arrays;

import aitoa.utils.math.Statistics;

/** a store of doubles */
final class Doubles extends Statistic {

  /** the internal data */
  private double[] m_data;

  /** the size */
  private int m_size;

  /** create */
  Doubles() {
    this.m_data = new double[10];
  }

  /** {@inheritDoc} */
  @Override
  void add(final double value) {
    final int size = this.m_size;
    double[] data = this.m_data;

    if (!Double.isFinite(value)) {
      throw new IllegalArgumentException(
          "all double values must be finite, but encountered " //$NON-NLS-1$
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
  Statistic doFinalize() {
    if (this.m_size <= 0) {
      throw new IllegalStateException("empty data array?"); //$NON-NLS-1$
    }
    this.m_data = Arrays.copyOf(this.m_data, this.m_size);
    Arrays.sort(this.m_data);

    // try to convert the data to longs
    final long[] alt =
        Statistics.tryConvertDoublesToLongs(this.m_data);
    if (alt == null) {
      return this;
    }
    return new Longs(alt);
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
