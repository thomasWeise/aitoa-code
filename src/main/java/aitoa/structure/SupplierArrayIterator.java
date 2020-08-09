package aitoa.structure;

import java.util.function.Supplier;

/**
 * an iterator over an array of suppliers, used by
 * {@link aitoa.structure.IModel}
 *
 * @param <X>
 *          the array type
 */
final class SupplierArrayIterator<X>
    extends IteratorBase<Supplier<X>, X> {

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
  SupplierArrayIterator(final Supplier<X>[] array,
      final int start, final int end) {
    super(array, start, end);
  }

  /** {@inheritDoc} */
  @Override
  public X next() {
    final int i = this.m_index;
    if (i >= this.m_end) {
      IteratorBase.endError(i);
    }
    final X x = this.m_array[i].get();
    this.m_index = (i + 1);
    return x;
  }
}