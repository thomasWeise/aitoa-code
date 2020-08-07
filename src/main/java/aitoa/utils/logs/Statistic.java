package aitoa.utils.logs;

/** the base class for internal statistics */
abstract class Statistic {

  /**
   * get the next size for the buffer
   *
   * @param orig
   *          the original size
   * @return the next size
   */
  static final int incSize(final int orig) {
    return Math.max(orig + 1, orig << 1);
  }

  /**
   * Finalize the data collection. If the collection contains
   * double data that can all be converted to long data without
   * loss of information, this will be done.
   *
   * @return the statistic object to use
   */
  abstract Statistic doFinalize();

  /**
   * the number of stored elements
   *
   * @return the size of the collection
   */
  abstract int size();

  /**
   * compute a quantile
   *
   * @param p
   *          the quantile value
   * @return the quantile number
   */
  abstract Number quantile(final double p);

  /**
   * compute the mean and standard deviation of the data
   *
   * @return an array with the mean and standard deviation
   */
  abstract Number[] meanAndStdDev();

  /**
   * divide the sum of the data by an integer value
   *
   * @param by
   *          the divisor
   * @return the result
   */
  abstract Number divideSumBy(final int by);

  /**
   * add a value to the list
   *
   * @param value
   *          the value
   */
  @SuppressWarnings("static-method")
  void add(final long value) {
    throw new UnsupportedOperationException();
  }

  /**
   * add a value to the list
   *
   * @param value
   *          the value
   */
  @SuppressWarnings("static-method")
  void add(final double value) {
    throw new UnsupportedOperationException();
  }
}
