package aitoa.structure;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * an internal base class for iterators
 *
 * @param <X>
 *          the array type
 * @param <Y>
 *          the element type
 */
abstract class _Iterator<X, Y> implements Iterator<Y> {
  /** the array */
  final X[] m_array;
  /** the index */
  int m_index;
  /** the end */
  final int m_end;

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
  _Iterator(final X[] array, final int start, final int end) {
    super();
    this.m_array = array;
    this.m_index = start;
    this.m_end = end;
  }

  /**
   * check the range
   *
   * @param array
   *          the array
   * @param start
   *          the start index
   * @param end
   *          the end index
   * @throws IllegalArgumentException
   *           if the range is empty or exceeds the array length
   */
  static final void _checkRange(final Object[] array,
      final int start, final int end) {
    if ((start < 0) || (start >= end) || (end > array.length)) {
      throw new IllegalArgumentException(
          (((("Array range is invalid: " + //$NON-NLS-1$
              start) + ':') + end) + ':') + array.length);
    }
  }

  /**
   * throw an error because we have reached the end
   *
   * @param i
   *          the end index
   */
  static final void _endError(final int i) {
    throw new NoSuchElementException("reached end index " //$NON-NLS-1$
        + i);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasNext() {
    return (this.m_index < this.m_end);
  }
}
