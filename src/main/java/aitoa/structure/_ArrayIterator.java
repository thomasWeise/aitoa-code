package aitoa.structure;

/**
 * an iterator over an array
 *
 * @param <X>
 *          the array type
 */
final class _ArrayIterator<X> extends _Iterator<X, X> {

  /**
   * create
   *
   * @param array
   *          the array
   * @param start
   *          the start index
   * @param end
   *          the end index
   */
  _ArrayIterator(final X[] array, final int start,
      final int end) {
    super(array, start, end);
  }

  /** {@inheritDoc} */
  @Override
  public final X next() {
    final int i = this.m_index;
    if (i >= this.m_end) {
      _Iterator._endError(i);
    }
    final X x = this.m_array[i];
    this.m_index = (i + 1);
    return x;
  }
}
