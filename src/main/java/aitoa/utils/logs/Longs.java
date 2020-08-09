package aitoa.utils.logs;

import java.util.Arrays;

import aitoa.utils.math.Statistics;

/** a store of longs */
final class Longs extends Statistic {

  /** the internal data */
  private long[] mData;

  /** the size */
  private int mSize;

  /** create */
  Longs() {
    this.mData = new long[10];
  }

  /**
   * create
   *
   * @param pData
   *          the data
   */
  Longs(final long[] pData) {
    this.mData = pData;
    this.mSize = pData.length;
  }

  /** {@inheritDoc} */
  @Override
  void add(final long value) {
    final int size = this.mSize;
    long[] data = this.mData;

    if (value < 0L) {
      throw new IllegalArgumentException(
          "all long values must be >= 0, but encountered " //$NON-NLS-1$
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
  Longs doFinalize() {
    if (this.mSize <= 0) {
      throw new IllegalStateException("empty data array?"); //$NON-NLS-1$
    }
    this.mData = Arrays.copyOf(this.mData, this.mSize);
    Arrays.sort(this.mData);
    return this;
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
