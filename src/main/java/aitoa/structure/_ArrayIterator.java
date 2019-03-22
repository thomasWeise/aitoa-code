package aitoa.structure;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * an iterator over an array
 *
 * @param <X>
 *          the array type
 */
final class _ArrayIterator<X> implements Iterator<X> {
  /** the array */
  private final X[] m_array;
  /** the index */
  private int m_index;
  /** the end */
  private final int m_end;

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
    super();
    this.m_array = array;
    this.m_index = start;
    this.m_end = end;
    if ((start < 0) || (start >= end) || (end > array.length)) {
      throw new IllegalArgumentException(
          (((("Array range is invalid: " + //$NON-NLS-1$
              start) + ':') + end) + ':') + array.length);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasNext() {
    return (this.m_index < this.m_end);
  }

  /** {@inheritDoc} */
  @Override
  public final X next() {
    final int i = this.m_index;
    if (i >= this.m_end) {
      throw new NoSuchElementException("reached end index " //$NON-NLS-1$
          + i);
    }
    final X x = this.m_array[i];
    this.m_index = (i + 1);
    return x;
  }
}
