package aitoa.utils.logs;

import java.util.Arrays;

import aitoa.utils.math.Statistics;

/** a store of doubles */
final class Doubles extends Statistic {

  /** the internal data */
  private double[] mData;

  /** the size */
  private int mSize;

  /** create */
  Doubles() {
    this.mData = new double[10];
  }

  /** {@inheritDoc} */
  @Override
  void add(final double value) {
    final int size = this.mSize;
    double[] data = this.mData;

    if (!Double.isFinite(value)) {
      throw new IllegalArgumentException(
          "all double values must be finite, but encountered " //$NON-NLS-1$
              + value);
    }

    if (size >= data.length) {
      this.mData =
          data = Arrays.copyOf(data, Statistic.incSize(size));
    }

    data[size] = value;
    this.mSize = (size + 1);
  }

  /** {@inheritDoc} */
  @Override
  Statistic doFinalize() {
    if (this.mSize <= 0) {
      throw new IllegalStateException("empty data array?"); //$NON-NLS-1$
    }
    this.mData = Arrays.copyOf(this.mData, this.mSize);
    Arrays.sort(this.mData);

    // try to convert the data to longs
    final long[] alt =
        Statistics.tryConvertDoublesToLongs(this.mData);
    if (alt == null) {
      return this;
    }
    return new Longs(alt);
  }

  /** {@inheritDoc} */
  @Override
  int size() {
    return this.mSize;
  }

  /** {@inheritDoc} */
  @Override
  Number quantile(final double p) {
    return Statistics.quantile(p, this.mData);
  }

  /** {@inheritDoc} */
  @Override
  Number[] meanAndStdDev() {
    return Statistics.sampleMeanAndStandardDeviation(this.mData);
  }

  /** {@inheritDoc} */
  @Override
  Number divideSumBy(final int by) {
    return Statistics.divideExact(Statistics.sum(this.mData),
        by);
  }
}
