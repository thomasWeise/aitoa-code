package aitoa.structure;

/**
 * an iterator over an array
 *
 * @param <X>
 *          the array type
 */
final class ArrayIterator<X> extends IteratorBase<X, X> {

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
  ArrayIterator(final X[] array, final int start,
      final int end) {
    super(array, start, end);
  }

  /** {@inheritDoc} */
  @Override
  public X next() {
    final int i = this.m_index;
    if (i >= this.m_end) {
      IteratorBase.endError(i);
    }
    final X x = this.m_array[i];
    this.m_index = (i + 1);
    return x;
  }
}
