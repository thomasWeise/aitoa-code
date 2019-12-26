package aitoa.utils.logs;

import java.util.Arrays;

import aitoa.utils.math.Statistics;

/** a store of doubles */
final class _Doubles extends _Statistic {

  /** the internal data */
  private double[] m_data;

  /** the size */
  private int m_size;

  /** create */
  _Doubles() {
    this.m_data = new double[10];
  }

  /** {@inheritDoc} */
  @Override
  final void _add(final double value) {
    final int size = this.m_size;
    double[] data = this.m_data;

    if (!Double.isFinite(value)) {
      throw new IllegalArgumentException(
          "all double values must be finite, but encountered " //$NON-NLS-1$
              + value);
    }

    if (size >= data.length) {
      this.m_data =
          data = Arrays.copyOf(data, _Statistic._incSize(size));
    }

    data[size] = value;
    this.m_size = (size + 1);
  }

  /** {@inheritDoc} */
  @Override
  final _Statistic _finalize() {
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
    return new _Longs(alt);
  }

  /** {@inheritDoc} */
  @Override
  final int size() {
    return this.m_size;
  }

  /** {@inheritDoc} */
  @Override
  final Number _quantile(final double p) {
    return Statistics.quantile(p, this.m_data);
  }

  /** {@inheritDoc} */
  @Override
  final Number[] _meanAndStdDev() {
    return Statistics
        .sampleMeanAndStandardDeviation(this.m_data);
  }

  /** {@inheritDoc} */
  @Override
  final Number _divideSumBy(final int by) {
    return Statistics.divideExact(Statistics.sum(this.m_data),
        by);
  }
}
