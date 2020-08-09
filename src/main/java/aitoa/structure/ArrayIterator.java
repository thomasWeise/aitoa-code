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
   * @param pArray
   *          the array
   * @param pStart
   *          the start index
   * @param pEnd
   *          the end index
   */
  ArrayIterator(final X[] pArray, final int pStart,
      final int pEnd) {
    super(pArray, pStart, pEnd);
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
